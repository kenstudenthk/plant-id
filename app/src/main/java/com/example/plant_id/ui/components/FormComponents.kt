package com.example.plant_id.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plant_id.ui.theme.BtnBg
import com.example.plant_id.ui.theme.CardBg
import com.example.plant_id.ui.theme.GreenDark
import com.example.plant_id.ui.theme.MutedColor
import com.example.plant_id.ui.theme.TextColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ═══════════════════════════════════════════════════════════════
// 表单卡片结构
// ═══════════════════════════════════════════════════════════════

/**
 * 表单卡片（对应原型 .form-card）
 * background: var(--card); border-radius: 24px; box-shadow; padding: 20px
 */
@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x478CA578),
                spotColor = Color(0x288CA578)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .padding(20.dp),
        content = content
    )
}

/**
 * 表单节标题（对应原型 .form-section）
 * font-size:11px; font-weight:700; color:var(--muted); uppercase; letter-spacing:.8px
 */
@Composable
fun FormSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MutedColor,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(bottom = 14.dp)
    )
}

/**
 * 表单字段标签（对应原型 .flabel）
 * font-size:13px; font-weight:600; color:var(--text)
 * required=true 时末尾显示红色星号
 */
@Composable
fun FormLabel(text: String, required: Boolean = false) {
    val displayText = if (required) {
        buildAnnotatedString {
            append(text)
            append(" ")
            withStyle(SpanStyle(color = Color(0xFFC06050))) { append("*") }
        }
    } else {
        buildAnnotatedString { append(text) }
    }
    Text(
        text = displayText,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextColor,
        modifier = Modifier.padding(bottom = 7.dp)
    )
}

// ═══════════════════════════════════════════════════════════════
// 输入组件
// ═══════════════════════════════════════════════════════════════

/**
 * 单行文本输入框（对应原型 .finput）
 * - 默认：BtnBg 背景，透明边框
 * - 聚焦：白色背景，绿色 1.5dp 边框
 */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = TextColor),
        cursorBrush = SolidColor(GreenDark),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                1.5.dp,
                if (isFocused) GreenDark else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) Color.White else BtnBg)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 14.sp, color = Color(0xFFBEBAB3))
                }
                innerTextField()
            }
        }
    )
}

/**
 * 多行文本区域（对应原型 .ftxt）
 * - min-height: 72dp，可向下扩展
 */
@Composable
fun FormTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 14.sp, color = TextColor, lineHeight = 20.sp),
        cursorBrush = SolidColor(GreenDark),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                1.5.dp,
                if (isFocused) GreenDark else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (isFocused) Color.White else BtnBg)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(text = placeholder, fontSize = 14.sp, color = Color(0xFFBEBAB3))
                }
                innerTextField()
            }
        }
    )
}

/**
 * 日期选择字段（对应原型 input[type=date]）
 * 点击后弹出原生 DatePickerDialog
 */
@Composable
fun DatePickerField(
    timestampMs: Long,
    onDateChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = timestampMs }
    val dateText = remember(timestampMs) {
        SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.CHINA).format(Date(timestampMs))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BtnBg)
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        Calendar.getInstance().apply {
                            set(year, month, day, 0, 0, 0)
                            set(Calendar.MILLISECOND, 0)
                            onDateChanged(timeInMillis)
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = dateText, fontSize = 14.sp, color = TextColor)
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "选择日期",
            tint = MutedColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 浇水间隔选择器（对应原型 .fsel）
 * 选项：1/2/3/5/7/10/14/30 天，点击后弹出下拉菜单
 */
@Composable
fun WateringIntervalPicker(
    selectedDays: Int,
    onDaysSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        1 to "每天浇水",
        2 to "每 2 天",
        3 to "每 3 天",
        5 to "每 5 天",
        7 to "每 7 天（一周一次）",
        10 to "每 10 天",
        14 to "每两周",
        30 to "每月"
    )
    val label = options.find { it.first == selectedDays }?.second ?: "选择浇水频率"

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        // 触发区域（样式同 .fsel）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.5.dp,
                    if (expanded) GreenDark else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(if (expanded) Color.White else BtnBg)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextColor
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MutedColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // 下拉菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (days, text) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = text,
                            fontSize = 14.sp,
                            color = if (days == selectedDays) GreenDark else TextColor,
                            fontWeight = if (days == selectedDays) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onDaysSelected(days)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 辅助组件
// ═══════════════════════════════════════════════════════════════

/** 字段提示文字（对应原型 .fhint） */
@Composable
fun FormHint(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = MutedColor,
        modifier = Modifier.padding(top = 5.dp)
    )
}

/** 表单字段组包装（对应原型 .fg，提供底部 14dp 间距） */
@Composable
fun FormFieldGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(bottom = 14.dp), content = content)
}

/** 表单字段组包装（末尾字段，无底部间距） */
@Composable
fun FormFieldGroupLast(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier, content = content)
}

// ═══════════════════════════════════════════════════════════════
// 操作按钮行（取消 + 确认）
// ═══════════════════════════════════════════════════════════════

/**
 * 表单底部操作按钮行（对应原型 .btns）
 * @param cancelText  取消按钮文字
 * @param confirmText 确认按钮文字（绿色渐变）
 * @param onCancel    取消回调
 * @param onConfirm   确认回调
 */
@Composable
fun FormActionButtons(
    cancelText: String = "取消",
    confirmText: String = "确认",
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 取消按钮（.btn-cancel）
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = Color(0x388CA578),
                    spotColor = Color(0x288CA578)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(CardBg)
                .clickable(onClick = onCancel)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cancelText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MutedColor
            )
        }

        // 确认按钮（.btn-submit，渐变绿）
        Box(
            modifier = Modifier
                .weight(2f)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = Color(0x4A3C5A32),
                    spotColor = Color(0x303C5A32)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF4A7C59), Color(0xFF3D5C33))
                    )
                )
                .clickable(onClick = onConfirm)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = confirmText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/** 通用页面顶部标题栏（返回按钮 + 居中标题 + 可选右侧按钮） */
@Composable
fun FormTopBar(
    title: String,
    onBack: () -> Unit,
    rightContent: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 返回按钮（.hbtn）
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = androidx.compose.foundation.shape.CircleShape,
                    ambientColor = Color(0x388CA578),
                    spotColor = Color(0x288CA578)
                )
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(CardBg)
                .clickable(onClick = onBack)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = TextColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // 标题（.htitle）
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextColor,
            modifier = Modifier.align(Alignment.Center)
        )

        // 右侧按钮（可选）
        if (rightContent != null) {
            Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                rightContent()
            }
        } else {
            Spacer(modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd))
        }
    }
}
