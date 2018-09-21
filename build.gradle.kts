plugins {
    id("java")
    id("idea")
    id("application")
//    id("com.sedmelluq.jdaction") version "1.0.1"
    id("org.jetbrains.kotlin.jvm") version "1.2.0"
    id("com.github.johnrengelman.shadow") version "2.0.1"
}

group = "xyz.gnarbot"
version = "4.8.3"

val mainClass: String = "xyz.gnarbot.gnar.Bot"

configure<ApplicationPluginConvention> {
    mainClassName = mainClass
}

tasks.create("wrapper", Wrapper::class.java) {
    gradleVersion = "4.3.1"
}

repositories {
    jcenter()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    // Kotlin
    compile(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jre8", version = "1.2.0")
    compile(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "0.19.3")

    compile(group = "org.codehaus.groovy", name = "groovy-jsr223", version = "2.4.12")

    // JDA
    compile(group = "net.dv8tion", name = "JDA", version = "3.3.1_307")
    compile(group = "com.sedmelluq", name = "jda-nas", version = "1.0.5")
    compile(group = "com.sedmelluq", name = "lavaplayer", version = "1.2.44")

    // Configuration
    compile(group = "ninja.leaping.configurate", name = "configurate-hocon", version = "3.3")
    compile(group = "org.json", name = "json", version = "20170516")

    // Database
    compile(group = "com.rethinkdb", name = "rethinkdb-driver", version = "2.3.3")
    compile(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-xml", version = "2.9.2")

    // Custom language
    compile(group = "xyz.avarel.kaiper", name = "Kaiper-Interpreter", version = "1.1.0")
    compile(group = "xyz.avarel.kaiper", name = "Kaiper-AST", version = "1.1.0")
    compile(group = "xyz.avarel.kaiper", name = "Kaiper-Runtime-Lib", version = "1.1.0")

    // Math
    compile(group = "xyz.avarel", name = "aljava", version = "0.0.3")
    compile(group = "org.scilab.forge", name = "jlatexmath", version = "1.0.6")

    // Net stuff
    compile(group = "com.squareup.okhttp3", name = "okhttp", version = "3.9.0")
    compile(group = "org.jsoup", name = "jsoup", version = "1.10.3")

    // Libraries
    compile(group = "org.apache.commons", name = "commons-text", version = "1.1")
    compile(group = "commons-io", name = "commons-io", version = "2.6")
    compile(group = "com.google.guava", name = "guava", version = "23.2-jre")

    // Logger
    compile(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

    // Riot API
    compile(group = "com.github.taycaldwell", name = "riot-api-java", version = "4.0.1")

    // Test dependencies
    testCompile(group = "org.jetbrains.kotlin", name = "kotlin-test-junit", version = "1.2.0")
    testCompile(group = "junit", name = "junit", version = "4.12")
}

configure<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<KotlinCompile> {
    targetCompatibility = JavaVersion.VERSION_1_9.name
    sourceCompatibility = JavaVersion.VERSION_1_9.name

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configure<Jar> {
    manifest {
        attributes["Main-Class"] = mainClass
    }
}

configure<KotlinProjectExtension> {
    experimental.coroutines = Coroutines.ENABLE
}