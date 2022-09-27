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

const val ARRAY_SIZE = 1_000_000
const val SEARCH_REPEATING_COUNT = 1
const val RUNS_PER_LOG = 1
val KEY_RANGE = 1..ARRAY_SIZE
val list = List(ARRAY_SIZE) { KEY_RANGE.random() }

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Box(
                modifier = Modifier.height(72.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Лабораторная работа #(1,2,3)\nВыполнил: Ларюшкин Сергей 21ВП-1",
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center
                )
            }
            Divider(modifier = Modifier.padding(top = 8.dp))
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                SearchColumn(
                    list = list,
                    optimalAlgorithm = IntLinearOptimizedSearchAlgorithm(),
                    nonOptimalAlgorithm = IntLinearUnoptimizedSearchAlgorithm(),
                    headerLabel = "Неупорядоченный массив",
                    nonOptimalAlgorithmLabel = "Неоптимальный поиск",
                    optimalAlgorithmLabel = "Оптимальный поиск",
                    keyLabel = "Ключ",
                    badKeyLabel = "*Ключ должен быть числом",
                    startSearchLabel = "Найти",
                    timeLabel = { "Время: ${it.toString().replace("us", "µs")}" },
                    indexLabel = { "Индекс: $it" },
                    modifier = Modifier.fillMaxHeight().weight(1f)
                )
                Divider(modifier = Modifier.padding(8.dp).fillMaxHeight().width(1.dp))
                SearchColumn(
                    list = remember(list) { list.sorted() },
                    optimalAlgorithm = LinearOptimizedSearchForSortedCollection(),
                    nonOptimalAlgorithm = IntLinearOptimizedSearchAlgorithm(),
                    headerLabel = "Упорядоченный массив",
                    nonOptimalAlgorithmLabel = "Как в неупорядоченном",
                    optimalAlgorithmLabel = "Как в упорядоченном",
                    keyLabel = "Ключ",
                    badKeyLabel = "*Ключ должен быть числом",
                    startSearchLabel = "Найти",
                    timeLabel = { "Время: ${it.toString().replace("us", "µs")}" },
                    indexLabel = { "Индекс: $it" },
                    modifier = Modifier.fillMaxHeight().weight(1f)
                )
            }
            Divider(modifier = Modifier.padding(bottom = 8.dp))
            Box(
                modifier = Modifier.height(48.dp).fillMaxWidth(),
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
    optimalAlgorithm: SearchAlgorithm<Int>,
    nonOptimalAlgorithm: SearchAlgorithm<Int>,
    modifier: Modifier = Modifier,
    headerLabel: String,
    nonOptimalAlgorithmLabel: String,
    optimalAlgorithmLabel: String,
    keyLabel: String,
    badKeyLabel: String,
    startSearchLabel: String,
    timeLabel: StringTemplate1<Duration?> = StringTemplate1 { "Time: $it" },
    indexLabel: StringTemplate1<Int?> = StringTemplate1 { "Index: $it" }
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
    var searchProgress by remember { mutableStateOf(0.0) }
    val optimalSearchLogger = remember {
        fun(runI: Int, runsCount: Int) {
            searchProgress = 0.5 * runI / runsCount
        }
    }
    val nonOptimalSearchLogger = remember {
        fun(runI: Int, runsCount: Int) {
            searchProgress = 0.5 + 0.5 * runI.toDouble() / runsCount
        }
    }

    LaunchedEffect(searchAttempt) {
        if (searchAttempt == 0) return@LaunchedEffect
        isSearchProcessing = true
        thread { runBlocking {
            val optimalResult = optimalAlgorithm.find(list, key, SEARCH_REPEATING_COUNT, RUNS_PER_LOG, optimalSearchLogger)
            val nonOptimalResult = nonOptimalAlgorithm.find(list, key, SEARCH_REPEATING_COUNT, RUNS_PER_LOG, nonOptimalSearchLogger)
            optimalAlgorithmMeasuredTime = optimalResult.searchTime
            optimalAlgorithmResultIndex = (if (optimalResult.isSuccess) optimalResult.data!!.resultIndex else -1)
            nonOptimalAlgorithmMeasuredTime = nonOptimalResult.searchTime
            nonOptimalAlgorithmResultIndex = (if (nonOptimalResult.isSuccess) nonOptimalResult.data!!.resultIndex else -1)
            searchProgress = 0.0
            isSearchProcessing = false
        } }
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
            isSearchProcessing = isSearchProcessing,
            searchProgress = searchProgress
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
    isSearchProcessing: Boolean,
    searchProgress: Double
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
                            searchProgress.toFloat(),
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

fun interface StringTemplate1<T> {
    fun format(arg: T): String
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
