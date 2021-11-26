package online.jeteam.qa.pom.page.modal

import com.codeborne.selenide.SelenideElement
import online.jeteam.qa.pom.annotation.Page
import online.jeteam.qa.pom.element.ModalDiv
import online.jeteam.qa.pom.page.BasePage
import online.jeteam.qa.pom.page.Element

@Page(value = "Getting Started", path = "/tutorial", expectedTitle = "Getting Started")
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