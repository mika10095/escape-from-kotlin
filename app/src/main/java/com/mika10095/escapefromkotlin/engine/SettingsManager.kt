package com.mika10095.escapefromkotlin.engine

import android.content.Context
import androidx.core.content.edit

class SettingsManager(context: Context) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)!!
    var fov: Float
        get()=prefs.getFloat("raycasterFOV", 60f)
        set(value) = prefs.edit { putFloat("raycasterFOV", value.coerceIn(30f,120f)) }
    var rayCount: Int
        get()=prefs.getInt("rayCount", 128)
        set(value) = prefs.edit { putInt("rayCount", value.coerceIn(16,1024)) }
    var buttonAimSens: Float
        get()=prefs.getFloat("buttonAimSens", 1f)
        set(value) = prefs.edit { putFloat("buttonAimSens", value.coerceIn(-4f,4f)) }
    var gyroAimSens: Float
        get()=prefs.getFloat("gyroAimSens", 1f)
        set(value) = prefs.edit { putFloat("gyroAimSens", value.coerceIn(-4f,4f)) }
    var tiltAimSens: Float
        get()=prefs.getFloat("tiltAimSens", 1f)
        set(value) = prefs.edit { putFloat("tiltAimSens", value.coerceIn(-4f,4f)) }
    var useButtonAim: Boolean
        get()=prefs.getBoolean("useButtonAim", true)
        set(value) = prefs.edit { putBoolean("useButtonAim", value) }
    var useGyroAim: Boolean
        get()=prefs.getBoolean("useGyroAim", true)
        set(value) = prefs.edit { putBoolean("useGyroAim", value) }
    var useTiltAim: Boolean
        get()=prefs.getBoolean("useTiltAim", true)
        set(value) = prefs.edit { putBoolean("useTiltAim", value) }
    var useTiltMap: Boolean
        get()=prefs.getBoolean("useTiltMap", true)
        set(value) = prefs.edit { putBoolean("useTiltMap", value) }
    var useButtonMap: Boolean
        get()=prefs.getBoolean("useButtonMap", true)
        set(value) = prefs.edit { putBoolean("useButtonMap", value) }
}