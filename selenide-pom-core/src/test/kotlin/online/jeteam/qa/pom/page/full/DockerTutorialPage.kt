package online.jeteam.qa.pom.page.full

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import io.qameta.allure.Step
import online.jeteam.qa.pom.annotation.Page
import online.jeteam.qa.pom.element.Block
import online.jeteam.qa.pom.element.Blocks
import online.jeteam.qa.pom.page.BasePage
import online.jeteam.qa.pom.page.Element
import java.util.*

/**
 * Page Object для Docker 'getting-started'
 */
@Page(value = "Стартовая страница образа getting-started", path = "tutorial", expectedTitle = "Getting Started")
class DockerTutorialPage : BasePage<DockerTutorialPage>() {

    @Element(
        value = "Лого Докера",
        required = true,
        css = "a.md-header-nav__button"
    )
    lateinit var dockerLogo: SelenideElement

    lateinit var contentBlock: ContentBlock

    @Element(
        value = "Навигация Getting Started",
        css = "nav.md-nav--primary>ul"
    )
    lateinit var gettingStartedNavigation: GettingStartedNavigation

    @Element(
        value = "Несуществующий список элементов",
        id = "not_exists_id"
    )
    lateinit var notExistsElementsCollection: ElementsCollection

    @Element(
        value = "Несуществующий одиночный элемент",
        id = "not_exists_id"
    )
    lateinit var notExistsElement: SelenideElement
}

/**
 * Блок
 */
class GettingStartedNavigation : Block() {

    @Element(
        value = "Пункты навигации",
        xpath = "./li"
    )
    lateinit var navigationItemList: Blocks<NavigationItem>

    @Step("Получен элемент навигации с текстом '{textContains}'")
    fun item(textContains: String) =
        Optional.ofNullable(
            navigationItemList.first { it.self.has(Condition.text(textContains)) }
        )
}

/**
 * Блок
 */
@Element(
    value = "Пункты навигации. Переопределен на классе. Отображаться не должен, т.к. приоритет у поля",
    xpath = "./li"
)
class NavigationItem : Block() {

    @Element(
        value = "Элемент навигации",
        tagName = "a"
    )
    lateinit var gettingStartedNavigation: SelenideElement
}

/**
 * Блок
 */
@Element(
    value = "Блок контента. На классе вместо поля",
    required = true,
    tagName = "article"
)
class ContentBlock : Block() {

    @Element(
        value = "Заголовок",
        required = true,
        tagName = "h1"
    )
    lateinit var header: SelenideElement
}
