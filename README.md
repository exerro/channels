<h1 align="center">
  channels
</h1>

<p align="center">
  <a href="https://jitpack.io/#exerro/channels"><img src="https://jitpack.io/v/exerro/channels.svg" alt="JitPack badge"/></a>
</p>

A channel is a bidirectional communication object capable of pushing and pulling
values. The type of values pushed can differ from the type of values pulled.

## Examples

```kotlin
val channel = Channel.createLoopback<Int>()

channel.push(1)
channel.push(2)

assert(channel.pull() == 1)
assert(channel.pull() == 2)
```

```kotlin
val (channelA, channelB) = Channel.createPair<Int, String>()

channelA.push(1)
assert(channelB.pull() == 1)

channelB.push("2")
assert(channelA.pull() == "2")
```

## Installation

Check out the [releases](https://github.com/exerro/channels/releases), or
using a build system...

### Gradle (`build.gradle.kts`)

```kotlin
repositories {
    // ...
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("me.exerro:channels:1.0.0")
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>me.exerro</groupId>
  <artifactId>channels</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Testing the build before release

    ./gradlew clean && ./gradlew build && ./gradlew build publishToMavenLocal
