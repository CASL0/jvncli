---
description: Gitのブランチ戦略・コミットメッセージ・リリースのルール
paths:
  - "**/*"
---

# Git Rules

## ブランチ戦略（GitHub Flow）

- `main` ブランチは常にリリース可能な状態を保つ
- 作業は必ず `main` から新しいブランチを切って行う
- ブランチ名は `<type>/<description>` の形式にする（例: `feat/jvn-search`, `fix/cvss-parse-error`）
- 作業完了後は Pull Request を作成して `main` にマージする
- マージ後はブランチを削除する

## コミットメッセージ（Conventional Commits）

```
<type>[optional scope]: <description>

[optional body]

[optional footer]
```

### type一覧

| type | 用途 | semver |
|------|------|--------|
| `feat` | 新機能 | minor |
| `fix` | バグ修正 | patch |
| `perf` | パフォーマンス改善 | patch |
| `docs` | ドキュメントのみの変更 | - |
| `style` | コードの意味に影響しない変更（フォーマット等） | - |
| `refactor` | バグ修正でも機能追加でもないコード変更 | - |
| `test` | テストの追加・修正 | - |
| `build` | ビルドシステムや依存関係の変更（Gradle 依存更新など） | - |
| `ci` | CI設定の変更 | - |
| `chore` | その他の雑多な変更（補助ツール等） | - |
| `revert` | 以前のコミットの取り消し | - |

### BREAKING CHANGE

破壊的変更は footer に `BREAKING CHANGE: <description>` を記載するか、type の後に `!` を付ける。

```
feat!: 検索コマンドの引数仕様を刷新

BREAKING CHANGE: `jvn search` の位置引数を廃止し `--keyword` オプションに変更しました
```

### コミット例

```
feat(search): JVN ID による脆弱性情報の検索コマンドを追加
fix(parser): CVSS スコアのパースに失敗する問題を修正
build: Kotlin を 2.x へアップデート
chore: ktlint フックを追加
```

## コミット前のローカル検証

- コミットする前に、手動で `./gradlew spotlessApply`（フォーマット適用）と変更したモジュールのテスト（例: `./gradlew :shared:jvmTest`）を実行し、いずれも通ることを確認してからコミットする。
- コミットは **bisect しやすい論理単位** で行う（各コミット時点でビルド・テストが通る状態を保つ）。

## semantic-release

`main` へのマージ時に semantic-release がコミット履歴からバージョンを決定し、自動でリリースする運用を想定する。

| コミットに含まれる type | バージョン変化 |
|---|---|
| `BREAKING CHANGE` | major (x.0.0) |
| `feat` | minor (0.x.0) |
| `fix`, `perf` | patch (0.0.x) |
| それ以外 | リリースなし |
