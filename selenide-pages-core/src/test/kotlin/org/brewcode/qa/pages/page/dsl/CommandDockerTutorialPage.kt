package org.brewcode.qa.pages.page.dsl

import org.brewcode.qa.pages.annotation.Page
import org.brewcode.qa.pages.page.BasePage

@Page(value = "The command you just ran", path = "/tutorial/#the-command-you-just-ran")
class CommandDockerTutorialPage : BasePage<CommandDockerTutorialPage>()