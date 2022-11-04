package com.prechtig.journeyplanner.dto

import javax.validation.constraints.NotBlank

data class JourneyDto(
	@field:NotBlank(message = "Start is required") var start: String = "",
	@field:NotBlank(message = "End is required") var end: String = "",
)