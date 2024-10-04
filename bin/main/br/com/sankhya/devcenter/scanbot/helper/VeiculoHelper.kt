package br.com.sankhya.devcenter.scanbot.helper

import br.com.sankhya.devcenter.scanbot.utils.BuscarCodigoCidade
import br.com.sankhya.jape.wrapper.JapeFactory
import br.com.sankhya.jape.wrapper.JapeWrapper
import java.math.BigDecimal

class VeiculoHelper() {
    private val veiDAO: JapeWrapper = JapeFactory.dao("Veiculo")
    private val scanbotParceiroHelper = ScanbotParceiroHelper()

    fun salvar(telefone: String): BigDecimal {
        try {
            var combustivelCode = ""
            val campos = scanbotParceiroHelper.buscarVeiculo(telefone)
            var codcid = 1.toBigDecimal()
            val codparc = campos["CODPARC"] ?: throw Exception("O cadastro não possui um código de parceiro!")
            val anomod = (campos["ANOMODELO"]?.toString()?.toBigDecimalOrNull()
                ?: 0.toBigDecimal())
            val anofab = (campos["ANOFABRICACAO"]?.toString()?.toBigDecimalOrNull()
                ?: 0.toBigDecimal())

            if(campos["COMBUSTIVEL"] != null) {
                val combustivelOriginal = campos["COMBUSTIVEL"]?.toString()
                    ?: "Gasolina"
                combustivelCode = determinarCodigoCombustivel(combustivelOriginal)
            }

            val veiVO = veiDAO.create()
                .set("PLACA", campos["PLACA"])
                .set("RENAVAM", campos["RENAVAM"])
                .set("CHASSIS", campos["CHASSI"])
                .set("ANOFABRIC", anofab)
                .set("ANOMOD", anomod)
                .set("MARCAMODELO", campos["MARCAMODELO"])
                .set("ESPECIETIPO", campos["ESPECIE"])
                .set("COR", campos["COR"])
                .set("COMBUSTIVEL", combustivelCode)
                .set("CAPPOTCIL", campos["POTENCIACILINDRADA"])
                .set("CODPARC", codparc)
                .set("ATIVO", "S")
//                .set("VALTRANSP", "N")
                .set("CODCID", codcid)
                .save()

            val codvei = veiVO.asBigDecimal("CODVEICULO")
            return codvei
        } catch (e: Exception) {
            throw Exception("Houve um problema ao salvar veículo: " + e.message.toString())
        }
    }
    /**
     * Determina o código de combustível com base na descrição fornecida.
     * Regras:
     * - Contém "álcool" -> "A"
     * - Contém "álcool" e "gasolina" -> "F"
     * - Contém "gasolina" -> "G"
     * - Contém "diesel" -> "D"
     * @param combustivel Descrição do combustível
     * @return Código correspondente
     * @throws Exception se o tipo de combustível for desconhecido
     */
    private fun determinarCodigoCombustivel(combustivel: String): String {
        val combustivelLower = combustivel.toLowerCase().trim()
        val contemAlcool = combustivelLower.contains("alcool")
        val contemGasolina = combustivelLower.contains("gasolina")
        val contemDiesel = combustivelLower.contains("diesel")

        return when {
            contemAlcool && contemGasolina -> "F"
            contemAlcool -> "A"
            contemGasolina -> "G"
            contemDiesel -> "D"
            else -> throw Exception("Tipo de combustível desconhecido: $combustivel")
        }
    }
}