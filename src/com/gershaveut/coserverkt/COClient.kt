package com.gershaveut.coserverkt

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
import kotlinx.coroutines.coroutineScope
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class COClient(val name: String, val socket: Socket, val coServer: COServer) {
	var admin: Boolean = false
	
	val reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
	val writer: PrintWriter = PrintWriter(socket.getOutputStream(), true)
	
	suspend fun receiveMessage() = coroutineScope {
		try {
			while (socket.isConnected) {
				val message = Message.createMessageFromText(reader.readLine())
				
				if (message.messageType == MessageType.Message)
					coServer.broadcast(message.apply { message.text = "$name: ${message.text}" })
				else if (admin)
					coServer.executeCommand(message)?.let { sendMessage(it) }
				else
					sendMessage(Message("You do not have sufficient rights to use this", MessageType.Error))
			}
		} catch (_: Exception) {
			disconnect("There was an error in your connection")
			
			return@coroutineScope
		}
		
		disconnect()
	}
	
	fun sendMessage(message: Message) {
		writer.println(message)
	}
	
	fun disconnect(reason: String?) {
		if (reason != null)
			sendMessage(Message(reason, MessageType.Kick))
		
		socket.close()
		reader.close()
		writer.close()
		
		coServer.clients.forEach {
			coServer.broadcast(Message(it.name, MessageType.Leave))
		}
		
		coServer.broadcast(Message("$name connected"))
		
		coServer.clients.remove(this)
	}
	
	fun disconnect() {
		disconnect(null)
	}
}