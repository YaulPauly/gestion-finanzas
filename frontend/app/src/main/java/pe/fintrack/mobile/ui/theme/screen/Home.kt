package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fintrack.mobile.ui.theme.components.SaldoActualComponent
import java.util.Locale

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF0F0F0)) // Un color de fondo suave para toda la pantalla
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF4A55A2)) // Un color sólido es más limpio que el degradado
        ) {
            SaldoActualComponent(
                saldoActual = 4500.50,
                onRegistrarGastoClick = { /* TODO: Navegar a registrar gasto */ },
                onRegistrarIngresoClick = { /* TODO: Navegar a registrar ingreso */ }
            )
        }

        // --- RESUMEN MENSUAL ---
        Text(
            text = "Resumen Mensual",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre las tarjetas
        ) {
            // ✅ CORRECCIÓN: Se pasa el monto como Double
            ResumenCard(
                titulo = "Ingresos",
                monto = 4500.00,
                colorFondo = Color(0xFF4A55A2),
                colorTexto = Color.White,
                esIngreso = true,
                modifier = Modifier.weight(1f)
            )
            // ✅ CORRECCIÓN: Se pasa el monto como Double
            ResumenCard(
                titulo = "Gastos",
                monto = 450.00,
                colorFondo = Color.White,
                colorTexto = Color.Black,
                esIngreso = false,
                modifier = Modifier.weight(1f)
            )
        }

        // --- MOVIMIENTOS RECIENTES ---
        Text(
            text = "Movimientos recientes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                MovimientoItem(
                    categoria = "Trabajo",
                    fecha = "24 Ago 2025 13:54 PM",
                    monto = "+ S/. 1,500.00",
                    esIngreso = true
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                MovimientoItem(
                    categoria = "Supermercado",
                    fecha = "23 Ago 2025 10:30 AM",
                    monto = "- S/. 120.50",
                    esIngreso = false
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                MovimientoItem(
                    categoria = "Freelance",
                    fecha = "22 Ago 2025 08:00 PM",
                    monto = "+ S/. 800.00",
                    esIngreso = true
                )
            }
        }
    }
}

@Composable
fun ResumenCard(
    titulo: String,
    monto: Double, // Se recibe como Double
    colorFondo: Color,
    colorTexto: Color,
    esIngreso: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = titulo, color = colorTexto, style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (esIngreso) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = titulo,
                    tint = colorTexto
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // ✅ CORRECCIÓN: Se formatea el Double a un String con formato de moneda.
            Text(
                text = "S/. ${String.format(Locale.US, "%,.2f", monto)}",
                color = colorTexto,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MovimientoItem(
    categoria: String,
    fecha: String,
    monto: String,
    esIngreso: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.LightGray, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = categoria, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = fecha, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(
            text = monto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (esIngreso) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}

// --- Otras Pantallas (Placeholder) ---

@Composable
fun IngresoScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Ingresos!")
}

@Composable
fun GastosScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Gastos!")
}

@Composable
fun MovimientosScreen(modifier: Modifier = Modifier) {
    Text(text = "Bienvenido a la pantalla de Movimientos!")
}