package org.brewcode.qa.pages.sample.test;

import org.brewcode.qa.pages.page.Pages;
import org.brewcode.qa.pages.sample.page.DockerGettingStartedMainPage;
import org.junit.jupiter.api.Test;

public class FunctionalTest {

    @Test
    public void test() {
        final Pages pages = Pages.PagesFactory.createWithStaticSelenideDriver("http://localhost");

        DockerGettingStartedMainPage page = pages.page(DockerGettingStartedMainPage.class).open().verify();

        String text = page.paragraphs.get(2).text();
        System.out.println("Text: " + text);
    }
}
