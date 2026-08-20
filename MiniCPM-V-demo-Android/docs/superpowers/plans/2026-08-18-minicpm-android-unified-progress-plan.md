# MiniCPM Android 统一进度与后续实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 MiniCPM Android 应用的历史功能计划、端侧 RAG 总体方案、低延迟重构、双三分类器训练和真机验证统一为唯一活动进度文档，准确区分已经完成、已经验证、已实现但未启用和仍未完成的工作。

**Architecture:** 应用继续使用 MiniCPM/llama.cpp-omni 作为有状态生成引擎；本地 RAG 使用 SAF、WorkManager、Room/SQLCipher、加密原文、FTS4、E5 INT8 ONNX、dense + lexical RRF、临时 native checkpoint 和引用快照。Answerability 与 Groundedness 共用一个 INT8 ONNX 编码骨干和两个三分类头，但只有通过脱敏真实办公分布质量门槛后才允许进入生产路径。

**Tech Stack:** Kotlin、Java、C++17/JNI、Android SDK 36、JDK 21、Gradle 9.6.1、Room、SQLCipher、WorkManager、ONNX Runtime Android、ONNX Runtime Extensions、PDFBox、ML Kit、JUnit 4、Android instrumentation。

> **2026-08-20 增量：** `9b229c2` 已完成单文档长按删除、失败导入无持久文档记录、失败提示左滑移除和同名/同内容重传；本轮继续完成来源 Chip 的当前索引块定位、来源删除状态和归档摘录降级。

---

## 1. 文档权威性与使用规则

从 2026-08-18 起，本文是项目功能进度和后续开发顺序的唯一活动计划。

- 历史计划保留用于追溯需求、设计取舍和旧测试，不再单独更新完成度。
- 若历史计划与本文冲突，以本文、当前代码和最近真机证据为准。
- 架构决策和威胁模型继续有效，但不承担进度跟踪职责。
- 每完成一个后续任务，必须同步更新本文的状态表、验收证据和剩余工作。
- “代码存在”不等于“可以发布”；必须分别记录实现状态、JVM 测试、真机测试、生产启用状态。

### 1.1 状态定义

| 状态 | 含义 |
|---|---|
| `COMPLETED` | 功能已实现，相关自动化测试和必要真机验证通过，已进入当前运行路径 |
| `VERIFIED` | 已在目标 vivo `V2359A` 或固定桌面工具链完成真实模型/文件/流程验证 |
| `IMPLEMENTED_NOT_ENABLED` | 代码、模型或策略已存在，但生产开关或质量闸门仍关闭 |
| `PARTIAL` | 主要路径可工作，但缺少性能、异常、规模或 UI 完整性 |
| `NOT_STARTED` | 当前代码中尚无对应生产实现 |
| `BLOCKED_BY_DATA` | 工具链已完成，但缺少经过授权和脱敏的真实分布数据 |
| `ARCHIVED` | 历史计划已被本文吸收，不再作为活动任务清单 |

## 2. 历史计划归并结果

### 2.1 已完成并归档的基础功能计划

以下计划的目标已经体现在当前代码和测试中。旧文档中的未勾选框不再代表当前完成度。

| 历史计划 | 统一状态 | 当前结果 |
|---|---|---|
| `2026-07-31-android-camera-pending-image.md` | `ARCHIVED / COMPLETED` | 拍照入口、图片缓存、预处理进度、原图查看和预处理期间删除已接入 |
| `2026-08-03-android-status-download-image-viewer.md` | `ARCHIVED / COMPLETED` | 状态栏常驻、模型下载返回前台不重复提示、原图查看已接入 |
| `2026-08-03-unified-chat-settings-and-no-image-research.md` | `ARCHIVED / COMPLETED` | 左上角统一设置、视觉上下文状态和无图视觉请求保护已接入 |
| `2026-08-03-visual-context-guard.md` | `ARCHIVED / COMPLETED` | `hasVisualContext` 生命周期、输入视觉意图保护和快捷入口状态已接入 |
| `2026-08-04-local-streaming-guard-reply.md` | `ARCHIVED / COMPLETED` | 本地拦截提示以模拟流式 AI 消息显示，且不进入模型上下文 |
| `2026-08-04-semantic-visual-output-guard.md` | `ARCHIVED / PARTIAL` | 视觉输入/输出语义保护已存在；RAG Groundedness 输出复核属于新的未完成生产路径 |
| `2026-08-05-inline-privacy-input-confirmation.md` | `ARCHIVED / COMPLETED` | 隐私输入在用户气泡下确认，拒绝后删除且不调用模型 |
| `2026-08-05-local-content-safety-stage-two.md` | `ARCHIVED / COMPLETED` | `ALLOW/WARNING/BLOCK/REVIEW` 策略、隐私检测和违法内容固定流式拒答已接入 |
| `2026-08-06-conversation-history-editing.md` | `ARCHIVED / COMPLETED` | 多会话、消息删除、用户消息修改重答和 AI 消息编辑已接入 |
| `2026-08-06-persistent-conversations.md` | `ARCHIVED / COMPLETED` | 版本化会话归档、原子保存、重启恢复和应用私有图片持久化已接入 |
| `2026-08-07-flexible-message-editing.md` | `ARCHIVED / COMPLETED` | 用户消息截断重答、AI 文本只改显示与上下文、整气泡长按已接入 |

### 2.2 RAG 计划归并

| 历史计划 | 统一状态 | 保留价值 |
|---|---|---|
| `2026-08-10-android-local-rag.md` | `ARCHIVED / SUPERSEDED` | 保留总体架构、数据模型、文件安全、解析格式和原始验收目标 |
| `2026-08-14-android-rag-low-latency-refactor.md` | `ARCHIVED / SUPERSEDED` | 保留 native checkpoint、RagCoordinator、混合检索、级联门控和性能门槛的实施历史 |

以下支持文档继续有效：

- `docs/architecture/ADR-001-local-rag-stack.md`
- `docs/architecture/rag-threat-model.md`
- `docs/execution/evidence/rag-retrieval-calibration-20260817.md`
- `tools/rag_guard/OFFICE_QUALITY_GATE.md`

## 3. 当前基线

| 项目 | 当前值 |
|---|---|
| 分支 | `codex/rag-all-queries-experiment` |
| 已提交基线 | `9b229c220690123af5ec00b37742d110f9bcc18b` |
| 工作树 | 正在实现来源生命周期；Guard、检索和导入删除改动均已提交 |
| Android 包名 | `com.example.minicpm_v_demo` |
| 目标真机 | vivo `V2359A` |
| 安装规则 | 先执行 `verifyInstallationSigning`，只允许 `adb install -r`，禁止自动卸载和清除数据 |
| E5 模型 | `multilingual-e5-small` INT8，384 维，固定文件 SHA-256 |
| Guard 模型 | v3 实验双头 INT8 ONNX，118,169,267 bytes，SHA-256 `6d11400d62b8f15250932e3187aa7b7823809dc0baf0a0ff0a3c157dbe1d35fa`；量化发布门槛失败，仅允许保守阈值实验路径 |
| 当前 RAG 模式 | `ALL_QUERIES` 实验模式，不是最终默认策略 |
| 当前生产门控 | 惰性分类器依赖已接入，`CurrentAnswerabilityCalibration.profile=null`，只安全放行精确锚点证据且不会打开 Guard session |

## 4. 完成度总览

完成度是工程估算，不使用历史计划中已经失真的勾选数量直接计算。

| 口径 | 当前完成度 | 说明 |
|---|---:|---|
| 基础 App 历史需求 | 约 `95%` | 状态栏、图片、设置、安全、多会话、持久化和编辑已稳定；仍需在最终 RAG 回归中证明不退化 |
| RAG 基础闭环 | 约 `80%` | 手机导入、解析、切块、向量化、选择知识库、检索、临时注入、回答和引用归档已经跑通 |
| RAG 完整办公发布目标 | 约 `60%` | 真实分布质量、输出复核、证据压缩、大库索引、来源 UI、生命周期和压力验收尚未完成 |
| 项目整体正式发布准备度 | 约 `70%` | 基础 App 较成熟，但 RAG 仍是实验/开发功能，不能标记为稳定版 |

### 4.1 RAG 子系统状态

| 子系统 | 完成度 | 状态 | 关键结论 |
|---|---:|---|---|
| 数据库、迁移和加密 | `95%` | `COMPLETED` | Room schema、SQLCipher、Keystore、加密原文、迁移和防备份已实现 |
| 知识库 UI | `95%` | `COMPLETED` | 创建、命名、淡蓝选择、阶段状态、知识库删除、单文档长按删除、失败提示左滑移除及同名重传已实现并验收 |
| 文件导入与恢复 | `90%` | `COMPLETED` | SAF、WorkManager、取消、失败原因、恢复和原子文件流程已实现 |
| 文档解析 | `90%` | `COMPLETED` | TXT、Markdown、CSV、HTML、PDF/OCR、DOCX、PPTX、XLSX 已有解析器和限额 |
| 切块与嵌入 | `90%` | `VERIFIED` | 结构化 chunk、中文 bigram、E5 tokenizer、INT8 embedding 和真机推理已通过 |
| 混合检索 | `75%` | `PARTIAL` | FTS4 BM25、dense、RRF、SQL 过滤已实现；普通语义证据仍被生产门闸关闭 |
| RAG 状态协调 | `90%` | `COMPLETED` | Disabled、NoSelection、Indexing、NoEvidence、Ready 和匿名失败已统一 |
| 临时上下文事务 | `90%` | `VERIFIED` | native checkpoint 保存/恢复、取消恢复、视觉 checkpoint 和证据不残留已验证 |
| 引用归档与校验 | `90%` | `PARTIAL` | 引用白名单、不可变快照、来源 chip、当前索引块定位和来源删除归档状态已实现；外部二进制文件页/单元格深链为后续增强 |
| Answerability 门控 | `70%` | `IMPLEMENTED_NOT_ENABLED` | 模型、Android runtime、性能和离线门槛工具完成；缺真实办公质量数据和生产 profile |
| Groundedness 输出审查 | `90%` | `IMPLEMENTED_NOT_ENABLED` | 候选隐藏、一次同证据重生成、checkpoint 回退和普通回答降级已接入实验路径；等待真机错误金额/伪引用验收 |
| 证据压缩和 token 预算 | `90%` | `PARTIAL` | 句子窗口缩减、跨来源去重、真实模型 token 计数和动态预算已接入；等待真机 token 对齐验收 |
| 大知识库向量索引 | `20%` | `NOT_STARTED` | 当前精确搜索可支撑小库；连续缓存、HNSW、分区降级和损坏恢复未实现 |
| 生命周期和 watchdog | `40%` | `PARTIAL` | 基础取消和事务恢复存在；完整前后台、编辑冲突和 15 秒 watchdog 未完成 |
| 性能/压力/灰度发布 | `30%` | `PARTIAL` | checkpoint、E5、Guard 单项真机数据存在；完整矩阵和灰度开关未完成 |

## 5. 已完成内容

### 5.1 基础 App 与办公安全

- 系统状态栏永久显示，App 不占据最上方系统区域。
- 模型下载切后台再返回时不会重复弹出“未下载模型”。
- 设置统一在左上角，模型管理、图片切片、会话和知识库均从统一入口进入。
- 聊天输入区支持相册和拍照；图片先缓存并预处理，处理期间变暗、显示圆形进度和提示。
- 图片预处理期间和完成后均可删除；输入区和聊天气泡可打开缓存原图。
- `hasVisualContext` 随图片成功写入、清空会话、切换/卸载模型正确变化。
- 无图视觉依赖问题会被本地拦截；本地提示模拟流式 AI 输出且 `includeInModelContext=false`。
- 隐私文本先在用户气泡下确认，只有明确选择“是”才发送给模型。
- 违法内容固定拒答；隐私、电话、身份证号和地址类内容进入 WARNING/REVIEW/BLOCK 规则。
- 多会话、永久存储、删除消息、用户消息修改重答、AI 消息编辑和会话回滚已实现。

### 5.2 RAG 数据、导入和索引闭环

- 用户可创建并命名不同知识库，名称规范化后防重名。
- 知识库选择使用淡蓝背景，不使用勾号；删除知识库有二次确认。
- SAF 支持一次选择多个文档，导入任务使用唯一 WorkManager 工作链。
- 文档状态覆盖复制、解析、OCR、切块、嵌入、最终 READY、失败、取消和恢复。
- 成功导入保留正常状态显示并支持长按二次确认删除；失败导入清理实际文件和文档记录，仅在页面显示可左滑移除的匿名原因，同名/同内容可再次上传。
- 文件类型同时使用扩展名、MIME 和魔数检测，避免仅凭 TXT 扩展名误判。
- 原始文档复制到应用私有隔离区并加密；数据库使用 SQLCipher，密钥由 Android Keystore 保护。
- 文档解析、chunk 和 embedding 受文件大小、页数、行数、解压大小、token 和维度上限保护。
- E5 模型包使用固定长度和 SHA-256，`.part` 写入后原子替换。
- Worker 链为 `ImportCopyWorker -> ParseWorker -> OcrWorker -> ChunkWorker -> EmbedWorker -> FinalizeIndexWorker`。
- 只有 chunk 与 embedding 完整且模型哈希一致时，文档才进入 READY。

### 5.3 检索、注入和无结果行为

- 当前会话可独立开启/关闭 RAG，并选择一个或多个知识库。
- 空选择永远不解释为“查询全部知识库”。
- FTS4 通过安全转义后的绑定参数查询，Kotlin 解析 `matchinfo` 并计算 BM25。
- dense 和 lexical 候选通过稳定 RRF 融合，限制每路候选数和单文档候选数。
- `RagCoordinator` 是唯一状态决策入口。
- `NoEvidence` 使用原始用户问题正常调用模型，不显示“知识库未命中”固定回复，不注入候选和引用。
- RAG Ready 路径使用 native checkpoint 临时追加证据；生成结束、取消或异常后恢复稳定上下文。
- 增强 prompt 不参与无图视觉意图判断；已确认 RAG 文本不会被误识别为图片描述请求而拦截。
- 证据来源编号由代码生成，伪造或越界 `[Sx]` 不会写入会话归档。
- 会话归档 v2 可持久保存引用快照、`ragRunId` 和 `answerEdited`。

### 5.4 双三分类器与质量工具链

- 共享 `multilingual-e5-small` 编码骨干，训练 Answerability 和 Groundedness 两个三分类头。
- Answerability 标签为 `SUPPORTED/PARTIAL/UNSUPPORTED`。
- Groundedness 标签为 `GROUNDED/PARTIAL/UNGROUNDED`。
- 训练集、校准集和测试集按 `document_id` 隔离；历史绕过、伪引用和提示注入作为 test-only 回归种子。
- 合成测试集两个任务 macro-F1 均为 1.0，但不作为真实办公上线结论。
- INT8/FP32 标签一致率为 0.9984，现有测试最大 macro-F1 降幅为 0。
- Android runtime 复现训练输入格式、256-token 截断、结束 token 保留和稳定 softmax。
- Guard 真机 CPU 打开耗时 `1385.750 ms`；Answerability P50/P95 为 `9.905/12.814 ms`；Groundedness P50/P95 为 `13.062/17.906 ms`；30 次无失败。
- Guard 测试进程 PSS 增量为 `239199 KB`，因此当前禁止 App 启动时预加载。
- `score_office_holdout.py` 使用与 Android 相同的 `tokenizer.onnx` 评分。
- `quality_gate.py` 检查人工脱敏标记、手机号/身份证号、文档隔离、模型哈希和聚合质量指标。
- Windows 现有 `.rag-python-tools` CPU 环境已完成真实 tokenizer + Guard ONNX 匿名样本端到端评分，不需要显卡或 CUDA。

### 5.5 构建、安装和数据保护

- 主 APK、AndroidTest APK、JVM 单元测试和 `verifyInstallationSigning` 已通过。
- 主 APK 与测试 APK 使用相同固定证书。
- 顶层 Gradle 禁止 `connectedCheck` 和所有 `connected*AndroidTest`，防止测试插件自动卸载应用并清空数据。
- 真机测试固定使用构建测试 APK、`adb install -r` 和 `scripts/run-device-instrumentation.ps1`。
- E5 和 Guard 模型均有本机独立备份，并在复制到设备的每个阶段验证 SHA-256。

## 6. 已实现但不能启用的内容

### 6.1 Answerability

`OnnxRagGuardClassifier`、模型管理器、级联接受策略、惰性适配器和质量门槛工具已经完成。当前 `MiniCPMApplication` 已接入：

```kotlin
classifier = LazyAnswerabilityClassifier(ragGuardModelManager::openInstalled)
profile = CurrentAnswerabilityCalibration.profile
```

`CurrentAnswerabilityCalibration.profile` 仍固定为 `null`。这意味着当前生产路径仅放行精确文件名、编号、条款等锚点；普通语义相似证据不会仅凭 dense 分数进入 prompt，惰性分类器也不会打开约 239 MB PSS 的 Guard session。必须先通过真实脱敏办公校准集和独立测试集。

### 6.2 Groundedness

`RagOutputReviewPolicy` 已定义：

1. `GROUNDED`：接受回答。
2. `PARTIAL/UNGROUNDED` 且尚未重生成：最多重生成一次。
3. 第二次仍失败：显示不进入上下文的本地固定提示。

该策略已接入 `MainActivity` 的真实 RAG 候选隐藏、一次同证据重生成和普通回答降级事务；因 v3 质量门槛未通过，仍只能声明为实验路径，不能标记为稳定生产审查。

### 6.3 ALL_QUERIES 实验模式

当前分支将：

```kotlin
retrievalMode = RagRetrievalMode.ALL_QUERIES
```

作为实验路径，用于比较“所有问题先检索”的行为。最终稳定版默认目标仍是 `ADAPTIVE`：普通问候、感谢、闲聊和不依赖知识库的问题不调用 E5、Room chunk DAO 或 checkpoint。

## 7. 未完成内容

### 7.1 真实办公分布质量验收

状态：`BLOCKED_BY_DATA`。

缺少经过授权、人工脱敏、按文档隔离的办公 Answerability/Groundedness 校准集和最终测试集。当前合成语料只能证明训练和导出管线可工作。

上线门槛固定为：

- Answerability 精确率不低于 `0.95`。
- Answerability 召回率不低于 `0.90`。
- Groundedness macro-F1 不低于 `0.85`。
- Groundedness ECE 不高于 `0.10`。
- `SIMILAR_BUT_WRONG`、错误金额、错误日期、字段缺失和前提不存在必须单独统计。
- 训练、校准和最终测试文档 ID 必须两两不相交。

### 7.2 Groundedness 输出生产接入

- 仅在 `RagTurnPlan.Ready` 且实际注入了证据时运行。
- 审查输入必须是用户原文、最终使用的证据快照和候选回答。
- 审查不能读取会话中未选择的知识库。
- 最多重生成一次；第二次失败恢复 checkpoint 并使用用户原文走普通模型回答，不显示 RAG 固定提示。
- 被拒绝的候选和修正 prompt 不写入稳定历史。
- 取消、异常、切后台均必须恢复 checkpoint。

### 7.3 句子级证据缩减和真实 token 预算

当前 `IdentityRagEvidenceReducer` 不缩减正文，`SourceCountRagEvidenceBudgeter` 只限制来源数量。仍需完成：

- 中文/英文句子、表格行和条款边界切分。
- query token 覆盖、编号、日期和金额奖励。
- 相邻句扩展和跨来源去重。
- 单来源最多 320 token。
- 默认总证据 768 token，硬上限 900 token。
- 给回答至少预留 768 token，协议和问题至少预留 256 token。
- XML escape、source ID 代码生成和文档提示注入声明。

### 7.4 大知识库向量后端

当前精确向量搜索是小库正确性基线。仍需完成：

- [已实现] 小于等于 5000 chunks 时使用连续 float buffer 缓存，不重复从 Room 解码全部 BLOB。
- 大于 5000 chunks 时使用 HNSW 或受限磁盘分区搜索。
- 索引头绑定模型 SHA、语料 generation、维度、数量和文件 SHA-256。
- `.part + fsync + atomic rename`。
- 索引损坏或 generation 不一致时后台重建并禁止返回旧文档。
- 1k、5k、20k chunks 的 Recall@10、P50/P95 和 RSS 对照。

### 7.5 E5 执行提供程序和内存策略

- CPU、NNAPI、NNAPI FP16 分别预热 5 次、测量 30 次。
- 记录 P50/P95、失败数、RSS、温升和向量余弦一致性。
- NNAPI 发生大量 CPU fallback 或慢于 ORT CPU 时必须保持 CPU。
- E5 session 按真实使用和系统 trim memory 释放。
- Guard 只能在通过质量门槛且本轮需要分类时懒加载，禁止启动预加载。

### 7.6 生命周期、编辑和超时恢复

- Home、返回前台、旋转、来电式 pause、切换会话、删除会话、编辑消息和模型切换状态矩阵。
- RAG turn 与用户编辑/删除不能并发修改同一时间线。
- 用户问题编辑重答要清除旧答案和旧引用，并恢复稳定上下文到编辑点。
- AI 文本编辑保留引用快照并设置 `answerEdited=true`。
- 任一非生成阶段连续 15 秒无进展时触发 watchdog，恢复 checkpoint、恢复输入框并输出本地提示。

### 7.7 来源和阶段 UI

- `正在检索知识库`、`正在整理依据`、`正在生成回答` 三类可行动阶段仍待实现。
- 普通聊天不显示 RAG 阶段。
- [已实现] AI 气泡下显示 `S1 · 文件名 · 定位` 来源 chip；点击后按 `documentId + chunkId` 定位当前索引原文。
- [已实现] 来源删除后继续显示回答时的归档摘录并标记“来源已删除”；索引不匹配单独标记“当前索引不可用”。
- chip 整体可点击、有 contentDescription、长名称省略且详情可查看完整名称。

### 7.8 全链验收和发布

- 两知识库隔离、关闭零调用、问候零调用、无证据、弱相关、跨文档、图片 + RAG。
- 隐私确认、违法拒答、无图保护、RAG 视觉优先级和文档提示注入。
- 编辑回滚、删除来源、进程重启、模型丢失和数据库迁移。
- 连续 100 个成功 RAG turn、50 次取消、20 次前后台切换。
- 空历史、10 轮、30 轮历史的普通聊天和 RAG P50/P95/TTFT/RSS。
- `low_latency_rag_v1` 灰度开关和失败降级。
- README、改版说明、模型来源、设备差异和隐私边界更新。

## 8. 后续唯一执行顺序

### Task 1：真实办公质量门槛与生产 profile

**Files:**
- Existing: `tools/rag_guard/score_office_holdout.py`
- Existing: `tools/rag_guard/quality_gate.py`
- Existing: `tools/rag_guard/OFFICE_QUALITY_GATE.md`
- Modify after passing: `app/src/main/java/com/example/minicpm_v_demo/MiniCPMApplication.kt`
- Test: `app/src/test/java/com/example/minicpm_v_demo/rag/retrieval/CascadedEvidenceAcceptancePolicyTest.kt`
- Test: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/guard/RagGuardInstrumentedTest.kt`

- [ ] **Step 1：准备受控数据目录。** 在不进入 Git 的 `D:\MiniCPM-V\private-eval\rag-guard` 保存人工脱敏的 `office_calibration_unscored.jsonl`、`office_test_unscored.jsonl` 和 `training_document_ids.txt`。
- [ ] **Step 2：执行隐私人工复核。** 每条数据确认不包含真实姓名、电话、身份证号、精确地址、内部账号、客户编号和未授权正文；将 `redaction_status` 标记为 `reviewed`。
- [ ] **Step 3：使用固定 CPU 环境评分。** 按 `tools/rag_guard/OFFICE_QUALITY_GATE.md` 运行 `score_office_holdout.py`，输出校准集和测试集 scored JSONL。
- [ ] **Step 4：运行质量门槛。** 使用固定 Guard SHA 和 tokenizer SHA 运行 `quality_gate.py`；预期退出码为 0，报告不得包含正文。
- [x] **公开数据预资格基线（不替代 Step 1-4）。** 2026-08-19 已从 Doc2Dial v1.0.1（政务服务）和 CUAD v1（商务合同）构造文档级隔离的公开许可评测集：校准/测试各 40 份文档、各 240 条记录，每项任务各 120 条，三分类平衡；归档哈希、ZIP 安全、文档隔离、重复 ID 和敏感号码扫描均通过。固定 ONNX Guard 完成 480 条 CPU 评分，但公开预资格失败：Answerability precision/recall 为 `1.0000/0.0250`，Groundedness macro-F1/ECE 为 `0.3996/0.1452`。因此 `CurrentAnswerabilityCalibration.profile` 继续保持 `null`；下一步先用公开训练文档和人工审核困难负例重训，再执行完全独立的真实办公 Step 1-4。详见 `tools/rag_guard/PUBLIC_OFFICE_HOLDOUT.md`。
- [x] **v3 扩充训练与一次独立测试。** 中英文公开语料扩充到每个任务训练 `92,244` 条、校准 `5,124` 条、测试 `5,124` 条，文档 ID 两两隔离并包含办公与日常对话负例。FP32 独立测试 Answerability/Groundedness macro-F1 为 `0.9897/0.8128`；INT8 为 `0.9885/0.8088`。量化标签一致率 `0.9921` 低于 `0.995` 门槛，旧回归种子最大 macro-F1 降幅 `0.0979`，所以稳定发布门槛失败。按实验分支要求不再重复训练/测试，使用与模型 SHA 绑定的 `0.95` 保守 Answerability/Groundedness 阈值；任一失败静默降级普通回答。训练 checkpoint 与 INT8 包已备份到 `D:\MiniCPM-V\artifacts\rag-guard-dual-head-v3`，并已原子部署到 vivo V2359A 私有 v3 目录。
- [x] **Step 5：写生产 profile 红灯测试。** 已覆盖错误 SHA、未安装模型、分类异常、低概率、最多 3 个候选、未校准 profile 和精确锚点旁路；所有失败路径均 fail-closed，取消继续传播。
- [x] **Step 6：接入惰性 Answerability。** `LazyAnswerabilityClassifier` 只在 `classify()` 首次实际执行时解析并缓存已验证模型；`MiniCPMApplication` 已接入该依赖，但 `CurrentAnswerabilityCalibration.profile=null`，因此启动、普通聊天、精确锚点和未校准语义候选均不会打开 Guard。2026-08-19 定向测试和 82-task 全量构建/签名回归通过。
- [ ] **Step 7：执行真机回归。** 单文档、10 文档、40 文档和 500 文档规模下，同一问题/证据必须得到相同分类决策。
- [ ] **Step 8：提交独立改动。** 只提交代码、聚合报告和脱敏统计；禁止提交私有评测 JSONL。

### Task 2：Groundedness 输出审查生产接入

**Files:**
- Modify: `app/src/main/java/com/example/minicpm_v_demo/MainActivity.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/RagTurnTransaction.kt`
- Existing: `app/src/main/java/com/example/minicpm_v_demo/rag/guard/RagOutputReviewPolicy.kt`
- Test: `app/src/test/java/com/example/minicpm_v_demo/rag/guard/RagOutputReviewPolicyTest.kt`
- Create: `app/src/test/java/com/example/minicpm_v_demo/rag/guard/RagReviewedGenerationTest.kt`

- [x] **Step 1：写成功、重生成、二次失败和取消测试。** 已覆盖候选隐藏、最多一次重生成、模型 SHA 不匹配和取消传播；checkpoint 事务另有独立成功/回滚测试。
- [x] **Step 2：定义 reviewed generation 结果。** 结果只允许 `Accepted` 和 `FallbackToNormalGeneration`，不向 UI 返回未审查候选正文。
- [x] **Step 3：在 Ready 路径执行 Groundedness。** 使用用户原文、最终证据快照和完整候选答案分类；普通聊天和 NoEvidence 不运行。
- [x] **Step 4：实现一次受限重生成。** 修正 prompt 只使用同一证据；第一次候选不进入 UI 或稳定历史。
- [x] **Step 5：接入普通回答降级。** 第二次失败、分类器缺失、profile 缺失或模型 SHA 不匹配时恢复 checkpoint，清空 RAG 引用并使用原始问题普通生成；只有审核通过时增加数据库来源标识。
- [ ] **Step 6：真机验证。** 覆盖正确引用、错误金额、无依据扩写、伪引用和取消。

### Task 3：证据缩减、token 预算和 prompt 加固

**Files:**
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/retrieval/EvidenceReducer.kt`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/prompt/RagContextBudgeter.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/retrieval/RagPromptAssembler.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/RagCoordinator.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/LlamaEngine.kt`
- Test: `app/src/test/java/com/example/minicpm_v_demo/rag/retrieval/EvidenceReducerTest.kt`
- Test: `app/src/test/java/com/example/minicpm_v_demo/rag/prompt/RagContextBudgeterTest.kt`

- [x] **Step 1：写句子边界和恶意输入测试。** 已覆盖中英文标点、表格行、条款、金额、日期、emoji、重复句和 XML 闭合标签。
- [x] **Step 2：实现无模型句子窗口 reducer。** 保留最高相关窗口及前后各一句，按规范化文本跨来源去重。
- [x] **Step 3：增加 native token 计数接口。** JNI 使用 MiniCPM 当前模型的 `common_tokenize`，不使用字符数估算；native 构建已通过。
- [x] **Step 4：实现动态预算。** 默认 768、硬上限 900、单来源上限 320，并为回答保留 768、协议和问题保留 256；可用预算不足 128 时返回 NoEvidence。
- [x] **Step 5：加固 prompt。** 文件名、定位和正文均 XML escape，并放入明确的不可信 `<knowledge_base>/<source>` 数据边界。
- [ ] **Step 6：运行 JVM 和真机 token 对齐测试。** 实际注入 token 不得超过预算。

### Task 4：有界向量后端

**Files:**
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/VectorSearchBackend.kt`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/ExactVectorBuffer.kt`
- Create: `app/src/main/java/com/example/minicpm_v_demo/rag/index/HnswIndexManager.kt`
- Create: `app/src/main/cpp/rag_hnsw_jni.cpp`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/retrieval/RoomDenseEvidenceRetriever.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/work/FinalizeIndexWorker.kt`
- Test: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/index/VectorSearchBackendInstrumentedTest.kt`

- [ ] **Step 1：写统一接口和精确 oracle 测试。** 所有后端返回稳定 chunk ID、score 和 generation。
- [x] **Step 2：实现小库连续 float buffer。** 最多 5000 chunks；缓存键绑定有序知识库集合、模型 SHA、corpusVersion、数量、最大更新时间和 chunk ID 校验和。
- [ ] **Step 3：实现大库 HNSW/分区后端。** 打开前验证文件头、长度、哈希和 RSS 预算。
- [ ] **Step 4：实现原子构建和损坏恢复。** 查询期间不得返回旧 generation。
- [ ] **Step 5：运行 1k/5k/20k benchmark。** 与精确 oracle 比较 Recall@10、P50/P95 和 RSS；Recall@10 不低于 0.95。

### Task 5：自适应路由、生命周期和 UI

**Files:**
- Modify: `app/src/main/java/com/example/minicpm_v_demo/MiniCPMApplication.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/MainActivity.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/ChatAdapter.kt`
- Modify: `app/src/main/java/com/example/minicpm_v_demo/rag/RagTurnTransaction.kt`
- Create: `app/src/main/res/layout/item_rag_source_chip.xml`
- Create: `app/src/test/java/com/example/minicpm_v_demo/rag/RagTurnLifecycleTest.kt`
- Create: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/ui/RagAnswerUiTest.kt`

- [ ] **Step 1：将正式默认模式改回 ADAPTIVE。** 保留 ALL_QUERIES 为显式实验开关；问候路径断言 E5、DAO、Guard 和 checkpoint 调用均为 0。
- [ ] **Step 2：实现 15 秒阶段 watchdog。** 超时必须恢复事务、恢复输入框并显示本地提示。
- [ ] **Step 3：完成编辑和会话切换状态矩阵。** RAG turn 与时间线编辑互斥，旧引用正确截断。
- [ ] **Step 4：增加三个 RAG 阶段文案。** 普通聊天不显示阶段，禁止伪百分比。
- [x] **Step 5：增加来源 chip 和定位。** 来源 chip、当前索引块定位、归档摘录、“来源已删除”和“当前索引不可用”状态已完成；外部 PDF 页/表格单元格二进制深链不作为最小闭环门槛。
- [ ] **Step 6：完成无障碍和视觉检查。** 使用现有淡蓝、绿色和红色状态体系。

### Task 6：全链验收、灰度和文档

**Files:**
- Create: `app/src/androidTest/java/com/example/minicpm_v_demo/rag/RagEndToEndPerformanceTest.kt`
- Modify: `README_MODIFIED_zh.md`
- Modify: `docs/architecture/ADR-001-local-rag-stack.md`
- Modify: `docs/architecture/rag-threat-model.md`
- Modify: `docs/superpowers/plans/2026-08-18-minicpm-android-unified-progress-plan.md`

- [ ] **Step 1：执行功能矩阵。** 覆盖第 7.8 节全部场景并保存聚合证据。
- [ ] **Step 2：执行 checkpoint 压力矩阵。** 100 次成功、50 次取消、20 次前后台切换后活动 checkpoint 必须为 0。
- [ ] **Step 3：执行性能矩阵。** 空历史、10 轮和 30 轮分别测试普通聊天与 RAG。
- [ ] **Step 4：执行安全矩阵。** 隐私、违法、无图、RAG 视觉绕过、提示注入和伪引用不得退化。
- [ ] **Step 5：执行固定签名覆盖安装。** 只使用 `adb install -r`，验证会话、知识库和模型仍存在。
- [ ] **Step 6：加入 `low_latency_rag_v1` 灰度开关。** 自检失败只关闭 RAG，普通聊天仍可用。
- [ ] **Step 7：更新 README 和发布说明。** 只有全部门槛通过后才能把 RAG 从“开发中”改为“测试版”。

## 9. 验证命令

### 9.1 Android 构建和 JVM 回归

```powershell
.\gradlew.bat --no-daemon --max-workers=1 :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:verifyInstallationSigning -x buildGgmlCpu_v86
```

预期：`BUILD SUCCESSFUL`。禁止运行 `connectedCheck` 或任何 `connected*AndroidTest`。

### 9.2 安全真机 instrumentation

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb install -r app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk
.\scripts\run-device-instrumentation.ps1 -ClassName <测试类完整名称>
```

执行前必须确认主 APK 和测试 APK 签名一致。脚本参数中的测试类必须替换为本轮明确要运行的类，不允许无筛选执行全部 instrumentation。

### 9.3 Guard Python 回归

```powershell
$env:PYTHONPATH = 'D:\MiniCPM-V\.rag-python-tools;D:\MiniCPM-V\MiniCPM-V-Apps\MiniCPM-V-demo-Android'
& 'C:\Users\mingjun.dong\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' -m unittest discover -s tools\rag_guard -p 'test_*.py' -v
```

当前证据：22 项发现，19 项通过，3 项需要训练依赖而按设计跳过，0 失败。

## 10. 发布门槛

在以下全部成立前，RAG 只能标记为“开发中”：

- [ ] Answerability 真实办公独立测试通过并写入固定 profile。
- [ ] Groundedness 已接入真实生成路径并通过错误金额/日期/伪引用测试。
- [ ] 证据 reducer 和真实 token 预算生效。
- [ ] 普通聊天使用 ADAPTIVE 零检索路径。
- [ ] 小库向量缓存完成；若声明支持大库，则 HNSW/分区后端完成。
- [ ] 来源 chip、删除来源快照和阶段 UI 完成。
- [ ] 生命周期、watchdog、编辑和取消矩阵通过。
- [ ] 功能、安全、压力、性能和固定签名覆盖安装全部通过。
- [ ] README、威胁模型和已知限制与当前代码一致。

## 11. 当前下一步

当前执行点是 **Task 1 v3 模型导出/profile 固定 + Task 2 真机验证**；随后继续 Task 4 大库后端和 Task 5 生命周期/UI 剩余项。

工具链、模型、CPU 环境和匿名格式示例均已就绪；真正阻塞项是经过授权和人工脱敏的真实办公校准/测试样本。在这些数据到位前，不得将 `classifier=null/profile=null` 改为生产模型，也不得声称语义 Answerability 或 Groundedness 已正式启用。

## 12. Graphify 持久知识图谱维护

项目知识图谱固定保存在 `graphify-out/`，纳入后续本地开发流程：

- `graph.json`：可查询的原始图谱。
- `GRAPH_REPORT.md`：社区、God Nodes、跨社区关系和建议问题报告。
- `graph.html`：离线交互式可视化。
- `.graphify_labels.json`：社区名称。
- `manifest.json`：增量检测基线。
- `cost.json`：语义提取 token 审计；无法取得子代理 token 遥测时必须明确记为 0 和不可用，不能伪造。

维护规则：

1. 回答代码结构问题前，优先使用 `graphify query/path/explain`。
2. 每次修改代码后执行 `graphify update .`。
3. 每次修改计划、ADR、威胁模型、README 或其他文档后，必须执行语义增量提取；仅运行 AST update 不算完成。
4. 完成任务前执行 `graphify check-update .`，不得留下未说明的 semantic pending 状态。
5. Graphify 提取失败、dangling edge、syntax warning 和 edge-collapse 诊断必须如实记录。
6. `post-commit/post-checkout` Git hook 为推荐补充机制，但不能替代任务结束前的显式检查。

当前首次构建范围为 Android 项目根目录。重复启动图标、TTS 参考音频、构建目录、模型二进制和生成训练语料通过 `.graphifyignore` 排除；它们的架构含义由代码、manifest 和文档表示。
