package org.brewcode.qa.pages.page.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.brewcode.qa.pages.cfg.TestConfiguration
import org.brewcode.qa.pages.page.modal.ModalDockerTutorialPage

open class DslDockerTutorialPageTest : StringSpec() {

    private val pages = TestConfiguration.pages

    init {

        "Scenario: base page dsl" {
            pages.page<ModalDockerTutorialPage>()
                .open()
                .verify()
                .whenDo {
                    link.click()
                }.thenOpen<CommandDockerTutorialPage> {
                    body.shouldNotBeNull()
                }.thenOpen(CommandDockerTutorialPage::class) {
                    title() shouldBe "Getting Started"
                }
        }
    }
}
