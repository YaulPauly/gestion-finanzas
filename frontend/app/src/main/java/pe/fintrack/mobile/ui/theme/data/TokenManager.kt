package pe.fintrack.mobile.ui.theme.data
import android.content.Context
import android.content.SharedPreferences
import pe.fintrack.mobile.ui.theme.data.model.LoginResponse

object TokenManager {

    private const val PREFS_NAME = "fintrack_prefs"
    private const val TOKEN_KEY = "auth_token"

    private const val USER_NAME_KEY = "user_name"

    private const val USER_ID_KEY = "user_id"
    private var prefs: SharedPreferences? = null

    // Debe llamarse una vez al iniciar la app (ej. en MainActivity o Application)
    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(loginData: LoginResponse) {
        prefs?.edit()?.apply {
            putString(TOKEN_KEY, loginData.token)
            putString(USER_NAME_KEY, loginData.name)
            putInt(USER_ID_KEY, loginData.userId)
            apply()
        }
    }

    fun clearSession() {
        prefs?.edit()?.apply {
            remove(TOKEN_KEY)
            remove(USER_NAME_KEY)
            remove(USER_ID_KEY)
            apply()
        }
    }

    fun getUserName(): String? {
        return prefs?.getString(USER_NAME_KEY, "Usuario")
    }

    fun getUserId(): Int? {
        val id = prefs?.getInt(USER_ID_KEY, -1)
        return if (id == -1) null else id
    }

    // Guarda el token
    fun saveToken(token: String?) { // Permitir guardar null para logout
        prefs?.edit()?.putString(TOKEN_KEY, token)?.apply()
    }

    // Obtiene el token guardado
    fun getToken(): String? {
        return prefs?.getString(TOKEN_KEY, null)
    }

    // Limpia el token (para logout)
    fun clearToken() {
        prefs?.edit()?.remove(TOKEN_KEY)?.apply()
    }

    // Verifica si hay un token guardado
    fun hasToken(): Boolean {
        return getToken() != null
    }
}