package org.brewcode.qa.pages.util

import java.time.Duration
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

internal object InternalExtension {
    fun <T> T?.optional(): Optional<T & Any> = Optional.ofNullable(this)

    fun <T> T?.or(fallback: () -> T?): T? = this ?: fallback()

    fun <T> T?.orNotNull(fallback: () -> T): T = this ?: fallback()

    fun <T> Optional<T?>.orNull(): T? = orElse(null)

    @Suppress("UNCHECKED_CAST")
    fun <R> Any.readProp(propertyName: String): R {
        val property = this::class.members
            .first { it.name == propertyName } as KProperty1<Any, *>
        return property.get(this) as R
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> KProperty1<*, *>.read(obj: Any): R = (this as KProperty1<Any, *>).get(obj) as R

    val Number.sec: Duration get() = Duration.ofSeconds(this as Long)
    val Number.mills: Duration get() = Duration.ofMillis(this as Long)

    inline fun <reified T : Annotation> KProperty<*>.annotation(): T? = javaField?.getAnnotation(T::class.java) ?: findAnnotation()

    inline fun <reified T> Array<T?>?.plusNotNull(element: T?): List<T> = (this ?: emptyArray()).let { list ->
        if (element != null) list.plus(element) else list
    }.mapNotNull { it }
}