package com.comixa.core.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watched_folders",
    indices = [Index(value = ["path"], unique = true)],
)
data class WatchedFolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val addedAt: Long,
)
