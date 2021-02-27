package dev.psuchanek.routes

import dev.psuchanek.database.getFlightsForUser
import dev.psuchanek.database.saveFlight
import dev.psuchanek.models.data.Flight
import dev.psuchanek.models.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.flightRoutes() {

    route("/getFlights") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val flights = getFlightsForUser(email)

                try {
                    call.respond(HttpStatusCode.OK, flights)
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "Something went wrong. Please try again"))
                }
            }
        }

    }

    route("addFlight") {
        authenticate {
            post {
                val request = try {
                    call.receive<Flight>()
                } catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong."))
                    return@post
                }

                if (saveFlight(request)) {
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "Flight record added successfully."))
                } else {
                    call.respond(
                        HttpStatusCode.Conflict,
                        SimpleResponse(false, "Something went wrong. Flight record not received.")
                    )
                }
            }
        }
    }
}