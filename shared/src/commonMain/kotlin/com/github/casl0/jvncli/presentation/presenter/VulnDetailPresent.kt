package com.github.casl0.jvncli.presentation.presenter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.model.VulnDetail
import com.github.casl0.jvncli.presentation.Presenter
import com.github.casl0.jvncli.presentation.event.VulnDetailEvent
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.presentation.state.VulnDetailUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** 脆弱性詳細ページのプレゼンター型。 */
typealias VulnDetailPresenter = Presenter<VulnDetailUiState, VulnDetailEvent>

/** [vulnId] の脆弱性詳細を取得して状態を算出する Molecule 本体。 */
@Composable
internal fun vulnDetailPresent(
    vulnId: String,
    events: Flow<VulnDetailEvent>,
    dataSource: JvnDataSource,
): VulnDetailUiState {
    var phase by remember { mutableStateOf(LoadPhase.Loading) }
    var detail by remember { mutableStateOf<VulnDetail?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun load() {
        phase = LoadPhase.Loading
        error = null
        when (val result = dataSource.getVulnDetailInfo(vulnId)) {
            is JvnResult.Success -> {
                detail = result.data.items.firstOrNull()
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

    LaunchedEffect(vulnId) { load() }
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                VulnDetailEvent.Reload -> launch { load() }
            }
        }
    }

    return VulnDetailUiState(phase = phase, detail = detail, error = error)
}

/** [VulnDetailPresenter] の Molecule 実装。表示対象の [vulnId] を保持する。 */
internal class MoleculeVulnDetailPresenter(
    private val vulnId: String,
    private val dataSource: JvnDataSource,
    scope: CoroutineScope,
) : VulnDetailPresenter {
    private val events = Channel<VulnDetailEvent>(Channel.BUFFERED)

    override fun onEvent(event: VulnDetailEvent) {
        events.trySend(event)
    }

    override val uiState: StateFlow<VulnDetailUiState> =
        scope.launchMolecule(mode = RecompositionMode.Immediate) {
            vulnDetailPresent(vulnId, events.receiveAsFlow(), dataSource)
        }
}
