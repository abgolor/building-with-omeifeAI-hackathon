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

package com.ajaytechsolutions.ekotranslate.domain.remote

import com.ajaytechsolutions.ekotranslate.domain.remote.model.ApiTranslationRequest
import com.ajaytechsolutions.ekotranslate.domain.remote.model.ApiTranslationResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit service interface for the OmeifeAI Translation API.
 * Defines the available API endpoints and their request/response types.
 */
interface TranslationApiService {

    /**
     * Sends a translation request to the OmeifeAI API.
     *
     * @param request The translation request containing text and language information
     * @return The API response with translation results
     */
    @POST("translate")
    suspend fun translate(@Body request: ApiTranslationRequest): ApiTranslationResponse
}