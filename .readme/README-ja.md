<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>ファイルマネージャープラグイン. ファイルシグネチャを検査して暗号学的チェックサムを検証</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語に対応しています:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### 概要

******

ファイルインスペクターは, ファイルマネージャーから一時的な読み取り専用content URIアクセスで渡された任意の読み取り可能な通常ファイルを検査します. 元ファイルを変更せずに, メタデータ, 先頭64バイトのヘッダー, 複数のダイジェストを表示します.

******

### 機能

******

- 共有 `org.autojs.plugin.EXPLORER_ACTION` プロトコルを通じて, 単一ファイル用の読み取り専用Explorerオーバーフローアクションを登録します.
- ソースを1回だけ読み取り, CRC32, MD5, SHA-1, SHA-256, SHA-512を同時に計算し, 進捗表示とキャンセルに対応します.
- 宣言サイズ, 実サイズ, MIMEタイプ, 拡張子, 先頭64バイトの16進数とASCII表示, BOM, 認識されたファイルシグネチャを表示します.
- 期待するダイジェストを厳密に正規化し, 有効な長さまたは明示的な接頭辞からアルゴリズムを判定し, 不一致位置で早期終了せず同じ長さのバイトを比較します.
- 個別のチェックサムをコピーするか, 完全な検査レポートをコピーまたは共有できます.

******

### 検査対象

******

バージョン1は任意の読み取り可能な通常ファイルのダイジェストを計算し, オフセット0にある次の固定ヘッダーシグネチャを認識します:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### プラグインインターフェース

******

ホストは次の識別子でプラグインを検出して実行します:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: file-inspector
engine: explorer-action
variant: default
Explorer action id: inspect-file
MIME type: */*
required host build: 5268
```

バージョン1では, メインのファイルマネージャーで単一ファイル用の読み取り専用オーバーフローアクションを利用できます.

ホストビルド5268以降が必要です.

******

### セキュリティ

******

プラグインはストレージ権限とネットワーク権限を要求しません. ホストは対象content URIへの一時的な読み取り専用アクセスだけを許可します. プラグインは正確なIntentアクション, URI, ClipData, ファイル名, MIMEタイプ, 宣言サイズを検証し, 書き込み権限と永続権限を拒否し, ソースへ書き込みません. 宣言サイズと実サイズの不一致, および8 TiBを超える入力を拒否します. ファイルのバイトは上限付きバッファーで処理され, レポートには64バイトのヘッダースナップショットだけが保持されます.

******

### 安全制限

******

- 最大入力サイズ: `8 TiB`.
- ヘッダースナップショット: `64 bytes`.
- 期待するダイジェスト文字列の上限: `512 ASCII characters`.
- 1回のアクションにつき対象ファイルは1つです.
- シグネチャ検出はオフセット0の固定バイトだけを使い, 完全な形式検証ではありません.
- MD5とSHA-1は旧式ダイジェストとして表示され, 衝突耐性のあるセキュリティ証明として扱うべきではありません.

******

### リリース履歴

******

# v1.0.1

###### 2026/08/08

* `修正` プラグインセンターで有効化するとサービスバインディングがnullになる問題
* `改善` より明確で簡潔なプラグイン名, 説明, ユーザードキュメント

# v1.0.0

###### 2026/08/02

* `機能` プラグインID `file-inspector`, アクションID `inspect-file`, エンジン `explorer-action`, バリアント `default` を持つFile Inspectorプラグイン
* `機能` 任意の読み取り可能な通常ファイルを対象とする単一ファイル用の読み取り専用Explorerアクション, 入力上限8 TiB, ストレージ権限とネットワーク権限は不要
* `機能` 1回の読み取りによるCRC32, MD5, SHA-1, SHA-256, SHA-512の同時計算, 進捗表示とキャンセル
* `機能` アルゴリズム推定, 明示的な接頭辞, 同じ長さのバイトの定数時間比較を使う期待ダイジェストの厳密な正規化と検証
* `機能` 64バイトの16進数とASCIIヘッダースナップショット, BOM検出, ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3シグネチャ認識
* `機能` スペイン語, フランス語, ロシア語, アラビア語, 日本語, 韓国語, 英語, 簡体字中国語, 香港繁体字中国語, 台湾繁体字中国語のメタデータ, UIテキスト, 使用説明, README, 変更履歴
* `依存関係` AndroidX Lifecycle ViewModel バージョン 2.9.4 を追加

##### その他のリリース

* [CHANGELOG-ja.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Releaseビルド:

```powershell
.\gradlew.bat :app:assembleRelease
```

ビルドパラメーターは `version.properties` から取得します. 現在の最小SDKは24, ターゲットSDKは36です.

******

### リソース構成

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` はプラグインのメタデータとUIテキストをローカライズします. `plugin_instruction.md` はホストが表示する説明を提供します. `.python/generate_markdown.py` はJSONソースからローカライズされたREADMEと変更履歴を生成します.

******

### リンク

******

- AutoJs6ドキュメント: https://docs.autojs6.com
- Androidの安全なファイル共有: https://developer.android.com/training/secure-file-sharing
