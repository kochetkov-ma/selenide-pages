package online.jeteam.qa.pom.page

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Modal
import com.codeborne.selenide.SelenideElement
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.Step
import online.jeteam.qa.pom.EMPTY
import online.jeteam.qa.pom.element.Block
import online.jeteam.qa.pom.element.ModalDiv
import online.jeteam.qa.pom.page.factory.PomSelenidePageFactory
import online.jeteam.qa.pom.util.InternalExtension.mills
import online.jeteam.qa.pom.util.withPath
import org.awaitility.kotlin.await
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
open class BasePage<SELF : BasePage<SELF>> {

    @Element("Body", tagName = "body")
    lateinit var body: SelenideElement

    var baseUrl: String = EMPTY
        internal set

    var expectedTitle: String = EMPTY
        internal set

    var name: String = this.javaClass.name
        internal set

    var pathSubstitutions: Map<String, String> = emptyMap()
        internal set

    var path: String = EMPTY
        internal set

    var requiredElements: Collection<SelenideElement> = emptySet()
        internal set(value) {
            field = value
            requiredElementsInfo = value.joinToString(" | ")
        }

    val url: String by lazy { baseUrl.withPath(path.resolve(pathSubstitutions)) }

    internal lateinit var driver: PageDriver

    internal lateinit var sourcePageFactory: PomSelenidePageFactory

    private var requiredElementsInfo: String = EMPTY

    fun driver() = driver.driver()

    @Step("Got page '{this.name}' title")
    fun title() = driver.title().orEmpty()

    @Step("Page '{this.name}' opened at '{url}'")
    open fun open(
        url: String = this.url,
        pathSubstitutions: Map<String, String> = emptyMap()
    ) = let {
        val processedPath = if (pathSubstitutions.isEmpty()) url else url.resolve(pathSubstitutions)
        driver.open(processedPath)
        self()
    }

    @Step("Page '{this.name}' opened at '{url}'")
    open fun open(url: URL) = let {
        driver.open(url)
        self()
    }

    @Step("Verified that page '{this.name}' opened successfully")
    open fun verifyOpen(openAssertion: (BasePage<*>) -> Unit = defaultOpenAssertion, timeoutMs: Long = driver.timeout()): SELF {

        await.alias("Page '$name' should be open at '$url'")
            .timeout(timeoutMs.mills)
            .pollInSameThread()
            .untilAsserted { openAssertion(this) }

        if (requiredElements.isNotEmpty()) assertRequiredElements()

        return self()
    }

    open fun <T : ModalDiv> modal(modalClass: Class<T>, vararg args: Any): T = sourcePageFactory.staticBlock(driver(), modalClass, *args)
        .also { it.driver = driver() }

    /**
     * Experimental.
     *
     * Violate POM architecture because [Block] should be created as part of [BasePage].
     * Allow only in subclasses.
     *
     * Please, use [Modal] for creating static blocks via [modal].
     */
    protected open fun <T : Block> block(blockClass: Class<T>, vararg args: Any): T = sourcePageFactory.staticBlock(driver(), blockClass, *args)

    @Step("Verified that '{this.requiredElementsInfo}' all required elements are 'visible'")
    protected open fun assertRequiredElements(timeoutMs: Long = driver.timeout()) =
        requiredElements.forAll {
            it.shouldBe(Condition.visible, timeoutMs.mills)
        }

    protected open val pathPattern: Regex by lazy {
        val pathOnlyPattern = path.resolve(pathSubstitutions).toPattern()
        ".+$pathOnlyPattern.*".toRegex()
    }

    override fun toString() =
        "${this::class.simpleName} (title='$expectedTitle', name='$name', path='$path', requiredElements='${requiredElements.size}')"

    @Suppress("UNCHECKED_CAST")
    protected open fun self(): SELF = this as SELF

    private companion object {
        var defaultOpenAssertion: BasePage<*>.() -> Unit = {
            if (path.isNotBlank()) driver.url() shouldMatch pathPattern
            if (expectedTitle.isNotBlank()) title() shouldContainIgnoringCase expectedTitle
        }

        fun String.resolve(substitutions: Map<String, String>) =
            substitutions.toList().fold(this) { resolvedString, (k, v) -> resolvedString.replace("{$k}", v) }

        fun String.toPattern() = replace("\\{.+?}".toRegex(), "([^/]+)")
    }
}
