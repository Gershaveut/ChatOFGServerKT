package com.gershaveut.coserverkt

fun detailedException(exception: Exception) : String {
	var text = exception.message.toString()
	
	for(line in exception.stackTrace) {
		text += "\n		at $line"
	}
	
	return text
}