package consumer

import (
	"encoding/json"
	"errors"
	"sms-store/model"
	"sms-store/repository"
	"testing"
)

// not testing consume messages function as it is kafka dependent

func TestHandleKafkaMessage_InvalidJSON(t *testing.T) {
	called := false

	saveMessage = func(m model.Sms) error {
		called = true
		return nil
	}
	defer func() { saveMessage = repository.SaveMessage }()

	handleKafkaMessage([]byte("not-json"))

	if called {
		t.Fatal("saveMessage should not be called on invalid JSON")
	}
}

func TestHandleKafkaMessage_ValidJSON(t *testing.T) {
	var saved model.Sms

	saveMessage = func(m model.Sms) error {
		saved = m
		return nil
	}
	defer func() { saveMessage = repository.SaveMessage }()

	msg := model.Sms{
		PhoneNumber: "123",
		Message:     "hello",
		Status:      "SUCCESS",
	}

	data, _ := json.Marshal(msg)

	handleKafkaMessage(data)

	if saved.Message != "hello" {
		t.Fatalf("expected message to be saved")
	}
}

func TestHandleKafkaMessage_SaveError(t *testing.T) {
	saveMessage = func(m model.Sms) error {
		return errors.New("db down")
	}
	defer func() { saveMessage = repository.SaveMessage }()

	msg := model.Sms{
		PhoneNumber: "123",
		Message:     "hello",
		Status:      "SUCCESS",
	}

	data, _ := json.Marshal(msg)

	handleKafkaMessage(data) // should still be called
}
