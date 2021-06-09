@file:JvmName("Main")


package vk.loader

import vk.launcher.Launcher


fun main(args: Array<String>) {
    val envLauncher = Launcher.load()
    val launcher = LoadLauncher(envLauncher.getKafkaAddress(), envLauncher.getVkKey())
    val future = launcher.run()
    future.get()
}

