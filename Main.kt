package tasklist
import kotlinx.datetime.*
import java.time.LocalTime

const val PADDING_LENGTH = 3

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
    override fun isValid(value: String): Boolean = try {
        val (hours, minutes) = value.split(":").map { it.toInt() }
        LocalTime.of(hours, minutes)
        true
        } catch (e: Exception) { false }
}

class Task {
    var priority: TaskPriority = TaskPriority()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
    var lines = mutableListOf<String>()

    override fun toString(): String = "$date $time $priority\n".padStart(PADDING_LENGTH) +
            lines.joinToString(separator = "\n", postfix = "\n",
                transform = { it.padStart(it.length + PADDING_LENGTH) })
}

fun createTask(): Task? {
    val task = Task()
    task.priority.inputValue()
    task.date.inputValue()
    task.time.inputValue()

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

fun isTasksListEmpty(tasks: MutableList<Task>): Boolean = if (tasks.isEmpty()) {
    println("No tasks have been input")
    true
    } else { false }

fun getValidIndex(tasks: MutableList<Task>): Int {
    val n = tasks.size
    println("Input the task number (1-$n):")
    var index = readln()
    while(true) {
        val i = index.toIntOrNull()
        if (i != null && i in 1..n) return i - 1
        else {
            println("Invalid task number")
            println("Input the task number (1-$n):")
            index = readln()
        }}
    }


fun printTasks(tasks: MutableList<Task>) {
    if (!isTasksListEmpty(tasks)) {
        tasks.forEachIndexed { i, task ->
            println("${(i + 1).toString().padEnd(PADDING_LENGTH)}$task")}
    }
}

fun deleteTask(tasks: MutableList<Task>) {
    if (tasks.isNotEmpty()) {
        val i = getValidIndex(tasks)
        tasks.removeAt(i)
        println("The task is deleted")
    }
}

fun main() {
    val tasks = mutableListOf<Task>()
    while(true) {
        println("Input an action (add, print, delete, end):")
        when(readln()) {
            "add" -> {
                val task = createTask()
                if (task != null) tasks.add(task)
            }
            "print" -> printTasks(tasks)
            "delete" -> {
                printTasks(tasks)
                deleteTask(tasks)
            }
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }
}