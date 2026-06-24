package com.github.casl0.jvncli.core

/** getProductList の正常レスポンス (Vendor 2 件、各 Product 1 件、retCd=0)。実 API の構造に合わせている。 */
internal val SUCCESS_PRODUCT_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <VendorInfo xml:lang="ja">
        <Vendor vname="#1 deals and maps app" cpe="cpe:/:pointinside" vid="10133">
          <Product pname="Point Inside Shopping &amp; Travel" cpe="cpe:/a:pointinside:point_inside_shopping_%26_travel" pid="21248"/>
        </Vendor>
        <Vendor vname="${'$'}0.99 kindle books project" cpe="cpe:/:%240.99_kindle_books_project" vid="11248">
          <Product pname="${'$'}0.99 kindle books" cpe="cpe:/a:%240.99_kindle_books_project:%240.99_kindle_books" pid="22920"/>
        </Vendor>
      </VendorInfo>
      <status:Status version="3.3" method="getProductList" lang="ja" retCd="0" retMax="10000" errCd="" errMsg="" totalRes="87800" totalResRet="2" firstRes="1" feed="hnd" maxCountItem="2"/>
    </Result>
    """
        .trimIndent()

/** getProductList のエラーレスポンス (retCd=1)。VendorInfo が無く Status に errCd/errMsg が入る。 */
internal val ERROR_PRODUCT_XML =
    """
    <?xml version="1.0" encoding="UTF-8" ?>
    <Result version="3.3" xmlns="http://jvndb.jvn.jp/myjvn/Results" xmlns:status="http://jvndb.jvn.jp/myjvn/Status">
      <status:Status version="3.3" method="getProductList" lang="ja" retCd="1" retMax="" errCd="PR01020003" errMsg="startItemは正の整数値のみ指定可能です。" totalRes="" totalResRet="" firstRes="" feed="hnd" startItem="-1"/>
    </Result>
    """
        .trimIndent()
