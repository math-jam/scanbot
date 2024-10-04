//package br.com.sankhya.devcenter.scanbot.action
//
//import br.com.sankhya.devcenter.sankhyaw.services.boot.BotaoAcao
//import br.com.sankhya.devcenter.sankhyaw.services.boot.PromptParam
//import br.com.sankhya.devcenter.sankhyaw.services.boot.enums.BotaoControlaAcesso
//import br.com.sankhya.devcenter.sankhyaw.services.boot.enums.BotaoControlaType
//import br.com.sankhya.devcenter.sankhyaw.services.boot.enums.BotaoRefresh
//import br.com.sankhya.devcenter.sankhyaw.services.boot.enums.BotaoTransacao
//import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava
//import br.com.sankhya.extensions.actionbutton.ContextoAcao
//import br.com.sankhya.jape.wrapper.JapeFactory
//import br.com.sankhya.jape.wrapper.JapeWrapper
//import com.sankhya.util.TimeUtils
//import br.com.sankhya.devcenter.scanbot.services.EnviarWhatsapp
//import br.com.sankhya.devcenter.scanbot.utils.toIso
//import br.com.sankhya.devcenter.scanbot.utils.toUtf8
//import java.util.*
//
//@BotaoAcao(
//    descricao = "Cadastro via Whatsapp",
//    instancia = "Parceiro",
//    controlaAcesso = BotaoControlaAcesso.SIM,
//    modoRefresh = BotaoRefresh.SEM_REFRESH,
//    transacao = BotaoTransacao.AUTOMATICA,
//    parametros = [
//        PromptParam(name = "TELEFONE", paramType = BotaoControlaType.Texto, label = "Telefone", required = true),
//        PromptParam(name = "NOME", paramType = BotaoControlaType.Texto, label = "Nome", required = false),
//        PromptParam(name = "CPF", paramType = BotaoControlaType.Texto, label = "CPF", required = false)
//    ]
//)
//class CadastroMotoristaWhatsapp : AcaoRotinaJava {
//    private val parDAO: JapeWrapper = JapeFactory.dao("Parceiro")
//    private val scanParDAO: JapeWrapper = JapeFactory.dao("ScanbotParceiro")
//    private val scanConfigDAO: JapeWrapper = JapeFactory.dao("ScanbotConfig")
//
//    override fun doAction(contexto: ContextoAcao) {
//        val telefone = contexto.getParam("TELEFONE").toString()
//        val cpfCnpj = (contexto.getParam("CPF")?: "").toString()
//        val nome = (contexto.getParam("NOME")?: "").toString()
//
//        var parVO = parDAO.findOne("CGC_CPF = ?", cpfCnpj)
//        var scanVO = scanParDAO.findOne("TELEFONE = ?", telefone)
//
//        if(scanVO != null) {
//            throw Exception("Ja existe um usu\u00e1rio para este telefone nos parceiros do Scanbot.")
//        }
//
//        var motoristaNaBase = true
//        if (parVO != null) {
//            motoristaNaBase = contexto.confirmarSimNao(
//                "Scanbot",
//                "J\u00e1 existe um motorista cadastrado para este CPF. Deseja continuar?",
//                1
//            );
//            if (!motoristaNaBase) return
//        }
//
//        parVO = parDAO.findOne("(TELEFONE = ? OR FAX = ?) ", telefone, telefone)
//
//        val ocruVO = scanConfigDAO.findOne("CODCONF = 1")
//
//        var simNao = false
//        if (parVO != null) {
//            simNao = contexto.confirmarSimNao("Scanboot", "Este telefone j\u00e1 est\u00e1 cadastrado para um usu\u00e1rio. Deseja continuar?", 1);
//        }
//
//        val ocr = if (ocruVO.asString("OCR") == "S") "A" else "M"
//
//        val fluid = scanParDAO.create()
//            .set("NOME", nome)
//            .set("TELEFONE", telefone)
//            .set("CPF", cpfCnpj)
//            .set("STATUSCAD", "AGUARDANDO")
//
//            if(simNao) {
//                fluid.set("CODPARC", parVO?.asBigDecimal("CODPARC"))
//            }
//
//            fluid.set("CODUSU", contexto.usuarioLogado)
//            fluid.set("DHALTER", TimeUtils.getNow())
//            fluid.set("STATUS_CENTRAL", "0")
//            fluid.set("TIPCAD", ocr)
//            fluid.save()
//
//        try {
//            val retornoMsg = EnviarWhatsapp().chamarUsuario(contexto)
//            var mensagem = "WhatsApp enviado para o Motorista. C\u00f3digo: $retornoMsg"
//
//            contexto.setMensagemRetorno(mensagem)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            contexto.setMensagemRetorno("Erro ao enviar WhatsApp para o Motorista.")}
//    }
//}
