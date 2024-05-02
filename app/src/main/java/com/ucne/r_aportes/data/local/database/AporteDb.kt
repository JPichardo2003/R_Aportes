package com.ucne.r_aportes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ucne.r_aportes.data.local.dao.AporteDao
import com.ucne.r_aportes.data.local.entities.AporteEntity

@Database(
    entities = [
        AporteEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AporteDb : RoomDatabase() {
    abstract fun aporteDao(): AporteDao
}