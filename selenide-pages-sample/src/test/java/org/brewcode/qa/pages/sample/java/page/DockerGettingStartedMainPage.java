package org.brewcode.qa.pages.sample.java.page;

import com.codeborne.selenide.ElementsCollection;
import org.brewcode.qa.pages.annotation.Element;
import org.brewcode.qa.pages.annotation.Page;
import org.brewcode.qa.pages.page.BasePage;

@Page(path = "/tutorial/")
public class DockerGettingStartedMainPage extends BasePage<DockerGettingStartedMainPage> {

    @Element(value = "Paragraphs", tagName = "h2")
    public ElementsCollection paragraphs;
}
