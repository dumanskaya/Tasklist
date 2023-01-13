package tasklist

fun centerString(obj: Any, width: Int): String {
    val value = obj.toString()
    val n = width - value.length
    return value.padStart(n / 2 + value.length).padEnd(width)
}


fun splitAndPadDescription(lines: List<String>, width: Int): List<String> = lines
    .map {it.chunked(width)}
    .flatten()
    .map { it.padEnd(width) }

class TableColumn(val name: String, val width: Int, val header: String = name)

class Table(
    private val columns: List<TableColumn>, private val borderKnot: String = "+",
    private val horizontalBorder: String = "-", private val verticalBorder: String = "|") {

    private fun getWidths(): List<Int> = columns.map { it.width }

    private fun getHorizontalLine(): String = addVerticalBorders(getWidths()
        .map {horizontalBorder.repeat(it)}, borderKnot)

    private fun addVerticalBorders(myList: List<String>, border: String): String = myList
        .joinToString(separator = border, prefix = border, postfix = border)

    private fun getHeader(): String {
        val header = columns.map { it.header }
        val widths = getWidths()
        return addVerticalBorders(header.mapIndexed { i, s -> centerString(s, widths[i]) }, verticalBorder)
    }

    private fun printHeader(){
        val horizontalLine = getHorizontalLine()
        println("$horizontalLine\n${getHeader()}\n$horizontalLine")
    }

    private fun printTask(task: Task, index: Int) {
        val widths = getWidths()
        val taskWidth = columns.first { it.name ==  "Task"}.width
        val printable = task.getPrintableData(index, taskWidth)
        val nRows = printable.maxOfOrNull { it.size } ?: 0
        for (i in 0 until nRows) {
            val row = emptyList<String>().toMutableList()
            for (j in widths.indices) {
                val m = printable[j].size
                val str = if (i < m) printable[j][i] else ""
                row.add(centerString(str, widths[j]))
            }
            println(addVerticalBorders(row, verticalBorder))
        }
    }

    fun printTasks(tasks: MutableList<Task>) {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
        } else {
            val horizontalLine = getHorizontalLine()
            printHeader()
            for ((i, task) in tasks.withIndex()) {
                printTask(task, i)
                println(horizontalLine)
            }
        }
    }
}
