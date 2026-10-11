package com.example.noignore.ui.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.Task
import com.example.noignore.data.TaskHistory
import com.example.noignore.model.RepeatMode
import com.example.noignore.reminder.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    val tasks: StateFlow<List<Task>> = db.taskDao().getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(
        title: String,
        timeMillis: Long,
        confirmDelayMinutes: Int,
        repeatMode: RepeatMode,
        ringtoneUri: String? = null,
        onCreated: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                timeMillis = timeMillis,
                confirmDelayMinutes = confirmDelayMinutes,
                repeatMode = repeatMode,
                ringtoneUri = ringtoneUri,
                completed = false
            )

            val newId = withContext(Dispatchers.IO) {
                db.taskDao().insert(task).toInt()
            }

            ReminderScheduler.schedule(
                context = getApplication(),
                taskId = newId,
                triggerAtMillis = timeMillis,
                confirmDelayMinutes = confirmDelayMinutes,
                ringtoneUri = ringtoneUri
            )

            onCreated(newId)
        }
    }

    fun updateTask(
        task: Task,
        onUpdated: () -> Unit = {}
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.taskDao().update(task)
            }

            // Cancel any old scheduled alarms
            ReminderScheduler.cancel(getApplication(), task.id)

            // Reschedule if not yet completed and in the future
            if (!task.completed && task.timeMillis > System.currentTimeMillis()) {
                ReminderScheduler.schedule(
                    context = getApplication(),
                    taskId = task.id,
                    triggerAtMillis = task.timeMillis,
                    confirmDelayMinutes = task.confirmDelayMinutes,
                    ringtoneUri = task.ringtoneUri
                )
            }

            onUpdated()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), task.id)
            withContext(Dispatchers.IO) {
                db.taskDao().delete(task)
            }
        }
    }

    fun markCompleted(task: Task, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val todayStr = LocalDate.now().toString()

            withContext(Dispatchers.IO) {
                // Cancel scheduled alarms
                ReminderScheduler.cancel(getApplication(), task.id)

                // Update task state
                db.taskDao().update(task.copy(completed = true))

                // Insert into history
                db.taskHistoryDao().insert(
                    TaskHistory(
                        taskId = task.id,
                        taskTitle = task.title,
                        date = todayStr,
                        status = "DONE",
                        confirmedAt = now
                    )
                )
            }

            onComplete()
        }
    }

    suspend fun getTaskById(taskId: Long): Task? {
        return withContext(Dispatchers.IO) {
            db.taskDao().getTask(taskId.toInt())
        }
    }
}
