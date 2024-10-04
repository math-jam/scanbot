package br.com.sankhya.devcenter.scanbot.utils

class ValidadorURL {
    fun validarURL (url: String): Boolean {
        val url = ((url.contains("https://") || url.contains("http://")) && (url.contains(".pdf") || url.contains(".jpg")))
        return url
    }
}