package br.com.sankhya.devcenter.scanbot.utils

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Image {
    fun generateByteArray(url: String): ByteArray {
        val imageUrl: URL = URL(url)
        val connection: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val inputStream: InputStream = connection.inputStream
        val byteArray = inputStream.use { it.readBytes() }

        return byteArray
    }
}
