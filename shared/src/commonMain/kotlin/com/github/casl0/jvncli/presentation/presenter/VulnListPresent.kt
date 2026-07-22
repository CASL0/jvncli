package com.github.casl0.jvncli.presentation.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.model.VulnOverview
import com.github.casl0.jvncli.presentation.Presenter
import com.github.casl0.jvncli.presentation.event.VulnListEvent
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.presentation.state.VulnListUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** 脆弱性情報ページのプレゼンター型。 */
typealias VulnListPresenter = Presenter<VulnListUiState, VulnListEvent>

/** 脆弱性概要一覧の状態を算出する Molecule 本体。 */
@Composable
internal fun vulnListPresent(
    events: Flow<VulnListEvent>,
    dataSource: JvnDataSource,
): VulnListUiState {
    var phase by remember { mutableStateOf(LoadPhase.Loading) }
    var items by remember { mutableStateOf<List<VulnOverview>>(emptyList()) }
    var cursor by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun load() {
        phase = LoadPhase.Loading
        error = null
        when (val result = dataSource.getVulnOverviewList(maxCountItem = 50)) {
            is JvnResult.Success -> {
                items = result.data.items
                cursor = 0
                phase = LoadPhase.Loaded
            }
            is JvnResult.ApiError -> {
                error = "APIエラー (retCd=${result.retCd}): ${result.errMsg}"
                phase = LoadPhase.Error
            }
            is JvnResult.NetworkError -> {
                error = "通信エラー: ${result.cause.message}"
                phase = LoadPhase.Error
            }
        }
    }

    LaunchedEffect(Unit) { load() }
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                VulnListEvent.Reload -> launch { load() }
                VulnListEvent.MoveUp -> cursor = (cursor - 1).coerceAtLeast(0)
                VulnListEvent.MoveDown ->
                    cursor = (cursor + 1).coerceAtMost((items.size - 1).coerceAtLeast(0))
            }
        }
    }

    return VulnListUiState(phase = phase, items = items, cursor = cursor, error = error)
}

/** [VulnListPresenter] の Molecule 実装。 */
internal class MoleculeVulnListPresenter(
    private val dataSource: JvnDataSource,
    scope: CoroutineScope,
) : VulnListPresenter {
    private val events = Channel<VulnListEvent>(Channel.BUFFERED)

    override fun onEvent(event: VulnListEvent) {
        events.trySend(event)
    }

    override val uiState: StateFlow<VulnListUiState> =
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            vulnListPresent(events.receiveAsFlow(), dataSource)
        }
}
