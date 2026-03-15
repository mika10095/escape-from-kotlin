package com.mika10095.escapefromkotlin.input

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class TiltInput(context: Context) : SensorEventListener {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val gyro: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    var turn = 0f
    var tilt = 0f
    fun start() {
        gyro?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }
    fun stop() {
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val tiltY = event!!.values[1]
        val tiltX = event.values[0]
        turn = if(abs(tiltY) > 0.2)
            (tiltY / 5f).coerceIn(-1f, 1f)
        else
            0f
        tilt = (tiltX / 5f).coerceIn(-1f, 1f)
    }
}