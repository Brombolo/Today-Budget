package com.example.data

import java.time.*
import java.time.temporal.ChronoUnit

object BudgetCalendarHelper {

    /**
     * Map a physical timestamp (in ms) to its "Business LocalDate" based on the custom daily reset hour.
     * E.g. if startHour = 4, then June 11 03:00 AM has a business date of June 10.
     */
    fun getBusinessDate(epochMilli: Long, startHour: Int): LocalDate {
        val instant = Instant.ofEpochMilli(epochMilli)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val businessDateTime = localDateTime.minusHours(startHour.toLong())
        return businessDateTime.toLocalDate()
    }

    /**
     * Given a target business date and billing start day (e.g. 12),
     * calculate the start date (inclusive) of the billing cycle containing that business date.
     */
    fun getCycleStart(businessDate: LocalDate, startDay: Int): LocalDate {
        val maxDayThisMonth = businessDate.lengthOfMonth()
        val capDayThisMonth = Math.min(startDay, maxDayThisMonth)
        
        return if (businessDate.dayOfMonth >= capDayThisMonth) {
            LocalDate.of(businessDate.year, businessDate.month, capDayThisMonth)
        } else {
            // It belongs to the cycle starting in the previous month
            val prevMonthDate = businessDate.minusMonths(1)
            val maxDayPrevMonth = prevMonthDate.lengthOfMonth()
            val capDayPrevMonth = Math.min(startDay, maxDayPrevMonth)
            LocalDate.of(prevMonthDate.year, prevMonthDate.month, capDayPrevMonth)
        }
    }

    /**
     * Given the cycle start date and the billing start day,
     * calculate the next billing cycle's start date.
     */
    fun getNextCycleStart(cycleStart: LocalDate, startDay: Int): LocalDate {
        val nextMonthDate = cycleStart.plusMonths(1)
        val maxDayNextMonth = nextMonthDate.lengthOfMonth()
        val capDayNextMonth = Math.min(startDay, maxDayNextMonth)
        return LocalDate.of(nextMonthDate.year, nextMonthDate.month, capDayNextMonth)
    }

    /**
     * Converts a business LocalDate back into physical timestamps: start of the business day (inclusive)
     */
    fun getBusinessDayStartTimestamp(businessDate: LocalDate, startHour: Int): Long {
        val localDateTime = businessDate.atStartOfDay().plusHours(startHour.toLong())
        val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }

    /**
     * Converts a business LocalDate back into physical timestamps: end of the business day (exclusive)
     */
    fun getBusinessDayEndTimestamp(businessDate: LocalDate, startHour: Int): Long {
        val localDateTime = businessDate.plusDays(1).atStartOfDay().plusHours(startHour.toLong())
        val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
        return zonedDateTime.toInstant().toEpochMilli()
    }
}
