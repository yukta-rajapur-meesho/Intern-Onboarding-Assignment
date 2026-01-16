package Repository

import (
	"context"
	"sms-store/Model"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

var collection *mongo.Collection

func Init() {
	client, _ := mongo.Connect(context.TODO(),
		options.Client().ApplyURI("mongodb://localhost:27017"))
	collection = client.Database("smsdb").Collection("messages")
}

func FindByUser(userId string) ([]Model.Sms, error) {
	var results []Model.Sms
	cursor, err := collection.Find(context.TODO(),
		map[string]string{"userId": userId})

	if err != nil {
		return nil, err
	}
	cursor.All(context.TODO(), &results)
	return results, nil
}
