var message = function (msg) {
  if (!params.init) {
    initParams();
    var bearerToken = gerarBearerToken();
    if (bearerToken.status == statusResult.error) {
      return 'Erro ao buscar o bearer token, contate a administradora.';
    }

    params.bearerToken = bearerToken.result.bearerToken;
    params.init = true;
  }

  var retornoBuscarCadastro = buscarCadastro(params.codSeq);
  params.codSeq = retornoBuscarCadastro.result.codSeq;

  if (msg == '#sair') {
    return 'Saindo do bot.'
  }

  if (retornoBuscarCadastro.status == statusResult.success) {
    return inciarFluxo(retornoBuscarCadastro.result.statusCod, msg, retornoBuscarCadastro.result.codSeq);
  } else {
    return 'Erro ao buscar o status do cadastro, contate a administradora.';
  }
};

var salvarNome = function (nome, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "NOME": {
                "$": nome
              },
              "STATUSCAD": {
                "$": StatusCadastroPFPJ.cpf
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarCpfCnpj = function (cpfCnpj, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "CPF": {
                "$": cpfCnpj
              },
              "STATUSCAD": {
                "$": StatusCadastroPFPJ.cnh
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarCnh = function (cnh, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "CNH": {
                "$": cnh
              },
              "STATUSCAD": {
                "$": StatusFotos.cnh
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarFotoDoc = function (urlDoc, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "FOTOCNH": {
                "$": urlDoc
              },
              "STATUSCAD": {
                "$": StatusFotos.rosto
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarFotoRosto = function (urlFoto, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "FOTO": {
                "$": urlFoto
              },
              "STATUSCAD": {
                "$": StatusCadastroVeiculo.placa
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarPlaca = function (placa, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "PLACA": {
                "$": placa
              },
              "STATUSCAD": {
                "$": StatusCadastroVeiculo.veiculoProprio
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarVeiculoProprio = function (veiculoProprio, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "VEICPROP": {
                "$": veiculoProprio
              },
              "STATUSCAD": {
                "$": StatusCadastroVeiculo.fotoDocVeic
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarDocVeic = function (urlDoc, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "FOTODOCVEIC": {
                "$": urlDoc
              },
              "STATUSCAD": {
                "$": StatusCadastroVeiculo.fotoDocVeic
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarFrenteVeic = function (urlFoto, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "FOTOFRENVEIC": {
                "$": urlFoto
              },
              "STATUSCAD": {
                "$": StatusFotos.traseiraVeic
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var salvarTraseriaVeic = function (urlFoto, codseq) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          includePresentationFields: "N",
          dataRow: {
            key: {
              "CODSEQ": {
                "$": codseq
              }
            },
            localFields: {
              "FOTOTRASVEIC": {
                "$": urlFoto
              },
              "STATUSCAD": {
                "$": StatusCadastroPFPJ.finalizado
              }
            }
          },
          entity: {
            fieldset: {
              list: "*"
            }
          }
        }
      }
    };

    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );

    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;

      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Erro ao salvar as informações, por favor entrar em contato com a administração da empresa."
        };
      }

      return {
        status: statusResult.success,
        result: responseBody
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
}

var inciarFluxo = function (status, msg, codseq) {
  if (status == StatusCadastroPFPJ.nome) {
    var dadosUsuario = salvarNome(msg, codseq);

    if (dadosUsuario.status == statusResult.success) {
      return 'Obrigado ' + msg + ', o seu nome foi registrado agora vamos proceguir com o cadastro, nos informe o seu CPF ou CNPJ por favor!';
    }
  } else if (status == StatusCadastroPFPJ.cpf) {
    var dadosCpfCnpj = salvarCpfCnpj(msg, codseq);

    if (dadosCpfCnpj.status == statusResult.success) {
      return 'Obrigado pela informação, agora preciso que nos informe sua CNH por favor!';
    }
  } else if (status == StatusCadastroPFPJ.cnh) {
    var dadosCnh = salvarCnh(msg, codseq);

    if (dadosCnh.status == statusResult.success) {
      return 'Obrigado pela informação, o seu CNH foi registrado agora nos envie uma foto da sua CNH por favor!';
    }
  } else if(status == StatusFotos.cnh) {
    var url = JSON.stringify(msg);
    var dadosDoc = salvarFotoDoc(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela informação, agora precisamos de uma foto do seu rosto segurando o documento, por favor!';
    }
  } else if (StatusFotos.rosto) {
    var url = JSON.stringify(msg);
    var dadosDoc = salvarFotoRosto(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela foto, precisamos que nos informe a placa do seu veículo por favor!';
    }

  } else if (status == StatusCadastroVeiculo.placa) {
    var dadosPlaca = salvarPlaca(msg, codseq);

    if (dadosPlaca.status == statusResult.success) {
      return 'Obrigado, a placa do seu veículo foi registrada com sucesso, nos informe agora, seu veículo é próprio?';
    }
  } else if (status == StatusCadastroVeiculo.veiculoProprio) {
    var dadosVeiculoProprio = salvarVeiculoProprio(msg, codseq);

    if (dadosVeiculoProprio.status == statusResult.success) {
      return 'Ok, precisamos da foto do documento do veículo, por favor!';
    }
  } else if (status == StatusFotos.docVeic) {
    var url = JSON.stringify(msg);
    var dadosDoc = salvarDocVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela foto, agora precisamos de uma foto da frente do veículo, por favor!';
    }

  } else if (status == StatusFotos.frenteVeic) {
    var url = JSON.stringify(msg);
    var dadosDoc = salvarFrenteVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'E por fim precisamos de uma foto da traseira do veículo, por favor!';
    }

  } else if (status == StatusFotos.traseiraVeic) {
    var url = JSON.stringify(msg);
    var dadosDoc = salvarTraseriaVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pelas informações, agora seu registro está com nossa equipe, qualquer dúvida entre em contato com a administração da empresa!';
    }
  } else {
    return 'Os dados já foram registrados com sucesso, caso tenha alguma dúvida entre em contato com a administradora, para verificar os status do seu cadastro.'
  }
};

function initParams() {
  params.init = false;
  params.botName = 'Scanbot';
  params.companyName = 'Sankhya';
  params.empresa = {
    codigoEmpresa: "1"
  };

  params.flow = 'FOTOFACE';

  params.veiculo = {
    tipo: '',
    proprio: '',
    placa: '',
  };

  params.security = {
    sankhya: {
      token: '234d5bf6-fbbb-4353-a39f-d1ee76b3b172',
      appKey: '3a51f41d-ebc4-429f-a888-55ff9fb48d34',
      username: 'matheus.andrade@sankhya.com.br',
      password: '_Arvore02@'
    },
  };
}

var gerarBearerToken = function () {
  try {
    var url = "https://api.sankhya.com.br/login";
    var response = restClient.postWithHeader(
      url,
      "{}",
      JSON.stringify({
        'Content-Type': 'application/json',
        'token': params.security.sankhya.token,
        'appkey': params.security.sankhya.appKey,
        'username': params.security.sankhya.username,
        'password': params.security.sankhya.password,
      })
    );
    response = JSON.parse(response);
    if (response.error !== null) {
      return {
        status: statusResult.error,
        error: errorType.forbidden,
        description: response.error
      };
    } else {
      return {
        status: statusResult.success,
        result: response
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
};

var buscarConfiguracoes = function () {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.loadRecords&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.loadRecords",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotConfig",
          includePresentationFields: "N",
          tryJoinedFields: "true",
          parallelLoader: "true",
          offsetPage: "0",
          criteria: {
            expression: {
              $: "CODCONF = 1"
            },
          },
          entity: [
            {
              path: "",
              fieldset: {
                list: "VEICULO, NOMEEMP"
              }
            }
          ]
        }
      }
    };
    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );
    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;
      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Parceiro não encontrado"
        };
      }

      var justOne = responseBody.entities.total == "1";

      //Preciso verificar esse retorno!!!
      var temVeiculo = justOne ? responseBody.entities.entity.f0.$ : responseBody.entities.entity[0].f0.$;
      var nomeDaEmpresa = justOne ? responseBody.entities.entity.f1.$ : responseBody.entities.entity[0].f1.$;

      params.veiculo = temVeiculo;
      params.companyName = nomeDaEmpresa;

      return {
        status: statusResult.success,
        result: {
          temVeiculo: temVeiculo,
          nomeDaEmpresa: nomeDaEmpresa
        }
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
};

var buscarCadastro = function () {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.loadRecords&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.loadRecords",
      requestBody: {
        dataSet: {
          rootEntity: "ScanbotParceiro",
          offsetPage: 0,
          entity: [
            {
              path: "",
              fieldset: {
                list: "CODSEQ, STATUSCAD"
              }
            }
          ],
          criteria: {
            expression: {
              "$": "this.TELEFONE = ?"
            },
            parameter: [
              {
                "$": "34999193828",
                "type": "N"
              }
            ]
          }
        }
      }
    };
    var response = restClient.postWithHeader(
      url,
      JSON.stringify(body),
      JSON.stringify({
        'Content-Type': 'application/json',
        'appkey': params.security.sankhya.appKey,
        'Authorization': 'Bearer ' + params.bearerToken
      })
    );
    response = JSON.parse(response);

    if (response.status != "1") {
      return {
        status: statusResult.error,
        error: errorType.internal_error,
        description: response.statusMessage
      };
    } else {
      var responseBody = response.responseBody;
      if (responseBody.entities.total == "0") {
        return {
          status: statusResult.error,
          error: errorType.not_found,
          description: "Parceiro não encontrado"
        };
      }

      var codSeq = responseBody.entities.entity.f0.$;
      var statusCod = responseBody.entities.entity.f1.$;

      return {
        status: statusResult.success,
        result: {
          codSeq: codSeq,
          statusCod: statusCod
        }
      };
    }
  } catch (err) {
    return {
      status: statusResult.error,
      error: errorType.internal_error,
      description: err.message
    };
  }
};

function validarCNPJ(cnpj) {
  cnpj = cnpj.replace(/[^\d]+/g, '');
  if (cnpj.length !== 14 || /^(\d)\1{13}$/.test(cnpj)) {
    return false;
  }

  var tamanho = cnpj.length - 2;
  var numeros = cnpj.substring(0, tamanho);
  var digitos = cnpj.substring(tamanho);
  var soma = 0;
  var pos = tamanho - 7;

  for (var i = tamanho; i >= 1; i--) {
    soma += numeros.charAt(tamanho - i) * pos--;
    if (pos < 2) {
      pos = 9;
    }
  }

  var resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;

  if (resultado != digitos.charAt(0)) {
    return false;
  }

  tamanho = tamanho + 1;
  numeros = cnpj.substring(0, tamanho);
  soma = 0;
  pos = tamanho - 7;

  for (var i = tamanho; i >= 1; i--) {
    soma += numeros.charAt(tamanho - i) * pos--;
    if (pos < 2) {
      pos = 9;
    }
  }

  resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;

  if (resultado != digitos.charAt(1)) {
    return false;
  }

  return true;
}

var StatusCadastroPFPJ = {
  nome: "NOME",
  cpf: "CPFCNPJ",
  cnh: "CNH",
  finalizado: "FINALIZADO"
}

var StatusCadastroVeiculo = {
  placa: "PLACA",
  veiculoProprio: "VEICPROP",
}

var StatusFotos = {
  rosto: "FOTO",
  cnh: "FOTOCNH",
  docVeic: "FOTODOCVEIC",
  frenteVeic: "FOTOFRENVEIC",
  traseiraVeic: "FOTOTRASVEIC"
}

var statusResult = {
  error: "Error",
  success: "Success"
}

var errorType = {
  not_found: "Não encontrado",
  internal_error: "Erro interno",
  forbidden: "Falha de permissão"
}
