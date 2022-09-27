import kotlin.time.ExperimentalTime

class IntLinearUnoptimizedSearchAlgorithm : SearchAlgorithm<Int>() {

    @Suppress("OPT_IN_IS_NOT_ENABLED")
    @OptIn(ExperimentalTime::class)
    override suspend fun find(
        list: List<Int>,
        itemToSearch: Int,
        runsCount: Int,
        runsPerLog: Int,
        log: (runI: Int, runsCount: Int) -> Unit
    ): Result<Int> {
        val array = list.toIntArray()
        val timedIndex = measureAverageTime(runsCount) measure@ { i ->
            if (i % runsPerLog == 0) log(i, runsCount)
            return@measure find(array, itemToSearch)
        }
        return Result(
            timedIndex.value.takeIf { it != -1 }?.let { ResultWithoutTime(list, itemToSearch, it) },
            timedIndex.duration / runsCount
        )
    }

    @Suppress("RedundantSuspendModifier")
    private suspend inline fun find(array: IntArray, itemToSearch: Int): Int {
        var i = 0
        while (i != array.size) {
            if (array[i] == itemToSearch) {
                return i
            } else {
                i++
            }
        }
        return -1
    }
}
