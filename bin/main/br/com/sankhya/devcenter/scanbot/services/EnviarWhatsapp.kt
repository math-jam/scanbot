package br.com.sankhya.devcenter.scanbot.services

import br.com.sankhya.devcenter.dao.JapeContext
import br.com.sankhya.devcenter.dao.openSession
import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteController
import br.com.sankhya.devcenter.sankhyaw.services.ejb2.annotations.EJB2TransactionType
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import com.google.gson.Gson
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
@RemoteController(serviceName = "WhatsappServiceSP", transactionType = EJB2TransactionType.Supports)
class WhatsappService() {
    fun chamarUsuario(telefone: String): String {
        var r = telefone
        openSession {
            JapeContext.setup(null)
            it.execEnsuringTX {
                if(telefone == null) throw Exception("Nenhum telefone informado")

                println("telefone $telefone")
                var conn: HttpURLConnection? = null

                val configDAO: JapeWrapper = JapeFactory.dao("ScanbotConfig")
                val configVO = configDAO.findByPK(BigDecimal.ONE)
                val token = configVO.asString("TOKEN")
                    ?: throw Exception("Token de envio não registrado, por favor registre o seu token nas configurações do ScanBot")

                try {
                    val url = URL("https://sankhya.tm2digital.com/chat/api/send/message")
                    conn = url.openConnection() as HttpURLConnection
                    conn.doOutput = true
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Authorization", token)
                    conn.setRequestProperty("Content-Type", "application/json")

                    val requestBody = Gson().toJson(
                        mapOf(
                            "phone" to telefone,
                            "channel" to "WHATSAPP",
                            "message" to "Boas vindas ao Scanbot DevCenter",
                            "group" to "Dev Center",
                            "createdBy" to "dev_center",
                            "additionalInfo" to "{\"namespace\":\" + 32d21eb1_4da4_44d8_aea6_33d29005631a \",\"elementName\":\" jornada_devcenter \", \"medias\":{},\"openSession\":false}"
                        )
                    )

                    println("requestBody $requestBody")

                    conn.outputStream.use { os ->
                        os.write(requestBody.toByteArray())
                        os.flush()
                    }

                    val responseCode = conn.responseCode
                    if (responseCode == 200) {
                        r = "Foi enviada a mensagem para o motorista\n Status: $responseCode"
                    } else{
                        throw Exception("Erro ao enviar mensagem\n status: $responseCode")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    r = "Erro ao enviar mensagem\n status: ${e.message}"
                }
            }
        }

        return r
    }
}
