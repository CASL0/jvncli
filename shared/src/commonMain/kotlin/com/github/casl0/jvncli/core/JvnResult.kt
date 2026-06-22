package com.github.casl0.jvncli.core

/**
 * MyJVN API 呼び出しの結果。
 *
 * 正常・API エラー (retCd != 0)・通信/解析エラーを型で区別する。呼び出し側は `when` で網羅的に扱える。
 */
sealed interface JvnResult<out T> {
    /** 正常終了。 */
    data class Success<T>(val data: T) : JvnResult<T>

    /** API がエラーを返した (retCd != 0)。 */
    data class ApiError(val retCd: Int, val errCd: String?, val errMsg: String?) :
        JvnResult<Nothing>

    /** 通信失敗またはレスポンス解析失敗。 */
    data class NetworkError(val cause: Throwable) : JvnResult<Nothing>
}
