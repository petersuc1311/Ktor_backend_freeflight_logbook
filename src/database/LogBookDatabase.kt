package dev.psuchanek.database


import com.mongodb.client.model.DeleteOptions
import dev.psuchanek.models.collections.DeletedUser
import dev.psuchanek.models.collections.User
import dev.psuchanek.models.data.Flight
import dev.psuchanek.security.checkHashForPassword
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("logbook_database")
private val flightCollection = database.getCollection<Flight>()
private val userCollection = database.getCollection<User>()
private val deletedUserCollection = database.getCollection<DeletedUser>()

suspend fun registerUser(user: User): Boolean {
    return userCollection.insertOne(user).wasAcknowledged()
}

suspend fun deleteAccount(email: String): Boolean {
    val user = userCollection.findOne(User::email eq email)
    println("Result for deleted Account: $user")
    user?.let {
        deletedUserCollection.insertOne(DeletedUser(user.email))
        return userCollection.deleteOneById(user.id).wasAcknowledged()
    } ?: return false
}

suspend fun deleteEmailFromDeletedUserList(email: String) {
    deletedUserCollection.deleteOne(DeletedUser::email eq email)
}

suspend fun checkIfUserExists(email: String): Boolean {
    return userCollection.findOne(User::email eq email) != null
}

suspend fun checkIfUserExistsInDeletedList(email: String): Boolean {
    return deletedUserCollection.findOne(DeletedUser::email eq email) != null
}

suspend fun checkPasswordForEmail(email: String, passwordToCheck: String): Boolean {
    val actualPassword = userCollection.findOne(User::email eq email)?.password ?: return false
    return checkHashForPassword(passwordToCheck, actualPassword)
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

suspend fun deleteFlight(flightID: String): Boolean {
    val flight = flightCollection.findOneById(flightID)
    flight?.let {
        return flightCollection.deleteOneById(flightID).wasAcknowledged()
    } ?: return false
}

suspend fun deleteAllFlights() {
    flightCollection.drop()
}
