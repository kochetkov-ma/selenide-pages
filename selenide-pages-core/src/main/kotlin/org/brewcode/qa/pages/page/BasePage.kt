package org.brewcode.qa.pages.page

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Modal
import com.codeborne.selenide.SelenideElement
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.asClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.Step
import org.awaitility.kotlin.await
import org.brewcode.qa.pages.EMPTY
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.element.Block
import org.brewcode.qa.pages.element.ModalDiv
import org.brewcode.qa.pages.util.InternalExtension.mills
import org.brewcode.qa.pages.util.withPath
import org.openqa.selenium.WebDriverException
import java.net.URL
import kotlin.reflect.KClass

/**
 * Inherit from it to define your block.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BasePage<SELF : BasePage<SELF>> {

    @Element("Body", tagName = "body")
    lateinit var body: SelenideElement
        @JvmSynthetic
        internal set

    var baseUrl: String = EMPTY
        @JvmSynthetic
        internal set

    var expectedTitle: String = EMPTY
        @JvmSynthetic
        internal set

    var name: String = this.javaClass.name
        @JvmSynthetic
        internal set

    var pathSubstitutions: Map<String, String> = emptyMap()
        @JvmSynthetic
        internal set

    var path: String = EMPTY
        @JvmSynthetic
        internal set

    var requiredElements: Collection<SelenideElement> = emptySet()
        @JvmSynthetic
        internal set(value) {
            field = value
            requiredElementsInfo = value.joinToString(" | ")
        }

    val url: String by lazy { baseUrl.withPath(path.resolve(pathSubstitutions)) }

    protected open val urlAutoCorrect: Boolean = true

    @JvmSynthetic
    internal lateinit var driver: PageDriver

    @JvmSynthetic
    internal lateinit var sourcePages: Pages

    private var requiredElementsInfo: String = EMPTY

    fun driver() = driver.driver()

    @Step("Got page '{this.name}' title")
    fun title() = driver.title().orEmpty()

    @Step("Page '{this.name}' opened at '{url}'")
    @JvmOverloads
    open fun open(
        url: String = this.url,
        pathSubstitutions: Map<String, String> = emptyMap()
    ) = let {
        val processedUrl = if (pathSubstitutions.isEmpty()) url else url.resolve(pathSubstitutions)
        if (processedUrl.endsWith('/') && !urlAutoCorrect)
            driver.open(processedUrl)
        else try {
            driver.open(processedUrl)
        } catch (error: WebDriverException) {

            val urlWithSlash = "$processedUrl/"
            log.error(error) {
                "<<<< ATTENTION >>>>" +
                    "\nCannot open URL '$processedUrl'. " +
                    "BasePage field urlAutoCorrect is $urlAutoCorrect. We noticed it's without '/' at the end. We will try open this URL '$urlWithSlash'. " +
                    "If the try will be success, we recommend add '/' to each page path!\n"
            }
            driver.open(urlWithSlash)
        }

        self()
    }

    @Step("Page '{this.name}' opened at '{url}'")
    open fun open(url: URL) = let {
        driver.open(url)
        self()
    }

    @Step("Verified that page '{this.name}' opened successfully")
    @JvmOverloads
    open fun verify(openAssertion: (BasePage<*>) -> Unit = defaultOpenAssertion, timeoutMs: Long = driver.timeout()): SELF {

        await.alias("Page '$name' should be open at '$url'")
            .timeout(timeoutMs.mills)
            .pollInSameThread()
            .untilAsserted { openAssertion(this) }

        if (requiredElements.isNotEmpty()) assertRequiredElements()

        return self()
    }

    @SafeVarargs
    open fun <T : ModalDiv> modal(modalClass: Class<T>, vararg args: Any): T = sourcePages.pageFactory.staticBlock(driver(), modalClass, *args)
        .also { it.driver = driver() }

    /**
     * Experimental.
     *
     * Violate pages architecture because [Block] should be created as part of [BasePage].
     * Allow only in subclasses.
     *
     * Please, use [Modal] for creating static blocks via [modal].
     */
    protected open fun <T : Block> block(blockClass: Class<T>, vararg args: Any): T = sourcePages.pageFactory.staticBlock(driver(), blockClass, *args)

    ///////////////////////
    // region KOTLIN DSL //
    ///////////////////////

    @JvmSynthetic
    open fun whenDo(actionFunc: SELF.() -> Unit): SELF = self().apply { actionFunc() }

    @JvmSynthetic
    open fun whenDoWithRetry(onFail: SELF.(Throwable) -> Unit = { Thread.sleep(100) }, actionFunc: SELF.() -> Unit): SELF =
        whenDo {
            runCatching(actionFunc).onFailure {
                onFail(it)
                actionFunc()
            }
        }

    @JvmSynthetic
    inline fun <reified T : BasePage<T>> thenOpen(pathSubstitutions: Map<String, String> = emptyMap(), noinline actionFunc: T.() -> Unit = {}): T =
        thenOpen(T::class, pathSubstitutions, actionFunc)

    @JvmSynthetic
    fun <T : BasePage<T>> thenOpen(newPageClass: Class<T>, pathSubstitutions: Map<String, String> = emptyMap(), actionFunc: T.() -> Unit = {}): T =
        thenOpen(newPageClass.kotlin, pathSubstitutions, actionFunc)

    @JvmSynthetic
    open fun <T : BasePage<T>> thenOpen(newPageClass: KClass<T>, pathSubstitutions: Map<String, String> = emptyMap(), actionFunc: T.() -> Unit = {}): T =
        sourcePages.page(newPageClass, *pathSubstitutions.toList().toTypedArray()).verify().apply { asClue(actionFunc) }

    // endregion

    @Step("Verified that '{this.requiredElementsInfo}' all required elements are 'visible'")
    protected open fun assertRequiredElements(timeoutMs: Long = driver.timeout()) =
        requiredElements.forAll {
            it.shouldBe(Condition.visible, timeoutMs.mills)
        }

    protected open val pathPattern: Regex by lazy {
        val pathOnlyPattern = path.resolve(pathSubstitutions).toPattern()
        ".+$pathOnlyPattern.*".toRegex()
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun self(): SELF = this as SELF

    override fun toString() =
        "${this::class.simpleName} (title='$expectedTitle', name='$name', path='$path', requiredElements='${requiredElements.size}')"

    private companion object {
        private val log = KotlinLogging.logger {}

        var defaultOpenAssertion: BasePage<*>.() -> Unit = {
            if (path.isNotBlank()) driver.url() shouldMatch pathPattern
            if (expectedTitle.isNotBlank()) title() shouldContainIgnoringCase expectedTitle
        }

        fun String.resolve(substitutions: Map<String, String>) =
            substitutions.toList().fold(this) { resolvedString, (k, v) -> resolvedString.replace("{$k}", v) }

        fun String.toPattern() = replace("\\{.+?}".toRegex(), "([^/]+)")
    }
}
