@file:JvmName("Main")

package vk.saver

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 3) {
        exitProcess(1)
    }
    val host = args[0]
    val port = Integer.parseInt(args[1])
    val launcher = SaverLauncher("", host, port)
    val future = launcher.run()
    future.get()
}