******

### 版本記錄

******

# v1.0.0

###### 2026/08/02

* `新增` 檔案檢查器外掛程式, 外掛程式 ID 為 `file-inspector`, 動作 ID 為 `inspect-file`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 適用於任何可讀一般檔案的單一檔案唯讀檔案瀏覽器動作, 輸入上限為 8 TiB, 不要求儲存空間或網絡權限
* `新增` 單次讀取同時計算 CRC32, MD5, SHA-1, SHA-256 和 SHA-512, 支援進度顯示和取消
* `新增` 嚴格正規化和驗證預期摘要, 支援演算法推斷, 明確前綴和等長位元組恆定時間比較
* `新增` 64 位元組十六進位和 ASCII 標頭快照, BOM 偵測, 以及 ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX 和 SQLite 3 檔案特徵辨識
* `新增` 純 JVM 實作且不包含原生程式庫, 透過 `supportedAbis = emptyArray()` 宣告 ABI 無限制, 發佈單一 ABI 無關 APK, 要求 AutoJs6 主程式組建版本 5268
* `新增` 外掛程式中繼資料, 介面文字, 使用說明, README 和 CHANGELOG 的多語言資源: 西班牙語/法語/俄語/阿拉伯語/日語/韓語/英語/簡體中文/香港繁體/台灣繁體
* `依賴` 附加 AndroidX Lifecycle ViewModel 版本 2.9.4
