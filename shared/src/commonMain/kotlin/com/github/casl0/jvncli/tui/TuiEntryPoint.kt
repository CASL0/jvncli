package com.github.casl0.jvncli.tui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.jakewharton.mosaic.runMosaic
import com.jakewharton.mosaic.ui.Text
import io.ktor.client.engine.HttpClientEngine

/**
 * TUI の起動エントリ。`runMosaic` をここに隠蔽し、app モジュールからは本関数だけを呼ぶ。
 *
 * Step 1 時点では Mosaic 描画と Molecule による状態生成が同一環境で成立することを確認するための最小実装。 [engine] は後続ステップで Presenter 経由の
 * MyJVN API 呼び出しに使う。
 */
suspend fun runJvnTui(engine: HttpClientEngine) = runMosaic {
    val scope = rememberCoroutineScope()
    val counter =
        remember(scope) {
            scope.launchMolecule(mode = RecompositionMode.Immediate) {
                var value by remember { mutableIntStateOf(0) }
                LaunchedEffect(Unit) { value = 42 }
                value
            }
        }
    val value by counter.collectAsState()
    Text("jvncli TUI (WIP) — molecule=$value, engine=${engine::class.simpleName}")
}
