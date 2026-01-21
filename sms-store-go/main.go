package main

import (
	"log"
	"net/http"
	"sms-store/consumer"
	"sms-store/handler"
	"sms-store/repository"
)

func main() {

	// Init DB
	err := repository.Init("mongodb://localhost:27017")
	if err != nil {
		log.Fatal(err)
	}

	// HTTP server
	http.HandleFunc("/v1/user/", handler.GetMessages)

	// Kafka consumer
	go consumer.ConsumeMessages()

	log.Println("SMS Store running on :8081")
	log.Fatal(http.ListenAndServe(":8081", nil))
}
