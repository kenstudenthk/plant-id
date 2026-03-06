package com.example.plant_id.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.plant_id.data.entity.Plant
import kotlinx.coroutines.flow.Flow

/**
 * 植物档案 DAO
 * 负责 plants 表的所有增删改查操作
 */
@Dao
interface PlantDao {

    // ─── 写操作 ───────────────────────────────────────────

    /** 插入一条植物记录，返回新行的 ID */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: Plant): Long

    /** 更新已有的植物记录 */
    @Update
    suspend fun update(plant: Plant)

    /** 删除指定植物记录 */
    @Delete
    suspend fun delete(plant: Plant)

    // ─── 读操作（Flow：数据变化时 UI 自动刷新）─────────────

    /** 获取所有存活中的植物（含休眠中/病恹恹），按创建时间倒序 */
    @Query("SELECT * FROM plants WHERE status IN ('alive', 'dormant', 'sick') ORDER BY createdAt DESC")
    fun getAlivePlants(): Flow<List<Plant>>

    /** 获取所有已归档的植物，按创建时间倒序 */
    @Query("SELECT * FROM plants WHERE status = 'archived' ORDER BY createdAt DESC")
    fun getArchivedPlants(): Flow<List<Plant>>

    /** 根据 ID 获取单株植物（Flow，档案页使用） */
    @Query("SELECT * FROM plants WHERE id = :id")
    fun getPlantById(id: Long): Flow<Plant?>

    // ─── 读操作（suspend：直接取一次快照）────────────────────

    /** 根据 NFC 标签 ID 查找植物（触碰 NFC 时用） */
    @Query("SELECT * FROM plants WHERE nfcTagId = :tagId LIMIT 1")
    suspend fun getPlantByNfcTag(tagId: String): Plant?

    /** 获取所有绑定了 NFC 标签的植物 tagId 列表（用于 NfcViewModel 缓存初始化） */
    @Query("SELECT nfcTagId FROM plants WHERE nfcTagId != ''")
    suspend fun getAllNfcTagIds(): List<String>

    /** 获取所有存活中植物的快照（用于后台推送通知检查） */
    @Query("SELECT * FROM plants WHERE status = 'alive'")
    suspend fun getAllAlivePlantsSnapshot(): List<Plant>

    /** 统计存活中的植物总数（用于「我的」页面统计卡片） */
    @Query("SELECT COUNT(*) FROM plants WHERE status = 'alive'")
    suspend fun countAlivePlants(): Int
}
