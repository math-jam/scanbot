package br.com.sankhya.devcenter.scanbot.services

import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteController
import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteControllerSession
import br.com.sankhya.devcenter.sankhyaw.services.ejb2.annotations.EJB2TransactionType
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.imageio.ImageIO

@RemoteController(serviceName = "OCRServiceSP", transactionType = EJB2TransactionType.Supports)
class GoogleVisionService {
    val privateKeyPEM = """
        -----BEGIN PRIVATE KEY-----
        MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDCDrkNck3xbRfw
        8Kp20EiPG8M6IfOhuIP2MFsem9oOZj7l1JNL6qegcAYysOyIIEjLGBhpN4Y9tOvX
        iPtTLwlRR2ArL/n307RiMTNU5RG7VMYXLlDV4xL388TU7Yh2HoleQrshL91c2Arl
        5mSRsr0TUXk/eaVXm+QNj3i+x+goBbpb1jmp4WIkaCRMeJ9UEfV8XAwgQv6wU0B7
        L7xrBC5zK0/qrxBuSJ3pA+v/3OvYr5oIUgDIDWMvo0BCvFQFl2DzyYdH8cxAi1mD
        6x3uVxkBvRg1l+XsF6KFAKT2pi8So0/O8vxSjvNqQsB3otRbxydue4DzEPYVgks8
        K4qHKMaRAgMBAAECggEAC2Th6LA1c198cGExIRjJqd1vcRJdQ7mZxfLA5uMvwniu
        aMA6hdKZVQo+0usgeD2tCH+5rzPbZfQhhN4+I3w3h9YxB1ghzTNY/Ar3gJySlfU8
        TFAj3useSgC+JeJsiMLyPd6dLi2pSbJFsYn9jbB4+ZFnFRsHYkV89m7eIBWMxAy3
        ulPofEN3cclHfY2wmClQfqyMIdBNEX4O0tLI9cyOTk4i0hg03gLk5epC20tL2rGB
        0Ivak0gnHs+AqF1mqDQ4tgBblnCU2Dg1kGEQLr90+Cs2flXmhuZJXjvPal/K5IKr
        DWIwhHBRFZ2bZMxjoDxIaBtUxdJm/udM5eQY3HhqAQKBgQD3yZZZW+qhRKfaKy95
        oSIQ6t9BHfjZlGuEMcCJ64ATDedtZGg/oWXg5OIdkF0acWUxv2zxoPuRMc5lIhZO
        bJEeeKAqkSwqGdDwTbDmrralrMx+kmI4D/S35fISuiFEDQ6S/PTUDv+DVLPpHaPN
        6BHUM04ID8vgqMUJ7hcKhvg0WQKBgQDIfUA4pHQcmXMINjrmgdhuaky0w7l0Sohy
        P79Pu0paijmym+V809ILu0XyQdnSEccuIrrOR1qTQgw+IU9Vlzx8WH7x0RsaYXgH
        xo00mZjAh30ySHBuvPJ3XC8xfgkXcUCs2aCgGw5FpNA82K4ZTe0qfXT/dsA9QTXR
        MUYUdCE8+QKBgBNIULbrfDrIiPdlXPBuJaSDYpRCrNbhwewrovwYo4LYFiZhJeut
        t6vko+QBE/22zVUtvGChUnJeGd2s5Wd8814XrP8jBPjRwXyxzw5kkjoaVF+VLKeI
        NlcJQelLKsOM8JFntfW6pQ+nS8jcUZ74bErDI1NlR0xYVd0L9icPkckhAoGAI4kP
        uPptGiSYnvv9tJ2PHwL4VZHbbSFIzfc+kYNAyot1bpLzOgzRTgMDgszpLrK3+xVs
        xuMeyrsKMHj29tS3g9KrrXDCvQpJnBc1L/2JdZaOOjtcskQLNdzBAKS2N9F7BSgN
        sFMbVHzB7Ab71MBbKQzTj/QcXr8QKuPw5+8tdQECgYEAwoPHkQOGmNoiXOUmnoxk
        6+SB4clalElGvdtph/UtyfWsTunJtGuLytUqXdYZ2W2UlQUQ04K33e8/ZL6rR/1w
        ii1a8t5fj2Wr9J2Xb9/mcslEBcKkgPUBOi6V6QV7sn9MekMlH+I1+tC9ulbIiafc
        ZCwxSQZw+wt56zF9PcdgKzI=
        -----END PRIVATE KEY-----
    """.trimIndent()
    val clientEmail = "scanbotapiteste@projecttest-bd7d2.iam.gserviceaccount.com"
    val tokenUri = "https://oauth2.googleapis.com/token"

    fun getAccessToken(): String {
        val privateKeyPEMFormatted = privateKeyPEM
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")
            .trim()

        val keyBytes = try {
            Base64.getDecoder().decode(privateKeyPEMFormatted)
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Failed to decode Base64 encoded private key", e)
        }

        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(keySpec) as RSAPrivateKey

        val algorithm = Algorithm.RSA256(null, privateKey)
        val jwtToken = JWT.create()
            .withIssuer(clientEmail)
            .withAudience(tokenUri)
            .withClaim("scope", "https://www.googleapis.com/auth/cloud-platform")
            .withExpiresAt(Date(System.currentTimeMillis() + 3600 * 1000))
            .withIssuedAt(Date(System.currentTimeMillis()))
            .sign(algorithm)

        val url = URL(tokenUri)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")

        val requestBody = """
            {
              "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
              "assertion": "$jwtToken"
            }
        """.trimIndent()

        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
            os.flush()
        }

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val responseMap = Gson().fromJson(response, Map::class.java)
            return responseMap["access_token"].toString()
        } else {
            throw RuntimeException("Failed to obtain access token. HTTP response code: ${connection.responseCode}")
        }
    }

    @RemoteControllerSession(EJB2TransactionType.Required)
    fun processUrl(url: String, typeDoc: String): Any {
        val accessToken = getAccessToken()
        return if (url.endsWith(".pdf", true)) {
            val imagePaths = pdfToImages(url, "src/imgs")
            val annotations = imagePaths.flatMap { extractAnnotations(processImageFile(it, accessToken)) }
            extractFieldsPdf(annotations.joinToString("\n") { it["description"].toString() }, typeDoc)
        } else {
            val annotations = extractAnnotations(processImageUrl(url, accessToken))
            extractFieldsImg(annotations.joinToString("\n") { it["description"].toString() }, typeDoc)
        }
    }

    fun extractAnnotations(responseJson: String): List<Map<String, Any>> {
        val responseMap = Gson().fromJson(responseJson, Map::class.java)
        val responses = responseMap["responses"] as List<Map<String, Any>>
        return responses.flatMap { it["textAnnotations"] as List<Map<String, Any>> }
    }

    fun processImageUrl(imageUrl: String, auth: String): String {
        val apiUrl = "https://vision.googleapis.com/v1/images:annotate"
        val requestBody = """
            {
              "requests": [
                {
                  "image": {
                    "source": {
                      "imageUri": "$imageUrl"
                    }
                  },
                  "features": [
                    {
                      "type": "TEXT_DETECTION"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $auth")

        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
            os.flush()
        }

        return if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            throw RuntimeException("Failed to process image. HTTP response code: ${connection.responseCode}")
        }
    }

    fun processImageFile(imagePath: String, auth: String): String {
        val apiUrl = "https://vision.googleapis.com/v1/images:annotate"
        val imageBytes = File(imagePath).readBytes()
        val base64Image = Base64.getEncoder().encodeToString(imageBytes)

        val requestBody = """
            {
              "requests": [
                {
                  "image": {
                    "content": "$base64Image"
                  },
                  "features": [
                    {
                      "type": "TEXT_DETECTION"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true

        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $auth")

        connection.outputStream.use { os ->
            os.write(requestBody.toByteArray(Charsets.UTF_8))
            os.flush()
        }

        return if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            throw RuntimeException("Failed to process image. HTTP response code: ${connection.responseCode}")
        }
    }


    fun pdfToImages(pdfUrl: String, outputDir: String): List<String> {
        val imagePaths = mutableListOf<String>()
        try {
            val outputDirectory = File(outputDir)
            if (outputDirectory.exists()) {
                outputDirectory.listFiles()?.forEach { it.delete() }
            } else {
                outputDirectory.mkdirs()
            }

            val url = URL(pdfUrl)
            val pdfFile = File.createTempFile("tempPdf", ".pdf")
            url.openStream().use { input ->
                pdfFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val document = PDDocument.load(pdfFile)
            val pages = document.documentCatalog.allPages

            for ((pageIndex, page) in pages.withIndex()) {
                val pdPage = page as PDPage
                val image = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300)
                val outputfile = File("$outputDir/page_$pageIndex.jpg")
                ImageIO.write(image, "jpg", outputfile)
                imagePaths.add(outputfile.absolutePath)
            }

            document.close()
            pdfFile.delete()

            println("Conversão concluída. Imagens salvas em: $outputDir")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imagePaths
    }

    private fun extractFieldsPdf(description: String, typeDoc: String): Map<String, String> {
        var descriptionNoSpace = description.replace(" ", "")

        if (typeDoc == "CNH") {
            val namePattern = Regex("""2 e 1 NOME E SOBRENOME\n(.*)\n""")
            val cnhPattern = Regex("""\n(\d{11})\n""")
            val cpfPattern = Regex("""\n(\d{3}\.\d{3}\.\d{3}-\d{2})\n""")
            val dobPattern = Regex("""NASCIMENTO\n(\d{2}/\d{2}/\d{4})\n""")
            val identidadePatttern = Regex("""[A-Z]{2}(\d{8})""")
            val validadePattern = Regex("""VALIDADE\n(\d{2}/\d{2}/\d{4})""")
            val dataEmissaoPattern = Regex("""DATA EMISSÃO\s+(\d{2}/\d{2}/\d{4})""")
            val categoriaPattern = Regex("""CAT HAB\n\d{11}\n([A-Z])\n""")
            val codigoSegurancaPattern = Regex("""\n(.*?)\n2 e 1. Nome""")
            val primeiraHabPattern = Regex("""1* HABILITAÇÃO\n(\d{2}/\d{2}/\d{4})\n""")
            val paiPattern = Regex("""\n(.*)\nformation""")
            val maePattern = Regex("""PORTADOR\n\d{1}\n(.*)\n""")
            val emissorPattern = Regex("UF\\n[A-Z0-9]+\\s(.*)\\s[A-Z]{2}\\n")
            val regexPaiMae = Regex("""FIILIAÇÃO\n([A-Z\s]+)\n([A-Z\s]+)\n""")
            val ufPattern = Regex("""/UF\n(.*)\n""")
            val ultimaEmissaoPattern = Regex("""DATA EMISSÃO\n(\d{2}/\d{2}/\d{4})\n""")

            var name = namePattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val cnh = cnhPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val cpf = cpfPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val dob = dobPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val identidade = identidadePatttern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var validade = validadePattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val dataEmissao = dataEmissaoPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var categoria = categoriaPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var codSeg = codigoSegurancaPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val primeiraHab = primeiraHabPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var pai = paiPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var mae = maePattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var emissor = emissorPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            var uf = ufPattern.find(description)?.groups?.get(1)?.value?.takeLast(2) ?: "Not Found"
            val ultimaEmissao = ultimaEmissaoPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"

            if(name == "Not Found") {
                val regexNome = Regex("""ACIONAL DE HABILITAÇÃO\n(.*)\n""")
                name = regexNome.find(description)?.groups?.get(1)?.value ?: "Not Found"
            }

            if(codSeg == "Not Found") {
                var segRegex = Regex("""\n(.*)\nCONTRAN""")
                codSeg = segRegex.find(description)?.groups?.get(1)?.value ?: "Not Found"
            }

            val regexEmissor = Regex("CPF\\n([A-Z]+)\\s([A-Z]{2})\\n")
            val matchEmissor = regexEmissor.find(description)
            if (matchEmissor != null) {
                emissor = matchEmissor.groupValues[1]
                uf = matchEmissor.groupValues[2]
            }

            if(validade == "Not Found") {
                val regexValidade = Regex("""VALIDADE\nACC\n(\d{2}/\d{2}/\d{4})\n""")
                validade = regexValidade.find(description)?.groups?.get(1)?.value ?: "Not Found"
            }

            if(categoria == "Not Found") {
                var categoPattern = Regex("""CAT HAB\n(.*)\n""")
                categoria = categoPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"

                if (categoria == "Not Found") {
                    categoPattern = Regex("""CAT. HAB.\n(.*)\n""")
                    categoria = categoPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
                }
            }

            if (emissor == "Not Found") {
                val regexEmissor = Regex("CPF\\n([A-Z]+)\\s([A-Z]{2})\\n")
                emissor = regexEmissor.find(description)?.groups?.get(1)?.value ?: "Not Found"
            }

            if (mae == "Not Found" || mae.length < 10) {
                var patternMae = Regex("""PORTADOR\n(.*)\n""")
                mae = patternMae.find(description)?.groups?.get(1)?.value ?: "Not Found"

                if(mae == "Not Found" || mae.length < 10) {
                    patternMae = Regex("""\n(.*)\nPERMISSÃO""")
                    mae = patternMae.find(description)?.groups?.get(1)?.value ?: "Not Found"

                    if(mae == "Not Found" || mae.length < 10) {
                        val matchPaiMae = regexPaiMae.find(description)
                        if (matchPaiMae != null) {
                            mae = matchPaiMae.groupValues[2]
                            if(mae.length < 10) {
                                mae = "Not Found"
                            }
                        }
                    }
                }
            }

            if (!validateInformation(pai)) {
                var patternPai = Regex("""HAB\n.*\n(.*)\n""")
                pai = patternPai.find(description)?.groups?.get(1)?.value ?: "Not Found"

                if(!validateInformation(pai)) {
                    patternPai = Regex("""\n(.*)\nN°""")
                    pai = patternPai.find(description)?.groups?.get(1)?.value ?: "Not Found"

                    if (!validateInformation(pai)) {
                        patternPai = Regex("""FIILIAÇÃO\n(.*)\n""")
                        pai = patternPai.find(description)?.groups?.get(1)?.value ?: "Not Found"
                    }

                    if (!validateInformation(pai)) {
                        val matchPaiMae = regexPaiMae.find(description)
                        if (matchPaiMae != null) {
                            pai = matchPaiMae.groupValues[1]
                            if(pai.length < 10) {
                                pai = "Not Found"
                            }
                        }
                    }
                }
            }

            return mapOf(
                "nome" to name,
                "cnh" to cnh,
                "cpf" to cpf.replace(".", "").replace("-", ""),
                "dtnasc" to dob,
                "rg" to identidade,
                "validade" to validade,
                "dataEmissao" to dataEmissao,
                "categoria" to categoria,
                "codSeguranca" to codSeg,
                "primeiraHab" to primeiraHab,
                "pai" to pai,
                "mae" to mae,
                "emissor" to emissor,
                "ultimaEmissao" to ultimaEmissao,
                "ufexped" to uf,
                "descricao" to description
            )

        } else if (typeDoc == "RENAVAM") {
            var cidade = "Not Found"
            var uf = "Not Found"

            val renavamPattern = Regex("""\n(\d{11})\n""")
            val placaPattern = Regex("""\n([A-Z]{3}[0-9][A-Z0-9][0-9]{2})\n""")
            val cpfCnpjPattern = Regex("""\n(\d{3}\.\d{3}\.\d{3}-\d{2})\n""")
            var chassiPattern = Regex("""\n([A-Za-z0-9]{17,19})\n""")
            val ufCityPattern = Regex("""\*\*\*\n(.*?)\n""")
            val marcaModeloPattern = Regex("""\n(.*?)\nPASSAGEI""")
            val especiePattern = Regex("""\n(.*?)\n\*\*\*\*\*\*""")
            val corPattern = Regex("""-\d{2}\n\*\n([A-Z]+)\n""")
            val anoModeloPattern = Regex("""\n\d{4}\n(\d{4})\nNão""")
            val anoFabricacaoPattern = Regex("""\n(\d{4})\n\d{4}\nNão""")
            val combustivelPattern = Regex("""\n(.*?)\n\*\n\*\n\*""")
            val pototenciaCilindradaPattern = Regex(""".br\n(.*?)\n""")

            val renavam = renavamPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val placa = placaPattern.find(descriptionNoSpace)?.groups?.get(1)?.value ?: "Not Found"
            val cpfCnpj = cpfCnpjPattern.find(descriptionNoSpace)?.groups?.get(1)?.value ?: "Not Found"
            val chassi = chassiPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val marcaModelo = marcaModeloPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val especie = especiePattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val combustivel = combustivelPattern.find(description)?.groups?.get(1)?.value?.replace(" ", "") ?: "Not Found"
            val potenciaCilindrada = pototenciaCilindradaPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val cor = corPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val anoModelo = anoModeloPattern.find(descriptionNoSpace)?.groups?.get(1)?.value ?: "Not Found"
            val anoFabricacao = anoFabricacaoPattern.find(descriptionNoSpace)?.groups?.get(1)?.value ?: "Not Found"

            val descriptionNoSpace = description.replace(" ", "")
            val cidadeUf = ufCityPattern.find(descriptionNoSpace)?.groups?.get(1)?.value ?: "Not Found"

            if(cidadeUf != "Not Found") {
                uf = cidadeUf.dropLast(2)
                cidade = cidadeUf.takeLast(2)
            }

            return mapOf(
                "renavam" to renavam,
                "placa" to placa,
                "cpfcnpj" to cpfCnpj.replace(" ", "").replace(".", "").replace("-", ""),
                "chassi" to chassi.replace(" ", ""),
                "marcaModelo" to marcaModelo,
                "especie" to especie,
                "combustivel" to combustivel,
                "potenciaCilindrada" to potenciaCilindrada,
                "cor" to cor,
                "anoModelo" to anoModelo,
                "anoFabricacao" to anoFabricacao,
                "cidade" to cidade,
                "uf" to uf,
                "descricao" to description
            )
        }
        return emptyMap()
    }

    private fun extractFieldsImg(description: String, typeDoc: String): Any {
        return if(typeDoc == "CNH") {
            val namePattern = Regex("""NOME\n([A-Z\s]+)\n""")
            val cnhPattern = Regex("""N° REGISTRO-\n(\d+)\n""")
            val cpfPattern = Regex("""DATA NASCIMENTO-\n([0-9]{3}\.[0-9]{3}\.[0-9]{3}-[0-9]{2})""")
            val dobPattern = Regex("""DATA NASCIMENTO-\n.*?([0-9]{2}/[0-9]{2}/[0-9]{4})""")

            val name = namePattern.find(description)?.groups?.get(1)?.value?.trim() ?: "Not Found"
            val cnh = cnhPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val cpf = cpfPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val dob = dobPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"

            mapOf(
                "nome" to name,
                "cnh" to cnh,
                "cpf" to cpf,
                "dtnasc" to dob,
                "descricao" to description
            )
        } else {
            val renavamPattern = Regex("""CÓDIGO RENAVAM\n(\d+)\n""")
            val placaPattern = Regex("""PLACA\n([A-Z0-9]+)\n""")
            val cpfCnpjPattern =
                Regex("""CPF/CNPJ\n([0-9]{3}\.[0-9]{3}\.[0-9]{3}-[0-9]{2}|[0-9]{2}\.[0-9]{3}\.[0-9]{3}/[0-9]{4}-[0-9]{2})\n""")
            val chassiPattern = Regex("""(?:\*\n){3}(\w{17})""")

            val renavam = renavamPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val placa = placaPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val cpfCnpj = cpfCnpjPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"
            val chassi = chassiPattern.find(description)?.groups?.get(1)?.value ?: "Not Found"

            mapOf(
                "renavam" to renavam,
                "placa" to placa,
                "cpfcnpj" to cpfCnpj,
                "chassi" to chassi,
                "descricao" to description
            )
        }
    }
    fun validateInformation(information: String): Boolean {
        val containsNumbers = information.contains(Regex("\\d"))
        return information.length > 15 && information.contains("Not Found") && !containsNumbers
    }
}
