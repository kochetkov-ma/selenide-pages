package online.jeteam.qa.pom.page.dsl

import online.jeteam.qa.pom.annotation.Page
import online.jeteam.qa.pom.page.BasePage

@Page(value = "The command you just ran", path = "/tutorial/#the-command-you-just-ran")
class CommandDockerTutorialPage : BasePage<CommandDockerTutorialPage>()