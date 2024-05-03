package com.gershaveut.coserverkt.command

import com.gershaveut.coserverkt.COServer
import com.gershaveut.ock.console.AbstractCommand
import com.gershaveut.ock.console.NeedArgument
import com.gershaveut.ock.tryGet

class CommandKick(private val coServer: COServer) : AbstractCommand("kick", "Kicks a user from the server.",
	NeedArgument("name<string>", "User name"),
	NeedArgument("cause<string>", "Reason for exclusion"))
{
	override val needArgument = 1
	
	override fun execute(arguments: List<String>?): String? {
		val name = arguments!![0]
		val reason = arguments.tryGet(1)
		
		return coServer.kick(name, reason)?.text
	}
}