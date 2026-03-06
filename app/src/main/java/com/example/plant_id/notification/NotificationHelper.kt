package com.example.plant_id.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.plant_id.MainActivity
import com.example.plant_id.R
import com.example.plant_id.data.entity.Plant

/**
 * 通知辅助工具
 * 负责：
 * 1. 创建通知渠道（Android 8.0+ 必须）
 * 2. 向指定植物发送"该浇水了"提醒
 * 3. 通知携带 Deep Link，点击后打开对应植物详情页
 */
object NotificationHelper {

    const val CHANNEL_ID = "plant_watering_reminders"
    const val CHANNEL_NAME = "浇水提醒"

    /** 创建通知渠道（应在 Application 启动时调用一次） */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "植物超期未浇水时发出提醒"
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    /**
     * 向某株植物发送浇水提醒通知
     * @param plant        目标植物
     * @param daysSince    距上次浇水或入手天数
     */
    fun sendWateringReminder(context: Context, plant: Plant, daysSince: Int) {
        val notifManager = context.getSystemService(NotificationManager::class.java)

        // 检查是否有权限（Android 13+ 动态权限）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!notifManager.areNotificationsEnabled()) return
        }

        // 点击通知时打开 MainActivity，并携带植物 ID
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_PLANT_ID, plant.id)
        }
        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            plant.id.toInt(),
            contentIntent,
            pendingFlags
        )

        val overdueText = if (daysSince >= plant.wateringIntervalDays) {
            "已超期 ${daysSince - plant.wateringIntervalDays + 1} 天，记得浇水哦"
        } else {
            "距离下次浇水还有 ${plant.wateringIntervalDays - daysSince} 天"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${plant.name} 该浇水了 💧")
            .setContentText(overdueText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // 以 plantId 作为通知 ID，保证同一植物不会堆积多条
        notifManager.notify(plant.id.toInt(), notification)
    }

    /** Intent extra 键名：植物 ID（用于通知深链接） */
    const val EXTRA_PLANT_ID = "plant_id"
}
