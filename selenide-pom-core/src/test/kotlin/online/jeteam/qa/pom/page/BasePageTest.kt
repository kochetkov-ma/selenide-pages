package online.jeteam.qa.pom.page

import com.codeborne.selenide.SelenideDriver
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import online.jeteam.qa.pom.annotation.Page
import org.awaitility.core.ConditionTimeoutException
import org.junit.jupiter.api.assertThrows

class BasePageTest : StringSpec() {

    private val driver = mockk<SelenideDriver>(relaxed = true)
    private val pages = Pages.createWithSelenideDriver(driver, "test")

    init {
        afterSpec { unmockkAll() }

        "на странице при открытии должны замениться плейсхолдеры в path" {
            val page = pages.initPage(PathPage())
            page.open()

            verify { driver.open("test/{id}/test/next/{id}/next/{uuid}") }

            page.open(pathSubstitutions = mapOf("id" to "1"))
            verify { driver.open("test/1/test/next/1/next/{uuid}") }

            page.open(pathSubstitutions = mapOf("id" to "1", "uuid" to "a1", "not_exists" to "foo"))
            verify { driver.open("test/1/test/next/1/next/a1") }
        }

        "на странице при создании должны замениться плейсхолдеры в path и всегда открываться уже с заменами" {
            val page = pages.initPage(PathPage(), "id" to "1", "uuid" to "a1", "not_exists" to "foo")
            page.open()
            verify { driver.open("test/1/test/next/1/next/a1") }

            page.open(pathSubstitutions = mapOf("id" to "1"))
            verify { driver.open("test/1/test/next/1/next/{uuid}") }
        }

        "на странице при создании должны замениться плейсхолдеры в path и проверка текущего url должна быть с заменами" {
            var page = pages.initPage(PathPage(), "id" to "1")
            every { driver.url() } returns "someBaseUrl/1/test/next/1/next/ANY_VALUE/###"
            page.verify(timeoutMs = 200)

            page = pages.initPage(PathPage(), "uuid" to "10")
            every { driver.url() } returns "someBaseUrl/ANY_VALUE/test/next/ANY_VALUE/next/10/###"
            page.verify(timeoutMs = 200)

            every { driver.url() } returns "someBaseUrl/ANY_VALUE/test/next/ANY_VALUE/next_ERROR/10/###"
            assertThrows<ConditionTimeoutException> {
                page.verify(timeoutMs = 200)
            }
        }
    }

    @Page("Замены в path", path = "/{id}/test/next/{id}/next/{uuid}")
    class PathPage : BasePage<PathPage>()
}
