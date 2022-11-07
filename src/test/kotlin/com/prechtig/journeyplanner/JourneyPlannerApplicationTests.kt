package com.prechtig.journeyplanner

import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.model.User
import com.prechtig.journeyplanner.repository.UserRepository
import org.json.JSONArray
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import java.time.Duration
import kotlin.system.measureTimeMillis


@SpringBootTest
class JourneyPlannerApplicationTests {

	@Test
	fun contextLoads() {
	}

}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate, @Autowired val userRepository: UserRepository) {

	@Test
	fun testJourneyById() {
		// Can we find a journey without a user id in the header?
		var response = restTemplate.getForEntity("/journey/123", String::class.java)
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
		Assertions.assertTrue(response.body is String)
		Assertions.assertEquals(response.body, "Missing user id in the header with key 'api-user-id'")

		// Create a user
		val user = createUser("TestUser")

		// Create headers with user id
		val headers = createHeaders(user)

		// Do we get no content when a user id is in the header, but no journeys yet
		var request = HttpEntity<String>(headers)
		response = restTemplate.exchange("/journey/123", HttpMethod.GET, request, String::class.java)
		Assertions.assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

		// Create a journey that we can query
		val json = """
			{
			"start": "Horsens",
			"end": "København"
			}
			""".trimIndent()
		request = HttpEntity(json, headers)
		val entity = restTemplate.postForEntity("/journey", request, Journey::class.java)
		Assertions.assertEquals(HttpStatus.CREATED, entity.statusCode)
		Assertions.assertTrue(entity.body is Journey)

		val journeyId = (entity.body as Journey).id

		// Can we find the created journey
		val journeyResponse = restTemplate.exchange("/journey/{journeyId}", HttpMethod.GET, request, Journey::class.java, journeyId)
		Assertions.assertEquals(HttpStatus.OK, journeyResponse.statusCode)
		Assertions.assertTrue(journeyResponse.body is Journey)
		Assertions.assertEquals("Horsens", journeyResponse.body!!.start)
		Assertions.assertEquals("København", journeyResponse.body!!.end)
		Assertions.assertEquals(journeyId, journeyResponse.body!!.id)
	}

	@Test
	fun testPerformanceSingleJourney() {
		// Let's define some arbitrary allowable query time
		val allowedQueryTime = Duration.ofMillis(250)

		val user = createUser("testPerformanceSingleJourney")
		val headers = createHeaders(user)

		// Add a lot of journeys to the user
		for (i in 1..100_000) {
			val journey = Journey("start", "end")
			user.journeys[journey.id] = journey
		}
		// Persist the added journeys to the user entity
		userRepository.save(user)

		// Create a journey we can query
		val createdJourney = addJourney(headers)

		// Let's try to query the journey and test the performance of the service
		val journeyId = createdJourney.id
		var response: ResponseEntity<Journey>
		val queryTime = Duration.ofMillis(measureTimeMillis {
			response = restTemplate.exchange("/journey/{journeyId}", HttpMethod.GET, HttpEntity<String>(headers), Journey::class.java, journeyId)
		})
		Assertions.assertTrue(queryTime < allowedQueryTime, "Actual query time ($queryTime) exceeded the allowed query time ($allowedQueryTime)")
		Assertions.assertEquals(HttpStatus.OK, response.statusCode)
		Assertions.assertTrue(response.body is Journey)
		Assertions.assertEquals("TestStart", response.body!!.start)
		Assertions.assertEquals("TestEnd", response.body!!.end)
		Assertions.assertEquals(journeyId, response.body!!.id)
	}

	@Test
	fun testPerformanceAllJourneysForUser() {
		// Let's define some arbitrary allowable query time
		val allowedQueryTimeWithoutCache = Duration.ofMillis(2000)
		val allowedQueryTimeWithCache = Duration.ofMillis(500)

		val user = createUser("testPerformanceAllJourneysForUser")
		val headers = createHeaders(user)

		// Add a lot of journeys to the user
		val journeys = 100_000
		for (i in 0 until journeys) {
			val journey = Journey("start", "end")
			user.journeys[journey.id] = journey
		}
		// Persist the added journeys to the user entity
		userRepository.save(user)

		////////////////////////////////////////
		// Test the performance of the service without the user being cached
		////////////////////////////////////////
		var response: ResponseEntity<String>
		var queryTime = Duration.ofMillis(measureTimeMillis {
			response = restTemplate.exchange("/user/{userId}/journeys", HttpMethod.GET, HttpEntity<String>(headers), String::class.java, user.id!!)
		})
		Assertions.assertTrue(queryTime < allowedQueryTimeWithoutCache, "Actual query time ($queryTime) exceeded the allowed query time ($allowedQueryTimeWithoutCache)")
		Assertions.assertEquals(HttpStatus.OK, response.statusCode)

		// Check the json result contains the expected number of entries
		var json = JSONArray(response.body)
		Assertions.assertEquals(journeys, json.length())

		////////////////////////////////////////
		// Test the performance of the service with the user being cached
		////////////////////////////////////////
		queryTime = Duration.ofMillis(measureTimeMillis {
			response = restTemplate.exchange("/user/{userId}/journeys", HttpMethod.GET, HttpEntity<String>(headers), String::class.java, user.id!!)
		})
		Assertions.assertTrue(queryTime < allowedQueryTimeWithCache, "Actual query time ($queryTime) exceeded the allowed query time ($allowedQueryTimeWithCache)")
		Assertions.assertEquals(HttpStatus.OK, response.statusCode)

		// Check the json result contains the expected number of entries
		json = JSONArray(response.body)
		Assertions.assertEquals(journeys, json.length())
	}

	private fun addJourney(headers: HttpHeaders): Journey {
		val json = """
			{
			"start": "TestStart",
			"end": "TestEnd"
			}
			""".trimIndent()
		val request = HttpEntity(json, headers)
		val response = restTemplate.postForEntity("/journey", request, Journey::class.java)
		Assertions.assertTrue(response.body is Journey)
		return response.body as Journey
	}

	private fun createUser(userName: String): User {
		val userResponse = restTemplate.postForEntity("/user/$userName", null, User::class.java)
		Assertions.assertEquals(HttpStatus.CREATED, userResponse.statusCode)
		Assertions.assertTrue(userResponse.body is User)
		return userResponse.body as User
	}

	private fun createHeaders(user: User): HttpHeaders {
		// Create headers with user id
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_JSON
		headers.set("api-user-id", user.id.toString())
		return headers
	}
}