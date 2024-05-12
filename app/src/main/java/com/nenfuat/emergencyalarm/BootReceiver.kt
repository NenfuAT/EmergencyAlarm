package com.nenfuat.emergencyalarm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // サービスを起動する
            Log.d("reciver","power on")
            val serviceIntent = Intent( context, ForegroundNotificationListener::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
