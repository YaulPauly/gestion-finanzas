package pe.fintrack.mobile.ui.theme.components

sealed class AppScreen(val route: String, val title: String) {

    object Login: AppScreen("login", "Login")
    object Home : AppScreen("home", "Inicio")
    object Ingreso : AppScreen("lista_ingresos", "Ingresos")

    object RegistrarIngreso: AppScreen("registrar_ingresos", "Registrar Ingreso")

    object Gastos : AppScreen("lista_gastos", "Gastos")
    object RegistrarGastos: AppScreen("registrar_gastos", "Registrar Gastos")

    object EditarIngreso: AppScreen("editar_ingresos/{transactionId}", "Editar Ingreso") {
        fun createRoute(transactionId: Long) = "editar_ingresos/$transactionId"
    }
    object EditarGastos: AppScreen("editar_gastos/{transactionId}", "Editar Gastos") {
        fun createRoute(transactionId: Long) = "editar_gastos/$transactionId"
    }

    object Movimientos : AppScreen("movimientos", "Movimientos")

    // ------------- METAS ---------------
    object ListaMetas : AppScreen("lista_metas", "Metas")
    object CrearMeta : AppScreen("crear_meta", "Crear Meta")
    object ContribuirMeta : AppScreen("contribuir_meta/{goalId}", "Contribuir a Meta"){
        fun createRoute(goalId: Long) = "contribuir_meta/$goalId"
    }
}


