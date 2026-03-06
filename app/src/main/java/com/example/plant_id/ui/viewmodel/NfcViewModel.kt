package com.example.plant_id.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_id.data.database.PlantDatabase
import kotlinx.coroutines.launch

// NFC 触发后的导航目标（sealed class 确保只有这三种状态）
sealed class NfcNavEvent {
    // 标签已绑定某株植物 → 跳转到其详情页
    data class GoToDetail(val plantId: Long) : NfcNavEvent()

    // 标签尚未绑定任何植物 → 跳转到创建页，并预填标签 ID
    data class GoToCreate(val nfcTagId: String) : NfcNavEvent()

    // 标签曾经绑定过植物，但档案已被删除 → 提示用户
    data class TagOrphaned(val nfcTagId: String) : NfcNavEvent()

    // 无待处理导航事件（初始状态 / 已消费后重置到此状态）
    object None : NfcNavEvent()
}

// NFC 功能 ViewModel
// Activity 侧调用 processTag() 触发 DB 查询
// Compose 侧监听 navEvent 并执行实际路由跳转，完成后调用 consumeNavEvent()
class NfcViewModel(application: Application) : AndroidViewModel(application) {

    private val plantDao = PlantDatabase.getInstance(application).plantDao()

    // 已绑定标签的 ID 缓存（线程安全：@Volatile + 不可变 Set 赋值替换）
    // NFC 回调线程读取 isTagBound()，Main 线程写入 markTagBound()
    @Volatile
    private var _boundTagIds: Set<String> = emptySet()

    init {
        // 启动时从 DB 预热缓存，确保已有绑定标签在首次扫描时即可触发 writeAar
        viewModelScope.launch {
            val ids = plantDao.getAllNfcTagIds()
            _boundTagIds = ids.toHashSet()
        }
    }

    // 判断标签是否已绑定（NFC 回调线程 / Main 线程均可安全调用）
    fun isTagBound(tagId: String): Boolean = tagId in _boundTagIds

    // 最近一次识别到的标签 ID
    var lastTagId by mutableStateOf<String?>(null)
        private set

    // 待处理的导航事件，Compose 侧通过 LaunchedEffect 监听
    var navEvent by mutableStateOf<NfcNavEvent>(NfcNavEvent.None)
        private set

    // 是否允许将未绑定标签导航到创建页（仅 NfcScanScreen 激活时为 true）
    var allowGoToCreate by mutableStateOf(false)
        private set

    // NFC 成功导入弹窗显示状态
    var showNfcSuccessDialog by mutableStateOf(false)
        private set

    // NfcScanScreen 进入时调用 setCreateMode(true)，离开时调用 setCreateMode(false)
    fun setCreateMode(enabled: Boolean) {
        allowGoToCreate = enabled
    }

    // 显示 NFC 成功弹窗
    fun showSuccessDialog() {
        showNfcSuccessDialog = true
    }

    // 隐藏 NFC 成功弹窗
    fun hideSuccessDialog() {
        showNfcSuccessDialog = false
    }

    /**
     * Activity 侧：收到 NFC 标签 ID 后调用（Main 线程）
     * - 已绑定：更新缓存，发出 GoToDetail 导航事件
     * - 未绑定 + 扫描引导页：发出 GoToCreate 导航事件（writeAar 由 MainActivity 在确认绑定后调用）
     * - 标签曾绑定但档案已删除（孤立标签）：发出 TagOrphaned 事件，提示用户
     * - 未绑定 + 其他页面：静默忽略
     *
     * 注意：writeAar 不在此处调用，由 MainActivity 的 NFC 回调同步执行，
     * 避免异步 IO 操作干扰 NFC 连接状态导致后续扫描失败。
     */
    fun processTag(tagId: String) {
        lastTagId = tagId
        viewModelScope.launch {
            val plant = plantDao.getPlantByNfcTag(tagId)
            when {
                plant != null -> {
                    // 已绑定：更新缓存（下次扫描时 MainActivity 将触发 writeAar）
                    markTagBound(tagId)
                    navEvent = NfcNavEvent.GoToDetail(plant.id)
                }
                // 标签在内存缓存中存在（曾绑定过），但 DB 查询不到 → 档案已被删除
                tagId in _boundTagIds -> {
                    _boundTagIds = _boundTagIds - tagId  // 从缓存移除孤立标签
                    navEvent = NfcNavEvent.TagOrphaned(tagId)
                }

                allowGoToCreate -> navEvent = NfcNavEvent.GoToCreate(tagId)
                else -> navEvent = NfcNavEvent.None
            }
        }
    }

    // 将 tagId 加入绑定缓存（创建新绑定或首次识别时调用）
    private fun markTagBound(tagId: String) {
        if (tagId !in _boundTagIds) {
            _boundTagIds = _boundTagIds + tagId
        }
    }

    // Compose 侧：导航已执行，清除事件防止重复触发
    fun consumeNavEvent() {
        navEvent = NfcNavEvent.None
    }

    // 通知深链接入口：点击浇水提醒通知打开 App 时直接跳转目标植物详情页
    fun navigateToPlant(plantId: Long) {
        navEvent = NfcNavEvent.GoToDetail(plantId)
    }
}
