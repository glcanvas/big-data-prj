@file:JvmName("Main")


package vk.loader

import vk.launcher.Launcher


fun main(args: Array<String>) {
    val envLauncher = Launcher.load()
    // val launcher = LoadLauncher(envLauncher.getKafkaAddress(), envLauncher.getVkKey())
    val launcher = LoadLauncher("PLAINTEXT://127.0.0.1:9092",
            "8b34623d8b34623d8b34623d128b595d3788b348b34623deb51e7e4149c9e0837beb45a")
    val future = launcher.run()
    future.get()
}

