package com.example.mystore.data.model


import com.google.gson.annotations.SerializedName

// 1. Respuesta de Cuenta
data class AccountResponse(
    val status: Int,
    val data: AccountDataDto?
)

data class AccountDataDto(
    val puuid: String,
    val name: String,
    val tag: String,
    @SerializedName("account_level") val accountLevel: Int,
    val region: String,
    val card: CardDto?
)

data class CardDto(
    val small: String?,
    val large: String?
)

// 2. Respuesta de MMR / Rango
data class MmrResponse(
    val status: Int,
    val data: MmrDataDto?
)

data class MmrDataDto(
    @SerializedName("current_data") val currentData: CurrentMmrDto?
)

data class CurrentMmrDto(
    @SerializedName("currenttierpatched") val currentTierPatched: String?,
    @SerializedName("ranking_in_tier") val rankingInTier: Int?,
    val mmr_change_to_last_game: Int?,
    val images: MmrImagesDto?
)

data class MmrImagesDto(
    val small: String?,
    val large: String?
)

// 3. Respuesta de Historial de Partidas
data class MatchesResponse(
    val status: Int,
    val data: List<MatchDto>?
)

data class MatchDto(
    val metadata: MatchMetadataDto?,
    val players: MatchPlayersDto?
)

data class MatchMetadataDto(
    val map: String?,
    val mode: String?,
    @SerializedName("match_id") val matchId: String?
)

data class MatchPlayersDto(
    @SerializedName("all_players") val allPlayers: List<PlayerDataDto>?
)

data class PlayerDataDto(
    val name: String,
    val tag: String,
    val team: String,
    val character: String,
    val assets: PlayerAssetsDto?,
    val stats: PlayerStatsDto?
)

data class PlayerAssetsDto(
    val card: PlayerCardAssetDto?,
    val agent: PlayerAgentAssetDto?
)

data class PlayerCardAssetDto(val small: String?)
data class PlayerAgentAssetDto(val small: String?, val full: String?)

data class PlayerStatsDto(
    val score: Int,
    val kills: Int,
    val deaths: Int,
    val assists: Int
)