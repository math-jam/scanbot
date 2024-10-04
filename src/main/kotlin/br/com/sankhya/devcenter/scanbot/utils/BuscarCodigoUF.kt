package br.com.sankhya.devcenter.scanbot.utils

import br.com.sankhya.jape.wrapper.JapeFactory
import java.math.BigDecimal

class BuscarCodigoUF {
  private val ufDAO = JapeFactory.dao("UnidadeFederativa")
  fun buscar(uf: String): BigDecimal {
    val ufVO = ufDAO.findOne("UF = ?", uf)
    return ufVO.asBigDecimal("CODUF")
  }
}