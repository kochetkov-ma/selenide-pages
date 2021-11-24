package online.jeteam.qa.pom.util

fun String.withPath(path: String?) =
    when (path.isNullOrBlank()) {
        true -> this
        false -> trimEnd('/') + '/' + path.trimStart('/')
    }
