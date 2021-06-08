@file:JvmName("Main")

package vk.cron


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

var configPath: Argument<String> = stringArgument("--config-path")
        .description("path to config")
        .build()

var mongoHost: Argument<String> = stringArgument("--mongo-host")
        .description("Mongo db host")
        .build()

var mongoPort: Argument<Int> = integerArgument("--mongo-port")
        .description("Mongo db port")
        .build()


fun main(args: Array<String>) {

    val argsList: List<String> = MutableList(args.size) { args[it] }

    val arguments: ParsedArguments = CommandLineParser.withArguments(kafkaAddress, vkKey, configPath, mongoHost, mongoPort)
            .andArguments(helpArgument)
            .parse(argsList)
    val launcher = CronLauncher(arguments.get(kafkaAddress)!!, arguments.get(vkKey)!!, arguments.get(configPath)!!, arguments.get(mongoHost)!!, arguments.get(mongoPort)!!)
    val future = launcher.run()
    future.get()
}

