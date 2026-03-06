package com.example.plant_id.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_id.data.database.PlantDatabase
import com.example.plant_id.data.entity.Photo
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.data.entity.WateringLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

// 植物档案详情页 ViewModel
// 负责加载植物数据、浇水记录、照片列表，以及浇水、拍照和归档操作
class PlantDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val plantDao = PlantDatabase.getInstance(application).plantDao()
    private val wateringLogDao = PlantDatabase.getInstance(application).wateringLogDao()
    private val photoDao = PlantDatabase.getInstance(application).photoDao()

    private var _plantId: Long = -1L

    // 当前植物档案数据（Flow 实时更新）
    var plant by mutableStateOf<Plant?>(null)
        private set

    // 浇水记录列表（时间倒序）
    var wateringLogs by mutableStateOf<List<WateringLog>>(emptyList())
        private set

    // 照片列表（拍摄时间倒序）
    var photos by mutableStateOf<List<Photo>>(emptyList())
        private set

    // -1 表示从未浇水；>= 0 表示距上次浇水的天数
    var lastWateredDaysAgo by mutableIntStateOf(-1)
        private set

    // 已养天数（从入手日期至今）
    var daysKept by mutableIntStateOf(0)
        private set

    // 是否正在写入浇水记录（防止重复提交）
    var isAddingWatering by mutableStateOf(false)
        private set

    // 相机拍照待保存的文件路径（由 prepareCameraUri 设置，saveXxx 消费后清除）
    var pendingPhotoFilePath by mutableStateOf<String?>(null)
        private set

    // 是否显示浇水成功弹窗
    var showWateringSuccess by mutableStateOf(false)
        private set

    fun loadPlant(id: Long) {
        if (_plantId == id) return
        _plantId = id

        // 订阅植物数据变化（编辑保存后自动刷新）
        viewModelScope.launch {
            plantDao.getPlantById(id).collect { p ->
                plant = p
                p?.let {
                    daysKept = ((System.currentTimeMillis() - it.acquiredDate) / 86_400_000L)
                        .toInt().coerceAtLeast(0)
                }
            }
        }

        // 订阅浇水记录变化
        viewModelScope.launch {
            wateringLogDao.getLogsByPlant(id).collect { logs ->
                wateringLogs = logs
                val last = logs.firstOrNull()
                lastWateredDaysAgo = if (last != null) {
                    ((System.currentTimeMillis() - last.wateredAt) / 86_400_000L)
                        .toInt().coerceAtLeast(0)
                } else {
                    -1
                }
            }
        }

        // 订阅照片列表变化
        viewModelScope.launch {
            photoDao.getPhotosByPlant(id).collect { list ->
                photos = list
            }
        }
    }

    // 准备相机拍照的目标文件，返回 FileProvider URI 供相机 App 写入
    fun prepareCameraUri(context: Context): Uri? {
        val id = _plantId
        if (id < 0) return null
        return try {
            val photoDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "plants"
            )
            photoDir.mkdirs()
            val file = File(photoDir, "plant_${id}_${System.currentTimeMillis()}.jpg")
            pendingPhotoFilePath = file.absolutePath
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    // 浇水 + 拍照：同时插入浇水记录（含 photoPath）和照片记录，成功后显示成功弹窗
    fun saveWateringWithPhoto() {
        val path = pendingPhotoFilePath ?: return addWatering()
        val id = _plantId
        if (id < 0 || isAddingWatering) return
        pendingPhotoFilePath = null
        isAddingWatering = true
        viewModelScope.launch {
            photoDao.insert(Photo(plantId = id, filePath = path))
            wateringLogDao.insert(WateringLog(plantId = id, photoPath = path))
            isAddingWatering = false
            showWateringSuccess = true
        }
    }

    // 手动拍照：仅插入照片记录（不记录浇水）
    fun savePhotoOnly() {
        val path = pendingPhotoFilePath ?: return
        val id = _plantId
        if (id < 0) return
        pendingPhotoFilePath = null
        viewModelScope.launch {
            photoDao.insert(Photo(plantId = id, filePath = path))
        }
    }

    // 添加一条今日浇水记录（无照片），成功后显示成功弹窗
    fun addWatering(onSuccess: () -> Unit = {}) {
        val id = _plantId
        if (id < 0 || isAddingWatering) return
        isAddingWatering = true
        viewModelScope.launch {
            wateringLogDao.insert(WateringLog(plantId = id))
            isAddingWatering = false
            showWateringSuccess = true
            onSuccess()
        }
    }

    // 关闭浇水成功弹窗
    fun dismissWateringSuccess() {
        showWateringSuccess = false
    }

    //将植物标记为已终结归档
    fun archivePlant(onSuccess: () -> Unit = {}) {
        val current = plant ?: return
        viewModelScope.launch {
            plantDao.update(
                current.copy(
                    status = "archived",
                    archivedAt = System.currentTimeMillis()
                )
            )
            onSuccess()
        }
    }

    // 彻底删除植物档案（不可恢复）
    fun deletePlant(onSuccess: () -> Unit = {}) {
        val current = plant ?: return
        viewModelScope.launch {
            // 先删除该植物的所有浇水记录
            val logs = wateringLogDao.getLogsByPlant(current.id).first()
            logs.forEach { wateringLogDao.delete(it) }
            // 再删除该植物的所有照片记录及磁盘文件
            val photos = photoDao.getPhotosByPlant(current.id).first()
            photos.forEach { photo ->
                photoDao.delete(photo)
                // 同时删除磁盘上的照片文件
                File(photo.filePath).takeIf { it.exists() }?.delete()
            }
            // 最后删除植物本身
            plantDao.delete(current)
            onSuccess()
        }
    }
}
