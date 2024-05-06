package com.gershaveut.coserverkt.command

import com.gershaveut.coserverkt.COServer
import com.gershaveut.ock.console.AbstractCommand
import com.gershaveut.ock.console.NeedArgument
import com.gershaveut.ock.tryGet

class CommandAdmin(private val coServer: COServer) : AbstractCommand("admin", "Assigns administrator status to the user.",
	NeedArgument("name", "User name"),
	NeedArgument("admin<bool> = true", "Admin State"))
{
	override val needArgument = 1
	
	override fun execute(arguments: List<String>?): String? {
		val userName = arguments!![0]
		val admin = arguments.tryGet(1)?.toBoolean()
		
		coServer.admin(userName, admin ?: true)
		
		return null
	}
}