import kotlin.time.Duration
import kotlin.time.TimedValue

abstract class SearchAlgorithm<T : Comparable<T>> {

    abstract suspend fun find(
        list: List<T>,
        itemToSearch: T,
        runsCount: Int,
        runsPerLog: Int,
        log: (runI: Int, runsCount: Int) -> Unit
    ): Result<T>

    suspend fun find(list: List<T>, itemToSearch: T, runsCount: Int): Result<T> =
        find(list, itemToSearch, runsCount, runsCount) { _, _ -> }

    data class Result<T : Comparable<T>>(
        val data: ResultWithoutTime<T>?,
        val searchTime: Duration
    ) {
        val isSuccess get() = data != null
    }

    data class ResultWithoutTime<T : Comparable<T>>(
        val input: List<T>,
        val itemToSearch: T,
        val actualIndex: Int
    ) {
        val resultIndex: Int get() = actualIndex + 1
    }
}
