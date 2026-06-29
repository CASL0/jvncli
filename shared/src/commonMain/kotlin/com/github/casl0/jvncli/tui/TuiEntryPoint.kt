package com.github.casl0.jvncli.tui

import com.jakewharton.mosaic.runMosaic
import io.ktor.client.engine.HttpClientEngine

/**
 * TUI の起動エントリ。`runMosaic` をここに隠蔽し、app モジュールからは本関数だけを呼ぶ。
 *
 * [engine] は後続ステップで Presenter 経由の MyJVN API 呼び出しに使う(現時点ではナビゲーション骨組みのみ)。
 */
suspend fun runJvnTui(engine: HttpClientEngine) = runMosaic { App() }
