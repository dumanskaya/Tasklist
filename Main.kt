package tasklist

import java.io.File
import kotlinx.datetime.*

const val BORDER_KNOT = "+"
const val HORIZONTAL_BORDER = "-"
const val VERTICAL_BORDER = "|"
const val NUMBER_WIDTH = 4
const val DATE_WIDTH = 12
const val TIME_WIDTH = 7
const val PRIORITY_WIDTH = 3
const val TAG_WIDTH = 3
const val TASK_WIDTH = 44
const val FILE_NAME = "tasklist.json"

val widths = listOf(NUMBER_WIDTH, DATE_WIDTH, TIME_WIDTH, PRIORITY_WIDTH, TAG_WIDTH, TASK_WIDTH)
val header = listOf("N", "Date", "Time", "P", "D", "Task ")

class Task {
    var priority: TaskPriority = TaskPriority()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
    var lines = mutableListOf<String>()

    fun tag(): Char {
        val (year, month, day) = this.date.toString().split("-").map { it.toInt() }
        val taskDate = LocalDate(year, month, day)
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+1")).date
        val numberOfDays = currentDate.daysUntil(taskDate)
        return when {
            numberOfDays > 0 -> 'I'
            numberOfDays < 0 -> 'O'
            else -> 'T'
        }
    }

    fun tagColored(): String = when (tag()) {
            'I' -> " \u001B[102m \u001B[0m "
            'O' -> " \u001B[101m \u001B[0m "
            'T' -> " \u001B[103m \u001B[0m "
            else -> ""
    }

    fun isValid(): Boolean = lines.isNotEmpty()

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

fun printHorizontalLine() {
    var line = BORDER_KNOT
    for (w in widths) {
        line += HORIZONTAL_BORDER.repeat(w) + BORDER_KNOT
    }
    println(line)
}

fun centerString(str: Any, width: Int): String {
    val value = str.toString()
    val n = width - value.length
    return value.padStart(n / 2 + value.length).padEnd(width)
}

fun addVerticalBorders(myList: List<Any>): String = myList
    .joinToString(separator = VERTICAL_BORDER, prefix = VERTICAL_BORDER,
        postfix = VERTICAL_BORDER)

fun getHeader(): String = addVerticalBorders(header.mapIndexed { i, s -> centerString(s, widths[i]) })

fun emptyRowWithBorders(): String = addVerticalBorders(List<String>(5) { "" }.
    mapIndexed { i, s ->  centerString(s, widths[i]) })

fun splitAndPadDescription(lines: List<String>): List<String> = lines
    .map {it.chunked(TASK_WIDTH)}
    .flatten()
    .map { it.padEnd(TASK_WIDTH) }

fun printTasks(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else {
        printHorizontalLine()
        println(getHeader())
        printHorizontalLine()
        for ((i, task) in tasks.withIndex()) {
            val firstLine = addVerticalBorders(listOf("${i + 1}", task.date, task.time,
                task.priority.getColor(), task.tagColored())
                .mapIndexed() { j, s -> centerString(s, widths[j]) })
            val descriptions = splitAndPadDescription(task.lines)
            val n = descriptions.size
            println("$firstLine${descriptions[0]}$VERTICAL_BORDER")
            for (i in 1 until n) {
                println("${emptyRowWithBorders()}${descriptions[i]}$VERTICAL_BORDER")
            }
            printHorizontalLine()
        }
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
    val jsonFile = File(FILE_NAME)
    var tasks = mutableListOf<Task>()
    if (jsonFile.exists()) {
        tasks = taskListAdapter.fromJson(jsonFile.readText())!!.toMutableList()
    }
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
            "end" -> {
                jsonFile.writeText(taskListAdapter.toJson(tasks))
                println("Tasklist exiting!")
                break
            }
            else -> println("The input action is invalid")
        }
    }
}