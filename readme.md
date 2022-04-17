# Placeholder Gradle Plugin
A gradle plugin that replaces transform the final output jar, and apply custom placeholders defined in build script.

## Source
This is a project originally made for an organization named "Dragon Commissions". I'm the developer of
it, and I feel like making it public is a good idea.

They have nothing to do with this project, they didn't even allow me to create this project under name
of "Dragon Commissions", so I decided to put it here, and replace `com.dragoncommissions` to `me.fan87`

## Setup
### Installation
```shell
# Clone the repository and CD into it
git clone https://github.com/fan87/placeholder-gradle-plugin
cd placeholder-gradle-plugin

# Build it
./gradlew build publishAllPublicationsToMavenLocalRepository # On linux
gradlew.bat build publishAllPublicationsToMavenLocalRepository # On Windows CMD
.\gradlew.sh build publishAllPublicationsToMavenLocalRepository # On Windows PowerShell
```
You can clone it to your desktop instead of a temporary directory, so if there's an update you can just git pull and rebuild it again.

### Use (`build.gradle.kts`)
> I will be using Kotlin as DSL for this part of tutorial. I know many people use groovy but the code completion is sh*t , and I don't like it. As an experienced genius expert Java developer (im joking), having better code completion is more important.

#### Add the Local Maven Repository to plugin repositories
First, go to your `settings.gradle.kts` and add this:
```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```
This is equal to having `<repostiroies>` in your pom.xml, but for gradle plugins.
> You have to install it first before using it. Check the [Installation](#installation) part first.

#### Add placeholder plugin to `plugins`
Add this to your `build.gradle.kts`. If you already have another plugin, just put the second line below that.
```kotlin
plugins {
    id("me.fan87.placeholder-plugin") version "1.0-SNAPSHOT"
}
```

#### Register Task
Add this to your `build.gradle.kts` (wherever you want):
```kotlin
tasks.register<PlaceHolderTask>("applyPlaceHolder") 
```
This will register a task, and tells the program what's the input and output file. **If you have shadow plugin, or other plugins that changes the final output jar location, you have to change the code in-order to make it work**

Here's how you change the input:
```kotlin
tasks.register<PlaceHolderTask>("applyPlaceHolder")  {
    input.set(File(project.buildDir, "libs/example-input-1.0-SNAPSHOT-all.jar"))
}
```

If you want to, you can also set the output to jar with no classifier. It will work.

#### Configuring
Here's an example config:
```kotlin
registerLazyPlaceHolder("PlaceHolder.BuildUnixTime") {
    System.currentTimeMillis().toString()
}
// Lazy PlaceHolder basically means that it will only be executed once, which is when you register the placeholder.

registerPlaceHolder("PlaceHolder.ProjectName", project.name)
registerPlaceHolder("PlaceHolder.ProjectVersion", project.version as String)
// Registering static placeholder (In this case: project name and version)
// Since it'll be static, it will also only read the field once (Lazy loaded)

registerPlaceHolder("PlaceHolder.CompiledTime") {
    System.currentTimeMillis().toString()
}
// It looks same as lazy placeholder, the only difference is that it will be executed everytime there's %PlaceHolder.CompiledTime% in a String
```

#### Code
Here's an example code:
```java
public class Main {
    
    public static void main(String[] args) {
        // How you do it
        System.out.println("Build Time: %PlaceHolder.BuildUnixTime%");
        
        // BAD EXAMPLE:
        String prefix = "PlaceHolder.";
        System.out.println("[BAD EXAMPLE] Build Time: %" + prefix + "BuildUnixTime%");
    }
}
```

#### Build
Run this command to build the Gradle project:

(For PowerShell/Linux)

```shell
./gradlew build applyPlaceHolder 
```

The output file is at `build/libs/placeholder-applied-<name>-<version>.jar`. Well, if you have changed the output file location in the [task registering](#register-task) part, it will be different.

## FAQ
Well, they are not frequently asked, but they are good questions anyway, lol.
### Why I chose Gradle, not Maven?
Maven's `pom.xml` is completely different from Gradle. Let's see what's the differences, and why Gradle:

First, `pom.xml` is literally a XMl file, it's a regular file with a structure, which means if you want to do more advanced 
stuff like: Send an HTTP request to localhost with token in header from a file `secret.txt`, of course you can find a plugin for it, but you get the idea: it's nearly impossible to do it, unless you code a plugin yourself.<br>
But Gradle uses a **Build Script** instead of a structured file (In maven's case: XMl), which allows you to do stuff like define variables, define methods, and even call a method. I know for Java developers gradle looks confusing, and it seems like it's same as pom.xml but with a different structure, but as I said it's actually a script. If you understand groovy/kotlin enough you'll understand that everything is indeed following the rule of groovy or kotlin.<br>
And since you can declare methods, run methods, it allows you to register a placeholder with lambda as parameter, so you can code your own placeholder behavior without modifying the plugin itself.
