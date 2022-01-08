package org.brewcode.qa.pages.sample.kotlin.page

import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.page.BasePage

@Page(value = "Our application", path = "/our-application/", expectedTitle = "Getting Started")
class OurApplicationPage : BasePage<OurApplicationPage>()
