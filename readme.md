## <span style="color:#6dc96a">Atenção</span>
A **parte 1** é a documentação deste próprio modelo, você deve apagar isso ao iniciar um novo projeto com base neste. A **parte 2** deve ser mantida e completada com a documentação do seu projeto. 

<div style="background-color:#6dc96a;min-width:100%;color: white;font-weight: 600;padding: 3px;" >
    PARTE 1
</div>

## <span style="color:#6dc96a">Objetivo</span>
Servir de modelo para a criação de novas extensões, bem como manter a padronização dos nossos projetos.<br/>

## <span style="color:#6dc96a">Padrões de nomenclatura</span>

<span style="color:#2f3b4e;font-weight:700;">pasta main/resources/nomeprojeto/dd/</span>

Usar o padrão Pascal Case para nomenclatura dos arquivos.
Isso vai facilitar a leitura do projeto e se diferenciar do padrão de criação de funções do projeto.
Para o nome da entidade também será usado o Pascal Case, da seguinte forma:

```
ModeloComposto.xml 
```

```xml
<entity name="DvcModeloComposto" />
<entity name="DvcOutroExemplo" />
<entity name="DvcPadraoNomeEntidade" />
```

<span style="color:#2f3b4e;font-weight:700;"> pasta main/resources/nomeprojeto/parameters/ </span>

Usar o padrão Pascal Case para nomenclatura dos arquivos.
Para o nome do parâmetro é adicionado BH antes do nome.
Adicionar a abreviação do projeto antes da descrição, sendo sempre 3 letras.

DVC + abreviação com 3 letras + nome parâmetro.

```ParameterComposto.xml```

```xml
<parameter
    name="br.com.sankhya.devcenter.xxxxx.nomeparam"
    key="DVCNOMEPARAM1"
    type="boolean"
    cacheable="true"
    required="false"
    default="false"
    description="ABREVIACAO DO PROJETO + Descrição"
    module="B"
/>
```

<span style="color:#2f3b4e;font-weight:700;"> pasta main/resources/datadictionary</span>

Este arquivo possui um extenso padrão de elementos e atributos.
Você pode acessar a documentação de 2 formas: <br/>
1. O que chamamos de _auto complete_, disponível através do .xsd configurado no elemento principal.
2. Através do arquivo readme.md, disponível na raiz dessa pasta.

_Dicas:_ <br />
1. Para ter acesso ao _auto complete_, basta usar o atalho ``ctrl + space``.
2. O atalho ``ctrl + p`` sugere possíveis valores. 
3. O atalho para a documentação é ``ctrl + q``. Lembre-se de posicionar o cursor no elemento.

<span style="color:#2f3b4e;font-weight:700;"> pasta main/resources/META-INF</span>

O padrão pra nome de arquivos é minúsculo com "-" entre palavras. 
Este é o padrão SankhyaOm dos arquivos desta pasta.
Grande parte dos arquivos aqui são "links" para ligar os arquivos da pasta resources.

## <span style="color:#6dc96a">Links Úteis + Conhecimento</span>

1. http://code.sankhya.com.br/desmistificando-o-commit-type-das-entidades-do-dicionario-de-dados/
2. http://code.sankhya.com.br/generator-sankhya-html5/
3. http://code.sankhya.com.br/instalacao-de-uma-extensao-no-sankhya-w/
4. http://code.sankhya.com.br/novo-recurso-que-permite-identificar-origem-do-e-mail-smtp-header/
5. http://code.sankhya.com.br/por-dentro-da-estrutura-de-dados/
6. http://code.sankhya.com.br/7027

<div style="background-color:#6dc96a;min-width:100%;color: white;font-weight: 600;padding: 3px;" >
    PARTE 2
</div>


## <span style="color:#6dc96a">Objetivo do Projeto</span>

<span style="color:#2f3b4e;font-weight:700;"> Parâmetros</span>

1. DVC1

## <span style="color:#6dc96a">Configuração</span>

## <span style="color:#6dc96a">Funcionamento</span>

## <span style="color:#6dc96a">Changelog</span>
- retira arquivo env do arquivo do docker para enviar a perda da base ao alterar o parametro do env.

<div style="position:absolute;right:0;padding:10px">
    <img src="https://www.sankhya.com.br/wp-content/uploads/2021/07/logo-horizontal-primary-color-dark.svg">
</div>
