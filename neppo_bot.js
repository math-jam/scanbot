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
  if (retornoBuscarCadastro.status == statusResult.error) {
    var bearerToken = gerarBearerToken();
    if (bearerToken.status == statusResult.error) {
      return 'Erro ao buscar o bearer token, contate a administradora.';
    }

    params.bearerToken = bearerToken.result.bearerToken;

    var retornoBuscarCadastro = buscarCadastro(params.codSeq);
    params.codSeq = retornoBuscarCadastro.result.codSeq;
  } else {
    var retornoBuscarCadastro = buscarCadastro(params.codSeq);
    params.codSeq = retornoBuscarCadastro.result.codSeq;
  }

  if (msg == '#sair') {
    return '#CLOSE_SESSION' + 'Saindo do bot.'
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

var validaURL = function(url) {
  return url.match(/^https:\/\/files\.tm2digital\.com\/storage\/file\/sankhya\/[0-9]{4}\/[0-1][0-9]\/[0-3][0-9]\/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}\.jpg$/);
}

var salvarCpfCnpj = function (cpfCnpj, codseq) {
  var parceiro = buscarParceiro(cpfCnpj);
  var body = {}

  if (parceiro && parceiro.result && parceiro.result.codigoParceiro) {
    body = {
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
              "CODPARC": {
                "$": parceiro.result.codigoParceiro
              },
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
    }
  } else {
    body = {
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
    }
  }

  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
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
                "$": StatusFotos.docVeic
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
                "$": StatusFotos.frenteVeic
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

var salvarParceiro = function (values) {
  var keyProp = {}

  if (values.CODPARC && values.CODPARC.$) {
    keyProp = {
      "CODPARC": {
        "$": values.CODPARC.$
      }
    }
  }

  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "Parceiro",
          includePresentationFields: "N",
          dataRow: {
            key: keyProp,
            localFields: {
              "ATIVO": {
                "$": "N"
              },
              "CENTRALCAD": {
                "$": "N"
              },
              "TIPPESSOA": {
                "$": "F"
              },
              "NOMEPARC": {
                "$": values.NOME.$
              },
              "CGC_CPF": {
                "$": values.CPF.$
              },
              "TELEFONE": {
                "$": values.TELEFONE.$
              },
              "CODCID": {
                "$": 1
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

var salvarVeiculo = function (values, codparc) {
  try {
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.saveRecord&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.saveRecord",
      requestBody: {
        dataSet: {
          rootEntity: "Veiculo",
          includePresentationFields: "N",
          dataRow: {
            localFields: {
              "ATIVO": {
                "$": "N"
              },
              "VALTRANSP": {
                "$": "N"
              },
              "PLACA": {
                "$": values.PLACA.$
              },
              "CODCID": {
                "$": 1
              },
              "CODPARC": {
                "$": codparc.$
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

var salvarParceiroScanbot = function (codseq, codparc) {
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
              "CODPARC": {
                "$": codparc.$
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
      params.fluxoConversa = false
      return 'Obrigado ' + msg + ', o seu nome foi registrado agora vamos proceguir com o cadastro, nos informe o seu CPF ou CNPJ por favor! [14]';
    }
  } else if (status == StatusCadastroPFPJ.cpf) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, vimos que voce já iniciou o seu cadastro, por favor, nos informe o seu CPF ou seu CNPJ. [14]';
    }

    var cpfvalido = validarCPF(msg);
    if (!cpfvalido) {
      return 'Por favor informe um CPF valido!';
    }

    var dadosCpfCnpj = salvarCpfCnpj(msg, codseq);

    if (dadosCpfCnpj.status == statusResult.success) {
      return 'Obrigado pela informação, agora preciso que nos informe sua CNH por favor!';
    }
  } else if (status == StatusCadastroPFPJ.cnh) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, vimos que voce já iniciou o seu cadastro, por favor, nos informe o CNH.';
    }

    if (msg.length != 11) {
      return 'Por favor informe um CNH valido!';
    }

    var dadosCnh = salvarCnh(msg, codseq);

    if (dadosCnh.status == statusResult.success) {
      return 'Obrigado pela informação, o seu CNH foi registrado agora nos envie uma foto da sua CNH por favor!';
    }
  } else if (status == StatusFotos.cnh) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, vimos que voce já iniciou o seu cadastro, por favor, nos envie a foto da sua CNH.';
    }


    var url = JSON.parse(JSON.stringify(msg));
    if (!validaURL(url)) {
      return 'Por favor nos envie uma imagem válida!';
    }

    var dadosDoc = salvarFotoDoc(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela informação, agora precisamos de uma foto do seu rosto segurando o documento, por favor!';
    }
  } else if (status == StatusFotos.rosto) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos envie a foto do seu rosto.';
    }
    var url = JSON.parse(JSON.stringify(msg));
    if (!validaURL(url)) {
      return 'Por favor nos envie uma imagem válida!';
    }
    var dadosDoc = salvarFotoRosto(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela foto, precisamos que nos informe a placa do seu veículo por favor!';
    }

  } else if (status == StatusCadastroVeiculo.placa) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos envie a placa do seu veículo.';
    }

    var reg = /^[A-Z]{3}[0-9][0-9A-Z][0-9]{2}$/;
    if (!reg.test(msg)) {
      return 'Por favor informe uma placa de veículo válida! Exemplos: AAA1A11 ou AAA1111';
    }

    var dadosPlaca = salvarPlaca(msg, codseq);

    if (dadosPlaca.status == statusResult.success) {
      return 'Obrigado, a placa do seu veículo foi registrada com sucesso, nos informe agora, seu veículo é próprio?\nResponda apenas com Sim ou Não';
    }
  } else if (status == StatusCadastroVeiculo.veiculoProprio) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos informe se seu veículo é proprio ou não.\nResponda apenas com Sim ou Não';
    }

    var resp = msg.trim()

    if(resp == "NÃO" || resp == "NAO" || resp == "Nao" || resp == "NaO" || resp == "N" || resp == "n") {
      resp = "NAO"
    } else if(resp == "SIM" || resp == "Sim" || resp == "SiM" || resp == "S" || resp == "s") {
      resp = "SIM"
    } else {
      return "Por favor, responda SIM ou NÃO"
    }

    var dadosVeiculoProprio = salvarVeiculoProprio(resp, codseq);

    if (dadosVeiculoProprio.status == statusResult.success) {
      return 'Ok, precisamos da foto do documento do veículo, por favor!';
    }

  } else if (status == StatusFotos.docVeic) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos envie a foto do documento do veículo.';
    }

    var url = JSON.parse(JSON.stringify(msg));
    if (!validaURL(url)) {
      return 'Por favor nos envie uma imagem válida!';
    }
    var dadosDoc = salvarDocVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'Obrigado pela foto, agora precisamos de uma foto da frente do veículo, por favor!';
    }
  } else if (status == StatusFotos.frenteVeic) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos envie umda foto da frente do veículo.';
    }
    var url = JSON.parse(JSON.stringify(msg));
    if (!validaURL(url)) {
      return 'Por favor nos envie uma imagem válida!';
    }
    var dadosDoc = salvarFrenteVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      return 'E por fim precisamos de uma foto da traseira do veículo, por favor!';
    }

  } else if (status == StatusFotos.traseiraVeic) {
    if(params.fluxoConversa){
      params.fluxoConversa = false
      return 'Olá, por favor, nos envie umda foto da traseira do veículo.';
    }
    var url = JSON.parse(JSON.stringify(msg));
    if (!validaURL(url)) {
      return 'Por favor nos envie uma imagem válida!';
    }
    var dadosDoc = salvarTraseriaVeic(url, codseq);

    if (dadosDoc.status == statusResult.success) {
      var retornoParc = salvarParceiro(dadosDoc.result.entities.entity);

      if (retornoParc.status == statusResult.success) {
        var retornoVeiculo = salvarVeiculo(dadosDoc.result.entities.entity, retornoParc.result.entities.entity.CODPARC);

        if (retornoVeiculo.status == statusResult.success) {

          var retornoSalvarParceiroScanbot = salvarParceiroScanbot(codseq, retornoParc.result.entities.entity.CODPARC);
          if (retornoSalvarParceiroScanbot.status == statusResult.success) {
            return '#SEND_AND_CLOSE' + 'Parabéns, o cadastro foi realizado com sucesso, caso tenha alguma dúvida entre em contato com a administradora, para verificar os status do seu cadastro.'
          } else {
            return '#SEND_AND_CLOSE' + 'Erro ao salvar status do usuário, por favor entre em contato com a administradora.'
          }
        } else {
          return '#SEND_AND_CLOSE' + 'Houve um erro ao cadastrar o veículo, entre em contato com a administradora, para verificar os status do seu cadastro.'
        }
      } else {
        return '#SEND_AND_CLOSE' + 'Houve um erro ao cadastrar o parceiro, entre em contato com a administradora, para verificar os status do seu cadastro.';
      }
    }
  } else {
    return '#SEND_AND_CLOSE' + 'Os dados já foram registrados com sucesso, caso tenha alguma dúvida entre em contato com a administradora, para verificar os status do seu cadastro.'
  }
};

function initParams() {
  params.init = false;
  params.fluxoConversa = true;
  params.botName = 'Scanbot';
  params.companyName = 'Sankhya';

  params.security = {
    sankhya: {
      token: 'fc97eef9-d639-40fe-8eb4-339ee21c617e',
      appKey: '3a51f41d-ebc4-429f-a888-55ff9fb48d34',
      username: 'matheus.andrade@sankhya.com.br',
      password: 'Sankhya2024@'
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
    var telefone = params.fromUser.split("_")[1];
    var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.loadRecords&outputType=json";
    var body = {
      serviceName: "CRUDServiceProvider.loadRecords",
      requestBody: {
        dataSet: {
          rootEntity: "Parceiro",
          includePresentationFields: "N",
          tryJoinedFields: "true",
          parallelLoader: "true",
          offsetPage: "0",
          criteria: {
            expression: {
              "$": "this.TELEFONE = ?"
            },
          },
          parameter: [
            {
              "$": telefone,
              "type": "N"
            }
          ],
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

var buscarParceiro = function (cpf) {
  var url = "https://api.sankhya.com.br/gateway/v1/mge/service.sbr?serviceName=CRUDServiceProvider.loadRecords&outputType=json";
  var body = {
    serviceName: "CRUDServiceProvider.loadRecords",
    requestBody: {
      dataSet: {
        rootEntity: "Parceiro",
        includePresentationFields: "N",
        tryJoinedFields: "true",
        parallelLoader: "true",
        offsetPage: "0",
        criteria: {
          expression: {
            $: "CGC_CPF = ?"
          },
          parameter: [
            {
              $: cpf,
              type: "S"
            }
          ]
        },
        entity: [
          {
            path: "",
            fieldset: {
              list: "CODPARC, NOMEPARC, NUMEND, COMPLEMENTO, CEP, CGC_CPF"
            }
          }
        ]
      }
    }
  }
  var response = restClient.postWithHeader(
    url,
    JSON.stringify(body),
    JSON.stringify({
      'Content-Type': 'application/json',
      'appkey': params.security.appKey,
      'Authorization': 'Bearer ' + params.bearerToken
    }));
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

    var codigoParceiro = justOne ? responseBody.entities.entity.f0.$ : responseBody.entities.entity[0].f0.$;
    var nomeParceiro = justOne ? responseBody.entities.entity.f1.$ : responseBody.entities.entity[0].f1.$;

    return {
      status: statusResult.success,
      result: {
        codigoParceiro: codigoParceiro,
        nomeParceiro: nomeParceiro
      }
    };
  }
}

var buscarCadastro = function () {
  var telefone = params.fromUser.split("_")[1];
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
                "$": telefone,
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

function validarCPF(cpf) {
  cpf = cpf.replace(/[^\d]+/g, '');
  if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
    return false;
  }

  var soma = 0;
  var resto;

  for (var i = 1; i <= 9; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;

  if ((resto === 10) || (resto === 11)) {
    resto = 0;
  }

  if (resto !== parseInt(cpf.substring(9, 10))) {
    return false;
  }

  soma = 0;

  for (var i = 1; i <= 10; i++) {
    soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }

  resto = (soma * 10) % 11;

  if ((resto === 10) || (resto === 11)) {
    resto = 0;
  }

  if (resto !== parseInt(cpf.substring(10, 11))) {
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
