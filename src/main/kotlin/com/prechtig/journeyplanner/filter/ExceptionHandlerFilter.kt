package com.prechtig.journeyplanner.filter

import com.prechtig.journeyplanner.error.InvalidUserIdHeaderException
import com.prechtig.journeyplanner.error.MissingUserIdHeaderException
import com.prechtig.journeyplanner.error.RestExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter for handling error messages thrown by other filters
 */
@Component
@Order(1)
class ExceptionHandlerFilter(val exceptionHandler: RestExceptionHandler): OncePerRequestFilter() {

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		try {
			filterChain.doFilter(request, response)
		} catch (e: Exception) {
			when (val cause = e.cause) {
				is MissingUserIdHeaderException -> handleMissingUserIdHeaderException(cause, response)
				is InvalidUserIdHeaderException -> handleInvalidUserIdHeaderException(cause, response)
				else ->	throw e
			}
		}
	}

	fun handleMissingUserIdHeaderException(exception: MissingUserIdHeaderException, response: HttpServletResponse) {
		val responseEntity = exceptionHandler.handleMissingUserIdHeaderException(exception)
		writeResponse(responseEntity, response)
	}

	fun handleInvalidUserIdHeaderException(exception: InvalidUserIdHeaderException, response: HttpServletResponse) {
		val responseEntity = exceptionHandler.handleInvalidUserIdHeaderException(exception)
		writeResponse(responseEntity, response)
	}

	fun writeResponse(responseEntity: ResponseEntity<Any>, response: HttpServletResponse) {
		response.status = responseEntity.statusCodeValue
		if (responseEntity.body is String) {
			response.writer.write(responseEntity.body as String)
		}
	}
}