package online.jeteam.qa.pom.page;

import online.jeteam.qa.pom.annotation.ElementFindByBuilder;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactoryFinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark pom element instead of FindBy annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@PageFactoryFinder(ElementFindByBuilder.class)
public @interface Element {

    String UNSET_LOCATOR = "";

    /**
     * Alias for element.
     *
     * @see com.codeborne.selenide.impl.Alias
     */
    String value() default "";

    /**
     * The element will be mandatory on page. It should be visible after page loading.
     */
    boolean required() default false;

    /////////////////
    //// FindBy /////
    /////////////////

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    How how() default How.UNSET;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String using() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String id() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String name() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String className() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String css() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String tagName() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String linkText() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String partialLinkText() default UNSET_LOCATOR;

    /**
     * Copy from FindBy. Work the same way.
     *
     * @see org.openqa.selenium.support.FindBy
     */
    String xpath() default UNSET_LOCATOR;

    /**
     * FindBys functionality. Work the same way.
     * Priority:
     * <ul>
     *  <li>findBys field if filled</li>
     *  <li>findAll field if filled</li>
     *  <li>other FindBy fields</li>
     * </ul>
     *
     * @see org.openqa.selenium.support.FindBys
     */
    FindBy[] findBys() default {};

    /**
     * FindAll functionality. Work the same way.
     * Priority:
     * <ul>
     *  <li>findBys field if filled</li>
     *  <li>findAll field if filled</li>
     *  <li>other FindBy fields</li>
     * </ul>
     *
     * @see org.openqa.selenium.support.FindAll
     */
    FindBy[] findAll() default {};
}
