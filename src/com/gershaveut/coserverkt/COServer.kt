package com.gershaveut.coserverkt

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
import com.gershaveut.ock.detailedException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.SocketAddress
import java.util.HashSet

class COServer(port: Int) {
	val serverSocket: ServerSocket = ServerSocket(port)
    val clients: HashSet<COClient> = HashSet()
	
	suspend fun start() = coroutineScope {
		launch {
			connectHandler()
		}
	}
	
	suspend fun connectHandler() = coroutineScope {
		while (!serverSocket.isClosed) {
			try {
				val clientSocket = serverSocket.accept()
                val client = COClient(BufferedReader(InputStreamReader(clientSocket.getInputStream())).readLine(), clientSocket, this@COServer)
				
				if (clients.any { it.name == client.name })
					client.disconnect("User ${client.name} is already joined")
				
				//Registering
				clients.forEach {
					client.sendMessage(Message(it.name, MessageType.Join))
				}
				
				clients.add(client)
				
				broadcast(Message(client.name, MessageType.Join))
				broadcast(Message("${client.name} connected"))
				
				launch {
                    client.receiveMessage()
				}
			} catch (e: Exception) {
				println(detailedException(e))
			}
		}
	}
    
    fun sendMessage(name: String, message: Message) {
        clients.forEach {
            if (name == it.name)
                it.sendMessage(message)
        }
    }
    
    fun broadcast(message: Message) {
        clients.forEach {
			if (message.messageType == MessageType.Message)
            	it.sendMessage(Message(message.text))
			else
				it.sendMessage(message)
        }
    }
	
	fun admin(name: String, admin: Boolean) {
		clients.forEach {
			if (name == it.name)
				it.admin = admin
		}
		
		broadcast(Message(if (admin) "$name became an administrator" else "$name stopped being an administrator", MessageType.Broadcast))
	}
	
	fun executeCommand(message: Message) : Message? {
		when (message.messageType) {
			MessageType.Kick -> return kick(message.text, message.arguments!!)
			else -> broadcast(message)
		}
		
		return null
	}
	
	fun kick(name: String, reason: String?): Message? {
		val client: COClient
		
		try {
			client = clients.first { it.name == name }
		} catch (_: Exception) {
			return Message("User not found", MessageType.Error)
		}
		
		client.disconnect(reason)
		
		if (reason.isNullOrEmpty())
			println("${client.name} excluded without a reason")
		else
			println("${client.name} excluded due to $reason")
		
		return null
	}
}