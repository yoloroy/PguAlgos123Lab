import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class LinearOptimizedSearchForSortedCollection<T : Comparable<T>> : SearchAlgorithm<T>() {

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalTime::class)
    override suspend fun find(
        list: List<T>,
        itemToSearch: T,
        runsCount: Int,
        runsPerLog: Int,
        log: (runI: Int, runsCount: Int) -> Unit
    ): Result<T> {
        val timedIndex = measureTimedValue measure@ {
            var i = -1
            while (list[++i] < itemToSearch);
            if (list[i] == itemToSearch) return@measure i
            return@measure null
        }
        return Result(
            timedIndex.value?.let { ResultWithoutTime(list, itemToSearch, it) },
            timedIndex.duration
        )
    }
}
