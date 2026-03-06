package com.example.plant_id.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_id.data.database.PlantDatabase
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.data.entity.WateringLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 单株植物的养护状态数据，用于养护提醒页列表展示
data class PlantCareItem(
    val plant: Plant,
    // -1 表示从未浇水；>= 0 表示距上次浇水的天数
    val daysSinceWatering: Int,
    // 负数 = 已超期；0 = 今日到期；正数 = 还差多少天才到期
    val daysUntilDue: Int
) {
    // 已超期（或从未浇水）
    val isOverdue: Boolean get() = daysUntilDue < 0

    // 今日到期
    val isDueToday: Boolean get() = daysUntilDue == 0

    // 1-2 天内即将到期
    val isUpcoming: Boolean get() = daysUntilDue in 1..2

    // 需要用户关注（超期 or 今日）
    val needsAttention: Boolean get() = isOverdue || isDueToday
}

// 养护提醒页 ViewModel
// 从数据库实时读取所有存活植物，计算每株的浇水到期状态
class CareViewModel(application: Application) : AndroidViewModel(application) {

    private val plantDao = PlantDatabase.getInstance(application).plantDao()
    private val wateringLogDao = PlantDatabase.getInstance(application).wateringLogDao()

    var careItems by mutableStateOf<List<PlantCareItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 订阅存活植物列表的变化
            plantDao.getAlivePlants().collect { plants ->
                computeCareItems(plants)
                isLoading = false
            }
        }
    }

    // 根据植物列表计算每株的养护状态
    private suspend fun computeCareItems(plants: List<Plant>) {
        val now = System.currentTimeMillis()
        val items = plants.map { plant ->
            val last = wateringLogDao.getLastWatering(plant.id)
            val daysSince = if (last != null) {
                ((now - last.wateredAt) / 86_400_000L).toInt().coerceAtLeast(0)
            } else {
                -1 // 从未浇水
            }
            val daysUntilDue = if (daysSince < 0) {
                // 从未浇水：视为超期，用负值（interval+1）表示
                -(plant.wateringIntervalDays + 1)
            } else {
                plant.wateringIntervalDays - daysSince
            }
            PlantCareItem(plant, daysSince, daysUntilDue)
        }
        // 按紧急程度升序排列（越小越靠前）
        careItems = items.sortedBy { it.daysUntilDue }
    }

    // 一键浇水：写入记录后立即刷新养护列表
    fun waterPlant(plantId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            wateringLogDao.insert(WateringLog(plantId = plantId))
            // 重新拉取一次快照并刷新状态
            val plants = plantDao.getAlivePlants().first()
            computeCareItems(plants)
            onSuccess()
        }
    }
}
