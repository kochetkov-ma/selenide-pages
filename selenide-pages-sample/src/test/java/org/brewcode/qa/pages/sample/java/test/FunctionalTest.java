package org.brewcode.qa.pages.sample.java.test;

import org.brewcode.qa.pages.page.Pages;
import org.brewcode.qa.pages.sample.java.page.DockerGettingStartedMainPage;
import org.brewcode.qa.pages.sample.java.page.DockerGettingStartedMainPage.GettingStartedNavigation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.empty;

public class FunctionalTest {

    @Test
    @Disabled("Work In Progress")
    public void test() {
        final Pages pages = Pages.PagesFactory
            .createWithStaticSelenideDriver("http://localhost"); // Create Page Factory
        final DockerGettingStartedMainPage page = pages
            .page(DockerGettingStartedMainPage.class) // Create page
            .open() // open page with path from @Page annotation
            .verify(); // verify that page opened successfully, url is correct, title is expected, all required elements is displayed
        
        // interact with element collection and check third element text is not empty
        page.paragraphs.get(2).shouldNotBe(empty);

        // get block
        final GettingStartedNavigation block = page.gettingStartedNavigation;
        block.navigationItemList.getSelf().shouldHave(sizeGreaterThan(0)); // check that list is loaded
        block.navigationItemList.get(1).gettingStartedNavigation.should(appear); // get element and check
    }
}
