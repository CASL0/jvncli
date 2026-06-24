package com.github.casl0.jvncli.core

/** getVendorList の正常レスポンス (Vendor 3 件、retCd=0)。実 API のレスポンス構造に合わせている。 */
internal val SUCCESS_VENDOR_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <VendorInfo xml:lang="ja">
        <Vendor vname="#1 deals and maps app" cpe="cpe:/:pointinside" vid="10133"/>
        <Vendor vname="${'$'}0.99 kindle books project" cpe="cpe:/:%240.99_kindle_books_project" vid="11248"/>
        <Vendor vname="&amp;SONS Creative Design" cpe="cpe:/:andsonsdesign" vid="30339"/>
      </VendorInfo>
      <status:Status version="3.3" method="getVendorList" lang="ja" retCd="0" retMax="10000" errCd="" errMsg="" totalRes="33423" totalResRet="3" firstRes="1" feed="hnd" maxCountItem="3"/>
    </Result>
    """
        .trimIndent()

/** getVendorList のエラーレスポンス (retCd=1)。VendorInfo が無く Status に errCd/errMsg が入る。 */
internal val ERROR_VENDOR_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <status:Status version="3.3" method="getVendorList" lang="ja" retCd="1" retMax="" errCd="VN01020003" errMsg="startItemは正の整数値のみ指定可能です。" totalRes="" totalResRet="" firstRes="" feed="hnd" startItem="-1"/>
    </Result>
    """
        .trimIndent()
