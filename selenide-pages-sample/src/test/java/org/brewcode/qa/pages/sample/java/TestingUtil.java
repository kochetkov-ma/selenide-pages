package org.brewcode.qa.pages.sample.java;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class TestingUtil {
    private static final GenericContainer<?> APPLICATION = new GenericContainer<>(DockerImageName.parse(
        "docker/getting-started@sha256:d79336f4812b6547a53e735480dde67f8f8f7071b414fbd9297609ffb989abc1"
    )).withExposedPorts(80);

    private static boolean started;

    private TestingUtil() {
    }

    public static synchronized String baseUrl() {
        if (!started) {
            APPLICATION.start();
            Runtime.getRuntime().addShutdownHook(new Thread(APPLICATION::stop));
            started = true;
        }
        Configuration.browser = "chrome";
        Configuration.headless = true;
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", Map.of("profile.default_content_setting_values.clipboard", 1));
        Configuration.browserCapabilities = chromeOptions;
        return "http://" + APPLICATION.getHost() + ":" + APPLICATION.getMappedPort(80);
    }
}
