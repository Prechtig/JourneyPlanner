package com.prechtig.journeyplanner.service

import com.prechtig.journeyplanner.error.InvalidUserIdHeaderException
import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.model.User
import com.prechtig.journeyplanner.repository.UserRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

	@Cacheable("userCache")
	fun createUser(name: String): User {
		return userRepository.save(User(name))
	}

	@Cacheable("userCache")
	fun findById(id: Long): User? {
		return userRepository.findById(id).orElse(null)
	}

	@CachePut("userCache", key = "#userId")
	fun addJourney(userId: Long, journey: Journey): User {
		val user = userRepository.findById(userId).orElseThrow { InvalidUserIdHeaderException("Something wrong") }
		user.journeys[journey.id] = journey
		return userRepository.save(user)
	}
}