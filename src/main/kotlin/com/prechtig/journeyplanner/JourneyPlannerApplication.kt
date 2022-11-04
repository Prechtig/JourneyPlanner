package com.prechtig.journeyplanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class JourneyPlannerApplication

fun main(args: Array<String>) {
	runApplication<JourneyPlannerApplication>(*args)
}
