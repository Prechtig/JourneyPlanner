package com.prechtig.journeyplanner.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Journeys")
data class Journey(val start: String, val end: String): Serializable {

	@Id
	var id: Long? = null
}