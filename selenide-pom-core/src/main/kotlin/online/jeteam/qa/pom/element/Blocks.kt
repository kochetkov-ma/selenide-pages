package online.jeteam.qa.pom.element

import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsContainer
import online.jeteam.qa.pom.annotation.NotPom
import online.jeteam.qa.pom.element.BlocksDelegate.Companion.wrap

class Blocks<T : ElementsContainer> internal constructor(
    delegate: BlocksDelegate,
    @NotPom
    val self: ElementsCollection = delegate.self
) : MutableList<T> by delegate.wrap() {
    fun asElementsCollection() = self
}