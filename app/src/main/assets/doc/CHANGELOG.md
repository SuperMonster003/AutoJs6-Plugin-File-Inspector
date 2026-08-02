******

### 版本历史

******

# v1.0.0

###### 2026/08/02

* `新增` 文件检查器插件, 插件 ID 为 `file-inspector`, 动作 ID 为 `inspect-file`, 引擎为 `explorer-action`, 变体为 `default`
* `新增` 面向任意可读普通文件的单文件只读文件浏览器动作, 输入上限为 8 TiB, 不申请存储或网络权限
* `新增` 单次读取同时计算 CRC32, MD5, SHA-1, SHA-256 和 SHA-512, 支持进度显示和取消
* `新增` 严格规范化和验证预期摘要, 支持算法推断, 显式前缀和等长字节恒定时间比较
* `新增` 64 字节十六进制和 ASCII 头部快照, BOM 检测, 以及 ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX 和 SQLite 3 文件特征识别
* `新增` 纯 JVM 实现且不包含原生库, 通过 `supportedAbis = emptyArray()` 声明 ABI 无限制, 发布单一 ABI 无关 APK, 要求 AutoJs6 宿主构建版本 5268
* `新增` 插件元数据, 界面文本, 使用说明, README 和 CHANGELOG 的多语言资源: 西班牙语/法语/俄语/阿拉伯语/日语/韩语/英语/简体中文/香港繁体/台湾繁体
* `依赖` 附加 AndroidX Lifecycle ViewModel 版本 2.9.4
