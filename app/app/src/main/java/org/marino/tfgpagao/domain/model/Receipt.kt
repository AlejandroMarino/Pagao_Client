package org.marino.tfgpagao.domain.model

data class Receipt (
    val id: Int,
    val name: String,
    val description: String?,
    val groupId: Int?,
    val participations: List<Participation>?,
    val totalPaid: Double?,
)