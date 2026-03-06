package com.example.plant_id.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.MutedColor
import kotlinx.coroutines.delay

/**
 * NFC 识别成功弹窗（高级版）
 * - 毛玻璃半透明背景遮罩
 * - 弹性入场动画（spring）+ 淡出退场
 * - 自定义 Canvas 勾号图标
 * - 自动 1800ms 后消失
 */
@Composable
fun NfcSuccessDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    duration: Long = 1800
) {
    var shouldAnimate by remember { mutableStateOf(false) }
    var checkProgress by remember { mutableStateOf(0f) }

    // 弹性缩放入场（spring 弹跳）
    val scale by animateFloatAsState(
        targetValue = if (shouldAnimate) 1f else 0.72f,
        animationSpec = if (shouldAnimate)
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        else
            tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "scale"
    )

    // 整体透明度
    val alpha by animateFloatAsState(
        targetValue = if (shouldAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    // 遮罩透明度（比卡片稍早消失）
    val backdropAlpha by animateFloatAsState(
        targetValue = if (shouldAnimate) 0.35f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "backdrop"
    )

    // 勾号绘制进度（0→1，入场后触发）
    val checkAnim by animateFloatAsState(
        targetValue = checkProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "check"
    )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            shouldAnimate = true
            delay(200)               // 等卡片弹出后再画勾
            checkProgress = 1f
            delay(duration)
            checkProgress = 0f
            shouldAnimate = false
            delay(280)
            onDismiss()
        } else {
            shouldAnimate = false
            checkProgress = 0f
        }
    }

    if (!isVisible && alpha == 0f) return

    // ── 遮罩层 ────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backdropAlpha)),
        contentAlignment = Alignment.Center
    ) {
        // ── 弹窗卡片 ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Color(0x608CA578),
                    spotColor = Color(0x408CA578)
                )
                .width(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8F6F0),   // 顶部：温暖米白
                            Color(0xFFEEEBE2)    // 底部：深一点的米色
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .clip(RoundedCornerShape(28.dp))
                .padding(vertical = 28.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ── 勾号圆形图标 ───────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GreenDark.copy(alpha = 0.15f),
                                    GreenDark.copy(alpha = 0.05f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val r = size.width * 0.40f
                        val sw = 2.2.dp.toPx()

                        // 外圆环
                        drawCircle(
                            color = GreenDark.copy(alpha = 0.25f),
                            radius = r,
                            center = Offset(cx, cy),
                            style = Stroke(sw * 0.8f)
                        )

                        // 勾号（分两段：短边 + 长边，随 checkAnim 进度绘制）
                        if (checkAnim > 0f) {
                            val shortEnd = Offset(cx - r * 0.12f, cy + r * 0.25f)
                            val start = Offset(cx - r * 0.45f, cy)
                            val end = Offset(cx + r * 0.40f, cy - r * 0.35f)

                            // 第一段：从 start → shortEnd（进度 0~0.45）
                            val seg1Progress = (checkAnim / 0.45f).coerceIn(0f, 1f)
                            drawLine(
                                color = GreenDark,
                                start = start,
                                end = Offset(
                                    start.x + (shortEnd.x - start.x) * seg1Progress,
                                    start.y + (shortEnd.y - start.y) * seg1Progress
                                ),
                                strokeWidth = sw,
                                cap = StrokeCap.Round
                            )

                            // 第二段：从 shortEnd → end（进度 0.45~1）
                            if (checkAnim > 0.45f) {
                                val seg2Progress = ((checkAnim - 0.45f) / 0.55f).coerceIn(0f, 1f)
                                drawLine(
                                    color = GreenDark,
                                    start = shortEnd,
                                    end = Offset(
                                        shortEnd.x + (end.x - shortEnd.x) * seg2Progress,
                                        shortEnd.y + (end.y - shortEnd.y) * seg2Progress
                                    ),
                                    strokeWidth = sw,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── 主标题 ────────────────────────────────────────
                Text(
                    text = "识别成功",
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = GreenDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(5.dp))

                // ── 副文案 ────────────────────────────────────────
                Text(
                    text = "已通过 NFC 进入档案",
                    fontSize = 11.sp,
                    color = MutedColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
