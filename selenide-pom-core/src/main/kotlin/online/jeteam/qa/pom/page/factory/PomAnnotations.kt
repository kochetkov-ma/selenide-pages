package online.jeteam.qa.pom.page.factory

import online.jeteam.qa.pom.page.Element
import online.jeteam.qa.pom.util.InternalExtension.optional
import online.jeteam.qa.pom.util.InternalExtension.or
import online.jeteam.qa.pom.util.InternalExtension.orNull
import org.openqa.selenium.By
import org.openqa.selenium.support.AbstractFindByBuilder
import org.openqa.selenium.support.PageFactoryFinder
import org.openqa.selenium.support.pagefactory.Annotations
import java.lang.reflect.Field
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

internal class PomAnnotations(field: Field?) : Annotations(field) {

    override fun buildBy(): By {
        assertValidAnnotations()

        return field.declaredAnnotations.plus(field.elementAnnotationFromContainerClass).firstNotNullOfOrNull { annotation ->
            runCatching {
                annotation.ktPageFactoryFinderAnnotations.or { annotation.javaPageFactoryFinderAnnotation }.optional()
                    .map { it.builder.buildIt(annotation, field) }
                    .orNull()
            }.getOrNull()
        } ?: buildByFromDefault()
    }

    private companion object {
        val Field.elementAnnotationFromContainerClass: Element get() = type.getAnnotation(Element::class.java)
        val Annotation.ktPageFactoryFinderAnnotations get() = annotationClass.findAnnotation<PageFactoryFinder>()
        val Annotation.javaPageFactoryFinderAnnotation: PageFactoryFinder get() = this::class.java.getAnnotation(PageFactoryFinder::class.java)
        val PageFactoryFinder.builder: AbstractFindByBuilder get() = value.primaryConstructor?.call() as AbstractFindByBuilder
    }
}