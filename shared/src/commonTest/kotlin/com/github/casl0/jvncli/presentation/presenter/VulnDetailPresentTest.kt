package com.github.casl0.jvncli.presentation.presenter

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.github.casl0.jvncli.core.ERROR_VULN_DETAIL_XML
import com.github.casl0.jvncli.core.SUCCESS_VULN_DETAIL_XML
import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.datasource.JvnDataSourceImpl
import com.github.casl0.jvncli.core.network.JvnClient
import com.github.casl0.jvncli.presentation.event.VulnDetailEvent
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.presentation.state.VulnDetailUiState
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

class VulnDetailPresentTest {
    private fun dataSource(xml: String): JvnDataSource =
        JvnDataSourceImpl(
            JvnClient.createApi(
                MockEngine {
                    respond(xml, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/xml"))
                }
            )
        )

    private suspend fun ReceiveTurbine<VulnDetailUiState>.awaitSettled(): VulnDetailUiState {
        var item = awaitItem()
        while (item.phase == LoadPhase.Loading) item = awaitItem()
        return item
    }

    @Test
    fun 指定IDの詳細を取得して表示する() = runTest {
        val events = Channel<VulnDetailEvent>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
                vulnDetailPresent(
                    "JVNDB-2026-000001",
                    events.receiveAsFlow(),
                    dataSource(SUCCESS_VULN_DETAIL_XML),
                )
            }
            .test {
                val loaded = awaitSettled()
                assertEquals(LoadPhase.Loaded, loaded.phase)
                assertEquals("JVNDB-2026-000001", loaded.detail?.id)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun APIエラーはError状態になる() = runTest {
        val events = Channel<VulnDetailEvent>()
        moleculeFlow(mode = RecompositionMode.Immediate) {
                vulnDetailPresent(
                    "INVALID",
                    events.receiveAsFlow(),
                    dataSource(ERROR_VULN_DETAIL_XML),
                )
            }
            .test {
                assertEquals(LoadPhase.Error, awaitSettled().phase)
                cancelAndIgnoreRemainingEvents()
            }
    }
}
