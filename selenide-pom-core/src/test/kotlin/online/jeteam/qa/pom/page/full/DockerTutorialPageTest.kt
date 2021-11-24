package online.jeteam.qa.pom.page.full

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import online.jeteam.qa.pom.cfg.TestConfiguration

open class DockerTutorialPageTest : FreeSpec() {

    private val pages = TestConfiguration.pages

    init {

        "начальная страница должна успешно инициализироваться, загрузиться и левое меню должно работать" - {
            lateinit var tutorialPage: DockerTutorialPage

            "страница DockerTutorialPage открыта" {
                tutorialPage = pages.page<DockerTutorialPage>().open().verifyOpen()
                Selenide.executeJavaScript<Any?>("console.error('Error message')")
                Selenide.executeJavaScript<Any?>("console.log('Info message')")
            }

            "на странице отсутствуют несуществующие элементы" {
                tutorialPage.notExistsElementsCollection.shouldBe(CollectionCondition.empty)
                tutorialPage.notExistsElement.shouldBe(Condition.not(Condition.visible))
            }

            "на странице видны все элементы, которые представляют из себя список контейнеров TppElementsContainer" {
                tutorialPage.gettingStartedNavigation.self.shouldBe(Condition.visible)

                tutorialPage.gettingStartedNavigation.navigationItemList.self.shouldHave(CollectionCondition.sizeGreaterThan(5))

                tutorialPage.gettingStartedNavigation.navigationItemList.forEach {
                    it.gettingStartedNavigation.shouldBe(Condition.visible)
                }
            }

            "выполнен переход на пункт 'What Next?' и проверен заголовок статьи" {
                tutorialPage.gettingStartedNavigation.item("What Next")
                    .map { it.gettingStartedNavigation.click() }.isPresent.shouldBeTrue()

                tutorialPage.contentBlock.header.shouldHave(Condition.exactText("What Next?"))
            }
        }
    }
}
