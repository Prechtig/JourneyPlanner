package com.prechtig.journeyplanner.controller

import com.prechtig.journeyplanner.dto.JourneyDto
import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.service.JourneyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/journey")
class JourneyController(val journeyService: JourneyService) {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createJourney(@Valid @RequestBody journeyDto: JourneyDto) = journeyService.createJourney(journeyDto.start, journeyDto.end)

	@GetMapping("/all")
	fun findAllJourneys() = journeyService.findAll()

	@GetMapping("/{journeyId}")
	fun journeyById(@PathVariable journeyId: Long): ResponseEntity<Journey?> {
		val journey = journeyService.findById(journeyId)
		return journey?.let { ResponseEntity(it, HttpStatus.OK) }
			?: ResponseEntity(null, HttpStatus.NO_CONTENT)
	}
}