package com.example.plant_id.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.data.entity.Plant
import com.example.plant_id.ui.theme.MutedColor

/**
 * 水平轮盘组件（使用 HorizontalPager）
 * @param plants 植物列表
 * @param currentIndex 当前选中索引
 * @param onIndexChange 索引变化回调
 * @param onCardClick 主卡片点击回调
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantCarousel(
    plants: List<Plant>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Pager 状态
    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { plants.size }
    )

    // 监听页面变化，同步到外部状态
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentIndex) {
            onIndexChange(pagerState.currentPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        // 左右各显示 1 页
        beyondViewportPageCount = 1,
        // 不循环，到尽头停止
        reverseLayout = false,
        key = { it }
    ) { page ->
        // 计算相对于当前页的位置
        val position = page - pagerState.currentPage

        // 副卡片缩小和虚化效果
        val scale = when (position) {
            0 -> 1f
            else -> 0.85f
        }

        val alpha = when (position) {
            0 -> 1f
            else -> 0.7f
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .scale(scale)
                    .alpha(alpha)
            ) {
                PlantCard(
                    plant = plants[page],
                    onClick = {
                        if (position == 0) {
                            onCardClick()
                        }
                    }
                )
            }
        }
    }
}

/**
 * 空状态组件
 */
@Composable
fun CarouselEmptyState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 有趣的话语
        PlantIllustration(
            iconName = "monstera",
            tint = MutedColor,
            modifier = Modifier
                .width(80.dp)
                .height(96.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Text(
            text = "还没有植物朋友",
            fontSize = 20.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            color = com.example.plant_id.ui.theme.TextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.material3.Text(
            text = "点击右上角的 + 按钮，\n迎接你的第一株植物吧！",
            fontSize = 14.sp,
            color = com.example.plant_id.ui.theme.MutedColor,
            lineHeight = 20.sp
        )
    }
}

/**
 * 单卡状态（不能滚动）
 */
@Composable
fun CarouselSingleCard(
    plant: Plant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PlantCard(
            plant = plant,
            onClick = onClick
        )
    }
}
