package com.example.plant_id.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_id.data.database.PlantDatabase
import com.example.plant_id.data.entity.Plant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 创建 / 编辑档案 ViewModel
 * - 创建模式：editingPlantId = null，填写后调用 createPlant()
 * - 编辑模式：先调用 loadPlant(id)，再调用 updatePlant()
 */
class CreatePlantViewModel(application: Application) : AndroidViewModel(application) {

    private val plantDao = PlantDatabase.getInstance(application).plantDao()

    // ── 表单字段（Compose mutable state） ──────────────────────────

    // 档案图标名称（对应 assets/svg 目录下 svg 文件名，不含扩展名）
    var iconName by mutableStateOf("monstera")

    /** 植物名称（必填） */
    var name by mutableStateOf("")

    /** 品种（必填） */
    var species by mutableStateOf("")

    /** 入手日期（Unix 毫秒时间戳） */
    var acquiredDate by mutableLongStateOf(System.currentTimeMillis())

    /** 浇水间隔天数（必填，默认 7 天） */
    var wateringIntervalDays by mutableIntStateOf(7)

    /** 养护备注（可选） */
    var notes by mutableStateOf("")

    /** 绑定的 NFC 标签 ID（NFC 扫描自动填入，或手动输入；未绑定时为空字符串） */
    var nfcTagId by mutableStateOf("")

    /**
     * 植物当前状态
     * - "alive"    存活中（默认）
     * - "dormant"  休眠中
     * - "sick"     病恹恹
     * - "archived" 已终结
     */
    var status by mutableStateOf("alive")

    // ── 编辑模式 ────────────────────────────────────────────────────

    /** 正在编辑的植物 ID（创建模式时为 null） */
    var editingPlantId: Long? = null
        private set

    /** 是否正在从数据库加载（编辑模式启动时为 true） */
    var isLoading by mutableStateOf(false)
        private set

    // ── 操作函数 ────────────────────────────────────────────────────

    /**
     * 加载已有档案数据（编辑模式启动时调用）
     * 通过 LaunchedEffect(plantId) 调用以避免重复加载
     */
    fun loadPlant(id: Long) {
        if (editingPlantId == id) return
        editingPlantId = id
        isLoading = true
        viewModelScope.launch {
            val plant = plantDao.getPlantById(id).first()
            plant?.let {
                iconName = it.iconName
                name = it.name
                species = it.species
                acquiredDate = it.acquiredDate
                wateringIntervalDays = it.wateringIntervalDays
                notes = it.notes
                status = it.status
                nfcTagId = it.nfcTagId
            }
            isLoading = false
        }
    }

    /** 预填 NFC 标签 ID（NFC 触发创建时由 Navigation 层调用） */
    fun prefillNfcTag(tagId: String) {
        if (nfcTagId.isBlank()) nfcTagId = tagId
    }

    /** 创建新植物档案，成功后回调 onSuccess */
    fun createPlant(onSuccess: () -> Unit) {
        viewModelScope.launch {
            plantDao.insert(
                Plant(
                    iconName = iconName,
                    name = name.trim(),
                    species = species.trim(),
                    acquiredDate = acquiredDate,
                    wateringIntervalDays = wateringIntervalDays,
                    notes = notes.trim(),
                    nfcTagId = nfcTagId.trim()
                )
            )
            onSuccess()
        }
    }

    /** 保存编辑后的档案，成功后回调 onSuccess */
    fun updatePlant(onSuccess: () -> Unit) {
        val id = editingPlantId ?: return
        viewModelScope.launch {
            val existing = plantDao.getPlantById(id).first() ?: return@launch
            plantDao.update(
                existing.copy(
                    iconName = iconName,
                    name = name.trim(),
                    species = species.trim(),
                    acquiredDate = acquiredDate,
                    wateringIntervalDays = wateringIntervalDays,
                    notes = notes.trim(),
                    status = status,
                    nfcTagId = nfcTagId.trim()
                )
            )
            onSuccess()
        }
    }

    /** 终结并归档植物（status → "archived"），成功后回调 onSuccess */
    fun archivePlant(onSuccess: () -> Unit) {
        val id = editingPlantId ?: return
        viewModelScope.launch {
            val existing = plantDao.getPlantById(id).first() ?: return@launch
            plantDao.update(existing.copy(status = "archived"))
            onSuccess()
        }
    }
}
