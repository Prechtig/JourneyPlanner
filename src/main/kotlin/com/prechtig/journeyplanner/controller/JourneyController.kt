package com.prechtig.journeyplanner.controller

import com.prechtig.journeyplanner.dto.JourneyDto
import com.prechtig.journeyplanner.helper.getUserId
import com.prechtig.journeyplanner.helper.userNotFound
import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/journey")
class JourneyController(val userService: UserService) {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun createJourney(request: HttpServletRequest, @Valid @RequestBody journeyDto: JourneyDto): ResponseEntity<Journey> {
		val userId = request.session.getUserId()
		val journey = Journey(journeyDto.start, journeyDto.end)
		userService.addJourney(userId, journey)
		return ResponseEntity(journey, HttpStatus.CREATED)
	}

	@GetMapping("/{journeyId}", produces = [MediaType.APPLICATION_JSON_VALUE])
	fun journeyById(request: HttpServletRequest, @PathVariable journeyId: String): ResponseEntity<Journey?> {
		val userId = request.session.getUserId()
		val user = userService.findById(userId) ?: throw userNotFound(userId)
		val journey = user.journeys[journeyId]
		return journey?.let { ResponseEntity(it, HttpStatus.OK) }
			?: ResponseEntity(null, HttpStatus.NO_CONTENT)
	}
}