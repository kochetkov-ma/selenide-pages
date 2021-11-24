package online.jeteam.qa.pom.page.part

import com.codeborne.selenide.Selenide
import io.kotest.core.spec.style.FreeSpec
import online.jeteam.qa.pom.cfg.TestConfiguration

open class PartialDockerTutorialPageTest : FreeSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: partial test" - {
            lateinit var tutorialPage: PartialDockerTutorialPage

            "страница DockerTutorialPage открыта" {
                tutorialPage = pages.page<PartialDockerTutorialPage>().open().verifyOpen()

                Selenide.executeJavaScript<Any?>("console.error('Error message')")
                Selenide.executeJavaScript<Any?>("console.log('Info message')")
            }
        }
    }
}
