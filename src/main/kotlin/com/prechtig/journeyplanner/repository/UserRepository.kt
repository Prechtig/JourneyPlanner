package com.prechtig.journeyplanner.repository

import com.prechtig.journeyplanner.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long>