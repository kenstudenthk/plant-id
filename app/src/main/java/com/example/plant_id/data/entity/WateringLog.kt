package com.example.plant_id.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 浇水记录表
 * 每一行代表一次浇水行为，关联到具体的植物
 */
@Entity(
    tableName = "watering_logs",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            // 植物档案删除时，对应的浇水记录也一并删除
            onDelete = ForeignKey.CASCADE
        )
    ],
    // 为 plantId 建立索引，加速按植物查询
    indices = [Index("plantId")]
)
data class WateringLog(

    /** 自增主键 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 关联的植物 ID（外键） */
    val plantId: Long,

    /** 浇水时间，Unix 时间戳（毫秒） */
    val wateredAt: Long = System.currentTimeMillis(),

    /**
     * 浇水时拍摄的照片路径（可选）
     * 为空字符串表示本次浇水没有拍照
     */
    val photoPath: String = ""
)
