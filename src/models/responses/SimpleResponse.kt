package dev.psuchanek.models.responses

data class SimpleResponse(
    val successful: Boolean,
    val message: String
)