package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

val type = Types.newParameterizedType(List::class.java, Task::class.java)
val taskListAdapter = moshi.adapter<List<Task>>(type)
