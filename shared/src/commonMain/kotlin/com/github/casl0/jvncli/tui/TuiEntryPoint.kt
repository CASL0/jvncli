package com.github.casl0.jvncli.tui

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.github.casl0.jvncli.presentation.providePresenters
import com.jakewharton.mosaic.runMosaic
import io.ktor.client.engine.HttpClientEngine

/**
 * TUI の起動エントリ。`runMosaic` をここに隠蔽し、app モジュールからは本関数だけを呼ぶ。
 *
 * [engine] を使って Presenter 群を構築し、ルートの [App] へ渡す。
 */
suspend fun runJvnTui(engine: HttpClientEngine) = runMosaic {
    val scope = rememberCoroutineScope()
    val presenters = remember { providePresenters(engine, scope) }
    App(presenters)
}
