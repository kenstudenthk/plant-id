package com.example.plant_id.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.plant_id.data.entity.WateringLog
import kotlinx.coroutines.flow.Flow

/**
 * 浇水记录 DAO
 * 负责 watering_logs 表的所有操作
 */
@Dao
interface WateringLogDao {

    /** 插入一条浇水记录，返回新行的 ID */
    @Insert
    suspend fun insert(log: WateringLog): Long

    /** 删除指定浇水记录（用于撤销错误操作） */
    @Delete
    suspend fun delete(log: WateringLog)

    /**
     * 获取某株植物的所有浇水记录，按时间倒序
     * 使用 Flow，列表会随数据库变化自动刷新
     */
    @Query("SELECT * FROM watering_logs WHERE plantId = :plantId ORDER BY wateredAt DESC")
    fun getLogsByPlant(plantId: Long): Flow<List<WateringLog>>

    /**
     * 获取某株植物最近一次浇水记录
     * 用于计算"距上次浇水天数"和判断是否需要提醒
     */
    @Query("SELECT * FROM watering_logs WHERE plantId = :plantId ORDER BY wateredAt DESC LIMIT 1")
    suspend fun getLastWatering(plantId: Long): WateringLog?

    /** 统计某株植物的总浇水次数（用于「我的」页面统计） */
    @Query("SELECT COUNT(*) FROM watering_logs WHERE plantId = :plantId")
    suspend fun countByPlant(plantId: Long): Int

    /** 统计所有植物的总浇水次数 */
    @Query("SELECT COUNT(*) FROM watering_logs")
    suspend fun countAll(): Int
}
