package com.example.mystore.data.model


data class AgentResponse(
    val status: Int,
    val data: List<AgentDto>
)

data class AgentDto(
    val uuid: String,
    val displayName: String,
    val description: String,
    val displayIcon: String,
    val fullPortrait: String?,
    val isPlayableCharacter: Boolean,
    val role: RoleDto?,
    val abilities: List<AbilityDto>?
)
data class RoleDto(
    val uuid: String,
    val displayName: String,
    val displayIcon: String?
)

data class AbilityDto(
    val slot: String,
    val displayName: String,
    val description: String?,
    val displayIcon: String?
)
