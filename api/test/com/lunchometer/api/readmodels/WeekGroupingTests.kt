//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import org.junit.jupiter.api.Test
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.util.*
//import kotlin.test.assertEquals
//
//class WeekGroupingTests {
//
//    @Test
//    fun `weeks are grouped correctly`() {
//        val transactions = listOf(
//            transaction().copy(created = LocalDateTime.of(2018, 9, 3, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 4, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 5, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 6, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 7, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 10, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 11, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 12, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 13, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 14, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 17, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 18, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 19, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 20, 0, 0)),
//            transaction().copy(created = LocalDateTime.of(2018, 9, 21, 0, 0))
//        )
//        val result = groupByWeek(transactions)
//
//        assertEquals(5, result[LocalDate.of(2018, 9, 3)]!!.size)
//        assertEquals(5, result[LocalDate.of(2018, 9, 10)]!!.size)
//        assertEquals(5, result[LocalDate.of(2018, 9, 17)]!!.size)
//    }
//}
//
//private fun transaction() = Transaction(
//    id = UUID.randomUUID(),
//    amount = 4.5.toBigDecimal(),
//    created = LocalDateTime.now(),
//    retailer = "some place"
//)