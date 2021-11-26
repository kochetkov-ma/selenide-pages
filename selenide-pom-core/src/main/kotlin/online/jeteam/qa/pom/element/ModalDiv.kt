package online.jeteam.qa.pom.element

import com.codeborne.selenide.ClickOptions
import com.codeborne.selenide.Driver

/**
 * Static Modal Window with close button and other confirmation or deny button. It is part of Web Page as a rule with `div` tag.
 * Generally all aria behind of Modal Window is inactive.
 * But you can click on inactive area and modal window will be closed.
 *
 * **! It is not Selenide [com.codeborne.selenide.Modal] Browser Alert !**
 */
open class ModalDiv : Block() {

    lateinit var driver: Driver

    fun clickOutsideLeft() {
        val offsetX = self.size.width / 2 + 10
        self.click(ClickOptions.usingDefaultMethod().offset(-offsetX, 0))
    }

    fun clickOutsideRight() {
        val offsetX = self.size.width / 2 + 10
        self.click(ClickOptions.usingDefaultMethod().offset(offsetX, 0))
    }

    fun clickOutsideTop() {
        val offsetY = self.size.height / 2 + 10
        self.click(ClickOptions.usingDefaultMethod().offset(0, offsetY))
    }

    fun clickOutsideBottom() {
        val offsetY = self.size.height / 2 + 10
        self.click(ClickOptions.usingDefaultMethod().offset(0, -offsetY))
    }
}
