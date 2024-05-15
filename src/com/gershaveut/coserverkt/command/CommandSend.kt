package com.gershaveut.coserverkt.command

import com.gershaveut.coapikt.Message
import com.gershaveut.coapikt.MessageType
import com.gershaveut.coserverkt.COServer
import com.gershaveut.ock.console.AbstractCommand
import com.gershaveut.ock.console.NeedArgument
import com.gershaveut.ock.tryGet

class CommandSend(private val coServer: COServer) : AbstractCommand("send", "Send custom message to user.",
	NeedArgument("text", "Message text"),
	NeedArgument("type<MessageType>", "Message type<Custom, ${MessageType.entries.toTypedArray().joinToString()}>"),
	NeedArgument("user?", "Shipping destination"))
{
	override val needArgument = 2
	
	override fun execute(arguments: List<String>?): String? {
		val text = arguments!![0]
		val type = if (arguments[1] != "Custom") MessageType.valueOf(arguments[1]) else null
		val user = arguments.tryGet(2)
		
		val message = if (type != null) Message(text, type) else Message.parseMessage(text)
		
		if (user != null)
			coServer.sendMessage(user, message)
		else
			coServer.broadcast(message)
		
		return null
	}
}