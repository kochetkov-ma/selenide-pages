package org.brewcode.qa.pages.element


/**
 * Alias of [ElementsContainer].
 *
 * Inherit from it to define your block.
 */
abstract class Block : ElementsContainer() {
    fun asSelenideElement() = self
}
