package com.github.casl0.jvncli.core

/**
 * getVulnOverviewList の正常レスポンス (item 2 件、retCd=0)。
 *
 * 1 件目は references/cpe/cvss を持ち、2 件目は最小構成 (title/link/identifier のみ)。実 API の構造に合わせている。
 */
internal val SUCCESS_VULN_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <rdf:RDF xmlns="http://purl.org/rss/1.0/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:sec="http://jvn.jp/rss/mod_sec/3.0/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xml:lang="ja">
      <channel rdf:about="https://jvndb.jvn.jp/apis/myjvn">
        <title>JVNDB　脆弱性対策情報</title>
        <link>https://jvndb.jvn.jp/apis/myjvn</link>
        <description>JVNDB　脆弱性対策情報</description>
        <items>
          <rdf:Seq>
            <rdf:li rdf:resource="https://jvndb.jvn.jp/ja/contents/2026/JVNDB-2026-020739.html"/>
          </rdf:Seq>
        </items>
      </channel>
      <item rdf:about="https://jvndb.jvn.jp/ja/contents/2026/JVNDB-2026-000001.html">
        <title>サンプル製品における SQL インジェクションの脆弱性</title>
        <link>https://jvndb.jvn.jp/ja/contents/2026/JVNDB-2026-000001.html</link>
        <description>サンプル製品には SQL インジェクション（CWE-89）の脆弱性が存在します。</description>
        <dc:creator>Information-technology Promotion Agency, Japan</dc:creator>
        <sec:identifier>JVNDB-2026-000001</sec:identifier>
        <sec:references source="CVE" id="CVE-2026-00001">https://www.cve.org/CVERecord?id=CVE-2026-00001</sec:references>
        <sec:references id="CWE-89" title="SQLインジェクション(CWE-89)">https://cwe.mitre.org/data/definitions/89.html</sec:references>
        <sec:cpe version="2.2" vendor="サンプルベンダ" product="サンプル製品">cpe:/a:sample:sample_product</sec:cpe>
        <sec:cvss score="9.8" severity="Critical" vector="CVSS:3.0/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H" version="3.0" type="Base"/>
        <dc:date>2026-06-24T14:38:41+09:00</dc:date>
        <dcterms:issued>2026-06-20T00:00:00+09:00</dcterms:issued>
        <dcterms:modified>2026-06-24T14:38:41+09:00</dcterms:modified>
      </item>
      <item rdf:about="https://jvndb.jvn.jp/ja/contents/2026/JVNDB-2026-000002.html">
        <title>最小エントリの脆弱性</title>
        <link>https://jvndb.jvn.jp/ja/contents/2026/JVNDB-2026-000002.html</link>
        <sec:identifier>JVNDB-2026-000002</sec:identifier>
      </item>
      <status:Status version="3.3" method="getVulnOverviewList" lang="ja" retCd="0" retMax="50" errCd="" errMsg="" totalRes="568" totalResRet="2" firstRes="1" feed="hnd" maxCountItem="2" rangeDatePublic="n"/>
    </rdf:RDF>
    """
        .trimIndent()

/** getVulnOverviewList のエラーレスポンス (retCd=1)。item が無く Status に errCd/errMsg が入る。 */
internal val ERROR_VULN_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <rdf:RDF xmlns="http://purl.org/rss/1.0/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:sec="http://jvn.jp/rss/mod_sec/3.0/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xml:lang="ja">
      <status:Status version="3.3" method="getVulnOverviewList" lang="ja" retCd="1" retMax="" errCd="VL01020003" errMsg="startItemは正の整数値のみ指定可能です。" totalRes="" totalResRet="" firstRes="" feed="hnd" startItem="-1"/>
    </rdf:RDF>
    """
        .trimIndent()
