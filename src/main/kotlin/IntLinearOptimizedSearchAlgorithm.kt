import kotlin.time.ExperimentalTime

class IntLinearOptimizedSearchAlgorithm : SearchAlgorithm<Int>() {

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalTime::class)
    override suspend fun find(
        list: List<Int>,
        itemToSearch: Int,
        runsCount: Int,
        runsPerLog: Int,
        log: (runI: Int, runsCount: Int) -> Unit
    ): Result<Int> {
        val boundedArray = (list + itemToSearch).toIntArray()
        val timedIndex = measureAverageTime(runsCount) measure@ { i ->
            if (i % runsPerLog == 0) log(i, runsCount)
            return@measure find(boundedArray, itemToSearch)
        }
        return Result(
            timedIndex.value.takeIf { it != -1 }?.let { ResultWithoutTime(list, itemToSearch, it) },
            timedIndex.duration / runsCount
        )
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun find(boundedArray: IntArray, itemToSearch: Int): Int {
        var i = 0
        while (boundedArray[i++] != itemToSearch);
        return if (i != boundedArray.size) i - 1 else -1
    }
}
