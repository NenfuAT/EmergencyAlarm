package com.nenfuat.emergencyalarm

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class ForegroundNotificationListener : NotificationListenerService() {

    private lateinit var mediaPlayer: MediaPlayer
    companion object {
        const val NOTIFICATION_ID = 101 // 通知のID
        const val CHANNEL_ID = "ForegroundServiceChannel" // チャネルID
    }

    private lateinit var notificationManager: NotificationManager


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val notification = sbn?.notification
        val contentText = notification?.extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        println(contentText)
        // "非常"という文字列が含まれている場合にアプリを起動する
        if (contentText?.contains("動作") == true) {
            // アプリを起動するIntentを作成する
            Log.d("flag","true")
            startMusic()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // 通知が削除された際の処理
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ForegroundNotificationListener", "Service stopped")
        stopForeground(true)
        stopSelf() // サービスを停止する
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service","start")

        // アクションが "STOP_MUSIC" の場合は音楽を停止
        if (intent?.action == "STOP_MUSIC") {
            stopMusic()
        } else {
            val notificationText = "通知を監視中です"
            startForeground(NOTIFICATION_ID, createNotification(notificationText))
        }
        return START_STICKY
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(contentText: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // ボタンを含めるかどうかをチェック
        val actions = mutableListOf<Notification.Action>()
        if (Flags.includeStopButton.value) {
            // 音楽を停止するアクションを追加
            val stopMusicIntent = Intent(this, ForegroundNotificationListener::class.java)
            stopMusicIntent.action = "STOP_MUSIC"
            val stopMusicPendingIntent = PendingIntent.getService(
                this,
                0,
                stopMusicIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val stopAction = Notification.Action.Builder(
                R.drawable.ic_launcher_foreground,
                "アラートを停止",
                stopMusicPendingIntent
            ).build()
            actions.add(stopAction)
        }

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            // アクションを設定
            .apply {
                if (actions.isNotEmpty()) {
                    for (action in actions) {
                        addAction(action)
                    }
                }
            }
            .build()
    }




    private fun updateNotification() {
        val notificationText = if (Flags.isMusicPlaying.value) {
            "異常が発生しました"
        } else {
            "通知を監視中です"
        }
        val notification = createNotification(notificationText)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun startMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        Flags.isMusicPlaying.value=true
        Flags.includeStopButton.value = true
        Log.d("a","再生中")
        updateNotification()
    }
    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()
        Flags.isMusicPlaying.value=false
        Flags.includeStopButton.value = false
        Log.d("a","停止")
        updateNotification()
    }
}
