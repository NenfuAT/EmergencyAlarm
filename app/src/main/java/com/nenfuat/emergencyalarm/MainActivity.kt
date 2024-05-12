package com.nenfuat.emergencyalarm
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.nenfuat.emergencyalarm.ui.theme.EmergencyAlarmTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestNotificationListenerPermission(this)

        enableEdgeToEdge()
        setContent {

            EmergencyAlarmTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (Flags.isMusicPlaying.value) {
                            StopMusicButton()
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun StopMusicButton() {
    val context = LocalContext.current
    Button(
        onClick = { stopMusic(context) },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth() // ボタンの幅を親要素に合わせる
    ) {
        Text(text = "Stop Service")
    }
}

fun stopMusic(context: Context) {
    // サービスに音楽停止のアクションを送信

    val stopIntent = Intent(context, ForegroundNotificationListener::class.java)
    stopIntent.action = "STOP_MUSIC"
    context.startService(stopIntent)
}

private fun requestNotificationListenerPermission(context: Context) {
    if (!isNotificationListenerEnabled(context)) {
        val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

private fun isNotificationListenerEnabled(context: Context): Boolean {
    val packageName = context.packageName // MainActivity内で利用可能なthisを使用してpackageNameを取得
    val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return flat != null && flat.contains(packageName)
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EmergencyAlarmTheme {
        Greeting("Android")
    }
}
