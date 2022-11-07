package com.prechtig.journeyplanner.controller

import com.prechtig.journeyplanner.helper.userNotFound
import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.model.User
import com.prechtig.journeyplanner.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(val userService: UserService) {

	@PostMapping("/{userName}")
	@ResponseStatus(HttpStatus.CREATED)
	fun createUser(@PathVariable userName: String) = userService.createUser(userName)

	@GetMapping("/{userId}")
	fun findById(@PathVariable userId: Long): ResponseEntity<User> {
		return userService.findById(userId)?.let { ResponseEntity.ok(it) }
			?: ResponseEntity(HttpStatus.NO_CONTENT)
	}

	@GetMapping("/{userId}/journeys", produces = [MediaType.APPLICATION_JSON_VALUE])
	fun journeysByUser(@PathVariable userId: Long): ResponseEntity<List<Journey>> {
		val user = userService.findById(userId) ?: throw userNotFound(userId)
		return ResponseEntity(user.journeys.values.toList(), HttpStatus.OK)
	}
}