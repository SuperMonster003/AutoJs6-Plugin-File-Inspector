<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="File Inspector" width="128" />
  </p>

  <h1>File Inspector</h1>

  <p>AutoJs6 文件管理器插件: 一次读取算出七种校验和, 粘贴官方校验值即刻核对完整性, 顺带看清文件真实格式</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml"><img alt="CI" src="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/actions/workflows/ci.yml/badge.svg"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-File-Inspector?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-File-Inspector?color=534BAE&label=License"/></a>
  </p>
</div>

### 语言 (Languages)

README 提供以下语言版本:

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

### 项目简介

文件检查器 (File Inspector) 是 AutoJs6 文件管理器的配套插件. 在文件管理器中对任意文件选择 “检查文件”, 插件只把文件读取一遍, 就同时算出 CRC32, MD5, SHA-1, SHA-224, SHA-256, SHA-384, SHA-512 七种校验和, 并展示文件的真实格式特征、大小与头部字节, 一屏看全. 全程只读, 不申请存储与网络权限, 绝不修改原文件.

报告页还内置 “完整性校验”: 把发布方公布的校验和粘贴进输入框, 插件自动识别算法并直接给出 “匹配 / 不匹配” 结论, 免去肉眼逐位比对长串十六进制的麻烦. 每条校验和与文件头快照均可独立复制; 复制整份报告或通过系统面板分享前, 可选择结构化 Markdown 或 JSON.

### 功能亮点

- 读一遍, 全都有: 文件只被顺序读取一次, 同时算出全部七种校验和, 大文件不必按算法分别等待多轮计算.
- 粘贴即核对: 支持十六进制与指纹分隔、Base64、SRI, 以及完整的 `md5sum` / `sha256sum` 输出行; 自动识别算法, 粘贴的文件名不同时给出警告.
- 识破真实格式: 通过有界头部与固定偏移字节识别 26 种常见特征, 为基于 ZIP 的 APK/JAR/Office 文档补充用途提示, 并根据可打印字符比例与熵值估算文本或二进制内容.
- 头部一览: 以 “十六进制 + ASCII” 对照样式展示文件前 64 字节, 并检测 UTF-8 / UTF-16 / UTF-32 的 BOM 字节序标记.
- 大小双重核对: 同时显示文件管理器声明的大小与实际读取的大小, 文件在读取期间被改动会立即报错, 帮助发现下载不完整或正在写入的文件.
- 进度可控: 大文件读取时显示实时字节数、读取速度与预计剩余时间, 随时可以取消; 失败后可一键重试.
- 结果即取即用: 校验和与文件头快照均可独立复制, 屏幕上的完整结果可选 Markdown 或 JSON 后复制到剪贴板或通过系统分享面板发送, 全程不创建文件.
- 旧算法明示: MD5 与 SHA-1 标注 Legacy 徽标, 提醒它们已不适合作为安全证明.

### 使用方法

1. 从同一个官方 Release 下载 APK 及其同名 `.apk.sha256` 校验文件, 安装 APK, 然后在 AutoJs6 的插件中心启用它 (需要 AutoJs6 版本代码 5268 或更高).
2. 升级前, 用已安装的文件检查器检查新下载的 APK; 首次安装时请保留 APK, 启用插件后再检查它. 把校验文件中的 64 位 SHA-256 粘贴到完整性校验框, 只有显示绿色匹配才通过; 两个文件都应来自同一个可信的 HTTPS Release 页面.
3. 打开 AutoJs6 的文件管理器, 找到想检查的文件; 任意类型的普通文件均可.
4. 在该文件的菜单中选择 “检查文件”, 插件立即开始读取并显示进度, 完成后即可看到校验和、格式特征与头部字节.
5. 需要核对完整性时, 把发布方公布的校验和粘贴到 “完整性校验” 输入框并点按 “校验”: 绿色表示匹配, 红色表示不匹配.
6. 点按校验和旁或文件头下方的复制按钮可取得单项结果; 选择 Markdown 或 JSON 后可复制或分享完整报告, 按返回键回到文件管理器.

> 校验和输入支持纯十六进制、`sha256: <值>` 或 `MD5=<值>` 等算法前缀、`AB:CD:EF` 式指纹分隔、CRC32 的 `0x` 前缀、标准 Base64、`sha256-<base64>` 式 SRI, 以及 coreutils 的完整 `<十六进制>  <文件名>` 或 `<十六进制> *<文件名>` 行. 自动忽略大小写与首尾空白; 摘要长度唯一时自动识别算法, 粘贴的文件名不同时显示警告.

### 可识别的格式特征

校验和计算适用于任意可读的普通文件; 在此基础上, 当前版本还能识别以下有界头部或固定偏移格式特征:

```text
ZIP, 7z, RAR 4, RAR 5, GZIP, XZ, BZIP2, Zstandard, LZ4, TAR, PDF, PNG, JPEG, GIF87a, GIF89a, WebP, MP4 / ISO-BMFF, EBML / Matroska, ELF, DEX, Java Class, Mach-O, PE, SQLite 3, WOFF, WOFF2
```

格式识别使用有界样本以及 TAR 偏移 257、ISO-BMFF 偏移 4 的 `ftyp`、PE 头指针等结构字段, 仅用于快速提示, 不构成完整的格式合法性验证; 可打印比例与熵值同样属于启发式估算. 未命中特征的文件显示为 “未知”, 校验和仍然正常计算.

### 常见问题

#### 这个插件适合在什么时候用?

最典型的场景是核对下载文件: 从网上获取安装包、固件或文档后, 把发布方公布的 SHA-256 等校验和粘贴进插件, 立即知道文件是否完整、有没有被篡改. 它也适合查看被改过扩展名的文件的真实格式, 或为任意文件快速生成校验和以便存档与比对.

#### 校验和 “匹配” 能说明文件是安全的吗?

“匹配” 只说明文件内容与公布该校验和的那份文件完全一致; 文件是否可信取决于校验和的来源是否可信. 请优先使用官方渠道通过 HTTPS 公布的 SHA-256 或 SHA-512. MD5 与 SHA-1 已能被人为构造碰撞, 插件将其标注为 Legacy, 不应作为安全证明.

#### 为什么有些文件无法检查?

常见原因: 文件超过 8 TiB 上限; 文件在读取过程中被其他应用改动, 导致实际大小与声明大小不一致; 或宿主授予的只读访问已失效. 错误提示会说明具体原因, 点按 “重试” 可再次检查.

#### 检查大文件会不会很慢?

插件把文件顺序读取一遍就同时算出全部七种校验和, 耗时主要取决于存储读取速度, 不会因为算法多而成倍变慢. 读取期间显示实时进度, 随时可以取消.

### 权限与安全

插件不申请存储与网络权限, 只能通过宿主临时授予的只读 content 地址访问用户选中的那一个文件, 检查结束后授权即失效, 无法触及其他文件. 来自文件管理器的请求会逐项校验动作标识、协议版本、content 地址结构、文件名、MIME 类型、声明大小与只读授权, 任何携带写入或持久化授权的请求都会被直接拒绝. 文件始终以流式只读方式处理; 内存中仅保留有界缓冲区、前 4096 字节分析样本、前 64 字节展示快照与 4 字节 PE 签名窗口, 源文件绝不会被修改.

为保证检查过程可控, 插件设有以下边界:

- 单个文件最大 8 TiB; 每次动作只处理一个目标文件.
- 分析最多采样文件前 4096 字节并额外保留 4 字节 PE 签名窗口, 展示的头部仍为 64 字节; 校验和输入最长 512 个字符, 仅 coreutils 文件名部分可使用非 ASCII 字符.
- 实际读取大小与声明大小不一致时, 判定为文件已变化, 检查失败并给出提示.
- 校验和比对使用等长恒定时间比较; MD5 与 SHA-1 仅供旧数据核对, 不应视为防碰撞的安全证明.

### 插件接口

宿主 (AutoJs6) 通过以下标识发现并调用插件, 供插件或宿主开发者参考:

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

当前版本在宿主文件管理器中提供单文件只读溢出菜单动作, 不修改源文件, 也不枚举目录. 插件未安装或被停用时, 宿主自动回退到默认行为, 互不影响.

### Roadmap

已实现能力以上文与 Roadmap 勾选条目为准; 依赖额外 provider 的摘要算法、批量校验与宿主协议扩展等后续计划集中维护在 Roadmap 中, 未勾选条目不代表当前版本已支持.

- [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/ROADMAP.md)

### 版本记录

#### v1.0.1

_2026/08/08_

- `修复` 修复插件在插件中心启用后宿主无法绑定服务的问题; 现在启用后 “检查文件” 动作立即可用
- `优化` 精简插件名称与描述, 使用户文档表述更自然易读

#### v1.0.0

_2026/08/02_

- `提示` 首个公开版本, 需要 AutoJs6 版本代码 5268 或更高
- `新增` 在 AutoJs6 文件管理器的文件菜单中新增只读动作 “检查文件”, 适用于任意类型的普通文件 (插件 ID `file-inspector`, 动作 ID `inspect-file`)
- `新增` 一次顺序读取同时计算 CRC32, MD5, SHA-1, SHA-256, SHA-512 五种校验和, 读取期间显示实时进度, 支持取消与重试
- `新增` 支持粘贴预期校验和做完整性校验: 按长度或 `sha256:` 等前缀自动识别算法, 兼容 `AB:CD:EF` 式指纹分隔与 CRC32 的 `0x` 前缀, 比对采用等长恒定时间比较
- `新增` 报告展示文件名、MIME 类型、扩展名、声明大小与实际大小, 以及前 64 字节的 “十六进制 + ASCII” 头部快照与 UTF BOM 检测
- `新增` 依据文件开头特征识别 ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3 十种常见格式
- `新增` 每条校验和可单独复制, 整份检查报告支持复制与系统分享; MD5 与 SHA-1 标注 Legacy 徽标
- `新增` 插件不申请存储与网络权限, 仅通过宿主临时授予的只读 content 地址访问文件, 单文件上限 8 TiB
- `新增` 内置 10 种语言的界面文本、使用说明、README 与 CHANGELOG: 简体中文、香港繁体、台湾繁体、英语、法语、西班牙语、日语、韩语、俄语、阿拉伯语
- `依赖` 引入 AndroidX Lifecycle ViewModel 2.9.4

##### 完整记录

- [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-File-Inspector/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

### 构建

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 构建:

```powershell
.\gradlew.bat :app:appendDigestToReleasedFiles
.\gradlew.bat :app:verifyReleaseChecksums
```

构建与签名参数由 version.properties 与 sign.properties 控制; 当前最低支持 Android 7.0 (SDK 24), 目标 SDK 36.

README、CHANGELOG 与 res/raw*/plugin_instruction.md 插件内说明均由 .python/generate_markdown.py 依据 .readme/ 与 .changelog/ 下的 JSON 语言资源和模板生成 (共 10 种语言). 修改文档请编辑对应 JSON 后重新运行脚本, 不要直接改动生成的 Markdown.

### 相关链接

- AutoJs6 文档: https://docs.autojs6.com
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing
