package no.thorbear.sonar.plugins.android.lint

import no.thorbear.sonar.plugins.android.utils.Resources
import org.slf4j.LoggerFactory
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.rules.ActiveRule
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition
import org.sonar.api.utils.ValidationMessages
import java.io.IOException

class AndroidLintSonarWay(
    private val profileImporter: AndroidLintProfileImporter
) : BuiltInQualityProfilesDefinition {

    private val logger = LoggerFactory.getLogger(AndroidLintSonarWay::class.java)

    // Cannot be in constructor, because sonar performs injection at runtime
    private val resources: Resources = Resources()

    override fun define(context: BuiltInQualityProfilesDefinition.Context) {
        val qualityProfile : BuiltInQualityProfilesDefinition.NewBuiltInQualityProfile = context.createBuiltInQualityProfile(AndroidLintRulesDefinition.REPOSITORY_KEY, "java")
        try {
            resources.asReader(PROFILE_XML_PATH).use {
                val rulesProfile: RulesProfile = profileImporter.importProfile(it, ValidationMessages.create())
                for (rule: ActiveRule in rulesProfile.activeRules) {
                    qualityProfile.activateRule(rule.repositoryKey, rule.ruleKey)
                }
            }
        } catch (e : IOException) {
            logger.error("Error Creating AndroidLint Profile", e)
        }
    }

    companion object {
        const val PROFILE_XML_PATH = "/no/thorbear/sonar/plugins/android/lint/android_lint_sonar_way.xml"
    }
}
