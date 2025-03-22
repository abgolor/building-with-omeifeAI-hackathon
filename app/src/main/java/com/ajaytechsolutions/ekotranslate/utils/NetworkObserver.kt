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

package com.ajaytechsolutions.ekotranslate.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import timber.log.Timber

/**
 * Enum class representing different states of network connectivity.
 * Each state includes descriptive text and an associated color for UI representation.
 *
 * @property statusText Human-readable description of the network status
 * @property statusColor Color used to represent this status in the UI
 */
enum class NetworkStatus(val statusText: String, val statusColor: Color) {
    /**
     * Device has active internet connection with full connectivity to target hosts
     */
    ONLINE("Online", Color(0xFF2E7D32)),

    /**
     * Device has network connectivity but unstable or limited internet access
     */
    WEAK("Weak Connection", Color(0xFFF9A825)),

    /**
     * Device has no network connectivity
     */
    OFFLINE("Offline", Color(0xFFD32F2F))
}

/**
 * Configuration constants for network connectivity checks.
 */
private object NetworkConfig {
    /**
     * Host to check for internet connectivity
     */
    const val HOST = "omeife.ai"

    /**
     * Port used for connectivity checks
     */
    const val PORT = 80

    /**
     * Timeout in milliseconds for connection attempts
     */
    const val TIMEOUT_MS = 1500

    /**
     * Interval in milliseconds between periodic connectivity checks
     */
    const val CHECK_INTERVAL_MS = 5000L
}

/**
 * Observer class that monitors and reports on network connectivity status.
 * Provides real-time updates about the quality and availability of internet connectivity
 * by actively testing connections to remote hosts.
 *
 * @param context Application context used to access system services
 */
open class NetworkObserver(context: Context) {

    private val _networkStatus = MutableStateFlow(NetworkStatus.OFFLINE)

    /**
     * Publicly exposed StateFlow that emits the current network status.
     * UI components can collect this flow to react to network changes.
     */
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Timber.d("Network available callback triggered")
            checkInternetAccess()
        }

        override fun onLost(network: Network) {
            Timber.d("Network lost callback triggered")
            _networkStatus.value = NetworkStatus.OFFLINE
        }
    }

    init {
        registerNetworkCallback()
        startInternetCheckLoop()
    }

    /**
     * Registers for network connectivity callbacks from the system.
     */
    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback)
            Timber.d("Network callback registered successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to register network callback")
        }
    }

    /**
     * Checks actual internet connectivity by attempting to reach a remote host.
     * Updates the network status based on the result.
     */
    private fun checkInternetAccess() {
        coroutineScope.launch {
            try {
                val isReachable = isHostReachable(
                    NetworkConfig.HOST,
                    NetworkConfig.PORT,
                    NetworkConfig.TIMEOUT_MS
                )

                _networkStatus.value = when {
                    isReachable -> NetworkStatus.ONLINE
                    connectivityManager.activeNetwork == null -> NetworkStatus.OFFLINE
                    else -> NetworkStatus.WEAK
                }

                Timber.d("Network status updated: ${_networkStatus.value}")
            } catch (e: Exception) {
                Timber.e(e, "Error checking internet access")
                _networkStatus.value = NetworkStatus.OFFLINE
            }
        }
    }

    /**
     * Starts a periodic loop that checks internet connectivity at regular intervals.
     */
    private fun startInternetCheckLoop() {
        coroutineScope.launch {
            while (isActive) {
                checkInternetAccess()
                delay(NetworkConfig.CHECK_INTERVAL_MS)
            }
        }
    }

    /**
     * Attempts to establish a socket connection to a specified host and port.
     *
     * @param host Hostname or IP address to connect to
     * @param port TCP port to connect to
     * @param timeout Maximum time in milliseconds to wait for connection
     * @return true if connection was successful, false otherwise
     */
    private fun isHostReachable(host: String, port: Int, timeout: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeout)
                true
            }
        } catch (e: IOException) {
            Timber.d(e, "Host unreachable: $host:$port")
            false
        }
    }

    /**
     * Cleans up resources when this observer is no longer needed.
     * Should be called when the application component using this observer is destroyed.
     */
    fun cleanup() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            coroutineScope.cancel()
            Timber.d("NetworkObserver resources cleaned up")
        } catch (e: Exception) {
            Timber.e(e, "Error cleaning up NetworkObserver")
        }
    }
}