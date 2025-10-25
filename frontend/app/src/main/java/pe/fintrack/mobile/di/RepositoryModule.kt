package pe.fintrack.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.repository.HomeRepository
import pe.fintrack.mobile.data.repository.HomeRepositoryImpl
import pe.fintrack.mobile.data.repository.UsuarioRepository
import javax.inject.Singleton

// (Dependency Injection)
@Module
@InstallIn(SingletonComponent::class) // Esto asegura que la inyección viva tanto como la aplicación
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository {
        // Hilt usará esta implementación para satisfacer la dependencia de HomeRepository
        return HomeRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUsuarioRepository(usuarioDao: UsuarioDao): UsuarioRepository {
     return UsuarioRepository(usuarioDao)
    }
}