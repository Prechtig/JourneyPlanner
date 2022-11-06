package com.prechtig.journeyplanner.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Users")
data class User(val name: String): Serializable {

	// TODO: Can we make this non-nullable?
	@Id
	var id: Long? = null
	var journeys: MutableMap<String, Journey> = mutableMapOf()
}