import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class IntLinearUnoptimizedSearchAlgorithm : SearchAlgorithm<Int>() {

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalTime::class)
    override suspend fun find(
        list: List<Int>,
        itemToSearch: Int
    ): Result<Int> {
        val array = list.toIntArray()
        val timedIndex = measureTimedValue measure@ {
            var i = 0
            while (i != array.size) {
                if (array[i] == itemToSearch) return@measure i
                i++
            }
            return@measure null
        }
        return Result(
            timedIndex.value?.let { ResultWithoutTime(list, itemToSearch, it) },
            timedIndex.duration
        )
    }
}
