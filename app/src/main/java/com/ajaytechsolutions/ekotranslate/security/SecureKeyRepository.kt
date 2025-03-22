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

/**
 * Repository interface for secure key management operations.
 *
 * This interface defines the contract for secure key storage operations.
 * It follows the Repository pattern to abstract the data source and provide
 * a clean API for the rest of the application.
 */
interface SecureKeyRepository {
    
    /**
     * Initializes the API key if it hasn't been stored yet.
     *
     * This method should be called once during application initialization.
     */
    fun initializeApiKeyIfNeeded()
    
    /**
     * Checks if an API key is currently stored.
     *
     * @return true if the API key exists in secure storage, false otherwise
     */
    fun isApiKeyStored(): Boolean
    
    /**
     * Saves the API key to secure storage.
     *
     * @param apiKey The API key to be securely stored
     */
    fun saveApiKey(apiKey: String)
    
    /**
     * Retrieves the API key from secure storage.
     *
     * @return The stored API key, or null if no key is stored
     */
    fun getApiKey(): String?
    
    /**
     * Clears the stored API key from secure storage.
     */
    fun clearApiKey()
}