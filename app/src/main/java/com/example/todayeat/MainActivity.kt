package com.example.todayeat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.math.roundToInt
import kotlin.random.Random
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.time.LocalTime
import com.example.todayeat.data.AppDatabase
import com.example.todayeat.data.HistoryDao
import com.example.todayeat.data.HistoryEntity
import com.example.todayeat.data.HistoryWithCount
import com.example.todayeat.data.FoodCount
import com.example.todayeat.data.FavoriteDao
import com.example.todayeat.data.FavoriteEntity

// ============================================================
//  美食候选池
// ============================================================

private val FOOD_POOL = listOf(
    "麻辣烫", "火锅", "汉堡", "螺蛳粉", "寿司", "披萨",
    "炸鸡", "烤肉", "牛排", "拉面", "酸菜鱼", "烧烤",
    "黄焖鸡", "煲仔饭", "小龙虾", "烤鱼", "麻辣香锅",
    "沙茶面", "兰州拉面", "云南米线", "桂林米粉",
    "烤鸭", "麻辣拌", "关东煮", "酸辣粉", "热干面",
    "蛋炒饭", "饺子", "馄饨", "煎饼果子", "肉夹馍",
    "回锅肉", "地三鲜", "宫保鸡丁", "麻婆豆腐", "鱼香肉丝",
    // 早餐
    "油条", "包子", "肠粉", "豆腐脑", "小米粥", "茶叶蛋", "豆浆",
    // 午餐
    "牛肉面", "卤肉饭", "盖浇饭", "红烧肉", "糖醋里脊", "白切鸡",
    // 下午茶 / 饮品
    "奶茶", "蛋挞", "蛋糕", "冰淇淋", "水果捞", "珍珠奶茶",
    "冰美式", "拿铁", "柠檬茶", "橙汁", "可乐",
    // 晚餐
    "水煮鱼", "干锅", "石锅拌饭", "铁板烧",
    // 宵夜
    "炒面", "烤串", "花甲粉"
)

// ============================================================
//  菜名视觉配色（温暖纯色兜底用，降饱和度）
// ============================================================

private data class FoodVisual(
    val gradientStart: Color,
    val gradientEnd: Color
)

private val FOOD_VISUALS: Map<String, FoodVisual> = mapOf(
    "麻辣烫" to FoodVisual(Color(0xFFE8A0A0), Color(0xFFD4856B)),
    "火锅"   to FoodVisual(Color(0xFFE8B080), Color(0xFFD49560)),
    "汉堡"   to FoodVisual(Color(0xFFE8C090), Color(0xFFD4A570)),
    "螺蛳粉" to FoodVisual(Color(0xFFC0A0C8), Color(0xFFA885B0)),
    "寿司"   to FoodVisual(Color(0xFFA0C8C0), Color(0xFF85B0A0)),
    "披萨"   to FoodVisual(Color(0xFFE0B888), Color(0xFFCC9A68)),
    "炸鸡"   to FoodVisual(Color(0xFFE8D090), Color(0xFFD4B870)),
    "烤肉"   to FoodVisual(Color(0xFFD89890), Color(0xFFC07870)),
    "牛排"   to FoodVisual(Color(0xFFB08868), Color(0xFF987048)),
    "拉面"   to FoodVisual(Color(0xFFE0CCA0), Color(0xFFCCB080)),
    "酸菜鱼" to FoodVisual(Color(0xFF90C8C0), Color(0xFF70A8A0)),
    "烧烤"   to FoodVisual(Color(0xFFD89888), Color(0xFFC07868)),
    "黄焖鸡" to FoodVisual(Color(0xFFE0AA98), Color(0xFFCC8878)),
    "煲仔饭" to FoodVisual(Color(0xFFE8D0A0), Color(0xFFD4B080)),
    "小龙虾" to FoodVisual(Color(0xFFE09098), Color(0xFFCC7078)),
    "烤鱼"   to FoodVisual(Color(0xFF98B8D8), Color(0xFF7898C0)),
    "麻辣香锅" to FoodVisual(Color(0xFFD8A098), Color(0xFFC08078)),
    "沙茶面" to FoodVisual(Color(0xFFE0A8B8), Color(0xFFCC8898)),
    "兰州拉面" to FoodVisual(Color(0xFFE0C088), Color(0xFFCCA068)),
    "云南米线" to FoodVisual(Color(0xFFA0D0B8), Color(0xFF80B098)),
    "桂林米粉" to FoodVisual(Color(0xFFA0C8C8), Color(0xFF80A8A8)),
    "烤鸭"   to FoodVisual(Color(0xFFE0B888), Color(0xFFCC9868)),
    "麻辣拌" to FoodVisual(Color(0xFFB0A0D0), Color(0xFF9080B8)),
    "关东煮" to FoodVisual(Color(0xFFD0C8C0), Color(0xFFB0A8A0)),
    "酸辣粉" to FoodVisual(Color(0xFFD89898), Color(0xFFC07878)),
    "热干面" to FoodVisual(Color(0xFFE0D098), Color(0xFFCCB078)),
    "蛋炒饭" to FoodVisual(Color(0xFFE8E090), Color(0xFFD0C070)),
    "饺子"   to FoodVisual(Color(0xFFE8E0A8), Color(0xFFCCB078)),
    "馄饨"   to FoodVisual(Color(0xFFC0E0E0), Color(0xFFA0C8C8)),
    "煎饼果子" to FoodVisual(Color(0xFFE0C8A0), Color(0xFFCCA880)),
    "肉夹馍" to FoodVisual(Color(0xFFE0A898), Color(0xFFCC8878)),
    "回锅肉" to FoodVisual(Color(0xFFD08880), Color(0xFFB87068)),
    "地三鲜" to FoodVisual(Color(0xFFA0C8A0), Color(0xFF80A880)),
    "宫保鸡丁" to FoodVisual(Color(0xFFD8A878), Color(0xFFC08858)),
    "麻婆豆腐" to FoodVisual(Color(0xFFD08880), Color(0xFFB07068)),
    "鱼香肉丝" to FoodVisual(Color(0xFFD89888), Color(0xFFC07868)),
    // 早餐
    "油条" to FoodVisual(Color(0xFFE8D898), Color(0xFFD4B868)),
    "包子" to FoodVisual(Color(0xFFE8E8D0), Color(0xFFD4D4A0)),
    "肠粉" to FoodVisual(Color(0xFFE0E0C8), Color(0xFFC8C8A8)),
    "豆腐脑" to FoodVisual(Color(0xFFF0E8C8), Color(0xFFD8C8A0)),
    "小米粥" to FoodVisual(Color(0xFFE8D8A8), Color(0xFFD4C080)),
    "茶叶蛋" to FoodVisual(Color(0xFFC8A080), Color(0xFFB08860)),
    "豆浆" to FoodVisual(Color(0xFFE8E8C0), Color(0xFFD4D4A0)),
    // 午餐
    "牛肉面" to FoodVisual(Color(0xFFD8A878), Color(0xFFC08858)),
    "卤肉饭" to FoodVisual(Color(0xFFC89878), Color(0xFFB08060)),
    "盖浇饭" to FoodVisual(Color(0xFFD8C098), Color(0xFFC0A878)),
    "红烧肉" to FoodVisual(Color(0xFFC88868), Color(0xFFB07050)),
    "糖醋里脊" to FoodVisual(Color(0xFFE0A878), Color(0xFFCC8858)),
    "白切鸡" to FoodVisual(Color(0xFFE8D8B8), Color(0xFFD4C098)),
    // 下午茶 / 饮品
    "奶茶" to FoodVisual(Color(0xFFD4B896), Color(0xFFC09878)),
    "蛋挞" to FoodVisual(Color(0xFFE8D0A0), Color(0xFFD4B880)),
    "蛋糕" to FoodVisual(Color(0xFFF0D8C8), Color(0xFFD8C0A8)),
    "冰淇淋" to FoodVisual(Color(0xFFD8E8F0), Color(0xFFB8D0E0)),
    "水果捞" to FoodVisual(Color(0xFF98D898), Color(0xFF78B878)),
    "珍珠奶茶" to FoodVisual(Color(0xFFD0B8A0), Color(0xFFB89880)),
    "冰美式" to FoodVisual(Color(0xFF887058), Color(0xFF685038)),
    "拿铁" to FoodVisual(Color(0xFFC8A888), Color(0xFFA88868)),
    "柠檬茶" to FoodVisual(Color(0xFFD8E8A0), Color(0xFFC0D888)),
    "橙汁" to FoodVisual(Color(0xFFF0C888), Color(0xFFD8A868)),
    "可乐" to FoodVisual(Color(0xFF604040), Color(0xFF402828)),
    // 晚餐
    "水煮鱼" to FoodVisual(Color(0xFFD08080), Color(0xFFB86060)),
    "干锅" to FoodVisual(Color(0xFFD89880), Color(0xFFC07860)),
    "石锅拌饭" to FoodVisual(Color(0xFFD8C098), Color(0xFFC0A078)),
    "铁板烧" to FoodVisual(Color(0xFFC89078), Color(0xFFB07058)),
    // 宵夜
    "炒面" to FoodVisual(Color(0xFFD8C088), Color(0xFFC0A068)),
    "烤串" to FoodVisual(Color(0xFFD89878), Color(0xFFC07858)),
    "花甲粉" to FoodVisual(Color(0xFFC8A8C0), Color(0xFFB088A0))
)


// ============================================================
//  主题/字体管理
// ============================================================

// 全局字体：由设置页驱动，整个 App 的 Text 都回退读取此值
val LocalAppFontFamily = staticCompositionLocalOf<FontFamily> { FontFamily.Default }

// 全局手势熔断器：面板退场（visible = false）的瞬间降为 false，
// 瞬时广播至所有子孙组件，使其 clickable 立即失活，
// 彻底消除退场动画期间子组件拦截命中测试形成的"隐形空气墙"。
val LocalPanelInteractive = compositionLocalOf { true }

// 安全点击修饰符：替代面板内所有普通 clickable。
// enabled / LocalPanelInteractive.current 联动，退场即瞬时释放手势。
//
// ⚠️ 关键修复：不可交互时必须「彻底不挂载 clickable 节点」，而非 enabled = false。
// Compose 中 enabled=false 的 clickable 仍会参与命中测试并吞掉指针事件，
// 导致退场动画期间全屏 Scrim 继续拦截导航栏——这正是「退出后要等约 1 秒」的根因。
// 因此不可交互时直接返回 this（不附加任何 pointer 节点），物理释放命中测试。
@Composable
fun Modifier.panelClickable(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    indication: Indication? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val isInteractive = LocalPanelInteractive.current
    return if (enabled && isInteractive) {
        this.clickable(
            interactionSource = interactionSource,
            indication = indication,
            onClick = onClick
        )
    } else {
        this
    }
}

private object ThemeManager {
    private const val PREFS_NAME = "todayeat_theme"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_FONT = "font_family"

    enum class ThemeMode(val label: String) { LIGHT("浅色"), DARK("深色") }

    enum class FontChoice(val label: String, val fontFamily: FontFamily) {
        SYSTEM_DEFAULT("系统默认", FontFamily.Default),
        SANS_SERIF("无衬线", FontFamily.SansSerif),
        SERIF("衬线", FontFamily.Serif),
        MONOSPACE("等宽", FontFamily.Monospace)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getTheme(context: Context): ThemeMode =
        ThemeMode.entries.getOrElse(prefs(context).getInt(KEY_THEME, 0)) { ThemeMode.LIGHT }

    fun setTheme(context: Context, mode: ThemeMode) {
        prefs(context).edit().putInt(KEY_THEME, mode.ordinal).apply()
    }

    fun getFont(context: Context): FontChoice =
        FontChoice.entries.getOrElse(prefs(context).getInt(KEY_FONT, 0)) { FontChoice.SYSTEM_DEFAULT }

    fun setFont(context: Context, choice: FontChoice) {
        prefs(context).edit().putInt(KEY_FONT, choice.ordinal).apply()
    }

    fun isDark(context: Context) = getTheme(context) == ThemeMode.DARK

    fun getFavoritesOnly(context: Context): Boolean =
        prefs(context).getBoolean("favorites_only", false)

    fun setFavoritesOnly(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean("favorites_only", enabled).apply()
    }

    fun applyColors(context: Context): ThemeColors {
        val dark = isDark(context)
        return ThemeColors(
            backgroundStart = if (dark) Color(0xFF1A1A1E) else Color(0xFFF5F5F7),
            backgroundEnd = if (dark) Color(0xFF222228) else Color(0xFFE8E8EA),
            surface = if (dark) Color(0xFF2C2C30).copy(alpha = 0.90f) else Color(0xFFF2F2F7).copy(alpha = 0.90f),
            surfaceStrong = if (dark) Color(0xFF3A3A3E).copy(alpha = 0.92f) else Color(0xFFF2F2F7).copy(alpha = 0.95f),
            textPrimary = if (dark) Color(0xFFF0F0F0) else Color(0xFF1C1C1E),
            textSecondary = if (dark) Color(0xFFA0A0A5) else Color(0xFF555555),
            textTertiary = if (dark) Color(0xFF636368) else Color(0xFF888888),
            borderLight = if (dark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.5f),
            capsuleBg = if (dark) Color(0xFF2C2C30).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.55f)
        )
    }
}

data class ThemeColors(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val surface: Color,
    val surfaceStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val borderLight: Color,
    val capsuleBg: Color
)

// ============================================================
//  版本更新日志
// ============================================================

private data class ChangeLogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

private val CHANGE_LOG = listOf(
    ChangeLogEntry("v4.14.3", "2026-07-06", listOf(
        "根除退场「约1秒无响应」：panelClickable 改为不可交互时彻底不挂载 clickable 节点",
        "修正 Compose 陷阱——enabled=false 的 clickable 仍参与命中测试吞掉指针，导致 Scrim 持续拦截导航栏",
        "改为不可交互时返回 this（物理移除 pointer 节点），dismiss 触发瞬间导航栏即恢复响应",
        "版本号升级至 v4.14.3 (build 47)",
    )),
    ChangeLogEntry("v4.14.2", "2026-07-06", listOf(
        "彻底修复退场动画期间「隐形空气墙」：dismiss 触发瞬间即切断 Scrim 点击/内层点击/拖拽手势",
        "新增面板级 isDismissing 状态，与 LocalPanelInteractive 联动，退场即瞬时释放底部导航栏命中",
        "覆盖全部 6 个 BottomSheet（历史/菜单池/设置/统计/收藏/更新日志），导航栏退出面板后可立即响应",
        "版本号升级至 v4.14.2 (build 46)",
    )),
    ChangeLogEntry("v4.14.1", "2026-07-06", listOf(
        "架构级手势熔断器：新增 CompositionLocal LocalPanelInteractive + panelClickable 修饰符",
        "MacOsSheetHost 退场瞬间经 Provider 广播，子组件 clickable 瞬时失活，消除隐形空气墙",
        "彻底清洗历史/菜单池/设置/统计四个面板的点击与 Button 交互，统一接入熔断逻辑",
        "版本号升级至 v4.14.1 (build 45)",
    )),
    ChangeLogEntry("v4.14.0", "2026-07-07", listOf(
        "新增触感反馈物理引擎 HapticEngine，支持振幅量化控制 (0~255)",
        "设置页新增「交互」分类：触感开关 + 无级强度滑块 + 测试按钮",
        "全面废除系统预设原语，改用纯手工波形引擎精准驱动马达",
        "版本号升级至 v4.14.0 (build 44)",
    )),
    ChangeLogEntry("v4.13.1", "2026-07-06", listOf(
        "还原为 AnimatedVisibility + Sheet 架构，取消 HorizontalPager",
        "导航栏新增水平滑动手势：左右滑动切换 Tab，50dp 阈值触发",
        "保留 iOS 18 液态玻璃导航栏效果",
        "版本号升级至 v4.13.1 (build 43)",
    )),
    ChangeLogEntry("v4.13.0", "2026-07-06", listOf(
        "新增 HorizontalPager 滑动切换：主页左右滑动切换五个页面",
        "导航栏升级为 iOS 18 液态玻璃效果：多层渲染、高光条、折射边框",
        "菜单池/历史/收藏/统计改为内嵌页面，支持滑动浏览",
        "导航栏与 Pager 联动：点击 Tab 平滑滚动到对应页",
        "版本号升级至 v4.13.0 (build 42)",
    )),
    ChangeLogEntry("v4.12.0", "2026-07-06", listOf(
        "引入 MacOsSheetHost 生命周期安全容器，基于 updateTransition 状态机",
        "所有 Sheet 退场动画不再被物理断流，transition 完全收拢后才销毁渲染树",
        "修复设置页退出时导航栏收藏 Tab 闪烁（改用独立 showSettings 状态）",
        "版本号升级至 v4.12.0 (build 41)",
    )),
    ChangeLogEntry("v4.11.1", "2026-07-06", listOf(
        "修复设置页退出时导航栏收藏Tab闪烁问题",
        "设置页改用独立状态控制，不再占用导航Tab选中态",
        "版本号升级至 v4.11.1 (build 40)",
    )),
    ChangeLogEntry("v4.11.0", "2026-07-06", listOf(
        "新增收藏模式：支持仅从收藏菜品中加权抽取",
        "收藏页全面升级 macOS 红绿灯风格，对齐统计页视觉",
        "主页收藏模式指示徽章，盲盒盘呼吸光环联动变色",
        "导航栏重排：收藏移至第4位，设置放最后",
        "全面清除界面 Emoji，改用 Canvas 矢量爱心和 Material Icon",
        "更新日志面板重构：Typography Tags 分类标签 + Hero Card + iOS 关闭按钮",
        "版本号升级至 v4.11.0 (build 39)",
    )),
    ChangeLogEntry("v4.10.0", "2026-07-06", listOf(
        "新增收藏功能：抽奖结果旁点击爱心收藏菜品",
        "导航栏新增「收藏」Tab，查看收藏列表和管理",
        "支持从收藏中随机抽取，只看最爱的菜",
        "收藏数据本地持久化，卸载前不丢失",
    )),
    ChangeLogEntry("v4.9.0", "2026-07-05", listOf(
        "设置页新增「重置历史记录」确认弹窗，防止误操作",
        "点击重置后弹出二次确认对话框，确认后清空全部历史",
        "版本号升级至 v4.9.0 (build 37)",
    )),
   ChangeLogEntry("v4.7.9", "2026-07-05", listOf(
       "菜品池从 35 道扩展至 65 道，覆盖全时段",
       "新增早餐/下午茶/饮品/宵夜等 30 道菜品和饮品",
   )),
    ChangeLogEntry("v4.8.0", "2026-07-05", listOf(
        "新增统计仪表盘页面：总抽奖次数、本周/本月最爱 TOP5",
        "时段偏好分布柱状图、最近7天抽奖趋势图",
        "macOS 经典红绿灯标题栏（红黄绿三色圆点）",
        "导航栏新增统计入口，动画与其他面板保持一致",
    )),
   ChangeLogEntry("v4.7.8", "2026-07-05", listOf(
        "引入实时时钟心跳：LocalTime + 每分钟 Tick + onResume 刷新",
        "时段判断函数重构为 hour 参数化，深度集成 Compose 状态驱动"
    )),
    ChangeLogEntry("v4.7.7", "2026-07-05", listOf(
        "完全重做菜单栏和历史页开关动画：macOS 原生风格",
        "打开: 350ms appleBezier + 从 92% 展开 + 全透明 fade-in",
        "关闭: 300ms appleBezier + 收缩至 92% + 全透明 fade-out",
        "面板位移量改为屏幕高度的 12%，适配不同设备"
    )),
    ChangeLogEntry("v4.7.5", "2026-07-05", listOf(
        "引入 iOS CubicBezier(0.32,0.72,0,1) 贝塞尔动画曲线",
        "面板切换改用 slideInVertically+slideOutVertically 物理滑动",
        "sheet dismiss 统一使用 appleBezier 曲线 350ms"
    )),
    ChangeLogEntry("v4.7.4", "2026-07-05", listOf(
        "修复面板关闭卡顿：回退 spring 为 tween(280ms) 平滑过渡"
    )),
    ChangeLogEntry("v4.7.3", "2026-07-05", listOf(
        "历史/菜单页关闭改用临界阻尼弹簧，添加旋转视差",
        "更新日志页添加全局返回手势",
        "设置页版本号同步为 4.7.3"
    )),
    ChangeLogEntry("v4.7.2", "2026-07-05", listOf(
        "全面重构毛玻璃材质：面板底色增至 85%，暗化遮罩提至 55%",
        "ThemeManager surface 升级为暖白 F2F2F7 高遮盖配色",
        "修复菜单/历史/设置页文字穿透叠影问题"
    )),
    ChangeLogEntry("v4.7.1", "2026-07-05", listOf(
        "修复首页布局：恢复白色玻璃标签和原始间距",
        "适配 CompositionLocalProvider 字体联动"
    )),
    ChangeLogEntry("v4.7.0", "2026-07-04", listOf(
        "设置页升级为全屏界面，支持字体联动切换",
        "Home 页标题改为大字排版，增强可读性"
    )),
    ChangeLogEntry("v4.6.0", "2026-07-04", listOf(
        "设置页升级为全屏界面，带入场/退场动画",
        "适配全局返回手势，从左滑动即可退出设置",
        "主界面优化 Oppo Find X8 适配：顶部避开挖孔和状态栏",
        "抽奖圆盘缩小至 260dp，菜名位置下调，液态玻璃框更紧凑"
    )),
    ChangeLogEntry("v4.4.0", "2025-11-15", listOf(
        "全新导航栏：玻璃拟态 + 自绘线条图标 + 弹性反馈",
        "抽中结果页增加「近3日已选次数」统计",
        "历史记录支持滑动删除单条记录",
        "调整默认菜品池，减少重复品类"
    )),
    ChangeLogEntry("v4.3.0", "2025-10-08", listOf(
        "新增智能权重系统：时段匹配、24h衰减、7日频次、历史偏好",
        "菜单池页面显示每道菜的实时权重条",
        "引入 Room 数据库持久化历史记录",
        "优化减少冷启动时间"
    )),
    ChangeLogEntry("v4.2.0", "2025-09-01", listOf(
        "圆形玻璃盲盒改为可抛掷卡片交互",
        "新增「就吃这个」确认按钮，写入历史",
        "补全每道菜的视觉配色体系",
        "修复深色模式下文字对比度问题"
    )),
    ChangeLogEntry("v4.1.0", "2025-08-01", listOf(
        "首次上线「今天吃啥」应用",
        "核心抽奖盘交互：点击摇晃→开盖展示结果",
        "百度图片搜索菜名图片",
        "基础历史记录展示"
    ))
)



private val appleBezier = CubicBezierEasing(0.32f, 0.72f, 0.0f, 1.0f)

// ============================================================
//  转台状态机与抽奖结果模型（v4.14.3 原始定义）
// ============================================================
private enum class PlateStatus { IDLE, SHAKING, OPENED }

private data class FoodResult(
    val chineseName: String,
    val imageUrl: String?,
    val chosenCount: Int = 0
)

// ============================================================

private enum class MealPeriod(val title: String, val timeRange: String) {
    BREAKFAST("早餐", "06:00-11:00"),
    LUNCH("午餐", "11:00-14:00"),
    AFTERNOON("下午茶", "14:00-17:00"),
    DINNER("晚餐", "17:00-21:00"),
    MIDNIGHT("夜宵", "21:00-06:00");

    companion object {
        fun getCurrent(): MealPeriod {
            val hour = java.time.LocalTime.now().hour
            return when (hour) {
                in 6..10 -> BREAKFAST
                in 11..13 -> LUNCH
                in 14..16 -> AFTERNOON
                in 17..20 -> DINNER
                else -> MIDNIGHT
            }
        }
    }
}

// 每道菜对应的时段分类
private val FOOD_TIME_TAGS: Map<String, List<MealPeriod>> = mapOf(
    // 早餐
    "煎饼果子" to listOf(MealPeriod.BREAKFAST),
    "热干面" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH),
    "饺子" to listOf(MealPeriod.BREAKFAST, MealPeriod.DINNER),
    "馄饨" to listOf(MealPeriod.BREAKFAST, MealPeriod.MIDNIGHT),
    "肉夹馍" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH),
    "蛋炒饭" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH, MealPeriod.MIDNIGHT),
    "桂林米粉" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH),
    "云南米线" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH),
    "豆浆" to listOf(MealPeriod.BREAKFAST),
    // 午餐
    "黄焖鸡" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "煲仔饭" to listOf(MealPeriod.LUNCH),
    "兰州拉面" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "宫保鸡丁" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "回锅肉" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "地三鲜" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "麻婆豆腐" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "鱼香肉丝" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "沙茶面" to listOf(MealPeriod.LUNCH),
    "关东煮" to listOf(MealPeriod.AFTERNOON, MealPeriod.MIDNIGHT),
    // 下午茶
    "寿司" to listOf(MealPeriod.AFTERNOON, MealPeriod.LUNCH),
    "汉堡" to listOf(MealPeriod.AFTERNOON, MealPeriod.LUNCH, MealPeriod.MIDNIGHT),
    "披萨" to listOf(MealPeriod.AFTERNOON, MealPeriod.LUNCH, MealPeriod.DINNER),
    "麻辣拌" to listOf(MealPeriod.AFTERNOON, MealPeriod.MIDNIGHT),
    // 晚餐
    "火锅" to listOf(MealPeriod.DINNER, MealPeriod.MIDNIGHT),
    "烤肉" to listOf(MealPeriod.DINNER),
    "牛排" to listOf(MealPeriod.DINNER),
    "烤鱼" to listOf(MealPeriod.DINNER),
    "烧烤" to listOf(MealPeriod.DINNER, MealPeriod.MIDNIGHT),
    "小龙虾" to listOf(MealPeriod.DINNER, MealPeriod.MIDNIGHT),
    "烤鸭" to listOf(MealPeriod.DINNER),
    "酸菜鱼" to listOf(MealPeriod.DINNER, MealPeriod.LUNCH),
    "麻辣香锅" to listOf(MealPeriod.DINNER, MealPeriod.LUNCH),
    "麻辣烫" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER, MealPeriod.MIDNIGHT),
    // 宵夜
    "螺蛳粉" to listOf(MealPeriod.MIDNIGHT),
    "炸鸡" to listOf(MealPeriod.MIDNIGHT, MealPeriod.AFTERNOON),
    "酸辣粉" to listOf(MealPeriod.MIDNIGHT, MealPeriod.LUNCH),
    "拉面" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER, MealPeriod.MIDNIGHT),
    // === 新增 ===
    // 早餐
    "油条" to listOf(MealPeriod.BREAKFAST),
    "包子" to listOf(MealPeriod.BREAKFAST),
    "肠粉" to listOf(MealPeriod.BREAKFAST, MealPeriod.LUNCH, MealPeriod.MIDNIGHT),
    "豆腐脑" to listOf(MealPeriod.BREAKFAST),
    "小米粥" to listOf(MealPeriod.BREAKFAST),
    "茶叶蛋" to listOf(MealPeriod.BREAKFAST),
    "豆浆" to listOf(MealPeriod.BREAKFAST),
    // 午餐
    "牛肉面" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "卤肉饭" to listOf(MealPeriod.LUNCH),
    "盖浇饭" to listOf(MealPeriod.LUNCH),
    "红烧肉" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "糖醋里脊" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    "白切鸡" to listOf(MealPeriod.LUNCH, MealPeriod.DINNER),
    // 下午茶 / 饮品
    "奶茶" to listOf(MealPeriod.AFTERNOON),
    "蛋挞" to listOf(MealPeriod.AFTERNOON),
    "蛋糕" to listOf(MealPeriod.AFTERNOON),
    "冰淇淋" to listOf(MealPeriod.AFTERNOON),
    "水果捞" to listOf(MealPeriod.AFTERNOON),
    "珍珠奶茶" to listOf(MealPeriod.AFTERNOON),
    "冰美式" to listOf(MealPeriod.BREAKFAST, MealPeriod.AFTERNOON),
    "拿铁" to listOf(MealPeriod.BREAKFAST, MealPeriod.AFTERNOON),
    "柠檬茶" to listOf(MealPeriod.AFTERNOON),
    "橙汁" to listOf(MealPeriod.BREAKFAST, MealPeriod.AFTERNOON),
    "可乐" to listOf(MealPeriod.AFTERNOON, MealPeriod.MIDNIGHT),
    // 晚餐
    "水煮鱼" to listOf(MealPeriod.DINNER),
    "干锅" to listOf(MealPeriod.DINNER),
    "石锅拌饭" to listOf(MealPeriod.DINNER),
    "铁板烧" to listOf(MealPeriod.DINNER),
    // 宵夜
    "炒面" to listOf(MealPeriod.MIDNIGHT),
    "烤串" to listOf(MealPeriod.MIDNIGHT),
    "花甲粉" to listOf(MealPeriod.MIDNIGHT)
)

private object WeightManager {
    private const val PREFS_NAME = "todayeat_weights"
    private const val KEY_CUSTOM = "custom_foods"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ---- 自定义菜品 ----

    fun getCustomFoods(context: Context): List<String> {
        val json = try { JSONObject(getPrefs(context).getString(KEY_CUSTOM, "[]") ?: "[]") } catch (_: Exception) { JSONObject() }
        val arr = json.optJSONArray("foods") ?: return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            val name = arr.optString(i, "")
            if (name.isNotEmpty()) list.add(name)
        }
        return list
    }

    fun addCustomFood(context: Context, name: String): Boolean {
        val n = name.trim()
        if (n.isEmpty() || n in FOOD_POOL) return false
        val customs = getCustomFoods(context).toMutableList()
        if (n in customs) return false
        customs.add(n)
        val json = JSONObject().apply { put("foods", org.json.JSONArray().also { customs.forEach { f -> it.put(f) } }) }
        getPrefs(context).edit().putString(KEY_CUSTOM, json.toString()).apply()
        return true
    }

    fun removeCustomFood(context: Context, name: String) {
        val customs = getCustomFoods(context).toMutableList()
        customs.remove(name)
        val json = JSONObject().apply { put("foods", org.json.JSONArray().also { customs.forEach { f -> it.put(f) } }) }
        getPrefs(context).edit().putString(KEY_CUSTOM, json.toString()).apply()
    }

    fun getAllFoods(context: Context): List<String> = FOOD_POOL + getCustomFoods(context)

    // ---- 自动权重计算 ----

    data class FoodWeightInfo(
        val foodName: String,
        val weight: Float,
        val reason: String
    )

    /**
     * 自动计算某道菜在当前时段的权重
     * - 时段匹配: 匹配时段 x2.0，不匹配 x0.3
     * - 24h内选过: x0.4（避免连续重复）
     * - 7天内频次衰减: 选过N次则 x(1 - N*0.12)，最低0.2
     * - 总历史偏好: 选过>3次 x1.15（说明喜欢）
     */
    fun calculateWeight(
        foodName: String,
        totalCount: Int,
        recentCount24h: Int,
        recentCount7d: Int
    ): FoodWeightInfo {
        val currentPeriod = MealPeriod.getCurrent()
        val timeTags = FOOD_TIME_TAGS[foodName] ?: emptyList()

        // 时段权重
        val timeWeight = when {
            currentPeriod in timeTags -> 2.0f
            timeTags.isEmpty() -> 1.0f // 自定义菜品无时段标签，保持中性
            else -> 0.3f
        }

        // 24h内衰减
        val recency24hWeight = if (recentCount24h > 0) 0.4f else 1.0f

        // 7天频次衰减
        val frequencyWeight = (1.0f - recentCount7d * 0.12f).coerceAtLeast(0.2f)

        // 历史偏好加成
        val preferenceWeight = if (totalCount > 3) 1.15f else 1.0f

        val finalWeight = (timeWeight * recency24hWeight * frequencyWeight * preferenceWeight).coerceAtLeast(0.05f)

        val reason = buildString {
            if (currentPeriod in timeTags) append("时段匹配 ") else if (timeTags.isNotEmpty()) append("非此时段 ")
            if (recentCount24h > 0) append("近期已选 ")
            if (recentCount7d > 2) append("频次较高 ")
            if (totalCount > 3) append("偏好菜品")
        }.trim().ifEmpty { "默认" }

        return FoodWeightInfo(foodName, finalWeight, reason)
    }

    /**
     * 加权随机选择
     */
    fun selectWeightedRandom(
        context: Context,
        historyWithCounts: List<HistoryWithCount>,
        favoritesOnly: List<String>? = null
    ): FoodWeightInfo {
        val allFoods = favoritesOnly?.filter { it in getAllFoods(context) } ?: getAllFoods(context)
        if (allFoods.isEmpty()) return FoodWeightInfo("暂无菜品", 0f, "收藏为空")
        val now = System.currentTimeMillis()
        val cutoff24h = now - 24 * 60 * 60 * 1000L
        val cutoff7d = now - 7 * 24 * 60 * 60 * 1000L

        // 预处理历史数据
        val totalCountMap = historyWithCounts.groupingBy { it.foodName }.eachCount()
        val count24hMap = historyWithCounts.filter { it.timestamp > cutoff24h }.groupingBy { it.foodName }.eachCount()
        val count7dMap = historyWithCounts.filter { it.timestamp > cutoff7d }.groupingBy { it.foodName }.eachCount()

        val weighted = allFoods.map { food ->
            val total = totalCountMap[food] ?: 0
            val c24h = count24hMap[food] ?: 0
            val c7d = count7dMap[food] ?: 0
            calculateWeight(food, total, c24h, c7d)
        }

        val totalWeight = weighted.sumOf { it.weight.toDouble() }
        if (totalWeight <= 0) return weighted.random()

        var r = Random.nextDouble() * totalWeight
        for (info in weighted) {
            r -= info.weight.toDouble()
            if (r <= 0) return info
        }
        return weighted.last()
    }

    /**
     * 批量获取所有菜品权重信息（用于菜单池展示）
     */
    fun getAllWeightInfos(
        context: Context,
        historyWithCounts: List<HistoryWithCount>
    ): List<FoodWeightInfo> {
        val allFoods = getAllFoods(context)
        val now = System.currentTimeMillis()
        val cutoff24h = now - 24 * 60 * 60 * 1000L
        val cutoff7d = now - 7 * 24 * 60 * 60 * 1000L

        val totalCountMap = historyWithCounts.groupingBy { it.foodName }.eachCount()
        val count24hMap = historyWithCounts.filter { it.timestamp > cutoff24h }.groupingBy { it.foodName }.eachCount()
        val count7dMap = historyWithCounts.filter { it.timestamp > cutoff7d }.groupingBy { it.foodName }.eachCount()

        return allFoods.map { food ->
            val total = totalCountMap[food] ?: 0
            val c24h = count24hMap[food] ?: 0
            val c7d = count7dMap[food] ?: 0
            calculateWeight(food, total, c24h, c7d)
        }.sortedByDescending { it.weight }
    }
}

// ============================================================
//  图片URL缓存 —— 避免菜单池滑动时重复请求
// ============================================================

private object FoodImageCache {
    private val cache = mutableMapOf<String, String>()
    fun get(foodName: String): String? = cache[foodName]
    fun put(foodName: String, url: String?) { if (url != null) cache[foodName] = url }
    fun has(foodName: String): Boolean = cache.containsKey(foodName)
}

// ============================================================
//  磨砂噪点纹理
// ============================================================

private fun DrawScope.drawFrostNoise() {
    val w = size.width
    val h = size.height
    var seed = 98765L
    fun pseudo(): Float {
        seed = (seed * 48271L) and 0x7fffffffL
        return seed.toFloat() / 0x7fffffffL
    }
    val count = (w * h / 5000f).toInt().coerceIn(40, 200)
    repeat(count) {
        val x = pseudo() * w
        val y = pseudo() * h
        val r = pseudo() * 1.5f + 0.5f
        val a = pseudo() * 0.06f + 0.02f
        drawCircle(color = Color.White.copy(alpha = a), radius = r, center = Offset(x, y))
    }
    repeat(count / 3) {
        val x = pseudo() * w
        val y = pseudo() * h
        val r = pseudo() * 1.0f + 0.3f
        val a = pseudo() * 0.03f + 0.01f
        drawCircle(color = Color(0xFF2C2C2C).copy(alpha = a), radius = r, center = Offset(x, y))
    }
}

// ============================================================
//  Activity
// ============================================================

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.VIBRATE)
            }
        }
        setContent { TodayEatApp() }
    }
}

// ============================================================
//  根 Composable
// ============================================================

@Composable
private fun TodayEatApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ---- 全局字体（设置页可即时切换） ----
    var appFont by remember { mutableStateOf(ThemeManager.getFont(context)) }

    val db = remember { AppDatabase.getInstance(context) }
    val dao = remember { db.historyDao() }
    val favoriteDao = remember { db.favoriteDao() }

    var plateStatus by remember { mutableStateOf(PlateStatus.IDLE) }
    var selectedFood by remember { mutableStateOf<FoodResult?>(null) }
    var isImageLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var showChangeLogSheet by remember { mutableStateOf(false) }
   var showHistorySheet by remember { mutableStateOf(false) }
   var showMenuSheet by remember { mutableStateOf(false) }
   var showStatsSheet by remember { mutableStateOf(false) }
   var showFavoritesSheet by remember { mutableStateOf(false) }
   var showSettings by remember { mutableStateOf(false) }
   var isCurrentFoodFavorited by remember { mutableStateOf(false) }
   var drawFromFavoritesOnly by remember { mutableStateOf(ThemeManager.getFavoritesOnly(context)) }


   // ---- 实时时钟：心跳 + 生命周期刷新 ----
    val now = LocalTime.now()
    var currentHour by remember { mutableIntStateOf(now.hour) }
    var currentMinute by remember { mutableIntStateOf(now.minute) }
    fun refreshTime() {
        val t = LocalTime.now()
        currentHour = t.hour
        currentMinute = t.minute
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    // 每分钟心跳刷新时间
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            refreshTime()
        }
    }
    // 从后台切回前台时强制刷新
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshTime()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val currentPeriod = MealPeriod.getCurrent()
    val historyWithCounts by dao.getHistoryWithCounts(200).collectAsState(initial = emptyList())
    val favoriteNames by favoriteDao.getAllNames().collectAsState(initial = emptyList())

    // ---- 动画状态 ----
    val shakeOffset = remember { Animatable(0f) }
    val shakeRotation = remember { Animatable(0f) }
    val buttonScale = remember { Animatable(1f) }
    val plateScale = remember { Animatable(1f) }
    val plateAlpha = remember { Animatable(1f) }
    val contentScale = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val foodNameAlpha = remember { Animatable(0f) }
    val foodNameOffset = remember { Animatable(30f) }

    // 背景缓慢偏移
    var bgOffset by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            val anim = Animatable(0f)
            anim.animateTo(1f, tween(20000, easing = LinearEasing))
            bgOffset = anim.value
        }
    }

    // IDLE 浮动
    val floatY = remember { Animatable(0f) }
    LaunchedEffect(plateStatus == PlateStatus.IDLE) {
        if (plateStatus == PlateStatus.IDLE) {
            while (plateStatus == PlateStatus.IDLE) {
                floatY.animateTo(-8f, tween(2000, easing = FastOutSlowInEasing))
                floatY.animateTo(0f, tween(2000, easing = FastOutSlowInEasing))
            }
        }
    }

    CompositionLocalProvider(LocalAppFontFamily provides appFont.fontFamily) {
    Box(modifier = Modifier.fillMaxSize()) {

        // ===== 灰白背景 =====
        Box(modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF5F5F7), Color(0xFFECECEE),
                    Color(0xFFF0F0F2), Color(0xFFE8E8EA)
                ),
                start = Offset(0f, 0f),
                end = Offset(1000f, 2000f)
            )
        ))

        // ===== 左上角时段状态感应胶囊 =====
        ContextCapsule(currentHour = currentHour,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 56.dp)
        )

        // ===== 主内容区 =====
        AnimatedVisibility(
            visible = selectedTab == 0,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400, easing = appleBezier)) + fadeIn(animationSpec = tween(300, easing = LinearEasing)),
            exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(350, easing = appleBezier)) + fadeOut(animationSpec = tween(250, easing = LinearEasing))
        ) {
        Column(modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
            Spacer(modifier = Modifier.height(100.dp))
            // ===== 标题区 =====
            Box(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x15000000), spotColor = Color(0x0A000000))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .border(0.5.dp, Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "今天吃啥",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = LocalAppFontFamily.current,
                    color = Color(0xFF1C1C1E),
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "当前时段  ${currentPeriod.title}  ${currentPeriod.timeRange}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF2C2C2C).copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ===== 收藏模式指示徽章 =====
            AnimatedVisibility(
                visible = drawFromFavoritesOnly,
                enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.8f, animationSpec = spring())
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF3B30).copy(alpha = 0.10f))
                        .border(0.5.dp, Color(0xFFFF3B30).copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            drawFromFavoritesOnly = false
                            ThemeManager.setFavoritesOnly(context, false)
                        }
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HeartIcon(filled = true, color = Color(0xFFFF3B30), modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("仅从收藏抽取", fontSize = 10.sp, fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF3B30).copy(alpha = 0.85f), letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Close, contentDescription = null,
                            modifier = Modifier.size(9.dp), tint = Color(0xFFFF3B30).copy(alpha = 0.5f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            // ===== 圆形盲盒抽奖盘 =====
            CircularGlassPlate(
                plateStatus = plateStatus,
                shakeOffset = shakeOffset.value,
                shakeRotation = shakeRotation.value,
                floatY = floatY.value,
                plateScale = plateScale.value,
                plateAlpha = plateAlpha.value,
                contentScale = contentScale.value,
                contentAlpha = contentAlpha.value,
                foodNameAlpha = foodNameAlpha.value,
                foodNameOffset = foodNameOffset.value,
                selectedFood = selectedFood,
                isImageLoading = isImageLoading,
                isFavoritesMode = drawFromFavoritesOnly,
                isCurrentFoodFavorited = isCurrentFoodFavorited,
                onToggleFavorite = {
                    selectedFood?.let { food ->
                        scope.launch {
                            if (isCurrentFoodFavorited) {
                                favoriteDao.delete(food.chineseName)
                            } else {
                                favoriteDao.insert(FavoriteEntity(foodName = food.chineseName))
                            }
                            isCurrentFoodFavorited = !isCurrentFoodFavorited
                            HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                        }
                    }
                },
                onClick = {
                    if (plateStatus != PlateStatus.IDLE) return@CircularGlassPlate
                    plateStatus = PlateStatus.SHAKING
                    HapticEngine.playShakeWaveform(context, HapticEngine.getFactor(context))
                    scope.launch {
                        // 递减阻尼摇晃 8 段
                        val amps = listOf(16f, 14f, 12f, 10f, 8f, 6f, 4f, 2f)
                        val rots = listOf(5f, 4.5f, 4f, 3.5f, 3f, 2.5f, 2f, 1f)
                        val durations = listOf(180L, 200L, 220L, 240L, 260L, 280L, 300L, 320L)
                        for (i in amps.indices) {
                            val dir = if (i % 2 == 0) 1f else -1f
                            launch { shakeOffset.animateTo(dir * amps[i], tween(durations[i].toInt(), easing = FastOutSlowInEasing)) }
                            launch { shakeRotation.animateTo(dir * rots[i], tween(durations[i].toInt(), easing = FastOutSlowInEasing)) }
                            delay(durations[i])
                        }
                        launch { shakeOffset.animateTo(0f, tween(200)) }
                        launch { shakeRotation.animateTo(0f, tween(200)) }
                        delay(160)

                        // 选菜 + 取图
                        val foodName = if (drawFromFavoritesOnly && favoriteNames.isNotEmpty()) {
                            WeightManager.selectWeightedRandom(context, historyWithCounts, favoriteNames).foodName
                        } else {
                            WeightManager.selectWeightedRandom(context, historyWithCounts).foodName
                        }
                        isImageLoading = true
                        selectedFood = FoodResult(chineseName = foodName, imageUrl = null, chosenCount = 0)
                        isCurrentFoodFavorited = foodName in favoriteNames
                        val url = try { searchFoodImage(foodName) } catch (_: Exception) { null }
                        isImageLoading = false
                        selectedFood = FoodResult(chineseName = foodName, imageUrl = url, chosenCount = 0)

                        // 开盖 Timeline（GSAP 风格）
                        plateStatus = PlateStatus.OPENED
                        HapticEngine.playSuccessEffect(context, HapticEngine.getFactor(context))
                        HapticEngine.stop(context)
                        // 盘子放大淡出
                        launch { plateScale.animateTo(1.2f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                        launch { plateAlpha.animateTo(0f, tween(400)) }
                        delay(100)
                        // 内容弹入
                        launch { contentScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                        launch { contentAlpha.animateTo(1f, tween(400)) }
                        delay(200)
                        // 菜名上滑
                        launch { foodNameOffset.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                        launch { foodNameAlpha.animateTo(1f, tween(300)) }
                    }
                },
                onSwipeDismiss = {
                    plateStatus = PlateStatus.IDLE; selectedFood = null; isImageLoading = false
                    scope.launch {
                        plateScale.snapTo(1f); plateAlpha.snapTo(1f)
                        contentScale.snapTo(0f); contentAlpha.snapTo(0f)
                        foodNameAlpha.snapTo(0f); foodNameOffset.snapTo(30f)
                    }
                }
            )

            Spacer(modifier = Modifier.height(64.dp))

            // ===== 操作按钮区 =====
            AnimatedVisibility(
                visible = plateStatus == PlateStatus.OPENED,
                enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)),
                exit = fadeOut(tween(200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    // 近3日选择次数 —— 始终显示，即使0次
                    selectedFood?.let { food ->
                        val cutoff3d = System.currentTimeMillis() - 3L * 24 * 60 * 60 * 1000
                        val count3d = historyWithCounts.count { it.foodName == food.chineseName && it.timestamp > cutoff3d }
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${count3d}", fontSize = 16.sp, fontWeight = FontWeight.Black,
                                    color = Color(0xFF1C1C1E), textAlign = TextAlign.Center)
                                Text("近3日", fontSize = 8.sp, color = Color(0xFF888888),
                                    letterSpacing = 1.sp)
                            }
                        }
                    }
                    // "就吃这个" —— 确认按钮，写入 Room 数据库
                    Box(modifier = Modifier.scale(buttonScale.value), contentAlignment = Alignment.Center) {
                        Text(
                            text = "就吃这个",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = 3.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF2C2C2C).copy(alpha = 0.9f), Color(0xFF3A3A3A).copy(alpha = 0.85f))
                                ))
                                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                                .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x10000000), spotColor = Color(0x08000000))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    selectedFood?.let { food ->
                                        scope.launch {
                                            dao.insert(HistoryEntity(foodName = food.chineseName, timestamp = System.currentTimeMillis()))
                                            // 更新 chosenCount
                                            val newCount = dao.getCountByName(food.chineseName)
                                            selectedFood = food.copy(chosenCount = newCount)
                                        }
                                    }
                                    scope.launch {
                                        buttonScale.animateTo(0.88f, tween(80))
                                        buttonScale.animateTo(1.06f, tween(120))
                                        buttonScale.animateTo(1f, tween(100))
                                    }
                                    plateStatus = PlateStatus.IDLE; selectedFood = null; isImageLoading = false
                                    scope.launch {
                                        plateScale.snapTo(1f); plateAlpha.snapTo(1f)
                                        contentScale.snapTo(0f); contentAlpha.snapTo(0f)
                                        foodNameAlpha.snapTo(0f); foodNameOffset.snapTo(30f)
                                    }
                                }.padding(horizontal = 28.dp, vertical = 12.dp))
                    }

                    // "再来一局"
                    Box(modifier = Modifier.scale(buttonScale.value), contentAlignment = Alignment.Center) {
                        Text(
                            text = "再来一局",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C2C2C),
                            letterSpacing = 3.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(24.dp))
                                .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x08000000), spotColor = Color(0x05000000))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    scope.launch {
                                        buttonScale.animateTo(0.88f, tween(80))
                                        buttonScale.animateTo(1.06f, tween(120))
                                        buttonScale.animateTo(1f, tween(100))
                                    }
                                    plateStatus = PlateStatus.IDLE; selectedFood = null; isImageLoading = false
                                    scope.launch {
                                        plateScale.snapTo(1f); plateAlpha.snapTo(1f)
                                        contentScale.snapTo(0f); contentAlpha.snapTo(0f)
                                        foodNameAlpha.snapTo(0f); foodNameOffset.snapTo(30f)
                                    }
                                }.padding(horizontal = 28.dp, vertical = 12.dp))
                    }
                    } // Row
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "向上或左右划走卡片可跳过",
                        fontSize = 10.sp,
                        color = Color(0xFF2C2C2C).copy(alpha = 0.2f),
                        letterSpacing = 1.sp
                    )
                } // Column

                } // AnimatedVisibility (操作按钮区)
            } // Column (outer)
        } // AnimatedVisibility

        // ===== 底部导航栏 =====
        FloatingGlassNavBar(
            selectedTab = selectedTab,
            onTabSelected = { index ->
                when (index) {
                    0 -> { selectedTab = 0 }
                    1 -> { showMenuSheet = true }
                    2 -> { showHistorySheet = true }
                    3 -> { showFavoritesSheet = true }
                    4 -> { showStatsSheet = true }
                    5 -> { showSettings = true }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        )

    }

        // ===== 历史记录 Bottom Sheet =====
        MacOsSheetHost(visible = showHistorySheet) {
            HistoryBottomSheet(
                dao = dao,
                onDismiss = { showHistorySheet = false }
            )
        }

        // ===== 菜单池 Bottom Sheet =====
        MacOsSheetHost(visible = showMenuSheet) {
            MenuPoolBottomSheet(
                context = context,
                historyWithCounts = historyWithCounts,
                currentPeriod = currentPeriod,
                onDismiss = { showMenuSheet = false; refreshTrigger++ }
            )
        }

        // ===== 统计仪表盘 Bottom Sheet =====
        MacOsSheetHost(visible = showStatsSheet) {
            StatsBottomSheet(
                dao = dao,
                onDismiss = { showStatsSheet = false }
            )
        }

        // ===== 收藏 Bottom Sheet =====
        MacOsSheetHost(visible = showFavoritesSheet) {
            FavoritesBottomSheet(
                context = context,
                favoriteDao = favoriteDao,
                favoriteNames = favoriteNames,
                isFavoritesMode = drawFromFavoritesOnly,
                onDismiss = { showFavoritesSheet = false },
                onToggleFavoritesMode = {
                    drawFromFavoritesOnly = !drawFromFavoritesOnly
                    ThemeManager.setFavoritesOnly(context, drawFromFavoritesOnly)
                }
            )
        }


        // ===== 设置全屏页 =====
        MacOsSheetHost(visible = showSettings) {
            SettingsBottomSheet(
                context = context,
                dao = dao,
                onDismiss = { showSettings = false },
                onShowChangelog = { showChangeLogSheet = true },
                onFontChange = { choice -> appFont = choice },

                fullPage = true
            )
        }
        MacOsSheetHost(visible = showChangeLogSheet) {
            ChangeLogSheet(
                onDismiss = { showChangeLogSheet = false }
            )
        }


    }
}

private data class CapsuleState(
    val title: String,
    val tip: String,

    val lightColor: Color
)

private fun getCapsuleState(hour: Int = LocalTime.now().hour): CapsuleState {
    val slot = MealPeriod.getCurrent()
    // Provide a helper that takes current hour for recomposition trigger
    return when (java.time.LocalTime.now().hour) {
        in 6..10 -> CapsuleState(
            slot.title.uppercase(),
            "宜选温和清淡，唤醒全天活力",
            Color(0xFF81C784)
        )
        in 11..13 -> CapsuleState(
            slot.title.uppercase(),
            "补充碳水蛋白，撑起午后能量",
            Color(0xFFFFB74D)
        )
        in 14..16 -> CapsuleState(
            slot.title.uppercase(),
            "午后小憩，来点轻食补充能量",
            Color(0xFFBA68C8)
        )
        in 17..20 -> CapsuleState(
            slot.title.uppercase(),
            "调理舒缓肠胃，享受克制犒劳",
            Color(0xFFFF8A65)
        )
        else -> CapsuleState(
            slot.title.uppercase(),
            "倾向暖胃流食，拒绝多余负担",
            Color(0xFF64B5F6)
        )
    }
}

@Composable
private fun ContextCapsule(currentHour: Int, modifier: Modifier = Modifier) {
    val state = getCapsuleState(currentHour)

    // 呼吸灯动画
    val infiniteTransition = rememberInfiniteTransition(label = "capsule_breath")
    val breathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    Row(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(50.dp), ambientColor = Color(0x12000000), spotColor = Color(0x08000000))
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .drawBehind { drawFrostNoise() }
            .border(BorderStroke(0.8.dp, Color.White.copy(alpha = 0.5f)), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 呼吸指示灯
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(state.lightColor.copy(alpha = breathAlpha))
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 时段标签
        Text(
            text = state.title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1C1C1E),
            letterSpacing = 1.sp,
            fontFamily = LocalAppFontFamily.current
        )

        Spacer(modifier = Modifier.width(6.dp))

        // 分隔竖线
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(10.dp)
                .background(Color(0xFF1C1C1E).copy(alpha = 0.1f))
        )

        Spacer(modifier = Modifier.width(6.dp))

        // 菜品提示
        Text(
            text = state.tip,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF555555),
            fontFamily = LocalAppFontFamily.current,
            maxLines = 1
        )
    }
}

// ============================================================
//  圆形玻璃盲盒
// ============================================================

@Composable
private fun CircularGlassPlate(
    plateStatus: PlateStatus, shakeOffset: Float, shakeRotation: Float, floatY: Float,
    plateScale: Float, plateAlpha: Float, contentScale: Float, contentAlpha: Float,
    foodNameAlpha: Float, foodNameOffset: Float,
    selectedFood: FoodResult?, isImageLoading: Boolean,
    isFavoritesMode: Boolean = false,
    isCurrentFoodFavorited: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onClick: () -> Unit, onSwipeDismiss: () -> Unit, modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    // 卡片抛掷偏移与旋转
    val cardOffsetX = remember { Animatable(0f) }
    val cardOffsetY = remember { Animatable(0f) }
    val cardRotation = remember { Animatable(0f) }
    val cardAlpha = remember { Animatable(1f) }
    var dragStartX by remember { mutableStateOf(0f) }
    var dragStartY by remember { mutableStateOf(0f) }

    val breathAlpha = remember { Animatable(0.15f) }
    LaunchedEffect(plateStatus == PlateStatus.IDLE) {
        if (plateStatus == PlateStatus.IDLE) {
            while (plateStatus == PlateStatus.IDLE) {
                breathAlpha.animateTo(0.35f, tween(1800, easing = FastOutSlowInEasing))
                breathAlpha.animateTo(0.15f, tween(1800, easing = FastOutSlowInEasing))
            }
        }
    }

    // OPENED 状态重置卡片位置
    LaunchedEffect(plateStatus) {
        if (plateStatus == PlateStatus.OPENED) {
            cardOffsetX.snapTo(0f)
            cardOffsetY.snapTo(0f)
            cardRotation.snapTo(0f)
            cardAlpha.snapTo(1f)
        }
    }

    Box(modifier = modifier.size(320.dp), contentAlignment = Alignment.Center) {

        if (plateStatus == PlateStatus.IDLE) {
            val glowColor = if (isFavoritesMode) Color(0xFFFF3B30).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.5f)
            Box(modifier = Modifier.size(300.dp).alpha(breathAlpha.value)
                .clip(CircleShape).border(1.dp, glowColor, CircleShape))
        }

        // ===== 玻璃罩（IDLE / SHAKING）=====
        Box(modifier = Modifier
            .offset { IntOffset(shakeOffset.roundToInt(), floatY.roundToInt()) }
            .rotate(shakeRotation)
            .size(260.dp)
            .scale(plateScale)
            .alpha(plateAlpha)
            .clip(CircleShape)
            .shadow(elevation = 24.dp, shape = CircleShape, ambientColor = Color(0x10000000), spotColor = Color(0x08000000))
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0x06000000), spotColor = Color(0x03000000))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.62f),
                        Color.White.copy(alpha = 0.42f),
                        Color.White.copy(alpha = 0.28f)
                    )
                ),
                shape = CircleShape
            )
            .drawBehind { drawFrostNoise() }
            .border(width = 1.5.dp, brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.12f))
            ), shape = CircleShape)
            .clickable(interactionSource = interactionSource, indication = null, enabled = plateStatus == PlateStatus.IDLE) { onClick() },
            contentAlignment = Alignment.Center) {

            when (plateStatus) {
                PlateStatus.IDLE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isFavoritesMode) {
                            HeartIcon(filled = true, color = Color(0xFFFF3B30).copy(alpha = 0.7f),
                                modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "FROM FAVORITES", fontSize = 15.sp, fontWeight = FontWeight.Black,
                                color = Color(0xFFFF3B30).copy(alpha = 0.8f), letterSpacing = 3.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "从收藏中抽取", fontSize = 11.sp, fontWeight = FontWeight.Normal,
                                color = Color(0xFF2C2C2C).copy(alpha = 0.35f), textAlign = TextAlign.Center)
                        } else {
                            Text(text = "TAP TO CHOOSE", fontSize = 17.sp, fontWeight = FontWeight.Black,
                                color = Color(0xFF2C2C2C), letterSpacing = 4.sp, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "点击开启今日美食盲盒", fontSize = 12.sp, fontWeight = FontWeight.Normal,
                                color = Color(0xFF2C2C2C).copy(alpha = 0.35f), textAlign = TextAlign.Center)
                        }
                    }
                }
                PlateStatus.SHAKING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 2.5.dp,
                            color = Color(0xFF2C2C2C).copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(text = "COOKING", fontSize = 16.sp, fontWeight = FontWeight.Black,
                            color = Color(0xFF2C2C2C).copy(alpha = 0.5f), letterSpacing = 4.sp, textAlign = TextAlign.Center)
                    }
                }
                PlateStatus.OPENED -> { }
            }
        }

        // ===== 开盖后内容卡片（可抛掷） =====
        if (contentAlpha > 0.01f && plateStatus == PlateStatus.OPENED) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .scale(contentScale)
                    .alpha(contentAlpha * cardAlpha.value)
                    .offset {
                        IntOffset(
                            (shakeOffset + cardOffsetX.value).roundToInt(),
                            (floatY + cardOffsetY.value).roundToInt()
                        )
                    }
                    .rotate(shakeRotation + cardRotation.value)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                dragStartX = cardOffsetX.value
                                dragStartY = cardOffsetY.value
                            },
                            onDragEnd = {
                                val totalX = cardOffsetX.value
                                val totalY = cardOffsetY.value
                                val distance = kotlin.math.sqrt(totalX * totalX + totalY * totalY)

                                if (distance > 200f) {
                                    // 抛出屏幕
                                    val direction = if (kotlin.math.abs(totalX) > kotlin.math.abs(totalY)) {
                                        if (totalX > 0) 1f else -1f
                                    } else 0f

                                    scope.launch {
                                        val flyX = direction * 1200f
                                        val flyY = if (direction == 0f) {
                                            if (totalY > 0) 1200f else -1200f
                                        } else {
                                            totalY * 3f
                                        }
                                        val flyRot = direction * 45f + totalX * 0.1f

                                        launch { cardOffsetX.animateTo(flyX, tween(400, easing = FastOutSlowInEasing)) }
                                        launch { cardOffsetY.animateTo(flyY, tween(400, easing = FastOutSlowInEasing)) }
                                        launch { cardRotation.animateTo(flyRot, tween(400, easing = FastOutSlowInEasing)) }
                                        launch {
                                            cardAlpha.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
                                        }
                                        delay(400)
                                        onSwipeDismiss()
                                    }
                                } else {
                                    // 弹回原位
                                    scope.launch {
                                        launch { cardOffsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                                        launch { cardOffsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                                        launch { cardRotation.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                scope.launch {
                                    cardOffsetX.snapTo(cardOffsetX.value + dragAmount.x)
                                    cardOffsetY.snapTo(cardOffsetY.value + dragAmount.y)
                                    // 旋转角度跟手指位移联动
                                    cardRotation.snapTo(cardOffsetX.value * 0.15f)
                                }
                                change.consume()
                            }
                        )
                    }
                    .clip(CircleShape)
                    .shadow(elevation = 24.dp, shape = CircleShape, ambientColor = Color(0x10000000), spotColor = Color(0x08000000))
                    .border(width = 1.5.dp, brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.05f))
                    ), shape = CircleShape)
            ) {
                PlateContent(food = selectedFood, isLoading = isImageLoading)
            }

            // 菜名 + 次数 —— 位于图片下方，液态玻璃框
            if (foodNameAlpha > 0.01f && selectedFood != null) {
                Column(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset {
                        IntOffset(
                            cardOffsetX.value.roundToInt(),
                            (foodNameOffset + cardOffsetY.value + 180f).roundToInt()
                        )
                    }
                    .alpha(foodNameAlpha * cardAlpha.value),
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    // 液态玻璃框
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x10000000), spotColor = Color(0x08000000))
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.55f),
                                        Color.White.copy(alpha = 0.35f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.7f),
                                        Color.White.copy(alpha = 0.15f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedFood.chineseName, fontSize = 22.sp, fontWeight = FontWeight.Black,
                                    color = Color(0xFF1C1C1E), textAlign = TextAlign.Center, letterSpacing = 3.sp,
                                    fontFamily = LocalAppFontFamily.current)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onToggleFavorite() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    HeartIcon(
                                        filled = isCurrentFoodFavorited,
                                        color = if (isCurrentFoodFavorited) Color(0xFFFF3B30) else Color(0xFF8E8E93),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "已选 ${selectedFood.chosenCount} 次",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C2C2C).copy(alpha = 0.4f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  Frequency Pill —— 次数统计徽章
// ============================================================

@Composable
private fun FrequencyPill(count: Int) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f))
            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "已选 ${count} 次",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 2.sp,
            fontFamily = LocalAppFontFamily.current
        )
    }
}

// ============================================================
//  餐盘内容
// ============================================================

@Composable
private fun PlateContent(food: FoodResult?, isLoading: Boolean) {
    if (food == null) return

    when {
        isLoading -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                .background(Color.White.copy(alpha = 0.1f), CircleShape)) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 2.5.dp,
                    color = Color(0xFF2C2C2C).copy(alpha = 0.3f))
            }
        }
        food.imageUrl != null -> {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(food.imageUrl).crossfade(600).build(),
                contentDescription = food.chineseName, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop,
                loading = {
                    Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)), Alignment.Center) {
                        CircularProgressIndicator(Modifier.size(32.dp), color = Color(0xFF2C2C2C).copy(alpha = 0.3f), strokeWidth = 2.5.dp)
                    }
                },
                error = { WarmFallbackCard(food.chineseName) }
            )
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Color(0xFF2C2C2C).copy(alpha = 0.3f))),
                shape = CircleShape))
        }
        else -> WarmFallbackCard(food.chineseName)
    }
}

// ============================================================
//  温暖纯色兜底卡片
// ============================================================

@Composable
private fun WarmFallbackCard(foodName: String) {
    val visual = FOOD_VISUALS[foodName] ?: FoodVisual(Color(0xFFE8C0C0), Color(0xFFD4A0A0))
    Box(modifier = Modifier.fillMaxSize().clip(CircleShape), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(colors = listOf(visual.gradientStart, visual.gradientEnd)), shape = CircleShape))
        Text(text = foodName, fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color.White,
            textAlign = TextAlign.Center, letterSpacing = 2.sp)
    }
}

// ============================================================
//  菜品百科详情页 —— 源点感知转场（Source-Aware Transition）
//  点击结果页菜名框时，以该框为物理源点放射状弹性放大至全屏；
//  退出时反向收束，并借 LocalPanelInteractive 熔断器在退场期间
//  彻底释放命中测试，底层导航栏点击流 100% 穿透。
// ============================================================
//  历史记录 Bottom Sheet —— 毛玻璃抽屉
// ============================================================

@Composable
private fun HistoryBottomSheet(dao: HistoryDao, onDismiss: () -> Unit) {
    val historyWithCounts by dao.getHistoryWithCounts(50).collectAsState(initial = emptyList())
    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val scope = rememberCoroutineScope()
    var dragAccum by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val slidePx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() } * 0.12f

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }

    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }

    BackHandler { dismiss() }

    Box(modifier = Modifier.fillMaxSize()
        .drawBehind {
            // 性能：将 sheetOffset 读取隔离到 draw 层，关闭动画每帧只触发重绘，
            // 不再向上触发 Box 及内部 LazyColumn 重组（消除关闭掉帧元凶）
            val scrimAlpha = 0.55f * (1f - sheetOffset.value.coerceIn(0f, 1f))
            drawRect(color = Color.Black.copy(alpha = scrimAlpha))
        }
        .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() }
    ) {
        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .graphicsLayer { val p = sheetOffset.value.coerceIn(0f, 1f); scaleX = 1f - p * 0.08f; scaleY = 1f - p * 0.08f; alpha = 1f - p }
            .offset { IntOffset(0, (sheetOffset.value * slidePx).roundToInt()) }
            .fillMaxWidth()
            .height(520.dp)
            .then(if (isInteractive) Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragAccum = 0f },
                    onDragEnd = {
                        if (dragAccum > 100f) {
                            dismiss()
                        } else {
                            scope.launch {
                                sheetOffset.animateTo(0f, tween(300, easing = appleBezier))
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                        val target = (dragAccum / slidePx).coerceIn(0f, 1f)
                        scope.launch { sheetOffset.snapTo(target) }
                        change.consume()
                    }
                )
            } else Modifier)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(width = 1.dp, brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.1f))
            ), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2C2C2C).copy(alpha = 0.15f)))
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("历史记录", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E), letterSpacing = 3.sp, fontFamily = LocalAppFontFamily.current)
                Spacer(modifier = Modifier.weight(1f))
                Text("${historyWithCounts.size} 条", fontSize = 11.sp,
                    color = Color(0xFF2C2C2C).copy(alpha = 0.3f))
            }

            if (historyWithCounts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有记录\n点击「就吃这个」后会出现在这里",
                        fontSize = 13.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.25f),
                        textAlign = TextAlign.Center, lineHeight = 22.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    items(historyWithCounts, key = { it.id }) { entry ->
                        HistoryRow(entry)
                    }
                }
            }
        }
    }
}

// ============================================================
//  历史记录行
// ============================================================

@Composable
private fun HistoryRow(entry: HistoryWithCount) {
    val visual = FOOD_VISUALS[entry.foodName] ?: FoodVisual(Color(0xFFE8C0C0), Color(0xFFD4A0A0))

    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White.copy(alpha = 0.4f))
        .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.04f), RoundedCornerShape(16.dp))
        .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(visual.gradientStart, visual.gradientEnd))),
            contentAlignment = Alignment.Center) {
            Text(entry.foodName.take(1), fontSize = 18.sp, fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(entry.foodName, fontSize = 15.sp, fontWeight = FontWeight.Medium,
            color = Color(0xFF1C1C1E), modifier = Modifier.weight(1f))

        Text(formatRelativeTime(entry.timestamp), fontSize = 11.sp,
            color = Color(0xFF888888), modifier = Modifier.padding(end = 8.dp))

        Box(modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF2C2C2C).copy(alpha = 0.06f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text("${entry.totalCount}次", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C).copy(alpha = 0.5f))
        }
    }
}

// ============================================================
//  菜单池 Bottom Sheet
// ============================================================

@Composable
private fun MenuPoolBottomSheet(
    context: Context,
    historyWithCounts: List<HistoryWithCount>,
    currentPeriod: MealPeriod,
    onDismiss: () -> Unit
) {
    val allFoods = remember(context) { WeightManager.getAllFoods(context) }
    val customFoods = remember(context) { WeightManager.getCustomFoods(context) }
    val weightInfos = remember(historyWithCounts, context) {
        WeightManager.getAllWeightInfos(context, historyWithCounts)
    }
    val totalCountMap = remember(historyWithCounts) { historyWithCounts.groupingBy { it.foodName }.eachCount() }

    // 排序：默认拼音，可切换权重
    var sortByWeight by remember { mutableStateOf(false) }
    val collator = remember { java.text.Collator.getInstance(java.util.Locale.CHINA) }
    val displayItems = remember(weightInfos, sortByWeight) {
        if (sortByWeight) {
            weightInfos.sortedByDescending { it.weight }
        } else {
            weightInfos.sortedWith(compareBy(collator) { it.foodName })
        }
    }

    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var newFoodName by remember { mutableStateOf("") }
    var dragAccum by remember { mutableStateOf(0f) }
    val density2 = LocalDensity.current
    val slidePx2 = with(density2) { LocalConfiguration.current.screenHeightDp.dp.toPx() } * 0.12f

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }

    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }

    BackHandler { dismiss() }

    Box(modifier = Modifier.fillMaxSize()
        .drawBehind {
            // 性能：将 sheetOffset 读取隔离到 draw 层，关闭动画每帧只触发重绘，
            // 不再向上触发 Box 及内部 LazyColumn 重组（消除关闭掉帧元凶）
            val scrimAlpha = 0.55f * (1f - sheetOffset.value.coerceIn(0f, 1f))
            drawRect(color = Color.Black.copy(alpha = scrimAlpha))
        }
        .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() }
    ) {
        Column(modifier = Modifier
            .align(Alignment.BottomCenter)
            .graphicsLayer { val p = sheetOffset.value.coerceIn(0f, 1f); scaleX = 1f - p * 0.08f; scaleY = 1f - p * 0.08f; alpha = 1f - p }
            .offset { IntOffset(0, (sheetOffset.value * slidePx2).roundToInt()) }
            .fillMaxWidth()
            .height(560.dp)
            .then(if (isInteractive) Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragAccum = 0f },
                    onDragEnd = {
                        if (dragAccum > 100f) {
                            dismiss()
                        } else {
                            scope.launch {
                                sheetOffset.animateTo(0f, tween(300, easing = appleBezier))
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                        val target = (dragAccum / slidePx2).coerceIn(0f, 1f)
                        scope.launch { sheetOffset.snapTo(target) }
                        change.consume()
                    }
                )
            } else Modifier)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(width = 1.dp, brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.1f))
            ), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2C2C2C).copy(alpha = 0.15f)))
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("菜单池", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1C1E), letterSpacing = 3.sp, fontFamily = LocalAppFontFamily.current)
                    Text("当前 ${currentPeriod.title} · 权重自动调节",
                        fontSize = 10.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.3f))
                }
                Spacer(modifier = Modifier.weight(1f))
                // 排序切换按钮
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        sortByWeight = !sortByWeight
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (sortByWeight) "推荐度" else "拼音",
                        fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2C2C).copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("${allFoods.size} 道", fontSize = 11.sp,
                    color = Color(0xFF2C2C2C).copy(alpha = 0.3f))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
            ) {
                item {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                        .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showAddDialog = true }
                        .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("+ 添加自定义菜品", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = Color(0xFF2C2C2C).copy(alpha = 0.5f), letterSpacing = 2.sp)
                    }
                }

                items(displayItems, key = { it.foodName }) { info ->
                    val food = info.foodName
                    val isCustom = food in customFoods
                    val totalChosen = totalCountMap[food] ?: 0
                    val visual = FOOD_VISUALS[food] ?: FoodVisual(Color(0xFFE8C0C0), Color(0xFFD4A0A0))
                    val timeTags = FOOD_TIME_TAGS[food]
                    val isCurrentTime = timeTags != null && currentPeriod in timeTags

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.85f))
                        .border(
                            0.5.dp,
                            if (isCurrentTime) Color(0xFF2C2C2C).copy(alpha = 0.12f) else Color(0xFF2C2C2C).copy(alpha = 0.04f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FoodThumbnail(foodName = food, modifier = Modifier.size(40.dp))

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(food, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                color = Color(0xFF1C1C1E))
                            Row {
                                if (isCurrentTime) {
                                    Text("此时段", fontSize = 9.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.4f),
                                        modifier = Modifier.padding(end = 6.dp))
                                }
                                if (totalChosen > 0) {
                                    Text("已选${totalChosen}次", fontSize = 9.sp, color = Color(0xFF888888))
                                }
                                if (isCustom) {
                                    Text(if (totalChosen > 0) " · 自定义" else "自定义",
                                        fontSize = 9.sp, color = Color(0xFF888888))
                                }
                            }
                        }

                        // 权重指示条
                        val weightBarWidth = (info.weight / 2.0f).coerceIn(0.05f, 1f)
                        Box(modifier = Modifier.width(50.dp).height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF2C2C2C).copy(alpha = 0.06f))
                        ) {
                            Box(modifier = Modifier
                                .fillMaxWidth(weightBarWidth)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isCurrentTime) Color(0xFF2C2C2C).copy(alpha = 0.6f)
                                    else Color(0xFF2C2C2C).copy(alpha = 0.25f)
                                )
                            )
                        }

                        if (isCustom) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                                .background(Color(0xFFCC4444).copy(alpha = 0.08f))
                                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    WeightManager.removeCustomFood(context, food)
                                },
                                contentAlignment = Alignment.Center) {
                                Text("x", fontSize = 10.sp, color = Color(0xFFCC4444).copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            Box(modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    showAddDialog = false; newFoodName = ""
                },
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(24.dp)
                    .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
                ) {
                    Text("添加菜品", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
                        modifier = Modifier.padding(bottom = 16.dp))
                    OutlinedTextField(
                        value = newFoodName,
                        onValueChange = { newFoodName = it },
                        placeholder = { Text("输入菜名", fontSize = 14.sp, color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Text("取消", fontSize = 14.sp, color = Color.Gray,
                            modifier = Modifier.panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                showAddDialog = false; newFoodName = ""
                            }.padding(8.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("添加", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
                            modifier = Modifier.panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                if (newFoodName.trim().isNotEmpty()) {
                                    WeightManager.addCustomFood(context, newFoodName.trim())
                                    newFoodName = ""
                                    showAddDialog = false
                                }
                            }.padding(8.dp))
                    }
                }
            }
        }
    }
}

// ============================================================
//  菜品缩略图
// ============================================================

@Composable
private fun FoodThumbnail(foodName: String, modifier: Modifier = Modifier) {
    var imageUrl by remember(foodName) { mutableStateOf(FoodImageCache.get(foodName)) }
    var loaded by remember(foodName) { mutableStateOf(FoodImageCache.has(foodName)) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(foodName) {
        if (!loaded) {
            scope.launch {
                try {
                    val url = searchFoodImage(foodName)
                    FoodImageCache.put(foodName, url)
                    imageUrl = url
                } catch (_: Exception) { }
                loaded = true
            }
        }
    }

    val visual = FOOD_VISUALS[foodName] ?: FoodVisual(Color(0xFFE8C0C0), Color(0xFFD4A0A0))

    Box(modifier = modifier.clip(RoundedCornerShape(10.dp))) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(200)
                    .memoryCacheKey(foodName)
                    .build(),
                contentDescription = foodName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()
                .background(Brush.linearGradient(listOf(visual.gradientStart, visual.gradientEnd))),
                contentAlignment = Alignment.Center) {
                Text(foodName.take(1), fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

// ============================================================
//  时间格式化
// ============================================================

private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "刚刚"
        diff < 3_600_000 -> "${diff / 60_000}分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000}小时前"
        diff < 2 * 86_400_000L -> "昨天"
        diff < 7 * 86_400_000L -> "${diff / 86_400_000}天前"
        else -> {
            val sdf = SimpleDateFormat("MM-dd", Locale.CHINA)
            sdf.format(Date(timestamp))
        }
    }
}

// ============================================================
//  悬浮胶囊导航栏 —— 自绘线条图标 + 选中指示器 + 弹性反馈
// ============================================================

@Composable
private fun FloatingGlassNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val tabLabels = listOf("抽奖", "菜单池", "历史", "收藏", "统计", "设置")
    val tabScales = List(6) { remember { Animatable(1f) } }
    val iconScales = List(6) { remember { Animatable(1f) } }

    // 水平滑动手势：在导航栏上左右滑动切换 Tab
    var swipeDragAccum by remember { mutableStateOf(0f) }
    val swipeThresholdPx = with(LocalDensity.current) { 50.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .height(62.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { swipeDragAccum = 0f },
                    onDragEnd = {
                        when {
                            swipeDragAccum > swipeThresholdPx -> {
                                // 右滑 → 下一个 Tab
                                val next = (selectedTab + 1).coerceAtMost(5)
                                HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                                onTabSelected(next)
                            }
                            swipeDragAccum < -swipeThresholdPx -> {
                                // 左滑 → 上一个 Tab
                                val prev = (selectedTab - 1).coerceAtLeast(0)
                                HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                                onTabSelected(prev)
                            }
                        }
                        swipeDragAccum = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        swipeDragAccum += dragAmount
                    }
                )
            }
    ) {
        // ===== iOS 18 液态玻璃多层渲染 =====
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(31.dp))
                // 层 1：外层弥散阴影（模拟深度）
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(31.dp),
                    ambientColor = Color(0x0C000000), spotColor = Color(0x06000000))
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(31.dp),
                    ambientColor = Color(0x08000000), spotColor = Color(0x04000000))
                // 层 2：基底磨砂玻璃（低透明度让背景色透出）
                .background(
                    brush = Brush.verticalGradient(colors = listOf(
                        Color.White.copy(alpha = 0.52f),
                        Color.White.copy(alpha = 0.38f),
                        Color.White.copy(alpha = 0.30f)
                    )),
                    shape = RoundedCornerShape(31.dp)
                )
                // 层 3：霜噪纹理（微颗粒感）
                .drawBehind { drawFrostNoise() }
                // 层 4：顶部高光条（模拟镜面反射 Specular Highlight）
                .drawBehind {
                    val highlightH = size.height * 0.38f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.40f),
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = highlightH
                        ),
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(size.width, highlightH)
                    )
                }
                // 层 5：底部内阴影（增加深度感）
                .drawBehind {
                    val shadowH = size.height * 0.25f
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.03f),
                                Color.Black.copy(alpha = 0.06f)
                            ),
                            startY = size.height - shadowH,
                            endY = size.height
                        ),
                        topLeft = Offset(0f, size.height - shadowH),
                        size = androidx.compose.ui.geometry.Size(size.width, shadowH)
                    )
                }
                // 层 6：折射边框（顶部亮、底部暗，模拟光线折射）
                .border(width = 1.dp, brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.82f),
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ), shape = RoundedCornerShape(31.dp))
        )

        // ===== Tab 内容 =====
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
        tabLabels.forEachIndexed { index, label ->
            val isSelected = selectedTab == index

            val indicatorAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                label = "indicator_$index"
            )
            val labelAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.35f,
                animationSpec = tween(350, easing = FastOutSlowInEasing),
                label = "label_$index"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 6.dp)
                    .scale(tabScales[index].value)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                        scope.launch {
                            tabScales[index].animateTo(0.82f, tween(70))
                            tabScales[index].animateTo(1.12f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
                            tabScales[index].animateTo(1f, tween(100))
                        }
                        scope.launch {
                            iconScales[index].snapTo(0.7f)
                            iconScales[index].animateTo(1.25f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium))
                            iconScales[index].animateTo(1f, tween(150))
                        }
                        onTabSelected(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.55f * indicatorAlpha), CircleShape))

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Canvas(modifier = Modifier.size(22.dp).scale(iconScales[index].value)) {
                        val iconColor = if (isSelected) Color(0xFF2C2C2C) else Color(0xFF2C2C2C).copy(alpha = 0.3f)
                        drawNavIcon(index, iconColor)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = label, fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = Color(0xFF2C2C2C).copy(alpha = labelAlpha),
                        letterSpacing = 1.sp, fontFamily = LocalAppFontFamily.current)
                }
            }
        }
    }
    }
}

// ============================================================
//  自绘线条导航图标
// ============================================================

private fun DrawScope.drawNavIcon(index: Int, color: Color) {
    val w = size.width
    val h = size.height
    val stroke = 2.2f
    val cap = StrokeCap.Round

    when (index) {
        0 -> {
            drawLine(color, Offset(w * 0.22f, h * 0.62f), Offset(w * 0.68f, h * 0.30f), stroke, cap)
            drawLine(color, Offset(w * 0.55f, h * 0.30f), Offset(w * 0.68f, h * 0.30f), stroke, cap)
            drawLine(color, Offset(w * 0.68f, h * 0.43f), Offset(w * 0.68f, h * 0.30f), stroke, cap)
            drawLine(color, Offset(w * 0.22f, h * 0.38f), Offset(w * 0.68f, h * 0.70f), stroke, cap)
            drawLine(color, Offset(w * 0.55f, h * 0.70f), Offset(w * 0.68f, h * 0.70f), stroke, cap)
            drawLine(color, Offset(w * 0.68f, h * 0.57f), Offset(w * 0.68f, h * 0.70f), stroke, cap)
        }
        1 -> {
            drawLine(color, Offset(w * 0.28f, h * 0.32f), Offset(w * 0.78f, h * 0.32f), stroke, cap)
            drawLine(color, Offset(w * 0.28f, h * 0.50f), Offset(w * 0.78f, h * 0.50f), stroke, cap)
            drawLine(color, Offset(w * 0.28f, h * 0.68f), Offset(w * 0.78f, h * 0.68f), stroke, cap)
            drawCircle(color, radius = stroke * 0.65f, center = Offset(w * 0.20f, h * 0.32f))
            drawCircle(color, radius = stroke * 0.65f, center = Offset(w * 0.20f, h * 0.50f))
            drawCircle(color, radius = stroke * 0.65f, center = Offset(w * 0.20f, h * 0.68f))
        }
        2 -> {
            drawCircle(color = color, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = stroke))
            drawLine(color, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.28f), stroke, cap)
            drawLine(color, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.68f, h * 0.5f), stroke, cap)
        }
        3 -> {
            // 收藏 —— 爱心
            val cx = w * 0.50f
            val cy = h * 0.52f
            val r = w * 0.16f
            drawCircle(color, radius = r, center = Offset(cx - r * 0.72f, cy - r * 0.25f), style = Stroke(width = stroke))
            drawCircle(color, radius = r, center = Offset(cx + r * 0.72f, cy - r * 0.25f), style = Stroke(width = stroke))
            drawLine(color, Offset(cx - r * 1.5f, cy), Offset(cx, cy + r * 1.6f), stroke, cap)
            drawLine(color, Offset(cx + r * 1.5f, cy), Offset(cx, cy + r * 1.6f), stroke, cap)
        }
        4 -> {
            // 统计 —— 柱状图
            drawLine(color, Offset(w * 0.22f, h * 0.74f), Offset(w * 0.78f, h * 0.74f), stroke, cap)
            drawLine(color, Offset(w * 0.30f, h * 0.70f), Offset(w * 0.30f, h * 0.40f), stroke, cap)
            drawLine(color, Offset(w * 0.50f, h * 0.70f), Offset(w * 0.50f, h * 0.26f), stroke, cap)
            drawLine(color, Offset(w * 0.70f, h * 0.70f), Offset(w * 0.70f, h * 0.50f), stroke, cap)
        }
        5 -> {
            // 设置 —— 齿轮
            val cx = w * 0.50f
            val cy = h * 0.50f
            val rOuter = w * 0.28f
            val rInner = w * 0.16f
            val toothLen = w * 0.08f
            drawCircle(color, radius = rOuter, center = Offset(cx, cy), style = Stroke(width = stroke))
            drawCircle(color, radius = rInner, center = Offset(cx, cy), style = Stroke(width = stroke * 0.9f))
            for (a in 0..7) {
                val angle = Math.toRadians(a * 45.0)
                val angleCos = Math.cos(angle).toFloat()
                val angleSin = Math.sin(angle).toFloat()
                drawLine(color,
                    Offset(cx + angleCos * rOuter, cy + angleSin * rOuter),
                    Offset(cx + angleCos * (rOuter + toothLen), cy + angleSin * (rOuter + toothLen)),
                    stroke, cap)
            }
        }
        }
}

// ============================================================
//  触感反馈物理引擎 (HapticEngine)
// ============================================================

private object HapticEngine {
    private const val PREFS_NAME = "todayeat_theme"
    private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
    private const val KEY_HAPTIC_FACTOR = "haptic_factor"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HAPTIC_ENABLED, true)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_HAPTIC_ENABLED, enabled).apply()
    }

    /** 获取振幅系数 0.1f ~ 1.0f，关闭时返回 0f */
    fun getFactor(context: Context): Float {
        if (!isEnabled(context)) return 0f
        return prefs(context).getFloat(KEY_HAPTIC_FACTOR, 0.6f).coerceIn(0.1f, 1.0f)
    }

    fun setFactor(context: Context, factor: Float) {
        prefs(context).edit().putFloat(KEY_HAPTIC_FACTOR, factor.coerceIn(0.1f, 1.0f)).apply()
    }

    private fun getVibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /** 细腻微震 (嗒嗒嗒)：根据强弱系数动态映射振幅 */
    fun playTickEffect(context: Context, factor: Float) {
        if (factor <= 0f) return
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return
        val targetAmplitude = (180 * factor).roundToInt().coerceIn(10, 255)
        val timings = longArrayOf(0, 8, 40)
        val amplitudes = intArrayOf(0, targetAmplitude, 0)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
    }

    /** 开盖清脆重震 (咚)：根据强弱系数动态映射振幅 */
    fun playSuccessEffect(context: Context, factor: Float) {
        if (factor <= 0f) return
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return
        val targetAmplitude = (255 * factor).roundToInt().coerceIn(30, 255)
        vibrator.vibrate(VibrationEffect.createOneShot(35, targetAmplitude))
    }

    /** 摇晃循环波形：根据强弱系数动态映射振幅 */
    fun playShakeWaveform(context: Context, factor: Float) {
        if (factor <= 0f) return
        val vibrator = getVibrator(context)
        if (!vibrator.hasVibrator()) return
        val amp = (180 * factor).roundToInt().coerceIn(10, 255)
        val timings = longArrayOf(0, 30, 40, 30, 40, 30, 40, 30, 40, 30, 40, 30)
        val amplitudes = intArrayOf(0, amp, 0, amp, 0, amp, 0, amp, 0, amp, 0, amp)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
    }

    /** 停止所有震动 */
    fun stop(context: Context) {
        getVibrator(context).cancel()
    }
}

// ============================================================
//  百度图片搜索
// ============================================================

private suspend fun searchFoodImage(chineseName: String): String? = withContext(Dispatchers.IO) {
    var connection: HttpURLConnection? = null
    try {
        val enrichedQuery = "$chineseName 美食 高清 特写 摄影"
        val encodedQuery = URLEncoder.encode(enrichedQuery, "UTF-8")
        val urlString = "https://image.baidu.com/search/acjson?tn=resultjson_com&word=$encodedQuery&pn=0&rn=5&ipn=rj&fp=result"
        connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            setRequestProperty("Accept", "application/json, text/plain, */*")
            setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9")
            setRequestProperty("Referer", "https://image.baidu.com/")
            connectTimeout = 8_000; readTimeout = 8_000
        }
        if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
        val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        val dataArray = json.optJSONArray("data") ?: return@withContext null
        if (dataArray.length() == 0) return@withContext null
        for (i in 0 until dataArray.length()) {
            val item = dataArray.optJSONObject(i) ?: continue
            val hoverUrl = item.optString("hoverURL", "")
            if (hoverUrl.isNotEmpty() && hoverUrl.startsWith("http")) return@withContext hoverUrl
            val objUrl = item.optString("objURL", "")
            if (objUrl.isNotEmpty() && objUrl.startsWith("http")) return@withContext objUrl
            val thumbUrl = item.optString("thumbURL", "")
            if (thumbUrl.isNotEmpty() && thumbUrl.startsWith("http")) return@withContext thumbUrl
            val middleUrl = item.optString("middleURL", "")
            if (middleUrl.isNotEmpty() && middleUrl.startsWith("http")) return@withContext middleUrl
        }
        return@withContext null
    } catch (_: Exception) { null } finally { connection?.disconnect() }
}

// ============================================================
//  设置底部面板 —— macOS 风格：左侧边栏 + 右侧内容区
// ============================================================

@Composable
private fun SettingsBottomSheet(
    context: Context,
    dao: HistoryDao,
    onDismiss: () -> Unit,
    onShowChangelog: () -> Unit,
    onFontChange: (ThemeManager.FontChoice) -> Unit = {},
    fullPage: Boolean = false
) {
   val tc = remember { ThemeManager.applyColors(context) }
   var selectedSection by remember { mutableIntStateOf(0) }
   val scope = rememberCoroutineScope()
   var showResetDialog by remember { mutableStateOf(false) }
   val sectionLabels = listOf("外观", "交互", "数据", "关于")
    val sectionIcons = listOf(0, 3, 1, 2) // 0=palette, 1=database, 2=info, 3=vibration

    // ---- 进出场动画（sheetOffset: 0=展开, 1=收起） ----
    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    // 入场位移幅度：屏幕高度的 16%，既有“上滑”的质感又不会太慢
    val slidePx = screenHeightPx * 0.12f
    var dragAccum by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }
    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }
    // OPPO Find / ColorOS 全局手势：侧滑返回即关闭设置页，而不是退出 App
    BackHandler { dismiss() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // scrim 随展开度渐显，收起时先于内容淡出，层次更细腻
                val s = (1f - sheetOffset.value.coerceIn(0f, 1f))
                drawRect(color = Color.Black.copy(alpha = 0.55f * s))
            }
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() },
        contentAlignment = if (fullPage) Alignment.TopStart else Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (fullPage) 1f else 0.94f)
                .fillMaxHeight(if (fullPage) 1f else 0.72f)
                .graphicsLayer {
                    val p = sheetOffset.value.coerceIn(0f, 1f)
                    translationY = p * slidePx
                    scaleX = 1f - p * 0.04f
                    scaleY = 1f - p * 0.04f
                    alpha = 1f - p
                }
                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                .shadow(24.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    ambientColor = Color(0x15000000), spotColor = Color(0x0A000000))
                .background(tc.surface)
                .drawBehind { drawFrostNoise() }
                .border(1.dp, tc.borderLight, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars)) {
                // 顶部抓手 + 下滑关闭区（仅顶部 64dp 响应，避开 OPPO 底部上滑手势区）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .then(if (isInteractive) Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { dragAccum = 0f },
                                onDragEnd = {
                                    if (dragAccum > 90f) dismiss()
                                    else scope.launch { sheetOffset.animateTo(0f, tween(300, easing = appleBezier)) }
                                },
                                onDrag = { change, dragAmount ->
                                    dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                                    val target = (dragAccum / slidePx).coerceIn(0f, 1f)
                                    scope.launch { sheetOffset.snapTo(target) }
                                    change.consume()
                                }
                            )
                        } else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(tc.textPrimary.copy(alpha = 0.18f)))
                }
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "设置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = tc.textPrimary,
                        letterSpacing = 2.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(tc.textPrimary.copy(alpha = 0.06f))
                            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(14.dp)) {
                            val w = size.width; val h = size.height
                            val c = tc.textSecondary
                            drawLine(c, Offset(0f, 0f), Offset(w, h), 2f, StrokeCap.Round)
                            drawLine(c, Offset(w, 0f), Offset(0f, h), 2f, StrokeCap.Round)
                        }
                    }
                }

                // 分隔线
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(tc.textPrimary.copy(alpha = 0.06f)))

                // 主体: 侧边栏 + 内容
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    // ===== 左侧边栏 =====
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(115.dp)
                            .background(tc.textPrimary.copy(alpha = 0.025f))
                            .padding(vertical = 8.dp)
                    ) {
                        sectionLabels.forEachIndexed { index, label ->
                            val isSelected = selectedSection == index
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) tc.textPrimary.copy(alpha = 0.06f)
                                        else Color.Transparent
                                    )
                                    .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                                        selectedSection = index
                                    }
                                    .padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Canvas(modifier = Modifier.size(16.dp)) {
                                    val w = size.width; val h = size.height
                                    val c = if (isSelected) tc.textPrimary else tc.textTertiary
                                    val stroke = 1.8f
                                    when (sectionIcons[index]) {
                                        0 -> {
                                            drawCircle(c, w * 0.5f, Offset(w * 0.35f, h * 0.35f), style = Stroke(stroke))
                                            drawCircle(c, w * 0.38f, Offset(w * 0.58f, h * 0.4f), style = Stroke(stroke))
                                            drawCircle(c, w * 0.42f, Offset(w * 0.46f, h * 0.6f), style = Stroke(stroke))
                                        }
                                        1 -> {
                                            val r = w * 0.4f
                                            drawCircle(c, r, Offset(w * 0.5f, h * 0.45f), style = Stroke(stroke))
                                            drawLine(c, Offset(w * 0.5f, h * 0.5f - r), Offset(w * 0.5f, h * 0.15f), stroke, StrokeCap.Round)
                                            drawLine(c, Offset(w * 0.28f, h * 0.58f), Offset(w * 0.72f, h * 0.58f), stroke, StrokeCap.Round)
                                        }
                                        2 -> {
                                            drawCircle(c, w * 0.42f, Offset(w * 0.5f, h * 0.5f), style = Stroke(stroke))
                                            drawLine(c, Offset(w * 0.5f, h * 0.5f), Offset(w * 0.5f, h * 0.22f), stroke, StrokeCap.Round)
                                            drawLine(c, Offset(w * 0.38f, h * 0.66f), Offset(w * 0.62f, h * 0.66f), stroke, StrokeCap.Round)
                                        }
                                        3 -> {
                                            // 震动波纹图标：中心竖线 + 两侧弧线
                                            drawLine(c, Offset(w * 0.5f, h * 0.25f), Offset(w * 0.5f, h * 0.75f), stroke, StrokeCap.Round)
                                            drawArc(c, -60f, 120f, false,
                                                topLeft = Offset(w * 0.58f, h * 0.30f),
                                                size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.40f),
                                                style = Stroke(stroke, cap = StrokeCap.Round))
                                            drawArc(c, 120f, 120f, false,
                                                topLeft = Offset(w * 0.12f, h * 0.30f),
                                                size = androidx.compose.ui.geometry.Size(w * 0.30f, h * 0.40f),
                                                style = Stroke(stroke, cap = StrokeCap.Round))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) tc.textPrimary else tc.textSecondary
                                )
                            }
                        }
                    }

                    // 右侧分隔
                    Box(modifier = Modifier.fillMaxHeight().width(1.dp).background(tc.textPrimary.copy(alpha = 0.06f)))

                    // ===== 右侧内容区 =====
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        when (selectedSection) {
                            0 -> {
                                item { SectionHeader("主题模式") }
                                item {
                                    val current = remember { ThemeManager.getTheme(context) }
                                    var selected by remember { mutableStateOf(current) }
                                    SettingsSegmentedControl(
                                        options = ThemeManager.ThemeMode.entries.map { it.label },
                                        selectedIndex = selected.ordinal,
                                        onSelect = { idx ->
                                            val mode = ThemeManager.ThemeMode.entries[idx]
                                            selected = mode
                                            ThemeManager.setTheme(context, mode)
                                        }
                                    )
                                }
                                item { Spacer(modifier = Modifier.height(20.dp)) }
                                item { SectionHeader("字体") }
                                item {
                                    val currentFont = remember { ThemeManager.getFont(context) }
                                    var selectedFont by remember { mutableStateOf(currentFont) }
                                    SettingsSegmentedControl(
                                        options = ThemeManager.FontChoice.entries.map { it.label },
                                        selectedIndex = selectedFont.ordinal,
                                        onSelect = { idx ->
                                            val font = ThemeManager.FontChoice.entries[idx]
                                            selectedFont = font
                                            ThemeManager.setFont(context, font)
                                            onFontChange(font)
                                        }
                                    )
                                }
                            }
                            1 -> {
                                item { SectionHeader("触感反馈") }
                                item {
                                    var hapticEnabled by remember { mutableStateOf(HapticEngine.isEnabled(context)) }
                                    var hapticFactor by remember { mutableStateOf(HapticEngine.getFactor(context).coerceAtLeast(0.1f)) }

                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        // 开关行
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(tc.textPrimary.copy(alpha = 0.04f))
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    hapticEnabled = !hapticEnabled
                                                    HapticEngine.setEnabled(context, hapticEnabled)
                                                    if (hapticEnabled) {
                                                        HapticEngine.playTickEffect(context, hapticFactor)
                                                    }
                                                }
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("启用震动", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                                color = tc.textPrimary, modifier = Modifier.weight(1f))
                                            // iOS 风格开关
                                            val trackW = 44.dp
                                            val trackH = 26.dp
                                            val thumbSz = 22.dp
                                            val thumbOff by animateFloatAsState(
                                                targetValue = if (hapticEnabled) with(LocalDensity.current) { (trackW - thumbSz).toPx() } else 2f,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
                                                label = "hapticThumb"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .width(trackW).height(trackH)
                                                    .clip(RoundedCornerShape(trackH / 2))
                                                    .background(if (hapticEnabled) Color(0xFF34C759) else Color(0xFFE0E0E0))
                                            ) {
                                                val ty = with(LocalDensity.current) { 2.dp.toPx().roundToInt() }
                                                Box(
                                                    modifier = Modifier
                                                        .size(thumbSz)
                                                        .offset { IntOffset(thumbOff.roundToInt(), ty) }
                                                        .clip(CircleShape)
                                                        .shadow(2.dp, CircleShape)
                                                        .background(Color.White)
                                                )
                                            }
                                        }

                                        if (hapticEnabled) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("震动强度", fontSize = 11.sp, color = tc.textTertiary,
                                                modifier = Modifier.padding(start = 4.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            // 无级滑块
                                            Slider(
                                                value = hapticFactor,
                                                onValueChange = { newVal ->
                                                    hapticFactor = newVal
                                                    HapticEngine.setFactor(context, newVal)
                                                },
                                                onValueChangeFinished = {
                                                    HapticEngine.playTickEffect(context, hapticFactor)
                                                },
                                                valueRange = 0.1f..1.0f,
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Color(0xFF2C2C2C),
                                                    activeTrackColor = Color(0xFF2C2C2C),
                                                    inactiveTrackColor = Color(0xFF2C2C2C).copy(alpha = 0.12f)
                                                )
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("弱", fontSize = 10.sp, color = tc.textTertiary)
                                                Text("${(hapticFactor * 100).roundToInt()}%", fontSize = 10.sp,
                                                    color = tc.textSecondary, fontWeight = FontWeight.Medium)
                                                Text("强", fontSize = 10.sp, color = tc.textTertiary)
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))
                                            // 测试按钮
                                            SettingsButton(
                                                label = "测试触感",
                                                iconIndex = 1,
                                                color = tc.textPrimary,
                                                onClick = {
                                                    HapticEngine.playTickEffect(context, hapticFactor)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            2 -> {
                                item { SectionHeader("操作") }
                                item {
                                    SettingsButton(
                                        label = "重置美食权重",
                                        iconIndex = 0,
                                        color = Color(0xFFCC4444),
                                        onClick = {
                                            // 删除所有历史记录以重置权重
                                            scope.launch {
                                                try {
                                                    val db = AppDatabase.getInstance(context)
                                                    db.clearAllTables()
                                                    HapticEngine.playSuccessEffect(context, HapticEngine.getFactor(context))
                                                } catch (_: Exception) {}
                                            }
                                        }
                                    )
                                   Spacer(modifier = Modifier.height(6.dp))
                                   Text(
                                       "清除所有抽中记录，权重将恢复默认状态",
                                       fontSize = 10.sp,
                                       color = tc.textTertiary,
                                       modifier = Modifier.padding(start = 4.dp)
                                   )
                                   Spacer(modifier = Modifier.height(16.dp))
                                   SettingsButton(
                                       label = "重置历史记录",
                                       iconIndex = 0,
                                       color = Color(0xFFCC4444),
                                       onClick = { showResetDialog = true }
                                   )
                                   Spacer(modifier = Modifier.height(6.dp))
                                   Text(
                                       "清空全部历史记录，不可恢复",
                                       fontSize = 10.sp,
                                       color = tc.textTertiary,
                                       modifier = Modifier.padding(start = 4.dp)
                                   )
                               }
                           }
                            3 -> {
                                item { SectionHeader("应用信息") }
                                item {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                         Row { SettingsInfoRow("版本", "v4.14.0 (build 44)", tc) }
                                        Row { SettingsInfoRow("应用包名", "com.example.todayeat", tc) }
                                        Row { SettingsInfoRow("最低系统", "Android 10+", tc) }
                                    }
                                }
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                                item { SectionHeader("更新日志") }
                                item {
                                    SettingsButton(
                                        label = "查看版本更新日志",
                                        iconIndex = 1,
                                        color = tc.textPrimary,
                                        onClick = {
                                            // Navigate to changelog
                                            onShowChangelog()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---- 重置历史记录确认弹窗 ----
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("重置历史记录") },
                text = { Text("确定要清空全部历史记录吗？此操作不可恢复。") },
                confirmButton = {
                    TextButton(
                        enabled = LocalPanelInteractive.current,
                        onClick = {
                            scope.launch {
                                dao.deleteAll()
                                HapticEngine.playSuccessEffect(context, HapticEngine.getFactor(context))
                            }
                            showResetDialog = false
                        }
                    ) {
                        Text("确认清空", color = Color(0xFFCC4444))
                    }
                },
                dismissButton = {
                    TextButton(enabled = LocalPanelInteractive.current, onClick = { showResetDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsButton(label: String, iconIndex: Int, color: Color, onClick: () -> Unit) {
        val ctx = LocalContext.current
    val tc = remember { ThemeManager.applyColors(ctx) }
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(tc.textPrimary.copy(alpha = 0.04f))
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                HapticEngine.playTickEffect(context, HapticEngine.getFactor(context))
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val w = size.width; val h = size.height
            when (iconIndex) {
                0 -> {
                    drawCircle(color, w * 0.38f, Offset(w * 0.5f, h * 0.5f), style = Stroke(2f))
                    drawLine(color, Offset(w * 0.22f, h * 0.22f), Offset(w * 0.78f, h * 0.78f), 2f, StrokeCap.Round)
                }
                1 -> {
                    drawLine(color, Offset(w * 0.2f, h * 0.35f), Offset(w * 0.5f, h * 0.60f), 2f, StrokeCap.Round)
                    drawLine(color, Offset(w * 0.5f, h * 0.60f), Offset(w * 0.80f, h * 0.25f), 2f, StrokeCap.Round)
                    drawCircle(color, 1.5f, Offset(w * 0.5f, h * 0.6f))
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (iconIndex == 0) color else tc.textPrimary,
            modifier = Modifier.weight(1f)
        )
        Canvas(modifier = Modifier.size(14.dp)) {
            val c = tc.textTertiary
            drawLine(c, Offset(size.width * 0.25f, size.height * 0.2f), Offset(size.width * 0.75f, size.height * 0.5f), 2f, StrokeCap.Round)
            drawLine(c, Offset(size.width * 0.75f, size.height * 0.5f), Offset(size.width * 0.25f, size.height * 0.8f), 2f, StrokeCap.Round)
        }
    }
}

@Composable
private fun SettingsInfoRow(label: String, value: String, tc: ThemeColors) {
    Text(label, fontSize = 12.sp, color = tc.textSecondary, modifier = Modifier.width(72.dp).padding(vertical = 2.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = tc.textPrimary)
}

@Composable
private fun SectionHeader(title: String) {
    val ctx = LocalContext.current
    val tc = remember { ThemeManager.applyColors(ctx) }
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = tc.textSecondary,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val ctx = LocalContext.current
    val tc = remember { ThemeManager.applyColors(ctx) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(tc.textPrimary.copy(alpha = 0.06f))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) tc.surface else Color.Transparent
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            0.5.dp, tc.borderLight, RoundedCornerShape(6.dp)
                        ) else Modifier
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        HapticEngine.playTickEffect(ctx, HapticEngine.getFactor(ctx))
                        onSelect(index)
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) tc.textPrimary else tc.textSecondary
                )
            }
        }
    }
}

// ============================================================
//  收藏底部面板 —— 收藏列表 + 从收藏中抽取
// ============================================================

@Composable
private fun FavoritesBottomSheet(
    context: Context,
    favoriteDao: FavoriteDao,
    favoriteNames: List<String>,
    isFavoritesMode: Boolean,
    onDismiss: () -> Unit,
    onToggleFavoritesMode: () -> Unit
) {
    val tc = remember { ThemeManager.applyColors(context) }
    val scope = rememberCoroutineScope()
    var dragAccum by remember { mutableStateOf(0f) }

    // ---- 进出场动画 ----
    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val density = LocalDensity.current
    val screenHeightPx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val slidePx = screenHeightPx * 0.12f

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }
    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }
    BackHandler { dismiss() }

    Box(
        modifier = Modifier.fillMaxSize()
            .drawBehind {
                val s = (1f - sheetOffset.value.coerceIn(0f, 1f))
                drawRect(color = Color.Black.copy(alpha = 0.55f * s))
            }
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    val p = sheetOffset.value.coerceIn(0f, 1f)
                    scaleX = 1f - p * 0.08f
                    scaleY = 1f - p * 0.08f
                    alpha = 1f - p
                }
                .offset { IntOffset(0, (sheetOffset.value * slidePx).roundToInt()) }
                .fillMaxWidth()
                .height(620.dp)
                .then(if (isInteractive) Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragAccum = 0f },
                        onDragEnd = {
                            if (dragAccum > 100f) {
                                dismiss()
                            } else {
                                scope.launch { sheetOffset.animateTo(0f, tween(300, easing = appleBezier)) }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                            val target = (dragAccum / slidePx).coerceIn(0f, 1f)
                            scope.launch { sheetOffset.snapTo(target) }
                            change.consume()
                        }
                    )
                } else Modifier)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White.copy(alpha = 0.88f))
                .drawBehind { drawFrostNoise() }
                .border(width = 1.dp, brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.1f))
                ), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
        ) {
            // ===== 抓手 =====
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2C2C2C).copy(alpha = 0.15f)))
            }

            // ===== macOS 红绿灯标题栏 =====
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrafficLightDot(color = Color(0xFFFF5F57), onClick = { dismiss() })
                    TrafficLightDot(color = Color(0xFFFEBC2E))
                    TrafficLightDot(color = Color(0xFF28C840))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("收藏", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E), letterSpacing = 4.sp, fontFamily = LocalAppFontFamily.current)
                Spacer(modifier = Modifier.weight(1f))
                Text("${favoriteNames.size} 道菜", fontSize = 11.sp,
                    fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2C).copy(alpha = 0.35f))
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2C2C2C).copy(alpha = 0.06f)))

            // ===== 内容区 =====
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = 28.dp)
            ) {
                // ---- 仅从收藏抽取开关 ----
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isFavoritesMode) Color(0xFFFF3B30).copy(alpha = 0.06f)
                                else Color(0xFF2C2C2C).copy(alpha = 0.03f)
                            )
                            .border(
                                0.5.dp,
                                if (isFavoritesMode) Color(0xFFFF3B30).copy(alpha = 0.15f)
                                else Color.Transparent,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onToggleFavoritesMode() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeartIcon(filled = true,
                            color = if (isFavoritesMode) Color(0xFFFF3B30) else Color(0xFF8E8E93).copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("仅从收藏中抽取", fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                color = if (isFavoritesMode) Color(0xFFFF3B30).copy(alpha = 0.9f) else Color(0xFF1C1C1E))
                            Text("开启后抽奖只在收藏菜品中进行", fontSize = 10.sp,
                                color = Color(0xFF2C2C2C).copy(alpha = 0.35f), letterSpacing = 0.5.sp)
                        }
                        // iOS 风格开关
                        val trackWidth = 44.dp
                        val trackHeight = 26.dp
                        val thumbSize = 22.dp
                        val thumbOffset by animateFloatAsState(
                            targetValue = if (isFavoritesMode) with(LocalDensity.current) { (trackWidth - thumbSize).toPx() } else 2f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
                            label = "favThumb"
                        )
                        Box(
                            modifier = Modifier
                                .width(trackWidth)
                                .height(trackHeight)
                                .clip(RoundedCornerShape(trackHeight / 2))
                                .background(
                                    if (isFavoritesMode) Color(0xFFFF3B30)
                                    else Color(0xFFE0E0E0)
                                )
                        ) {
                            val thumbYOffset = with(LocalDensity.current) { 2.dp.toPx().roundToInt() }
                            Box(
                                modifier = Modifier
                                    .size(thumbSize)
                                    .offset { IntOffset(thumbOffset.roundToInt(), thumbYOffset) }
                                    .clip(CircleShape)
                                    .shadow(2.dp, CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }
                }

                if (favoriteNames.isEmpty()) {
                    // ---- 空状态 ----
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                HeartIcon(filled = false, color = Color(0xFF8E8E93).copy(alpha = 0.3f),
                                    modifier = Modifier.size(52.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("还没有收藏", fontSize = 14.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.45f))
                                Text("抽奖后点击菜名旁的爱心即可收藏", fontSize = 11.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.25f))
                            }
                        }
                    }
                } else {
                    // ---- 收藏列表 ----
                    items(favoriteNames) { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2C2C2C).copy(alpha = 0.03f))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HeartIcon(filled = true, color = Color(0xFFFF3B30), modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                color = Color(0xFF1C1C1E), modifier = Modifier.weight(1f),
                                fontFamily = LocalAppFontFamily.current)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        scope.launch { favoriteDao.delete(name) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null,
                                    modifier = Modifier.size(12.dp), tint = Color(0xFF8E8E93).copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
//  更新日志页面
// ============================================================

@Composable
private fun ChangeLogSheet(onDismiss: () -> Unit) {
    BackHandler { onDismiss() }

    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val scope = rememberCoroutineScope()
    var dragAccum by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val slidePx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() } * 0.12f

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }
    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }

    // 根据文本内容自动推断标签类型
    fun classifyChange(text: String): Pair<String, Color> {
        return when {
            text.contains("新增") || text.contains("引入") || text.contains("支持") || text.contains("首次") ->
                "新增" to Color(0xFF22C55E)
            text.contains("修复") || text.contains("修复") || text.contains("重置") ->
                "修复" to Color(0xFFEF4444)
            else ->
                "优化" to Color(0xFF3B82F6)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .drawBehind {
                val s = (1f - sheetOffset.value.coerceIn(0f, 1f))
                drawRect(color = Color.Black.copy(alpha = 0.55f * s))
            }
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    val p = sheetOffset.value.coerceIn(0f, 1f)
                    scaleX = 1f - p * 0.08f
                    scaleY = 1f - p * 0.08f
                    alpha = 1f - p
                }
                .offset { IntOffset(0, (sheetOffset.value * slidePx).roundToInt()) }
                .fillMaxWidth()
                .height(620.dp)
                .then(if (isInteractive) Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragAccum = 0f },
                        onDragEnd = {
                            if (dragAccum > 100f) dismiss()
                            else scope.launch { sheetOffset.animateTo(0f, tween(300, easing = appleBezier)) }
                        },
                        onDrag = { change, dragAmount ->
                            dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                            scope.launch { sheetOffset.snapTo((dragAccum / slidePx).coerceIn(0f, 1f)) }
                            change.consume()
                        }
                    )
                } else Modifier)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White.copy(alpha = 0.88f))
                .drawBehind { drawFrostNoise() }
                .border(width = 1.dp, brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.1f))
                ), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
        ) {
            // ===== 抓手 =====
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2C2C2C).copy(alpha = 0.15f)))
            }

            // ===== macOS 红绿灯标题栏 =====
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrafficLightDot(color = Color(0xFFFF5F57), onClick = { dismiss() })
                    TrafficLightDot(color = Color(0xFFFEBC2E))
                    TrafficLightDot(color = Color(0xFF28C840))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("更新日志", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E), letterSpacing = 4.sp, fontFamily = LocalAppFontFamily.current)
                Spacer(modifier = Modifier.weight(1f))
                CloseCircleButton(onClick = { dismiss() })
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2C2C2C).copy(alpha = 0.06f)))

            // ===== 版本列表 =====
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 28.dp)
            ) {
                itemsIndexed(CHANGE_LOG) { index, entry ->
                    val isLatest = index == 0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isLatest) Brush.horizontalGradient(listOf(
                                    Color.White.copy(alpha = 0.75f),
                                    Color(0xFFFFF8E7).copy(alpha = 0.45f)
                                )) else Brush.horizontalGradient(listOf(
                                    Color(0xAAFFFFFF),
                                    Color(0xAAFFFFFF)
                                ))
                            )
                            .border(
                                0.5.dp,
                                if (isLatest) Color(0xFFFFF0C0).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Column {
                            // 版本号行
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(entry.version, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1C1E), fontFamily = LocalAppFontFamily.current)
                                Spacer(modifier = Modifier.width(8.dp))
                                if (isLatest) {
                                    // CURRENT 胶囊
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF333333))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("CURRENT", fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                            color = Color.White, letterSpacing = 1.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(entry.date, fontSize = 10.sp,
                                    color = Color(0xFF2C2C2C).copy(alpha = 0.35f),
                                    fontFamily = LocalAppFontFamily.current)
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // 变更条目 + Typography Tags
                            entry.changes.forEach { change ->
                                val (label, color) = classifyChange(change)
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // 纯文字标签
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 1.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(color.copy(alpha = 0.10f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Medium,
                                            color = color, letterSpacing = 0.5.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(change, fontSize = 12.sp,
                                        color = Color(0xFF2C2C2C).copy(alpha = 0.7f),
                                        lineHeight = 18.sp,
                                        fontFamily = LocalAppFontFamily.current)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
//  统计仪表盘 Bottom Sheet —— macOS 经典红绿灯 + 数据可视化
// ============================================================

private data class StatsData(
    val totalPicks: Int,
    val weekTop: List<Pair<String, Int>>,
    val monthTop: List<Pair<String, Int>>,
    val periodCounts: Map<MealPeriod, Int>,
    val dailyCounts: List<Int>
)

private fun computeStats(history: List<HistoryWithCount>): StatsData {
    val now = System.currentTimeMillis()
    val cal = java.util.Calendar.getInstance()

    // 本周起始（周一 00:00）
    cal.timeInMillis = now
    cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    val weekStart = cal.timeInMillis

    // 本月起始（1 号 00:00）
    cal.timeInMillis = now
    cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    val monthStart = cal.timeInMillis

    // 本周 TOP5
    val weekTop = history
        .filter { it.timestamp >= weekStart }
        .groupingBy { it.foodName }.eachCount()
        .entries.sortedByDescending { it.value }
        .take(5).map { it.key to it.value }

    // 本月 TOP5
    val monthTop = history
        .filter { it.timestamp >= monthStart }
        .groupingBy { it.foodName }.eachCount()
        .entries.sortedByDescending { it.value }
        .take(5).map { it.key to it.value }

    // 时段分布
    val periodCounts = MealPeriod.entries.associateWith { 0 }.toMutableMap()
    history.forEach { entry ->
        val hour = java.util.Calendar.getInstance().apply {
            timeInMillis = entry.timestamp
        }.get(java.util.Calendar.HOUR_OF_DAY)
        val period = when (hour) {
            in 6..10 -> MealPeriod.BREAKFAST
            in 11..13 -> MealPeriod.LUNCH
            in 14..16 -> MealPeriod.AFTERNOON
            in 17..20 -> MealPeriod.DINNER
            else -> MealPeriod.MIDNIGHT
        }
        periodCounts[period] = (periodCounts[period] ?: 0) + 1
    }

    // 最近 7 天每日次数
    val dailyCounts = (6 downTo 0).map { daysAgo ->
        cal.timeInMillis = now
        cal.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis
        val dayEnd = dayStart + 24 * 60 * 60 * 1000L
        history.count { it.timestamp >= dayStart && it.timestamp < dayEnd }
    }

    return StatsData(history.size, weekTop, monthTop, periodCounts, dailyCounts)
}

@Composable
private fun StatsBottomSheet(dao: HistoryDao, onDismiss: () -> Unit) {
    val allHistory by dao.getHistoryWithCounts(1000).collectAsState(initial = emptyList())
    val stats = remember(allHistory) { computeStats(allHistory) }

    val sheetOffset = remember { Animatable(1f) }
    var isDismissing by remember { mutableStateOf(false) }
    val isInteractive = LocalPanelInteractive.current && !isDismissing
    val scope = rememberCoroutineScope()
    var dragAccum by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val slidePx = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() } * 0.12f

    LaunchedEffect(Unit) {
        sheetOffset.animateTo(0f, tween(350, easing = appleBezier))
    }

    fun dismiss() {
        isDismissing = true
        scope.launch {
            sheetOffset.animateTo(1f, tween(300, easing = appleBezier))
            onDismiss()
        }
    }

    BackHandler { dismiss() }

    Box(
        modifier = Modifier.fillMaxSize()
            .drawBehind {
                val s = (1f - sheetOffset.value.coerceIn(0f, 1f))
                drawRect(color = Color.Black.copy(alpha = 0.55f * s))
            }
            .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) { dismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    val p = sheetOffset.value.coerceIn(0f, 1f)
                    scaleX = 1f - p * 0.08f
                    scaleY = 1f - p * 0.08f
                    alpha = 1f - p
                }
                .offset { IntOffset(0, (sheetOffset.value * slidePx).roundToInt()) }
                .fillMaxWidth()
                .height(620.dp)
                .then(if (isInteractive) Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { dragAccum = 0f },
                        onDragEnd = {
                            if (dragAccum > 100f) {
                                dismiss()
                            } else {
                                scope.launch { sheetOffset.animateTo(0f, tween(300, easing = appleBezier)) }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            dragAccum = (dragAccum + dragAmount.y).coerceAtLeast(0f)
                            val target = (dragAccum / slidePx).coerceIn(0f, 1f)
                            scope.launch { sheetOffset.snapTo(target) }
                            change.consume()
                        }
                    )
                } else Modifier)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White.copy(alpha = 0.88f))
                .drawBehind { drawFrostNoise() }
                .border(width = 1.dp, brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.1f))
                ), shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .panelClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = isInteractive) {}
        ) {
            // ===== 抓手 =====
            Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.width(36.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF2C2C2C).copy(alpha = 0.15f)))
            }

            // ===== macOS 红绿灯标题栏 =====
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 经典红绿灯：红/黄/绿
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrafficLightDot(color = Color(0xFFFF5F57), onClick = { dismiss() })
                    TrafficLightDot(color = Color(0xFFFEBC2E))
                    TrafficLightDot(color = Color(0xFF28C840))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("统计", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E), letterSpacing = 4.sp, fontFamily = LocalAppFontFamily.current)
                Spacer(modifier = Modifier.weight(1f))
                // 占位平衡标题居中
                Box(modifier = Modifier.width(56.dp))
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF2C2C2C).copy(alpha = 0.06f)))

            // ===== 内容 =====
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 28.dp)
            ) {
                // ---- 总抽奖次数 ----
                item { StatsTotalCard(stats.totalPicks) }

                // ---- 本周 TOP5 ----
                item { StatsTopCard("本周最爱 TOP5", stats.weekTop) }

                // ---- 本月 TOP5 ----
                item { StatsTopCard("本月最爱 TOP5", stats.monthTop) }

                // ---- 时段偏好分布 ----
                item { StatsPeriodCard(stats.periodCounts) }

                // ---- 最近 7 天趋势 ----
                item { StatsTrendCard(stats.dailyCounts) }
            }
        }
    }
}

// ---- macOS 红绿灯圆点 ----
@Composable
private fun TrafficLightDot(color: Color, onClick: (() -> Unit)? = null) {
    val mod = if (onClick != null) {
        Modifier.size(12.dp).clip(CircleShape).background(color)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
    } else {
        Modifier.size(12.dp).clip(CircleShape).background(color)
    }
    Box(modifier = mod)
}

// ---- 矢量爱心图标（替代 Emoji） ----
@Composable
private fun HeartIcon(
    filled: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.08f
        val cx = w * 0.5f
        val cy = h * 0.55f
        val r = w * 0.22f
        if (filled) {
            // 实心爱心：两个圆 + 三角形填充
            drawCircle(color, radius = r, center = Offset(cx - r * 0.65f, cy - r * 0.3f))
            drawCircle(color, radius = r, center = Offset(cx + r * 0.65f, cy - r * 0.3f))
            drawPath(
                path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - r * 1.45f, cy + r * 0.1f)
                    lineTo(cx, cy + r * 1.55f)
                    lineTo(cx + r * 1.45f, cy + r * 0.1f)
                    close()
                },
                color = color
            )
        } else {
            // 空心爱心：两个圆弧 + 两条线
            drawCircle(color, radius = r, center = Offset(cx - r * 0.65f, cy - r * 0.3f),
                style = Stroke(width = strokeW))
            drawCircle(color, radius = r, center = Offset(cx + r * 0.65f, cy - r * 0.3f),
                style = Stroke(width = strokeW))
            drawLine(color, Offset(cx - r * 1.45f, cy + r * 0.1f),
                Offset(cx, cy + r * 1.55f), strokeW, StrokeCap.Round)
            drawLine(color, Offset(cx + r * 1.45f, cy + r * 0.1f),
                Offset(cx, cy + r * 1.55f), strokeW, StrokeCap.Round)
        }
    }
}

// ---- iOS 风格圆形关闭按钮 ----
@Composable
private fun CloseCircleButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.04f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier.size(14.dp),
            tint = Color.Black.copy(alpha = 0.35f)
        )
    }
}

// ---- macOS 生命周期安全弹性窗口容器 ----
// 保证退场动画 100% 播放完毕后才销毁子组件，避免动画被物理断流
@Composable
private fun MacOsSheetHost(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isRendered by remember { mutableStateOf(visible) }

    val transition = updateTransition(targetState = visible, label = "macOS Sheet Lifecycle")

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (targetState) tween(durationMillis = 130)
            else tween(durationMillis = 180)
        }, label = "Sheet Alpha"
    ) { if (it) 1f else 0f }

    // 生命周期拦截：只有退场动画完全结束才释放渲染树
    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.currentState && !transition.targetState) {
            isRendered = false
        } else if (transition.targetState) {
            isRendered = true
        }
    }

    if (isRendered) {
        Box(
            modifier = modifier.graphicsLayer {
                this.alpha = alpha
            }
        ) {
            // 退场（visible = false）的瞬间，将"不可交互"信号瞬时广播至所有子孙组件，
            // 确保关闭键 / 列表项 / Scrim 在离场动画期间不再拦截命中测试。
            CompositionLocalProvider(LocalPanelInteractive provides visible) {
                content()
            }
        }
    }
}

@Composable
private fun StatsTotalCard(total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF2C2C2C).copy(alpha = 0.9f), Color(0xFF3A3A3A).copy(alpha = 0.85f))))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(18.dp))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("总抽奖次数", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp,
                fontFamily = LocalAppFontFamily.current)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$total", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White,
                fontFamily = LocalAppFontFamily.current)
        }
        Spacer(modifier = Modifier.weight(1f))
        Canvas(modifier = Modifier.size(48.dp)) {
            val w = size.width; val h = size.height
            val c = Color.White.copy(alpha = 0.25f)
            drawLine(c, Offset(w * 0.2f, h * 0.8f), Offset(w * 0.2f, h * 0.55f), 4f, StrokeCap.Round)
            drawLine(c, Offset(w * 0.4f, h * 0.8f), Offset(w * 0.4f, h * 0.35f), 4f, StrokeCap.Round)
            drawLine(c, Offset(w * 0.6f, h * 0.8f), Offset(w * 0.6f, h * 0.45f), 4f, StrokeCap.Round)
            drawLine(c, Offset(w * 0.8f, h * 0.8f), Offset(w * 0.8f, h * 0.2f), 4f, StrokeCap.Round)
        }
    }
}

// ---- TOP5 横条卡片 ----
@Composable
private fun StatsTopCard(title: String, top: List<Pair<String, Int>>) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
            letterSpacing = 2.sp, fontFamily = LocalAppFontFamily.current, modifier = Modifier.padding(bottom = 12.dp))

        if (top.isEmpty()) {
            Text("暂无数据", fontSize = 12.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.25f),
                modifier = Modifier.padding(vertical = 8.dp))
        } else {
            val maxCount = top.maxOf { it.second }
            top.forEachIndexed { idx, (name, count) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${idx + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C).copy(alpha = 0.3f), modifier = Modifier.width(20.dp))
                    Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1C1C1E),
                        fontFamily = LocalAppFontFamily.current, modifier = Modifier.width(70.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    val barRatio = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                    Box(modifier = Modifier.weight(1f).height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C2C2C).copy(alpha = 0.05f))
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(barRatio).fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(when (idx) {
                                0 -> Brush.horizontalGradient(listOf(Color(0xFFFF6B6B), Color(0xFFEE5A5A)))
                                1 -> Brush.horizontalGradient(listOf(Color(0xFFFFB74D), Color(0xFFFEA743)))
                                2 -> Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFBC02D)))
                                else -> Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFF66BB6A)))
                            })
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$count", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF1C1C1E),
                        modifier = Modifier.width(28.dp), textAlign = TextAlign.End)
                }
            }
        }
    }
}

// ---- 时段偏好分布 ----
@Composable
private fun StatsPeriodCard(periodCounts: Map<MealPeriod, Int>) {
    val periods = MealPeriod.entries
    val maxCount = periodCounts.values.maxOrNull() ?: 0
    val periodColors = mapOf(
        MealPeriod.BREAKFAST to Color(0xFF81C784),
        MealPeriod.LUNCH to Color(0xFFFFB74D),
        MealPeriod.AFTERNOON to Color(0xFFBA68C8),
        MealPeriod.DINNER to Color(0xFFFF8A65),
        MealPeriod.MIDNIGHT to Color(0xFF64B5F6)
    )

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text("时段偏好分布", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
            letterSpacing = 2.sp, fontFamily = LocalAppFontFamily.current, modifier = Modifier.padding(bottom = 16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            periods.forEach { period ->
                val count = periodCounts[period] ?: 0
                val barRatio = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                val barColor = periodColors[period] ?: Color(0xFF2C2C2C)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C).copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 4.dp))
                    Box(modifier = Modifier
                        .width(28.dp)
                        .height((100 * barRatio).coerceAtLeast(2f).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(barColor))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(period.title, fontSize = 10.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.5f),
                        fontFamily = LocalAppFontFamily.current)
                }
            }
        }
    }
}

// ---- 最近 7 天趋势 ----
@Composable
private fun StatsTrendCard(dailyCounts: List<Int>) {
    val maxCount = dailyCounts.maxOrNull() ?: 0
    val dayLabels = listOf("7天前", "6天前", "5天前", "4天前", "3天前", "昨天", "今天")

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(0.5.dp, Color(0xFF2C2C2C).copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Text("最近 7 天", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1E),
            letterSpacing = 2.sp, fontFamily = LocalAppFontFamily.current, modifier = Modifier.padding(bottom = 16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            dailyCounts.forEachIndexed { idx, count ->
                val barRatio = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
                val isToday = idx == dailyCounts.lastIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("$count", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C).copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 4.dp))
                    Box(modifier = Modifier
                        .width(22.dp)
                        .height((80 * barRatio).coerceAtLeast(2f).dp)
                        .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                        .background(if (isToday) Color(0xFF2C2C2C) else Color(0xFF2C2C2C).copy(alpha = 0.25f)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(dayLabels[idx], fontSize = 8.sp, color = Color(0xFF2C2C2C).copy(alpha = 0.4f),
                        fontFamily = LocalAppFontFamily.current)
                }
            }
        }
    }
}
