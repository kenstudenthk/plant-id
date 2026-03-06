package com.example.plant_id.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.theme.BtnBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.GreenLightBg
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor

// 植物图标数据（iconName 对应 assets/png/ 目录下的文件名，不含扩展名）
// speciesDefault：创建档案时自动填入的品种名称；notesDefault：自动填入的养护备注
// "其他" 图标的两者均为空字符串，选中后不自动填写任何字段
data class PlantIconItem(
    val iconName: String,
    val label: String,
    val speciesDefault: String = "",
    val notesDefault: String = ""
)

// 9 个植物图标列表
val PLANT_ICONS = listOf(
    PlantIconItem(
        iconName = "monstera",
        label = "龟背竹",
        speciesDefault = "龟背竹",
        notesDefault = "喜散射光，避免强直射。每1-2周浇水，待土表干透再浇。喜湿润，可定期叶面喷水。"
    ),
    PlantIconItem(
        iconName = "cactus",
        label = "仙人掌",
        speciesDefault = "仙人掌",
        notesDefault = "极耐旱，每2-4周少量浇水。喜充足阳光，通风良好。冬季基本断水，避免积水烂根。"
    ),
    PlantIconItem(
        iconName = "money-tree",
        label = "发财树",
        speciesDefault = "发财树",
        notesDefault = "喜明亮间接光，耐半阴。每10-14天浇水，待土壤大部分干透后浇透。不耐积水。"
    ),
    PlantIconItem(
        iconName = "succulent",
        label = "多肉",
        speciesDefault = "多肉植物",
        notesDefault = "喜充足阳光，耐旱。春秋生长期每1-2周浇水，夏冬休眠期减少浇水，避免积水。"
    ),
    PlantIconItem(
        iconName = "bird-of-paradise",
        label = "天堂鸟",
        speciesDefault = "天堂鸟",
        notesDefault = "喜充足光照，可耐直射。每5-7天浇水，保持土壤湿润但不积水。生长旺盛，可定期施肥。"
    ),
    PlantIconItem(
        iconName = "spider-plant",
        label = "吊兰",
        speciesDefault = "吊兰",
        notesDefault = "适应性强，喜散射光。每5-7天浇水，保持土壤微湿。可悬挂摆放，净化空气效果佳。"
    ),
    PlantIconItem(
        iconName = "hoya",
        label = "心叶球兰",
        speciesDefault = "心叶球兰",
        notesDefault = "喜明亮散射光，耐半阴。每1-2周浇水，土表干透再浇。耐干旱，不耐积水，生长缓慢。"
    ),
    PlantIconItem(
        iconName = "orchid",
        label = "蝴蝶兰",
        speciesDefault = "蝴蝶兰",
        notesDefault = "喜散射光，避免强光直射。每7-10天浇水，待介质干透后浇。喜高湿，可喷雾加湿。"
    ),
    PlantIconItem(
        iconName = "schefflera",
        label = "其他",
        speciesDefault = "",
        notesDefault = ""
    ),
)

/**
 * 图标选择器（对应原型 .icon-preview + .icon-grid）
 * - 顶部：当前选中图标的预览（72dp 圆角框 + 名称 + 说明）
 * - 分隔线
 * - 3 列图标网格，选中项有绿色边框和背景
 *
 * @param selectedIconName 当前选中的图标名称
 * @param onIconSelected   用户点击图标后的回调 (iconName, iconLabel, speciesDefault, notesDefault)
 */
@Composable
fun IconSelector(
    selectedIconName: String,
    onIconSelected: (iconName: String, iconLabel: String, speciesDefault: String, notesDefault: String) -> Unit
) {
    val selectedIcon = PLANT_ICONS.find { it.iconName == selectedIconName }
        ?: PLANT_ICONS.first()

    Column {
        // ── 预览行（.icon-preview） ────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 预览图（72dp 圆角方框，对应 .icon-preview-img）
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color(0x3D8CA578),
                        spotColor = Color(0x258CA578)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(GreenLightBg),
                contentAlignment = Alignment.Center
            ) {
                PlantIllustration(
                    iconName = selectedIconName,
                    modifier = Modifier.size(52.dp)
                )
            }

            // 文字信息
            Column {
                Text(
                    text = selectedIcon.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "将显示在植物卡片和档案封面上",
                    fontSize = 12.sp,
                    color = MutedColor
                )
            }
        }

        // ── 分隔线（border-bottom 1px） ────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x0F000000))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ── 3 列图标网格（.icon-grid） ─────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            PLANT_ICONS.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { icon ->
                        IconGridItem(
                            icon = icon,
                            selected = icon.iconName == selectedIconName,
                            onClick = {
                                onIconSelected(
                                    icon.iconName,
                                    icon.label,
                                    icon.speciesDefault,
                                    icon.notesDefault
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // 补空格（当行不足 3 个时）
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/** 单个图标网格项（.icon-item / .icon-item.active） */
@Composable
private fun IconGridItem(
    icon: PlantIconItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = 2.dp,
                color = if (selected) GreenDark else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GreenLightBg else BtnBg)
            .clickable(onClick = onClick)
            .padding(top = 12.dp, start = 8.dp, end = 8.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PlantIllustration(
            iconName = icon.iconName,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = icon.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) GreenDark else TextColor
        )
    }
}
