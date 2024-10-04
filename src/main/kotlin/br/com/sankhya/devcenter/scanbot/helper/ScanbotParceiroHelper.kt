package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.devcenter.dao.select
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import com.sankhya.util.TimeUtils

class ScanbotParceiroHelper() {
    private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")

    fun salvar(mapValues: Map<String, Any?>, nextStatus: String): Map<String, Any> {
        println("~~~ mapValues => $mapValues ~~~")
        try {
            val campo = mapValues["campo"]!!.toString()
            var valor = mapValues["valor"]
            println("telefone -> ${mapValues["telefone"]}")

            val retorno = mutableMapOf<String, Any>()
            val scanParVO = scanParDAO.findOne("TELEFONE = ?", mapValues["telefone"]!!)

            if (campo == "CODPARC" && valor is String) {
                valor = valor.toBigDecimalOrNull() ?: throw IllegalArgumentException("Valor de CODPARC inválido para BigDecimal")
            }

            scanParDAO.prepareToUpdate(scanParVO)
                .set(campo, valor)
                .set("STATUSCAD", nextStatus)
                .update()

            retorno["status"] = 200
            return retorno
        } catch (e: Exception) {
            throw Exception("Deu problema ao salvar, porque? -> " + e.message.toString())
        }
    }

    fun updateStatus(telefone: String, valor: String): Any {
        val retorno = mutableMapOf<String, Any>()
        var scanParVO = scanParDAO.findOne("TELEFONE = ?", telefone)
        scanParVO.setProperty("STATUSCAD", valor)

        retorno["status"] = 200
        retorno["message"] = "Update feito com sucesso"

        return retorno
    }

    fun updateTimestamp(telefone: String, statusCadastro: String): Any {
        val scanbotParceiroHelper = ScanbotParceiroHelper()
        val scanParVO = scanParDAO.findOne("TELEFONE = ?", telefone)

        val valorASalvar: Map<String, Any?> = mapOf(
            "telefone" to scanParVO.getProperty("TELEFONE").toString(),
            "campo" to "DHALTER",
            "valor" to TimeUtils.getNow()
        )

        scanbotParceiroHelper.salvar(valorASalvar as Map<String, String>, statusCadastro)

        return mapOf(
            "status" to 200,
            "message" to "Obrigado por nos informar, por favor aguarde enquanto nossa equipe analisa o seu cadastro."
        );
    }

    fun buscar(telefone: String):Map<String, Any?> {
        try {
            val params = mapOf("TELEFONE" to telefone)
            val sql = "SELECT TELEFONE, CPF, CODPARC, DTNASC, RG, UFEXPED, EMISSOR, CATEGORIA, VALIDADE, SEGURANCA, PRIMEIRAHAB, DTEMISSAO, PAI, MAE, CPF, CNH, NOME FROM TSBPAR WHERE REPLACE(TELEFONE, ' ', '') = :TELEFONE"
            val result = mutableMapOf<String, Any?>()
            select(sql, params) {
                println("params => $params")
                if(it.next()) {
                    val metaData = it.metaData
                    println("metaData => $metaData")
                    for (i in 1..metaData.columnCount) {
                        result[metaData.getColumnName(i)] = it.getObject(i)
                    }
                }
            }

            return result
        } catch (e: Exception) {
            println("error scanbot helper: $e.message")

            return mapOf(
                "status" to 406,
                "message" to "Erro ao buscar parceiro ${e.message}"
            )
        }
    }

    fun buscarVeiculo(telefone: String):Map<String, Any?> {
        try {
            val params = mapOf("TELEFONE" to telefone)
            val sql = "SELECT PLACA, RENAVAM, CHASSI, MARCAMODELO, ESPECIE, ANOFABRICACAO, ANOMODELO, COR, COMBUSTIVEL, POTENCIACILINDRADA, CODPARC, CPF FROM TSBPAR WHERE REPLACE(TELEFONE, ' ', '') = :TELEFONE"
            val result = mutableMapOf<String, Any?>()
            select(sql, params) {
                it.next()
                val metaData = it.metaData
                for (i in 1..metaData.columnCount) {
                    result[metaData.getColumnName(i)] = it.getObject(i)
                }
            }

            return result
        } catch (e: Exception) {
            return mapOf(
                "status" to 406,
                "message" to "Erro ao buscar parceiro ${e.message}"
            )
        }
    }
}