package pe.fintrack.mobile.data.repository

import kotlinx.coroutines.flow.Flow
import pe.fintrack.mobile.data.local.dao.MetaDao
import pe.fintrack.mobile.data.local.entity.MetaEntity

class MetaRepository (private val MetaDao: MetaDao ) {

    fun getAllMetas(usuarioId: Int): Flow<List<MetaEntity>>{
        return MetaDao.getAllMetas(usuarioId)
    }
    suspend fun getMetaById(id: Int): MetaEntity?{
        return MetaDao.getMetaById(id)
    }
    suspend fun asignarIngreso(metaId: Int, montoIngresado: Double){
        return MetaDao.asignarIngreso(metaId, montoIngresado)
    }
    suspend fun marcarComoAlcanzada(metaId: Int){
        return MetaDao.marcarComoAlcanzada(metaId)
    }
    suspend fun insertMeta(meta: MetaEntity): Long{
        return MetaDao.insertMeta(meta)
    }
    suspend fun updateMeta(meta: MetaEntity){
        return MetaDao.updateMeta(meta)
    }
    suspend fun deletedMeta(meta: MetaEntity){
        return MetaDao.deletedMeta(meta)
    }
}