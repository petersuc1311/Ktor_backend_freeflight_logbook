package dev.psuchanek.routes

import dev.psuchanek.database.checkPasswordForEmail
import dev.psuchanek.models.requests.AccountRequest
import dev.psuchanek.models.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute() {
    route("/login") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong. Check request parsing."))
                return@post
            }

            val isPasswordCorrect = checkPasswordForEmail(request.email, request.password)
            if (!isPasswordCorrect) {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "The email or password is incorrect."))
                return@post
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Login successful."))
            }
        }
    }
}