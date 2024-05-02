package com.ucne.r_aportes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.r_aportes.data.local.entities.AporteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AporteDao {
    @Upsert()
    suspend fun save(aporte: AporteEntity)

    @Query(
        """
        SELECT * 
        FROM Aportes 
        WHERE aporteId=:id  
        LIMIT 1
        """
    )
    suspend fun find(id: Int): AporteEntity?

    @Delete
    suspend fun delete(aporte: AporteEntity)

    @Query("SELECT * FROM Aportes")
    fun getAll(): Flow<List<AporteEntity>>
}