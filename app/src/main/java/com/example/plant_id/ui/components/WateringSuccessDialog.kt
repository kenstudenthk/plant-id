package com.example.plant_id.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.theme.CardBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.LeafGreen
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor
import kotlinx.coroutines.delay

@Composable
fun WateringSuccessDialog(
    plantName: String,
    nextWateringDate: String,
    onDismiss: () -> Unit
) {
    // 动画状态
    var animationStarted by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // 背景圆缩放动画
    val backgroundScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "bgScale"
    )

    // 对勾描边动画
    val checkProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "checkProgress"
    )

    // 脉冲环动画
    val pulseInfinite = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseInfinite.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by pulseInfinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // 闪光点动画
    val sparkleInfinite = rememberInfiniteTransition(label = "sparkle")
    val sparkleAlpha by sparkleInfinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleAlpha"
    )

    // 文本渐入
    LaunchedEffect(animationStarted) {
        if (animationStarted) {
            delay(400)
            showContent = true
        }
    }

    // 启动动画
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    // 文字透明度动画
    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "contentAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        // 卡片
        Box(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(CardBg)
                .clickable(enabled = false) {} // 阻止点击穿透
                .padding(vertical = 40.dp, horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 动画区域
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 脉冲环
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.minDimension / 2 * 0.7f

                        // 3层脉冲环
                        drawCircle(
                            color = GreenDark.copy(alpha = pulseAlpha * 0.15f),
                            radius = radius * pulseScale,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = GreenDark.copy(alpha = pulseAlpha * 0.1f),
                            radius = radius * (pulseScale * 0.85f),
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = GreenDark.copy(alpha = pulseAlpha * 0.05f),
                            radius = radius * (pulseScale * 0.7f),
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // 对勾背景圆
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(backgroundScale)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFE8F0E0), Color(0xFFD8E8D0))
                                ),
                                shape = RoundedCornerShape(55.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // 对勾
                        Canvas(modifier = Modifier.size(60.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.25f, size.height * 0.5f)
                                lineTo(size.width * 0.42f, size.height * 0.68f)
                                lineTo(size.width * 0.75f, size.height * 0.32f)
                            }
                            drawPath(
                                path = path,
                                color = GreenDark,
                                style = Stroke(
                                    width = 5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }

                    // 闪光点
                    val sparklePositions = listOf(
                        Offset(145f, 35f),
                        Offset(35f, 130f),
                        Offset(160f, 90f),
                        Offset(25f, 70f)
                    )
                    sparklePositions.forEachIndexed { index, offset ->
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = (offset.x - 90).dp,
                                    y = (offset.y - 90).dp
                                )
                                .size(if (index % 2 == 0) 8.dp else 6.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = LeafGreen.copy(alpha = sparkleAlpha),
                                    radius = if (index % 2 == 0) 4.dp.toPx() else 3.dp.toPx()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 文本内容
                Column(
                    modifier = Modifier.alpha(contentAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "浇水成功！",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$plantName 已经吸饱了水分",
                        fontSize = 14.sp,
                        color = TextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "下次浇水：$nextWateringDate",
                        fontSize = 12.sp,
                        color = MutedColor
                    )
                }
            }
        }
    }
}
