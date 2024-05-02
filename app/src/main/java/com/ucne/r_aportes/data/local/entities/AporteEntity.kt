package com.ucne.r_aportes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "Aportes")
data class AporteEntity(
    @PrimaryKey(autoGenerate = true)
    val aporteId: Int? = null,
    var fecha: LocalDate? = LocalDate.now(),
    var persona: String? = "",
    var observacion: String? = "",
    var monto: Double? = 0.0
)