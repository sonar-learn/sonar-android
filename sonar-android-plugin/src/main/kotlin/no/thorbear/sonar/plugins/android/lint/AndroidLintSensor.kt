package no.thorbear.sonar.plugins.android.lint

import no.thorbear.sonar.plugins.android.AndroidPlugin
import org.slf4j.LoggerFactory
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.config.Configuration
import org.sonar.api.profiles.RulesProfile
import java.io.File

class AndroidLintSensor(
    private val profile: RulesProfile,
    private val fs: FileSystem
) :
    Sensor {

    override fun describe(descriptor: SensorDescriptor) {
        descriptor
            .onlyOnLanguages("java", "kt")
            .name("AndroidLint")
            .onlyOnFileType(InputFile.Type.MAIN)
            .onlyWhenConfiguration{ config : Configuration -> config.hasKey(AndroidPlugin.LINT_REPORT_PROPERTY) }
    }

    override fun execute(context: SensorContext) {
        val lintReport = getFile(
            context.config()
                .get(AndroidPlugin.LINT_REPORT_PROPERTY)
                .orElse(AndroidPlugin.LINT_REPORT_PROPERTY_DEFAULT)
        )
        AndroidLintProcessor(context, profile).process(lintReport!!)
    }

    private fun getFile(path: String): File? {
        try {
            var file = File(path)
            if (!file.isAbsolute) {
                file = File(fs.baseDir(), path).canonicalFile
            }
            return file
        } catch (e: Exception) {
            LOGGER.warn(
                "Lint report not found, please set {} property to a correct value.",
                AndroidPlugin.LINT_REPORT_PROPERTY
            )
            LOGGER.warn("Unable to resolve path : $path", e)
        }

        return null
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AndroidLintSensor::class.java)
    }
}
