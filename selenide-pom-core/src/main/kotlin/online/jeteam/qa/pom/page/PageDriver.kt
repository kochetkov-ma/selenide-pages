package online.jeteam.qa.pom.page

import com.codeborne.selenide.*
import online.jeteam.qa.pom.page.PageDriver.PageDriverFactory
import java.net.URL

/**
 * Pom interface with necessary only methods for acting with Selenium Driver.
 *
 * Use static [PageDriverFactory] for instancing PageDriver from selenide driver.
 *
 * @see PageDriverFactory
 */
interface PageDriver {

    /**
     * [SelenideDriver.driver]
     */
    fun driver(): Driver

    /**
     * [SelenideDriver.hasWebDriverStarted]
     */
    fun hasWebDriverStarted(): Boolean

    /**
     * [SelenideDriver.open]
     */
    fun open()

    /**
     * [SelenideDriver.url]
     */
    fun url(): String

    /**
     * [SelenideDriver.open]
     */
    fun open(relativeOrAbsoluteUrl: String)

    /**
     * [SelenideDriver.open]
     */
    fun open(absoluteUrl: URL)

    /**
     * [SelenideDriver.open]
     */
    fun title(): String?

    /**
     * [Config.timeout]
     */
    fun timeout(): Long

    /**
     * [Config.baseUrl]
     */
    fun baseUrl(): String

    /**
     * Static [PageDriverFactory] for instancing PageDriver from selenide driver.
     *
     * @see PageDriver
     */
    companion object PageDriverFactory {

        /**
         * Create [PageDriver] from [SelenideDriver]
         */
        fun SelenideDriver.asPageDriver(): PageDriver {
            val delegate = this

            return object : PageDriver {
                override fun driver(): Driver = delegate.driver()

                override fun hasWebDriverStarted(): Boolean = delegate.hasWebDriverStarted()

                override fun open(): Unit = delegate.open()

                override fun open(relativeOrAbsoluteUrl: String) = delegate.open(relativeOrAbsoluteUrl)

                override fun open(absoluteUrl: URL) = delegate.open(absoluteUrl)

                override fun url(): String = delegate.url()

                override fun title(): String? = delegate.title()

                override fun timeout(): Long = delegate.config().timeout()

                override fun baseUrl(): String = delegate.config().baseUrl()

            }
        }

        /**
         * Create [PageDriver] from static [Selenide]
         */
        fun selenideAsPageDriver(): PageDriver = object : PageDriver {

            override fun driver(): Driver = WebDriverRunner.driver()

            override fun hasWebDriverStarted(): Boolean = WebDriverRunner.hasWebDriverStarted()

            override fun open(): Unit = Selenide.open()

            override fun open(relativeOrAbsoluteUrl: String) = Selenide.open(relativeOrAbsoluteUrl)

            override fun open(absoluteUrl: URL) = Selenide.open(absoluteUrl)

            override fun url(): String = WebDriverRunner.url()

            override fun title(): String? = Selenide.title()

            override fun timeout(): Long = Configuration.timeout

            override fun baseUrl(): String = Configuration.baseUrl
        }
    }
}