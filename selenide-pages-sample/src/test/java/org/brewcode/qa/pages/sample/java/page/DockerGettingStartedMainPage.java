package org.brewcode.qa.pages.sample.java.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.brewcode.qa.pages.annotation.Element;
import org.brewcode.qa.pages.annotation.Page;
import org.brewcode.qa.pages.element.Block;
import org.brewcode.qa.pages.element.Blocks;
import org.brewcode.qa.pages.page.BasePage;

/**
 * Define selenide page via @Page annotation. Must extend BasePage class.
 * 
 * value = "Getting Started" - page name for log message
 * path = "/tutorial/" - expected path. Will check on page open verify, and will append when open this page
 * expectedTitle = "Getting Started" - The title text will check on page open verify
 */
@Page(value = "Getting Started", path = "/tutorial/", expectedTitle = "Getting Started")
public class DockerGettingStartedMainPage extends BasePage<DockerGettingStartedMainPage> {

    /**
     * Create element collection.
     * With alias "Paragraphs" to use in log messages.
     * Search by tag name.
     * 
     * "required = true" - This element must be visible on the page after loading. When you open the page, this element must be visible, otherwise an error will occur
     */
    @Element(value = "Paragraphs", tagName = "h2", required = true)
    public ElementsCollection paragraphs;

    public GettingStartedNavigation gettingStartedNavigation;

    /**
     * The page block defined.
     * Must extend Block class.
     * 
     * Be aware - @Element annotation uses on class instead of field. 
     * But you may use field annotation and field annotation has a higher priority.
     */
    @Element(value = "Getting started navigation", css = "nav.md-nav--primary>ul")
    public static class GettingStartedNavigation extends Block {

        /**
         * Collection of page blocks.
         */
        @Element(value = "Пункты навигации", xpath = "./li")
        public Blocks<NavigationItem> navigationItemList;
    }

    public static class NavigationItem extends Block {

        @Element(value = "Navigation item", tagName = "a", required = true)
        public SelenideElement gettingStartedNavigation;
    }
}
