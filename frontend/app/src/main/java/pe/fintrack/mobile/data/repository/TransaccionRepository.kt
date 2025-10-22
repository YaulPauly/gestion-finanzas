package pe.fintrack.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import pe.fintrack.mobile.data.local.dao.TransaccionDao
import pe.fintrack.mobile.data.local.entity.TransaccionEntity

class TransaccionRepository (private val TransaccionDao: TransaccionDao){

    fun getAllTransactions(): Flow<List<TransaccionEntity>>{
        return TransaccionDao.getAllTransactions()
    }
    suspend fun getTransactionsById(id: Int): TransaccionEntity?{
        return TransaccionDao.getTransactionsById(id)
    }
    suspend fun insertTransaccion(transaccion: TransaccionEntity): Long{
        return TransaccionDao.insertTransaccion(transaccion)
    }
    suspend fun updateTransaccion(transaccion: TransaccionEntity){
        return TransaccionDao.updateTransaccion(transaccion)
    }
    suspend fun deletedTransaccion(transaccion: TransaccionEntity){
        return TransaccionDao.deletedTransaccion(transaccion)
    }
}