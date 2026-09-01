# AutoJs6 File Inspector Roadmap

本文档是文件检查器从 "基础校验和检查" 演进为 "全面的离线文件完整性与类型分析工具" 的执行清单. 每一项只有在代码/测试与可验证的验收条件同时完成后才可勾选.

## 状态与边界

- `[x]`: 已完成并在当前仓库验证.
- `[ ]`: 尚未完成; 括号中的 `插件`/`宿主`/`API`/`测试`/`发布` 表示主要落点.
- 插件的只读定位不可动摇: 任何条目都不得引入源文件写入、目录枚举, 或存储/网络权限申请.
- 插件代码位于本仓库; Explorer Action 协议与宿主文件管理器位于配套的 AutoJs6 仓库. 标注 `API`/`宿主` 的条目需要两侧代码与联合验证同时完成后才可勾选.
- 当前部署基线: Explorer Action 协议 v1, 宿主版本代码 5268, 单文件只读溢出菜单动作.

## M0: 已交付基线 (v1.0.x)

- [x] (插件) 文件管理器单文件只读动作 "检查文件", 逐字段校验动作标识、协议版本、content URI 结构、ClipData、文件名、MIME 类型、声明大小与只读授权, 拒绝一切写入与持久化授权.
- [x] (插件) 单次流式读取同时计算 CRC32 / MD5 / SHA-1 / SHA-256 / SHA-512, 单文件上限 8 TiB, 实时进度、取消与重试; 实际读取与声明大小不一致即失败.
- [x] (插件) 预期校验和规范化与核对: 按长度或 `sha256:` 等前缀识别算法, 兼容 `AB:CD:EF` 式指纹分隔与 CRC32 `0x` 前缀, 等长恒定时间比对, 输入上限 512 ASCII 字符.
- [x] (插件) 前 64 字节 "十六进制 + ASCII" 头部快照, UTF-8/16/32 BOM 检测, 以及 10 种偏移 0 格式特征识别 (ZIP, GZIP, PDF, PNG, JPEG, GIF87a, GIF89a, ELF, DEX, SQLite 3).
- [x] (插件) 校验和单条复制、报告整体复制与系统分享; MD5 / SHA-1 显示 Legacy 徽标.
- [x] (发布) 10 种语言的界面文本、使用说明与 README / CHANGELOG 生成管线 (.readme + .changelog + .python).

验收条件: 已随 v1.0.0 / v1.0.1 发布并在宿主 5268+ 实机验证.

## M1: 格式特征识别扩展

- [x] (插件) 扩展头部采样机制: 在单次流式读取中同时记录若干关键偏移窗口 (如前 512 字节与 TAR 的 257、MP4 `ftyp` 的 4 等固定偏移), 保持有界内存与单遍读取, 不引入随机访问.
- [x] (插件) 新增常见格式特征: 7z, RAR4/RAR5, XZ, BZIP2, ZSTD, LZ4, TAR, WebP (RIFF), MP4/ISO-BMFF, Matroska (EBML), Java Class, Mach-O, PE (MZ), WOFF/WOFF2.
- [x] (插件) 基于扩展名与 ZIP 特征的提示性细分: 对 `.apk` / `.jar` / `.docx` 等同为 ZIP 容器的文件, 在报告中补充一句容器用途说明 (仅提示, 不深入解析).
- [x] (插件) 文本/二进制启发式: 统计可打印字符比例与简单熵值估算, 帮助识别纯文本、压缩或加密数据.
- [x] (测试) 为每种新特征建立最小正样本与误报回归样本, 覆盖 "特征位于非零偏移" 与 "文件短于采样窗口" 两类边界.

验收条件: 样本集识别结果与 `file` 工具在覆盖格式上的结论一致; 扩展采样后单遍读取吞吐量相对 v1.0.x 基线无可感知回退.

验证记录 (2026-08-30): 前 4096 字节分析样本、64 字节展示快照与动态 4 字节 PE 窗口均在一次顺序读取内完成, 采样窗口收齐后仅保留常量分支; 全部新增格式的最小样本、错位误报与短文件测试通过, 并与 `file` 工具的格式结论交叉一致.

## M2: 摘要与核对增强

- [x] (插件) 新增 JCA 内置摘要算法 SHA-224 与 SHA-384; SHA-3 与 BLAKE2 不在 API 24 基线保证的 `MessageDigest` 算法列表中, 完整 provider 依赖的体积又与轻量离线插件定位不符, 本阶段明确不引入.
- [x] (插件) 支持粘贴 `sha256sum` / `md5sum` 输出行 (`<hex>  <文件名>` 与 `<hex> *<文件名>`): 自动提取十六进制部分, 并在文件名与当前文件不一致时给出警示.
- [x] (插件) 支持标准 Base64 (含无填充形式) 与 SRI (`sha256-<base64>`) 预期摘要输入, 与现有十六进制输入共用规范化与等长恒定时间核对管线.
- [x] (插件) 长度推断使用唯一候选门禁并保留 `algorithmHint` 手动指定入口; 当前七种算法的摘要长度均唯一, 因而不显示无作用的选择器. 未来加入同长度算法时, 自动推断会先拒绝歧义, 必须由界面选择结果传入该入口; 现有无歧义输入行为不变.
- [x] (测试) DigestInputNormalizer 单元测试矩阵覆盖全部 `DigestInputError` 分支、旧输入回归与新增形式, 并与 GNU coreutils / OpenSSL 输出交叉验证.

验收条件: 新增输入形式与 coreutils / openssl 生成的校验值可直接互认; 现有输入的解析结果与 v1.0.x 完全一致.

验证记录 (2026-08-30): 项目最低 API 24 高于 Android 对 SHA-224 标注的连续支持起点 API 22, SHA-384 则为 API 1+; 空输入及 `123456789` 的 SHA-224/SHA-384 已与 OpenSSL 4.0.1 核对. Android 官方 `MessageDigest` 保证列表未包含 SHA-3/BLAKE2; Bouncy Castle 1.85.2 完整 provider JAR 原始体积为 10,280,518 字节, 因此暂缓引入. GNU coreutils 8.32 的 `md5sum`/`sha256sum` 文本与二进制标记行、OpenSSL 生成的 Base64/SRI 向量、文件名一致/不一致分支及全部旧解析用例均通过. 参考: [Android MessageDigest API](https://developer.android.com/reference/java/security/MessageDigest), [Bouncy Castle Java provider](https://www.bouncycastle.org/download/bouncy-castle-java/).

## M3: 报告与界面体验

- [x] (插件) 深色主题与动态取色 (Material You) 跟随系统, 与宿主明暗状态一致.
- [x] (插件) 报告导出为结构化文本 (Markdown / JSON) 并经系统分享面板发送, 不落盘、不申请存储权限.
- [x] (插件) 头部快照独立复制; 读取速度与剩余时间估算显示; 长文件名与长校验和的折行与等宽展示优化.
- [x] (插件) TalkBack 审计、至少 48 dp 触控目标、阿拉伯语 RTL 镜像复核, 以及 1.5x / 2.0x 字体倍率与窄屏 (320 dp) 布局检查.
- [x] (发布) 插件内置使用说明 (res/raw*/plugin_instruction.md) 纳入 .python 生成管线, 与 README 文案同源维护, 消除双头维护.

验收条件: 屏幕尺寸 × 字体倍率 × 明暗主题 × RTL 矩阵内无文本截断与不可达控件; 导出文本与屏幕内容一致.

验证记录 (2026-08-30): Android 实机 UI 自动化覆盖 API 28 深色英文 360 dp / 1.0x、API 31 深色英文 418 dp / 1.0x 动态取色、API 35 浅色中文 720 dp / 1.0x、API 35 浅色英文 320 dp / 1.5x, 以及 API 35 深色阿拉伯语 RTL 320 dp / 2.0x; 各组合均无可见文本省略、不可达操作或小于 48 dp 的可点击目标, 长文件名、最长摘要和头部快照可完整折行或横向浏览. UI 测试逐项核对 Markdown / JSON 导出包含屏幕所示文件信息、分析详情、全部摘要与头部快照; 导出仅进入剪贴板或系统 `ACTION_SEND`, 不产生报告文件. 58 项 JVM 单元测试全部通过, `assembleDebug`、`assembleDebugAndroidTest` 与 `lintDebug` 通过 (0 error, 36 个既有工具链/依赖版本 warning). 6 组后备主题前景/背景对比度均不低于 6.11:1. 文档生成器的 36 个输出重跑前后聚合 SHA-256 均为 `0331c78d5d549bc4e9c689b5915b4fa1dbfa7de89f532236536794ecc252bb32`, 证明 README、CHANGELOG 与 11 份 `plugin_instruction.md` 可幂等生成.

## M4: 工程与质量

- [x] (测试) core 层单元测试: FileInspectionEngine 的上限/大小不一致/取消分支, HeaderInspector 全部特征与 BOM, DigestVerifier 等长恒定时间比较.
- [x] (测试) FileInspectorIntentPolicy 伪造 Intent 矩阵: 错误动作、协议版本不符、坏 content URI、ClipData 不符、越权授权、超大声明等全部拒绝分支.
- [ ] (发布) GitHub Actions: assembleDebug + 单元测试 + 文档一致性检查 (运行 generate_markdown.py 后工作区无 diff 才通过). 工作流与本地 clean-snapshot 验证已完成, 待远程仓库建立后取得首次 GitHub-hosted 绿色运行记录.
- [x] (发布) releases/ 产物附 SHA-256 校验文件, 命名与现有 `autojs6-plugin-file-inspector-v*-<hash>.apk` 规则统一; README 使用方法中引导用户用本插件完成自校验.

验收条件: CI 在干净克隆上绿灯; 手工改动生成的 Markdown 会被文档一致性检查拦截; 每个发布产物均可用插件自身校验通过.

验证记录 (2026-08-30): 59 项 JVM 单元测试全部通过 (0 failure / 0 error / 0 skipped), 其中 FileInspectionEngine 18 项覆盖声明/硬上限、长短大小不一致、零长度读取、I/O 失败与协程取消关流, HeaderInspector 9 项覆盖全部 26 种特征、5 种 BOM、短文件与错位误报, DigestVerifier 覆盖首/中/末差异和异长拒绝. FileInspectorIntentPolicy 的 11 项伪造矩阵分别在 API 28、31、35 实机通过, 共执行 33 项且全部拒绝分支符合预期. `.github/workflows/ci.yml` 已以只读权限和完整提交 SHA 固定官方 actions, `actionlint` 通过; 由当前 170 个非忽略文件建立的干净 Git 快照按 workflow 同等命令完成 `assembleDebug`、`testDebugUnitTest` 与生成文档零 diff. 但当前本地仓库没有 Git remote, 目标 GitHub 仓库亦尚不可解析, 无法触发 GitHub-hosted runner, 因而 CI 条目严格保留为未完成. 两份历史 APK 的 CRC32 文件名后缀与同名 `.apk.sha256` 已由 `verifyReleaseChecksums` 核对; `ReleaseArtifactSelfCheckTest` 再使用插件自身的 sidecar 解析、单遍读取、SHA-256 与恒定时间比较管线确认两份均匹配. 10 种语言 README/内置说明均给出升级前或首装后的 APK 自检步骤, 36 个生成输出二次运行前后聚合 SHA-256 均为 `63e977bd315826a694b7f896aacf0d44a13e7073925a0c099c15d2b68f336cb5`.

## M5: 宿主协同 (跨仓库, 可选)

- [ ] (API/宿主) 多选批量检查: 依托 Explorer Action 协议的多目标扩展, 一次为多个文件生成校验和列表并汇总导出 (协议 v1 当前仅支持单文件).
- [ ] (API/宿主) 宿主呈现模式: 参照 Explorer Action v5 的宿主呈现能力, 让宿主文件页直接内嵌显示轻量校验摘要, 免于跳转独立页面.
- [ ] (插件) ACTION_VIEW "打开方式" 网关评估: 参照 APK-Inspector 的独立网关模式, 允许其他应用以 content 地址调起检查; 需先完成通配 MIME 带来的入口泛滥与安全影响评估.
- [ ] (插件) 上述协议扩展保持向后兼容: v1 宿主行为完全不变, 新能力按能力位协商.

验收条件: 新旧宿主混布时功能按能力位自动降级, 无崩溃与入口错位.
