package pe.fintrack.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pe.fintrack.mobile.data.local.entity.TransaccionEntity

@Dao
interface TransaccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaccion(transaccion: TransaccionEntity): Long

    @Update
    suspend fun updateTransaccion(transaccion: TransaccionEntity)

    @Delete
    suspend fun deletedTransaccion(transaccion: TransaccionEntity)

    @Query("SELECT * FROM transacciones ORDER BY fecha ASC")
    fun getAllTransactions(): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun getTransactionsById(id: Int): TransaccionEntity?

}