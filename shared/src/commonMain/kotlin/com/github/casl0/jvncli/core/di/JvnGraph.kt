package com.github.casl0.jvncli.core.di

import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.datasource.JvnDataSourceImpl
import com.github.casl0.jvncli.core.network.JvnApi
import com.github.casl0.jvncli.core.network.JvnClient
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph

/**
 * Metro による DI グラフ。
 *
 * API クライアント ([JvnApi]) を [Provides] で生成し、[JvnDataSourceImpl] を [JvnDataSource] として [Binds]
 * で束ねる。グラフ自体と実装詳細はモジュール外へ公開せず、[provideJvnDataSource] のみを公開する。
 */
@DependencyGraph
internal interface JvnGraph {
    val jvnDataSource: JvnDataSource

    @Provides fun provideJvnApi(): JvnApi = JvnClient.createApi()

    @Binds val JvnDataSourceImpl.bind: JvnDataSource
}

/** DI グラフを構築して [JvnDataSource] を取得する公開エントリポイント。 */
fun provideJvnDataSource(): JvnDataSource = createGraph<JvnGraph>().jvnDataSource
