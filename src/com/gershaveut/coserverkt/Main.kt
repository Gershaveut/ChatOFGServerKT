package com.gershaveut.coserverkt

import com.gershaveut.coserverkt.command.CommandAdmin
import com.gershaveut.ock.console.CommandHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() = coroutineScope {
	println("Enter port")
	
	val coServer = COServer(readln().toInt())
	
	launch {
		coServer.start()
	}
	
	val commandHandler = CommandHandler()
	
	commandHandler.commands.add(CommandAdmin(coServer))
	
	while (true) {
		println(commandHandler.executeCommand(readln()))
	}
}