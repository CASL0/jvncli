package com.github.casl0.jvncli.presentation.presenter

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.github.casl0.jvncli.core.ERROR_ALERT_XML
import com.github.casl0.jvncli.core.SUCCESS_ALERT_XML
import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.datasource.JvnDataSourceImpl
import com.github.casl0.jvncli.core.network.JvnClient
import com.github.casl0.jvncli.presentation.event.AlertEvent
import com.github.casl0.jvncli.presentation.state.AlertUiState
import com.github.casl0.jvncli.presentation.state.LoadPhase
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.runTest

class AlertPresentTest {
    private fun dataSource(xml: String): JvnDataSource =
        JvnDataSourceImpl(
            JvnClient.createApi(
                MockEngine {
                    respond(xml, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/xml"))
                }
            )
        )

    /** Loading をスキップして最初の Loaded/Error を待つ。 */
    private suspend fun ReceiveTurbine<AlertUiState>.awaitSettled(): AlertUiState {
        var item = awaitItem()
        while (item.phase == LoadPhase.Loading) item = awaitItem()
        return item
    }

    @Test
    fun 初回ロードで一覧を表示する() = runTest {
        val events = Channel<AlertEvent>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
                alertPresent(events.receiveAsFlow(), dataSource(SUCCESS_ALERT_XML))
            }
            .test {
                val loaded = awaitSettled()
                assertEquals(LoadPhase.Loaded, loaded.phase)
                assertEquals(2, loaded.alerts.size)
                assertEquals(0, loaded.cursor)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun MoveDownでカーソルが進み末尾で止まる() = runTest {
        val events = Channel<AlertEvent>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
                alertPresent(events.receiveAsFlow(), dataSource(SUCCESS_ALERT_XML))
            }
            .test {
                assertEquals(0, awaitSettled().cursor)
                events.send(AlertEvent.MoveDown)
                assertEquals(1, awaitItem().cursor)
                // 2 件なので index 1 が末尾。さらに MoveDown しても 1 のまま(新たな emission は無い)。
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun APIエラーはError状態になる() = runTest {
        val events = Channel<AlertEvent>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
                alertPresent(events.receiveAsFlow(), dataSource(ERROR_ALERT_XML))
            }
            .test {
                val settled = awaitSettled()
                assertEquals(LoadPhase.Error, settled.phase)
                cancelAndIgnoreRemainingEvents()
            }
    }
}
