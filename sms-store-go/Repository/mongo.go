package repository

import (
	"context"
	"errors"
	"fmt"
	"log"
	"sms-store/model"
	"time"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

var Collection *mongo.Collection
var Client *mongo.Client

func Init(uri string) error {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	client, err := mongo.Connect(ctx, options.Client().ApplyURI(uri))
	if err != nil {
		return err
	}

	Client = client
	Collection = client.Database("sms_store").Collection("messages")

	log.Println("MongoDB connected")
	return nil
}

func FindByUser(phoneNumber string) ([]model.Sms, error) {
	var results []model.Sms
	cursor, err := Collection.Find(context.TODO(),
		map[string]string{"phoneNumber": phoneNumber})

	if err != nil {
		return nil, err
	}
	cursor.All(context.TODO(), &results)
	return results, nil
}

func SaveMessage(msg model.Sms) error {
	fmt.Println("Saving message to Mongo:", msg)

	if Collection == nil {
		return errors.New("mongo collection not initialized")
	}

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	_, err := Collection.InsertOne(ctx, msg)
	if err != nil {
		fmt.Println("Mongo insert failed:", err)
		return err
	}

	fmt.Println("Mongo insert successful")
	return nil
}
