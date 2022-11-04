package com.prechtig.journeyplanner.controller

import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.model.User
import com.prechtig.journeyplanner.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(val userService: UserService) {

	@GetMapping("/all")
	fun findAllUsers() = userService.findAll()

	@PostMapping("/{userName}")
	@ResponseStatus(HttpStatus.CREATED)
	fun createUser(@PathVariable userName: String) = userService.createUser(userName)

	@GetMapping("/{userId}")
	fun findById(@PathVariable userId: Long): ResponseEntity<User> {
		return userService.findById(userId)?.let { ResponseEntity.ok(it) }
			?: ResponseEntity(HttpStatus.NO_CONTENT)
	}

	@GetMapping("/{userId}/journeys")
	fun journeysByUser(@PathVariable userId: Long): List<Journey>? {
		return listOf(
			Journey("København", "Køge"),
			Journey("København", "Maribo")
		)
	}
}