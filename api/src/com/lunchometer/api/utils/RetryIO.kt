//package com.lunchometer.api.utils
//
//import kotlinx.coroutines.experimental.delay
//
//suspend fun <T> retryIO(
//    times: Int = Int.MAX_VALUE,
//    initialDelay: Long = 100, // 0.1 second
//    maxDelay: Long = 10000,    // 1 second
//    factor: Double = 2.0,
//    block: suspend () -> T): T
//{
//    var currentDelay = initialDelay
//    repeat(times - 1) {
//        val response = block()
//        if(response != null) {
//            return response
//        } else {
//            delay(currentDelay)
//            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
//        }
//    }
//    return block()
//}