package consumer

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	"sms-store/model"
	"sms-store/repository"

	"github.com/segmentio/kafka-go"
)

func ConsumeMessages() {
	log.Println("Kafka consumer started")

	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{"localhost:9092"},
		Topic:   "sms-messages",
		GroupID: "sms-store",
	})

	for {
		msg, err := reader.ReadMessage(context.Background())
		if err != nil {
			log.Println("Kafka read error:", err)
			continue
		}

		handleKafkaMessage(msg.Value)
	}
}

func handleKafkaMessage(data []byte) {
	var message model.Sms

	fmt.Println("RAW KAFKA MESSAGE:", string(data))

	if err := json.Unmarshal(data, &message); err != nil {
		log.Println("Invalid JSON:", err)
		return
	}

	if err := repository.SaveMessage(message); err != nil {
		log.Println("DB error:", err)
	}
}
