package com.github.casl0.jvncli.core

/** getAlertList の正常レスポンス (entry 2 件、retCd=0)。実 API のレスポンス構造に合わせている。 */
internal val SUCCESS_ALERT_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <feed xmlns="http://www.w3.org/2005/Atom" xmlns:sec="http://jvn.jp/rss/mod_sec/3.0/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xml:lang="ja">
      <title type="text">IPA注意警戒サービスAPI</title>
      <updated>2026-06-22T20:26:53+09:00</updated>
      <id>swid:ipa.go.jp+myjvn_alert+1.0.0</id>
      <entry>
        <title>Cisco 製品に関するセキュリティ情報 (2025年11月6日 公開)</title>
        <id>MYJVN-ALT-2025-0096</id>
        <published>2026-04-27T16:14:36+09:00</published>
        <updated>2026-04-27T16:14:36+09:00</updated>
        <category label="注意" term="Low"/>
        <sec:items>
          <sec:item>
            <sec:title>更新:Cisco Secure Firewall ASA の脆弱性について</sec:title>
            <sec:identifier>MYJVN-ALT-2025-0096-0001</sec:identifier>
            <sec:link href="https://www.ipa.go.jp/security/security-alert/2025/alert20251106.html"/>
            <sec:published>2026-04-27T00:00:00+09:00</sec:published>
            <sec:updated>2026-04-27T16:14:23+09:00</sec:updated>
          </sec:item>
        </sec:items>
      </entry>
      <entry>
        <title>Joomla! に関するセキュリティ情報 (2026年1月7日 公開)</title>
        <id>MYJVN-ALT-2026-0001</id>
        <published>2026-01-07T10:51:25+09:00</published>
        <updated>2026-01-07T10:51:25+09:00</updated>
        <category label="INFO" term="Info"/>
        <sec:items>
          <sec:item>
            <sec:title>Joomla! Downloads (6.0.2)</sec:title>
            <sec:identifier>MYJVN-ALT-2026-0001-0001</sec:identifier>
            <sec:link href="https://downloads.joomla.org/ja/cms/joomla6/6-0-2"/>
            <sec:cpe>cpe:/a:joomla:joomla%21</sec:cpe>
            <sec:published>2026-01-07T00:00:00+09:00</sec:published>
            <sec:updated>2026-01-07T10:51:25+09:00</sec:updated>
          </sec:item>
        </sec:items>
      </entry>
      <status:Status version="3.3" method="getAlertList" retCd="0" retMax="50" errCd="" errMsg="" totalRes="59" totalResRet="2" firstRes="1" feed="hnd" maxCountItem="2" ft="xml"/>
    </feed>
    """
        .trimIndent()

/** getAlertList のエラーレスポンス (retCd=1)。entry が無く Status に errCd/errMsg が入る。 */
internal val ERROR_ALERT_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <feed xmlns="http://www.w3.org/2005/Atom" xmlns:sec="http://jvn.jp/rss/mod_sec/3.0/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xml:lang="ja">
      <title type="text">IPA注意警戒サービスAPI</title>
      <updated>2026-06-22T20:27:59+09:00</updated>
      <id>swid:ipa.go.jp+myjvn_alert+1.0.0</id>
      <status:Status version="3.3" method="getAlertList" retCd="1" retMax="" errCd="AL0703019" errMsg="datePublishedは半角数字のみ指定可能です（1～9999）。" totalRes="" totalResRet="" firstRes="" feed="hnd" maxCountItem="2" datePublished="abcd" ft="xml"/>
    </feed>
    """
        .trimIndent()
