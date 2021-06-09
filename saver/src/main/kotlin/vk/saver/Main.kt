@file:JvmName("Main")

package vk.saver

import vk.launcher.Launcher


fun main(args: Array<String>) {
    val envLauncher = Launcher.load()
    val launcher = SaverLauncher(envLauncher.getKafkaAddress(), envLauncher.getMongoHost(), envLauncher.getMongoPort())
    val future = launcher.run()
    future.get()
}