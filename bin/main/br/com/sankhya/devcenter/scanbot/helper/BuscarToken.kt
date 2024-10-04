package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import java.math.BigDecimal
import org.apache.http.impl.client.DefaultHttpClient
import java.net.HttpURLConnection
import java.nio.charset.Charset
import java.util.*
import javax.json.Json


class BuscarToken {

    companion object {

        fun buscarBearerToken(): String {
            val configDAO: JapeWrapper = JapeFactory.dao("ScanbotConfig")
            val configVO = configDAO.findByPK(BigDecimal.ONE)

            val userameNeppo: String = configVO.asString("USERNEPPO")
            val passwordNeppo: String = configVO.asString("PASSNEPPO")
            val keyTokenNeppo: String = configVO.asString("TOKENNEPPO")

            try {
                val httpClient = HttpClient()
                val post = PostMethod("https://wso2-cp.neppo.com.br/oauth2/token")
                post.addRequestHeader(
                    "Authorization",
                    "Basic $keyTokenNeppo"
                )
                post.addRequestHeader(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
                )
                post.requestEntity = StringRequestEntity(
                    "grant_type=password&username=$userameNeppo&password=$passwordNeppo",
                    "application/x-www-form-urlencoded",
                    "UTF-8"
                )

                val statusCode = httpClient.executeMethod(post)
                if (statusCode == 200) {
                    val gson = Gson()
                    val responseBody = gson.fromJson(post.responseBodyAsString, ResponseBuscarToken::class.java)

                    if (responseBody.accessToken != null) {
                        return responseBody.accessToken!!
                    } else {
                        throw Exception("Erro ao buscar o https://neppo.docs.apiary.io/#reference/exemplos-de-chamadas/envio-ativo/agenda-uma-mensagem-para-envio-ativo de acesso. Causa: $responseBody")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                throw Exception(e.message)
            }

            return ""
        }
    }


    data class ResponseBuscarToken (
        @SerializedName("access_token"  ) var accessToken  : String? = null,
        @SerializedName("refresh_token" ) var refreshToken : String? = null,
        @SerializedName("scope"         ) var scope        : String? = null,
        @SerializedName("token_type"    ) var tokenType    : String? = null,
        @SerializedName("expires_in"    ) var expiresIn    : Int?    = null
    )
}