package com.example.plant_id.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

/**
 * Material3 颜色方案——映射到原型配色
 * 注意：大量 UI 直接使用 Color.kt 中的自定义颜色，而非 Material3 token
 */
private val AppColorScheme = lightColorScheme(
    primary = GreenDark,
    onPrimary = CardBg,
    primaryContainer = GreenLightBg,
    onPrimaryContainer = GreenDark,
    secondary = LeafGreen,
    onSecondary = TextColor,
    background = BgMid,
    onBackground = TextColor,
    surface = CardBg,
    onSurface = TextColor,
    surfaceVariant = BtnBg,
    onSurfaceVariant = MutedColor,
    outline = CardDivider,
    error = UrgentColor,
    onError = CardBg,
)

/** 全局渐变背景画笔（155deg，对角方向） */
val AppBackgroundBrush: Brush
    get() = Brush.linearGradient(
        colorStops = arrayOf(
            0f to BgTop,
            0.45f to BgMid,
            1f to BgBot,
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

@Composable
fun PlantidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
