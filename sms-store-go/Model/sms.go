package model

type Sms struct {
	PhoneNumber string `bson:"phoneNumber" json:"phoneNumber"`
	Message     string `bson:"message" json:"message"`
	Status      string `bson:"status" json:"status"`
}
