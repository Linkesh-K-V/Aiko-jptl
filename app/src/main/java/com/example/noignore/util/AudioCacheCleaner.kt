package com.example.noignore.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Utility for automatic audio and temporary file cache management.
 * Periodically prunes transient speech audio clips, temp files, and ensures
 * local application storage remains efficient and lightweight.
 */
object AudioCacheCleaner {

    private const val TAG = "AudioCacheCleaner"
    private const val MAX_CACHE_SIZE_BYTES = 50 * 1024 * 1024L // 50 MB threshold
    private const val MAX_FILE_AGE_MS = 3 * 24 * 60 * 60 * 1000L // 3 days

    /**
     * Cleans up stale speech recordings, temporary audio files, and orphan cache entries.
     */
    suspend fun cleanStaleAudioCache(context: Context): Long = withContext(Dispatchers.IO) {
        var bytesFreed = 0L
        try {
            val cacheDir = context.cacheDir ?: return@withContext 0L
            val now = System.currentTimeMillis()

            // 1. Clean audio subdirectory if present
            val audioSubDir = File(cacheDir, "audio")
            if (audioSubDir.exists() && audioSubDir.isDirectory) {
                audioSubDir.listFiles()?.forEach { file ->
                    if (file.isFile && (now - file.lastModified() > MAX_FILE_AGE_MS)) {
                        val size = file.length()
                        if (file.delete()) {
                            bytesFreed += size
                        }
                    }
                }
            }

            // 2. Clean general cache audio files (e.g. .mp3, .wav, .aac, .pcm, .tmp)
            cacheDir.listFiles()?.forEach { file ->
                if (file.isFile && (file.extension in listOf("wav", "mp3", "m4a", "aac", "tmp", "pcm"))) {
                    if (now - file.lastModified() > MAX_FILE_AGE_MS) {
                        val size = file.length()
                        if (file.delete()) {
                            bytesFreed += size
                        }
                    }
                }
            }

            // 3. Enforce maximum total cache budget
            val totalSize = getFolderSize(cacheDir)
            if (totalSize > MAX_CACHE_SIZE_BYTES) {
                val sortedFiles = cacheDir.walkTopDown()
                    .filter { it.isFile }
                    .sortedBy { it.lastModified() }
                    .toList()

                var currentTotal = totalSize
                for (file in sortedFiles) {
                    if (currentTotal <= MAX_CACHE_SIZE_BYTES / 2) break
                    val size = file.length()
                    if (file.delete()) {
                        bytesFreed += size
                        currentTotal -= size
                    }
                }
            }

            Log.i(TAG, "Audio cache auto-cleanup completed. Freed $bytesFreed bytes.")
        } catch (e: Exception) {
            Log.w(TAG, "Failed during audio cache cleanup", e)
        }
        bytesFreed
    }

    private fun getFolderSize(file: File): Long {
        var size = 0L
        try {
            if (file.isDirectory) {
                file.listFiles()?.forEach { child ->
                    size += getFolderSize(child)
                }
            } else {
                size += file.length()
            }
        } catch (_: Exception) {}
        return size
    }
}
