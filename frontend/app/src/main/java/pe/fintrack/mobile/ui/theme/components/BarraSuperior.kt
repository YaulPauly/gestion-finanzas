package pe.fintrack.mobile.ui.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
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
            IconButton(onClick = onNotificationClick) {
                Surface {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.descripcion_notificaciones)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // Usamos colores del tema para que se adapte a modo claro/oscuro
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true) // showBackground = true añade un fondo blanco
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