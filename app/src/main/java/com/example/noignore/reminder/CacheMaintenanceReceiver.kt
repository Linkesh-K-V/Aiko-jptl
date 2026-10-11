package com.example.noignore.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.noignore.util.AudioCacheCleaner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver scheduled to perform reliable background maintenance.
 * Prunes audio cache, ensures storage hygiene, and survives OEM task killers.
 */
class CacheMaintenanceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AudioCacheCleaner.cleanStaleAudioCache(context.applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
