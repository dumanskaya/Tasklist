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

class Task {
    var priority: TaskPriority = TaskPriority()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
    var lines = mutableListOf<String>()

    fun tag(): Char {
        val (year, month, day) = this.date.toString().split("-").map {it.toInt()}
        val taskDate = LocalDate(year, month, day)
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val numberOfDays = currentDate.daysUntil(taskDate)
        return when {
            numberOfDays > 0 -> 'I'
            numberOfDays < 0 -> 'O'
            else -> 'T'
        }
    }

    fun isValid(): Boolean = lines.isEmpty()

    fun edit() {
        while(true) {
            println("Input a field to edit (priority, date, time, task):")
            when(readln()) {
                "priority" -> { priority.inputValue(); break }
                "date" -> { date.inputValue(); break }
                "time" -> { time.inputValue(); break }
                "task" -> {
                    val newDescription = createTaskDescription()
                    if (newDescription.isNotEmpty()) {
                        lines = newDescription
                    }
                    break
                }
                else -> println("Invalid field")
            }
        }
        println("The task is changed")
    }

    override fun toString(): String = "$date $time $priority ${tag()}\n".padStart(PADDING_LENGTH) +
            lines.joinToString(separator = "\n", postfix = "\n",
                transform = { it.padStart(it.length + PADDING_LENGTH) })
}

fun createTaskDescription(): MutableList<String> {
    val description = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    var line = readln().trim()
    if (line.isEmpty()) println("The task is blank")
    while (line.isNotEmpty()) {
        description.add(line)
        line = readln().trim()
    }
    return description
}

fun createTask(): Task {
    val task = Task()
    task.priority.inputValue()
    task.date.inputValue()
    task.time.inputValue()
    task.lines = createTaskDescription()
    return task
}

fun inputValidIndex(tasks: MutableList<Task>): Int {
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
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else {
        tasks.forEachIndexed { i, task ->
            println("${(i + 1).toString().padEnd(PADDING_LENGTH)}$task")}
    }
}

fun editTask(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) return
    tasks[inputValidIndex(tasks)].edit()
}

fun deleteTask(tasks: MutableList<Task>) {
    if (tasks.isNotEmpty()) {
        tasks.removeAt(inputValidIndex(tasks))
        println("The task is deleted")
    }
}

fun main() {
    val tasks = mutableListOf<Task>()
    while(true) {
        println("Input an action (add, print, edit, delete, end):")
        when(readln()) {
            "add" -> {
                val task = createTask()
                if (task.isValid()) tasks.add(task)
            }
            "print" -> printTasks(tasks)
            "edit" -> {
                printTasks(tasks)
                editTask(tasks)
            }
            "delete" -> {
                printTasks(tasks)
                deleteTask(tasks)
            }
            "end" -> { println("Tasklist exiting!"); break }
            else -> println("The input action is invalid")
        }
    }
}