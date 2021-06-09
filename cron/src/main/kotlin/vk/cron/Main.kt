@file:JvmName("Main")

package vk.cron

import vk.launcher.Launcher


fun main(args: Array<String>) {

    val envLauncher = Launcher.load()
    val launcher = CronLauncher(envLauncher.getKafkaAddress(), envLauncher.getVkKey(), envLauncher.getHttpPort(),
            envLauncher.getMongoHost(), envLauncher.getMongoPort())

    /*val launcher = CronLauncher("0.0.0.0:9092",
            "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a",
            8080, "localhost", 27017)

     */
    val future = launcher.run()
    future.get()
}

