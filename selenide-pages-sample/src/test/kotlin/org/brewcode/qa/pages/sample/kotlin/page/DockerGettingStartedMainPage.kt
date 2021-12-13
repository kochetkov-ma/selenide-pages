package org.brewcode.qa.pages.sample.kotlin.page

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import io.qameta.allure.Step
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.element.Block
import org.brewcode.qa.pages.element.Blocks
import org.brewcode.qa.pages.page.BasePage

@Page(value = "Getting Started", path = "/tutorial/", expectedTitle = "Getting Started")
class DockerGettingStartedMainPage : BasePage<DockerGettingStartedMainPage>() {

    @Element(value = "Paragraphs", tagName = "h2")
    var paragraphs: ElementsCollection? = null

    lateinit var dockerRunCodeBlock: DockerRunCodeBlock

    lateinit var gettingStartedNavigation: GettingStartedNavigation
}

@Element(
    value = "Docker run command example",
    required = true,
    css = "div.highlight pre#__code_0"
)
class DockerRunCodeBlock : Block() {

    @Element(
        value = "Docker run code",
        required = true,
        tagName = "code"
    )
    lateinit var code: SelenideElement

    @Element(
        value = "Copy to clipboard button",
        required = true,
        css = "button.md-clipboard"
    )
    lateinit var copyButton: SelenideElement

    @Element(
        value = "Copied to clipboard message",
        css = "span.md-clipboard__message"
    )
    lateinit var copyMessage: SelenideElement
}

@Element(
    value = "Getting started navigation",
    css = "nav.md-nav--primary>ul"
)
class GettingStartedNavigation : Block() {

    @Element(
        value = "Пункты навигации",
        xpath = "./li"
    )
    lateinit var navigationItemList: Blocks<NavigationItem>

    @Step("Get navigation item with text '{textContains}'")
    fun item(textContains: String) = navigationItemList.first { it.self.has(Condition.text(textContains)) }
}

/**
 * Annotation [Element] can be placed at variable definition [GettingStartedNavigation.navigationItemList]
 */
class NavigationItem : Block() {

    @Element(
        value = "Navigation item",
        tagName = "a"
    )
    lateinit var gettingStartedNavigation: SelenideElement
}
