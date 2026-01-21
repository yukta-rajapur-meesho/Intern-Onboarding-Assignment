package handler

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"sms-store/model"
	"sms-store/repository"
	"testing"
)

func TestSmsHandler_MethodNotAllowed(t *testing.T) {
	req := httptest.NewRequest(http.MethodPost, "/v1/user/123/messages", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusMethodNotAllowed {
		t.Fatalf("expected 405, got %d", rr.Code)
	}
}

func TestSmsHandler_InvalidURL_1(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/v1/user", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusBadRequest {
		t.Fatalf("expected 400, got %d", rr.Code)
	}
}

func TestSmsHandler_InvalidURL_2(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/v1/user/123/messages/status", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusBadRequest {
		t.Fatalf("expected 400, got %d", rr.Code)
	}
}

func TestSmsHandler_InvalidURL_3(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/api/user/123/unknown", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rr.Code)
	}

	if rr.Body.String() != "Invalid API" {
		t.Fatalf("unexpected response body")
	}
}

func TestGetMessages_Success(t *testing.T) {
	// mock repository
	findByUser = func(phoneNumber string) ([]model.Sms, error) {
		return []model.Sms{
			{PhoneNumber: "123", Message: "hello"},
		}, nil
	}
	defer func() { findByUser = repository.FindByUser }()

	req := httptest.NewRequest(http.MethodGet, "/v1/user/123/messages", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rr.Code)
	}

	var body []map[string]string
	// This decodes the JSON response body from the HTTP test recorder into the 'body' variable,
	// reporting an error if the response is not valid JSON.
	if err := json.NewDecoder(bytes.NewBuffer(rr.Body.Bytes())).Decode(&body); err != nil {
		t.Fatalf("invalid JSON response")
	}

	if body[0]["message"] != "hello" {
		t.Fatalf("unexpected response body")
	}
}
