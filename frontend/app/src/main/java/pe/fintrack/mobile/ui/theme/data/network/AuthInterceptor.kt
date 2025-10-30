package pe.fintrack.mobile.ui.theme.data.network

import okhttp3.Interceptor
import okhttp3.Response
import pe.fintrack.mobile.ui.theme.data.TokenManager

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {
        // Obtener la solicitud original
        val originalRequest = chain.request()

        // Obtener el token guardado
        val token = TokenManager.getToken()

        // Si hay token y la URL no es la de login (o registro), añade la cabecera
        val requestBuilder = originalRequest.newBuilder()
        if (token != null && !originalRequest.url.encodedPath.startsWith("/api/auth/")) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        // Construye la nueva solicitud (con o sin cabecera)
        val request = requestBuilder.build()

        // Continúa con la cadena de interceptores y la llamada
        return chain.proceed(request)
    }

}