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
    enum class Priority {C, H, N, L}
    val priorityList = Priority.values().joinToString(", ") { it.toString() }
    override val inputMessage = "Input the task priority ($priorityList):"
    override fun isValid(value: String) = try {
        Priority.valueOf(value.uppercase())
        true
    } catch(e: IllegalArgumentException) { false }

    fun getColor(): String {
        val color = when(Priority.valueOf(value.uppercase())) {
            Priority.C -> Color.red
            Priority.H -> Color.yellow
            Priority.N -> Color.green
            Priority.L -> Color.blue
        }
        return " ${color.color} "
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
