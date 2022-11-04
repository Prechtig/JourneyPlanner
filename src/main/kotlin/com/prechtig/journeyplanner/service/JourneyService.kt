package com.prechtig.journeyplanner.service

import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.repository.JourneyRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class JourneyService(val journeyRepository: JourneyRepository) {

	@Cacheable("journeyCache")
	fun findById(journeyId: Long): Journey? {
		return journeyRepository.findById(journeyId).orElse(null)
	}

	@Cacheable("journeyCache")
	fun createJourney(start: String, end: String): Journey {
		return journeyRepository.save(Journey(start, end))
	}

	fun findAll(): List<Journey> {
		return journeyRepository.findAll().toList()
	}
}