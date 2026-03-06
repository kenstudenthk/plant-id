package com.example.plant_id.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.ui.theme.BtnBg
import com.example.plant_id.ui.theme.CardBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.GreenLightBg
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.StatusGrayBg
import com.example.plant_id.ui.theme.TextColor

/**
 * 浇水紧急程度枚举
 */
enum class WateringUrgency {
    OVERDUE,    // 已超期（红色）
    DUE_TODAY,  // 今日到期（橙色）
    OK          // 状态良好（无指示）
}

/**
 * 植物列表卡片（网格版，对应原型 .card）
 * - 上方：PNG 植物插图
 * - 中间：植物名称 + 品种
 * - 下方：状态胶囊（浇水间隔）
 * - 右上角：浇水状态小圆点（可选）
 */
@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    wateringUrgency: WateringUrgency = WateringUrgency.OK
) {
    val dashColor = Color(0x47506E3C)  // 虚线颜色

    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x478CA578),
                spotColor = Color(0x288CA578)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .drawBehind {
                // 内边缘虚线圈：距离边缘4dp，1dp粗细，2dp on/off
                val strokeWidth = 1.dp.toPx()
                val dashOn = 2.dp.toPx()
                val dashOff = 2.dp.toPx()
                val inset = 4.dp.toPx()
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(dashOn, dashOff)
                        )
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    size = this.size.copy(
                        width = this.size.width - inset * 2,
                        height = this.size.height - inset * 2
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset)
                )
            }
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── 植物插图区域 ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            PlantIllustration(iconName = plant.iconName)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ── 植物名称 ───────────────────────────────────────────────
        Text(
            text = plant.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // ── 品种 ─────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = plant.species,
            fontSize = 11.sp,
            color = MutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── 状态胶囊 ───────────────────────────────────────────────
        StatusPill(
            wateringIntervalDays = plant.wateringIntervalDays,
            wateringUrgency = wateringUrgency
        )
    }
}

/** 植物 SVG 插图（从 assets/svg/ 加载） */
@Composable
fun PlantIllustration(
    iconName: String,
    modifier: Modifier = Modifier.size(width = 88.dp, height = 104.dp),
    tint: androidx.compose.ui.graphics.Color? = null
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/png/$iconName.png")
            .build(),
        contentDescription = iconName,
        contentScale = ContentScale.Fit,
        modifier = modifier,
        colorFilter = tint?.let { androidx.compose.ui.graphics.ColorFilter.tint(it) },
        placeholder = null,
        error = null
    )
}

/**
 * 状态胶囊（.pill 样式）
 * 小圆点颜色随浇水紧急度变化：灰色（正常）/ 橙色（今日）/ 红色（超期）
 */
@Composable
private fun StatusPill(
    wateringIntervalDays: Int,
    wateringUrgency: WateringUrgency = WateringUrgency.OK
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(BtnBg)
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 状态指示圆点（颜色随浇水紧急度变化）
        val dotColor = when (wateringUrgency) {
            WateringUrgency.OVERDUE -> Color(0xFFC85050)    // 红色
            WateringUrgency.DUE_TODAY -> Color(0xFFE89B40)  // 橙色
            WateringUrgency.OK -> Color(0xFFB0B0B0)         // 灰色
        }
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "每 $wateringIntervalDays 天浇水",
            fontSize = 12.sp,
            color = Color(0xFF555555)
        )
    }
}

/**
 * 「添加植物」虚线卡片（对应原型 .add-card）
 * 原型：background:rgba(242,238,230,.55); border:2px dashed rgba(80,110,60,.28)
 */
@Composable
fun AddPlantCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dashColor = Color(0x47506E3C)  // rgba(80,110,60,.28)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0x8CF2EEE6))  // rgba(242,238,230,.55)
            .drawBehind {
                // 2dp 虚线边框
                val strokeWidth = 2.dp.toPx()
                val dashOn = 8.dp.toPx()
                val dashOff = 5.dp.toPx()
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(dashOn, dashOff)
                        )
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            }
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 虚线圆圈 + 加号（用 emoji 代替 SVG 虚线圆圈）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(GreenLightBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp,
                    color = GreenDark,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "添加植物", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "扫描 NFC 标签", fontSize = 11.sp, color = MutedColor)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(StatusGrayBg)
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "扫描标签", fontSize = 12.sp, color = MutedColor)
        }
    }
}
