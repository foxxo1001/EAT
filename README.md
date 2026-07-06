<p align="center">
  <img src="https://img.shields.io/badge/platform-Android-3DDC84" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.00-4285F4" alt="Compose" />
  <img src="https://img.shields.io/badge/minSdk-29%20(Android%2010)-34A853" alt="Min SDK" />
  <img src="https://img.shields.io/badge/targetSdk-35-34A853" alt="Target SDK" />
</p>

<h1 align="center">🍜 今天吃啥 · EAT</h1>

<p align="center">
  <b>一个用 Jetpack Compose 打造的决定困难症治愈器</b><br />
  转动转盘，让「今天吃什么」不再纠结。
</p>

---

> **TodayEat** is a modern Android app built entirely with Jetpack Compose.
> Spin the glassy dial, let physics-driven springs do the work, and never
> agonize over lunch again.

> 🤖 **Vibe Coding 声明**：本项目 **100% 由 AI 结对编程（Vibe Coding）完成**。
> 架构设计、Compose UI、手搓物理弹簧动画、Room 持久化、手势熔断，乃至构建与
> GitHub 发布，全部由人类用自然语言描述意图、AI 实时生成并迭代代码而成——
> **不包含任何人工手写的业务代码**。这是一个「说一句话，AI 写代码」的纯粹产物。

## 📑 目录

- [✨ 特性](#-特性)
- [📱 截图](#-截图)
- [🏗 技术栈](#-技术栈)
- [🧩 架构](#-架构)
- [🚀 快速开始](#-快速开始)
- [📦 项目结构](#-项目结构)
- [🔖 版本管理约定](#-版本管理约定)
- [🤝 贡献](#-贡献)
- [📄 开源协议](#-开源协议)

## ✨ 特性

| 特性 | 说明 |
| --- | --- |
| 🎡 **液态玻璃转盘** | 自定义 `GraphicsLayer` 绘制的高光/渐变玻璃质感转盘，拒绝千篇一律的 Material 模板。 |
| 🌀 **物理弹簧动画** | 采用 `Spring.StiffnessMedium` / `DampingRatioMediumBouncy` 手感参数，开合、抖动、滑出皆具弹性。 |
| 📋 **六大面板** | 历史记录、菜单池、设置、统计、收藏、更新日志，底部抽屉式交互。 |
| 🖼 **食物配图** | 通过 Unsplash API 拉取菜品图片（需 `INTERNET` 权限），`Coil` 原生加载。 |
| 💾 **本地持久化** | `Room` 存储抽中历史与收藏，支持 `Flow` 响应式查询。 |
| 📳 **触觉反馈** | 转动与命中时触发马达振动（`VIBRATE`）。 |
| 📱 **瘦长屏优化** | 针对 OPPO Find X8（6.59″ / 359×789dp / density 3.5）等 19.8:9 屏幕做了布局适配。 |

## 📱 截图

> 📷 截图待补充。建议在 `docs/screenshots/` 下放置以下画面后在此引用：
> - 主界面转盘（含结果菜名框）
> - 历史记录面板
> - 统计面板（抽中频次可视化）

```markdown
<!-- 示例：
![主界面](docs/screenshots/home.png)
-->
```

## 🏗 技术栈

| 分类 | 技术 |
| --- | --- |
| 语言 | **Kotlin 1.9** (JVM target 17) |
| UI | **Jetpack Compose** BOM `2024.09.00`、Material 3、`material-icons-extended` |
| 架构 | 单 `Activity` + Compose、`CompositionLocal` 全局状态广播 |
| 持久化 | **Room** `2.6.1`（`room-ktx` + KSP 注解处理） |
| 图片 | **Coil** `2.6.0`（`coil-compose`，`SubcomposeAsyncImage`） |
| 异步 | **Kotlin Coroutines** `1.8.1`、Lifecycle `2.8.6`（`runtime-ktx` / `runtime-compose`） |
| 构建 | **Gradle 8.5**（本地发行版离线构建），AGP + KSP |

**权限声明**

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
```

## 🧩 架构

应用采用 **单 Activity + Compose** 结构，所有界面与交互逻辑集中在 `MainActivity.kt`，
数据层通过 `Room` 持久化。

```
UI 层 (Compose)
   └── MainActivity.kt            # 转盘、面板宿主、手势熔断、CHANGE_LOG
数据层 (Room)
   ├── AppDatabase.kt             # RoomDatabase 单例
   ├── HistoryEntity / HistoryDao # 抽中历史
   └── FavoriteEntity / FavoriteDao # 收藏菜品
```

**关键设计约定**（详见 [`CLAUDE.md`](CLAUDE.md)）

- **手势命中测试**：禁用某区域手势必须**物理移除** pointer 修饰符
  （`if (interactive) this.clickable(...) else this`），绝不用
  `clickable(enabled = false)`——后者仍会吞掉指针事件。
- **面板手势熔断**：用 `compositionLocalOf<Boolean>` + `CompositionLocalProvider`
  广播「是否可交互」，面板 `dismiss()` 触发瞬间即翻转，避免退场动画期间的拦截窗口。
- **视觉铁律**：不改动液态玻璃材质与弹簧刚度常量，保持统一手感。

## 🚀 快速开始

### 环境要求

- **Android SDK** `compileSdk = 35`，`targetSdk = 35`
- **JDK 17**
- **Gradle 8.5**（本项目附带本地发行版，适合离线环境）

### 构建 Release 包

```bash
# 方式一：Gradle Wrapper（联网拉取依赖）
./gradlew assembleRelease

# 方式二：本地 Gradle 8.5 离线发行版（推荐无网/代理环境）
gradle-8.5/bin/gradle assembleRelease
```

产物位置：

```
app/build/outputs/apk/release/app-release.apk
```

> 💡 构建细节（AGP spool、D8 堆内存、离线缓存等）见 [`CLAUDE.md`](CLAUDE.md)。

### 安装到设备

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 📦 项目结构

```
EAT/
├── app/
│   ├── build.gradle.kts              # 应用模块配置（SDK / 依赖 / 版本号）
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml        # 权限与入口 Activity
│       ├── java/com/example/todayeat/
│       │   ├── MainActivity.kt        # 主界面 + 六大面板 + 手势熔断
│       │   └── data/
│       │       ├── AppDatabase.kt
│       │       ├── HistoryEntity.kt / HistoryDao.kt
│       │       └── FavoriteEntity.kt / FavoriteDao.kt
│       └── res/                        # 资源（mipmap / values / xml ...）
├── gradle/                            # Gradle Wrapper
├── gradlew / gradlew.bat
├── build.gradle.kts                   # 根工程配置
├── settings.gradle.kts
├── CLAUDE.md                          # AI 编码约定与防坑规则
├── .gitignore
└── README.md
```

## 🔖 版本管理约定

本项目每次修改代码都遵循统一版本规范（见 `app/build.gradle.kts`）：

1. `versionCode` **+1**，`versionName` 升一小位（如 `4.14.3 → 4.14.4`）。
2. 在 `MainActivity.kt` 顶部的 `CHANGE_LOG` 列表**最前**追加一条
   `ChangeLogEntry`（版本号 / 日期 / 要点）。
3. 将上述改动合并为一个 commit，例如：
   ```bash
   git commit -m "bump 4.14.3 → 4.14.4: 新增 xx 特性"
   ```

## 🤝 贡献

1. Fork 本仓库并创建特性分支：`git checkout -b feat/your-feature`
2. 提交改动：`git commit -m "feat: ..."`
3. 推送到分支：`git push origin feat/your-feature`
4. 发起 Pull Request

> ⚚ 提交前请确保遵循 [`CLAUDE.md`](CLAUDE.md) 中的 Compose 手势与视觉约定。

## 📄 开源协议

⚠️ 当前仓库**尚未包含 LICENSE 文件**。如需以开源协议发布，请在根目录添加
对应协议文件（如 `LICENSE`）后重新提交。在明确协议前，代码保留所有权利。

---

<p align="center">
  Made with 💚 & Compose · <a href="https://github.com/foxxo1001/EAT">github.com/foxxo1001/EAT</a>
</p>
