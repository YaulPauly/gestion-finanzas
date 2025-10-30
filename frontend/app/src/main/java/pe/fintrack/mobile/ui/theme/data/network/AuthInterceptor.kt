package pe.fintrack.mobile.ui.theme.data.network

import okhttp3.Interceptor
import okhttp3.Response
import pe.fintrack.mobile.ui.theme.data.TokenManager

class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain) : Response {
        val originalRequest = chain.request()

        // Obtener el token guardado
        val token = TokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()
        if (token != null && !originalRequest.url.encodedPath.startsWith("/api/auth/")) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()

        // Continúa con la cadena de interceptores y la llamada
        return chain.proceed(request)
    }

}