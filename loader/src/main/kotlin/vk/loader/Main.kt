@file:JvmName("Main")


package vk.loader

import se.softhouse.jargo.Argument
import se.softhouse.jargo.Arguments.*
import se.softhouse.jargo.CommandLineParser
import se.softhouse.jargo.ParsedArguments

var helpArgument: Argument<*> = helpArgument("-h", "--help") //Will throw when -h is encountered

var kafkaAddress: Argument<String> = stringArgument("--kafka")
        .description("host:port to kafka")
        .build()

var vkKey: Argument<String> = stringArgument("--vk-key")
        .description("vk key")
        .build()


fun main(args: Array<String>) {

    val argsList: List<String> = MutableList(args.size) { args[it] }

    val arguments: ParsedArguments = CommandLineParser.withArguments(kafkaAddress, vkKey)
            .andArguments(helpArgument)
            .parse(argsList)
    val launcher = LoadLauncher(arguments.get(kafkaAddress)!!, arguments.get(vkKey)!!)
    val future = launcher.run()
    future.get()
}
