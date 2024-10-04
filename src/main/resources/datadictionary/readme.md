## Exemplo de Arquivo

O objetivo deste arquivo vai te ajudar a entender e usar cada tag dispon�vel no metadado Sankhya. 
Al�m deste arquivo, temos os "autocompletes" no momento que voc�s estiver redigindo o arquivo. 

## Para Tabela:

```xml
<!-- Este � o n� raiz. N�o h� muito o que se fazer aqui, mas ele � obrigat�rio. O links xsd s�o os respons�veis por disponibilizar a documenta��o e os autocompletes. -->
<metadados xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://backend.sankhyatec.mgcloud.net.br/files/metadados.xsd" xsi:schemaLocation="http://backend.sankhyatec.mgcloud.net.br/files/metadados.xsd">
    <!-- Este elemento � o respons�vel por detalhar uma tabela que deve ser criada. -->
	<table name="" sequenceType="" sequenceField="" nativo="">
        <!-- Descri��o da tabela -->
		<description><![CDATA[]]></description> 
        <!-- Lista de primarys keys -->
		<primaryKey>
			<field name="NOMEDAPK1"/>
			<field name="NOMEDAPK2"/>
		</primaryKey>
        <!-- Lista de inst�ncias da tabela -->
		<instances> 
            <!-- Este elemento � o respons�vel por detalhar a inst�ncia da tabela. Consulte o padr�o de nomes. -->
			<instance name="" nativo="">
                <description><![CDATA[]]></description>
                <documentation><![CDATA[]]></documentation>
                <!-- Este elemento � o respons�vel por detalhar a rela��o das inst�ncias -->
                <relationShip>
                    <relation entityName="" insert="" update="" remove="" relation="" ignoreFK="">
                        <expression><![CDATA[]]></expression>
                        <fields>
                            <field localName="" targetName="" />
                        </fields>
                    </relation>
                </relationShip>
                <!-- Este elemento � respons�vel por cadastrar as telas da extens�o -->
                <controls>
                    <control resourceId="" paramMenuAtivo="" contexto="">
                        <description><![CDATA[]]></description>
                        <url><![CDATA[]]></url>
                        <acessos acronym="" sequence="">
                            <description><![CDATA[]]></description>
                        </acessos>
                    </control>
                </controls>
			</instance>
		</instances>
        <!-- Lista de campos da tabela -->
		<fields>
			<field name="" dataType="" presentationType="" tamanho="" calculated="" allowSearch="" allowDefault="" visibleOnSearch="" isPresentation="" nullable="" nativo="" defaultValue="" order="" >
                <description><![CDATA[]]></description>
                <documentation><![CDATA[]]></documentation>
                <expression><![CDATA[SQL ou JavaBeanShell]]></expression>
                <fieldOptions>
                    <option value="" nativo=""><![CDATA[]]></option>
                </fieldOptions>
                <properties>
                    <prop name="" nativo=""><![CDATA[]]></prop>
                </properties>
            </field>
		</fields>
    </table>
</metadados>
```

## Para View:
```xml
<metadados>
    <view name="" sequenceType="" sequenceField="" nativo="">
        <sqlMSSQL><![CDATA[INSERT...]]></sqlMSSQL>
        <sqlOracle><![CDATA[INSERT...]]></sqlOracle>
    </view>
</metadados>
```

## Para Script
```xml
<metadados>
    <script nomeObjeto="" tipoObjeto="" nomeTabela="" executar="">
        <sqlMSSQL><![CDATA[INSERT...]]></sqlMSSQL>
        <sqlOracle><![CDATA[INSERT...]]></sqlOracle>
    </script>
</metadados>
```