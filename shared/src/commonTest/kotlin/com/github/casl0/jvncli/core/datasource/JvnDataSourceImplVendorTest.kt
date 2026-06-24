package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_VENDOR_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.SUCCESS_VENDOR_XML
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

class JvnDataSourceImplVendorTest {
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
    fun getVendorList_正常レスポンスをパースする() = runTest {
        val result = dataSource(respondXml(SUCCESS_VENDOR_XML)).getVendorList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val list = result.data
        assertEquals(33423, list.totalResults)
        assertEquals(3, list.returnedResults)
        assertEquals(1, list.firstResult)
        assertEquals(3, list.vendors.size)

        val first = list.vendors.first()
        assertEquals(10133, first.id)
        assertEquals("#1 deals and maps app", first.name)
        assertEquals("cpe:/:pointinside", first.cpe)

        // XML エンティティ (&amp;) が正しくデコードされる。
        assertEquals("&SONS Creative Design", list.vendors[2].name)
    }

    @Test
    fun getVendorList_期待するクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_VENDOR_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler)
            .getVendorList(startItem = 1, maxCountItem = 100, keyword = "cisco", lang = "en")

        val params = assertNotNull(captured).url.parameters
        assertEquals("getVendorList", params["method"])
        assertEquals("hnd", params["feed"])
        assertEquals("xml", params["ft"])
        assertEquals("1", params["startItem"])
        assertEquals("100", params["maxCountItem"])
        assertEquals("cisco", params["keyword"])
        assertEquals("en", params["lang"])
        // 未指定の任意パラメータはクエリに含まれない。
        assertNull(params["cpeName"])
    }

    @Test
    fun getVendorList_retCdが0以外ならApiErrorを返す() = runTest {
        val result = dataSource(respondXml(ERROR_VENDOR_XML)).getVendorList(startItem = -1)

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("VN01020003", result.errCd)
        assertEquals("startItemは正の整数値のみ指定可能です。", result.errMsg)
    }

    @Test
    fun getVendorList_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getVendorList()

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
