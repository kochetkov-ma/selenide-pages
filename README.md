[![Maven Central](https://img.shields.io/maven-central/v/org.brewcode/selenide-pages-core)](https://search.maven.org/#search|ga|1|selenide-pages-core)
# Selenide pages

![selenide pages](pages.png)
---

Framework based on [Selenide](https://github.com/selenide/selenide) provided convenient functional to create and manage
your app [Page Objects](https://www.selenium.dev/documentation/guidelines/page_object_models/) in automation testing.

## Description

- Quickly creating a convenient, extensible PageObject hierarchy based on Selenide.
- Additional functionality for working with a page and organizing elements structure.
- Kotlin DSL for Page Object, and fully compatible with Java 11
- Cucumber steps for using Page Objects

## Modules
### [`selenide-pages-core`](selenide-pages-core/README.md)
Main module

### [`selenide-pages-cucumber`](selenide-pages-cucumber/README.md)
Cucumber steps provided `selenide-pages-core` and `Selenide` functions in Gherkin `features` 

### [`selenide-pages-sample`](selenide-pages-sample/README.md)
Example of use on `Kotlin` and `Java`
> Very interesting! Have a look before using ...

## Getting started

#### Add dependency

Add `selenide-pages-core` dependency and your favorite testing framework for example `junit-jupiter` 
```groovy
dependencies {
    testImplementation "org.brewcode:selenide-pages-core:1.0.0"
    testImplementation "org.junit.jupiter:junit-jupiter:5.8.2"
}
```
The `selenide-pages-core` brings with transitive dependencies:
- `selenide`
- `selenium 4`
- `chrome driver` (without another drivers sush as firefox or edge)
- `awaitility`
- `kotlin`
- `kotest assertions`
- `slf4j`

The dependencies above are necessary for the operation of our library. But you can disable it and override 
```groovy
dependencies {
    testImplementation("org.brewcode:selenide-pages-core:1.0.0") { transitive false }
}
```

#### Create first page

```java
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
```

- Use `@Page` annotation on page class
- Your page class must inherit the `BasePage` with parameter as self class
- Use `@Element` annotation for `SelenideElement`, `Block` and `Blocks<T>`
- Your block class must inherit the `Block`

#### Create test

```java
public class FunctionalTest {

    @Test
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
```

## Advanced Usage

`in progress ...`

## Kotlin DSL

## Support

- Create an issue on [GitLab selenide-pages](https://gitlab.com/brewcode/selenide-pages)
- Create an issue on [GitHub selenide-pages](https://github.com/kochetkov-ma/selenide-pages)

**Kochetkov Maxim** - [kochetkov-ma@yandex.ru](mailto:kochetkov-ma@yandex.ru)

## Roadmap

Cucumber steps module - expected release date `30.01.2022`

## Contributing

Mail to me [kochetkov-ma@yandex.ru](mailto:kochetkov-ma@yandex.ru), and I will add you to GitLab Group

## Authors and acknowledgment

**Kochetkov Maxim** - [kochetkov-ma@yandex.ru](mailto:kochetkov-ma@yandex.ru)
**Frolov Sergey** - [tbd](mailto:tbd)

## License

`Apache License 2.0`

## Project status

Released on January 2021

`ACTIVE DEVELOPING`