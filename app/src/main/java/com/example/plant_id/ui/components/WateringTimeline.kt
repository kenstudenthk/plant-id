package com.example.plant_id.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.data.entity.WateringLog
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor
import com.example.plant_id.ui.theme.TimelineColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 浇水时间线组件，展示某株植物的所有浇水记录（时间倒序）
// 对应原型 .timeline / .tl-item 样式
@Composable
fun WateringTimeline(
    logs: List<WateringLog>,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) {
        Text(
            text = "暂无浇水记录",
            fontSize = 13.sp,
            color = MutedColor,
            modifier = modifier.padding(vertical = 4.dp)
        )
        return
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINA) }

    Column(modifier = modifier) {
        logs.forEachIndexed { index, log ->
            val isLast = index == logs.size - 1
            Row(modifier = Modifier.fillMaxWidth()) {
                // 左侧：时间线圆点 + 连接线
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(22.dp)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    // 绿色圆点
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GreenDark)
                    )
                    // 竖向连接线（最后一项不显示）
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(TimelineColor)
                        )
                    }
                }
                // 右侧：日期 + 描述
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 12.dp,
                            bottom = if (isLast) 0.dp else 10.dp
                        )
                ) {
                    Text(
                        text = dateFormatter.format(Date(log.wateredAt)),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextColor
                    )
                    Text(
                        text = if (log.photoPath.isNotEmpty()) "浇水 + 拍照 1 张" else "仅浇水",
                        fontSize = 12.sp,
                        color = MutedColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
