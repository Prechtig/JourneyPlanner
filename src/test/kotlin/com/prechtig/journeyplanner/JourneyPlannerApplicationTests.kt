package com.prechtig.journeyplanner

import com.prechtig.journeyplanner.model.Journey
import com.prechtig.journeyplanner.model.User
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*


@SpringBootTest
class JourneyPlannerApplicationTests {

	@Test
	fun contextLoads() {
	}

}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

	@Test
	fun testJourneyById() {
		// Let's see if we can find a journey without a user id in the header
		var response = restTemplate.getForEntity("/journey/123", String::class.java)
		Assertions.assertEquals(response.statusCode, HttpStatus.BAD_REQUEST)
		Assertions.assertTrue(response.body is String)
		Assertions.assertEquals(response.body, "Missing user id in the header with key 'api-user-id'")


		// Let's create a user
		val userResponse = restTemplate.postForEntity("/user/TestUser", null, User::class.java)
		Assertions.assertEquals(userResponse.statusCode, HttpStatus.CREATED)
		Assertions.assertTrue(userResponse.body is User)
		val user = userResponse.body as User


		// Create headers with user id
		val headers = HttpHeaders()
		headers.contentType = MediaType.APPLICATION_JSON
		headers.set("api-user-id", user.id.toString())

		// Let's see if we get no content when a user id is in the header, but no journeys yet
		var request = HttpEntity<String>(headers)
		response = restTemplate.exchange("/journey/123", HttpMethod.GET, request, String::class.java)
		Assertions.assertEquals(response.statusCode, HttpStatus.NO_CONTENT)


		// Create a journey that we can query
		val json = """
			{
			"start": "Horsens",
			"end": "København"
			}
			""".trimIndent()
		request = HttpEntity(json, headers)
		val entity = restTemplate.postForEntity("/journey", request, Journey::class.java)
		Assertions.assertEquals(entity.statusCode, HttpStatus.CREATED)
		Assertions.assertTrue(entity.body is Journey)

		val journeyId = (entity.body as Journey).id


		// Let's see if we can find the created journey
		response = restTemplate.exchange("/journey/{journeyId}", HttpMethod.GET, request, String::class.java, journeyId)
		Assertions.assertEquals(response.statusCode, HttpStatus.OK)
		Assertions.assertTrue(response.body is String)
		Assertions.assertTrue(response.body!!.contains("start"))
		Assertions.assertTrue(response.body!!.contains("end"))
		Assertions.assertTrue(response.body!!.contains("København"))
		Assertions.assertTrue(response.body!!.contains("Horsens"))
		Assertions.assertTrue(response.body!!.contains("id"))
		Assertions.assertTrue(response.body!!.contains(journeyId))
	}

	@Test
	fun testJourneysByUserId() {
		val entity = restTemplate.getForEntity("/user/123/journeys", List::class.java)
		Assertions.assertEquals(entity.statusCode, HttpStatus.OK)
	}
}