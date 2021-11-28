package org.brewcode.qa.pages.util

import com.codeborne.selenide.SelenideDriver
import io.qameta.allure.Allure
import org.openqa.selenium.OutputType

object SelenideUtil {

    fun SelenideDriver.screenToAllure(name: String = "Screenshot") =
        Allure.getLifecycle().addAttachment(name, "image/png", "png", screenshot(OutputType.BYTES))

}
