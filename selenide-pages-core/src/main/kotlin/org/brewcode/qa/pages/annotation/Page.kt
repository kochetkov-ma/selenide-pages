package org.brewcode.qa.pages.annotation

import org.brewcode.qa.pages.EMPTY

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Page(
    val value: String = EMPTY,
    val expectedTitle: String = EMPTY,
    val path: String = EMPTY
)
