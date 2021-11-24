package online.jeteam.qa.pom.page.part

import com.codeborne.selenide.SelenideElement
import online.jeteam.qa.pom.annotation.Page
import online.jeteam.qa.pom.element.Block
import online.jeteam.qa.pom.page.BasePage
import online.jeteam.qa.pom.page.Element

/**
 * Page Object для Docker 'getting-started'
 */
@Page(value = "Стартовая страница образа getting-started", path = "/tutorial", expectedTitle = "Getting Started")
class PartialDockerTutorialPage : BasePage<PartialDockerTutorialPage>() {

    @Element(
        value = "Лого Докера",
        required = true,
        css = "a.md-header-nav__button"
    )
    lateinit var dockerLogo: SelenideElement

    lateinit var contentBlock: ContentBlock
}

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