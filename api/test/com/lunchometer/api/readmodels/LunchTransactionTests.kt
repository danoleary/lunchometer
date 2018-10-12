//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Test
//import java.time.LocalDateTime
//import java.util.*
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.Arguments
//import org.junit.jupiter.params.provider.MethodSource
//import java.util.stream.Stream
//import kotlin.test.assertEquals
//
//class LunchTransactionTests {
//
//    @Test
//    fun `an empty event list cannot be mapped to a lunch transaction`() {
//        val events = listOf<Event>()
//        Assertions.assertThrows(Exception::class.java) { isLunchTransaction(events) }
//    }
//
//    @Test
//    fun `events with different transaction ids cannot be combined`() {
//        val events = listOf<Event>(
//            transactionAdded.copy(
//                transaction = transaction().copy(id = UUID.randomUUID(), created = fridayLunchtime)),
//            transactionAdded.copy(
//                transaction = transaction().copy(id = UUID.randomUUID(), created = fridayLunchtime))
//        )
//        Assertions.assertThrows(Exception::class.java) {
//            isLunchTransaction(events)
//        }
//    }
//
//    @ParameterizedTest
//    @MethodSource("eventParams")
//    fun eventsAreMappedCorrectlyIntoLunchTransaction(events: List<Event>, isLunch: Boolean) {
//        val isLunchTransaction = isLunchTransaction(events)
//        assertEquals(isLunch, isLunchTransaction)
//    }
//
//    companion object {
//        @JvmStatic
//        private fun eventParams(): Stream<Arguments> {
//            val transactionId = UUID.randomUUID()
//            return Stream.of(
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime))),
//                    false),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsLunch.copy(id = transactionId)
//                    ),
//                    true),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsNotLunch.copy(id = transactionId)
//                    ),
//                    false),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now()),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(1))
//                    ),
//                    false),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(1)),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now())
//                    ),
//                    true),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(5)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(3)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(7)),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(2)),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now())
//                    ),
//                    true),
//                Arguments.of(
//                    listOf(
//                        transactionAdded.copy(
//                            transaction = transaction().copy(created = fridayLunchtime, id = transactionId)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(5)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(3)),
//                        markedAsNotLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(7)),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(8)),
//                        markedAsLunch.copy(id = transactionId, timestamp = LocalDateTime.now().minusDays(10))
//                    ),
//                    false)
//            )
//        }
//    }
//}
//
//private val userId = UUID.randomUUID().toString()
//
//private fun transaction() =
//    Transaction(
//        id = UUID.randomUUID(),
//        amount = (3..10).shuffled().first().toBigDecimal(),
//        created = LocalDateTime.now(),
//        retailer = "some place"
//    )
//
//private val transactionAdded =
//    Event.CardTransactionAdded(userId, LocalDateTime.now(), transaction())
//
//private val markedAsLunch =
//    Event.TransactionMarkedAsLunch(userId, LocalDateTime.now(), UUID.randomUUID())
//
//private val markedAsNotLunch =
//    Event.TransactionMarkedAsNotLunch(userId, LocalDateTime.now(), UUID.randomUUID())
//
//private val fridayLunchtime = LocalDateTime.of(2018, 9, 7, 13, 0)
//private val saturdayLunchtime = LocalDateTime.of(2018, 9, 8, 13, 0)
//private val sundayLunchtime = LocalDateTime.of(2018, 9, 9, 13, 0)
