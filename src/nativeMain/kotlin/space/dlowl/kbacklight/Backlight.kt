package space.dlowl.kbacklight

import com.soywiz.korio.file.std.localVfs
import com.soywiz.korio.lang.parseInt
import kotlinx.coroutines.runBlocking
import kotlin.math.max
import kotlin.math.min

class Backlight(classname: String, val name: String) {
    private val directory = localVfs("/sys/class/$classname/$name")
    val current = getValue("brightness")
    val maxValue = getValue("max_brightness")
    val percent = (current*100)/maxValue

    private fun getValue(filename: String): Int {
        var value = 0
        runBlocking {
            value = directory[filename].readString().trim().parseInt()
        }
        return value
    }

    fun setCurrent(value: Int) {
        require(value <= maxValue) { "Value to set must not be greater ${maxValue}" }
        require(value > 0) { "Value to set must not be negative" }

        runBlocking {
            directory["brightness"].writeString("$value")
        }
    }

    fun setPercent(value: Int) {
        require(value <= 100) { "Percent to set must not be greater than 100%" }
        require(value > 0) { "Percent to set must not be negative" }
        setCurrent((maxValue * value) / 100)
    }

    fun addCurrent(value: Int) {
        setCurrent(max(min(current + value, maxValue), 1))
    }

    fun addPercent(value: Int) {
        setPercent(max(min(percent + value, 100), 1))
    }

}

class BacklightService(val classname: String = "backlight") {
    private lateinit var backlightNames: List<String>
    init {
        runBlocking {
            backlightNames = localVfs("/sys/class/$classname").listNames().filter { it != "" }
        }
    }

    fun getNames() = backlightNames

    fun getBacklights() = backlightNames.map { get(it)!! }

    fun get(name: String): Backlight? {
        return if (name in backlightNames) {
            Backlight(classname, name)
        } else {
            null
        }
    }
}