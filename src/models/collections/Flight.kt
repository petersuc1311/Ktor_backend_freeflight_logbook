package dev.psuchanek.models.data

import org.bson.BsonObjectId
import org.bson.codecs.pojo.annotations.BsonId

data class Flight(
    @BsonId
    val id: String = BsonObjectId().toString(),
    val date: Long,
    val title: String,
    val duration: String,
    val typeOfFlight: String,
    val gliderFlown: Glider,
    val distanceFlown: Float = 0.0f
)