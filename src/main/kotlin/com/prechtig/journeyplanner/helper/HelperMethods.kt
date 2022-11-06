package com.prechtig.journeyplanner.helper

import com.prechtig.journeyplanner.error.InvalidUserIdHeaderException

fun userNotFound(userId: Long) = InvalidUserIdHeaderException("User with the given id '$userId' not found")