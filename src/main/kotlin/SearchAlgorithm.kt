import kotlin.time.Duration

abstract class SearchAlgorithm<T : Comparable<T>> {

    abstract suspend fun find(
        list: List<T>,
        itemToSearch: T
    ): Result<T>

    data class Result<T : Comparable<T>>(
        val data: ResultWithoutTime<T>?,
        val searchTime: Duration
    )

    data class ResultWithoutTime<T : Comparable<T>>(
        val input: List<T>,
        val itemToSearch: T,
        val actualIndex: Int
    ) {
        val resultIndex: Int get() = actualIndex + 1
    }
}
