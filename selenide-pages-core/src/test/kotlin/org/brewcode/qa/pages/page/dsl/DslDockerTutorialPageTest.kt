package org.brewcode.qa.pages.page.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.brewcode.qa.pages.cfg.TestConfiguration
import org.brewcode.qa.pages.page.modal.ModalDockerTutorialPage

open class DslDockerTutorialPageTest : StringSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: base page dsl" {
            var throwError = true
            pages.page<ModalDockerTutorialPage>()
                .open()
                .verify()
                .whenDoWithRetry({ throwError = false }) {
                    if (throwError) throw IllegalStateException("Should be ignored")
                    link.click()
                }.thenOpen<CommandDockerTutorialPage> {
                    body.shouldNotBeNull()
                }.thenOpen(CommandDockerTutorialPage::class) {
                    title() shouldContain "Getting Started"
                }
        }
    }
}
