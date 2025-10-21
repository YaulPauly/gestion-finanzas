package pe.fintrack.mobile.ui.theme.components

// ? sealed class es una clase que solo puede ser extendida
// ? dentro del archivo en el que se encuentra
sealed class AppScreen(val route: String){
    object Home: AppScreen("home_screen")

    object RegistrarIngreso: AppScreen("registrar_ingreso_screen")

    object RegistrarGasto: AppScreen("registrar_gasto_screen")
    object ListaIngreso: AppScreen("lista_ingreso_screen")
    object ListaGastos: AppScreen("lista_gastos_screen")
    object ListaMovimientos: AppScreen("lista_movimientos_screen")
}