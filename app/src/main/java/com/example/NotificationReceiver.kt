package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val type = intent.getStringExtra("notification_type") ?: "daily"

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.budgetDao()
            
            // Re-schedule alarm if we received a boot completed event
            if (Intent.ACTION_BOOT_COMPLETED == action) {
                val dailyEnabled = dao.getSettingValue("push_daily_enabled")?.toBoolean() ?: false
                val dailyTime = dao.getSettingValue("push_daily_time") ?: "20:00"
                NotificationScheduler.scheduleDailyNotification(context, dailyEnabled, dailyTime)
                return@launch
            }

            // Check if user enabled this notification type in settings
            when (type) {
                "daily" -> {
                    val enabled = dao.getSettingValue("push_daily_enabled")?.toBoolean() ?: false
                    if (enabled) {
                        showNotification(
                            context,
                            id = 101,
                            title = "Registra le tue spese! 📝",
                            text = "Non dimenticarti di annotare le spese di oggi per calcolare lo spendibile di domani."
                        )
                    }
                }
                "weekly_monthly" -> {
                    val enabled = dao.getSettingValue("push_weekly_monthly_enabled")?.toBoolean() ?: false
                    if (enabled) {
                        showNotification(
                            context,
                            id = 102,
                            title = "Riepilogo Spese 📊",
                            text = "Dai un'occhiata all'andamento del tuo budget in corso nella sezione Statistiche."
                        )
                    }
                }
                "budget_confirm" -> {
                    val enabled = dao.getSettingValue("push_budget_confirm_enabled")?.toBoolean() ?: false
                    if (enabled) {
                        showNotification(
                            context,
                            id = 103,
                            title = "Nuovo Mese Iniziato 📅",
                            text = "Oggi inizia il tuo ciclo di budget! Accedi a Today Budget per confermare o modificare la cifra."
                        )
                    }
                }
            }
        }
    }

    private fun showNotification(context: Context, id: Int, title: String, text: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "budget_alerts"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Avvisi Budget",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifiche e promemoria di spesa per Today Budget"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id, notification)
    }
}
