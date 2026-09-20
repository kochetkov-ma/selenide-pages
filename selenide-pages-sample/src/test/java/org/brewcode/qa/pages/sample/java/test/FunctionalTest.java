package org.brewcode.qa.pages.sample.java.test;

import org.brewcode.qa.pages.page.Pages;
import org.brewcode.qa.pages.sample.java.TestingUtil;
import org.brewcode.qa.pages.sample.java.page.DockerGettingStartedMainPage;
import org.brewcode.qa.pages.sample.java.page.DockerGettingStartedMainPage.GettingStartedNavigation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.closeWebDriver;

public class FunctionalTest {

    @Test
    public void opensTutorialAndFindsNavigation() {
        final Pages pages = Pages.PagesFactory
            .createWithStaticSelenideDriver(TestingUtil.baseUrl());
        final DockerGettingStartedMainPage page = pages
            .page(DockerGettingStartedMainPage.class)
            .open()
            .verify();

        // GIVEN the tutorial page, WHEN its contents load, THEN required navigation is visible.
        page.paragraphs.shouldHave(size(4).because("Pinned tutorial has four section headings"));
        page.paragraphs.get(2).shouldHave(text("What is a container?").because("Third section explains containers"));
        final GettingStartedNavigation block = page.gettingStartedNavigation;
        block.navigationItemList.getSelf().shouldHave(size(10).because("Pinned tutorial has ten navigation items"));
        block.navigationItemList.get(1).gettingStartedNavigation.should(appear.because("Second navigation item is visible"));
    }

    @AfterEach
    public void closeBrowser() {
        closeWebDriver();
    }
}
