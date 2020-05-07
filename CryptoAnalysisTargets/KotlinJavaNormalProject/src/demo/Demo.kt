package demo

import javax.crypto.Cipher

fun main(args: Array<String>) {
	println("Hello, World!")
	val e = EncryptionUtils("AES/ECB/PKCS5Padding")
	e.encrypt()
}

class EncryptionUtils(val algorithm: String) {

	fun encrypt() {
		var instance: Cipher = Cipher.getInstance(algorithm)
	}
}