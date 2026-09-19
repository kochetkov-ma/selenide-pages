package org.brewcode.qa.pages.annotation

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class ElementFindByBuilderTest : StringSpec({

    "findAll combines every alternative locator" {
        // GIVEN
        val field = LocatorFields::class.java.getDeclaredField("alternatives")
        val annotation = field.getAnnotation(Element::class.java)
        withClue("alternatives fixture contains two findAll locators") { annotation?.findAll?.size shouldBe 2 }

        // WHEN
        val selector = ElementFindByBuilder().buildIt(requireNotNull(annotation), field)

        // THEN
        withClue("findAll retains both selectors with OR semantics") {
            selector.toString() shouldBe "By.all({By.id: first,By.cssSelector: .second})"
        }
    }

    "findBys chains the locators in order" {
        // GIVEN
        val field = LocatorFields::class.java.getDeclaredField("nested")
        val annotation = field.getAnnotation(Element::class.java)
        withClue("nested fixture contains two findBys locators") { annotation?.findBys?.size shouldBe 2 }

        // WHEN
        val selector = ElementFindByBuilder().buildIt(requireNotNull(annotation), field)

        // THEN
        withClue("findBys retains parent and child selectors with chain semantics") {
            selector.toString() shouldBe "By.chained({By.id: parent,By.cssSelector: .child})"
        }
    }

}) {
    private class LocatorFields {
        @field:Element(findAll = [FindBy(id = "first"), FindBy(css = ".second")])
        lateinit var alternatives: WebElement

        @field:Element(findBys = [FindBy(id = "parent"), FindBy(css = ".child")])
        lateinit var nested: WebElement
    }
}
