package com.example.mystore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mystore.R

val ValorantFontFamily = FontFamily(
    Font(R.font.valorant_font, FontWeight.Normal)
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = ValorantFontFamily),
    displayMedium = TextStyle(fontFamily = ValorantFontFamily),
    displaySmall = TextStyle(fontFamily = ValorantFontFamily),
    headlineLarge = TextStyle(fontFamily = ValorantFontFamily),
    headlineMedium = TextStyle(fontFamily = ValorantFontFamily),
    headlineSmall = TextStyle(fontFamily = ValorantFontFamily),
    titleLarge = TextStyle(fontFamily = ValorantFontFamily),
    titleMedium = TextStyle(fontFamily = ValorantFontFamily),
    titleSmall = TextStyle(fontFamily = ValorantFontFamily),
    bodyLarge = TextStyle(
        fontFamily = ValorantFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = ValorantFontFamily),
    bodySmall = TextStyle(fontFamily = ValorantFontFamily),
    labelLarge = TextStyle(fontFamily = ValorantFontFamily),
    labelMedium = TextStyle(fontFamily = ValorantFontFamily),
    labelSmall = TextStyle(fontFamily = ValorantFontFamily)
)
