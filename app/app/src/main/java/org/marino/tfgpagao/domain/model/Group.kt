package org.marino.tfgpagao.domain.model

data class Group (
    val id: Int = 0,
    val name: String = "",
    val description: String?,
    val members: List<Member>?,
    val receipts: List<Receipt>?,
)