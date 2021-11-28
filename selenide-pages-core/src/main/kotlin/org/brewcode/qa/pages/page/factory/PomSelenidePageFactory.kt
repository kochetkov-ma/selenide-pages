package org.brewcode.qa.pages.page.factory

import com.codeborne.selenide.Driver
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.ex.PageObjectException
import com.codeborne.selenide.impl.*
import mu.KotlinLogging
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.annotation.NotInit
import org.brewcode.qa.pages.element.Blocks
import org.brewcode.qa.pages.element.BlocksDelegate
import org.brewcode.qa.pages.page.factory.PagesAnnotations.Companion.builder
import org.brewcode.qa.pages.page.factory.PagesAnnotations.Companion.javaPageFactoryFinderAnnotation
import org.brewcode.qa.pages.page.factory.PagesAnnotations.Companion.ktPageFactoryFinderAnnotations
import org.brewcode.qa.pages.util.InternalExtension.annotation
import org.brewcode.qa.pages.util.InternalExtension.optional
import org.brewcode.qa.pages.util.InternalExtension.or
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindAll
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.FindBys
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.kotlinProperty

open class PagesSelenidePageFactory : SelenidePageFactory() {

    private val genericCache = ConcurrentHashMap<Field, Class<*>>(4)

    open fun <T> staticBlock(driver: Driver, blockClass: Class<T>, vararg args: Any): T {

        val annotation = blockClass.getAnnotation(Element::class.java).optional().orElseThrow()
        val pageFactoryFinder = annotation.ktPageFactoryFinderAnnotations.or { annotation.javaPageFactoryFinderAnnotation }.optional().orElseThrow()

        val selector = pageFactoryFinder.builder.buildIt(annotation, null)

        try {
            val self = ElementFinder.wrap(driver, selector)
            val searchContext = object : WebElementWrapper(driver, self) {}

            val argsType = args.map { it::class.java }.toTypedArray()
            val constructor: Constructor<T> = blockClass.getDeclaredConstructor(*argsType)
            constructor.isAccessible = true
            val result: T = constructor.newInstance(*args)

            initElements(driver, searchContext, result as Any, blockClass.genericInterfaces)
            return result
        } catch (e: ReflectiveOperationException) {
            throw PageObjectException("Failed to create elements static container for class '$blockClass'", e)
        }
    }

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

    private fun Any.addAlias(field: Field) {
        log.trace { "Try to add alias for '$field' ..." }

        field.kotlinProperty.optional().map { property ->
            property.elementAnnotation.or { field.elementAnnotationFromComponentClass }
                .optional()
                .ifPresent {
                    val alias = it.value.ifBlank { field.name }

                    when (this) {
                        is SelenideElement -> this.`as`(alias)
                        is ElementsContainer -> this.self.`as`(alias)
                        is Blocks<*> -> this.self.`as`(alias)
                        else -> this::class.declaredFunctions
                            .find { method -> method.name == "as" }
                            .optional()
                            .ifPresent { method -> method.call(this, alias) }
                    }

                    log.trace { "Alias '$alias' has been added to field '$field'" }
                }
        }
    }

    private fun Field.cache(genericTypes: Array<out Type>) {
        genericCache[this] = genericTypes.first() as Class<*>
    }

    private fun Field.evictCache(fallback: Class<*>): Class<out Any> = genericCache.remove(this) ?: fallback

    private val Field.isSelfElementsContainer get() = ElementsContainer::class.java == declaringClass && "self" == name

    private val Field.isWebElement get() = WebElement::class.java.isAssignableFrom(type)

    private val Field.isContainer get() = ElementsCollection::class.java.isAssignableFrom(type)

    private fun Field.isWebElementList(genericTypes: Array<out Type>) = isDecoratableList(this, genericTypes, WebElement::class.java)

    private fun Field.isElementsCollection(genericTypes: Array<out Type>) = isContainer or isWebElementList(genericTypes)

    private val Field.isElementsContainer get() = ElementsContainer::class.java.isAssignableFrom(type)

    private fun Field.isGenericElementsContainer(genericTypes: Array<out Type>) = genericTypes.size == 1
        && genericTypes.first() is Class<*>
        && ElementsContainer::class.java.isAssignableFrom(genericTypes.first() as Class<*>)

    private fun Field.isElementsContainerList(genericTypes: Array<out Type>) = isDecoratableList(this, genericTypes, ElementsContainer::class.java)

    override fun decorate(
        loader: ClassLoader,
        driver: Driver,
        searchContext: WebElementSource?,
        field: Field,
        selector: By,
        genericTypes: Array<out Type>
    ): Any? = run {
        val genericTypesInfo = genericTypes.contentToString()

        var thisSelector = selector
        val findSelector = { if (selector == EMPTY_BY) findSelector(driver, field).also { thisSelector = it } else selector }

        log.trace { "Start decorating field '$field'. Generics: $genericTypesInfo ..." }
        when {
            field.isSelfElementsContainer -> if (searchContext != null && !field.isAnnotationPresent(NotInit::class.java))
                ElementFinder.wrap(SelenideElement::class.java, searchContext) else null
            field.isWebElement -> ElementFinder.wrap(driver, searchContext, findSelector(), 0)
            field.isElementsCollection(genericTypes) -> ElementsCollection(BySelectorCollection(driver, searchContext, findSelector()))
            field.isGenericElementsContainer(genericTypes) -> field.cache(genericTypes)
                .run { createElementsContainer(driver, searchContext, field, findSelector()) }
            field.isElementsContainer -> createElementsContainer(driver, searchContext, field, findSelector())
            field.isElementsContainerList(genericTypes) -> createElementsContainerList(driver, searchContext, field, genericTypes, findSelector())
            else -> defaultFieldDecorator(driver, searchContext).decorate(loader, field)
        }
            .also { it?.addAlias(field) }
            .also { log.trace { "Finish decorating field '$field' with locator '$thisSelector' and genericTypes '$genericTypesInfo'. Element: '${it.cls}'" } }
    }

    override fun initElementsContainer(driver: Driver, field: Field, self: WebElementSource, type: Class<*>, genericTypes: Array<out Type>): ElementsContainer =
        super.initElementsContainer(driver, field, self, field.evictCache(type), genericTypes)

    override fun isDecoratableList(field: Field, genericTypes: Array<out Type>, type: Class<*>): Boolean {
        if (!Collection::class.java.isAssignableFrom(field.type)) return false

        val listType = getListGenericType(field, genericTypes)

        val hasAnnotation = field.isAnnotationPresent(FindBy::class.java)
            || field.isAnnotationPresent(FindBys::class.java)
            || field.isAnnotationPresent(FindAll::class.java)
            || field.isAnnotationPresent(Element::class.java)

        return (listType != null) && type.isAssignableFrom(listType) && hasAnnotation
    }

    override fun findSelector(driver: Driver, field: Field): By =
        if (ElementsContainer::class.java.isAssignableFrom(field.declaringClass) && "self" == field.name) EMPTY_BY
        else PagesAnnotations(field).buildBy()

    override fun initFields(
        driver: Driver,
        searchContext: WebElementSource?,
        page: Any,
        proxyIn: Class<*>,
        genericTypes: Array<out Type>
    ) {
        proxyIn.declaredFields.filter { field ->
            log.trace { "Start initializing field '$field'. Page: '$page'. Proxy: '$proxyIn'. Generics: '${genericTypes.joinToString(", ")}' ..." }

            if (field.isNotInitializedYetOnPage(page)) {
                log.trace { "Check before initialize field '$field' - passed. Is not initialize yet ..." }

                val value = decorate(page.javaClass.classLoader, driver, searchContext, field, EMPTY_BY, genericTypes)
                if (value != null) {
                    setFieldValue(page, field, value)

                    return@filter true
                } else log.trace { "Break initializing field '$field'. Is not suitable type" }
            } else log.trace { "Break initializing field '$field'. Already initialized" }

            return@filter false
        }.also { fields ->
            log.debug {
                "Finish initializing page object '$proxyIn' successfully. Initialized fields: " + fields.joinToString { it.name }.ifBlank { "no fields" }
            }
        }
    }

    private fun Field.isNotInitializedYetOnPage(page: Any) = !isInitialized(page, this)

    internal companion object PagesSelenidePageFactoryUtil {
        internal val INSTANCE = PagesSelenidePageFactory()

        private val log = KotlinLogging.logger {}
        private val Any?.cls
            get() = optional().map {
                if (Proxy.isProxyClass(it::class.java)) it::class.java.interfaces.joinToString(", ") else it::class.java.toString()
            }.orElse("NULL")
        private val KProperty<*>.elementAnnotation get() = annotation<Element>()
        private val Field.elementAnnotationFromComponentClass: Element? get() = type.getAnnotationsByType(Element::class.java).firstOrNull()
        private val EMPTY_BY = object : By() {
            override fun findElements(context: SearchContext?): MutableList<WebElement> = mutableListOf()
            override fun toString(): String = "Empty By"
        }
    }
}
