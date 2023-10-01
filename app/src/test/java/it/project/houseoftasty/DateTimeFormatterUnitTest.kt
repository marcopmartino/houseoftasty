package it.project.houseoftasty

import it.project.houseoftasty.network.ProfileNetwork
import it.project.houseoftasty.utility.DateTimeFormatter
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DateTimeFormatterUnitTest {

    private fun testSeconds(seconds: Long): String {
        return DateTimeFormatter.timePassedToString(seconds)
    }

    @Test
    fun secondsToStringTest0() { assertEquals("Adesso", testSeconds(0)) }
    @Test
    fun secondsToStringTest1() { assertEquals("Adesso", testSeconds(59)) }

    @Test
    fun secondsToStringTest2() { assertEquals("1 minuto fa", testSeconds(60)) }

    @Test
    fun secondsToStringTest3() { assertEquals("3 minuti fa", testSeconds(200)) }

    @Test
    fun secondsToStringTest4() { assertEquals("9 minuti fa", testSeconds(599)) }

    @Test
    fun secondsToStringTest5() { assertEquals("10 minuti fa", testSeconds(600)) }

    @Test
    fun secondsToStringTest6() { assertEquals("2 ore fa", testSeconds(10000)) }

    @Test
    fun secondsToStringTest7() { assertEquals("4 giorni fa", testSeconds(431999)) }

    @Test
    fun secondsToStringTest8() { assertEquals("5 giorni fa", testSeconds(432000)) }

    @Test
    fun secondsToStringTest9() { assertEquals("1 anno fa", testSeconds(50000000)) }
}