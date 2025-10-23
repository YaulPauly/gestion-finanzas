package pe.fintrack.mobile.ui.theme.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.fintrack.mobile.R
import java.text.DecimalFormat

@Composable
fun SaldoActualComponent(
    saldoActual: Double,
    onRegistrarGastoClick: () -> Unit,
    onRegistrarIngresoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val formatter = remember { DecimalFormat("S/ #,##0.00") }

    val formattedBalance = remember(saldoActual) {
        formatter.format(saldoActual)
    }

    Surface(modifier = modifier, color= Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.saldo_actual),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White

                )
                Text(
                    text = formattedBalance,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
            Box {
                // Botón con ícono de "más opciones"
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.mas_opciones),
                        tint = Color.White
                    )
                }

                // Menú desplegable
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.registrar_gasto)) },
                        onClick = {
                            isMenuExpanded = false
                            onRegistrarGastoClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.registrar_ingreso)) },
                        onClick = {
                            isMenuExpanded = false
                            onRegistrarIngresoClick()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaldoActualPreview() {
    SaldoActualComponent(
        saldoActual = 5432.50,
        onRegistrarGastoClick = { /* Acción de prueba */ },
        onRegistrarIngresoClick = { /* Acción de prueba */ }
    )
}

