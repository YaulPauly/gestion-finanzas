package pe.fintrack.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pe.fintrack.mobile.data.local.entity.MetaEntity

@Dao
interface MetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(meta: MetaEntity): Long

    @Update
    suspend fun updateMeta(meta: MetaEntity)

    @Delete
    suspend fun deletedMeta(meta: MetaEntity)

    @Query("SELECT * FROM metas WHERE usuarioId = :usuarioId ORDER BY fechaLimite ASC")
    fun getAllMetas(usuarioId: Int): Flow<List<MetaEntity>>

    @Query("SELECT * FROM metas WHERE id = :id")
    suspend fun getMetaById(id: Int): MetaEntity?

    @Query("UPDATE metas SET acumulado = acumulado + :montoIngresado WHERE id = :metaId")
    suspend fun asignarIngreso(metaId: Int, montoIngresado: Double)

    @Query("UPDATE metas SET estado = 'ALCANZADA' WHERE id = :metaId")
    suspend fun marcarComoAlcanzada(metaId: Int)

}