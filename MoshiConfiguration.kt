package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.LocalDate
import java.time.LocalTime

class LocalDateAdapter: JsonAdapter<LocalDate>() {
    @FromJson
    override fun fromJson(reader: JsonReader): LocalDate {
        val date = reader.nextString()
        val (year, month, day) = date.split('-').map { it.toInt() }
        return LocalDate(year, month, day)
    }
    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        if (value != null) {
            writer.value(value.toString())
        }
    }
}

class LocalTimeAdapter: JsonAdapter<LocalTime>() {
    @FromJson
    override fun fromJson(reader: JsonReader): LocalTime {
        val time = reader.nextString()
        val (hours, minutes) = time.split(":").map { it.toInt() }
        return LocalTime.of(hours, minutes)
    }
    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalTime?) {
        if (value != null) {
            writer.value(value.toString())
        }
    }
}

val moshi = Moshi.Builder()
    .add(LocalDateAdapter())
    .add(LocalTimeAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

val type = Types.newParameterizedType(List::class.java, Task::class.java)
val taskListAdapter = moshi.adapter<List<Task>>(type)
