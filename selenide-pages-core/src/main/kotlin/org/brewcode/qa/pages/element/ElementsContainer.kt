package org.brewcode.qa.pages.element

import com.codeborne.selenide.Container
import com.codeborne.selenide.Container.Self
import com.codeborne.selenide.SelenideElement

/**
 * Alias of [Container].
 *
 * Inherit from it to define your block.
 */
abstract class ElementsContainer : Container {

    @Self
    lateinit var self: SelenideElement
}
