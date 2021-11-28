package org.brewcode.qa.pages.page.modal

import com.codeborne.selenide.SelenideElement
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.element.ModalDiv
import org.brewcode.qa.pages.page.BasePage

@Page(value = "Getting Started", path = "/tutorial/", expectedTitle = "Getting Started")
class ModalDockerTutorialPage : BasePage<ModalDockerTutorialPage>() {

    @Element(
        "Search",
        required = true,
        className = "md-search__input"
    )
    lateinit var search: SelenideElement

    @Element(
        css = ".md-sidebar--secondary a[href='#the-command-you-just-ran']"
    )
    lateinit var link: SelenideElement
}

@Element(
    "Search modal",
    className = "md-search__inner"
)
class SearchModal : ModalDiv() {
    @Element(
        "Search input",
        tagName = "form"
    )
    lateinit var input: SelenideElement

    @Element(
        "Search results",
        className = "md-search__output"
    )
    lateinit var result: SelenideElement
}