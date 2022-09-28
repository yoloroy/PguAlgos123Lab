import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class IntLinearOptimizedSearchForSortedCollection : SearchAlgorithm<Int>() {

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalTime::class)
    override suspend fun find(
        list: List<Int>,
        itemToSearch: Int
    ): Result<Int> {
        require(list.isSorted())
        val boundedArray = (list + (itemToSearch + 1)).toIntArray()
        val timedIndex = measureTimedValue measure@ {
            var i = 0
            while (boundedArray[i] < itemToSearch) i++
            return@measure i.takeIf { boundedArray[i] == itemToSearch }
        }
        return Result(
            timedIndex.value?.let { ResultWithoutTime(list, itemToSearch, it) },
            timedIndex.duration
        )
    }
}
