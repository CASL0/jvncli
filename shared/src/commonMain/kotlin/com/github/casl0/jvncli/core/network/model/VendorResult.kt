package com.github.casl0.jvncli.core.network.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * getVendorList のレスポンスルート `<Result>`。
 *
 * 正常時は [vendorInfo] にベンダー一覧が入り、エラー時は [vendorInfo] が無く [status] の `retCd` が 0 以外になる。
 */
@Serializable
@XmlSerialName(value = "Result", namespace = RESULTS_NS, prefix = "")
internal data class VendorResult(
    @XmlSerialName(value = "VendorInfo", namespace = RESULTS_NS, prefix = "")
    val vendorInfo: VendorInfo? = null,
    val status: JvnStatus,
)

/** ベンダー一覧のコンテナ `<VendorInfo>`。 */
@Serializable
@XmlSerialName(value = "VendorInfo", namespace = RESULTS_NS, prefix = "")
internal data class VendorInfo(val vendors: List<VendorEntry> = emptyList())

/**
 * 個々のベンダー `<Vendor vname="..." cpe="..." vid="...">`。属性は results_3.3.xsd 上すべて必須。
 *
 * [products] は getProductList で子要素 `<Product>` として入る (getVendorList では空)。
 */
@Serializable
@XmlSerialName(value = "Vendor", namespace = RESULTS_NS, prefix = "")
internal data class VendorEntry(
    val vname: String,
    val cpe: String,
    val vid: Int,
    val products: List<ProductEntry> = emptyList(),
)

/** 個々の製品 `<Product pname="..." cpe="..." pid="..."/>`。属性は results_3.3.xsd 上すべて必須。 */
@Serializable
@XmlSerialName(value = "Product", namespace = RESULTS_NS, prefix = "")
internal data class ProductEntry(val pname: String, val cpe: String, val pid: Int)
