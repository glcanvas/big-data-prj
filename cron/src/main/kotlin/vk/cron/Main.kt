@file:JvmName("Main")

package vk.cron

import vk.launcher.Launcher


fun main(args: Array<String>) {

    val envLauncher = Launcher.load()
    val launcher = CronLauncher(envLauncher.getKafkaAddress(), envLauncher.getVkKey(), envLauncher.getConfigPath(),
            envLauncher.getMongoHost(), envLauncher.getMongoPort())
    val future = launcher.run()
    future.get()
}

