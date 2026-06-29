package com.github.casl0.jvncli.presentation

import kotlinx.coroutines.flow.StateFlow

/**
 * UI 非依存の共通プレゼンター。状態型 [S] とイベント型 [E] だけをパラメータ化する。
 *
 * 画面ごとの型は `typealias AlertListPresenter = Presenter<AlertUiState, AlertEvent>` のように別名で表す。
 * `launchMolecule` と `@Composable` 本体は実装クラス側に隠蔽する。
 */
interface Presenter<S, E> {
    val uiState: StateFlow<S>

    fun onEvent(event: E)
}
