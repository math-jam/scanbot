package br.com.sankhya.devcenter.scanbot.utils

fun String.toUtf8() = String(this.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
fun String.toIso() = String(this.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)
