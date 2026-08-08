<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>檔案管理器外掛程式. 檢查檔案簽章並驗證密碼學檢查碼</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 (台灣) [zh-Hant-TW] # 目前
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### 簡介

******

檔案檢查器透過檔案管理器暫時授予的唯讀 content URI 檢查任何可讀的一般檔案. 外掛程式會報告檔案中繼資料, 前 64 位元組標頭和多個摘要, 而不會修改來源檔案.

******

### 功能

******

- 透過共用的 `org.autojs.plugin.EXPLORER_ACTION` 通訊協定註冊單一檔案唯讀檔案瀏覽器溢出選單動作.
- 只讀取來源檔案一次, 同時計算 CRC32, MD5, SHA-1, SHA-256 和 SHA-512, 並支援進度顯示和取消.
- 顯示宣告大小, 實際大小, MIME 類型, 副檔名, 前 64 位元組十六進位和 ASCII 標頭, BOM 和已辨識的檔案特徵.
- 嚴格正規化預期摘要輸入, 根據有效長度或明確前綴辨識演算法, 並以不因不相符位置提前結束的方式比較等長位元組.
- 支援複製單一校驗和, 以及複製或分享完整檢查報告.

******

### 檢查的資料

******

版本 1 可為任何可讀的一般檔案計算摘要, 並辨識以下位於偏移 0 的固定標頭特徵:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### 外掛程式介面

******

主程式使用以下識別資訊探索和執行外掛程式:

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

版本 1 在主檔案管理器中提供單一檔案唯讀溢出選單動作.

需要主程式建置版本 5268 或更新版本.

******

### 安全性

******

外掛程式不要求儲存空間或網路權限. 主程式只授予目標 content URI 暫時唯讀存取權. 外掛程式會驗證確切的 Intent 動作, URI, ClipData, 檔案名稱, MIME 類型和宣告大小, 拒絕寫入或持久授權, 且絕不寫入來源檔案. 宣告大小與實際大小不相符或輸入大於 8 TiB 時會被拒絕. 檔案位元組使用有界緩衝區處理, 報告只保留 64 位元組標頭快照.

******

### 安全限制

******

- 最大輸入大小: `8 TiB`.
- 標頭快照: `64 bytes`.
- 預期摘要文字上限: `512 ASCII characters`.
- 每次動作只處理 1 個目標檔案.
- 檔案特徵偵測只依據偏移 0 的固定位元組, 不代表完整格式驗證.
- MD5 和 SHA-1 會顯示為舊式摘要, 不應視為具抗碰撞能力的安全證明.

******

### 版本記錄

******

# v1.0.1

###### 2026/08/08

* `修復` 外掛程式中心啟用時出現空服務綁定的問題
* `優化` 外掛程式名稱, 描述和使用者文件更簡潔自然

# v1.0.0

###### 2026/08/02

* `新增` 檔案檢查器外掛程式, 外掛程式 ID 為 `file-inspector`, 動作 ID 為 `inspect-file`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 適用於任何可讀一般檔案的單一檔案唯讀檔案瀏覽器動作, 輸入上限為 8 TiB, 不要求儲存空間或網路權限
* `新增` 單次讀取同時計算 CRC32, MD5, SHA-1, SHA-256 和 SHA-512, 支援進度顯示和取消
* `新增` 嚴格正規化和驗證預期摘要, 支援演算法推斷, 明確前綴和等長位元組恆定時間比較
* `新增` 64 位元組十六進位和 ASCII 標頭快照, BOM 偵測, 以及 ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX 和 SQLite 3 檔案特徵辨識
* `新增` 外掛程式中繼資料, 介面文字, 使用說明, README 和 CHANGELOG 的多語言資源: 西班牙語/法語/俄語/阿拉伯語/日語/韓語/英語/簡體中文/香港繁體/台灣繁體
* `相依性` 附加 AndroidX Lifecycle ViewModel 版本 2.9.4

##### 查看更多版本

* [CHANGELOG-zh-Hant-TW.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

發行建置:

```powershell
.\gradlew.bat :app:assembleRelease
```

建置參數來自 `version.properties`. 目前最低 SDK 為 24, 目標 SDK 為 36.

******

### 資源配置

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 為外掛程式中繼資料和介面文字提供本地化. `plugin_instruction.md` 提供主程式顯示的說明. `.python/generate_markdown.py` 根據 JSON 來源檔案產生多語言 README 和更新記錄.

******

### 連結

******

- AutoJs6 文件: https://docs.autojs6.com
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing
