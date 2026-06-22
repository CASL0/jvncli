package com.github.casl0.jvncli.core.datasource

import com.github.casl0.jvncli.core.JvnResult
import com.github.casl0.jvncli.core.model.Alert
import com.github.casl0.jvncli.core.model.AlertList
import com.github.casl0.jvncli.core.model.AlertReference
import com.github.casl0.jvncli.core.network.JvnApi
import com.github.casl0.jvncli.core.network.model.AlertEntry
import com.github.casl0.jvncli.core.network.model.AlertFeed
import com.github.casl0.jvncli.core.network.model.SecItem
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import nl.adaptivity.xmlutil.serialization.XML

/**
 * [JvnApi] を呼び出してレスポンス XML を領域モデルへ変換する [JvnDataSource] の実装。
 *
 * [JvnApi] はコンストラクタで注入され、本クラス自身も DI グラフ ([com.github.casl0.jvncli.core.di.JvnGraph]) から生成される。
 */
@Inject
internal class JvnDataSourceImpl(private val api: JvnApi) : JvnDataSource {
    override suspend fun getAlertList(
        startItem: Int?,
        maxCountItem: Int?,
        datePublished: Int?,
        dateFirstPublished: Int?,
        cpeName: String?,
    ): JvnResult<AlertList> {
        val rawXml =
            try {
                api.getAlertList(
                    startItem = startItem,
                    maxCountItem = maxCountItem,
                    datePublished = datePublished,
                    dateFirstPublished = dateFirstPublished,
                    cpeName = cpeName,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                return JvnResult.NetworkError(e)
            }

        return try {
            val feed = xml.decodeFromString(AlertFeed.serializer(), rawXml)
            val status = feed.status
            if (status.retCd != 0) {
                JvnResult.ApiError(
                    retCd = status.retCd,
                    errCd = status.errCd?.ifBlank { null },
                    errMsg = status.errMsg?.ifBlank { null },
                )
            } else {
                JvnResult.Success(feed.toAlertList())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            JvnResult.NetworkError(e)
        }
    }

    private companion object {
        val xml = XML { defaultPolicy { ignoreUnknownChildren() } }
    }
}

private fun AlertFeed.toAlertList(): AlertList =
    AlertList(
        alerts = entries.map { it.toAlert() },
        totalResults = status.totalRes?.toIntOrNull() ?: 0,
        returnedResults = status.totalResRet?.toIntOrNull() ?: entries.size,
        firstResult = status.firstRes?.toIntOrNull() ?: 0,
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
