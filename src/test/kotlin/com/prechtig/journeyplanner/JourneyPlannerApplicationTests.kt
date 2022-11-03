package com.prechtig.journeyplanner

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

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
		val entity = restTemplate.getForEntity("/journey/123", Journey::class.java)
		Assertions.assertEquals(entity.statusCode, HttpStatus.OK)
	}

	@Test
	fun testJourneysByUserId() {
		val entity = restTemplate.getForEntity("/user/123/journeys", List::class.java)
		Assertions.assertEquals(entity.statusCode, HttpStatus.OK)
	}
}