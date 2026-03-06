package com.example.plant_id.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.navigation.FLOATING_NAV_BOTTOM_PADDING
import com.example.plant_id.ui.navigation.screenTopPadding
import com.example.plant_id.ui.theme.ArrowColor
import com.example.plant_id.ui.theme.CardBg
import com.example.plant_id.ui.theme.CardDivider
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.IconYellowBg
import com.example.plant_id.ui.theme.IconYellowColor
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor
import com.example.plant_id.ui.theme.VersionColor

@Composable
fun ProfileScreen() {

    var showFaq by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .screenTopPadding,
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            bottom = FLOATING_NAV_BOTTOM_PADDING.dp
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // ── 标题 ────────────────────────────────────────────
            Text(
                text = "我的",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = TextColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── 帮助 ─────────────────────────────────────────────
            SectionTitle(text = "帮助")
            MenuCard {
                MenuItem(
                    iconBg = IconYellowBg,
                    iconColor = IconYellowColor,
                    iconEmoji = "❓",
                    label = "常见问题",
                    sub = "NFC 绑定、浇水提醒等",
                    onClick = { showFaq = true }
                )
                MenuDivider()
                MenuItem(
                    iconBg = IconYellowBg,
                    iconColor = IconYellowColor,
                    iconEmoji = "🌱",
                    label = "关于芽见",
                    sub = "版本 v1.0.0",
                    onClick = { showAbout = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── 版本信息 ─────────────────────────────────────────
            Text(
                text = "芽见 v1.0.0  ·  © 2026",
                fontSize = 11.sp,
                color = VersionColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    // ── 常见问题弹窗 ──────────────────────────────────────────
    if (showFaq) {
        AlertDialog(
            onDismissRequest = { showFaq = false },
            title = {
                Text(
                    text = "常见问题",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextColor
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    FaqItem(
                        q = "如何将 NFC 标签绑定到植物？",
                        a = "①\u2003点击首页右上角「+」进入 NFC 扫描页\n" +
                                "②\u2003将手机背面靠近 NFC 标签\n" +
                                "③\u2003自动跳转到创建档案页，NFC 标签 ID 已预填\n" +
                                "④\u2003填写植物信息后点击「确认创建」即完成绑定\n\n" +
                                "💡 绑定后，在 App 内再触碰一次标签，可激活冷启动快速跳转功能。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "触碰已绑定标签，为什么没有直接跳转到档案？",
                        a = "绑定完成后需要在 App 打开的状态下再触碰一次标签，App 会在此时将识别信息写入标签。此后从桌面触碰即可一键直跳档案。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "一个 NFC 标签可以绑定多株植物吗？",
                        a = "不可以，每个标签只能对应一株植物。若想换绑，请先在原档案页终结该档案，标签会自动解绑，之后可重新绑定新植物。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "别人的手机扫我的标签会发生什么？",
                        a = "绑定关系仅存储在本机。其他手机扫描同一标签时，会打开芽见 App 并进入「创建新档案」页面，不会看到你的植物档案。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "为什么扫描未绑定的标签没有反应？",
                        a = "这是正常的。只有在首页点击「+」进入 NFC 扫描引导页时，才会识别未绑定标签并跳转到创建页。在其他页面触碰未绑定标签会被忽略。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "浇水提醒是怎么工作的？",
                        a = "App 每天早上 8:00 自动检查所有植物的浇水状态。如果某株植物超过设定间隔天数未浇水，会发送推送通知提醒你。"
                    )
                    FaqDivider()
                    FaqItem(
                        q = "推荐使用哪种 NFC 标签？",
                        a = "推荐使用 NTAG215 型号，支持标准 NDEF 格式，读写稳定。贴在花盆侧面或底部，建议选防水款以适应浇水环境。"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaq = false }) {
                    Text("知道了", color = GreenDark, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── 关于芽见弹窗 ──────────────────────────────────────────
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = {
                Text(
                    text = "关于芽见",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextColor
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🌱",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "芽见",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "每次浇灌，都是生命的芽见",
                        fontSize = 13.sp,
                        color = MutedColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "版本 v1.0.0",
                        fontSize = 12.sp,
                        color = MutedColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "作者 @Renais",
                        fontSize = 12.sp,
                        color = MutedColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "© 2026",
                        fontSize = 11.sp,
                        color = VersionColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("关闭", color = GreenDark, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = CardBg,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── 常见问题单条 ───────────────────────────────────────────────
@Composable
private fun FaqItem(q: String, a: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = q,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextColor,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = a,
            fontSize = 12.sp,
            color = MutedColor,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun FaqDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(1.dp)
            .background(CardDivider)
    )
}

/** 分组标题（.sec-title 样式） */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MutedColor,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp, top = 16.dp)
    )
}

/** 菜单卡片容器 */
@Composable
private fun MenuCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x388CA578),
                spotColor = Color(0x288CA578)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
    ) {
        content()
    }
}

/** 菜单项 */
@Composable
private fun MenuItem(
    iconBg: Color,
    iconColor: Color,
    iconEmoji: String,
    label: String,
    sub: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextColor)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = sub, fontSize = 11.sp, color = MutedColor)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = ArrowColor,
            modifier = Modifier.size(16.dp)
        )
    }
}

/** 菜单分隔线 */
@Composable
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(CardDivider)
    )
}

