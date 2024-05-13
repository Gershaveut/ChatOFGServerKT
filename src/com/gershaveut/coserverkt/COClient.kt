package com.gershaveut.coserverkt

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class COClient(val name: String, val socket: Socket, val coServer: COServer) {
	var admin: Boolean = false
	
	private val reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
	private val writer: PrintWriter = PrintWriter(socket.getOutputStream(), true)
	
	lateinit var receiveMessageJob: Job
	
	suspend fun receiveMessage() = coroutineScope {
		try {
			while (socket.isConnected) {
				val message = Message.createMessageFromText(reader.readLine())
				
				if (message.text.isEmpty())
					continue
				
				if (message.messageType == MessageType.Message) {
					coServer.broadcast(message.apply { message.text = "$name: ${message.text}" })
					println(message.text)
				} else if (admin) {
					coServer.executeCommand(message)?.let { sendMessage(it) }
					println("$name: $message")
				} else {
					sendMessage(Message("You do not have sufficient rights to use this", MessageType.Error))
					println("$name tried to execute $message")
				}
			}
		} catch (_: Exception) {
		} finally {
			if (isActive)
				disconnect()
		}
	}
	
	fun sendMessage(message: Message) {
		writer.println(message)
	}
	
	fun silentDisconnect(reason: String? = null) {
		if (reason != null) {
			sendMessage(Message(reason.ifEmpty { "No reason" }, MessageType.Kick))
			println("$name excluded due to $reason")
		}
		
		socket.close()
		reader.close()
		writer.close()
		
		coServer.clients.remove(this)
		
		println("$name disconnect")
	}
	
	fun disconnect(reason: String? = null) {
		silentDisconnect(reason)
		
		coServer.broadcast(Message(name, MessageType.Leave))
		coServer.broadcast(Message("$name disconnect"))
	}
	
	fun kick(reason: String? = null) {
		receiveMessageJob.cancel()
		
		disconnect(reason)
	}
}