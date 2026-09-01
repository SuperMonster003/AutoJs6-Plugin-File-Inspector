package io.github.supermonster003.autojs6.plugin.fileinspector.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/** Exercises the same parse, inspect, and verify pipeline documented for release self-checks. */
class ReleaseArtifactSelfCheckTest {

    @Test
    fun locallyAvailableReleaseApksMatchTheirPublishedSha256Sidecars() {
        val releaseDirectory = File(repositoryRoot(), "releases")
        val apks = releaseDirectory.listFiles { file ->
            file.isFile && file.extension.equals("apk", ignoreCase = true)
        }.orEmpty().sortedBy(File::getName)
        assumeTrue("No local release APKs are available for the optional self-check", apks.isNotEmpty())

        apks.forEach { apk ->
            val checksumFile = File(releaseDirectory, "${apk.name}.sha256")
            assertTrue("Missing ${checksumFile.name}", checksumFile.isFile)
            val parsed = DigestInputNormalizer.parse(checksumFile.readText(Charsets.UTF_8).trimEnd())
            assertTrue("Invalid ${checksumFile.name}", parsed is DigestParseResult.Valid)
            val expected = (parsed as DigestParseResult.Valid).value

            assertEquals(DigestAlgorithm.SHA256, expected.algorithm)
            assertFalse(expected.hasSourceFileNameMismatch(apk.name))

            val report = runBlocking {
                FileInspectionEngine().inspect(
                    object : InspectorSource {
                        override val declaredSize: Long = apk.length()
                        override fun openStream(): InputStream = FileInputStream(apk)
                    },
                )
            }
            assertEquals(apk.name, VerificationStatus.MATCH, DigestVerifier.verify(report, expected))
        }
    }

    private fun repositoryRoot(): File {
        val workingDirectory = requireNotNull(System.getProperty("user.dir"))
        return generateSequence(
            File(workingDirectory).absoluteFile,
            File::getParentFile,
        ).firstOrNull { directory ->
            File(directory, "settings.gradle.kts").isFile && File(directory, "releases").isDirectory
        } ?: error("Could not locate repository root from $workingDirectory")
    }
}
