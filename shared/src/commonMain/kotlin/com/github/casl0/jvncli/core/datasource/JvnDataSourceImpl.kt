package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.model.Alert
import com.github.casl0.jvncli.core.model.AlertList
import com.github.casl0.jvncli.core.model.AlertReference
import com.github.casl0.jvncli.core.model.Product
import com.github.casl0.jvncli.core.model.ProductList
import com.github.casl0.jvncli.core.model.ProductVendor
import com.github.casl0.jvncli.core.model.Vendor
import com.github.casl0.jvncli.core.model.VendorList
import com.github.casl0.jvncli.core.network.JvnApi
import com.github.casl0.jvncli.core.network.model.AlertEntry
import com.github.casl0.jvncli.core.network.model.AlertFeed
import com.github.casl0.jvncli.core.network.model.JvnStatus
import com.github.casl0.jvncli.core.network.model.ProductEntry
import com.github.casl0.jvncli.core.network.model.SecItem
import com.github.casl0.jvncli.core.network.model.VendorEntry
import com.github.casl0.jvncli.core.network.model.VendorResult
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * [JvnApi] を呼び出してレスポンスを領域モデルへ変換する [JvnDataSource] の実装。
 *
 * XML から DTO へのデコードは ContentNegotiation が担うため、ここでは retCd 判定と領域モデルへの マッピングに専念する。[JvnApi]
 * はコンストラクタで注入され、本クラス自身も DI グラフ ([com.github.casl0.jvncli.core.di.JvnGraph]) から生成される。
 */
@Inject
internal class JvnDataSourceImpl(private val api: JvnApi) : JvnDataSource {
    override suspend fun getAlertList(
        startItem: Int?,
        maxCountItem: Int?,
        datePublished: Int?,
        dateFirstPublished: Int?,
        cpeName: String?,
    ): JvnResult<AlertList> =
        fetch(
            call = {
                api.getAlertList(
                    startItem = startItem,
                    maxCountItem = maxCountItem,
                    datePublished = datePublished,
                    dateFirstPublished = dateFirstPublished,
                    cpeName = cpeName,
                )
            },
            statusOf = { it.status },
            onSuccess = { it.toAlertList() },
        )

    override suspend fun getVendorList(
        startItem: Int?,
        maxCountItem: Int?,
        cpeName: String?,
        keyword: String?,
        lang: String?,
    ): JvnResult<VendorList> =
        fetch(
            call = {
                api.getVendorList(
                    startItem = startItem,
                    maxCountItem = maxCountItem,
                    cpeName = cpeName,
                    keyword = keyword,
                    lang = lang,
                )
            },
            statusOf = { it.status },
            onSuccess = { it.toVendorList() },
        )

    override suspend fun getProductList(
        startItem: Int?,
        maxCountItem: Int?,
        cpeName: String?,
        vendorId: Int?,
        productId: Int?,
        keyword: String?,
        lang: String?,
    ): JvnResult<ProductList> =
        fetch(
            call = {
                api.getProductList(
                    startItem = startItem,
                    maxCountItem = maxCountItem,
                    cpeName = cpeName,
                    vendorId = vendorId,
                    productId = productId,
                    keyword = keyword,
                    lang = lang,
                )
            },
            statusOf = { it.status },
            onSuccess = { it.toProductList() },
        )

    /**
     * API 呼び出し・retCd 判定・例外処理を一元化する共通ヘルパー。
     *
     * @param call API を呼び DTO を取得する処理
     * @param statusOf DTO から [JvnStatus] を取り出す処理
     * @param onSuccess retCd=0 のとき DTO を領域モデルへ変換する処理
     */
    private suspend fun <T, R> fetch(
        call: suspend () -> T,
        statusOf: (T) -> JvnStatus,
        onSuccess: (T) -> R,
    ): JvnResult<R> =
        try {
            val dto = call()
            val status = statusOf(dto)
            if (status.retCd != 0) {
                JvnResult.ApiError(
                    retCd = status.retCd,
                    errCd = status.errCd.ifBlank { null },
                    errMsg = status.errMsg.ifBlank { null },
                )
            } else {
                JvnResult.Success(onSuccess(dto))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            JvnResult.NetworkError(e)
        }
}

private fun AlertFeed.toAlertList(): AlertList =
    AlertList(
        alerts = entries.map { it.toAlert() },
        totalResults = status.totalRes.toIntOrNull() ?: 0,
        returnedResults = status.totalResRet.toIntOrNull() ?: entries.size,
        firstResult = status.firstRes.toIntOrNull() ?: 0,
    )

private fun AlertEntry.toAlert(): Alert =
    Alert(
        id = id,
        title = title,
        published = published,
        updated = updated,
        severityLabel = category?.label,
        severityTerm = category?.term,
        references = items?.items?.map { it.toReference() } ?: emptyList(),
    )

private fun SecItem.toReference(): AlertReference =
    AlertReference(
        title = title,
        identifier = identifier,
        url = link?.href,
        cpe = cpe,
        published = published,
        updated = updated,
    )

private fun VendorResult.toVendorList(): VendorList {
    val vendors = vendorInfo?.vendors.orEmpty()
    return VendorList(
        vendors = vendors.map { it.toVendor() },
        totalResults = status.totalRes.toIntOrNull() ?: 0,
        returnedResults = status.totalResRet.toIntOrNull() ?: vendors.size,
        firstResult = status.firstRes.toIntOrNull() ?: 0,
    )
}

private fun VendorEntry.toVendor(): Vendor = Vendor(id = vid, name = vname, cpe = cpe)

private fun VendorResult.toProductList(): ProductList {
    val vendors = vendorInfo?.vendors.orEmpty()
    return ProductList(
        vendors = vendors.map { it.toProductVendor() },
        totalResults = status.totalRes.toIntOrNull() ?: 0,
        returnedResults = status.totalResRet.toIntOrNull() ?: vendors.size,
        firstResult = status.firstRes.toIntOrNull() ?: 0,
    )
}

private fun VendorEntry.toProductVendor(): ProductVendor =
    ProductVendor(id = vid, name = vname, cpe = cpe, products = products.map { it.toProduct() })

private fun ProductEntry.toProduct(): Product = Product(id = pid, name = pname, cpe = cpe)
