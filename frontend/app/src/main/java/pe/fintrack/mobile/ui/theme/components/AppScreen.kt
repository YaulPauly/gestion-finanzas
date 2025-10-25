package pe.fintrack.mobile.ui.theme.components

sealed class AppScreen(val route: String, val title: String) {

    object Login : AppScreen("login_screen", "Iniciar Sesión")
    object MainContent : AppScreen("main_content", "Contenido Principal")

    object Home : AppScreen("home", "Inicio")
    object Ingreso : AppScreen("lista_ingresos", "Ingresos")

    object RegistrarIngreso: AppScreen("registrar_ingresos", "Registrar Ingreso")
    object EditarIngreso: AppScreen("editar_ingresos", "Editar Ingreso")


    object RegistrarGastos: AppScreen("registrar_gastos", "Registrar Gastos")
    object EditarGastos: AppScreen("editar_gastos", "Editar Gastos")

    object Gastos : AppScreen("lista_gastos", "Gastos")
    object Movimientos : AppScreen("movimientos", "Movimientos")
}