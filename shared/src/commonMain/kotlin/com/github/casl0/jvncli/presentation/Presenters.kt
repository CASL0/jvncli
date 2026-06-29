package com.github.casl0.jvncli.presentation

import com.github.casl0.jvncli.core.di.provideJvnDataSource
import com.github.casl0.jvncli.presentation.presenter.AlertListPresenter
import com.github.casl0.jvncli.presentation.presenter.MoleculeAlertListPresenter
import com.github.casl0.jvncli.presentation.presenter.MoleculeVulnListPresenter
import com.github.casl0.jvncli.presentation.presenter.VulnListPresenter
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineScope

/** 各画面の Presenter をまとめて保持する入れ物。 */
internal class Presenters(val alertList: AlertListPresenter, val vulnList: VulnListPresenter)

/**
 * HTTP エンジンと [scope] から Presenter 群を構築する。
 *
 * データ層は既存の [provideJvnDataSource] 経由で取得し、実装詳細(Molecule/DI)は外へ出さない。
 */
internal fun providePresenters(engine: HttpClientEngine, scope: CoroutineScope): Presenters {
    val dataSource = provideJvnDataSource(engine)
    return Presenters(
        alertList = MoleculeAlertListPresenter(dataSource, scope),
        vulnList = MoleculeVulnListPresenter(dataSource, scope),
    )
}
