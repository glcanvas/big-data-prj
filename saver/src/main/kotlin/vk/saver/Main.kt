package vk.saver

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) {
        exitProcess(1)
    }
    val host = args[0]
    val port = Integer.parseInt(args[1])
    val dbClient = DaoClient(host, port)

    dbClient.close()
}