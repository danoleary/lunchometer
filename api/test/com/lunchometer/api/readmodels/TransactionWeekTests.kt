//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import org.junit.jupiter.api.Test
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.util.*
//import kotlin.test.assertEquals
//
//class TransactionWeekTests {
//
//    @Test
//    fun `transactions are mapped correctly to lunch weeks`() {
//        val transactions = mapOf(LocalDate.of(2018, 9, 3) to
//            listOf(
//                transaction().copy(created = monday, amount = 3.5.toBigDecimal()),
//                transaction().copy(created = tuesday, amount = 4.95.toBigDecimal()),
//                transaction().copy(created = wednesday, amount = 3.2.toBigDecimal()),
//                transaction().copy(created = thursday, amount = 6.toBigDecimal()),
//                transaction().copy(created = friday, amount = 15.75.toBigDecimal()),
//                transaction().copy(created = friday, amount = 5.2.toBigDecimal())
//            )
//        )
//
//        val result = toTransactionWeek(transactions.entries.first())
//
//        assertEquals(LocalDate.of(2018, 9, 3).toString(), result.startDate)
//        assertEquals(4.41.toBigDecimal(), result.averageMonToThurs)
//        assertEquals(20.95.toBigDecimal(), result.fridaySpend)
//        assertEquals(6, result.transactions.size)
//    }
//}
//
//private fun transaction() = Transaction(
//    id = UUID.randomUUID(),
//    amount = 4.5.toBigDecimal(),
//    created = LocalDateTime.now(),
//    retailer = "some place"
//)
//
//private val monday = LocalDateTime.of(2018, 9, 3, 13, 0)
//private val tuesday = LocalDateTime.of(2018, 9, 4, 13, 0)
//private val wednesday = LocalDateTime.of(2018, 9, 5, 13, 0)
//private val thursday = LocalDateTime.of(2018, 9, 6, 13, 0)
//private val friday = LocalDateTime.of(2018, 9, 7, 13, 0)