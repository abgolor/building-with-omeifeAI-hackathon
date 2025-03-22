/**
 * MIT License
 *
 * Copyright (c) 2025 Eko Translate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ajaytechsolutions.ekotranslate.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ajaytechsolutions.ekotranslate.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Manages secure storage and retrieval of sensitive API keys.
 *
 * This class provides a secure way to store and retrieve API keys using Android's
 * [EncryptedSharedPreferences] backed by the Android Keystore system.
 * It follows the Repository pattern and is designed to be injected where needed.
 */
@Singleton
class SecureKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
): SecureKeyRepository {
    companion object {
        private const val PREFS_NAME = "encrypted_credentials"
        private const val API_KEY_NAME = "translation_api_key"
    }

    /**
     * The encrypted shared preferences instance backed by Android Keystore.
     */
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Initializes the API key if it hasn't been stored yet.
     *
     * This method should be called once during application initialization.
     * It will only store the key if it doesn't exist already, preventing
     * overwriting of existing credentials.
     */
    override fun initializeApiKeyIfNeeded() {
        if (!isApiKeyStored()) {
            try {
                saveApiKey(BuildConfig.API_KEY)
                Timber.d("API key initialized successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize API key")
            }
        }
    }

    /**
     * Checks if an API key is currently stored.
     *
     * @return true if the API key exists in secure storage, false otherwise
     */
    override fun isApiKeyStored(): Boolean {
        return encryptedPrefs.contains(API_KEY_NAME)
    }

    /**
     * Saves the API key to secure storage.
     *
     * @param apiKey The API key to be securely stored
     */
    override fun saveApiKey(apiKey: String) {
        encryptedPrefs.edit { putString(API_KEY_NAME, apiKey) }
    }

    /**
     * Retrieves the API key from secure storage.
     *
     * @return The stored API key, or null if no key is stored
     */
    override fun getApiKey(): String? {
        val apiKey = encryptedPrefs.getString(API_KEY_NAME, null)
        Timber.d("Retrieved API key: ${apiKey?.take(3)}***") // Only log first few chars for security
        return encryptedPrefs.getString(API_KEY_NAME, null)
    }

    /**
     * Clears the stored API key from secure storage.
     *
     * This should be used during user logout or when refreshing credentials.
     */
    override fun clearApiKey() {
        encryptedPrefs.edit { remove(API_KEY_NAME) }
    }
}