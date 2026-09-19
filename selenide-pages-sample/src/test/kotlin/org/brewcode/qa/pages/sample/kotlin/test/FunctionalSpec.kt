package org.brewcode.qa.pages.sample.kotlin.test

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.WebDriverRunner
import io.kotest.assertions.asClue
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.brewcode.qa.pages.page.Pages
import org.brewcode.qa.pages.sample.java.TestingUtil
import org.brewcode.qa.pages.sample.kotlin.page.DockerGettingStartedMainPage
import org.brewcode.qa.pages.sample.kotlin.page.OurApplicationPage
import org.openqa.selenium.JavascriptExecutor

class FunctionalSpec : StringSpec({
    "opens tutorial, copies command, and navigates to Our Application" {
        try {
            // GIVEN the pinned tutorial website, WHEN it opens, THEN the page verifies.
            val pages = Pages.createWithStaticSelenideDriver(TestingUtil.baseUrl())
            val mainPage = withClue("Pinned tutorial opens at the expected path with required elements") {
                pages.page<DockerGettingStartedMainPage>().open().verify()
            }

            // WHEN the copy button is clicked, THEN the UI and clipboard agree.
            mainPage.dockerRunCodeBlock.copyButton.click()
            withClue("Copy confirmation is visible and names the successful action") {
                mainPage.dockerRunCodeBlock.copyMessage.asClue {
                    it.shouldBe(Condition.visible)
                    it.shouldHave(Condition.exactText("Copied to clipboard"))
                }
            }
            val clipboard = (WebDriverRunner.getWebDriver() as JavascriptExecutor).executeAsyncScript(
                "const done = arguments[arguments.length - 1]; navigator.clipboard.readText().then(done, error => done(error.message));"
            )
            withClue("Browser clipboard equals the displayed Docker command") {
                clipboard shouldBe mainPage.dockerRunCodeBlock.code.text
            }

            // WHEN Our Application is selected, THEN its page opens and verifies.
            withClue("Our Application navigation opens the pinned destination page") {
                mainPage.whenDo {
                    mainPage.gettingStartedNavigation.item("Our Application").asClue { item ->
                        item.shouldNotBeNull()
                        item.gettingStartedNavigation.shouldBe(Condition.visible)
                        item.gettingStartedNavigation.shouldHave(Condition.exactText("Our Application"))
                        item.gettingStartedNavigation.click()
                    }
                }.thenOpen<OurApplicationPage> { verify() }
            }
        } finally {
            Selenide.closeWebDriver()
        }
    }
})
