package com.prechtig.journeyplanner.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash("Users")
data class User(val name: String): Serializable {

	@Id
	var id: Long? = null
}