package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_VULN_DETAIL_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.SUCCESS_VULN_DETAIL_XML
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
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class JvnDataSourceImplVulnDetailTest {
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
    fun getVulnDetailInfo_VULDEF詳細を全項目パースする() = runTest {
        val result =
            dataSource(respondXml(SUCCESS_VULN_DETAIL_XML)).getVulnDetailInfo("JVNDB-2026-000001")

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val list = result.data
        assertEquals(1, list.totalResults)
        val vuln = list.items.single()

        assertEquals("JVNDB-2026-000001", vuln.id)
        assertEquals("サンプル製品における送信元の確認が不十分な脆弱性", vuln.title)
        assertEquals("サンプル製品には、送信元の確認が不十分（CWE-346）の脆弱性が存在します。", vuln.overview)

        // Affected
        val affected = vuln.affected.single()
        assertEquals("サンプルベンダ", affected.vendor)
        assertEquals("サンプル製品", affected.productName)
        assertEquals(listOf("cpe:/a:sample:sample_product"), affected.cpes)
        assertEquals(listOf("バージョン2.0.25.0およびそれ以前"), affected.versions)

        // CVSS (Severity/Base/Vector のネストを CvssScore へ)
        val cvss = vuln.cvssScores.single()
        assertEquals("3.0", cvss.version)
        assertEquals("Base", cvss.type)
        assertEquals("High", cvss.severity)
        assertEquals(7.8, cvss.score)
        assertEquals("CVSS:3.0/AV:L/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H", cvss.vector)

        // Impact / Solution
        assertEquals(listOf("任意のコマンドを実行される可能性があります。"), vuln.impacts)
        assertEquals(listOf("[アップデートする] 最新版へアップデートしてください。"), vuln.solutions)

        // Related
        assertEquals(2, vuln.related.size)
        val cve = vuln.related.first()
        assertEquals("advisory", cve.type)
        assertEquals("CVE-2026-20893", cve.vulinfoId)
        assertEquals("https://www.cve.org/CVERecord?id=CVE-2026-20893", cve.url)

        // History
        val history = vuln.history.single()
        assertEquals("1", history.historyNo)
        assertEquals(listOf("[2026年01月07日]　掲載"), history.descriptions)

        assertEquals("2026-01-07T00:00:00+09:00", vuln.datePublic)
    }

    @Test
    fun getVulnDetailInfo_vulnIdを含むクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_VULN_DETAIL_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler).getVulnDetailInfo("JVNDB-2026-000001", lang = "en")

        val params = assertNotNull(captured).url.parameters
        assertEquals("getVulnDetailInfo", params["method"])
        assertEquals("hnd", params["feed"])
        assertEquals("xml", params["ft"])
        assertEquals("JVNDB-2026-000001", params["vulnId"])
        assertEquals("en", params["lang"])
    }

    @Test
    fun getVulnDetailInfo_retCdが0以外ならApiErrorを返す() = runTest {
        val result = dataSource(respondXml(ERROR_VULN_DETAIL_XML)).getVulnDetailInfo("INVALID")

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("VD01030602", result.errCd)
    }

    @Test
    fun getVulnDetailInfo_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getVulnDetailInfo("JVNDB-2026-000001")

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
