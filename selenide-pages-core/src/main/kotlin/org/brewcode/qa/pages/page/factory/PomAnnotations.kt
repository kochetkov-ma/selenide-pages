package org.brewcode.qa.pages.page.factory

import io.github.oshai.kotlinlogging.KotlinLogging
import org.brewcode.qa.pages.annotation.Element
import org.brewcode.qa.pages.util.InternalExtension.optional
import org.brewcode.qa.pages.util.InternalExtension.or
import org.brewcode.qa.pages.util.InternalExtension.orNotNull
import org.brewcode.qa.pages.util.InternalExtension.orNull
import org.brewcode.qa.pages.util.InternalExtension.plusNotNull
import org.openqa.selenium.By
import org.openqa.selenium.support.AbstractFindByBuilder
import org.openqa.selenium.support.PageFactoryFinder
import org.openqa.selenium.support.pagefactory.Annotations
import java.lang.reflect.Field
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

internal class PagesAnnotations(field: Field?) : Annotations(field) {

    override fun buildBy(): By {
        assertValidAnnotations()

        return field.declaredAnnotations.plusNotNull(field.elementAnnotationFromContainerClass).firstOrNull()?.let { annotation ->
            log.trace { "Found annotation '$annotation' on field '$field'" }
            try {
                annotation.ktPageFactoryFinderAnnotations.or { annotation.javaPageFactoryFinderAnnotation }
                    .optional()
                    .map { existAnnotation -> existAnnotation.builder.buildIt(annotation, field) }
                    .orNull()
                    .also { log.trace { "Build locator '$it' from annotation '$annotation'" } }
            } catch (ignored: ReflectiveOperationException) {
                log.error(ignored) { "Error while By building '$annotation' for field '$field'" }
                null
            } catch (ex: Throwable) {
                throw RuntimeException("Error while By building '$annotation' for field '$field'", ex)
            }
        }.orNotNull { buildByFromDefault().also { log.debug { "ATTENTION - Locator '$it' built by default way with 'ByIdOrName' for field '$field'" } } }
    }

    internal companion object {
        private val log = KotlinLogging.logger {}

        val Field.elementAnnotationFromContainerClass: Element?
            get() =
                type.getAnnotationsByType(Element::class.java).firstOrNull()
        val Annotation.ktPageFactoryFinderAnnotations get() = annotationClass.findAnnotation<PageFactoryFinder>()
        val Annotation.javaPageFactoryFinderAnnotation: PageFactoryFinder? get() = this::class.java.getAnnotation(PageFactoryFinder::class.java)
        val PageFactoryFinder.builder: AbstractFindByBuilder get() = value.primaryConstructor?.call() as AbstractFindByBuilder
    }
}