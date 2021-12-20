package org.brewcode.qa.pages.page.modal

import com.codeborne.selenide.Condition
import io.kotest.assertions.retry
import io.kotest.core.spec.style.FreeSpec
import junit.framework.AssertionFailedError
import org.brewcode.qa.pages.cfg.TestConfiguration
import kotlin.time.Duration.Companion.seconds

open class ModalDockerTutorialPageTest : FreeSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: modal window" - {
            lateinit var tutorialPage: ModalDockerTutorialPage

            "ModalDockerTutorialPage opened" {
                tutorialPage = pages.page<ModalDockerTutorialPage>().open().verify()
            }

            "click search and open modal" {
                val action = {
                    tutorialPage.search.click()
                    val searchModal = tutorialPage.modal(SearchModal::class.java)
                    searchModal.input.shouldBe(Condition.visible)
                    searchModal.result.shouldBe(Condition.visible)
                    searchModal.clickOutsideLeft()
                    tutorialPage.link.click()
                }
                
                runCatching(action).onFailure { action() }
            }
        }
    }
}
