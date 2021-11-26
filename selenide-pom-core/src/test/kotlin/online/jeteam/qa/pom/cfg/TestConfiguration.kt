package online.jeteam.qa.pom.cfg

import com.codeborne.selenide.Configuration
import io.kotest.core.config.AbstractProjectConfig
import online.jeteam.qa.pom.page.Pages
import org.testcontainers.containers.GenericContainer

@Suppress("HttpUrlsUsage")
object TestConfiguration : AbstractProjectConfig() {
    private val container: GenericContainer<Nothing> = GenericContainer<Nothing>("docker/getting-started")
        .apply { withExposedPorts(80) }

    lateinit var pages: Pages

    // override fun listeners(): List<Listener> = listOf(container.perProject("getting-started"))

    override fun beforeAll() {
        Configuration.baseUrl = "http://" + if (container.isRunning) container.host + ":" + container.firstMappedPort else "localhost"
        pages = Pages.createWithStaticSelenideDriver()
        super.beforeAll()
    }
}