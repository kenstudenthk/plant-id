package com.example.plant_id.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.plant_id.data.entity.Photo
import kotlinx.coroutines.flow.Flow

/**
 * 照片记录 DAO
 * 负责 photos 表的所有操作
 */
@Dao
interface PhotoDao {

    /** 插入一条照片记录，返回新行的 ID */
    @Insert
    suspend fun insert(photo: Photo): Long

    /** 删除指定照片记录 */
    @Delete
    suspend fun delete(photo: Photo)

    /**
     * 获取某株植物的所有照片，按拍摄时间倒序
     * 使用 Flow，照片时间线会随数据库变化自动刷新
     */
    @Query("SELECT * FROM photos WHERE plantId = :plantId ORDER BY takenAt DESC")
    fun getPhotosByPlant(plantId: Long): Flow<List<Photo>>

    /**
     * 获取某株植物最新的一张照片
     * 用于在植物卡片上显示封面缩略图
     */
    @Query("SELECT * FROM photos WHERE plantId = :plantId ORDER BY takenAt DESC LIMIT 1")
    suspend fun getLatestPhoto(plantId: Long): Photo?

    /** 统计所有照片总数（用于「我的」页面统计） */
    @Query("SELECT COUNT(*) FROM photos")
    suspend fun countAll(): Int
}
