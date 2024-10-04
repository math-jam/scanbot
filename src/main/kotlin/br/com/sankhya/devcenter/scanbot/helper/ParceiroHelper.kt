package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import java.math.BigDecimal
class ParceiroHelper() {
    fun salvar(telefone: String): BigDecimal {
        var codparc = 0.toBigDecimal()
        val parDAO: JapeWrapper = JapeFactory.dao("Parceiro")
        val dadosExtrasDAO: JapeWrapper = JapeFactory.dao("ComplementoParc")
        val scanbotParceiroHelper = ScanbotParceiroHelper()

        val campos = scanbotParceiroHelper.buscar(telefone)

        if (campos["CPF"] == null) {
            return 0.toBigDecimal()
        }

        var parVO = parDAO.findOne("CGC_CPF = ?", campos["CPF"]!!)
        if (parVO == null) {
            parVO = parDAO.create()
                .set("TELEFONE", campos["TELEFONE"].toString())
                .set("NOMEPARC", campos["NOME"].toString())
                .set("CGC_CPF", campos["CPF"].toString())
                .set("ATIVO", "S")
//                        .set("CENTRALCAD", "N")
                .set("TIPPESSOA", "F")
                .set("CODCID", 1.toBigDecimal())
                .save()
            codparc = parVO.asBigDecimal("CODPARC")
            println("codparc -> $codparc")

            dadosExtrasDAO.create()
                .set("CODPARC", codparc)
                .set("NROCNH", campos["CNH"].toString())
                .set("PAI", campos["PAI"].toString())
                .set("MAE", campos["MAE"].toString())
                .set("VENCIMENTOCNH", campos["VENCIMENTO"])
//                        .set("DTPRIMEIRACNH", campos["PRIMEIRAHAB"])
//                        .set("DTEMISSAOCNHATUAL", campos["PRIMEIRAHAB"])
//                        .set("NROSEGURANCACNH", campos["SEGURANCA"].toString())
                .set("CATEGORIACNH", campos["CATEGORIA"].toString())
                .save()

            return codparc
        } else {
            parDAO.prepareToUpdate(parVO)
                .set("TELEFONE", campos["TELEFONE"].toString())
                .set("NOMEPARC", campos["NOME"].toString())
                .set("CGC_CPF", campos["CPF"].toString())
                .set("ATIVO", "S")
//                        .set("CENTRALCAD", "N")
                .set("TIPPESSOA", "F")
                .update()

            codparc = parVO.asBigDecimal("CODPARC")
            println("codparc -> $codparc")

            val dadosExtrasVO = dadosExtrasDAO.findOne("CODPARC = ?", codparc)

            dadosExtrasDAO.prepareToUpdate(dadosExtrasVO)
                .set("CODPARC", codparc)
                .set("NROCNH", campos["CNH"].toString())
                .set("PAI", campos["PAI"].toString())
                .set("MAE", campos["MAE"].toString())
                .set("VENCIMENTOCNH", campos["VENCIMENTO"])
//                        .set("DTPRIMEIRACNH", campos["PRIMEIRAHAB"])
//                        .set("DTEMISAOCNHATUAL", campos["PRIMEIRAHAB"])
                .set("CATEGORIACNH", campos["CATEGORIA"].toString())
                .update()
            return codparc
        }
    }

    fun buscar(cpf: String): BigDecimal {
        val parDAO: JapeWrapper = JapeFactory.dao("Parceiro")
        val parVO = parDAO.findOne("CGC_CPF = ?", cpf) ?: return 0.toBigDecimal()
        val codparc = parVO.asBigDecimal("CODPARC") ?: return 0.toBigDecimal()

        return codparc
    }
}