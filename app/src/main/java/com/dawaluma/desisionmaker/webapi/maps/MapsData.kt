package com.dawaluma.desisionmaker.webapi.maps

import kotlinx.serialization.*

@Serializable
data class MapsResponse(val results: List<MapsPlace>)

@Serializable
data class MapsPlace(
    val business_status: String ="",
    val formatted_address: String ="",
    val geometry: Geometry,
    val icon: String= "",
    val icon_background_color: String ="",
    val icon_mask_base_uri: String ="",
    val name: String ="",
    val opening_hours: OpeningHours? = null,
    val photos: List<Photo> = emptyList(),
    val place_id: String = "",
    val plus_code: PlusCode? =null,
    val rating: Double,
    val reference: String = "",
    val types: List<String> = emptyList(),
    val user_ratings_total: Int=0
)

@Serializable
data class Geometry(
    val location: Location,
    val viewport: Viewport
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class Viewport(
    val northeast: Location,
    val southwest: Location
)

@Serializable
data class OpeningHours(
    val open_now: Boolean
)

@Serializable
data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)

@Serializable
data class PlusCode(
    val compound_code: String,
    val global_code: String
)
