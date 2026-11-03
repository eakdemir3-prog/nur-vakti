package com.eavisionlabs.nurvakti.account

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Kullanıcının kendi Google Drive'ındaki gizli uygulama klasörüne (appDataFolder) yedekleme.
 * Bu klasörü yalnızca NurVakti görebilir; veriler geliştiricinin sunucusuna gitmez.
 */
object DriveBackup {

    private const val SCOPE = "https://www.googleapis.com/auth/drive.appdata"
    private const val FILE_NAME = "nurvakti-yedek.json"

    /** Yetki sonucu: ya erişim belirteci ya da kullanıcı onayı için açılacak ekran */
    sealed interface Auth {
        data class Token(val value: String) : Auth
        data class NeedsConsent(val sender: IntentSender) : Auth
    }

    suspend fun authorize(context: Context): Auth {
        val request = AuthorizationRequest.builder().setRequestedScopes(listOf(Scope(SCOPE))).build()
        val result: AuthorizationResult = suspendCancellableCoroutine { cont ->
            Identity.getAuthorizationClient(context).authorize(request)
                .addOnSuccessListener { cont.resume(it) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
        val token = result.accessToken
        if (result.hasResolution() || token == null) {
            val pi = result.pendingIntent ?: error("Drive izni alınamadı")
            return Auth.NeedsConsent(pi.intentSender)
        }
        return Auth.Token(token)
    }

    /** Onay ekranından dönen sonuçtan belirteç */
    fun tokenFromResult(activity: Activity, data: android.content.Intent?): String? = runCatching {
        Identity.getAuthorizationClient(activity).getAuthorizationResultFromIntent(data).accessToken
    }.getOrNull()

    private fun open(url: String, token: String, method: String = "GET"): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            if (method == "PATCH") {
                requestMethod = "POST"
                setRequestProperty("X-HTTP-Method-Override", "PATCH")
            } else requestMethod = method
            setRequestProperty("Authorization", "Bearer $token")
            connectTimeout = 20_000
            readTimeout = 30_000
        }

    private fun HttpURLConnection.body(): String {
        val code = responseCode
        val stream = if (code in 200..299) inputStream else errorStream
        val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
        if (code !in 200..299) throw IllegalStateException("Drive hatası ($code)")
        return text
    }

    /** Yedek dosyasının kimliği ve son değişiklik zamanı (yoksa null) */
    suspend fun find(token: String): Pair<String, String>? = withContext(Dispatchers.IO) {
        val q = URLEncoder.encode("name='$FILE_NAME'", "UTF-8")
        val c = open("https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&q=$q&fields=files(id,modifiedTime)", token)
        val arr = JSONObject(c.body()).optJSONArray("files")
        if (arr == null || arr.length() == 0) null
        else arr.getJSONObject(0).let { it.getString("id") to it.optString("modifiedTime") }
    }

    suspend fun upload(token: String, json: String) = withContext(Dispatchers.IO) {
        val existing = find(token)
        if (existing != null) {
            val c = open("https://www.googleapis.com/upload/drive/v3/files/${existing.first}?uploadType=media", token, "PATCH")
            c.doOutput = true
            c.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            c.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            c.body()
        } else {
            val boundary = "nurvakti${System.currentTimeMillis()}"
            val meta = JSONObject().put("name", FILE_NAME).put("parents", org.json.JSONArray().put("appDataFolder")).toString()
            val payload = "--$boundary\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n$meta\r\n" +
                "--$boundary\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n$json\r\n--$boundary--"
            val c = open("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart", token, "POST")
            c.doOutput = true
            c.setRequestProperty("Content-Type", "multipart/related; boundary=$boundary")
            c.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
            c.body()
        }
        Unit
    }

    suspend fun download(token: String): String? = withContext(Dispatchers.IO) {
        val f = find(token) ?: return@withContext null
        open("https://www.googleapis.com/drive/v3/files/${f.first}?alt=media", token).body()
    }

    suspend fun delete(token: String) = withContext(Dispatchers.IO) {
        val f = find(token) ?: return@withContext
        runCatching { open("https://www.googleapis.com/drive/v3/files/${f.first}", token, "DELETE").responseCode }
    }
}
