import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.notkamui.libs"
version = "0.0.1"
val jvmVersion = "1.8"

plugins {
    kotlin("jvm") version "1.5.30"
    java
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.21")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.21")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    jar {
        manifest {
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jvmVersion
    }

    withType<JavaCompile> {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    withType<Wrapper> {
        distributionType = Wrapper.DistributionType.ALL
    }
}

val repositoryUrl = if (version.toString().endsWith("SNAPSHOT"))
    "https://oss.sonatype.org/content/repositories/snapshots/"
else
    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            pom {
                name.set(project.name)
                description.set("A Kotlin wrapper around the JavaMail API")
                url.set("https://github.com/notKamui/${project.name}")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://mit-license.org/")
                    }
                }
                developers {
                    developer {
                        id.set("notKamui")
                        name.set("Jimmy Teillard")
                        email.set("jimmy.teillard@notkamui.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/notKamui/${project.name}.git")
                    developerConnection.set("scm:git:ssh://github.com/notKamui/${project.name}.git")
                    url.set("https://github.com/notKamui/${project.name}.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "MavenCentral"
            setUrl(repositoryUrl)
            credentials {
                username = project.properties["ossrhUsername"] as String? ?: "Unknown user"
                password = project.properties["ossrhPassword"] as String? ?: "Unknown password"
            }
        }
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/notKamui/${project.name}")
            credentials {
                username = project.properties["githubUsername"] as String? ?: "Unknown user"
                password = project.properties["githubPassword"] as String? ?: "Unknown password"
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}
