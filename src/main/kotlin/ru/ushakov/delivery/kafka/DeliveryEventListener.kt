package ru.ushakov.delivery.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import ru.ushakov.delivery.domain.Delivery
import ru.ushakov.delivery.repository.DeliveryRepository
import java.time.LocalDate

@Service
class DeliveryEventListener(
    private val deliveryRepository: DeliveryRepository,
    private val kafkaProducer: WarehouseEventProducer
) {

    @KafkaListener(topics = ["ItemReserved"], groupId = "delivery-service-group")
    fun handleItemReservedEvent(message: String) {
        val event = parseItemReservedEvent(message)

        if (event.deliveryDate.isAfter(LocalDate.now())) {
            val delivery = Delivery(
                orderId = event.orderId,
                deliveryDate = event.deliveryDate,
                address = event.deliveryAddress,
                status = "SCHEDULED"
            )
            deliveryRepository.save(delivery)
            kafkaProducer.sendDeliveryReservedEvent(
                DeliveryReservedEvent(orderId = event.orderId)
            )
            println("Delivery scheduled for orderId: ${event.orderId}, date: ${event.deliveryDate}")
        } else {
            kafkaProducer.sendDeliveryReserveFailedEvent(
                DeliveryFailedEvent(orderId = event.orderId)
            )
            println("DeliveryFailedEvent sent for orderId: ${event.orderId}, invalid date: ${event.deliveryDate}")
        }
    }

    private fun parseItemReservedEvent(message: String): ItemReservedEvent {
        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        return mapper.readValue(message, ItemReservedEvent::class.java)
    }
}

data class ItemReservedEvent(
    val orderId: Long,
    val deliveryDate: LocalDate,
    val deliveryAddress: String
)

data class DeliveryReservedEvent(
    val orderId: Long
)

data class DeliveryFailedEvent(
    val orderId: Long
)

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
    }
}