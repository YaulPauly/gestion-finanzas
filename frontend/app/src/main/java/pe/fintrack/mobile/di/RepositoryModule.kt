package pe.fintrack.mobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.fintrack.mobile.data.local.dao.UsuarioDao
import pe.fintrack.mobile.data.remote.FintrackApiService
import pe.fintrack.mobile.data.repository.HomeRepository
import pe.fintrack.mobile.data.repository.HomeRepositoryImpl
import pe.fintrack.mobile.data.repository.UsuarioRepository
import pe.fintrack.mobile.data.repository.UsuarioRepositoryImpl
import javax.inject.Singleton

// (Dependency Injection)
@Module
@InstallIn(SingletonComponent::class) // Esto asegura que la inyección viva tanto como la aplicación
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeRepository(apiService: FintrackApiService): HomeRepository {
        // Hilt usará esta implementación para satisfacer la dependencia de HomeRepository
        return HomeRepositoryImpl(apiService)
    }

    // 2. Provee la implementación de UsuarioRepository (Debe inyectar la API Service)
    @Provides
    @Singleton
    fun provideUsuarioRepository(apiService: FintrackApiService): UsuarioRepository {
        // Usamos UsuarioRepository (el del login)
        return UsuarioRepositoryImpl(apiService)
    }
}