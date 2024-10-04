package br.com.sankhya.devcenter.scanbot.services

import br.com.sankhya.devcenter.dao.JapeContext
import br.com.sankhya.devcenter.dao.openSession
import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteController
import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteControllerSession
import br.com.sankhya.devcenter.sankhyaw.services.ejb2.annotations.EJB2TransactionType
import br.com.sankhya.devcenter.scanbot.helper.*
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import java.time.Duration
import java.time.Instant

@RemoteController(serviceName = "ScanbotServiceSP", transactionType = EJB2TransactionType.Supports)
class FluxoService {
  private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")
  private val scanConfDAO: JapeWrapper = JapeFactory.dao("ScanbotConfig")
  private val fluxoManual = FluxoManualHelper()
  private val fluxoAutomatico = FluxoAutomaticoHelper()
  private val scanbotParceiroHelper = ScanbotParceiroHelper()

  @RemoteControllerSession(EJB2TransactionType.Required)
  fun iniciarFluxo(valor: String, telefone: String): Any {
    println("~~~ INICIANDO FLUXO ~~~")
    println("~~~ valor: $valor , telefone: $telefone ~~~")
    var r = mapOf("status" to 200, "message" to "Success")
    openSession {
      JapeContext.setup(null)
      it.execEnsuringTX {
        try {
          var scanParVO = scanParDAO.findOne("TELEFONE = ?", telefone)
          var confVO = scanConfDAO.findOne("CODCONF = 1")
          if (scanParVO == null) {
            println("~~~ Não encontrou scanbot ~~~")
            r = mapOf(
              "status" to 400,
              "message" to "Parceiro nao encontrado, por favor entre em contato com a administracao da empresa."
            )
          }
          val ocrFluxo = confVO.asString("OCR") == "S"
          val autFluxo = scanParVO.asString("TIPCAD") == "A"
          val fluxoManualOuAutomatico = ocrFluxo && autFluxo


          val dhAlter = scanParVO.asTimestamp("DHALTER").toInstant()
          val now = Instant.now()
          val minutesElapsed = Duration.between(dhAlter, now).toMinutes()

          if (minutesElapsed > 31) {
            val retorno = FluxoOcioso().init(fluxoManualOuAutomatico, telefone)

            println("retorno: $retorno")
            r = mapOf(
              "status" to retorno["status"] as Int,
              "message" to retorno["message"] as String
            )
          } else {
            val statusCadastro = StatusCadastro.valueOf(scanParVO.getProperty("STATUSCAD").toString())
            scanbotParceiroHelper.updateTimestamp(telefone, statusCadastro.toString())
            val mapa = if (fluxoManualOuAutomatico) {
              fluxoAutomatico.init(valor, telefone)
            } else {
              fluxoManual.init(valor, telefone)
            }

            val mensagem = mapa["message"] as String
            r = mapOf(
              "status" to 200,
              "message" to mensagem
            )
          }
        } catch (e: Exception) {
          r = mapOf("status" to 406, "message" to e.message.toString())
          e.printStackTrace()
        }
      }
    }

    return r
  }

  enum class StatusCadastro {
    AGUARDANDO,
    NOME,
    CPFCNPJ,
    CNH,
    FOTO,
    FOTOCNH,
    PLACA,
    VEICPROP,
    RENAVAM,
    FOTODOCVEIC,
    FOTOTRASVEIC,
    FOTOFRENVEIC,
    LGPD,
    FINALIZADO
  }
}
