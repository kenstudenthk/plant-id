package com.example.plant_id.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plant_id.ui.components.PlantCard
import com.example.plant_id.ui.navigation.FLOATING_NAV_BOTTOM_PADDING
import com.example.plant_id.ui.navigation.screenTopPadding
import com.example.plant_id.ui.theme.BtnBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor
import com.example.plant_id.ui.viewmodel.HomeViewModel

/**
 * 首页（对应原型 index.html）
 * - 渐变背景（由 Navigation.kt 提供）
 * - Georgia 衬线体标题 "我的植物"
 * - 胶囊型 Tab：存活中 / 已归档
 * - 2列网格：PlantCard + AddPlantCard（存活中最后一个）
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToPlantDetail: (Long) -> Unit = {},
    onNavigateToCreatePlant: () -> Unit = {}
) {
    // selectedTab 保存在 ViewModel 中，导航返回后不会重置
    val selectedTab = viewModel.selectedTab
    val tabs = listOf("存活中", "已归档")

    val alivePlants by viewModel.alivePlants.collectAsState()
    val archivedPlants by viewModel.archivedPlants.collectAsState()
    val currentList = if (selectedTab == 0) alivePlants else archivedPlants

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenTopPadding
    ) {
        // ── 状态栏 top padding ──────────────────────────────────
        Spacer(modifier = Modifier.height(14.dp))

        // ── 标题行（Georgia 衬线体 + 右侧添加按钮） ────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "我的植物",
                fontFamily = FontFamily.Serif,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = TextColor
            )
            IconButton(onClick = onNavigateToCreatePlant) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "添加植物",
                    tint = GreenDark
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── 胶囊型 Tab 切换 ─────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(BtnBg)
                .padding(4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .then(
                            if (selectedTab == index)
                                Modifier
                                    .shadow(2.dp, RoundedCornerShape(50.dp))
                                    .background(Color.White, RoundedCornerShape(50.dp))
                            else Modifier
                        )
                        .clickable { viewModel.selectedTab = index }
                        .padding(horizontal = 22.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold
                        else FontWeight.Normal,
                        color = if (selectedTab == index) TextColor else MutedColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── 植物网格 ────────────────────────────────────────────
        if (currentList.isEmpty() && selectedTab == 0) {
            // 存活中空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌱", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "还没有植物档案",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "点击右上角 + 开始添加你的第一株植物",
                        fontSize = 13.sp,
                        color = MutedColor
                    )
                }
            }
        } else if (currentList.isEmpty() && selectedTab == 1) {
            // 已归档空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌿", fontSize = 52.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "还没有已归档的植物",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "在植物档案中选择「归档」来结束养护",
                        fontSize = 13.sp,
                        color = MutedColor
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 0.dp,
                    bottom = FLOATING_NAV_BOTTOM_PADDING.dp
                ),
                verticalArrangement = Arrangement.spacedBy(13.dp),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                // 植物卡片
                items(
                    items = currentList,
                    key = { it.id }
                ) { plant ->
                    PlantCard(
                        plant = plant,
                        onClick = { onNavigateToPlantDetail(plant.id) },
                        wateringUrgency = viewModel.wateringStatusMap[plant.id]
                            ?: com.example.plant_id.ui.components.WateringUrgency.OK
                    )
                }
            }
        }
    }
}
