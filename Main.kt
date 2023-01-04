package tasklist
import kotlinx.datetime.*

interface Parameter {
    fun isValid(value: String): Boolean
}

class TaskDate: Parameter {
    var date: String = "1970-01-01"
        set(value) {
            if(isValid(value)) field = value
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

    fun setValueFromUser(){
        println("Input the date (yyyy-mm-dd):")
        var line = readln()
        while (!isValid(line)) {
            println("The input date is invalid")
            println("Input the date (yyyy-mm-dd):")
            line = readln()
        }
        this.date = line
    }

    override fun toString(): String {
        return this.date
    }
}

class TaskTime: Parameter {
    var time: String = "00:00"
        set(value) {
            if (isValid(value)) field = value
        }

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

    fun setValueFromUser(){
        println("Input the time (hh:mm):")
        var line = readln()
        while (!isValid(line)) {
            println("The input time is invalid")
            println("Input the time (hh:mm):")
            line = readln()
        }
        this.time = line
    }

    override fun toString(): String {
        return this.time
    }
}



class Task {
    var lines = mutableListOf<String>()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
}

fun createTask(): Task? {
    val task = Task()
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
            println("$sep${tasks[i].date} ${tasks[i].time}")
            for (j in tasks[i].lines.indices) {
                println("   ${tasks[i].lines[j]}")
            }
            println()
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
