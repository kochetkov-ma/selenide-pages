package org.brewcode.qa.pages.cfg

import com.codeborne.selenide.Configuration
import io.kotest.core.config.AbstractProjectConfig
import org.brewcode.qa.pages.page.Pages
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@Suppress("HttpUrlsUsage")
object TestConfiguration : AbstractProjectConfig() {

    private val container = GenericContainer<Nothing>(
        DockerImageName.parse("docker/getting-started@sha256:d79336f4812b6547a53e735480dde67f8f8f7071b414fbd9297609ffb989abc1")
    ).apply {
        withExposedPorts(80)
    }

    lateinit var pages: Pages

    override suspend fun beforeProject() {
        container.start()
        Configuration.browser = "chrome"
        Configuration.headless = true
        Configuration.baseUrl = "http://${container.host}:${container.firstMappedPort}"
        pages = Pages.createWithStaticSelenideDriver()
    }

    override suspend fun afterProject() {
        container.stop()
    }
}
