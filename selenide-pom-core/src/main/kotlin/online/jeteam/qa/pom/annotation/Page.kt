package online.jeteam.qa.pom.annotation

import online.jeteam.qa.pom.EMPTY

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Page(
    val value: String = EMPTY,
    val expectedTitle: String = EMPTY,
    val path: String = EMPTY
)
