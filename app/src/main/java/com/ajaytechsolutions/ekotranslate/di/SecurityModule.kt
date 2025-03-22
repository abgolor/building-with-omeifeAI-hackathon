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

package com.ajaytechsolutions.ekotranslate.di

import com.ajaytechsolutions.ekotranslate.security.SecureKeyManager
import com.ajaytechsolutions.ekotranslate.security.SecureKeyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides security-related dependencies.
 *
 * This module provides bindings for the secure key management functionality
 * and is installed in the [SingletonComponent] to ensure single instances
 * throughout the application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    /**
     * Provides the implementation for the [SecureKeyRepository] interface.
     *
     * @param secureKeyManager The implementation that will be bound to the interface
     * @return A singleton instance of [SecureKeyRepository]
     */
    @Binds
    @Singleton
    abstract fun bindSecureKeyRepository(
        secureKeyManager: SecureKeyManager
    ): SecureKeyRepository
}