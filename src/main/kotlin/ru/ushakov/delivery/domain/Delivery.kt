package ru.ushakov.delivery.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "deliveries")
data class Delivery(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val orderId: Long,
    val deliveryDate: LocalDate,
    val address: String,
    var status: String
)