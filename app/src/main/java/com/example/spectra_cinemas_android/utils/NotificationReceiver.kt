package com.example.spectra_cinemas_android.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Spectra Cinemas"
        val message = intent.getStringExtra("message") ?: ""
        val orderId = intent.getStringExtra("ORDER_ID")
        NotificationHelper.sendNotification(context, title, message, orderId = orderId)
    }
}
