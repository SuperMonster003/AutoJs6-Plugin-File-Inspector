<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>AutoJs6 ファイルマネージャープラグイン: 1回の読み取りで7種類のチェックサムを計算し, 公開値を貼り付けるだけで完全性を即確認, ファイルの本当の形式もひと目で分かります</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### 言語 (Languages)

このREADMEは次の言語で利用できます:

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

### 概要

File Inspectorは, AutoJs6ファイルマネージャーの拡張プラグインです. 任意のファイルで「ファイルを検査」を選ぶと, プラグインはファイルを1回だけ読み取り, CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512の7種類のチェックサムを同時に計算します. さらにファイルの本当の形式シグネチャ, サイズ, 先頭バイトも1画面に表示します. 処理はすべて読み取り専用で, ストレージやネットワークの権限は要求せず, 元のファイルは一切変更しません.

レポート画面には「完全性チェック」欄があります. 配布元が公開しているチェックサムを貼り付けると, プラグインがアルゴリズムを自動判別し, 一致 / 不一致の結論をはっきり示します. 長い16進文字列を目で見比べる必要はもうありません. 各チェックサムとヘッダースナップショットは個別にコピーでき, レポート全体をコピーまたはシステム共有する前に Markdown / JSON を選べます.

### 特長

- 1回の読み取りで全部: ファイルは順次1回だけ読み取られ, その間に7種類のチェックサムをまとめて計算します. 大きなファイルでもアルゴリズムごとに待つ必要はありません.
- 貼り付けて即照合: 16進数とフィンガープリント, Base64, SRI, `md5sum` / `sha256sum` の完全な出力行に対応し, アルゴリズムを自動判別します. 貼り付けたファイル名が異なる場合は警告します.
- 本当の形式を見抜く: 有界の先頭領域と固定オフセットから26種類の一般的なシグネチャを認識し, ZIPベースのAPK/JAR/Office文書には用途のヒントを表示. 印字可能文字率とエントロピーからテキストかバイナリかも推定します.
- ヘッダーをひと目で: ファイルの先頭 64 バイトを16進 + ASCII対照ビューで表示し, UTF-8 / UTF-16 / UTF-32のバイトオーダーマーク (BOM) も検出します.
- サイズの二重確認: ファイルマネージャーが申告したサイズと実際に読み取ったサイズを両方表示します. 読み取り中にファイルが変更されると即座にエラーとなり, 不完全なダウンロードや書き込み途中のファイルに気づけます.
- 進捗を制御: 大きなファイルでは読取済みバイト数, 読取速度, 推定残り時間を表示し, いつでもキャンセルできます. 失敗した検査はワンタップで再試行できます.
- 結果をすぐ活用: チェックサムとヘッダースナップショットを個別にコピーし, 画面の全結果を Markdown / JSON でクリップボードまたはシステム共有へ書き出せます. ファイルは作成しません.
- 旧式アルゴリズムを明示: MD5とSHA-1にはLegacyバッジが付き, もはや安全性の証明には適さないことを注意喚起します.

### 使い方

1. 同じ公式ReleaseからAPKと同名の`.apk.sha256`チェックファイルをダウンロードし, APKをインストールしてAutoJs6のプラグインセンターで有効化します (AutoJs6バージョンコード 5268 以降が必要).
2. 更新前は, インストール済みのFile InspectorでダウンロードしたAPKを検査します. 初回インストールではAPKを残し, プラグインを有効化してから検査してください. チェックファイルの64文字のSHA-256を貼り付け, 緑の一致を確認します. 両方のファイルは同じ信頼できるHTTPS Releaseページから取得してください.
3. AutoJs6のファイルマネージャーを開き, 検査したいファイルを探します. 通常のファイルなら種類は問いません.
4. ファイルのメニューから「ファイルを検査」を選ぶと, すぐに読み取りが始まり進捗が表示されます. 完了するとチェックサム, 形式シグネチャ, ヘッダーバイトが表示されます.
5. 完全性を確認するには, 公開されているチェックサムを「完全性チェック」欄に貼り付けて「検証」をタップします. 緑は一致, 赤は不一致です.
6. チェックサム横またはヘッダースナップショット下のコピーボタンで個別の値を取得できます. Markdown / JSON を選んでレポート全体をコピーまたは共有し, 戻るボタンでファイルマネージャーに戻ります.

> チェックサム入力は素の16進, `sha256: <値>` や `MD5=<値>` などの接頭辞, `AB:CD:EF` 形式, CRC32用の `0x`, 標準Base64, `sha256-<base64>` 形式のSRI, coreutilsの完全な`<16進>  <ファイル>`または`<16進> *<ファイル>`行に対応します. 大文字小文字と前後の空白は無視され, 長さが一意ならアルゴリズムを自動判別し, ファイル名が異なる場合は警告します.

### 認識できる形式シグネチャ

チェックサム計算は読み取り可能な通常ファイルすべてで使えます. それに加えて, 現在のバージョンは有界の先頭領域または固定オフセットにある次の形式シグネチャを認識します:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

シグネチャ検出には有界サンプルと, オフセット257のTARマジック, オフセット4のISO-BMFF `ftyp`, PEヘッダーポインターなどの構造フィールドを使います. これは素早いヒントであり完全な形式検証ではなく, 印字可能文字率とエントロピーもヒューリスティックな推定です. 該当しないファイルは「不明」と表示されますが, チェックサムは通常どおり計算されます.

### よくある質問

#### このプラグインはどんなときに役立ちますか?

定番はダウンロードの検証です. インストーラーやファームウェア, ドキュメントを入手したら, 配布元が公開しているSHA-256などのチェックサムを貼り付けるだけで, ファイルが完全か, 改ざんされていないかがすぐ分かります. 拡張子が偽装されたファイルの本当の形式を確かめたり, 任意のファイルのチェックサムを手早く作って保管・比較したりするのにも便利です.

#### チェックサムが「一致」すればファイルは安全ですか?

一致が証明するのは, そのチェックサムの公開対象だったファイルとバイト単位で同一だということだけです. 信頼できるかどうかは, チェックサムの入手元次第です. 公式サイトがHTTPSで公開しているSHA-256またはSHA-512を優先してください. MD5とSHA-1は意図的な衝突を作れるため, プラグインはLegacyと表示しています. 安全性の証明として扱わないでください.

#### 検査できないファイルがあるのはなぜですか?

主な原因: ファイルが 8 TiB の上限を超えている, 読み取り中に他のアプリがファイルを変更して実サイズが申告サイズと一致しなくなった, ホストが付与した読み取り専用権限が失効した, など. エラーメッセージに具体的な理由が表示され, 「再試行」で再検査できます.

#### 大きなファイルの検査は遅くありませんか?

プラグインはファイルを順次1回読み取るだけで7種類のチェックサムをまとめて計算するため, 所要時間はストレージの読み取り速度でほぼ決まり, アルゴリズムの数に比例して遅くなることはありません. 進捗は常時表示され, いつでもキャンセルできます.

### 権限とセキュリティ

プラグインはストレージとネットワークの権限を要求せず, ホストが一時的に付与する読み取り専用content URIを通じて, ユーザーが選んだその1ファイルにしかアクセスできません. 権限は検査終了とともに失効し, 他のファイルには届きません. ファイルマネージャーからのリクエストは, アクションID, プロトコルバージョン, content URIの形式, ファイル名, MIMEタイプ, 申告サイズ, 読み取り専用grantを項目ごとに検証し, 書き込みや永続化のgrantを伴うリクエストは即座に拒否します. ファイルは常に読み取り専用ストリームとして処理され, メモリに保持するのは有界バッファ, 解析用の先頭 4096 バイト, 表示用の先頭 64 バイト, 4バイトのPEシグネチャ窓だけで, 元のファイルは変更されません.

検査を常に予測可能に保つため, プラグインには次の上限があります:

- 1ファイルの上限は 8 TiB で, 1回のアクションで処理する対象ファイルは1つだけです.
- 解析は先頭 4096 バイトと4バイトのPEシグネチャ窓だけを採取し, 表示ヘッダーは 64 バイトのままです. チェックサム入力は 512 文字までで, 非ASCII文字はcoreutilsのファイル名部分だけに使用できます.
- 実際に読み取ったサイズが申告サイズと異なる場合, ファイルが変更されたと判定し, 検査はメッセージ付きで失敗します.
- チェックサムの照合は同じ長さの値どうしの定数時間比較で行います. MD5とSHA-1は旧データの照合専用で, 衝突耐性の証明として扱ってはいけません.

### プラグインインターフェース

ホスト (AutoJs6) は次の識別子でプラグインを検出して呼び出します. プラグインやホストの開発者向けの参考情報です:

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

現在のバージョンは, ホストのファイルマネージャーに単一ファイル対象の読み取り専用オーバーフローメニューアクションを追加します. 元ファイルの変更もディレクトリの列挙も行いません. プラグインが未導入または無効の場合, ホストは既定の動作へ自動的にフォールバックします.

### Roadmap

実装済みの機能は上記とRoadmapのチェック済み項目のとおりです. 追加providerを要するダイジェストアルゴリズム, 一括検証, ホストプロトコル拡張などの計画はRoadmapで管理しており, 未チェック項目は現在の機能ではありません.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### リリース履歴

#### v1.0.1

_2026/08/08_

- `修正` プラグインセンターで有効化した後にホストがサービスへバインドできない問題を修正. 有効化直後から「ファイルを検査」アクションが使えるようになりました
- `改善` プラグインの名称と説明を簡潔にし, ユーザー向けドキュメントをより自然で読みやすい表現に調整

#### v1.0.0

_2026/08/02_

- `ヒント` 初の公開バージョン. AutoJs6バージョンコード 5268 以降が必要です
- `機能` AutoJs6ファイルマネージャーのファイルメニューに読み取り専用アクション「ファイルを検査」を追加. あらゆる種類の通常ファイルに対応 (プラグインID `file-inspector`, アクションID `inspect-file`)
- `機能` 1回の順次読み取りで CRC32, MD5, SHA-1, SHA-256, SHA-512 の5種類のチェックサムをまとめて計算. ライブ進捗表示とキャンセル・再試行に対応
- `機能` 期待チェックサムの貼り付けによる完全性検証に対応: 長さまたは `sha256:` などの接頭辞からアルゴリズムを自動判別し, `AB:CD:EF` 形式の区切りとCRC32の `0x` 接頭辞も受け付け, 同じ長さの値どうしを定数時間で比較
- `機能` レポートにファイル名, MIMEタイプ, 拡張子, 申告サイズと実サイズ, 先頭 64 バイトの16進 + ASCIIスナップショット, UTF BOM検出を表示
- `機能` 先頭のシグネチャバイトから10種類の一般的な形式を認識: ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
- `機能` 各チェックサムの個別コピー, レポート全体のコピーとシステム共有に対応. MD5とSHA-1にはLegacyバッジを表示
- `機能` プラグインはストレージ・ネットワーク権限を要求せず, ホストが一時付与する読み取り専用content URIのみでファイルを読み取り. 1ファイル上限 8 TiB
- `機能` インターフェース文言, 使用説明, README, CHANGELOGを10言語で同梱: 簡体字中国語, 繁体字中国語 (香港), 繁体字中国語 (台湾), 英語, フランス語, スペイン語, 日本語, 韓国語, ロシア語, アラビア語
- `依存関係` AndroidX Lifecycle ViewModel 2.9.4 を導入

##### 全履歴

- [CHANGELOG-ja.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

### ビルド

```powershell
.\gradlew.bat :app:assembleDebug
```

Releaseビルド:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

ビルドと署名のパラメーターはversion.propertiesとsign.propertiesで管理されます. 現在の最小要件はAndroid 7.0 (SDK 24), ターゲットSDKは36です.

README, CHANGELOG, res/raw*/plugin_instruction.md のプラグイン内説明は, .readme/ と .changelog/ のJSON言語ソースとテンプレートから .python/generate_markdown.py が生成します (10言語). 変更時は生成済みMarkdownを直接編集せず, JSONソースを編集してスクリプトを再実行してください.

### 関連リンク

- AutoJs6ドキュメント: https://docs.autojs6.com
- Androidの安全なファイル共有: https://developer.android.com/training/secure-file-sharing
