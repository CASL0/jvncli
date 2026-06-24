package com.github.casl0.jvncli.core

/** getStatistics(feed=hnd, theme=sumCvss) の正常レスポンス。resDataTotal と CVSS 内訳を持つ。 */
internal val SUCCESS_STAT_HND_CVSS_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:mjstat="http://jvndb.jvn.jp/myjvn/Statistics" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <mjstat:sumCvss>
        <mjstat:title xml:lang="ja">CVSSスコア</mjstat:title>
        <mjstat:title xml:lang="en-US">CVSS Score</mjstat:title>
        <mjstat:resDataTotal vulinfo="289246" vendor="33411" product="87811"/>
        <mjstat:resData date="2024" cntAll="29114" cntC="3838" cntH="10410" cntM="14307" cntL="559" cntN="0"/>
        <mjstat:resData date="2025" cntAll="25232" cntC="4523" cntH="9246" cntM="10915" cntL="548" cntN="0"/>
      </mjstat:sumCvss>
      <status:Status version="3.3" method="getStatistics" lang="ja" retCd="0" retMax="10000" retMaxCnt="29114" errCd="" errMsg="" totalRes="2" totalResRet="2" firstRes="" feed="hnd" theme="sumCvss" type="y"/>
    </Result>
    """
        .trimIndent()

/** getStatistics(feed=itm, theme=sumJvnDb) の正常レスポンス。itm は resDataTotal を持たない。 */
internal val SUCCESS_STAT_ITM_JVNDB_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.2" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:mjstat="http://jvndb.jvn.jp/myjvn/Statistics" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <mjstat:sumJvnDb>
        <mjstat:title xml:lang="ja">脆弱性統計情報</mjstat:title>
        <mjstat:resData date="2024" cntAll="29228"/>
        <mjstat:resData date="2025" cntAll="25367"/>
      </mjstat:sumJvnDb>
      <status:Status version="3.2" method="getStatistics" lang="ja" retCd="0" retMax="10000" retMaxCnt="29228" errCd="" errMsg="" totalRes="2" totalResRet="2" firstRes="" feed="itm" theme="sumJvnDb" type="y"/>
    </Result>
    """
        .trimIndent()

/** getStatistics のエラーレスポンス (retCd=1)。 */
internal val ERROR_STAT_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:mjstat="http://jvndb.jvn.jp/myjvn/Statistics" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <status:Status version="3.3" method="getStatistics" lang="ja" retCd="1" retMax="" retMaxCnt="" errCd="VS06030970" errMsg="日付の組み合わせが無効です。" totalRes="" totalResRet="" firstRes="" feed="hnd" theme="sumJvnDb" type="m"/>
    </Result>
    """
        .trimIndent()
