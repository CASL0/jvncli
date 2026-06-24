package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_VULN_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.SUCCESS_VULN_XML
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

class JvnDataSourceImplVulnOverviewTest {
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
    fun getVulnOverviewList_references_cpe_cvssを含めてパースする() = runTest {
        val result = dataSource(respondXml(SUCCESS_VULN_XML)).getVulnOverviewList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val list = result.data
        assertEquals(568, list.totalResults)
        assertEquals(2, list.returnedResults)
        assertEquals(1, list.firstResult)
        assertEquals(2, list.items.size)

        val vuln = list.items.first()
        assertEquals("JVNDB-2026-000001", vuln.id)
        assertEquals("サンプル製品における SQL インジェクションの脆弱性", vuln.title)

        // references (本文URL + 属性) のパース。
        assertEquals(2, vuln.references.size)
        val cve = vuln.references.first()
        assertEquals("CVE", cve.source)
        assertEquals("CVE-2026-00001", cve.id)
        assertEquals("https://www.cve.org/CVERecord?id=CVE-2026-00001", cve.url)

        // cpe (本文CPE + 属性) のパース。
        val product = vuln.affectedProducts.single()
        assertEquals("サンプルベンダ", product.vendor)
        assertEquals("cpe:/a:sample:sample_product", product.cpe)

        // cvss のパース (score は Double へ変換)。
        val cvss = vuln.cvssScores.single()
        assertEquals(9.8, cvss.score)
        assertEquals("Critical", cvss.severity)
        assertEquals("CVSS:3.0/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H", cvss.vector)
    }

    @Test
    fun getVulnOverviewList_任意要素が欠けた最小itemもパースできる() = runTest {
        val result = dataSource(respondXml(SUCCESS_VULN_XML)).getVulnOverviewList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val minimal = result.data.items[1]
        assertEquals("JVNDB-2026-000002", minimal.id)
        assertNull(minimal.description)
        assertTrue(minimal.references.isEmpty())
        assertTrue(minimal.affectedProducts.isEmpty())
        assertTrue(minimal.cvssScores.isEmpty())
        assertNull(minimal.date)
    }

    @Test
    fun getVulnOverviewList_期待するクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_VULN_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler)
            .getVulnOverviewList(
                maxCountItem = 20,
                severity = "c",
                rangeDatePublic = "m",
                keyword = "sql",
            )

        val params = assertNotNull(captured).url.parameters
        assertEquals("getVulnOverviewList", params["method"])
        assertEquals("hnd", params["feed"])
        assertEquals("xml", params["ft"])
        assertEquals("20", params["maxCountItem"])
        assertEquals("c", params["severity"])
        assertEquals("m", params["rangeDatePublic"])
        assertEquals("sql", params["keyword"])
        // 未指定の任意パラメータはクエリに含まれない。
        assertNull(params["vendorId"])
        assertNull(params["vector"])
    }

    @Test
    fun getVulnOverviewList_retCdが0以外ならApiErrorを返す() = runTest {
        val result = dataSource(respondXml(ERROR_VULN_XML)).getVulnOverviewList(startItem = -1)

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("VL01020003", result.errCd)
    }

    @Test
    fun getVulnOverviewList_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getVulnOverviewList()

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
