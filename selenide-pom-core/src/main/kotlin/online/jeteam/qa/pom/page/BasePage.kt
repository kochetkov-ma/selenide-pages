package online.jeteam.qa.pom.page

import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.SelenideElement
import io.kotest.inspectors.forAll
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.Step
import online.jeteam.qa.pom.EMPTY
import online.jeteam.qa.pom.util.InternalExtension.mills
import online.jeteam.qa.pom.util.withPath
import org.awaitility.kotlin.await
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
open class BasePage<SELF : BasePage<SELF>> {

    private companion object {

        private var defaultOpenAssertion: BasePage<*>.() -> Unit = {
            if (path.isNotBlank()) driver.url() shouldMatch pathPattern
            if (expectedTitle.isNotBlank()) title() shouldContainIgnoringCase expectedTitle
        }

        private fun String.resolve(substitutions: Map<String, String>) =
            substitutions.toList().fold(this) { resolvedString, (k, v) -> resolvedString.replace("{$k}", v) }

        private fun String.toPattern() = replace("\\{.+?}".toRegex(), "([^/]+)")
    }

    internal lateinit var driver: SelenideDriver

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

    private var requiredElementsInfo: String = EMPTY

    val url: String by lazy { baseUrl.withPath(path.resolve(pathSubstitutions)) }

    protected open val pathPattern: Regex by lazy {
        val pathOnlyPattern = path.resolve(pathSubstitutions).toPattern()
        ".+$pathOnlyPattern.*".toRegex()
    }

    @Step("Открыта страница '{this.name}' по вычисленному адресу '{url}'")
    open fun open(
        url: String = this.url,
        pathSubstitutions: Map<String, String> = emptyMap()
    ) = let {
        val processedPath = if (pathSubstitutions.isEmpty()) url else url.resolve(pathSubstitutions)
        driver.open(processedPath)
        self()
    }

    @Step("Открыта страница '{this.name}' по указанному адресу '{url}'")
    open fun open(url: URL) = let {
        driver.open(url)
        self()
    }

    @Step("Проверено, что открыта именно страница '{this.name}'")
    open fun verifyOpen(openAssertion: (BasePage<*>) -> Unit = defaultOpenAssertion, timeoutMs: Long = driver.config().timeout()): SELF {

        await.alias("Page '$name' should be open at '$url'")
            .timeout(timeoutMs.mills)
            .pollInSameThread()
            .untilAsserted { openAssertion(this) }

        if (requiredElements.isNotEmpty()) assertRequiredElements()

        return self()
    }

    @Step("Получен title страницы '{this.name}'")
    fun title() = driver.title().orEmpty()

    override fun toString() =
        "${this::class.simpleName} (title='$expectedTitle', name='$name', path='$path', requiredElements='${requiredElements.size}')"

    @Step("Проверено, что '{this.requiredElementsInfo}' все обязательные элементы 'visible'")
    protected open fun assertRequiredElements(timeoutMs: Long = driver.config().timeout()) =
        requiredElements.forAll {
            it.shouldBe(Condition.visible, timeoutMs.mills)
        }

    @Suppress("UNCHECKED_CAST")
    protected open fun self(): SELF = this as SELF
}
