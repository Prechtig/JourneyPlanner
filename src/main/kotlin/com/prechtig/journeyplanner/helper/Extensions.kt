package com.prechtig.journeyplanner.helper

import javax.servlet.http.HttpSession

fun HttpSession.setUserId(userId: Long) = setAttribute("userId", userId)
fun HttpSession.getUserId(): Long = getAttribute("userId") as Long