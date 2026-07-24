# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

MyJVN API (`https://jvndb.jvn.jp/`) から脆弱性情報・注意警戒情報を取得して表示する **TUI アプリケーション**。
Kotlin Multiplatform (KMP) で JVM / Linux (x64) / macOS (arm64) / Windows (mingwX64) 向けにビルドする。

## 開発コマンド

Gradle ラッパー (`./gradlew`) を使う。Windows でも Bash ツールから `./gradlew` で実行できる。

| 目的 | コマンド |
|------|---------|
| フォーマット適用 (コミット前に必須) | `./gradlew spotlessApply` |
| フォーマット検証 (CI と同じ) | `./gradlew spotlessCheck` |
| JVM テスト実行 | `./gradlew :shared:jvmTest` |
| 単一テスト実行 | `./gradlew :shared:jvmTest --tests "*JvnDataSourceImplTest*"` |
| カバレッジ検証 (行 80% 下限) | `./gradlew :shared:koverVerify` |
| カバレッジ XML レポート (CI と同じ) | `./gradlew :shared:koverXmlReport` |
| TUI を JVM で起動 | `./gradlew :app:jvmRun` |
| ビルド全体 | `./gradlew build` |

- CI (`.github/workflows/`) は push 時に `spotlessCheck` と `:shared:koverXmlReport` を実行する。**コミット前に `spotlessApply` とテストをローカルで通すこと** (詳細は [.claude/rules/git.md](.claude/rules/git.md))。
- テストとカバレッジ計測は `shared` モジュールに集約されている (`app` はエントリポイントと HTTP エンジン選択のみ)。

### Bash 実行の注意

- **Bash ツールは 1 コマンドずつ実行する。`;` / `&&` / `|` で複数コマンドを連結しない**。連結すると全体が 1 つの複合コマンド扱いになり、許可リスト（settings.json の `permissions.allow`）のプレフィックスマッチが成立せず、許可済みコマンドでも確認プロンプトが出る。
- 複数の情報を確認したいときは、コマンドを分けてそれぞれ実行する（並列実行は可）。
- ファイル一覧・検索・閲覧は `ls`/`grep`/`cat` ではなく専用の Glob/Grep/Read ツールを優先する。

## モジュール構成

- **`:shared`** — 再利用ライブラリ。ネットワーク・データ変換・状態管理・TUI 描画のすべてを含む。公開 API の可視性・型を明示し、実装詳細は `internal` にする。
- **`:app`** — 実行可能バイナリ。エントリポイント ([Main.kt](app/src/commonMain/kotlin/com/github/casl0/jvncli/Main.kt)) と**プラットフォームごとの HTTP エンジン選択のみ**を担う。

## アーキテクチャ / コーディング規約

- アーキテクチャ (データフロー `TUI → Presenter → JvnDataSource → JvnApi → MyJVN`、各レイヤの責務、HTTP エンジンの注入境界、新しい MyJVN API を追加する手順) は [.claude/rules/architecture.md](.claude/rules/architecture.md) に従う。
- Kotlin のスタイルは [.claude/rules/kotlin.md](.claude/rules/kotlin.md) に従う (spotless + ktfmt で整形自動化。命名・イディオム・null 許容の指針あり)。`!!` 禁止、`val` 優先、不変コレクション型を露出。
- Git 運用 (GitHub Flow・Conventional Commits・semantic-release) は [.claude/rules/git.md](.claude/rules/git.md) に従う。
