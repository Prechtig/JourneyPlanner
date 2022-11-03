package com.prechtig.journeyplanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JourneyPlannerApplication

fun main(args: Array<String>) {
	runApplication<JourneyPlannerApplication>(*args)
}
