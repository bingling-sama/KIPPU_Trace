package com.kippu.trace.data

import androidx.room.TypeConverter
import com.kippu.trace.model.DisplayMode

class Converters {
    @TypeConverter
    fun fromDisplayMode(mode: DisplayMode): String {
        return mode.name
    }

    @TypeConverter
    fun toDisplayMode(mode: String): DisplayMode {
        return DisplayMode.valueOf(mode)
    }
}
