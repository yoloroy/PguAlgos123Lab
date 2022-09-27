import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlin.time.Duration

// region array init
const val arraySize = 1_000_000
val keyRange = 1..arraySize
val list = List(arraySize) { keyRange.random() }
// endregion
// region labels
val timeLabel = StringTemplate1<Duration?> { "Время: ${it?.toString()?.replace("us", "µs") ?: "Не замеряно"}" }
val indexLabel = StringTemplate1<Int?> { "Индекс: ${it ?: "Не найден"}" }
const val unsortedArrayLabel = "Неупорядоченный массив"
const val unsortedArrayNonOptimalAlgorithmLabel = "Неоптимальный поиск"
const val unsortedArrayOptimalAlgorithmLabel = "Оптимальный поиск"
const val sortedArrayNonOptimalAlgorithmLabel = "Как в неупорядоченном"
const val sortedArrayOptimalAlgorithmLabel = "Как в упорядоченном"
const val keyLabel = "Ключ"
const val badKeyLabel = "*Ключ должен быть числом"
const val startSearchLabel = "Найти"
const val sortedArrayLabel = "Упорядоченный массив"
// endregion

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Лабораторная работа #(1,2,3)\nВыполнил: Ларюшкин Сергей 21ВП-1",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center
                )
            }
            Divider(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SearchColumn(
                    list = list,
                    headerLabel = unsortedArrayLabel,
                    nonOptimalAlgorithmLabel = unsortedArrayNonOptimalAlgorithmLabel,
                    optimalAlgorithmLabel = unsortedArrayOptimalAlgorithmLabel,
                    keyLabel = keyLabel,
                    badKeyLabel = badKeyLabel,
                    startSearchLabel = startSearchLabel,
                    timeLabel = timeLabel,
                    indexLabel = indexLabel,
                    searchStarter = SearchStarter(
                        IntLinearOptimizedSearchAlgorithm(),
                        IntLinearUnoptimizedSearchAlgorithm()
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
                Divider(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                        .width(1.dp)
                )
                SearchColumn(
                    list = remember(list) { list.sorted() },
                    headerLabel = sortedArrayLabel,
                    nonOptimalAlgorithmLabel = sortedArrayNonOptimalAlgorithmLabel,
                    optimalAlgorithmLabel = sortedArrayOptimalAlgorithmLabel,
                    keyLabel = keyLabel,
                    badKeyLabel = badKeyLabel,
                    startSearchLabel = startSearchLabel,
                    timeLabel = timeLabel,
                    indexLabel = indexLabel,
                    searchStarter = SearchStarter(
                        IntLinearOptimizedSearchForSortedCollection(),
                        IntLinearOptimizedSearchAlgorithm()
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
            Divider(modifier = Modifier.padding(bottom = 8.dp))
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { exitProcess(0) }) {
                    Text("Выйти")
                }
            }
        }
    }
}

@Composable
fun SearchColumn(
    list: List<Int>,
    modifier: Modifier = Modifier,
    headerLabel: String,
    nonOptimalAlgorithmLabel: String,
    optimalAlgorithmLabel: String,
    keyLabel: String,
    badKeyLabel: String,
    startSearchLabel: String,
    timeLabel: StringTemplate1<Duration?> = StringTemplate1 { "Time: $it" },
    indexLabel: StringTemplate1<Int?> = StringTemplate1 { "Index: $it" },
    searchStarter: SearchStarter
) {
    // region fields
    var key by remember { mutableStateOf(0) }
    val setKey = remember { fun(value: Int) { key = value } }
    var nonOptimalAlgorithmMeasuredTime by remember { mutableStateOf<Duration?>(null) }
    var nonOptimalAlgorithmResultIndex by remember { mutableStateOf<Int?>(null) }
    var optimalAlgorithmMeasuredTime by remember { mutableStateOf<Duration?>(null) }
    var optimalAlgorithmResultIndex by remember { mutableStateOf<Int?>(null) }
    // endregion
    // region search
    var searchAttempt by remember { mutableStateOf(0) }
    val startSearch = remember { fun() { searchAttempt++ } }
    var isSearchProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(searchAttempt) {
        if (searchAttempt == 0) return@LaunchedEffect
        isSearchProcessing = true
        searchStarter.start(key, list) {
                _nonOptimalAlgorithmMeasuredTime,
                _nonOptimalAlgorithmResultIndex,
                _optimalAlgorithmMeasuredTime,
                _optimalAlgorithmResultIndex ->
            nonOptimalAlgorithmMeasuredTime = _nonOptimalAlgorithmMeasuredTime
            nonOptimalAlgorithmResultIndex = _nonOptimalAlgorithmResultIndex
            optimalAlgorithmMeasuredTime = _optimalAlgorithmMeasuredTime
            optimalAlgorithmResultIndex = _optimalAlgorithmResultIndex
            isSearchProcessing = false
        }
    }
    // endregion
    // region block
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SearchColumnHeader(headerLabel)
        Divider(modifier = Modifier.padding(8.dp))
        AlgorithmResultBlock(
            label = nonOptimalAlgorithmLabel,
            measuredTime = nonOptimalAlgorithmMeasuredTime,
            resultIndex = nonOptimalAlgorithmResultIndex,
            timeLabel = timeLabel,
            indexLabel = indexLabel
        )
        Spacer(modifier = Modifier.height(24.dp))
        AlgorithmResultBlock(
            label = optimalAlgorithmLabel,
            measuredTime = optimalAlgorithmMeasuredTime,
            resultIndex = optimalAlgorithmResultIndex,
            timeLabel = timeLabel,
            indexLabel = indexLabel
        )
        StartSearchBlock(
            key = key,
            setKey = setKey,
            badKeyLabel = badKeyLabel,
            keyLabel = keyLabel,
            startSearch = startSearch,
            startSearchLabel = startSearchLabel,
            isSearchProcessing = isSearchProcessing
        )
    }
    // endregion
}

@Composable
fun StartSearchBlock(
    key: Int,
    setKey: (Int) -> Unit,
    badKeyLabel: String,
    keyLabel: String,
    startSearch: () -> Unit,
    startSearchLabel: String,
    isSearchProcessing: Boolean
) {
    var isKeyError by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = key.toString(),
            onValueChange = {
                isKeyError = try {
                    val newKey = when {
                        it.isEmpty() -> 0
                        else -> it.toInt()
                    }
                    setKey(newKey)
                    false
                } catch (exception: NumberFormatException) {
                    true
                }
            },
            isError = isKeyError,
            label = { Text(text = if (isKeyError) badKeyLabel else keyLabel) },
            trailingIcon = {
                if (isSearchProcessing) {
                    IconButton(onClick = {}) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                } else {
                    TextButton(
                        onClick = startSearch,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(startSearchLabel)
                    }
                }
            },
            modifier = Modifier.scale(0.85f).padding(vertical = 8.dp)
        )
    }
}

@Composable
fun SearchColumnHeader(headerLabel: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = headerLabel, style = MaterialTheme.typography.h6)
    }
}

@Composable
fun AlgorithmResultBlock(
    label: String,
    measuredTime: Duration?,
    resultIndex: Int?,
    timeLabel: StringTemplate1<Duration?>,
    indexLabel: StringTemplate1<Int?>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(8.dp)
        )
        Text(text = timeLabel.format(measuredTime))
        Text(text = indexLabel.format(resultIndex))
    }
}

class SearchStarter(
    private val optimalAlgorithm: SearchAlgorithm<Int>,
    private val nonOptimalAlgorithm: SearchAlgorithm<Int>
) {
    fun start(
        key: Int,
        list: List<Int>,
        onFinish: (
            nonOptimalAlgorithmMeasuredTime: Duration,
            nonOptimalAlgorithmResultIndex: Int?,
            optimalAlgorithmMeasuredTime: Duration,
            optimalAlgorithmResultIndex: Int?
        ) -> Unit
    ) {
        thread {
            runBlocking {
                val optimalResult = optimalAlgorithm.find(list, key)
                val nonOptimalResult = nonOptimalAlgorithm.find(list, key)
                onFinish(
                    nonOptimalResult.searchTime,
                    nonOptimalResult.data?.resultIndex,
                    optimalResult.searchTime,
                    optimalResult.data?.resultIndex
                )
            }
        }
    }
}

fun interface StringTemplate1<T> {
    fun format(arg: T): String
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
