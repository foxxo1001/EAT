# CLAUDE.md — 项目约定与防坑规则（EAT / TodayEat Android）

本文件记录所有 Claude/AI 编码会话都应遵守的项目事实与"防复发"规则。

## 构建与依赖（gradle）
- 用本地 Gradle：`gradle-8.5/bin/gradle.bat`（wrapper 会尝试联网下载，离线环境直接用本地发行版）。
- 构建必须带：`-Dcom.android.tools.analytics.spoolFileLocation=/tmp/agp-spool/spool`（先 `mkdir -p /tmp/agp-spool`），否则 AGP AnalyticsService 初始化失败。
- 不要 `--offline`：本地缺 `ui-tooling`/`ui-test-manifest` 等缓存，离线会解析失败；走在线拉取。
- D8 dex 合并需大堆：`-Dorg.gradle.jvmargs="-Xmx6g -XX:MaxMetaspaceSize=1g"`，否则 `OutOfMemoryError`。
- 出包：`assembleRelease` 即可（build.gradle.kts 中 release 已复用 debug 签名，产物为 `app/build/outputs/apk/release/app-release.apk`）。

## 版本与变更日志（每次改代码必做）
- `app/build.gradle.kts`：`versionCode` +1、`versionName` 升一小位。
- `MainActivity.kt` 顶部 `CHANGE_LOG`：在列表**最前**追加一条新 `ChangeLogEntry`（版本号 / 日期 / 要点）。

## ⚠️ Compose 手势 / 命中测试（高优先级防坑）
- **禁用某区域手势以释放命中测试，绝不能靠 `clickable(enabled = false)`**——它只禁回调，pointer 节点仍参与 hit-test 并吞掉指针事件，下层组件收不到点击。
- 正确做法：不可交互时**物理移除 pointer 修饰符**：
  - clickable 类：`if (interactive) this.clickable(...) else this`
  - pointerInput 类（拖拽）：`.then(if (interactive) Modifier.pointerInput(Unit){...} else Modifier)`
- 全局面板手势开关模式：用 `compositionLocalOf<Boolean>` + `CompositionLocalProvider` 广播"是否可交互"；面板 `dismiss()` 触发**瞬间**即翻转（绑定面板自身 `isDismissing` 状态），不要等宿主 `visible` 翻转——否则退场动画期间仍有拦截窗口（曾导致"退出面板后导航栏约1秒无响应"）。
- 严守：不改动液态玻璃材质（border 高光/渐变）与弹簧刚度参数（`Spring.StiffnessMedium` / `DampingRatioMediumBouncy` 等）。
