package edu.stanford.bmir.radx.cde.generator

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import picocli.CommandLine

@SpringBootApplication
class Application {
	@Bean
	fun commandLineRunner(applicationContext: ApplicationContext): CommandLineRunner {
		return CommandLineRunner { args: Array<String> ->
			val commandLine = CommandLine(applicationContext.getBean(ConverterCLI::class.java))
			val exitCode = commandLine.execute(*args)
			System.exit(exitCode)
		}
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
