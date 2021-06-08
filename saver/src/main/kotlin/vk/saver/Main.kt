@file:JvmName("Main")

package vk.saver

import se.softhouse.jargo.Argument
import se.softhouse.jargo.Arguments.*
import se.softhouse.jargo.CommandLineParser
import se.softhouse.jargo.ParsedArguments

var helpArgument: Argument<*> = helpArgument("-h", "--help") //Will throw when -h is encountered

var kafkaAddress: Argument<String> = stringArgument("--kafka")
        .description("host:port to kafka")
        .build()

var mongoHost: Argument<String> = stringArgument("--mongo-host")
        .description("Mongo db host")
        .build()

var mongoPort: Argument<Int> = integerArgument("--mongo-port")
        .description("Mongo db port")
        .build()



fun main(args: Array<String>) {
    val argsList: List<String> = MutableList(args.size) { args[it] }

    val arguments: ParsedArguments = CommandLineParser.withArguments(kafkaAddress, mongoHost, mongoPort)
            .andArguments(helpArgument)
            .parse(argsList)
    val launcher = SaverLauncher(arguments.get(kafkaAddress)!!, arguments.get(mongoHost)!!, arguments.get(mongoPort)!!)
    val future = launcher.run()
    future.get()
}