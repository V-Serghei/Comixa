import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        target.pluginManager.withPlugin("com.android.application") {
            target.extensions.configure<ApplicationExtension> {
                buildFeatures { compose = true }
            }
        }
        target.pluginManager.withPlugin("com.android.library") {
            target.extensions.configure<LibraryExtension> {
                buildFeatures { compose = true }
            }
        }
    }
}
