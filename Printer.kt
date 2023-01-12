package tasklist

const val BORDER_KNOT = "+"
const val HORIZONTAL_BORDER = "-"
const val VERTICAL_BORDER = "|"
const val NUMBER_WIDTH = 4
const val DATE_WIDTH = 12
const val TIME_WIDTH = 7
const val PRIORITY_WIDTH = 3
const val TAG_WIDTH = 3
const val TASK_WIDTH = 44

val widths = listOf(NUMBER_WIDTH, DATE_WIDTH, TIME_WIDTH, PRIORITY_WIDTH, TAG_WIDTH, TASK_WIDTH)
val header = listOf("N", "Date", "Time", "P", "D", "Task ")

class TableColumn(val header: String, val width: Int, val isCentered: Boolean = true,
                    val isChunked: Boolean = false){
}

fun getHorizontalLine(symbol: String, border: String): String  = addVerticalBorders(widths
    .map {symbol.repeat(it)}, border)

fun centerString(str: Any, width: Int): String {
    val value = str.toString()
    val n = width - value.length
    return value.padStart(n / 2 + value.length).padEnd(width)
}

fun addVerticalBorders(myList: List<Any>, border: String = VERTICAL_BORDER): String = myList
    .joinToString(separator = border, prefix = border, postfix = border)

fun getHeader(): String = addVerticalBorders(header.mapIndexed { i, s -> centerString(s, widths[i]) })

fun emptyRowWithBorders(): String = addVerticalBorders(List(5) { "" }.
mapIndexed { i, s ->  centerString(s, widths[i]) })

fun splitAndPadDescription(lines: List<String>, width: Int): List<String> = lines
    .map {it.chunked(width)}
    .flatten()
    .map { it.padEnd(width) }
