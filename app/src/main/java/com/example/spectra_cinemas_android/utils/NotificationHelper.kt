package com.example.spectra_cinemas_android.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.spectra_cinemas_android.R

object NotificationHelper {
    private const val CHANNEL_ID = "spectra_notifications"
    private const val CHANNEL_NAME = "Spectra Cinemas Notifications"
    private const val CHANNEL_DESC = "Notifications for account and bookings"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt(), orderId: String? = null) {
        val intent = Intent(context, com.example.spectra_cinemas_android.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("ORDER_ID", orderId)
            putExtra("GO_TO_TICKET", orderId != null)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            notificationId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.l_spectra_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                // Android 13+ permission handling
            }
        }
    }

    fun scheduleNotification(context: Context, title: String, message: String, delayMillis: Long, orderId: String? = null) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("ORDER_ID", orderId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.NOTIFICATION_SERVICE.let { Context.ALARM_SERVICE }) as AlarmManager
        val triggerTime = System.currentTimeMillis() + delayMillis
        
        // Χρησιμοποιούμε την απλή set αντί για την setExact για να αποφύγουμε το SecurityException σε Android 13/14
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
