package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.devcenter.scanbot.services.FluxoService
import br.com.sankhya.devcenter.scanbot.utils.Image
import br.com.sankhya.devcenter.scanbot.utils.ValidadorURL
import br.com.sankhya.devcenter.scanbot.utils.toUtf8
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import com.google.gson.Gson
import com.sankhya.util.ValidadorCpfCnpj
import java.math.BigDecimal

class FluxoManualHelper {
  private val scanbotParceiroHelper = ScanbotParceiroHelper()
  private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")
  private val scanConfDAO: JapeWrapper = JapeFactory.dao("ScanbotConfig")

  fun init(valor: String, telefone: String): Map<String, Any?> {
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
    var confVO = scanConfDAO.findOne("CODCONF = 1")

    fun message(msg: String, valor1: String, valor2: String): String {
      return orderMenu(msg, valor1, valor2)
    }
    val mapValues: MutableMap<String, Any?> = mutableMapOf(
      "telefone" to telefone, "valor" to valor
    )
    val MsgError = mapOf(
      "status" to 400, "message" to "#SEND_AND_CLOSE" + "Ocorreu um erro, por favor entre em contato com o suporte."
    )

    val status = FluxoService.StatusCadastro.valueOf(scanParVO.getProperty("STATUSCAD").toString())

    when (status) {
      FluxoService.StatusCadastro.AGUARDANDO, FluxoService.StatusCadastro.NOME -> {
        try {
          val message = message("Agora precisamos que voce de um aceite no termo termo de consentimento para tratamento de Dados Pessoais. (https://www.sankhya.com.br/politica-de-privacidade/) \n\nAo continuar, voce concorda com a coleta e o uso de seus dados pessoais de acordo com a nossa Politica de Privacidade.".toUtf8()
            , "ACEITAR", "REJEITAR")
          mapValues["campo"] = "NOME"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "LGPD")
          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to message
            )
          } else {
            return MsgError
          }
        } catch (e: Exception) {
          return MsgError
        }
      }

      FluxoService.StatusCadastro.LGPD -> {
        try {
          val scanbotParceiroHelper = ScanbotParceiroHelper()
          var mapValues = mutableMapOf(
            "telefone" to telefone, "valor" to valor, "campo" to "LGPD"
          )

          if(valor == "REJEITAR") {
            mapValues["valor"] = "N"
            scanbotParceiroHelper.salvar(mapValues, "FINALIZADO")

            return mapOf(
              "status" to 200,
              "message" to "#SEND_AND_CLOSE" + "Ok, como voce nao aceitou a solicitacao da LGPD, nao continuaremos com as solicitacoes."
            )
          } else if(valor == "ACEITAR") {
            mapValues["valor"] = "S"
            val retorno = scanbotParceiroHelper.salvar(mapValues, "CPFCNPJ")

            if(retorno["status"] == 200) {
              return mapOf(
                "status" to 200,
                "message" to "Obrigado pelo aceite.  \n" +
                        "Agora precisamos que nos envie o PDF da sua CNH.\n" +
                        "Para obter o PDF da sua CNH, entre no aplicativo 'Carteira Digital' e nos envie o PDF.\n" +
                        "Selecionando o botao exportar e compartilhando na nossa conversa do WhatsApp."
              )
            }
          } else {
            return mapOf(
              "status" to 400,
              "message" to "Por favor, clique em uma das opções acima.")
          }

        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.CPFCNPJ -> {
        try {
          if (!ValidadorCpfCnpj(valor).isValid) {
            return mapOf("status" to 400, "message" to "CPF ou CNPJ invalido")
          }

          val scanbotParceiroHelper = ScanbotParceiroHelper()
          mapValues["campo"] = "CPF"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "CNH")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "Seus dados foram registrados com sucesso, por favor insira o numero de registro da sua CNH."
            )
          }
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.CNH -> {
        try {
          if (valor.length != 11) {
            return mapOf("status" to 400, "message" to "CNH invalido")
          }
          mapValues["campo"] = "CNH"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "FOTO")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "Sua CNH foi registrada com sucesso, por favor nos envie uma foto do seu rosto."
            )
          }
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.FOTO -> {
        try {
          if (!ValidadorURL().validarURL(valor)) {
            return mapOf("status" to 415, "message" to "Por favor, insira uma foto válida.")
          }

          val byteArray = Image().generateByteArray(valor)
          mapValues["valor"] = byteArray
          mapValues["campo"] = "FOTO"

          val retorno = scanbotParceiroHelper.salvar(mapValues, "FOTOCNH")
          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "Sua foto foi registrada com sucesso, por favor nos envie uma foto da sua CNH."
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }


      FluxoService.StatusCadastro.FOTOCNH -> {
        try {
          if (!ValidadorURL().validarURL(valor)) {
            return mapOf("status" to 415, "message" to "Por favor, insira uma foto valida.")
          }

          val byteArray = Image().generateByteArray(valor)
          mapValues["valor"] = byteArray
          mapValues["campo"] = "FOTOCNH"
          if (confVO.getProperty("VEICULO") == "S") {
            val retorno = scanbotParceiroHelper.salvar(mapValues, "RENAVAM")
            if (retorno["status"] == 200) {
              return mapOf(
                "status" to 200,
                "message" to "A foto da sua CNH foi registrada com sucesso, por favor insira o numero do seu RENAVAM."
              )
            }
            return mapOf("status" to retorno["status"], "message" to retorno["message"])
          } else {
            val retorno = scanbotParceiroHelper.salvar(mapValues, "FINALIZADO")

            if (retorno["status"] == 200) {
              return mapOf(
                "status" to 200,
                "message" to "O seu registro foi concluido com sucesso, agora e so aguardar enquanto analisamos seu cadastro, assim que finalizado entraremos em contato.\nObrigado."
              )
            }

            return mapOf("status" to retorno["status"], "message" to retorno["message"])
          }

        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.RENAVAM -> {
        try {
          println("Entrou para registar o renavam")
          if (valor.length != 11) {
            return mapOf("status" to 400, "message" to "RENAVAM invalido")
          }
          mapValues["campo"] = "RENAVAM"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "PLACA")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "Seu RENAVAM foi registrado com sucesso, por favor insira a placa do seu veiculo."
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.PLACA -> {
        try {
          val regexplaca = Regex("^[A-Z]{3}[0-9][A-Z][0-9]{2}\$|^[A-Z]{3}[0-9]{4}\$")
          val placa = valor.uppercase()
          if (!placa.matches(regexplaca)) {
            return mapOf("status" to 400, "message" to "Placa invalida")
          }

          mapValues["campo"] = "PLACA"
          mapValues["valor"] = placa
          val retorno = scanbotParceiroHelper.salvar(mapValues, "VEICPROP")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to message("Sua placa foi salva com sucesso, agora nos diga, o seu veiculo e proprio ou nao", "Proprietario", "Nao proprietario")
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.VEICPROP -> {
        try {
          var req = ""
          if (valor == "NAO" || valor == "NAO" || valor == "Nao" || valor == "NaO" || valor == "N" || valor == "n") {
            req = "NAO"
          } else if (valor == "SIM" || valor == "Sim" || valor == "SiM" || valor == "S" || valor == "s") {
            req = "SIM"
          } else {
            return mapOf("status" to 400, "message" to "Por favor, informe SIM ou NAO")
          }

          mapValues["valor"] = req
          mapValues["campo"] = "VEICPROP"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "FOTODOCVEIC")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "Foi regisgtrada a informacao, precisamos que mande uma foto do documento do seu veiculo, por favor."
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.FOTODOCVEIC -> {
        try {
          if (!ValidadorURL().validarURL(valor)) {
            return mapOf("status" to 415, "message" to "Por favor, insira uma foto valida.")
          }

          val byteArray = Image().generateByteArray(valor)
          mapValues["valor"] = byteArray
          mapValues["campo"] = "FOTODOCVEIC"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "FOTOFRENVEIC")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "A foto foi registrada com sucesso, precisamos que mande uma foto da frente do seu veiculo, por favor."
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.FOTOFRENVEIC -> {
        try {
          if (!ValidadorURL().validarURL(valor)) {
            return mapOf("status" to 415, "message" to "Por favor, insira uma foto valida.")
          }

          val byteArray = Image().generateByteArray(valor)
          mapValues["valor"] = byteArray
          mapValues["campo"] = "FOTOFRENVEIC"
          val retorno = scanbotParceiroHelper.salvar(mapValues, "FOTOTRASVEIC")

          if (retorno["status"] == 200) {
            return mapOf(
              "status" to 200,
              "message" to "A foto foi registrada com sucesso, precisamos que mande uma foto da tras do seu veiculo, por favor."
            )
          }

          return mapOf("status" to retorno["status"], "message" to retorno["message"])
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.FOTOTRASVEIC -> {
        try {
          if (!ValidadorURL().validarURL(valor)) {
            return mapOf("status" to 415, "message" to "Por favor, insira uma foto valida.")
          }

          val byteArray = Image().generateByteArray(valor)
          mapValues["valor"] = byteArray
          mapValues["campo"] = "FOTOTRASVEIC"
          scanbotParceiroHelper.salvar(mapValues, "FINALIZADO")

          scanbotParceiroHelper.salvar(mapOf("campo" to "STATUS_CENTRAL", "valor" to "1", "telefone" to telefone), "FINALIZADO")

          return mapOf(
            "status" to 200,
            "message" to "#SEND_AND_CLOSE" + "Registro finalizado com sucesso, nosso time ira verificar as informacoes e entrar em contato.",
          )
        } catch (e: Exception) {
          e.printStackTrace()
          return mapOf("status" to 400, "message" to e.message)
        }
      }

      FluxoService.StatusCadastro.FINALIZADO -> {
        val dadosParc = scanbotParceiroHelper.buscar(telefone)
        println("dadosParc: $dadosParc")

        if (dadosParc["LGPD"] == "NAO") {
          return mapOf(
            "status" to 200,
            "message" to "Não conseguimos prosseguir com o atendimento, pois o LGPD não foi aceito, por favor entre em contato com a administração da empresa."
          )
        }
        return mapOf(
          "status" to 200,
          "message" to "#SEND_AND_CLOSE" + "Seus dados foram salvos com sucesso, aguarde enquanto nossa equipe efetua uma análise do seu cadastro."
        )
      }
    }

    return mapOf(
      "status" to 400, "message" to "#SEND_AND_CLOSE" + "Erro ao buscar status, contate o administrador. ${status}"
    )
  }

  data class Reply(val id: String, val title: String)
  data class Button(val type: String, val reply: Reply)
  data class Action(val buttons: List<Button>)
  data class Body(val text: String)
  data class ButtonMessage(val type: String, val body: Body, val action: Action)

}