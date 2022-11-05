package com.prechtig.journeyplanner.filter

import com.prechtig.journeyplanner.error.InvalidUserIdHeaderException
import com.prechtig.journeyplanner.error.MissingUserIdHeaderException
import com.prechtig.journeyplanner.service.UserService
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.NumberFormatException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter responsible for validation the api-user-id header
 */
@Component
@Order(2)
class ApiUserIdFilter(val userService: UserService): OncePerRequestFilter() {

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		val apiUserId = request.getHeader("api-user-id")
			?: throw MissingUserIdHeaderException("Missing user id in the header with key 'api-user-id'")

		try {
			userService.findById(apiUserId.toLong())
				?: throw InvalidUserIdHeaderException("User with the given id '$apiUserId' not found")
		} catch (ex: NumberFormatException) {
			throw InvalidUserIdHeaderException("Invalid format for the header 'api-user-id'. Must be a long value")
		}

		filterChain.doFilter(request, response)
	}

	override fun shouldNotFilter(request: HttpServletRequest): Boolean {
		return "POST" == request.method
				&& (request.servletPath == "/journey"
					|| request.servletPath.startsWith("/user/"))
	}
}