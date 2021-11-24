package online.jeteam.qa.pom.page.factory

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.SelenidePageFactory
import com.codeborne.selenide.impl.WebElementSource
import online.jeteam.qa.pom.element.Blocks
import online.jeteam.qa.pom.element.BlocksDelegate
import online.jeteam.qa.pom.page.Element
import online.jeteam.qa.pom.util.InternalExtension.annotation
import online.jeteam.qa.pom.util.InternalExtension.optional
import online.jeteam.qa.pom.util.InternalExtension.or
import org.openqa.selenium.By
import org.openqa.selenium.support.FindAll
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.FindBys
import java.lang.reflect.Field
import java.lang.reflect.Type
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.kotlinProperty

open class PomSelenidePageFactory : SelenidePageFactory() {

    override fun createElementsContainerList(
        driver: Driver,
        searchContext: WebElementSource?,
        field: Field,
        genericTypes: Array<out Type>,
        selector: By
    ): MutableList<ElementsContainer> {
        val listType = getListGenericType(field, genericTypes) ?: throw IllegalArgumentException("Cannot detect list type for $field")

        return Blocks(BlocksDelegate(this, driver, searchContext, field, listType, genericTypes, selector))
    }

    override fun decorate(
        loader: ClassLoader,
        driver: Driver,
        searchContext: WebElementSource?,
        field: Field,
        selector: By,
        genericTypes: Array<out Type>
    ): Any? = super.decorate(loader, driver, searchContext, field, selector, genericTypes)?.also { initializedField ->

        field.kotlinProperty.optional().map { property ->
            property.elementAnnotation.or { field.elementAnnotationFromComponentClass }
                .optional()
                .ifPresent {
                    val alias = it.value.ifBlank { field.name }

                    when (initializedField) {
                        is SelenideElement -> initializedField.`as`(alias)
                        else -> initializedField::class.declaredFunctions
                            .findLast { method -> method.name == "as" }
                            .optional()
                            .ifPresent { method -> method.call(initializedField, alias) }
                    }
                }
        }
    }

    override fun isDecoratableList(field: Field, genericTypes: Array<out Type>, type: Class<*>): Boolean {
        if (!Collection::class.java.isAssignableFrom(field.type)) return false

        val listType = getListGenericType(field, genericTypes)

        val hasAnnotation = field.isAnnotationPresent(FindBy::class.java)
            || field.isAnnotationPresent(FindBys::class.java)
            || field.isAnnotationPresent(FindAll::class.java)
            || field.isAnnotationPresent(Element::class.java)

        return (listType != null) && type.isAssignableFrom(listType) && hasAnnotation
    }

    override fun findSelector(driver: Driver, field: Field): By = PomAnnotations(field).buildBy()

    private companion object {
        val KProperty<*>.elementAnnotation get() = annotation<Element>()
        val Field.elementAnnotationFromComponentClass get() = type.getAnnotation(Element::class.java)
    }
}