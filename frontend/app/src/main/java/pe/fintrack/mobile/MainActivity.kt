package pe.fintrack.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import dagger.hilt.android.AndroidEntryPoint
import pe.fintrack.mobile.ui.navigation.AppNavHost
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme
import pe.fintrack.mobile.ui.theme.screen.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FintrackMobileTheme {
                AppNavHost()
            }
        }
    }
}

