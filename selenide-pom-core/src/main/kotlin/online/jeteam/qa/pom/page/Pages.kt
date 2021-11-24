package online.jeteam.qa.pom.page

import com.codeborne.selenide.ElementsContainer
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.SelenideElement
import online.jeteam.qa.pom.annotation.Page
import online.jeteam.qa.pom.page.factory.PomSelenidePageFactory
import online.jeteam.qa.pom.util.InternalExtension.annotation
import online.jeteam.qa.pom.util.InternalExtension.optional
import online.jeteam.qa.pom.util.InternalExtension.or
import online.jeteam.qa.pom.util.InternalExtension.orNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.javaField

@Suppress("MemberVisibilityCanBePrivate")
class Pages(
    val selenideDriver: SelenideDriver,
    private val pageBaseUrl: String = selenideDriver.config().baseUrl(),
    private val pageFactory: PomSelenidePageFactory = PomSelenidePageFactory()
) {

    inline fun <reified T : BasePage<*>> page(vararg pathSubstitutions: Pair<String, String>): T = page(T::class, *pathSubstitutions)

    fun <T : BasePage<*>> page(pageClass: KClass<T>, vararg pathSubstitutions: Pair<String, String>): T {
        if (!selenideDriver.hasWebDriverStarted()) selenideDriver.open()

        return initPage(pageFactory.page(selenideDriver.driver(), pageClass.java), *pathSubstitutions)
    }

    internal fun <T : BasePage<*>> initPage(pageObject: T, vararg pathSubstitutions: Pair<String, String>) = pageObject.apply {
        driver = selenideDriver
        baseUrl = pageBaseUrl

        applyPageAnnotation(pageObject)
        applyElementAnnotation(pageObject)
        pageObject.pathSubstitutions = pathSubstitutions.toMap()
    }

    private fun <T : BasePage<*>> applyPageAnnotation(basePage: T) {
        basePage::class.findAnnotation<Page>()?.let {
            basePage.name = it.value
            basePage.path = it.path
            basePage.expectedTitle = it.expectedTitle
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : BasePage<*>> applyElementAnnotation(basePage: T) {
        basePage.requiredElements = getRequiredElements(basePage)
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

    private companion object {
        val KProperty<*>.elementAnnotation get() = annotation<Element>()
        inline fun <reified T : Annotation> KProperty<*>.elementAnnotationFromComponentClass() = javaField?.type?.kotlin?.findAnnotation<T>()
    }
}
