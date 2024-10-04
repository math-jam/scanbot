plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    // Necessário para configurar a tarefa Docker Compose Up
    id("com.palantir.docker-compose") version "0.25.0"
    // Necessário para executar a tarefa Atualizar Dicionário
    id("com.bmuschko.docker-remote-api") version "6.4.0"
}

group = "br.com.sankhya.devcenter.scanbot"
val branchName =
    if (System.getenv("CI_COMMIT_REF_NAME") == "master") "r"
    else if (System.getenv("CI_COMMIT_REF_NAME")?.endsWith("beta") == true) "b"
    else "d"
val versao = "1.0.0_${branchName}${System.getenv("CI_PIPELINE_IID")}";
version = versao
description = "Scanbot"

val dvcVersion = "1.6.3"
val skwVersion = "master"

apply<br.com.sankhya.devcenter.SankhyaPlugin>()
apply<de.fntsoftware.gradle.MarkdownToPdfPlugin>()
configure<br.com.sankhya.devcenter.SankhyaPluginExtension> {
    /* outputDir.set(file("dist/"))*/
    sankhyaWUrl.set("http://localhost:8080/")
    sankhyaWUrlProducao.set("http://localhost:8080/")
    usuario.set("SUP")
    usuarioProducao.set("SUP")
    senha.set("tecsis")
    senhaProducao.set("tecsis")
    plataformaMinima.set("4.20")
    parceiroId.set(System.getenv("PARCEIRO_ID"))
    parceiroNome.set(System.getenv("PARCEIRO_SANKHYA"))
    moduloPadrao.set("")
    branch.set(versao)
    chave.set(System.getenv("CHAVE_SANKHYA"))
    modelDD.set("Lugh")
    // A appKey é abrigatória para que seja gerada a EXTS da extensão.
    appKey.set("30497df3-6356-4435-851a-7e007d4cc025")
}

buildscript{
    repositories{
        mavenCentral()
        maven {
            url = uri("https://nexus-repository.sankhya.com.br/repository/maven-public/")
        }
        maven {
            url = uri("https://nexus-repository.sankhya.com.br/repository/maven-devcenter-releases")
        }
    }
    dependencies {
        classpath("br.com.sankhya.devcenter:sankhyaw-gradle:6.4.31")
        classpath("com.fasterxml.jackson.core", "jackson-databind", "2.11.3")
        classpath("org.apache.directory.studio", "org.apache.commons.io", "2.1")
        classpath("gradle.plugin.de.fntsoftware.gradle:markdown-to-pdf:1.1.0")
        classpath("com.vladsch.flexmark:flexmark-all:0.62.2")
        classpath("commons-io:commons-io:2.11.0")
        classpath("net.lingala.zip4j:zip4j:1.2.3")
        classpath("com.google.api-client:google-api-client:1.31.1")
        classpath("com.auth0:java-jwt:3.18.0")

    }
}


repositories {
    mavenCentral()
    maven {
        url = uri("https://nexus-repository.sankhya.com.br/repository/maven-public/")
    }
    maven {
        url = uri("https://nexus-repository.sankhya.com.br/repository/maven-devcenter-releases")
    }
    maven {
        url = uri("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/")
    }
}
configurations.implementation.get().isCanBeResolved = true
dependencies {
    // Processado de anotações e geradores de código
//    "implementationExt"("br.com.sankhya.devcenter", "dvc-lib", lughVersion)
    "implementationExts"("br.com.sankhya.devcenter", "dvc-lib", dvcVersion)
    implementation("br.com.sankhya.devcenter", "dvc-lib-annotation", dvcVersion)
    kapt("br.com.sankhya.devcenter", "dvc-lib-processor", dvcVersion)
    "implementationWar"("br.com.sankhya.devcenter", "dvc-lib-web", dvcVersion)
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    implementation("org.projectlombok:lombok:1.18.22")
    kapt("org.projectlombok:lombok:1.18.22")
    // Nativo Sankhya
    implementation("br.com.sankhya", "mge-modelcore", skwVersion)
    implementation("br.com.sankhya", "jape", skwVersion)
    implementation("br.com.sankhya", "dwf", skwVersion)
    implementation("br.com.sankhya", "sanws", skwVersion)
    implementation("br.com.sankhya", "mge-param", skwVersion)
    implementation("br.com.sankhya","skw-environment", skwVersion)
    implementation("br.com.sankhya","sanutil", skwVersion)
    implementation("br.com.sankhya", "cuckoo", skwVersion)
    implementation("br.com.sankhya", "mgecom-model", skwVersion)
    implementation("br.com.sankhya", "mgefin-model", skwVersion)
    // Status HTTP / Apoio as Servlets
    implementation("org.apache.directory.studio", "org.apache.commons.io", "2.1")
    implementation("org.apache.httpcomponents", "httpclient", "4.0.1")
    implementation("commons-fileupload", "commons-fileupload", "1.2")
    implementation("commons-httpclient", "commons-httpclient", "3.0.1")

    // Manipulador de JSON
    implementation("com.google.code.gson", "gson", "2.1")
    // EJB / Escrever no container wildfly
    implementation("org.wildfly:wildfly-spec-api:16.0.0.Final")
    implementation("org.jdom", "jdom", "1.1.3")

    //Scanbot
    "implementationLib"("com.google.api-client:google-api-client:1.31.1")
    "implementationLib"("com.auth0:java-jwt:3.18.0")
    implementation("org.apache.pdfbox", "pdfbox", "1.8.4")


    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("br.com.sankhya.devcenter", "dvc-lib-test", dvcVersion)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("br.com.sankhya.devcenter", "dvc-lib-test", dvcVersion)
    // Obsoletas
    //implementation(kotlin("stdlib-jdk8"))
    //implementation("jdom", "jdom", "1.0")
    //implementation("org.beanshell", "bsh", "1.3.0")
    //implementation("org.apache.directory.studio", "org.apache.commons.io", "2.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    kapt{
        useBuildCache = false
    }
    test {
        useJUnitPlatform()
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
    }
    test {
        useJUnitPlatform()
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
    }
}

tasks.withType(com.palantir.gradle.docker.DockerComposeUp::class.java) {
    dependsOn("configureExtension")
}
dockerCompose {
    this.setDockerComposeFile("docker-compose.yml")
}

tasks.register("atualizarDicionario", com.bmuschko.gradle.docker.tasks.container.DockerExecContainer::class.java) {
    dependsOn("copyMetadados", "convertMetadados", "convertScripts")
    group = "sankhyaw"
    this.containerId.set("${System.getProperty("COMPOSE_PROJECT_NAME")}_wildfly_1")
    this.commands.add(arrayOf("installExtension"))
}

tasks.register("doc",de.fntsoftware.gradle.MarkdownToPdfTask::class.java){
    setInputFile(File("readme.md"))
    setOutputFile(File("build/classes/java/main/readme.pdf"))
    //setOption(Parser)
    //setOption(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()))
}

tasks.named("configureExtension"){
    dependsOn("doc")
}

// Dependencia entre os modulos
sourceSets {
    val main by getting
    val web by getting {
        java {
            compileClasspath += main.output
        }
    }
}