package org.brewcode.qa.pages.page.block

import com.codeborne.selenide.SelenideElement
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.element.Block
import org.brewcode.qa.pages.page.BasePage

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

    lateinit var infoTitleBlock: InfoBlock<TitleBlock>

    lateinit var infoCodeBlock: InfoBlock<CodeBlock>
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

@Element(
    value = "Information with title",
    required = false,
    css = ".admonition.info"
)
class InfoBlock<T : Block> : Block() {

    @Element(xpath = ".")
    lateinit var item: T
}

@Element(
    value = "Title",
    required = false,
    className = "admonition-title"
)
class TitleBlock : Block()

@Element(
    value = "Code",
    required = false,
    className = "highlight"
)
class CodeBlock : Block()