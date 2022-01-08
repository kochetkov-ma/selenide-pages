package org.brewcode.qa.pages.sample.kotlin.test

import com.codeborne.selenide.Condition
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.brewcode.qa.pages.page.Pages
import org.brewcode.qa.pages.sample.kotlin.page.DockerGettingStartedMainPage
import org.brewcode.qa.pages.sample.kotlin.page.OurApplicationPage
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class FunctionalSpec : FreeSpec() {
    init {
        "Scenario: functional test" - {
            lateinit var mainPage: DockerGettingStartedMainPage
            lateinit var ourApplicationPage: OurApplicationPage
            
            "Open" {
                val pages = Pages.createWithStaticSelenideDriver("http://localhost")
                mainPage = pages.page<DockerGettingStartedMainPage>().open().verify()
            }

            "copy docker run command to clipboard" - {
                "when click on copy button" {
                    mainPage.dockerRunCodeBlock.copyButton.click()
                }

                "then 'Copied to clipboard' displayed" {
                    mainPage.dockerRunCodeBlock.copyMessage.asClue {
                        it.shouldBe(Condition.visible)
                        it.shouldHave(Condition.text("Copied to clipboard"))
                    }
                }

                "and command '${mainPage.dockerRunCodeBlock.code.text}' copied to clipboard" {
                    Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor) shouldBe mainPage.dockerRunCodeBlock.code.text
                }
            }

            "go to 'Our application' page" {
                ourApplicationPage = mainPage.whenDo {
                    mainPage.gettingStartedNavigation.item("Our application").asClue { ourApplicationItem ->
                        ourApplicationItem.shouldNotBeNull()
                        ourApplicationItem.gettingStartedNavigation.shouldBe(Condition.visible)
                        ourApplicationItem.gettingStartedNavigation.shouldHave(Condition.text("Our application"))
                        ourApplicationItem.gettingStartedNavigation.click()
                    }
                }.thenOpen { verify() }
            }
        }
    }
}
