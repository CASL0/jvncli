package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.ERROR_PRODUCT_XML
import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.SUCCESS_PRODUCT_XML
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

class JvnDataSourceImplProductTest {
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
    fun getProductList_ベンダー配下に製品を入れ子でパースする() = runTest {
        val result = dataSource(respondXml(SUCCESS_PRODUCT_XML)).getProductList()

        assertTrue(result is JvnResult.Success, "Success を期待: $result")
        val list = result.data
        assertEquals(87800, list.totalResults)
        assertEquals(2, list.returnedResults)
        assertEquals(1, list.firstResult)
        assertEquals(2, list.vendors.size)

        val firstVendor = list.vendors.first()
        assertEquals(10133, firstVendor.id)
        assertEquals("#1 deals and maps app", firstVendor.name)
        assertEquals(1, firstVendor.products.size)

        val product = firstVendor.products.first()
        assertEquals(21248, product.id)
        // XML エンティティ (&amp;) が正しくデコードされる。
        assertEquals("Point Inside Shopping & Travel", product.name)
    }

    @Test
    fun getProductList_期待するクエリパラメータを送信する() = runTest {
        var captured: HttpRequestData? = null
        val handler: MockRequestHandler = { request ->
            captured = request
            respond(
                content = SUCCESS_PRODUCT_XML,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/xml"),
            )
        }

        dataSource(handler).getProductList(vendorId = 10133, maxCountItem = 50, lang = "en")

        val params = assertNotNull(captured).url.parameters
        assertEquals("getProductList", params["method"])
        assertEquals("hnd", params["feed"])
        assertEquals("xml", params["ft"])
        assertEquals("10133", params["vendorId"])
        assertEquals("50", params["maxCountItem"])
        assertEquals("en", params["lang"])
        // 未指定の任意パラメータはクエリに含まれない。
        assertNull(params["productId"])
        assertNull(params["cpeName"])
    }

    @Test
    fun getProductList_retCdが0以外ならApiErrorを返す() = runTest {
        val result = dataSource(respondXml(ERROR_PRODUCT_XML)).getProductList(startItem = -1)

        assertTrue(result is JvnResult.ApiError, "ApiError を期待: $result")
        assertEquals(1, result.retCd)
        assertEquals("PR01020003", result.errCd)
    }

    @Test
    fun getProductList_通信に失敗したらNetworkErrorを返す() = runTest {
        val handler: MockRequestHandler = { throw RuntimeException("接続失敗") }

        val result = dataSource(handler).getProductList()

        assertTrue(result is JvnResult.NetworkError, "NetworkError を期待: $result")
    }
}
