package mykotlintest

import javax.crypto.Cipher

class Demo {
	companion object {
		@JvmStatic fun main(args: Array<String>) {
			print("Hello World!")
			var instance: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
		}
	}
}