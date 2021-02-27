package dev.psuchanek.routes

import dev.psuchanek.database.deleteAccount
import dev.psuchanek.models.requests.AccountRequest
import dev.psuchanek.models.requests.DeleteAccountRequest
import dev.psuchanek.models.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.deleteAccountRoutes() {
    route("/deleteAccount") {
        authenticate {
            post {
                val request = try {
                    call.receive<DeleteAccountRequest>()
                }catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong."))
                    return@post
                }

                if(deleteAccount(request.email)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Your account was successfully deleted."))
                } else {
                    call.respond(HttpStatusCode.Conflict, SimpleResponse(false, "The account was not deleted. There is no account associated with that email."))
                }
            }

        }
    }
}