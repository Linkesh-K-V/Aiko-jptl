package com.example.noignore.data

import androidx.room.TypeConverter
import com.example.noignore.model.RepeatMode

class Converters {
    @TypeConverter
    fun fromRepeatMode(repeatMode: RepeatMode): String {
        return repeatMode.name
    }

    @TypeConverter
    fun toRepeatMode(value: String): RepeatMode {
        return try {
            RepeatMode.valueOf(value)
        } catch (e: Exception) {
            RepeatMode.ONCE
        }
    }
}
