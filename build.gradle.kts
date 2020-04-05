plugins {
    kotlin("jvm") version "1.3.71"
    id("application")
}

val mainClass = "org.gern.workers.AppKt"

application {
    mainClassName = mainClass
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:1.3.2")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to mainClass)
        }

        from({
            configurations.compileClasspath.get()
                    .filter { it.name.endsWith("jar") }
                    .map { zipTree(it) }
        })
    }
}

