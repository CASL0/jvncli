package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_STAT_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.SUCCESS_STAT_HND_CVSS_XML
import com.github.casl0.jvncli.core.SUCCESS_STAT_ITM_JVNDB_XML
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class JvnDataSourceImplStatisticsTest {
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
    fun getStatistics_hndのsumCvssを総数と内訳付きでパースする() = runTest {
        val result =
            dataSource(respondXml(SUCCESS_STAT_HND_CVSS_XML)).getStatistics(theme = "sumCvss")

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val stats = result.data
        assertNull(stats.vulnCount)
        val cvss = assertNotNull(stats.cvssBreakdown)

        // resDataTotal
        assertEquals(289246, cvss.total?.vulinfo)
        assertEquals(87811, cvss.total?.product)

        // 期間ごとの CVSS 内訳
        assertEquals(2, cvss.dataPoints.size)
        val y2024 = cvss.dataPoints.first()
        assertEquals("2024", y2024.date)
        assertEquals(29114, y2024.countAll)
        assertEquals(3838, y2024.countCritical)
        assertEquals(10410, y2024.countHigh)
        assertEquals(0, y2024.countNone)
    }

    @Test
    fun getStatistics_itmのsumJvnDbをresDataTotalなしでパースする() = runTest {
        val result =
            dataSource(respondXml(SUCCESS_STAT_ITM_JVNDB_XML))
                .getStatistics(theme = "sumJvnDb", feed = "itm")

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val stats = result.data
        assertNull(stats.cvssBreakdown)
        val count = assertNotNull(stats.vulnCount)

        // itm は resDataTotal を持たない。
        assertNull(count.total)
        assertEquals(2, count.dataPoints.size)
        assertEquals(29228, count.dataPoints.first().countAll)
        // sumJvnDb には CVSS 内訳が無い。
        assertNull(count.dataPoints.first().countCritical)
    }

    @Test
    fun getStatistics_themeとfeedを含むクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_STAT_ITM_JVNDB_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler)
            .getStatistics(
                theme = "sumJvnDb",
                feed = "itm",
                type = "y",
                datePublicStartY = 2024,
                datePublicEndY = 2025,
            )

        val params = assertNotNull(captured).url.parameters
        assertEquals("getStatistics", params["method"])
        assertEquals("itm", params["feed"])
        assertEquals("sumJvnDb", params["theme"])
        assertEquals("y", params["type"])
        assertEquals("2024", params["datePublicStartY"])
        assertEquals("2025", params["datePublicEndY"])
        assertEquals("xml", params["ft"])
        assertNull(params["cweId"])
    }

    @Test
    fun getStatistics_retCdが0以外ならApiErrorを返す() = runTest {
        val result =
            dataSource(respondXml(ERROR_STAT_XML)).getStatistics(theme = "sumJvnDb", type = "m")

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("VS06030970", result.errCd)
    }

    @Test
    fun getStatistics_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getStatistics(theme = "sumJvnDb")

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
