package dev.psuchanek.routes

import dev.psuchanek.database.checkIfUserExists
import dev.psuchanek.database.registerUser
import dev.psuchanek.models.collections.User
import dev.psuchanek.models.requests.AccountRequest
import dev.psuchanek.models.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute() {
    route("/register") {
        post {
            val request = try {
                call.receive<AccountRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong. Check request parsing."))
                return@post
            }

            val userExists = checkIfUserExists(request.email)
            if (!userExists) {
                //TODO: add hashing with salt to the password for security
                if(registerUser(User(request.email, request.password))) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Account successfully created."))
                } else {
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occurred."))
                }
            } else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "An user with that email already exists"))
            }
        }
    }
}