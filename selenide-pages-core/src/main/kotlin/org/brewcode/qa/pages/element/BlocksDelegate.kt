package org.brewcode.qa.pages.element

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.ElementsContainerCollection
import com.codeborne.selenide.impl.PageObjectFactory
import com.codeborne.selenide.impl.WebElementSource
import org.brewcode.qa.pages.annotation.NotInit
import org.openqa.selenium.By
import java.lang.reflect.Field
import java.lang.reflect.Type

internal class BlocksDelegate(
    pageFactory: PageObjectFactory,
    driver: Driver,
    parent: WebElementSource?,
    field: Field,
    listType: Class<*>,
    genericTypes: Array<out Type>,
    selector: By,
    alias: String = "-",
) : ElementsContainerCollection(pageFactory, driver, parent, field, listType, genericTypes, selector) {

    internal companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <T : ElementsContainer> ElementsContainerCollection.wrap(): MutableList<T> = this as MutableList<T>
    }

    @NotInit
    internal val self: ElementsCollection = ElementsCollection(BySelectorCollection(driver, parent, selector)).`as`(alias)
}