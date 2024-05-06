package com.gershaveut.coserverkt

import com.gershaveut.coserverkt.command.CommandAdmin
import com.gershaveut.coserverkt.command.CommandKick
import com.gershaveut.coserverkt.command.CommandSend
import com.gershaveut.ock.console.CommandHandler
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
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
	commandHandler.commands.add(CommandKick(coServer))
	commandHandler.commands.add(CommandSend(coServer))
	
	try {
		GlobalScreen.registerNativeHook()
		GlobalScreen.addNativeKeyListener(object : NativeKeyListener {
			var historyCommand: String? = null
				set(value) {
					if (value != null)
						println(value)
					
					field = value
				}
			
			override fun nativeKeyPressed(e: NativeKeyEvent) {
				when (e.keyCode) {
					NativeKeyEvent.VC_UP -> historyCommand = commandHandler.getNext()
					NativeKeyEvent.VC_DOWN -> historyCommand = commandHandler.getPrevious()
					NativeKeyEvent.VC_RIGHT -> historyCommand?.let {
						println(it)
						println(commandHandler.executeCommand(it))
					}
				}
			}
		})
	} catch (e: Exception) {
		println(e)
	}
	
	while (true) {
		val command = readln()
		
		if (command.isNotEmpty())
			println(commandHandler.executeCommand(command))
	}
}