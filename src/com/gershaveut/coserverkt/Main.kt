package com.gershaveut.coserverkt

suspend fun main() {
    try {
        println("Enter port")
        
        COServer(readln().toInt()).start()
    } catch (_: Exception) {
        main()
    }
}