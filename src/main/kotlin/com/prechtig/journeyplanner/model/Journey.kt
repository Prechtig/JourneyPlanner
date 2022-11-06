package com.prechtig.journeyplanner.model

import org.springframework.data.annotation.Id
import java.io.Serializable
import java.util.*

data class Journey(val start: String, val end: String, @Id val id: String = UUID.randomUUID().toString()): Serializable