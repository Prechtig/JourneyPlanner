package com.prechtig.journeyplanner.error

import java.lang.Exception

class MissingUserIdHeaderException(message: String): Exception(message)

class InvalidUserIdHeaderException(message: String): Exception(message)