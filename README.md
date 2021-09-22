# Kourrier

From the French "Courrier" (mail)

***A Kotlin/JVM wrapper around the JavaMail API***

[![Kotlin](https://img.shields.io/badge/Kotlin-1.5.31-7f52ff.svg)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/com.notkamui.libs/kourrier.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.notkamui.libs%22%20AND%20a:%22kourrier%22)
[![CodeFactor](https://www.codefactor.io/repository/github/notkamui/kourrier/badge)](https://www.codefactor.io/repository/github/notkamui/kourrier)

## Integration

You can import Kourrier directly with the jar files, or using your favorite dependency manager with the Maven Central
repository:

### Maven

```XML
<dependencies>
  <dependency>
    <groupId>com.notkamui.libs</groupId>
    <artifactId>kourrier</artifactId>
    <version>0.2.0</version>
  </dependency>
</dependencies>
```

### Gradle

<details>
<summary>Kotlin DSL</summary>
<p>

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("com.notkamui.libs:kourrier:0.2.0")
}
```
</p>
</details>

<details>
<summary>Groovy DSL</summary>
<p>

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.notkamui.libs:kourrier:0.2.0'
}
```
</p>
</details>

(In case you're using it with another language than Kotlin -- i.e. Java --, make sure you include kotlin stdlib too)

## Usage

Please refer to the [Wiki](https://github.com/notKamui/Kourrier/wiki)

## Future plans

- SMTP connection
