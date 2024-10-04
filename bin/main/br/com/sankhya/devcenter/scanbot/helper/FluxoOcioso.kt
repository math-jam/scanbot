package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.devcenter.scanbot.services.FluxoService
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper

class FluxoOcioso {
  private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")
  private val scanbotParceiroHelper = ScanbotParceiroHelper()

  fun init(flux: Boolean, telefone: String): Map<String, Any> {
    println("Entrou no FluxoOcioso()")
    var scanParVO = scanParDAO.findOne("TELEFONE = ?", telefone)

    val status = FluxoService.StatusCadastro.valueOf(scanParVO.getProperty("STATUSCAD").toString()).toString()
    scanbotParceiroHelper.updateTimestamp(telefone, status.toString())

    println("Entrou no FluxoOcioso STATUS CADASTRO: ${status}")
    println("~~ Entrou no FluxoOcioso ${status} ~~")

    when (status) {
      "AGUARDANDO" -> {
        println("Entrou aqui FluxoOcioso para retornar um msg de usuario ocioso. AGUARDANDO")

        var message =
          "Olá, verificamos que voce não iniciou o processo de cadastro, por favor nos informe seu nome completo."

        scanbotParceiroHelper.updateStatus(telefone, "LGPD")

        return mapOf(
          "status" to 200, "message" to message
        )
      }

      "LGPD" -> {
        var message =
          "Olá, vimos que você já iniciou o fluxo de casdastro, porém ainda não deu aceite nos termos de LGPD, por favor precisamos que voce de o aceite no termo para continuarmos o cadastro."

        return mapOf("status" to 200, "message" to message)
      }

      "NOME" -> {
        var message =
          "Olá, verificamos que voce não iniciou o processo de cadastro, por favor nos informe seu nome completo."

        return mapOf("status" to 200, "message" to message)
      }

      "CPFCNPJ" -> {
        var message = "Verificamos que voce já inicou o processo de casdastro, por favor nos envie agora o seu CPF, somente os números."
        if(flux) {
          message = "Verificamos que voce não iniciou o fluxo de cadastro, por favor nos envie o PDF do seu documento de habilitação, para adquirir esse documento, voce deverá acessar o aplicativo de carteira digital."
        }
        return mapOf(
          "status" to 200,
          "message" to message
        )
      }

      "CNH" -> {
        return mapOf(
          "status" to 200,
          "message" to "Verificamos que voce já iniciou o processo de cadastro, por favor nos envie o número da sua CNH."
        )
      }

      "FOTO" -> {
        return mapOf(
          "status" to 200,
          "message" to "Verificamos aqui que voce já inicou o processo de cadastro, por favor nos envie uma selfie com o seu documento em mãos."
        )
      }

      "FOTOCNH" -> {
        return mapOf(
          "status" to 200,
          "message" to "Identificamos que voce já iniciou o processo de casdatro, por favor nos envie uma foto da sua CNH."
        )
      }

      "RENAVAM" -> {
        var message =
          "Verificamos que voce já começou o fluxo de cadastro, por favor nos envie o RENAVAM do seu veículo."

        if (flux) {
          message =
            "Verificamos que voce já iniciou o fluxo de casdastro, por favor nos envie o PDF do documento do veículo, para adiquiri-lo acesse o aplicativo da Carteira Digital."
        }
        return mapOf(
          "status" to 200, "message" to message
        )
      }

      "PLACA" -> {
        return mapOf(
          "status" to 200,
          "message" to "Verificamos que voce não iniciou o processo de cadastro, por favor nos informe a placa do seu veículo."
        )
      }

      "VEICPROP" -> {
        return mapOf(
          "status" to 200,
          "message" to "Verificamos que voce já iniciou o processo de cadastro, por favor nos informe se o veículo é próprio. SIM ou NAO"
        )
      }

      "FOTODOCVEIC" -> {
        return mapOf(
          "status" to 200,
          "message" to "Identificamos que voce já iniciou o cadastro, por favor nos envie uma foto do documento do seu veículo."
        )
      }

      "FOTOFRENVEIC" -> {
        return mapOf(
          "status" to 200,
          "message" to "Identificamos que voce já iniciou o processo de cadastro, por favor nos envie uma foto da parte de frente do seu veículo."
        )
      }

      "FOTOTRASVEIC" -> {
        return mapOf(
          "status" to 200,
          "message" to "Identificamos que voce já iniciou o processo de cadastro, por favor nos envie uma foto da parte de trás do seu veículo."

        )
      }

      "FINALIZADO" -> {
        return mapOf(
          "status" to 200,
          "message" to "O seu cadastro já foi finalizado, por favor aguarde enquanto nossa equipe analisa o seu cadastro."
        )
      }

      else -> {
        return mapOf(
          "status" to 400,
          "message" to "#SEND_AND_CLOSE" + "Houve um problema com sua solicitação, por favor entre em contato com o administrativo da empresa."
        )
      }
    }
  }
}