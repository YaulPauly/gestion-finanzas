package pe.fintrack.mobile.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.fintrack.mobile.R
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinTrackTopBar(
    nombreUsuario: String,

    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
    )

{
    val isNotificationsExpanded = remember { mutableStateOf(false) }

            TopAppBar(

        title = {
            Column {
                Text(
                    text = "Hola",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = nombreUsuario,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

            }
        },
        actions = {
            // Botón para las notificaciones
            IconButton(onClick = onNotificationClick) {Icon(
                imageVector = Icons.Filled.Notifications, // <-- Coma añadida
                contentDescription = stringResource(R.string.descripcion_notificaciones), // <-- Coma añadida
                modifier = Modifier
                    .background(Color.White, shape = CircleShape) // 1. Aplica el fondo blanco y la forma circular
                    .padding(8.dp), // 2. Añade un padding interno para que el icono no toque los bordes del círculo
                tint = Color.Black // 3. (Opcional) Cambia el color del icono para que contraste con el fondo blanco
            )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFE3E3E3),
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true) 
@Composable
fun FinTrackTopBarPreview() {
    // Envuelve el componente en tu tema para ver los estilos correctos
    FintrackMobileTheme {
        FinTrackTopBar(
            nombreUsuario = "John Doe",
            onNotificationClick = {

            }
        )
    }
}