package com.example.noignore.reminder

import android.app.KeyguardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.Task
import com.example.noignore.data.TaskHistory
import com.example.noignore.model.RepeatMode
import com.example.ui.theme.MyApplicationTheme
import com.example.noignore.util.RecurringUtils
import com.example.noignore.util.TaskHapticFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AlarmActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow display over lock screen and wake up device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val taskId = intent.getIntExtra(ReminderScheduler.EXTRA_TASK_ID, -1)
        val isConfirm = intent.getBooleanExtra(ReminderScheduler.EXTRA_IS_CONFIRM, false)
        val ringtoneUriStr = intent.getStringExtra(ReminderScheduler.EXTRA_RINGTONE_URI)

        startAlarmSound(ringtoneUriStr)

        setContent {
            MyApplicationTheme {
                AlarmScreenContent(
                    taskId = taskId,
                    isConfirm = isConfirm,
                    onDismiss = {
                        stopAlarmSound()
                        finish()
                    },
                    onTaskIgnored = {
                        stopAlarmSound()
                        finish()
                    },
                    onTaskCompleted = {
                        stopAlarmSound()
                        finish()
                    },
                    onSnooze = {
                        stopAlarmSound()
                        finish()
                    }
                )
            }
        }
    }

    private fun startAlarmSound(customUri: String?) {
        try {
            val alertUri: Uri = if (!customUri.isNullOrEmpty()) {
                Uri.parse(customUri)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            ringtone = RingtoneManager.getRingtone(applicationContext, alertUri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopAlarmSound() {
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        stopAlarmSound()
        super.onDestroy()
    }
}

@Composable
fun AlarmScreenContent(
    taskId: Int,
    isConfirm: Boolean,
    onDismiss: () -> Unit,
    onTaskIgnored: () -> Unit,
    onTaskCompleted: () -> Unit,
    onSnooze: () -> Unit
) {
    // Prevent back button dismissal
    BackHandler { }

    val coroutineScope = rememberCoroutineScope()
    var task by remember { mutableStateOf<Task?>(null) }
    var showIgnoreDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(taskId) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(context)
            task = db.taskDao().getTask(taskId)
        }
    }

    val taskTitle = task?.title ?: "Scheduled Task"

    val backgroundBrush = Brush.verticalGradient(
        colors = if (isConfirm) {
            listOf(Color(0xFF2C1014), Color(0xFF14080A), Color(0xFF0D0304))
        } else {
            listOf(Color(0xFF330C10), Color(0xFF1F0608), Color(0xFF100203))
        }
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Warning icon badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isConfirm) Color(0xFFFF9800).copy(alpha = 0.2f) else Color(0xFFE53935).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isConfirm) Icons.Default.Warning else Icons.Default.Alarm,
                        contentDescription = "Alarm status",
                        tint = if (isConfirm) Color(0xFFFFB74D) else Color(0xFFFF5252),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isConfirm) "CONFIRMATION CHECK" else "NO IGNORE ALARM",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isConfirm) Color(0xFFFFB74D) else Color(0xFFFF5252),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConfirm) {
                        "Did you execute your task?"
                    } else {
                        "Time to take immediate action!"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Task details card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x33FFFFFF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = taskTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (task != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val timeText = String.format("%02d:%02d", task!!.hour, task!!.minute)
                            Text(
                                text = "Scheduled for $timeText",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFB0BEC5)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (!isConfirm) {
                    // MAIN ALARM BUTTONS
                    Button(
                        onClick = {
                            // "I DID IT" -> dismisses alarm, stays scheduled for confirmation check
                            TaskHapticFeedback.performTaskCompleteHaptic(context)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("alarm_did_it_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I DID IT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            showIgnoreDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("alarm_ignored_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I IGNORED IT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // CONFIRMATION ALARM BUTTONS
                    Button(
                        onClick = {
                            TaskHapticFeedback.performTaskCompleteHaptic(context)
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val db = AppDatabase.getInstance(context)
                                    val today = LocalDate.now().toString()
                                    db.taskHistoryDao().insert(
                                        TaskHistory(
                                            taskId = taskId,
                                            date = today,
                                            status = "DONE"
                                        )
                                    )
                                    ReminderScheduler.cancel(context, taskId)
                                    val currentTask = db.taskDao().getTask(taskId)
                                    if (currentTask != null) {
                                        if (currentTask.repeatMode != RepeatMode.ONCE) {
                                            val nextMillis = RecurringUtils.nextOccurrence(
                                                currentTask.repeatMode,
                                                currentTask.hour,
                                                currentTask.minute,
                                                currentTask.repeatDays
                                            )
                                            val newTask = currentTask.copy(
                                                id = 0,
                                                timeMillis = nextMillis,
                                                completed = false
                                            )
                                            val newId = db.taskDao().insert(newTask).toInt()
                                            ReminderScheduler.schedule(
                                                context = context,
                                                taskId = newId,
                                                triggerAtMillis = nextMillis,
                                                confirmDelayMinutes = currentTask.confirmDelayMinutes,
                                                ringtoneUri = currentTask.ringtoneUri
                                            )
                                        }
                                        db.taskDao().delete(currentTask)
                                    }
                                }
                                onTaskCompleted()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("alarm_yes_did_it_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "YES I DID IT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            // Snooze 10 min
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    val nowPlus10 = System.currentTimeMillis() + (10 * 60_000L)
                                    val db = AppDatabase.getInstance(context)
                                    val currentTask = db.taskDao().getTask(taskId)
                                    if (currentTask != null) {
                                        ReminderScheduler.schedule(
                                            context = context,
                                            taskId = taskId,
                                            triggerAtMillis = nowPlus10,
                                            confirmDelayMinutes = currentTask.confirmDelayMinutes,
                                            ringtoneUri = currentTask.ringtoneUri
                                        )
                                    }
                                }
                                onSnooze()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("alarm_snooze_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFB74D)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SNOOZE 10 MIN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    if (showIgnoreDialog) {
        AlertDialog(
            onDismissRequest = { showIgnoreDialog = false },
            title = { Text("Admit Failure?") },
            text = {
                Text("Giving up registers this task as MISSED for today and breaks your discipline streak. Are you certain?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showIgnoreDialog = false
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                val db = AppDatabase.getInstance(context)
                                val today = LocalDate.now().toString()
                                db.taskHistoryDao().insert(
                                    TaskHistory(
                                        taskId = taskId,
                                        date = today,
                                        status = "MISSED"
                                    )
                                )
                                ReminderScheduler.cancel(context, taskId)
                                val currentTask = db.taskDao().getTask(taskId)
                                if (currentTask != null) {
                                    db.taskDao().delete(currentTask)
                                }
                            }
                            onTaskIgnored()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Yes, I Ignored It")
                }
            },
            dismissButton = {
                TextButton(onClick = { showIgnoreDialog = false }) {
                    Text("Go Back")
                }
            }
        )
    }
}
