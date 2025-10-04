package com.comixa.app.model

data class DrawerSection(
    val title: String,
    val iconRes: Int,
    val subItems: List<DrawerSubItem>,
    var expanded: Boolean = false
)

data class DrawerSubItem(
    val title: String,
    val destinationId: Int
)
