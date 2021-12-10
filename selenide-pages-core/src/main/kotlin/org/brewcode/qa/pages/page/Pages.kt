package org.brewcode.qa.pages.page

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.SelenideElement
import mu.KotlinLogging.logger
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.page.PageDriver.PageDriverFactory.asPageDriver
import org.brewcode.qa.pages.page.PageDriver.PageDriverFactory.selenideAsPageDriver
import org.brewcode.qa.pages.page.Pages.PagesFactory
import org.brewcode.qa.pages.page.factory.PagesSelenidePageFactory
import org.brewcode.qa.pages.util.InternalExtension.annotation
import org.brewcode.qa.pages.util.InternalExtension.optional
import org.brewcode.qa.pages.util.InternalExtension.or
import org.brewcode.qa.pages.util.InternalExtension.orNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaField

/**
 * Main class for creating and manage page objects. All page object inherit [Page].
 *
 * Also see static constructors: [PagesFactory.createWithStaticSelenideDriver] or [PagesFactory.createWithSelenideDriver].
 *
 * @param pageDriver [PageDriver] Lightweight pages WebDriver abstraction.
 * Use [PageDriver.selenideAsPageDriver] or  [PageDriver.asPageDriver] to create from familiar Selenide classes.
 *
 * @param pageBaseUrl Base app url for opening and verifying address.
 *
 * @param pageFactory Default is PagesSelenidePageFactory. Use default value if you don't extend [PagesSelenidePageFactory].
 *
 * @constructor Main constructor. See [Pages].
 * Or use static one [PageDriver.selenideAsPageDriver] or  [PageDriver.asPageDriver]
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Pages @JvmOverloads constructor(
    val pageDriver: PageDriver,
    private val pageBaseUrl: String = pageDriver.baseUrl(),
    @JvmSynthetic
    internal val pageFactory: PagesSelenidePageFactory = PagesSelenidePageFactory.INSTANCE
) {

    init {
        log.info { "Pages created. Base URL is '${pageBaseUrl}'. Driver is '${pageDriver::class}'. Page factory is '${pageFactory::class}'" }
    }

    inline fun <reified T : BasePage<*>> page(vararg pathSubstitutions: Pair<String, String>): T = page(T::class, *pathSubstitutions)

    @SafeVarargs
    fun <T : BasePage<*>> page(pageClass: Class<T>, vararg pathSubstitutions: Pair<String, String>): T = page(pageClass.kotlin, *pathSubstitutions)

    @JvmSynthetic
    fun <T : BasePage<*>> page(pageClass: KClass<T>, vararg pathSubstitutions: Pair<String, String>): T {
        if (!pageDriver.hasWebDriverStarted()) {
            log.debug { "Driver is not open yet. Will open on blank page" }
            pageDriver.open()
        }

        log.debug { "Initializing page '$pageClass' started ..." }
        return initPage(pageFactory.page(pageDriver.driver(), pageClass.java), *pathSubstitutions)
    }

    @JvmSynthetic
    internal fun <T : BasePage<*>> initPage(pageObject: T, vararg pathSubstitutions: Pair<String, String>) = pageObject.apply {
        driver = pageDriver
        baseUrl = pageBaseUrl
        sourcePages = this@Pages

        applyPageAnnotation(pageObject)
        applyElementAnnotation(pageObject)
        pageObject.pathSubstitutions = pathSubstitutions.toMap()

        log.debug { "Initializing page '$pageObject' finished" }
    }

    private fun <T : BasePage<*>> applyPageAnnotation(basePage: T) {
        basePage::class.findAnnotation<Page>()?.let {
            basePage.name = it.value
            basePage.path = it.path
            basePage.expectedTitle = it.expectedTitle

            log.debug { "Applied annotation '$it' to page '$basePage'" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : BasePage<*>> applyElementAnnotation(basePage: T) {
        basePage.requiredElements = getRequiredElements(basePage)

        log.debug {
            val info = basePage.requiredElements.joinToString { it.alias.orEmpty() }
            "Applied requiredElements '${info}' to page '$basePage'"
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRequiredElements(page: Any?): List<SelenideElement> =
        if (page == null)
            emptyList()
        else
            page::class.declaredMemberProperties
                .mapNotNull { field ->
                    field.elementAnnotation
                        .or { field.elementAnnotationFromComponentClass() }
                        .optional()
                        .map { field to it }
                        .orNull()
                }.flatMap { e ->
                    when {
                        e.first.returnType.isSubtypeOf(SelenideElement::class.createType()) ->
                            listOf((e.first as KProperty1<Any, *>).get(page) as SelenideElement).filter { e.second.required }

                        e.first.returnType.isSubtypeOf(ElementsContainer::class.createType()) ->
                            getRequiredElements((e.first as KProperty1<Any, *>).get(page)).plus(
                                if (e.second.required)
                                    listOf(((e.first as KProperty1<Any, *>).get(page) as ElementsContainer).self)
                                else
                                    emptyList()
                            )

                        else -> emptyList()
                    }
                }

    companion object PagesFactory {

        /**
         * Main class for creating and manage page objects. All page object inherit [Page].
         *
         * Alias of main constructor, just apply [PageDriver.asPageDriver] for [selenideDriver].
         *
         * @param selenideDriver [SelenideDriver] Further transformed to PageDriver by [PageDriver.asPageDriver].
         *
         * @param pageBaseUrl Base app url for opening and verifying address.
         *
         * @param pageFactory Default is PagesSelenidePageFactory. Use default value if you don't extend [PagesSelenidePageFactory].
         */
        @JvmOverloads
        fun createWithSelenideDriver(
            selenideDriver: SelenideDriver,
            pageBaseUrl: String = selenideDriver.config().baseUrl(),
            pageFactory: PagesSelenidePageFactory = PagesSelenidePageFactory()
        ) = Pages(selenideDriver.asPageDriver(), pageBaseUrl, pageFactory)

        /**
         * Main class for creating and manage page objects. All page object inherit [Page].
         *
         * Alias of main constructor, just apply [PageDriver.selenideAsPageDriver] and create static [PageDriver].
         *
         * @param pageBaseUrl Base app url for opening and verifying address.
         *
         * @param pageFactory Default is PagesSelenidePageFactory. Use default value if you don't extend [PagesSelenidePageFactory].
         */
        @JvmOverloads
        fun createWithStaticSelenideDriver(
            pageBaseUrl: String = Configuration.baseUrl,
            pageFactory: PagesSelenidePageFactory = PagesSelenidePageFactory()
        ) = Pages(selenideAsPageDriver(), pageBaseUrl, pageFactory)

        private val log = logger {}
        private val KProperty<*>.elementAnnotation get() = annotation<Element>()
        private inline fun <reified T : Annotation> KProperty<*>.elementAnnotationFromComponentClass() = javaField?.type?.kotlin?.findAnnotation<T>()
    }
}
