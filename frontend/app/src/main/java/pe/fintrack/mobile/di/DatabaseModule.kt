package pe.fintrack.mobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pe.fintrack.mobile.data.local.database.AppDatabase
import pe.fintrack.mobile.data.local.dao.UsuarioDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // PROPORCIONA LA BASE DE DATOS ROOM
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fintrack_db" // Nombre de tu base de datos
        ).build()
    }

    // PROPORCIONA EL USUARIO DAO A PARTIR DE LA BASE DE DATOS
    @Provides
    @Singleton
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao {
        // Asumiendo que tu FintrackDatabase tiene el método abstracto: abstract fun usuarioDao(): UsuarioDao
        return database.UsuarioDao()
    }
}