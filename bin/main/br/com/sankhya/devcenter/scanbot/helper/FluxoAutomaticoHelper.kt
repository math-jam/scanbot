package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.devcenter.scanbot.services.FluxoService
import br.com.sankhya.devcenter.scanbot.services.GoogleVisionService
import br.com.sankhya.devcenter.scanbot.utils.*
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import com.google.gson.Gson
import com.sankhya.util.TimeUtils
import com.sankhya.util.ValidadorCpfCnpj
import java.io.Serializable
import java.time.LocalDate

class FluxoAutomaticoHelper {
  private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")
  private val scanbotParceiroHelper = ScanbotParceiroHelper()

  fun init(url: String, telefone: String): Map<out Serializable, Any?> {
    fun orderMenu(text: String, valor1: String, valor2: String): String {
      val buttons = listOf(
        Button("reply", Reply("S", valor1)),
        Button("reply", Reply("N", valor2))
      )
      val action = Action(buttons)
      val body = Body(text)
      val buttonMessage = ButtonMessage("button", body, action)

      val objectMapper = Gson()
      return objectMapper.toJson(buttonMessage)
    }

    var scanParVO = scanParDAO.findOne("TELEFONE = ?", telefone)

    val statusCadastro = FluxoService.StatusCadastro.valueOf(scanParVO.getProperty("STATUSCAD").toString())
    val dados = GoogleVisionService()
    val dadosCadastro: Map<String, String>
    var retornoFluxo: Map<String, Any?> = mapOf("status" to 200, "message" to "Success")

    fun message(msg: String, valor1: String, valor2: String): String {
      return orderMenu(msg, valor1, valor2)
    }

    try {
      if (statusCadastro == FluxoService.StatusCadastro.AGUARDANDO || statusCadastro == FluxoService.StatusCadastro.NOME) {
        val message = message(
          "Agora precisamos que voce de um aceite no termo de consentimento para tratamento de Dados Pessoais. (https://www.sankhya.com.br/politica-de-privacidade/) \n" +
            "\n" +
            "Ao continuar, voce concorda com a coleta e o uso de seus dados pessoais de acordo com a nossa Politica de Privacidade.",
          "ACEITAR",
          "REJEITAR"
        )

        scanbotParceiroHelper.salvar(
          mapOf(
            "telefone" to telefone,
            "campo" to "NOME",
            "valor" to url
          ), "LGPD"
        )

        return mapOf(
          "status" to 200,
          "message" to message
        )
      } else if (statusCadastro == FluxoService.StatusCadastro.LGPD) {
        try {
          val retorno: Map<String, Any?>
          val scanbotParceiroHelper = ScanbotParceiroHelper()
          var mapValues = mutableMapOf<String, String>()
          mapValues = mutableMapOf(
            "telefone" to telefone, "valor" to url, "campo" to "LGPD"
          )

          if (url == "REJEITAR") {
            mapValues["valor"] = "N"
            scanbotParceiroHelper.salvar(mapValues, "FINALIZADO")
            retorno = mapOf(
              "status" to 200,
              "message" to "#SEND_AND_CLOSE" + "Ok, como voce nao aceitou os termos de LGPD nao continuaremos com o cadastro, caso tenha alguma duvida entre em contato com a administração da empressa."
            )
          } else if(url == "ACEITAR") {
            mapValues["valor"] = "S"
            scanbotParceiroHelper.salvar(mapValues, "CPFCNPJ")
            retorno = mapOf(
              "status" to 200,
              "message" to "Obrigado pelo aceite.  \n" +
              "Agora precisamos que nos envie o PDF da sua CNH.\n" +
              "Para obter o PDF da sua CNH, entre no aplicativo 'Carteira Digital' e nos envie o PDF.\n" +
              "Selecionando o botao exportar e compartilhando na nossa conversa do WhatsApp."
            )
          } else {
            retorno = mapOf(
              "status" to 400,
              "message" to "Por favor, clique em uma das opções acima. $url")
          }

          return retorno
        } catch (e: Exception) {
          e.printStackTrace()
          println("~~ ERRO AO SALVAR CADASTRO: ${e.message} ~~")
          return mapOf("status" to 400, "message" to e.message)
        }

      } else if (statusCadastro == FluxoService.StatusCadastro.CPFCNPJ) {
        if (!ValidadorURL().validarURL(url)) {
          return mapOf("status" to 400, "message" to "Por favor nos envie um PDF valido da sua CNH.")
        }
        dadosCadastro = dados.processUrl(url, typeDoc = "CNH") as Map<String, String>
        for ((key, value) in dadosCadastro) {
          when (key) {
            "nome" -> {
              println("nome: $value")

              if (value == "Not Found" || value.length < 5) {
                println(" ~~ Entrou aqui porque não tem nome: $value ~~")
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "TIPCAD", "valor" to "M"
                  ), "CPFCNPJ"
                )
                retornoFluxo = mapOf(
                  "status" to 400,
                  "message" to "Não conseguimos identificar as informações, seguiremos pelo fluxo manual, nos informe o seu CPF ou CNPJ por favor."
                )

                return retornoFluxo
              } else {
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "NOME", "valor" to value
                  ), "CPFCNPJ"
                )
              }
            }

            "cpf" -> {
              println("cpf: $value")

              if (value == "Not Found" || !ValidadorCpfCnpj(value).isValid()) {
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "TIPCAD", "valor" to "M"
                  ), "CPFCNPJ"
                )
                retornoFluxo = mapOf(
                  "status" to 400,
                  "message" to "Nao conseguimos identificar o CPF no seu documento, por favor nos informe posteriormente."
                )
                return retornoFluxo
              } else {
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "CPF", "valor" to value
                  ), "FINALIZADO"
                )
              }
            }

            "dtnasc" -> {
              if(value != "Not Found") {
                val data = TimeUtils.buildTimestamp(value)

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "DTNASC", "valor" to data
                  ), "RENAVAM"
                )
              }
            }

            "rg" -> {
              if(value != "Not Found") {
                println("rg: $value")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "RG", "valor" to value
                  ), "RENAVAM"
                )
              }
            }

            "ufexped" -> {
              if(value != "Not Found") {
                println("ufexped: $value")
                val UF = BuscarCodigoUF().buscar(value)

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "UFEXPED", "valor" to UF
                  ), "RENAVAM"
                )
              }
            }

            "emissor" -> {
              if(value != "Not Found") {
                println("emissor: $value")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "EMISSOR", "valor" to value
                  ), "RENAVAM"
                )
              }
            }

            "cnh" -> {
                println("cnh: $value")

                if (value == "Not Found" || value.length < 11) {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "TIPCAD", "valor" to "M"
                    ), "CNH"
                  )
                  retornoFluxo = mapOf(
                    "status" to 400,
                    "message" to "Nao conseguimos identificar o numero da sua CNH, por favor nos envie."
                  )
                  return retornoFluxo
                } else {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "CNH", "valor" to value
                    ), "RENAVAM"
                  )

              }
            }

            "categoria" -> {
              if(value != "Not Found") {
                println("categoria: $value")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "CATEGORIA", "valor" to value
                  ), "RENAVAM"
                )
              }
            }

            "validade" -> {
              if(value != "Not Found") {

                val data = LocalDate.parse(value)
                println("validade: $data")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "VALIDADE", "valor" to data
                  ), "RENAVAM"
                )
              }
            }

            "codSeguranca" -> {
              if(value != "Not Found") {

                println("codSeguranca: $value")
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "SEGURANCA", "valor" to value
                  ), "RENAVAM"
                )
              }

            }

            "primeiraHab" -> {
              if(value != "Not Found") {

                val data = LocalDate.parse(value)
                println("primeiraHab: $data")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "PRIMEIRAHAB", "valor" to data
                  ), "RENAVAM"
                )
              }
            }

            "pai" -> {
              if(value != "Not Found") {
                println("pai: $value")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "PAI", "valor" to value
                  ), "RENAVAM"
                )
              }
            }

            "mae" -> {
              if (value != "Not Found") {
                println("mae: $value")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "MAE", "valor" to value
                  ), "RENAVAM"
                )
              }
            }

            "dataEmissao" -> {
              if(value != "Not Found") {
                val data = LocalDate.parse(value)
                println("dataEmissao: $data")

                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "DTEMISSAO", "valor" to data
                  ), "RENAVAM"
                )
              }
            }

            else -> {
              println("Outro Campo: $key")
            }
          }
        }

        val byteArray = Image().generateByteArray(url)

        scanbotParceiroHelper.salvar(
          mapOf(
            "telefone" to telefone, "campo" to "FOTOCNH", "valor" to byteArray
          ), "RENAVAM"
        )


        val parceiroScanbot = scanbotParceiroHelper.buscar(telefone)
        println("parceiroScanbot: $parceiroScanbot")

        scanbotParceiroHelper.updateStatus(telefone, "RENAVAM")

        return retornoFluxo
      } else if (statusCadastro == FluxoService.StatusCadastro.RENAVAM) {
        if (!ValidadorURL().validarURL(url)) {
          return mapOf(
            "status" to 400, "message" to "Por favor nos envie um PDF valido do documento do veiculo."
          )
        }
        dadosCadastro = dados.processUrl(url, typeDoc = "RENAVAM") as Map<String, String>
        for ((key, value) in dadosCadastro) {
          println("key: $key, value: $value")
          if(value != "Not Found") {
            when (key) {
              "placa" -> {
                println("~~ ENTROU NA PLACA AUTOMATICO $value ~~")
                val placaRegex = Regex("^[A-Z]{3}\\d{4}$|^[A-Z]{3}\\d[A-Z]\\d{2}$")
                if (value == "Not Found" || !value.matches(placaRegex)) {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "TIPCAD", "valor" to "M"
                    ), "PLACA"
                  )
                  retornoFluxo = mapOf(
                    "status" to 400, "message" to "Placa nao identificada, por favor nos envie posteriormente."
                  )
                  return retornoFluxo
                } else {

                  retornoFluxo = mapOf(
                    "status" to 200,
                    "message" to "#SEND_AND_CLOSE" + "Seus dados foram salvos com sucesso, aguarde enquanto nossa equipe efetua uma análise do seu cadastro."
                  )
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "PLACA", "valor" to value
                    ), "VEICPROP"
                  )
                }
              }

              "renavam" -> {
                println("~~ ENTROU NO RENAVAM AUTOMATICO $value ~~")
                if (value == "Not Found" || value.length != 11) {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "TIPCAD", "valor" to "M"
                    ), "RENAVAM"
                  )
                  retornoFluxo = mapOf(
                    "status" to 400,
                    "message" to "Nao identificamos o numero do RENAVAM, por favor nos envie posteriormente."
                  )

                  return retornoFluxo

                } else {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "RENAVAM", "valor" to value
                    ), "PLACA"
                  )
                }
              }

              "cpfcnpj" -> {
                val parceiroscanbot = scanbotParceiroHelper.buscar(telefone)
                var veiculoproprio = if (parceiroscanbot["CPF"] == value) "S" else "N"

                retornoFluxo = mapOf(
                  "status" to 200,
                  "message" to "#SEND_AND_CLOSE" + "Seus dados foram salvos com sucesso, aguarde enquanto nossa equipe efetua uma análise do seu cadastro."
                )
                scanbotParceiroHelper.salvar(
                  mapOf(
                    "telefone" to telefone, "campo" to "VEICPROP", "valor" to veiculoproprio
                  ), "FINALIZADO"
                )
              }

              "chassi" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "CHASSI", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "marcaModelo" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "MARCAMODELO", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "especie" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "ESPECIE", "valor" to value
                    ), "FINALIZADO"
                  )

              }

              "anoFabricacao" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "ANOFABRICACAO", "valor" to value
                    ), "FINALIZADO"
                  )

              }

              "anoModelo" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "ANOMODELO", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "cor" -> {
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "COR", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "combustivel" -> {

                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "COMBUSTIVEL", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "potenciaCilindrada" -> {

                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "POTENCIACILINDRADA", "valor" to value
                    ), "FINALIZADO"
                  )
              }

              "cidade" -> {
                if (value == "Not Found") {

                  val codcid = BuscarCodigoCidade().buscar(value)
                  scanbotParceiroHelper.salvar(
                    mapOf(
                      "telefone" to telefone, "campo" to "CODCID", "valor" to codcid
                    ), "FINALIZADO"
                  )
                }
              }
            }
        }
        }

        val byteArray = Image().generateByteArray(url)

        scanbotParceiroHelper.salvar(
          mapOf(
            "telefone" to telefone, "campo" to "FOTODOCVEIC", "valor" to byteArray
          ), "FINALIZADO"
        )

        scanbotParceiroHelper.salvar(
          mapOf("campo" to "STATUS_CENTRAL", "valor" to "1", "telefone" to telefone),
          "FINALIZADO"
        )
        return retornoFluxo
      } else if (statusCadastro == FluxoService.StatusCadastro.FINALIZADO) {
        val dadosParc = scanbotParceiroHelper.buscar(telefone)

        if (dadosParc["LGPD"] == "NAO") {
          return mapOf(
            "status" to 200,
            "message" to "Não conseguimos prosseguir com o atendimento, pois o LGPD não foi aceito, por favor entre em contato com a administração da empresa."
          )
        }
        return mapOf(
          "status" to 200,
          "message" to "#SEND_AND_CLOSE" + "Cadastro ja concluido, para mais informacoes, contactar a administracao da empresa."
        )
      } else {
        return mapOf(
          "status" to 400,
          "message" to "#SEND_AND_CLOSE" + "Ocorreu um erro, por favor entre em contato com a administracao da empresa.."
        )
      }
    } catch (e: Exception) {
      e.printStackTrace()
      return mapOf(
        "status" to 400, "message" to "Entrou no catch -> " + e.message
      )
    }
  }

  data class Reply(val id: String, val title: String)
  data class Button(val type: String, val reply: Reply)
  data class Action(val buttons: List<Button>)
  data class Body(val text: String)
  data class ButtonMessage(val type: String, val body: Body, val action: Action)

}