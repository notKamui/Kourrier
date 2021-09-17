package com.notkamui.kourrier.core

/**
 * Kourrier, a Kotlin/JVM wrapper around the JavaMail API.
 */
object Kourrier

/**
 * POJO that holds the different credentials for a generic connection session.
 *
 * - the [hostname] of the server
 * - a [port]
 * - the [username] of the user which to connect to
 * - the [password] of said user
 *
 * Optionally, debug mode can be enabled with [debugMode] (defaults to false),
 * and SSL can be enabled with [enableSSL] (defaults to true).
 */
data class KourrierConnectionInfo(
    /**
     * Hostname of the server.
     */
    val hostname: String,

    /**
     * Port to open the connection (e.g. 993).
     */
    val port: Int,

    /**
     * Username which to log in with.
     */
    val username: String,

    /**
     * Password of the user.
     */
    val password: String,

    /**
     * Launch connection in debug mode (defaults to false).
     */
    val debugMode: Boolean = false,

    /**
     * Enable SSL on the connection (defaults to true).
     */
    val enableSSL: Boolean = true,
)
