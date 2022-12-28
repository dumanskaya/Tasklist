package tasklist

class Task {
    var lines = mutableListOf<String>()

}
fun createTask(): Task? {
    val task = Task()
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
            for (j in tasks[i].lines.indices) {
                val sep = if (j == 0) (i + 1).toString().padEnd(3) else "   "
                println("$sep${tasks[i].lines[j]}")
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
