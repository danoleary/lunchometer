//package com.lunchometer.api.readmodels
//
//import com.lunchometer.shared.Transaction
//import java.math.BigDecimal
//import java.math.RoundingMode
//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//data class TransactionDto(val id: UUID, val amount: BigDecimal, val day: String, val location: String)
//
//data class TransactionWeekDto(
//    val id: UUID,
//    val startDate: String,
//    val averageMonToThurs: BigDecimal,
//    val fridaySpend: BigDecimal,
//    val transactions: List<TransactionDto>)
//
//fun toTransactionWeek(groupedTransactions: Map.Entry<LocalDate, List<Transaction>>): TransactionWeekDto {
//    val transactions = groupedTransactions.value
//    return TransactionWeekDto(
//        id = UUID.randomUUID(),
//        startDate = groupedTransactions.key.toString(),
//        averageMonToThurs = averageMonToThursSpend(transactions),
//        fridaySpend = fridaySpend(transactions),
//        transactions = transactions.map { toTransactionDto(it) }
//    )
//}
//
//private fun toTransactionDto(tran: Transaction): TransactionDto {
//    val formatter = DateTimeFormatter.ofPattern("EEEE HH:mm")
//    return TransactionDto(
//        tran.id,
//        tran.amount.abs(),
//        tran.created.format(formatter),
//        tran.retailer)
//}
//
//private fun averageMonToThursSpend(transactions: List<Transaction>): BigDecimal {
//    val mondayToThursday = transactions.filter { it.created.dayOfWeek != DayOfWeek.FRIDAY }
//    if(mondayToThursday.isEmpty()) {
//        return 0.toBigDecimal()
//    }
//    val total = mondayToThursday.sumByDouble { it.amount.toDouble() }
//    val average = total / mondayToThursday.size
//    return average.toBigDecimal().setScale(2, RoundingMode.FLOOR).abs()
//}
//
//private fun fridaySpend(transactions: List<Transaction>): BigDecimal {
//    return transactions
//        .filter { it.created.dayOfWeek == DayOfWeek.FRIDAY }
//        .map { it.amount.abs().toDouble() }
//        .sumByDouble { it }
//        .toBigDecimal().setScale(2, RoundingMode.FLOOR).abs()
//}