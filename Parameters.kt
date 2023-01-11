package tasklist

import com.squareup.moshi.JsonClass
import kotlinx.datetime.LocalDate
import java.time.LocalTime

abstract class Parameter<T> {
    var value: T? = null
    fun set(t: T) {
            value = t
    }
    abstract val inputMessage: String
    open val warningMessage: String = ""
    abstract fun fromString(str: String): T?
    override fun toString(): String {
        return this.value.toString()
    }
    open fun inputValue(){
        println(inputMessage)
        var line = readln()
        while (fromString(line) == null) {
            println("${warningMessage}\n${inputMessage}")
            line = readln()
        }
        this.value = fromString(line)
    }
}

@JsonClass(generateAdapter = true)
class TaskPriority: Parameter<TaskPriority.Priority>() {
    enum class Priority {C, H, N, L}
    private val priorityList = Priority.values().joinToString(", ") { it.toString() }
    override val inputMessage = "Input the task priority ($priorityList):"
    override fun fromString(str: String): Priority? = try {
        Priority.valueOf(str.uppercase())
    } catch(e: IllegalArgumentException) { null }

    fun getColor(): String {
        val color = when(value!!) {
            Priority.C -> Color.Red
            Priority.H -> Color.Yellow
            Priority.N -> Color.Green
            Priority.L -> Color.Blue
        }
        return " ${color.code} "
    }
}

@JsonClass(generateAdapter = true)
class TaskDate: Parameter<LocalDate>() {
    override val inputMessage = "Input the date (yyyy-mm-dd):"
    override val warningMessage = "The input date is invalid"
    override fun fromString(str: String): LocalDate? = try {
        val (year, month, day) = str.split('-').map { it.toInt() }
        LocalDate(year, month, day)
    } catch (e: Exception) { null }
}

@JsonClass(generateAdapter = true)
class TaskTime: Parameter<LocalTime>() {
    override val inputMessage = "Input the time (hh:mm):"
    override val warningMessage = "The input time is invalid"
    override fun fromString(str: String): LocalTime? = try {
        val (hours, minutes) = str.split(":").map { it.toInt() }
        LocalTime.of(hours, minutes)
    } catch (e: Exception) { null }
}

@JsonClass(generateAdapter = true)
class TaskDescription: Parameter<MutableList<String>> () {
    override val inputMessage: String = "Input a new task (enter a blank line to end):"
    override val warningMessage = "The task is blank"
    override fun inputValue() {
        val description = mutableListOf<String>()
        println(inputMessage)
        var line = readln().trim()
        if (line.isEmpty()) println(warningMessage)
        while (line.isNotEmpty()) {
            description.add(line)
            line = readln().trim()
        }
        value = description
    }

    override fun fromString(str: String): MutableList<String>? = str.split("/n").toMutableList()
}