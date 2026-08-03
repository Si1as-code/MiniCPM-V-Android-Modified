# MiniCPM-V Android 改版说明

本版本基于官方仓库 [OpenBMB/MiniCPM-V-Apps](https://github.com/OpenBMB/MiniCPM-V-Apps) 的 Android Demo 2.3（基础提交 `2b4049fd877be538e77cae5122204ee0ea3ac34c`），增加沉浸式状态栏、聊天拍照入口和“先预处理、后推理”的图片发送流程。除下述 Android 改动外，iOS、HarmonyOS 和共享 `llama.cpp-omni` 子模块仍保持官方仓库结构。

## 功能变更

### 1. 使用时隐藏顶部状态栏

- 主界面、模型管理和 TTS 界面进入后隐藏系统状态栏。
- 保留底部导航栏；从屏幕边缘滑动时，系统栏可临时显示。
- Activity 恢复前台或窗口重新获得焦点时会再次应用隐藏状态。

### 2. 聊天输入区增加拍照按钮

- 拍照按钮位于相册按钮和发送按钮之间。
- 调用系统相机完成拍摄，不直接申请 `CAMERA` 权限。
- 拍摄文件只通过未导出的 `FileProvider` 临时共享，并且共享范围限制在应用缓存目录 `cache/camera/`。
- 用户取消拍照、预处理失败或清除对话时会清理对应临时文件。

### 3. 图片先缓存并预处理，再发送推理

相册选择或拍照完成后的流程如下：

1. 图片立即出现在聊天输入区的待发送卡片中。
2. 预处理期间图片变暗，中央显示圆形进度指示。
3. 应用读取 EXIF 方向、生成最大边 512 px 的预览缩略图，并按限制缩放模型输入图。
4. 原生图像预填充完成后才显示 `100%`；此时图片可以发送。
5. 点击发送只把已预填充的图像上下文和文本交给推理，不会再次执行图片预处理或图像预填充。

当前 JNI 接口只提供一次阻塞式图像预填充调用，因此处理中使用不确定进度圆环；只有原生调用真实成功后才显示 `100%`，不展示虚假的中间百分比。

## 图片安全与资源限制

- 单个源文件最大 64 MiB。
- 模型输入最大边 4096 px，最大约 419 万像素。
- 解码采用 2 的幂次采样，避免直接展开超大图片导致内存峰值。
- 支持 EXIF 1–8 的旋转和镜像方向。
- 完整模型位图编码后立即回收，只保留输入区/消息列表所需的缩略图。
- 同一时间只保留一个待发送图片；新选择会取消并替换旧任务。
- 切换模型、清除对话或 Activity 销毁时会等待媒体任务退出，避免与原生上下文并发访问。

## 构建

环境要求：

- JDK 21
- Android SDK / compileSdk 36
- Android NDK `27.0.12077973`
- CMake 4.1.2 与 Ninja

PowerShell：

```powershell
$env:ANDROID_HOME = 'D:\android\SDK'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

项目的自定义 v8.6 CPU 原生库任务显式使用 SDK 自带 Ninja，并复用标准 CMake 构建下载好的 KleidiAI 源码，因此离线重建时不会再次访问 GitHub。

## 安装

生成文件：

```text
app\build\outputs\apk\debug\app-debug.apk
```

这是 Debug 签名 APK。如果手机已安装官方签名的同包名版本，Android 不允许直接覆盖；需先卸载 `com.example.minicpm_v_demo`。卸载会删除该应用的内部数据和已下载模型，请先确认数据是否需要保留。

```powershell
adb uninstall com.example.minicpm_v_demo
adb install app\build\outputs\apk\debug\app-debug.apk
```

## 验证结果

| 检查项 | 结果 |
|---|---|
| 单元测试 | 15/15 通过 |
| Android Lint | 通过，无阻断错误 |
| Android 测试代码编译 | 通过 |
| Debug APK 组装 | 通过 |
| APK 签名校验 | APK Signature Scheme v2 验证通过 |
| arm64 原生库 | `libminicpm_v_demo.so`、`libggml-cpu-v86.so` 均已打包 |
| 真机安装与启动 | 等待设备重新连接后验证 |

仪器测试覆盖相机 `FileProvider` 的允许/拒绝路径，以及主界面拍照按钮、待发送区和状态栏隐藏状态。完整图像推理仍需要手机上存在兼容模型。
