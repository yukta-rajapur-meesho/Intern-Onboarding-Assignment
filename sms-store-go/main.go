package main

import (
	"log"
	"net/http"
	"os"
	"sms-store/consumer"
	"sms-store/handler"
	"sms-store/repository"
)

func main() {

	// Init DB
	err := repository.Init(os.Getenv("MONGO_URI"), os.Getenv("MONGO_DB"), os.Getenv("MONGO_COLLECTION"))
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
