import kotlin.time.ExperimentalTime
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
inline fun <T> measureAverageTime(times: Int, block: (i: Int) -> T) = measureTimedValue measure@ {
    val result = block(0)
    for (i in 1..times) { block(i + 1) }
    return@measure result
}.let { (value, duration) ->
    TimedValue(value, duration / times)
}
