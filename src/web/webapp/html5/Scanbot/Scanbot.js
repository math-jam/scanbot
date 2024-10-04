angular.module('ScanbotApp', ['snk', 'dvc.services']).controller('ScanbotController', ScanbotController);

function ScanbotController(
	ServiceProxy,
	MessageUtils,
	Criteria,
	SkApplicationInstance,
	ObjectUtils,
	DateUtils,
	SkApplication,
	PopUpParameter,
	FinalizarSPService,
	WhatsappServiceSPService
) {
	let self = this;
	self.dynaform;
	self.dataset;
	self.callWhatsapp = callWhatsapp;
	self.finalizarCadastro = finalizarCadastro;
	self.onDynaformLoaded = onDynaformLoaded;
	self.btnWhatsapp = true;
	self.btnFinalizar = true;
	self.disabledForm = false;
	self.status = false;
	self.init = init;
	self.saveEvent = "dataset.observer.EVENT_SAVE_AVOID"
	self.editEvent = "dataset.observer.EVENT_INSERT_MODE"

	function init() {
		self.dataset.addAllObserverEventsListener(function (event, parameters) {
		    const row = self.dataset.getCurrentRowAsObject("STATUSCAD, STATUS_CENTRAL")


            if (event === self.editEvent) {
              self.disabledForm = false;
            }

            if (event === "dataset.observer.EVENT_EDITION_STARTED") {
                self.btnWhatsapp = true
                self.btnFinalizar = true
            }

           self.dynaform.setFieldProperty("STATUSCAD,CODSEQ,STATUS_CENTRAL,CODPAIS,TELEFONE,CPF,NOME,DTNASC,RG,UFEXPED,EMISSOR,CNH,CATEGORIA,VALIDADE,SEGURANCA,PRIMEIRAHAB,DTEMISSAO,PAI,MAE,FOTO,FOTOCNH,PLACA,RENAVAM,VEICPROP,CHASSI,MARCAMODELO,ESPECIE,ANOFABRICACAO,ANOMODELO,COR,COMBUSTIVEL,POTENCIACILINDRADA,CIDADE,FOTODOCVEIC,FOTOFRENVEIC,FOTOTRASVEIC", "enabled", !self.disabledForm);
        });

		self.dataset.addLineChangeListener(function (newIndex) {
			if (self.dataset.size() > 0 && !self.dataset.isInsertionMode()) {
                const row = self.dataset.getCurrentRowAsObject("STATUSCAD, CODPARC, CODVEICULO, STATUS_CENTRAL")
                const codveic = !!row['CODVEICULO']
                const codparc = !!row['CODPARC']
                const statusCad = row["STATUSCAD"]
                const statuscentral = row["STATUS_CENTRAL"]

                const liberada = statuscentral === "5"
                const aguardando = statusCad === "AGUARDANDO"
                const finalizado = statusCad === "FINALIZADO"

                self.disabledForm = row["STATUSCAD"] === "FINALIZADO" && row["STATUS_CENTRAL"] === "5";
                self.btnWhatsapp = row["STATUSCAD"] !== "AGUARDANDO";

                if(finalizado && liberada && codparc && codveic) {
                    self.btnFinalizar = true
                } else {
                    self.btnFinalizar = false
                }
            }
		});

		self.dataset.addInsertionModeListener(function (indexBeforeInsert, dataset) {
			if (self.dataset.isInsertionMode()) {
				self.btnWhatsapp = false
			}

			dataset.setFieldValue("STATUSCAD", "AGUARDANDO");
			dataset.setFieldValue("STATUS_CENTRAL", "0");
		});
    }

    function onDynaformLoaded(dynaform, dataset) {
        if (dataset.getEntityName() === "ScanbotParceiro") {
            self.dynaform = dynaform;
            self.dataset = dataset;
        }

        init()
    }

	function callWhatsapp() {
		const row = self.dataset.getCurrentRowAsObject("TELEFONE, CODPAIS")
        const telefone = `${row["CODPAIS"]}${row["TELEFONE"]}`.replace(/\s/g, '')

        const requestBody = {
            telefone: telefone
        }

        WhatsappServiceSPService.chamarUsuario(requestBody).then((response) => {
            MessageUtils.showInfo(MessageUtils.TITLE_INFORMATION, "Foi enviada uma mensagem para o Motorista continuar o cadastro.")
        })
	}

	function finalizarCadastro() {
		const rows = self.dataset.getCurrentRowAsObject("TELEFONE, CODPAIS, CPF, NOME, CODCID, PLACA")

        if(!rows["CPF"] && !rows["NOME"]){
            return MessageUtils.showInfo(MessageUtils.TITLE_INFORMATION, "Campos obrigatórios para salvar Parceiro não preenchidos.\n\nCPF\nNOME")
        } else if(!rows["CODCID"] && !rows["PLACA"]) {
            return MessageUtils.showInfo(MessageUtils.TITLE_INFORMATION, "Campos obrigatórios para salvar Veículo não preenchidos.\n\nCÓDIGO DA CIDADE\nPLACA\nRENAVAM")
        }

        const requestBody = {
            telefone: rows["TELEFONE"]
        }

		FinalizarSPService.init(requestBody).then((response) => {
		    MessageUtils.showInfo(MessageUtils.TITLE_INFORMATION, response.message)
		})
	}
}