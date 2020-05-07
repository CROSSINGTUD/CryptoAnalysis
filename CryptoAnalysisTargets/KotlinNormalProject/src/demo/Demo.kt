package demo

import javax.crypto.Cipher

fun main(args: Array<String>) {
	print("Hello World!")
	var instance: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
}