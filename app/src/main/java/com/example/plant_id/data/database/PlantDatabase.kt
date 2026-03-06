package com.example.plant_id.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.plant_id.data.dao.PhotoDao
import com.example.plant_id.data.dao.PlantDao
import com.example.plant_id.data.dao.WateringLogDao
import com.example.plant_id.data.entity.Photo
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.data.entity.WateringLog

/**
 * 植物身份证本地数据库
 *
 * 包含 3 张表：plants、watering_logs、photos
 * version = 2：新增 plants.archivedAt 列，记录终结归档时间
 */
@Database(
    entities = [Plant::class, WateringLog::class, Photo::class],
    version = 2,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao
    abstract fun wateringLogDao(): WateringLogDao
    abstract fun photoDao(): PhotoDao

    companion object {

        /** v1 → v2：为 plants 表添加 archivedAt 列（可空 INTEGER，默认 NULL） */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE plants ADD COLUMN archivedAt INTEGER")
            }
        }

        /**
         * 单例实例
         * @Volatile 确保多线程环境下可见性
         */
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        /**
         * 获取数据库实例（单例）
         * 整个 app 生命周期内只创建一次数据库连接
         */
        fun getInstance(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"   // 数据库文件名
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
