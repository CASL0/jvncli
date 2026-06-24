package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_ALERT_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.MINIMAL_ALERT_XML
import com.github.casl0.jvncli.core.SUCCESS_ALERT_XML
import com.github.casl0.jvncli.core.network.JvnClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class JvnDataSourceImplTest {
    private fun dataSource(handler: MockRequestHandler): JvnDataSource =
        JvnDataSourceImpl(JvnClient.createApi(MockEngine(handler)))

    private fun respondXml(content: String): MockRequestHandler = {
        respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "text/xml"),
        )
    }

    @Test
    fun getAlertList_正常レスポンスをパースする() = runTest {
        val result = dataSource(respondXml(SUCCESS_ALERT_XML)).getAlertList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val list = result.data
        assertEquals(59, list.totalResults)
        assertEquals(2, list.returnedResults)
        assertEquals(1, list.firstResult)
        assertEquals(2, list.alerts.size)

        val first = list.alerts.first()
        assertEquals("MYJVN-ALT-2025-0096", first.id)
        assertEquals("注意", first.severityLabel)
        assertEquals("Low", first.severityTerm)
        assertEquals(1, first.references.size)
        assertEquals(
            "https://www.ipa.go.jp/security/security-alert/2025/alert20251106.html",
            first.references.first().url,
        )

        val joomla = list.alerts[1].references.first()
        assertEquals("cpe:/a:joomla:joomla%21", joomla.cpe)
    }

    @Test
    fun getAlertList_期待するクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_ALERT_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler)
            .getAlertList(startItem = 5, maxCountItem = 10, cpeName = "cpe:/a:foo:bar")

        val params = captured!!.url.parameters
        assertEquals("getAlertList", params["method"])
        assertEquals("hnd", params["feed"])
        assertEquals("xml", params["ft"])
        assertEquals("5", params["startItem"])
        assertEquals("10", params["maxCountItem"])
        assertEquals("cpe:/a:foo:bar", params["cpeName"])
        // 未指定の任意パラメータはクエリに含まれない。
        assertNull(params["datePublished"])
    }

    @Test
    fun getAlertList_任意要素が欠けてもパースできる() = runTest {
        val result = dataSource(respondXml(MINIMAL_ALERT_XML)).getAlertList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val alert = result.data.alerts.single()
        assertEquals("最小エントリ", alert.title)
        assertNull(alert.published)
        assertNull(alert.severityLabel)

        val ref = alert.references.single()
        assertNull(ref.title)
        assertNull(ref.identifier)
        assertNull(ref.url)
        assertEquals("2099-01-01T00:00:00+09:00", ref.updated)
    }

    @Test
    fun getAlertList_retCdが0以外ならApiErrorを返す() = runTest {
        val result = dataSource(respondXml(ERROR_ALERT_XML)).getAlertList(datePublished = 9999)

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("AL0703019", result.errCd)
        assertEquals("datePublishedは半角数字のみ指定可能です（1～9999）。", result.errMsg)
    }

    @Test
    fun getAlertList_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getAlertList()

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
