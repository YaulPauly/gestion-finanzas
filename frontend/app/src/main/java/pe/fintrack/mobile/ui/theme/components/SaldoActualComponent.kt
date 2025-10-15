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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.fintrack.mobile.R
import java.text.DecimalFormat

@Composable
fun CurrentBalanceCard(
    currentBalance: Double,
    onRegisterExpenseClick: () -> Unit,
    onRegisterIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Formateador para la moneda. Se usa 'remember' para que no se recree en cada recomposición.
    val formatter = remember { DecimalFormat("S/ #,##0.00") }

    // Formatea el saldo y recuerda el resultado. Solo se recalcula si 'currentBalance' cambia.
    val formattedBalance = remember(currentBalance) {
        formatter.format(currentBalance)
    }

    Surface(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.current_balance),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formattedBalance,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Box {
                // Botón con ícono de "más opciones"
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.more_options)
                    )
                }

                // Menú desplegable
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.register_expense)) },
                        onClick = {
                            isMenuExpanded = false
                            onRegisterExpenseClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.register_income)) },
                        onClick = {
                            isMenuExpanded = false
                            onRegisterIncomeClick()
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentBalanceCardPreview() {
    // FintrackMobileTheme { // Envuelve con tu tema si es necesario
    CurrentBalanceCard(
        currentBalance = 5432.50,
        onRegisterExpenseClick = { /* Acción de prueba */ },
        onRegisterIncomeClick = { /* Acción de prueba */ }
    )
    // }
}