package com.prechtig.journeyplanner

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

	@GetMapping("journey/{journeyId}")
	fun journeyById(@PathVariable journeyId: Long): Journey? {
		return Journey(journeyId, "CPH", "ROME")
	}

	@GetMapping("user/{userId}/journeys")
	fun journeysByUser(@PathVariable userId: Long): List<Journey>? {
		return listOf(
			Journey(1, "København", "Køge"),
			Journey(2, "København", "Maribo")
		)
	}
}

data class Journey(val id: Long, val start: String, val end: String)