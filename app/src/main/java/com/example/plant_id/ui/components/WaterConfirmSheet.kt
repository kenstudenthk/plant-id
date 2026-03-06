package com.example.plant_id.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.theme.BtnBg
import com.example.plant_id.ui.theme.CardBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.GreenLight
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor

// 浇水确认底部弹窗（对应原型 #waterModal）
@Composable
fun WaterConfirmSheet(
    plantName: String,
    onDismiss: () -> Unit,
    onWaterOnly: () -> Unit,
    onWaterAndPhoto: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x73000000))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardBg)
                .clickable(enabled = false) {}
                .padding(horizontal = 20.dp)
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            Text(
                text = "为 $plantName 浇水",
                fontFamily = FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = TextColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "记录此次浇水，可选择拍照留念。",
                fontSize = 13.sp,
                color = MutedColor,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 浇水 + 拍照（绿色渐变）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(GreenLight, GreenDark)))
                    .clickable(onClick = onWaterAndPhoto)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "浇水 + 拍照",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 仅浇水（浅绿）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1F4A7C59))
                    .clickable(onClick = onWaterOnly)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "仅浇水，不拍照",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenDark
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 取消
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BtnBg)
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "取消",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MutedColor
                )
            }
        }
    }
}
