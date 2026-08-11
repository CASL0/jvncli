package com.github.casl0.jvncli.tui

import com.github.casl0.jvncli.core.model.CvssScore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** テスト対象は深刻度の正規化だけなので、関係しない項目は既定値で埋める。 */
private fun cvss(score: Double? = null, severity: String? = null, version: String? = "3.0") =
    CvssScore(version = version, type = null, severity = severity, score = score, vector = null)

class SeverityTest {
    @Test
    fun `severityLevel_スコアの閾値ちょうどで区分が切り替わる`() {
        assertEquals(SeverityLevel.CRITICAL, cvss(score = 9.0).severityLevel())
        assertEquals(SeverityLevel.HIGH, cvss(score = 8.9).severityLevel())
        assertEquals(SeverityLevel.HIGH, cvss(score = 7.0).severityLevel())
        assertEquals(SeverityLevel.MEDIUM, cvss(score = 6.9).severityLevel())
        assertEquals(SeverityLevel.MEDIUM, cvss(score = 4.0).severityLevel())
        assertEquals(SeverityLevel.LOW, cvss(score = 3.9).severityLevel())
        assertEquals(SeverityLevel.LOW, cvss(score = 0.1).severityLevel())
        assertEquals(SeverityLevel.NONE, cvss(score = 0.0).severityLevel())
    }

    @Test
    fun `severityLevel_スコアが無ければ深刻度なし`() {
        assertEquals(SeverityLevel.NONE, cvss().severityLevel())
    }

    @Test
    fun `severityLevel_重要度の語をスコアより優先する`() {
        // スコアだけなら LOW だが、ラベルがあるならそちらを採る。
        assertEquals(
            SeverityLevel.CRITICAL,
            cvss(score = 1.0, severity = "Critical").severityLevel(),
        )
    }

    @Test
    fun `severityLevel_重要度の語は大小文字と前後の空白を無視する`() {
        assertEquals(SeverityLevel.HIGH, cvss(severity = "HIGH").severityLevel())
        assertEquals(SeverityLevel.MEDIUM, cvss(severity = " medium ").severityLevel())
        assertEquals(SeverityLevel.LOW, cvss(severity = "Low").severityLevel())
        assertEquals(SeverityLevel.NONE, cvss(severity = "None").severityLevel())
    }

    @Test
    fun `severityLevel_未知の語はスコア判定へ落ちる`() {
        assertEquals(SeverityLevel.HIGH, cvss(score = 7.5, severity = "重大").severityLevel())
        assertEquals(SeverityLevel.NONE, cvss(severity = "重大").severityLevel())
    }

    @Test
    fun `severityLevelOfTerm_警戒情報の重要度区分を写す`() {
        assertEquals(SeverityLevel.LOW, severityLevelOfTerm("Low"))
        assertEquals(SeverityLevel.HIGH, severityLevelOfTerm("High"))
        assertEquals(SeverityLevel.CRITICAL, severityLevelOfTerm("critical"))
    }

    @Test
    fun `severityLevelOfTerm_未設定と未知の語は深刻度なし`() {
        assertEquals(SeverityLevel.NONE, severityLevelOfTerm(null))
        assertEquals(SeverityLevel.NONE, severityLevelOfTerm("緊急"))
    }

    @Test
    fun `highestScored_最大スコアの1件を選ぶ`() {
        val scores = listOf(cvss(score = 5.0, version = "2.0"), cvss(score = 9.8, version = "3.0"))
        assertEquals(9.8, scores.highestScored()?.score)
    }

    @Test
    fun `highestScored_スコアが無い評価は選ばない`() {
        val scores = listOf(cvss(severity = "High"), cvss(score = 4.2))
        assertEquals(4.2, scores.highestScored()?.score)
    }

    @Test
    fun `highestScored_スコアが無いものだけなら先頭を返す`() {
        val scores = listOf(cvss(severity = "High"), cvss(severity = "Low"))
        assertEquals("High", scores.highestScored()?.severity)
    }

    @Test
    fun `highestScored_空なら選べない`() {
        assertNull(emptyList<CvssScore>().highestScored())
    }

    @Test
    fun `severityBadge_スコアがあれば数値を出す`() {
        assertEquals("[9.8]", severityBadge(cvss(score = 9.8, severity = "Critical")))
    }

    @Test
    fun `severityBadge_スコアが無ければ重要度の語に落とす`() {
        assertEquals("[High]", severityBadge(cvss(severity = "High")))
    }

    @Test
    fun `severityBadge_手がかりが無ければバッジを出さない`() {
        assertNull(severityBadge(null))
        assertNull(severityBadge(cvss()))
        assertNull(severityBadge(cvss(severity = "  ")))
    }
}
