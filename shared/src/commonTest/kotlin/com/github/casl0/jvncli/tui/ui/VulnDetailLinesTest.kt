package com.github.casl0.jvncli.tui.ui

import com.github.casl0.jvncli.core.model.CvssScore
import com.github.casl0.jvncli.core.model.VulnDetail
import com.github.casl0.jvncli.presentation.state.LoadPhase
import com.github.casl0.jvncli.presentation.state.VulnDetailUiState
import com.github.casl0.jvncli.tui.ERROR_COLOR
import com.github.casl0.jvncli.tui.SeverityLevel
import com.github.casl0.jvncli.tui.severityColor
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.TextStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** 検証対象の行だけを持つ最小の詳細。指定しない項目は空にする。 */
private fun detail(cvssScores: List<CvssScore> = emptyList()) =
    VulnDetail(
        id = "JVNDB-2026-000001",
        title = "テスト脆弱性",
        overview = null,
        affected = emptyList(),
        cvssScores = cvssScores,
        impacts = emptyList(),
        solutions = emptyList(),
        related = emptyList(),
        history = emptyList(),
        dateFirstPublished = null,
        dateLastUpdated = null,
        datePublic = null,
    )

private fun cvss(score: Double?, severity: String?, version: String = "3.0") =
    CvssScore(version = version, type = null, severity = severity, score = score, vector = null)

private fun loaded(cvssScores: List<CvssScore>) =
    VulnDetailUiState(phase = LoadPhase.Loaded, detail = detail(cvssScores), error = null)

class VulnDetailLinesTest {
    @Test
    fun `buildLines_CVSS行を深刻度色で着色する`() {
        val lines = buildLines(loaded(listOf(cvss(9.8, "Critical"))), 80)
        val cvssLine = assertNotNull(lines.find { it.text.startsWith("- v3.0") })
        assertEquals(severityColor(SeverityLevel.CRITICAL), cvssLine.color)
    }

    @Test
    fun `buildLines_バージョンごとに異なる深刻度色を引く`() {
        val lines =
            buildLines(loaded(listOf(cvss(9.8, "Critical"), cvss(5.0, "Medium", "2.0"))), 80)
        val colors = lines.filter { it.text.startsWith("- v") }.map { it.color }
        assertEquals(
            listOf(severityColor(SeverityLevel.CRITICAL), severityColor(SeverityLevel.MEDIUM)),
            colors,
        )
    }

    @Test
    fun `buildLines_見出しは色を付けず太字のまま`() {
        val lines = buildLines(loaded(listOf(cvss(9.8, "Critical"))), 80)
        val heading = assertNotNull(lines.find { it.text == "CVSS" })
        assertEquals(TextStyle.Bold, heading.style)
        assertEquals(Color.Unspecified, heading.color)
    }

    @Test
    fun `buildLines_エラーメッセージを赤にする`() {
        val state = VulnDetailUiState(phase = LoadPhase.Error, detail = null, error = "通信に失敗")
        val lines = buildLines(state, 80)
        assertEquals(listOf("通信に失敗"), lines.map { it.text })
        assertEquals(ERROR_COLOR, lines.single().color)
    }

    @Test
    fun `buildLines_読み込み中と該当なしは控えめに出す`() {
        val loading = VulnDetailUiState(phase = LoadPhase.Loading, detail = null, error = null)
        assertEquals(TextStyle.Dim, buildLines(loading, 80).single().style)

        val notFound = VulnDetailUiState(phase = LoadPhase.Loaded, detail = null, error = null)
        assertEquals(TextStyle.Dim, buildLines(notFound, 80).single().style)
    }

    @Test
    fun `buildLines_スコアも重要度も無い行は色を付けない`() {
        val lines = buildLines(loaded(listOf(cvss(null, null))), 80)
        val cvssLine = assertNotNull(lines.find { it.text.startsWith("- v3.0") })
        assertEquals(Color.Unspecified, cvssLine.color)
        assertEquals(TextStyle.Dim, cvssLine.style)
    }

    @Test
    fun `buildLines_タイトル行は色を持たない`() {
        val lines = buildLines(loaded(emptyList()), 80)
        assertTrue(lines.none { it.text == "テスト脆弱性" && it.color != Color.Unspecified })
    }
}
