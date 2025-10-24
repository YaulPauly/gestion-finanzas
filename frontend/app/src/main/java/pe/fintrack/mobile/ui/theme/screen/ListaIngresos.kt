package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource


@Composable
fun ListaIngresosScreen(modifier: Modifier = Modifier){
    Column (modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF0F0F0))){
        Row {
            Text(
                text = "Ingresos",
                style = MaterialTheme.typography.titleLarge
                )
        }
    }
}