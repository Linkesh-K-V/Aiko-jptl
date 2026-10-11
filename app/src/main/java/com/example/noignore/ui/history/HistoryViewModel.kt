package com.example.noignore.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noignore.data.AppDatabase
import com.example.noignore.data.TaskHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)

    private val _historyByDate = MutableStateFlow<Map<String, List<TaskHistory>>>(emptyMap())
    val historyByDate: StateFlow<Map<String, List<TaskHistory>>> = _historyByDate.asStateFlow()

    private val _totalDone = MutableStateFlow(0)
    val totalDone: StateFlow<Int> = _totalDone.asStateFlow()

    private val _totalMissed = MutableStateFlow(0)
    val totalMissed: StateFlow<Int> = _totalMissed.asStateFlow()

    private val _completionRate = MutableStateFlow(1.0f)
    val completionRate: StateFlow<Float> = _completionRate.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = db.taskHistoryDao().getAllHistory()
                val grouped = list.groupBy { it.date }.toSortedMap(compareByDescending { it })

                val done = list.count { it.status == "DONE" }
                val missed = list.count { it.status == "MISSED" }
                val total = done + missed
                val rate = if (total > 0) done.toFloat() / total else 1.0f

                _historyByDate.value = grouped
                _totalDone.value = done
                _totalMissed.value = missed
                _completionRate.value = rate
            }
        }
    }
}
