package org.brewcode.qa.pages.element

import com.codeborne.selenide.ElementsContainer

/**
 * Alias of [ElementsContainer].
 *
 * Inherit from it to define your block.
 */
abstract class Block : ElementsContainer() {
    fun asSelenideElement() = self
}
