package pe.fintrack.mobile.ui.theme.data.network

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import pe.fintrack.mobile.ui.theme.data.TokenManager

object ReportDownloader{
    private const val REPORT_URL = "https://gestion-finanzas.onrender.com/api/reports/monthly-transactions"

    fun download(context: Context) {
        val token = TokenManager.getToken()
        if (token == null) {
            Toast.makeText(context, "Error: No estás autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileName = "reporte_fintrack_${System.currentTimeMillis()}.pdf"
            val request = DownloadManager.Request(Uri.parse(REPORT_URL))
                .setTitle("Reporte Mensual (FinTrack)")
                .setDescription("Descargando PDF...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .addRequestHeader("Authorization", "Bearer $token") // <-- AÑADE EL TOKEN AQUÍ
                .setMimeType("application/pdf")
                .setAllowedOverMetered(true)
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Iniciando descarga...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Error al iniciar descarga: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



}