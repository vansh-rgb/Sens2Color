package com.strink.hardwaresensors

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.getSystemService
import com.strink.hardwaresensors.databinding.ActivityMainBinding
import kotlin.math.roundToInt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var proxySensorListener: SensorEventListener
    private lateinit var accelSensorListener: SensorEventListener
    private lateinit var proxySensor: Sensor
    private lateinit var accelSensor: Sensor
    private val colors = arrayOf(Color.RED, Color.CYAN, Color.GREEN, Color.YELLOW)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService()!!
        proxySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        proxySensorListener = object: SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {

                if (p0!!.values[0] > 0) {
                    val x = colors[Random.nextInt(4)]
                    Log.d("colorCheck", "onSensorChanged: $x")
                    binding.proxy.setBackgroundColor(x)
                }
            }
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                Log.d("Accuracy P1:", "onAccuracyChanged: $p1")
            }
        }

        accelSensorListener = object: SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {

                val bgColor = Color.rgb(
                    accel2color(p0!!.values[0]),
                    accel2color(p0.values[1]),
                    accel2color(p0.values[2])
                )

                binding.acclelometer.setBackgroundColor(bgColor)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                Log.d("Accuracy P1:", "onAccuracyChanged: $p1")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            proxySensorListener,
            proxySensor,
            1000 * 1000
        )
        sensorManager.registerListener(
            accelSensorListener,
            accelSensor,
            1000 * 1000
        )
    }

    override fun onPause() {
        sensorManager.unregisterListener(proxySensorListener)
        sensorManager.unregisterListener(accelSensorListener)
        super.onPause()

    }

    private fun accel2color(accel: Float) = (((accel+20)/24)*255).roundToInt()
}