package tasklist
import kotlinx.datetime.*

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
    fun setValueFromUser(){
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
    override fun isValid(value: String): Boolean {
        val priorities = listOf("C", "H", "N", "L")
        return value.uppercase() in priorities
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

    override fun isValid(value: String): Boolean {
        val parts = value.split('-')
        if (parts.size != 3) return false
        try {
            parts.map { it.toInt() }
        } catch (e: NumberFormatException) {
            return false
        }
        try {
            val (year, month, day) = parts.map { it.toInt() }
            LocalDate(year, month, day)
        } catch (e: IllegalArgumentException){
            return false
        }
        return true
    }
}

class TaskTime: Parameter() {
    override val inputMessage = "Input the time (hh:mm):"
    override val warningMessage = "The input time is invalid"
    override fun isValid(value: String): Boolean {
        val parts = value.split(":")
        if (parts.size != 2) return false
        try {
            parts.map { it.toInt() }
        } catch (e: NumberFormatException) {
            return false
        }
        val (hours, minutes) = parts.map { it.toInt() }
        if (hours in 0..23 && minutes in 0..59) return true
        return false
    }
}

class Task {
    var priority: TaskPriority = TaskPriority()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
    var lines = mutableListOf<String>()

    override fun toString(): String {
        var result = "$date $time $priority"
        result += lines.joinToString(prefix = "\n   ", separator = "\n   ", postfix = "\n")
        return result
    }
}

fun createTask(): Task? {
    val task = Task()
    task.priority.setValueFromUser()
    task.date.setValueFromUser()
    task.time.setValueFromUser()

    println("Input a new task (enter a blank line to end):")
    var line = readln().trim()
    if (line.isEmpty()) {
        println("The task is blank")
        return null
    }
    while (line.isNotEmpty()) {
        task.lines.add(line)
        line = readln().trim()
    }
    return task
}

fun printTasks(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else {
        for (i in tasks.indices) {
            val sep = (i + 1).toString().padEnd(3)
            println("$sep${tasks[i]}")
        }
    }
}

fun main() {
    val tasks = mutableListOf<Task>()
    while(true) {
        println("Input an action (add, print, end):")
        when(readln()) {
            "add" -> {
                val task = createTask()
                if (task != null) tasks.add(task)
            }
            "print" -> printTasks(tasks)
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }
}
