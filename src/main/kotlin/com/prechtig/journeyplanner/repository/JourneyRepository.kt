package com.prechtig.journeyplanner.repository

import com.prechtig.journeyplanner.model.Journey
import org.springframework.data.repository.CrudRepository

interface JourneyRepository: CrudRepository<Journey, Long>