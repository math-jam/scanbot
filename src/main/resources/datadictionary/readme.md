## Exemplo de Arquivo

O objetivo deste arquivo vai te ajudar a entender e usar cada tag disponível no metadado Sankhya. 
Além deste arquivo, temos os "autocompletes" no momento que vocês estiver redigindo o arquivo. 

## Para Tabela:

```xml
<!-- Este é o nó raiz. Não há muito o que se fazer aqui, mas ele é obrigatório. O links xsd são os responsáveis por disponibilizar a documentação e os autocompletes. -->
<metadados xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://backend.sankhyatec.mgcloud.net.br/files/metadados.xsd" xsi:schemaLocation="http://backend.sankhyatec.mgcloud.net.br/files/metadados.xsd">
    <!-- Este elemento é o responsável por detalhar uma tabela que deve ser criada. -->
	<table name="" sequenceType="" sequenceField="" nativo="">
        <!-- Descrição da tabela -->
		<description><![CDATA[]]></description> 
        <!-- Lista de primarys keys -->
		<primaryKey>
			<field name="NOMEDAPK1"/>
			<field name="NOMEDAPK2"/>
		</primaryKey>
        <!-- Lista de instâncias da tabela -->
		<instances> 
            <!-- Este elemento é o responsável por detalhar a instância da tabela. Consulte o padrão de nomes. -->
			<instance name="" nativo="">
                <description><![CDATA[]]></description>
                <documentation><![CDATA[]]></documentation>
                <!-- Este elemento é o responsável por detalhar a relação das instâncias -->
                <relationShip>
                    <relation entityName="" insert="" update="" remove="" relation="" ignoreFK="">
                        <expression><![CDATA[]]></expression>
                        <fields>
                            <field localName="" targetName="" />
                        </fields>
                    </relation>
                </relationShip>
                <!-- Este elemento é responsável por cadastrar as telas da extensão -->
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