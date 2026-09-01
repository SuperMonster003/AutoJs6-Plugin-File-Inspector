import com.android.build.api.variant.FilterConfiguration
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.file.RelativePath
import org.gradle.api.provider.Property
import java.io.File
import java.security.MessageDigest
import java.util.HexFormat

plugins {
    id("org.autojs.build.utils")
    id("org.autojs.build.versions")
    id("org.autojs.build.signs")
    id("org.autojs.build.jvm-convention")
    id("com.android.application")
}

val globalApplicationId = "io.github.supermonster003.autojs6.plugin.fileinspector"

val buildTypeDebug = "debug"
val buildTypeRelease = "release"

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().buffered().use { input ->
        val buffer = ByteArray(64 * 1024)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            if (count > 0) digest.update(buffer, 0, count)
        }
    }
    return HexFormat.of().formatHex(digest.digest())
}

android {
    namespace = globalApplicationId
    compileSdk = versions.sdkVersionCompile

    defaultConfig {
        applicationId = globalApplicationId
        minSdk = versions.sdkVersionMin
        targetSdk = versions.sdkVersionTarget
        versionCode = versions.appVersionCode
        versionName = versions.appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "plugin_author", "SuperMonster003")
        resValue("string", "plugin_version_date", utils.getDateString("MMM d, yyyy", "GMT+08:00"))
    }

    lint {
        abortOnError = false
    }

    signingConfigs {
        if (signs.isValid) {
            create(buildTypeRelease) {
                storeFile = signs.properties["storeFile"]?.let { file(it as String) }
                keyPassword = signs.properties["keyPassword"] as String
                keyAlias = signs.properties["keyAlias"] as String
                storePassword = signs.properties["storePassword"] as String
            }
        }
    }

    buildTypes {
        val proguardFiles = arrayOf<Any>(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro",
        )
        val niceSigningConfig = takeIf { signs.isValid }?.let {
            signingConfigs.getByName(buildTypeRelease)
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(*proguardFiles)
            niceSigningConfig?.let { signingConfig = it }
        }
    }

    buildFeatures {
        aidl = true
        resValues = true
        viewBinding = true
    }

    sourceSets.named("main") {
        kotlin.directories += "src/main/java"
    }

    packaging {
        resources.pickFirsts.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.*",
                "META-INF/NOTICE",
                "META-INF/NOTICE.*",
                "META-INF/*.kotlin_module",
            ),
        )
    }

    bundle {
        language.enableSplit = false
        density.enableSplit = false
        abi.enableSplit = false
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            val architecture = output.filters.find {
                it.filterType == FilterConfiguration.FilterType.ABI
            }?.identifier
            val outputFileNameProperty = output.javaClass.methods.firstOrNull {
                it.name == "getOutputFileName" && it.parameterTypes.isEmpty()
            }?.invoke(output) as? Property<*>

            @Suppress("UNCHECKED_CAST")
            (outputFileNameProperty as? Property<String>)?.set(
                output.versionName.map { versionName ->
                    val version = versionName.replace("\\s".toRegex(), "-")
                    val abiSuffix = architecture?.let { "-$it" }.orEmpty()
                    "${rootProject.name}-v$version$abiSuffix.${utils.FILE_EXTENSION_APK}".lowercase()
                },
            )
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:2.2.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation(files("$rootDir/libs/common-plugin-api.aar"))
    implementation(files("$rootDir/libs/explorer-action-api.aar"))

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.test.runner)
}

tasks {
    withType(JavaCompile::class.java) {
        options.encoding = "UTF-8"
    }

    register<Copy>("appendDigestToReleasedFiles") {
        description = "Copies release APKs with CRC32 suffixes and writes SHA-256 sidecars"
        dependsOn("assembleRelease")

        val ext = utils.FILE_EXTENSION_APK
        val src = layout.buildDirectory.dir("outputs/apk/$buildTypeRelease")
        val dst = file("$rootDir/${buildTypeRelease}s")

        from(src)
        into(dst)
        include("*.$ext")
        includeEmptyDirs = false
        duplicatesStrategy = DuplicatesStrategy.FAIL

        eachFile {
            val digest = utils.digestCRC32(file)
            relativePath = RelativePath(true, "${name.removeSuffix(".$ext")}-$digest.$ext")
        }

        doLast {
            val releasedApks = dst.listFiles { file ->
                file.isFile && file.extension.equals(ext, ignoreCase = true)
            }.orEmpty()
            check(releasedApks.isNotEmpty()) { "No released APK files found in $dst" }
            releasedApks.forEach { apk ->
                val checksumFile = File(dst, "${apk.name}.sha256")
                checksumFile.writeText("${sha256(apk)}  ${apk.name}\n", Charsets.UTF_8)
                println("SHA-256: ${checksumFile.name}")
            }
            println("Destination: $dst")
        }
    }

    register("verifyReleaseChecksums") {
        group = "verification"
        description = "Verifies release APK name CRC32 suffixes and SHA-256 sidecars"

        val ext = utils.FILE_EXTENSION_APK
        val releaseDirectory = file("$rootDir/${buildTypeRelease}s")
        inputs.files(fileTree(releaseDirectory) { include("*.$ext", "*.$ext.sha256") })

        doLast {
            val apks = releaseDirectory.listFiles { file ->
                file.isFile && file.extension.equals(ext, ignoreCase = true)
            }.orEmpty().sortedBy(File::getName)
            check(apks.isNotEmpty()) { "No release APK files found in $releaseDirectory" }

            val expectedSidecars = apks.map { apk -> "${apk.name}.sha256" }.toSet()
            val actualSidecars = releaseDirectory.listFiles { file ->
                file.isFile && file.name.endsWith(".$ext.sha256")
            }.orEmpty().map(File::getName).toSet()
            check(actualSidecars == expectedSidecars) {
                "Release APK/SHA-256 sidecar mismatch: expected=$expectedSidecars actual=$actualSidecars"
            }

            val releaseNamePattern = Regex(
                "^${Regex.escape(rootProject.name.lowercase())}-v.+-([0-9a-f]{8})\\.$ext$",
            )
            val checksumLinePattern = Regex("^([0-9a-f]{64})  (.+)$")
            apks.forEach { apk ->
                val releaseNameMatch = requireNotNull(releaseNamePattern.matchEntire(apk.name)) {
                    "Invalid released APK name: ${apk.name}"
                }
                val expectedCrc32 = releaseNameMatch.groupValues[1]
                check(utils.digestCRC32(apk) == expectedCrc32) {
                    "CRC32 suffix does not match ${apk.name}"
                }

                val checksumFile = File(releaseDirectory, "${apk.name}.sha256")
                val lines = checksumFile.readLines(Charsets.UTF_8)
                check(lines.size == 1) { "Expected exactly one checksum line in ${checksumFile.name}" }
                val checksumMatch = requireNotNull(checksumLinePattern.matchEntire(lines.single())) {
                    "Invalid SHA-256 sidecar format: ${checksumFile.name}"
                }
                check(checksumMatch.groupValues[2] == apk.name) {
                    "SHA-256 sidecar names the wrong APK: ${checksumFile.name}"
                }
                check(checksumMatch.groupValues[1] == sha256(apk)) {
                    "SHA-256 does not match ${apk.name}"
                }
                println("Verified: ${apk.name}")
            }
        }
    }
}

extra {
    versions.handleIfNeeded(project, "", listOf(buildTypeDebug, buildTypeRelease))
}
