package com.example.plant_id.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 植物档案表
 * 每一行代表一株植物的完整档案
 */
@Entity(tableName = "plants")
data class Plant(

    /** 自增主键，植物唯一 ID */
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 植物名称，如：绿萝、发财树 */
    val name: String,

    /** 品种，如：心叶绿萝（可选） */
    val species: String = "",

    /** 入手日期，存储为 Unix 时间戳（毫秒） */
    val acquiredDate: Long = System.currentTimeMillis(),

    /** 浇水间隔天数，如：3 表示每 3 天浇一次水 */
    val wateringIntervalDays: Int = 7,

    /** 养护备注，如喜光/耐阴偏好等 */
    val notes: String = "",

    /**
     * 档案图标名称，对应 png/ 文件夹中的文件名（不含扩展名）
     * 可选值：monstera, cactus, money-tree, succulent,
     *         bird-of-paradise, spider-plant, hoya,
     *         orchid, schefflera
     */
    val iconName: String = "monstera",

    /**
     * 植物当前状态
     * - "alive"    存活中（默认）
     * - "archived" 已终结/已归档
     */
    val status: String = "alive",

    /** 绑定的 NFC 标签 ID，未绑定时为空字符串 */
    val nfcTagId: String = "",

    /** 档案创建时间，Unix 时间戳（毫秒） */
    val createdAt: Long = System.currentTimeMillis(),

    /** 终结归档时间，Unix 时间戳（毫秒）；存活中时为 null */
    val archivedAt: Long? = null
)
