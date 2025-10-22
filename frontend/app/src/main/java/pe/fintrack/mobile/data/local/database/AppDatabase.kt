package pe.fintrack.mobile.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.fintrack.mobile.data.local.dao.MetaDao
import pe.fintrack.mobile.data.local.dao.TransaccionDao
import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.local.entity.CategoriaEntity
import pe.fintrack.mobile.data.local.entity.MetaEntity
import pe.fintrack.mobile.data.local.entity.TransaccionEntity
import pe.fintrack.mobile.data.local.entity.UsuarioEntity
import pe.fintrack.mobile.data.local.utils.Converters

@Database(
    entities = [
        UsuarioEntity::class,
        CategoriaEntity::class,
        MetaEntity::class,
        TransaccionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class) // <- ¡Importante para usar los Enums!
abstract class AppDatabase: RoomDatabase(){
    abstract fun TransaccionDao(): TransaccionDao
    abstract fun MetaDao(): MetaDao
    abstract fun UsuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase (context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fintrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE=instance
                instance
            }
        }
    }
}