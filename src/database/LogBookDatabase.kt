package dev.psuchanek.database


import dev.psuchanek.models.collections.User
import dev.psuchanek.models.data.Flight
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("logbook_database")
private val flightCollection = database.getCollection<Flight>()
private val userCollection = database.getCollection<User>()

suspend fun registerUser(user: User): Boolean {
    return userCollection.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean {
    return userCollection.findOne(User::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = userCollection.findOne(User::email eq email)?.password ?: return false
    return actualPassword == passwordToCheck
}

suspend fun getFlightsForUser(email: String): List<Flight> {
    return flightCollection.find(Flight::owner eq email).toList()
}

suspend fun saveFlight(flight: Flight): Boolean {
    val flightExists = flightCollection.findOneById(flight.id) != null
    return if (flightExists) {
        flightCollection.updateOneById(flight.id, flight).wasAcknowledged()
    } else {
        flightCollection.insertOne(flight).wasAcknowledged()
    }
}