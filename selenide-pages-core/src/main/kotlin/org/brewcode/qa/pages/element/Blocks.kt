package org.brewcode.qa.pages.element

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsContainer
import org.brewcode.qa.pages.annotation.NotInit
import org.brewcode.qa.pages.element.BlocksDelegate.Companion.wrap

/**
 * Collection of [Block]
 *
 * Use [self] to access source selenide [ElementsCollection] and verify it.
 *
 * **! Verify expected source collection size via [self] before using !**
 *
 * Example:
 *
 * ```kotlin
 * yourPage.yourBlockCollection.self
 *      .shouldHave(CollectionCondition.sizeGreaterThan(<your_expected_size>))
 *
 * val singleBlock = yourPage.yourBlockCollection.get(2)
 * ```
 */
class Blocks<T : ElementsContainer> internal constructor(
    delegate: BlocksDelegate,
    @NotInit
    val self: ElementsCollection = delegate.self
) : MutableList<T> by delegate.wrap() {

    /**
     * Alias of [self]
     */
    fun asElementsCollection() = self
}