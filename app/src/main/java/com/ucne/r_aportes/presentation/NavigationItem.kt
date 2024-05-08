package com.ucne.r_aportes.presentation

import androidx.compose.ui.graphics.vector.ImageVector
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    var badgeCount: Double? = null
)