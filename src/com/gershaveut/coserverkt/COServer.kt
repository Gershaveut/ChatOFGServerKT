package com.gershaveut.coserverkt

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
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
                
                clients.add(client)
				
				clients.forEach {
					client.sendMessage(Message(it.name, MessageType.Join))
				}
				
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
		
		println(message)
    }
	
	fun executeCommand(message: Message) : Message? {
		when (message.messageType) {
			MessageType.Kick -> {
				val client: COClient
				val reason = message.arguments!!
				
				try {
					client = clients.first { it.name == message.text }
				} catch (_: Exception) {
					return Message("User not found", MessageType.Error)
				}
				
				client.disconnect(reason)
				
				println("$client.name excluded due to $reason")
			}
			else -> broadcast(message)
		}
		
		return null
	}
}