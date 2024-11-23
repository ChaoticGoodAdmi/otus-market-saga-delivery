package ru.ushakov.delivery.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class WarehouseEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    val objectMapper: ObjectMapper
) {

    fun sendDeliveryReservedEvent(event: DeliveryReservedEvent) {
        kafkaTemplate.send("DeliveryReserved", objectMapper.writeValueAsString(event))
        println("DeliveryReservedEvent sent to Kafka: $event")
    }

    fun sendDeliveryReserveFailedEvent(event: DeliveryFailedEvent) {
        kafkaTemplate.send("DeliveryReserveFailed", objectMapper.writeValueAsString(event))
        println("DeliveryFailedEvent sent to Kafka: $event")
    }
}