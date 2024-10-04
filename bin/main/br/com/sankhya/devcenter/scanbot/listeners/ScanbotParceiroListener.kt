package br.com.sankhya.devcenter.scanbot.listeners

import br.com.sankhya.devcenter.sankhyaw.services.boot.Listener
import br.com.sankhya.jape.event.PersistenceEvent
import br.com.sankhya.jape.event.PersistenceEventAdapter
import br.com.sankhya.jape.vo.DynamicVO
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.modelcore.MGEModelException

@Listener(instanceNames = ["ScanbotParceiro"])
class ScanbotParceiroListener: PersistenceEventAdapter() {
  override fun beforeInsert(event: PersistenceEvent) {
    val dao = JapeFactory.dao("ScanbotParceiro")
    val vo = event.vo as DynamicVO

    vo.setProperty("TELEFONE", vo.asString("TELEFONE").replace(" ", ""))
    val telefone = vo.asString("TELEFONE")
    val codseq = vo.asBigDecimal("CODSEQ")

    val rows = dao.find("TELEFONE = ? AND CODSEQ <> ?", telefone, codseq)

    if (!rows.isEmpty()) {
      throw MGEModelException("Já existe um parceiro com este telefone")
    }
  }
}