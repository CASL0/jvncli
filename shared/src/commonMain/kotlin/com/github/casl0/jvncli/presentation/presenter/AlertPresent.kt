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
import com.github.casl0.jvncli.core.model.Alert
import com.github.casl0.jvncli.presentation.Presenter
import com.github.casl0.jvncli.presentation.event.AlertEvent
import com.github.casl0.jvncli.presentation.state.AlertUiState
import com.github.casl0.jvncli.presentation.state.LoadPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** 警戒情報ページのプレゼンター型。 */
typealias AlertListPresenter = Presenter<AlertUiState, AlertEvent>

/**
 * 警戒情報の状態を算出する Molecule 本体(Molecule 公式 counter サンプルと同形)。
 *
 * テストは `moleculeFlow { alertPresent(events, fake) }` でこの関数を直接回す。
 */
@Composable
internal fun alertPresent(events: Flow<AlertEvent>, dataSource: JvnDataSource): AlertUiState {
    var phase by remember { mutableStateOf(LoadPhase.Loading) }
    var alerts by remember { mutableStateOf<List<Alert>>(emptyList()) }
    var cursor by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun load() {
        phase = LoadPhase.Loading
        error = null
        when (val result = dataSource.getAlertList(maxCountItem = 50)) {
            is JvnResult.Success -> {
                alerts = result.data.alerts
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
                AlertEvent.Reload -> launch { load() }
                AlertEvent.MoveUp -> cursor = (cursor - 1).coerceAtLeast(0)
                AlertEvent.MoveDown ->
                    cursor = (cursor + 1).coerceAtMost((alerts.size - 1).coerceAtLeast(0))
            }
        }
    }

    return AlertUiState(phase = phase, alerts = alerts, cursor = cursor, error = error)
}

/** [AlertListPresenter] の Molecule 実装。Channel + launchMolecule の配線を持つ。 */
internal class MoleculeAlertListPresenter(
    private val dataSource: JvnDataSource,
    scope: CoroutineScope,
) : AlertListPresenter {
    private val events = Channel<AlertEvent>(Channel.BUFFERED)

    override fun onEvent(event: AlertEvent) {
        events.trySend(event)
    }

    override val uiState: StateFlow<AlertUiState> =
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            alertPresent(events.receiveAsFlow(), dataSource)
        }
}
