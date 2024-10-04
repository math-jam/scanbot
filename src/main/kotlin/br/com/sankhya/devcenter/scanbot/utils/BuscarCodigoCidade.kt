package br.com.sankhya.devcenter.scanbot.utils

import br.com.sankhya.jape.wrapper.JapeFactory
import java.math.BigDecimal

private val cidDAO = JapeFactory.dao("Cidade")

class BuscarCodigoCidade {

  fun buscar(cidade: String): BigDecimal {
    try {
      val parVO = cidDAO.findOne("DESCRICAOCORREIO = ?", cidade)
      return parVO.asBigDecimal("CODCID")
    } catch (e: Exception) {
      return 1.toBigDecimal()
    }
  }
}