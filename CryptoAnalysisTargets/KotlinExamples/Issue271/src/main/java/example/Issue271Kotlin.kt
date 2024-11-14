package example;

import java.security.MessageDigest

fun main() {
    testFail("abc123ABC")
    testOk("abc123ABC")
}

fun testFail(input: String) {
    val someManipulation = input.substring(0, 2)
    MessageDigest.getInstance("SHA-256").digest(someManipulation.toByteArray())
}

fun testOk(input: String) {
    MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
}
