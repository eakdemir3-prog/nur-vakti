package com.eavisionlabs.nurvakti.account

import android.app.Activity
import android.content.Context
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.eavisionlabs.nurvakti.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import org.json.JSONObject

/** Google ile giriş (Credential Manager). Hesap bilgisi yalnızca cihazda saklanır. */
object GoogleAccount {

    data class Info(val id: String, val email: String, val name: String?)

    class Cancelled : Exception()

    val isConfigured: Boolean get() = BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

    suspend fun signIn(activity: Activity): Info {
        val option = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val result = try {
            CredentialManager.create(activity).getCredential(activity, request)
        } catch (e: GetCredentialCancellationException) {
            throw Cancelled()
        }
        val cred = result.credential
        require(cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Beklenmeyen giriş türü"
        }
        val g = GoogleIdTokenCredential.createFrom(cred.data)
        val sub = subjectOf(g.idToken) ?: g.id
        return Info(id = sub, email = g.id, name = g.displayName)
    }

    suspend fun signOut(context: Context) {
        runCatching { CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest()) }
    }

    /** Kimlik belirtecindeki değişmez Google hesap kimliği ("sub") */
    private fun subjectOf(idToken: String): String? = runCatching {
        val payload = idToken.split('.')[1]
        val json = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING), Charsets.UTF_8)
        JSONObject(json).getString("sub")
    }.getOrNull()
}
