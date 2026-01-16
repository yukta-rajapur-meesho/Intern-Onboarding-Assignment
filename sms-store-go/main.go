package main

import (
	"log"
	"net/http"
	"sms-store/Handler"
)

func main() {
	http.HandleFunc("/v1/user/", Handler.GetMessages)

	log.Println("SMS Store running on :8081")
	log.Fatal(http.ListenAndServe(":8081", nil))
}
