package com.mika10095.escapefromkotlin.engine

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)!!
    var fov: Float
        get() = prefs.getFloat("raycasterFOV", 90f)
        set(value) = prefs.edit { putFloat("raycasterFOV", value.coerceIn(30f, 120f)) }
    var rayCount: Int
        get() = prefs.getInt("rayCount", 128)
        set(value) = prefs.edit { putInt("rayCount", value.coerceIn(16, 1024)) }
    var buttonAimSens: Float
        get() = prefs.getInt("buttonAimSens", 10).toFloat() / 10f
        set(value) = prefs.edit {
            putInt("buttonAimSens", (value.coerceIn(-4f, 4f) * 10).toInt())
        }
    var gyroAimSens: Float
        get() = prefs.getInt("gyroAimSens", 10).toFloat() / 10f
        set(value) = prefs.edit {
            putInt("gyroAimSens", (value.coerceIn(-4f, 4f) * 10).toInt())
        }
    var tiltAimSens: Float
        get() = prefs.getInt("tiltAimSens", 10).toFloat() / 10f
        set(value) = prefs.edit {
            putInt("tiltAimSens", (value.coerceIn(-4f, 4f) * 10).toInt())
        }
    var debug: Boolean
        get() = prefs.getBoolean("debug", false)
        set(value) = prefs.edit { putBoolean("debug", value) }
    var useButtonAim: Boolean
        get() = prefs.getBoolean("useButtonAim", true)
        set(value) = prefs.edit { putBoolean("useButtonAim", value) }
    var useGyroAim: Boolean
        get() = prefs.getBoolean("useGyroAim", true)
        set(value) = prefs.edit { putBoolean("useGyroAim", value) }
    var useTiltAim: Boolean
        get() = prefs.getBoolean("useTiltAim", true)
        set(value) = prefs.edit { putBoolean("useTiltAim", value) }
    var useTiltMap: Boolean
        get() = prefs.getBoolean("useTiltMap", true)
        set(value) = prefs.edit { putBoolean("useTiltMap", value) }
    var useButtonMap: Boolean
        get() = prefs.getBoolean("useButtonMap", true)
        set(value) = prefs.edit { putBoolean("useButtonMap", value) }

    fun clearPrefs() {
        prefs.edit { clear() }
    }
}