---
description: Kotlin のコーディングスタイル (命名・構造・イディオム・null 許容)
paths:
  - "**/*.kt"
---

# Kotlin Coding Style Rules

[Kotlin 公式コーディング規約](https://kotlinlang.org/docs/coding-conventions.html) に準拠する。
本プロジェクトでは **spotless + ktfmt (`kotlinlangStyle`)** で整形を自動適用するため、
**フォーマット(インデント・改行・空白・末尾カンマ)は手書きで気にせず、保存/コミット前に `./gradlew spotlessApply` に委ねる**。
このドキュメントは、自動整形では担保されない**命名・構造・イディオム**を中心に守るべき指針をまとめる。

## 命名規則

| 対象 | 規則 | 例 |
|------|------|-----|
| パッケージ | 小文字・アンダースコアなし | `com.github.casl0.jvncli.core.network` |
| クラス / オブジェクト / interface | アッパーキャメルケース | `JvnDataSource`, `AlertEntry` |
| 関数 / プロパティ / ローカル変数 | ローワーキャメルケース | `getAlertList`, `severityLabel` |
| `const val` / トップレベル・object の `val` 定数 | SCREAMING_SNAKE_CASE | `BASE_URL`, `ATOM_NS` |
| バッキングプロパティ | アンダースコア接頭辞 | `private val _items` / `val items get() = _items` |
| 頭字語 | 2 文字は全大文字、3 文字以上は先頭のみ大文字 | `IOStream`, `XmlFormatter`, `HttpClient` |

- テスト関数名はバッククォートで日本語・スペース可(本プロジェクトの慣例)。例: `` fun `getAlertList_正常レスポンスをパースする`() ``
- ファイル名: 公開要素が 1 つならその名前、複数なら内容を表すアッパーキャメルケース。`Util` のような無意味語は避ける。
- expect/actual のファイル名はプラットフォーム接尾辞を付ける: `Platform.jvm.kt` / `Platform.mingwX64.kt`(`commonMain` 側は `Platform.kt`)。

## 関数・プロパティ

- **単一式の関数は式本体 (`=`) を使う**: `fun foo() = 1`(`{ return 1 }` にしない)。
- 戻り値が `Unit` の場合は **`: Unit` を省略**。
- **オーバーロードよりデフォルト引数**を優先する。
- bool や同じ型の引数が並ぶ呼び出しは**名前付き引数**を使う: `drawSquare(x = 10, y = 10, fill = true)`。
- プロパティとして表現してよいのは「例外を投げない・安価・状態不変なら同値」を満たす場合のみ。それ以外は関数にする。

## クラスのレイアウト

宣言順は **プロパティ/初期化ブロック → セカンダリコンストラクタ → メソッド → コンパニオンオブジェクト**。
アルファベット順や可視性順に並べず、**関連する処理をまとめる**。オーバーロードは隣接させる。

## 修飾子の順序

`public/internal/...` (可視性) → `expect/actual` → `final/open/abstract/sealed/const` → `override` → `lateinit` → `suspend` → `inner` → `companion` → `inline/value` → `data` …
アノテーションは修飾子より前(原則として別行)。

## イディオム

- **`val` を優先**(`var` は必要な場合のみ)。
- **不変コレクション型を優先**(引数・戻り値は `List`/`Set`/`Map`。`MutableList` 等を露出しない)。
- ループより**高階関数** (`map`/`filter`/`forEach`) を優先。範囲は開区間 `..<` を使う。
- 文字列は**テンプレート** (`"$name ..."`) を優先。単純変数は `${}` を付けない。複数行は `"""..."""` + `trimIndent()`。
- **`if` は二者択一、`when` は 3 分岐以上**。`try`/`if`/`when` は式として使ってよい(`return if (x) a else b`)。
- セミコロンは省略する。
- 関数型・ジェネリック型が繰り返し出るなら `typealias` を検討。名前衝突は `import ... as ...` で回避。
- 拡張関数は積極利用しつつ、可視性(`private` トップレベル等)で API 汚染を抑える。

## null 許容

- **型の nullable は実データに合わせる**。スキーマ/仕様で必須なら非 null、任意なら nullable。
  「来ない null」を防御的に nullable にして増やさない(本リポジトリでは XSD の `minOccurs`/`use` に整合させている)。
- nullable Boolean は `if (value == true)` / `== false` で判定。
- **`!!`(非null表明)は使わない**。null になり得ないなら型を非 null にする。やむを得ず解す場合は
  `?.`/`?:`/`requireNotNull`/`checkNotNull`(メッセージ付き)を使う。テストでは `kotlin.test.assertNotNull`
  (非null値を返す)を用いる。

## ドキュメントコメント (KDoc)

- 公開 API には KDoc を付ける(オーバーライドは除く)。複数行は `/**` を独立行、各行 `*` 始まり。
- `@param`/`@return` は原則使わず、本文に `[paramName]` 参照を埋め込んで説明する。長文が必要な場合のみ使用。

## ライブラリ的コード (`shared` モジュール)

`shared` は再利用ライブラリとして扱う。**公開メンバーの可視性・関数戻り値型・プロパティ型を明示**し、
モジュール外に出さない実装は `internal` にする。プラットフォーム型(Java 由来)を公開する場合は型を明示する。
