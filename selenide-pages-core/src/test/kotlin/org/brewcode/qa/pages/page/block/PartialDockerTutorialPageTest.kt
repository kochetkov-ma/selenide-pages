package org.brewcode.qa.pages.page.block

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide
import io.kotest.core.spec.style.FreeSpec
import org.brewcode.qa.pages.cfg.TestConfiguration

open class PartialDockerTutorialPageTest : FreeSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: partial test" - {
            lateinit var tutorialPage: PartialDockerTutorialPage

            "страница DockerTutorialPage открыта" {
                tutorialPage = pages.page<PartialDockerTutorialPage>().open().verify()

                Selenide.executeJavaScript<Any?>("console.error('Error message')")
                Selenide.executeJavaScript<Any?>("console.log('Info message')")
            }

            "страница DockerTutorialPage блоки" {
                tutorialPage.infoTitleBlock.item.self.shouldBe(Condition.visible)
                tutorialPage.infoTitleBlock.item.self.shouldBe(Condition.text("Pro tip"))

                tutorialPage.infoCodeBlock.item.self.shouldBe(Condition.text("docker run"))
            }
        }
    }
}
