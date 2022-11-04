package com.prechtig.journeyplanner.error

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@ControllerAdvice
class RestExceptionHandler: ResponseEntityExceptionHandler() {

	override fun handleMethodArgumentNotValid(
		ex: MethodArgumentNotValidException,
		headers: HttpHeaders,
		status: HttpStatus,
		request: WebRequest
	): ResponseEntity<Any> {
		val body = LinkedHashMap<String, Any>()
		val errors = ex.bindingResult.fieldErrors
			.map { it.defaultMessage }
			.toList()

		body["timestamp"] = Date()
		body["status"] = status.value()
		body["errors"] = errors

		return ResponseEntity(body, headers, status)
	}
}