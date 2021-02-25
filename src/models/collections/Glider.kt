package dev.psuchanek.models.data

import org.bson.BsonObjectId
import org.bson.codecs.pojo.annotations.BsonId

data class Glider(
    @BsonId
    val gliderID: String = BsonObjectId().toString(),
    val manufacturer: String = "",
    val model: String = "",
    val gliderHours: String = ""
    )