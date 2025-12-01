package com.comixa.app.network

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HttpHelper {

    fun authedGet(url: String, token: String): Pair<Int, String> {
        return get(url, token)
    }

    fun get(urlString: String, token: String? = null): Pair<Int, String> {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            if (!token.isNullOrEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("token", token)
            }

            val code = conn.responseCode
            val stream: InputStream =
                if (code in 200..299) conn.inputStream else conn.errorStream

            val sb = StringBuilder()
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { br ->
                br.forEachLine { sb.append(it) }
            }

            Pair(code, sb.toString())
        } catch (e: Exception) {
            Pair(-1, e.message ?: "Unknown error")
        } finally {
            conn?.disconnect()
        }
    }

    fun postForm(
        urlString: String,
        params: Map<String, String>,
        token: String? = null
    ): Pair<Int, String> {
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=utf-8"
            )
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            if (!token.isNullOrEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer $token")
                conn.setRequestProperty("token", token)
            }

            val body = params.entries.joinToString("&") { (k, v) ->
                URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
            }

            conn.outputStream.use { os ->
                os.write(body.toByteArray(StandardCharsets.UTF_8))
            }

            val code = conn.responseCode
            val stream: InputStream =
                if (code in 200..299) conn.inputStream else conn.errorStream

            val sb = StringBuilder()
            BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { br ->
                br.forEachLine { sb.append(it) }
            }

            Pair(code, sb.toString())
        } catch (e: Exception) {
            Pair(-1, e.message ?: "Unknown error")
        } finally {
            conn?.disconnect()
        }
    }
}
