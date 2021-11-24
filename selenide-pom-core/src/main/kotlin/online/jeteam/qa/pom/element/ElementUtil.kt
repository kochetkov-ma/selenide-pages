package online.jeteam.qa.pom.element

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement

object ElementUtil {
    fun Collection<SelenideElement>.toSelenideCollection(driver: Driver) = ElementsCollection(driver, this)

    fun ttt() {

    }
}