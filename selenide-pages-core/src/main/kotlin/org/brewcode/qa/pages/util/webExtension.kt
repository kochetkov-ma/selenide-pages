package org.brewcode.qa.pages.util

fun String.withPath(path: String?) =
    when (path.isNullOrBlank()) {
        true -> this
        false -> trimEnd('/') + '/' + path.trimStart('/')
    }
