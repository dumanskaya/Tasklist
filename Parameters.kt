package tasklist

import kotlinx.datetime.LocalDate
import java.time.LocalTime

abstract class Parameter {
    open var value: String = ""
        set(value) {
            if(isValid(value)) field = value
        }
    abstract val inputMessage: String
    open val warningMessage: String = ""
    abstract fun isValid(value: String): Boolean
    override fun toString(): String {
        return this.value
    }
    fun inputValue(){
        println(inputMessage)
        var line = readln()
        while (!isValid(line)) {
            println("${warningMessage}\n${inputMessage}")
            line = readln()
        }
        this.value = line
    }
}

class TaskPriority: Parameter() {
    override val inputMessage = "Input the task priority (C, H, N, L):"
    private val priorities = listOf("C", "H", "N", "L")
    override fun isValid(value: String): Boolean = value.uppercase() in priorities
    fun getColor(): String = when(value.uppercase()) {
        "C" -> " \u001B[101m \u001B[0m "
        "H" -> " \u001B[103m \u001B[0m "
        "N" -> " \u001B[102m \u001B[0m "
        "L" -> " \u001B[104m \u001B[0m "
        else -> " \u001B[0m "
    }
}

class TaskDate: Parameter() {
    override val inputMessage = "Input the date (yyyy-mm-dd):"
    override val warningMessage = "The input date is invalid"
    override var value: String = "1970-01-01"
        set(value) {
            if(isValid(value)) {
                val (year, month, day) = value.split("-").map {it.toInt()}
                field = LocalDate(year, month, day).toString()
            }
        }

    override fun isValid(value: String): Boolean = try {
        val (year, month, day) = value.split('-').map { it.toInt() }
        LocalDate(year, month, day)
        true
    } catch (e: Exception) { false }
}

class TaskTime: Parameter() {
    override val inputMessage = "Input the time (hh:mm):"
    override val warningMessage = "The input time is invalid"
    override var value: String = "00:00"
        set(value) {
            if(isValid(value)) {
                val (hours, minutes) = value.split(":").map { it.toInt() }
                field = LocalTime.of(hours, minutes).toString()
            }
        }
    override fun isValid(value: String): Boolean = try {
        val (hours, minutes) = value.split(":").map { it.toInt() }
        LocalTime.of(hours, minutes)
        true
    } catch (e: Exception) { false }
}
