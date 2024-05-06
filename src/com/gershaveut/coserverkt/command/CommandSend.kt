package com.gershaveut.coserverkt.command

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
import com.gershaveut.coserverkt.COServer
import com.gershaveut.ock.console.AbstractCommand
import com.gershaveut.ock.console.NeedArgument
import com.gershaveut.ock.tryGet

class CommandSend(private val coServer: COServer) : AbstractCommand("send", "Send custom message to user.",
	NeedArgument("text", "Message text"),
	NeedArgument("type<MessageType>", "Message type<${MessageType.entries.toTypedArray().joinToString()}>"),
	NeedArgument("user?", "Shipping destination"))
{
	override val needArgument = 2
	
	override fun execute(arguments: List<String>?): String? {
		val text = arguments!![0]
		val type = MessageType.valueOf(arguments[1])
		val user = arguments.tryGet(2)
		
		val message = Message(text, type)
		
		if (user != null)
			coServer.sendMessage(user, message)
		else
			coServer.broadcast(message)
		
		return null
	}
}