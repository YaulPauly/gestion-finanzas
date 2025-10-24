package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RegistrarGastosScreen(navController : NavController, modifier: Modifier = Modifier){
    Column {
        Text(
            text = "Registrar Gastos"
        )
    }
}