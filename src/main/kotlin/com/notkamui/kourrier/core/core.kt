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
 * and SSL can be enabled with [enableSSL] (defaults to false).
 */
data class KourrierConnectionInfo(
    val hostname: String,
    val port: Int,
    val username: String,
    val password: String,
    val debugMode: Boolean = false,
    val enableSSL: Boolean = false,
)
