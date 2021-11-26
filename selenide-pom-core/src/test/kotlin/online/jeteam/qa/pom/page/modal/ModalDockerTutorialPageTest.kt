package online.jeteam.qa.pom.page.modal

import com.codeborne.selenide.Condition
import io.kotest.core.spec.style.FreeSpec
import online.jeteam.qa.pom.cfg.TestConfiguration

open class ModalDockerTutorialPageTest : FreeSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: modal window" - {
            lateinit var tutorialPage: ModalDockerTutorialPage

            "ModalDockerTutorialPage opened" {
                tutorialPage = pages.page<ModalDockerTutorialPage>().open().verifyOpen()
            }

            "click search and open modal" {
                tutorialPage.search.click()
                val searchModal = tutorialPage.modal(SearchModal::class.java)
                searchModal.input.shouldBe(Condition.visible)
                searchModal.result.shouldBe(Condition.visible)

                searchModal.clickOutsideLeft()

                tutorialPage.link.click()
            }
        }
    }
}
