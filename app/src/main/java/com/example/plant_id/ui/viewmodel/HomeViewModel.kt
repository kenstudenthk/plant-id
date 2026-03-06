package com.example.plant_id.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.plant_id.data.database.PlantDatabase
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.ui.components.WateringUrgency
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel
 * - 从数据库实时观察存活/已归档植物列表（Flow → StateFlow）
 * - 首次启动时自动植入示例数据（方便阶段四验收）
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val plantDao = PlantDatabase.getInstance(application).plantDao()
    private val wateringLogDao = PlantDatabase.getInstance(application).wateringLogDao()

    /** 当前选中的 Tab（0=存活中，1=已归档），保存在 ViewModel 中避免导航返回时重置 */
    var selectedTab by mutableIntStateOf(0)

    /** 浇水状态映射表：plantId → WateringUrgency（用于快速查询） */
    var wateringStatusMap by mutableStateOf<Map<Long, WateringUrgency>>(emptyMap())
        private set

    /** 存活中的植物列表（实时） */
    val alivePlants: StateFlow<List<Plant>> = plantDao.getAlivePlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** 已归档的植物列表（实时） */
    val archivedPlants: StateFlow<List<Plant>> = plantDao.getArchivedPlants()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        // 仅在「从未初始化过」时植入示例数据，用 SharedPreferences 持久化标记
        // 防止用户删除全部档案后重复植入
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val seeded = prefs.getBoolean("sample_data_seeded", false)
        if (!seeded) {
            viewModelScope.launch {
                seedSampleData()
                prefs.edit().putBoolean("sample_data_seeded", true).apply()
            }
        }

        // 订阅存活植物的浇水状态，实时更新状态映射表
        viewModelScope.launch {
            plantDao.getAlivePlants().collect { plants ->
                // 计算浇水状态（同步进行，无需 suspend）
                val now = System.currentTimeMillis()
                val statusMap = mutableMapOf<Long, WateringUrgency>()

                plants.forEach { plant ->
                    // 使用 runBlocking 等待异步数据库查询
                    val last = try {
                        wateringLogDao.getLastWatering(plant.id)
                    } catch (e: Exception) {
                        null
                    }

                    val daysSince = if (last != null) {
                        ((now - last.wateredAt) / 86_400_000L).toInt().coerceAtLeast(0)
                    } else {
                        -1 // 从未浇水
                    }

                    val urgency = when {
                        daysSince < 0 -> WateringUrgency.OK              // 从未浇水暂不显示紧急
                        daysSince > plant.wateringIntervalDays -> WateringUrgency.OVERDUE      // 已超期
                        daysSince == plant.wateringIntervalDays -> WateringUrgency.DUE_TODAY   // 今日到期
                        else -> WateringUrgency.OK
                    }

                    statusMap[plant.id] = urgency
                }

                wateringStatusMap = statusMap
            }
        }
    }

    /** 写入示例植物数据（仅数据库为空时执行一次） */
    private suspend fun seedSampleData() {
        listOf(
            Plant(
                name = "绿萝",
                species = "魔鬼藤",
                wateringIntervalDays = 5,
                iconName = "pothos",
                notes = "适合室内散射光，土干透后再浇"
            ),
            Plant(
                name = "仙人掌",
                species = "金琥",
                wateringIntervalDays = 21,
                iconName = "cactus",
                notes = "喜强光，极耐旱，冬季减少浇水"
            ),
            Plant(
                name = "虎皮兰",
                species = "虎尾兰属",
                wateringIntervalDays = 14,
                iconName = "haworthia",
                notes = "耐阴耐旱，新手首选，忌积水"
            ),
            Plant(
                name = "龟背竹",
                species = "天南星科",
                wateringIntervalDays = 7,
                iconName = "monstera",
                notes = "喜温暖湿润，夏季保持土壤微湿"
            ),
        ).forEach { plantDao.insert(it) }
    }
}
