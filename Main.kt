package tasklist

import com.squareup.moshi.JsonClass
import java.io.File
import kotlinx.datetime.*

const val FILE_NAME = "tasklist.json"

enum class Color(val code: String) {
    Red("\u001B[101m \u001B[0m"),
    Yellow("\u001B[103m \u001B[0m"),
    Green("\u001B[102m \u001B[0m"),
    Blue("\u001B[104m \u001B[0m")
}

@JsonClass(generateAdapter = true)
class Task {
    var priority: TaskPriority = TaskPriority()
    var date: TaskDate = TaskDate()
    var time: TaskTime = TaskTime()
    var description: TaskDescription = TaskDescription()
    enum class Tag { I, O, T }

    private fun tag(): Tag {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+1")).date
        val numberOfDays = currentDate.daysUntil(date.value!!)
        return when {
            numberOfDays > 0 -> Tag.I
            numberOfDays < 0 -> Tag.O
            else -> Tag.T
        }
    }

    private fun tagColored(): String {
        val color = when (tag()) {
            Tag.I -> Color.Green
            Tag.O -> Color.Red
            Tag.T -> Color.Yellow
        }
        return " ${color.code} "
    }

    fun isValid(): Boolean = description.value!!.isNotEmpty()

    fun edit() {
        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            when (readln()) {
                "priority" -> {priority.inputValue(); break }
                "date" -> { date.inputValue(); break }
                "time" -> { time.inputValue(); break }
                "task" -> { description.inputValue(); break }
                else -> println("Invalid field")
            }
        }
        println("The task is changed")
    }

    fun getPrintableData(index: Int, descriptionWidth: Int): MutableList<List<String>> {
        val printable = listOf("${index + 1}", date, time, priority.getColor(), tagColored())
            .map { s -> listOf(s.toString()) }.toMutableList()
        val descriptions = splitAndPadDescription(description.value ?: emptyList<String>(),
            descriptionWidth)
        printable.add(descriptions)
        return printable
    }
}

fun inputTask(): Task {
    val task = Task()
    task.priority.inputValue()
    task.date.inputValue()
    task.time.inputValue()
    task.description.inputValue()
    return task
}

fun inputValidIndex(n: Int): Int {
    while (true) {
        println("Input the task number (1-$n):")
        val index = readln()
        val i = index.toIntOrNull()
        if (i != null && i in 1..n) return i - 1
        else println("Invalid task number")
    }
}

fun main() {
    val table = Table(listOf(
        TableColumn("N", 4),
        TableColumn("Date", 12),
        TableColumn("Time", 7),
        TableColumn("Priority", 3, "P"),
        TableColumn("Tag", 3, "D"),
        TableColumn("Task", 44, "Task "),
    ))

    val jsonFile = File(FILE_NAME)
    var tasks = mutableListOf<Task>()
    if (jsonFile.exists()) {
        try {
            tasks = taskListAdapter.fromJson(jsonFile.readText())!!.toMutableList()
        } catch (e: Exception) {
            System.err.println("Error reading json file: $FILE_NAME, ${e.message}")
            return
        }
    }
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln()) {
            "add" -> {
                val task = inputTask()
                if (task.isValid()) tasks.add(task)
            }
            "print" -> table.printTasks(tasks)
            "edit" -> {
                table.printTasks(tasks)
                if (tasks.isNotEmpty()) tasks[inputValidIndex(tasks.size)].edit()
            }
            "delete" -> {
                table.printTasks(tasks)
                if (tasks.isNotEmpty()) {
                    tasks.removeAt(inputValidIndex(tasks.size))
                    println("The task is deleted")
                }
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
