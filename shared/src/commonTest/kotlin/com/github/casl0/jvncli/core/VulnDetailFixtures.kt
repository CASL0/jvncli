package com.github.casl0.jvncli.core

/** getVulnDetailInfo の正常レスポンス (Vulinfo 1 件、retCd=0)。実 API の VULDEF 構造に合わせている。 */
internal val SUCCESS_VULN_DETAIL_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <VULDEF-Document version="3.2" xmlns="http://jvn.jp/vuldef/" xmlns:vuldef="http://jvn.jp/vuldef/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xmlns:sec="http://jvn.jp/rss/mod_sec/3.0/" xml:lang="ja">
      <Vulinfo>
        <VulinfoID>JVNDB-2026-000001</VulinfoID>
        <VulinfoData>
          <Title>サンプル製品における送信元の確認が不十分な脆弱性</Title>
          <VulinfoDescription>
            <Overview>サンプル製品には、送信元の確認が不十分（CWE-346）の脆弱性が存在します。</Overview>
          </VulinfoDescription>
          <Affected>
            <AffectedItem>
              <Name>サンプルベンダ</Name>
              <ProductName>サンプル製品</ProductName>
              <Cpe version="2.2">cpe:/a:sample:sample_product</Cpe>
              <VersionNumber>バージョン2.0.25.0およびそれ以前</VersionNumber>
            </AffectedItem>
          </Affected>
          <Impact>
            <Cvss version="3.0">
              <Severity type="Base">High</Severity>
              <Base>7.8</Base>
              <Vector>CVSS:3.0/AV:L/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:H</Vector>
            </Cvss>
            <ImpactItem>
              <Description>任意のコマンドを実行される可能性があります。</Description>
            </ImpactItem>
          </Impact>
          <Solution>
            <SolutionItem>
              <Description>[アップデートする] 最新版へアップデートしてください。</Description>
            </SolutionItem>
          </Solution>
          <Related>
            <RelatedItem type="advisory">
              <Name>Common Vulnerabilities and Exposures (CVE)</Name>
              <VulinfoID>CVE-2026-20893</VulinfoID>
              <URL>https://www.cve.org/CVERecord?id=CVE-2026-20893</URL>
            </RelatedItem>
            <RelatedItem type="cwe">
              <Name>JVNDB</Name>
              <VulinfoID>CWE-346</VulinfoID>
              <Title>送信元の確認が不十分</Title>
              <URL>https://cwe.mitre.org/data/definitions/346.html</URL>
            </RelatedItem>
          </Related>
          <History>
            <HistoryItem>
              <HistoryNo>1</HistoryNo>
              <DateTime>2026-01-05T13:43:31+09:00</DateTime>
              <Description>[2026年01月07日]　掲載</Description>
            </HistoryItem>
          </History>
          <DateFirstPublished>2026-01-07T12:10:05+09:00</DateFirstPublished>
          <DateLastUpdated>2026-01-07T12:10:05+09:00</DateLastUpdated>
          <DatePublic>2026-01-07T00:00:00+09:00</DatePublic>
        </VulinfoData>
      </Vulinfo>
      <status:Status version="3.3" method="getVulnDetailInfo" lang="ja" retCd="0" retMax="10" errCd="" errMsg="" totalRes="1" totalResRet="1" firstRes="1" feed="hnd" vulnId="JVNDB-2026-000001"/>
    </VULDEF-Document>
    """
        .trimIndent()

/** getVulnDetailInfo のエラーレスポンス (retCd=1)。Vulinfo が無く Status に errCd/errMsg が入る。 */
internal val ERROR_VULN_DETAIL_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <VULDEF-Document version="3.2" xmlns="http://jvn.jp/vuldef/" xmlns:status="http://jvndb.jvn.jp/myjvn/Status" xml:lang="ja">
      <status:Status version="3.3" method="getVulnDetailInfo" lang="ja" retCd="1" retMax="" errCd="VD01030602" errMsg="vulnIdの先頭は「JVNDB」のみ指定可能です。" totalRes="" totalResRet="" firstRes="" feed="hnd" vulnId="INVALID"/>
    </VULDEF-Document>
    """
        .trimIndent()
