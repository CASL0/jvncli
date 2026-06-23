package com.github.casl0.jvncli.core.di

import com.github.casl0.jvncli.core.datasource.JvnDataSource
import com.github.casl0.jvncli.core.datasource.JvnDataSourceImpl
import com.github.casl0.jvncli.core.network.JvnApi
import com.github.casl0.jvncli.core.network.JvnClient
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import io.ktor.client.engine.HttpClientEngine

/**
 * Metro による DI グラフ。
 *
 * 外部から注入された HTTP エンジンで API クライアント ([JvnApi]) を [Provides] で生成し、[JvnDataSourceImpl] を
 * [JvnDataSource] として [Binds] で束ねる。グラフ自体と実装詳細はモジュール外へ公開せず、 [provideJvnDataSource] のみを公開する。
 */
@DependencyGraph
internal interface JvnGraph {
    val jvnDataSource: JvnDataSource

    @Provides fun provideJvnApi(engine: HttpClientEngine): JvnApi = JvnClient.createApi(engine)

    @Binds val JvnDataSourceImpl.bind: JvnDataSource

    @DependencyGraph.Factory
    interface Factory {
        fun create(@Provides engine: HttpClientEngine): JvnGraph
    }
}

/**
 * DI グラフを構築して [JvnDataSource] を取得する公開エントリポイント。
 *
 * @param engine 使用する HTTP エンジン (実行環境ごとに app モジュールが選択して渡す)。
 */
fun provideJvnDataSource(engine: HttpClientEngine): JvnDataSource =
    createGraphFactory<JvnGraph.Factory>().create(engine).jvnDataSource
