package br.com.sankhya.devcenter.scanbot.services

import br.com.sankhya.devcenter.dao.JapeContext
import br.com.sankhya.devcenter.dao.openSession
import br.com.sankhya.devcenter.sankhyaw.services.boot.RemoteController
import br.com.sankhya.devcenter.sankhyaw.services.ejb2.annotations.EJB2TransactionType
import br.com.sankhya.devcenter.scanbot.helper.ParceiroHelper
import br.com.sankhya.devcenter.scanbot.helper.ScanbotParceiroHelper
import br.com.sankhya.devcenter.scanbot.helper.VeiculoHelper

@RemoteController(serviceName = "FinalizarSP", transactionType = EJB2TransactionType.Supports)
class FinalizarService{
  val parceiroHelper = ParceiroHelper()
  val veiculoHelper = VeiculoHelper()
  val scanbotHelper = ScanbotParceiroHelper()
  fun init(telefone: String): Map<String, Any> {
    var retorno = mapOf("message" to "Ok")
    openSession {
      JapeContext.setup()
      it.execEnsuringTX {
        try {
          val codparc = parceiroHelper.salvar(telefone)
          println("codparc $codparc")

          if (codparc != 0.toBigDecimal()) {
            scanbotHelper.salvar(
              mapOf("campo" to "CODPARC", "valor" to codparc, "telefone" to telefone),
              "FINALIZADO"
            )

            val codveic = veiculoHelper.salvar(telefone)
            retorno = if (codveic == 0.toBigDecimal()) {
              mapOf("message" to "Erro ao salvar veiculo")
            } else {
              scanbotHelper.salvar(
                        mapOf("campo" to "CODVEICULO", "valor" to codveic, "telefone" to telefone),
                "FINALIZADO"
              )
              scanbotHelper.salvar(
                mapOf("campo" to "STATUS_CENTRAL", "valor" to "5", "telefone" to telefone), "FINALIZADO")

              mapOf("message" to "Sucesso\n Código do Parceiro: $codparc\n Código do Veículo: $codveic")
            }
          } else {
            retorno = mapOf("message" to "Erro ao salvar parceiro.")
          }
        } catch (e: Exception) {
          retorno = mapOf("message" to e.message.toString())
        }
      }
    }
    return retorno
  }
}
