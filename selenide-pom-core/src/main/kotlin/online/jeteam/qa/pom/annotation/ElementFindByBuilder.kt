package online.jeteam.qa.pom.annotation

import io.leangen.geantyref.TypeFactory
import online.jeteam.qa.pom.annotation.ElementFindByBuilder.Type.ALL
import online.jeteam.qa.pom.annotation.ElementFindByBuilder.Type.CHAIN
import online.jeteam.qa.pom.page.Element
import online.jeteam.qa.pom.util.InternalExtension.orNotNull
import org.openqa.selenium.By
import org.openqa.selenium.support.AbstractFindByBuilder
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.pagefactory.ByAll
import org.openqa.selenium.support.pagefactory.ByChained
import java.lang.reflect.Field
import kotlin.reflect.full.declaredMemberFunctions

internal class ElementFindByBuilder : AbstractFindByBuilder() {

    override fun buildIt(annotation: Any, field: Field): By {
        require(annotation is Element) { "Something went wrong with @Element, it turned out as '$annotation' in '$field'" }

        val findBys = annotation.findBys
        val findAll = annotation.findAll

        require(findBys.isEmpty() or findAll.isEmpty()) {
            "Using 'findBys' and 'findAll' at the same time is not allowed. Keep only one. Annotation: $annotation. Field: $field"
        }

        return when {
            findBys.isNotEmpty() -> multipleBuildIt(findBys, CHAIN)

            findAll.isNotEmpty() -> multipleBuildIt(findBys, ALL)

            else -> annotation.toFinBy().let {
                buildByFromShortFindBy(it)
                    .orNotNull { buildByFromLongFindBy(it) }
            }
        }
    }

    private fun multipleBuildIt(findByArray: Array<FindBy>, type: Type): By {
        val byArray = findByArray.mapNotNull { buildByFromFindBy(it) }.toTypedArray()

        return when (type) {
            ALL -> ByAll(*byArray)
            CHAIN -> ByChained(*byArray)
        }
    }

    private enum class Type { CHAIN, ALL }

    private companion object {

        private val excludeFunctions = arrayOf("equals", "hashCode", "toString", "annotationType")

        private fun Element.toFinBy(): FindBy = TypeFactory.annotation(
            FindBy::class.java,
            this::class.declaredMemberFunctions
                .filterNot { (it.name in excludeFunctions) or (it.parameters.size != 1) }
                .associate { prop -> prop.name to prop.call(this) }
        )
    }
}