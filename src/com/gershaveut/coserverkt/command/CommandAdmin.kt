package com.gershaveut.coserverkt.command

import com.gershaveut.coserverkt.COServer
import com.gershaveut.ock.console.AbstractCommand
import com.gershaveut.ock.console.NeedArgument

class CommandAdmin(val coServer: COServer) : AbstractCommand("admin", "Assigns administrator status to the user.", arrayListOf(
	NeedArgument("name<string>", "User name"), NeedArgument("admin<bool>", "Admin State"))
) {
	override fun execute(arguments: List<String>?): String? {
		val userName = arguments?.get(0)
		val admin = arguments?.get(1).toBoolean()
		
		if (userName != null) {
			coServer.admin(userName, admin)
		}
		
		return null
	}
}