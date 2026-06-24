package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * getStatistics のレスポンスルート `<Result>`。
 *
 * feed=hnd / feed=itm で構造は同じ (itm は resDataTotal を持たない)。theme に応じて [sumJvnDb] / [sumCvss] のいずれか
 * (sumAll では両方) が入る。title は xml:lang が絡むため読み飛ばす。
 */
@Serializable
@XmlSerialName(value = "Result", namespace = RESULTS_NS, prefix = "")
internal data class StatisticsResult(
    @XmlSerialName(value = "sumJvnDb", namespace = STATISTICS_NS, prefix = "mjstat")
    val sumJvnDb: StatisticsSummary? = null,
    @XmlSerialName(value = "sumCvss", namespace = STATISTICS_NS, prefix = "mjstat")
    val sumCvss: StatisticsSummary? = null,
    val status: JvnStatus,
)

/** 統計の集計コンテナ。要素名は利用側 ([StatisticsResult]) のプロパティで決まる。 */
@Serializable
internal data class StatisticsSummary(
    @XmlSerialName(value = "resDataTotal", namespace = STATISTICS_NS, prefix = "mjstat")
    val total: ResDataTotal? = null,
    @XmlSerialName(value = "resData", namespace = STATISTICS_NS, prefix = "mjstat")
    val data: List<ResData> = emptyList(),
)

/** 全体の総数 `<mjstat:resDataTotal vulinfo="..." vendor="..." product="..."/>`。 */
@Serializable
@XmlSerialName(value = "resDataTotal", namespace = STATISTICS_NS, prefix = "mjstat")
internal data class ResDataTotal(
    val vulinfo: Int? = null,
    val vendor: Int? = null,
    val product: Int? = null,
)

/**
 * 期間ごとの集計 `<mjstat:resData date="2024" cntAll="..." .../>`。
 *
 * cntC/cntH/cntM/cntL/cntN は sumCvss のときのみ付与される。
 */
@Serializable
@XmlSerialName(value = "resData", namespace = STATISTICS_NS, prefix = "mjstat")
internal data class ResData(
    val date: String? = null,
    val cntAll: Int? = null,
    val cntC: Int? = null,
    val cntH: Int? = null,
    val cntM: Int? = null,
    val cntL: Int? = null,
    val cntN: Int? = null,
)
