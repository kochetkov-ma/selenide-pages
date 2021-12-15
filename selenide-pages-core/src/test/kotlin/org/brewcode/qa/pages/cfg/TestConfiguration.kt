package org.brewcode.qa.pages.cfg

import com.codeborne.selenide.Configuration
import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.testcontainers.perProject
import io.kotest.mpp.env
import org.brewcode.qa.pages.page.Pages
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chromium.ChromiumOptions
import org.openqa.selenium.remote.Browser
import org.openqa.selenium.remote.CapabilityType
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName

@Suppress("HttpUrlsUsage")
object TestConfiguration : AbstractProjectConfig() {

    @ExperimentalKotest
    override val concurrentSpecs: Int = 4

    override val parallelism: Int = 4

    private var testNetwork: Network = Network.newNetwork()

    private var alias = "getting-started"

    private val container: GenericContainer<Nothing> = GenericContainer<Nothing>("docker/getting-started").apply {
        withExposedPorts(80)
        withNetwork(testNetwork)
        withNetworkAliases(alias)
    }

    private val browser: BrowserWebDriverContainer<Nothing> =
        BrowserWebDriverContainer<Nothing>(DockerImageName.parse("selenium/standalone-chrome:4.1.0"))
            .apply {
                withCapabilities(
                    ChromiumOptions<ChromeOptions>(CapabilityType.BROWSER_NAME, Browser.CHROME.browserName(), ChromeOptions.CAPABILITY).apply {
                        addEnv("JAVA_OPTS", "-Dwebdriver.chrome.whitelistedIps=")
                        addEnv("SE_NODE_SESSION_TIMEOUT", "120")
                        addEnv("SE_NODE_MAX_SESSIONS", "10")
                        addEnv("SE_NODE_OVERRIDE_MAX_SESSIONS", "true")
                        addArguments("--disable-gpu")
                        addArguments("--no-sandbox")
                        addArguments("--disable-dev-shm-usage")
                    }
                )
                withNetwork(testNetwork)
                withExposedPorts(4444, 7900)
            }

    lateinit var pages: Pages

    override fun listeners() = buildList {
        add(container.perProject(alias))
        if (isCI) add(browser.perProject("chrome"))
    }

    override fun beforeAll() {
        Configuration.baseUrl = "http://" + when {
            container.isCreated and isNotCI -> container.host + ":" + container.firstMappedPort
            container.isCreated and isCI -> alias
            else -> "localhost"
        }

        if (isCI) Configuration.remote = "http://" + browser.host + ":" + browser.firstMappedPort

        println("GRID URL: " + Configuration.remote)

        pages = Pages.createWithStaticSelenideDriver()
    }

    private val isCI = env("CI") != null
    private val isNotCI = !isCI
}