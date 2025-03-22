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

package com.ajaytechsolutions.ekotranslate.domain.repository

import com.ajaytechsolutions.ekotranslate.domain.model.TranslationRequest
import com.ajaytechsolutions.ekotranslate.domain.remote.TranslationApiService
import com.ajaytechsolutions.ekotranslate.domain.remote.model.ApiTranslationRequest
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TranslationRepository] that interacts with the remote API service.
 *
 * @param apiService The API service responsible for translation requests.
 */
@Singleton
class TranslationRepositoryImpl @Inject constructor(
    private val apiService: TranslationApiService
) : TranslationRepository {

    override suspend fun translateText(request: TranslationRequest): Result<String> {
        return try {
            // Create API request model
            val apiRequest = ApiTranslationRequest(
                text = request.text,
                sourceLanguage = request.sourceLanguage,
                targetLanguage = request.targetLanguage
            )

            // Execute translation API call
            val response = apiService.translate(apiRequest)

            // Process response
            if (response.status == "success" && response.data != null) {
                Result.success(response.data.translatedText)
            } else {
                Result.failure(Exception("Translation failed: ${response.message ?: "Unknown error"}"))
            }
        } catch (e: CancellationException) {
            throw e // Ensure coroutine cancellations are propagated properly
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
