package com.example.plant_id.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 照片记录表
 * 每一行代表一张与植物关联的照片
 */
@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plantId"],
            // 植物档案删除时，对应的所有照片记录也一并删除
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantId")]
)
data class Photo(

    /** 自增主键 */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 关联的植物 ID（外键） */
    val plantId: Long,

    /**
     * 照片在本地的存储路径
     * 格式：app 私有目录的绝对路径，如 /data/user/0/.../files/photos/1_20260227.jpg
     */
    val filePath: String,

    /** 拍摄时间，Unix 时间戳（毫秒） */
    val takenAt: Long = System.currentTimeMillis(),

    /** 照片备注，如"第一次开花"（可选） */
    val note: String = ""
)
