<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-file-inspector-ic-launcher" border="0" width="128" />
  </p>

  <p>为 AutoJs6 文件浏览器提供只读文件特征, 头部信息和校验和验证</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/.readme/README-ar.md)

******

### 简介

******

AutoJs6 文件检查器插件通过宿主临时授予的只读 content URI 检查任意可读普通文件. 插件报告文件元数据, 前 64 字节头部和多个摘要, 不会修改源文件.

******

### 功能

******

- 通过共享的 `org.autojs.plugin.EXPLORER_ACTION` 协议注册单文件只读文件浏览器溢出菜单动作.
- 只读取源文件一次, 同时计算 CRC32, MD5, SHA-1, SHA-256 和 SHA-512, 并支持进度显示和取消.
- 显示声明大小, 实际大小, MIME 类型, 扩展名, 前 64 字节十六进制和 ASCII 头部, BOM 和识别出的文件特征.
- 严格规范化预期摘要输入, 根据有效长度或显式前缀识别算法, 并以不因不匹配位置提前结束的方式比较等长字节.
- 支持复制单个校验和, 以及复制或分享完整检查报告.

******

### 检查的数据

******

版本 1 可为任意可读普通文件计算摘要, 并识别以下位于偏移 0 的固定头部特征:

```text
ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3
```

******

### 插件接口

******

AutoJs6 使用以下标识发现并执行插件:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: file-inspector
engine: explorer-action
variant: default
Explorer action id: inspect-file
MIME type: */*
required host build: 5268
supported ABIs: unrestricted (supportedAbis = emptyArray())
```

版本 1 仅提供 AutoJs6 主文件浏览器中的单文件只读溢出菜单动作.

插件完全使用 JVM 实现, 不包含原生库. 插件声明 `supportedAbis = emptyArray()`, 并以单一 ABI 无关 APK 发布. 需要 AutoJs6 宿主构建版本 5268 或更高版本.

******

### 安全性

******

插件不请求存储或网络权限. 宿主仅授予目标 content URI 临时只读访问权限. 插件验证确切的 Intent 动作, URI, ClipData, 文件名, MIME 类型和声明大小, 拒绝写入或持久授权, 并且绝不写入源文件. 声明大小与实际大小不一致或输入大于 8 TiB 时将被拒绝. 文件字节使用有界缓冲区处理, 报告仅保留 64 字节头部快照.

******

### 安全限制

******

- 最大输入大小: `8 TiB`.
- 头部快照: `64 bytes`.
- 预期摘要文本上限: `512 ASCII characters`.
- 每次动作仅处理 1 个目标文件.
- 文件特征检测仅依据偏移 0 的固定字节, 不代表完整格式验证.
- MD5 和 SHA-1 以旧式摘要显示, 不应视为具备抗碰撞能力的安全证明.

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

##### 查看更多版本

* [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

发布构建:

```powershell
.\gradlew.bat :app:assembleRelease
```

构建参数来自 `version.properties`. 当前最低 SDK 为 24, 目标 SDK 为 36.

******

### 资源布局

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 为插件元数据和界面文本提供本地化. `plugin_instruction.md` 提供宿主显示的说明. `.python/generate_markdown.py` 根据 JSON 源文件生成多语言 README 和更新日志.

******

### 链接

******

- AutoJs6 文档: https://docs.autojs6.com
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing
