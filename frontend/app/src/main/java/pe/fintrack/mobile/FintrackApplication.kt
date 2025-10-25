package pe.fintrack.mobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FintrackApplication : Application() {
    // No necesitas añadir código dentro de esta clase por ahora.
    // La anotación @HiltAndroidApp se encarga de iniciar la generación de código de Hilt.
}