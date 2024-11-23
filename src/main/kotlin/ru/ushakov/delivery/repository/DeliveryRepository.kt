package ru.ushakov.delivery.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.ushakov.delivery.domain.Delivery

interface DeliveryRepository : JpaRepository<Delivery, Long>