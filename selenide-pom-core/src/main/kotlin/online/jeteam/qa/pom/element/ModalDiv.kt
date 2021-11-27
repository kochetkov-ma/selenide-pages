package online.jeteam.qa.pom.element

import com.codeborne.selenide.ClickOptions
import com.codeborne.selenide.Driver

/**
 * Static Modal Window with close button and other confirmation or deny button. It is part of Web Page as a rule with `div` tag.
 * Generally all aria behind of Modal Window is inactive.
 * But you can click on inactive area and modal window will be closed.
 *
 * Inherit from it to define your block.
 *
 * **! It is not Selenide [com.codeborne.selenide.Modal] Browser Alert !**
 */
open class ModalDiv : Block() {

    lateinit var driver: Driver

    fun clickOutsideLeft(extraOffset: Int = EXTRA_OFFSET) {
        val offsetX = self.size.width / 2 + extraOffset
        self.click(ClickOptions.usingDefaultMethod().offset(-offsetX, 0))
    }

    fun clickOutsideRight(extraOffset: Int = EXTRA_OFFSET) {
        val offsetX = self.size.width / 2 + extraOffset
        self.click(ClickOptions.usingDefaultMethod().offset(offsetX, 0))
    }

    fun clickOutsideTop(extraOffset: Int = EXTRA_OFFSET) {
        val offsetY = self.size.height / 2 + extraOffset
        self.click(ClickOptions.usingDefaultMethod().offset(0, offsetY))
    }

    fun clickOutsideBottom(extraOffset: Int = EXTRA_OFFSET) {
        val offsetY = self.size.height / 2 + extraOffset
        self.click(ClickOptions.usingDefaultMethod().offset(0, -offsetY))
    }

    private companion object {
        const val EXTRA_OFFSET = 10
    }
}
