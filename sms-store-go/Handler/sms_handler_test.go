package handler

import (
	"encoding/json"
	"errors"
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

	if rr.Code != http.StatusBadRequest {
		t.Fatalf("expected 400, got %d", rr.Code)
	}
}

func TestSmsHandler_Success(t *testing.T) {
	// mock repository
	findByUser = func(phoneNumber string) ([]model.Sms, error) {
		return []model.Sms{
			{PhoneNumber: "123", Message: "hello"},
		}, nil
	}
	req := httptest.NewRequest(http.MethodGet, "/v1/user/123/messages", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusOK {
		t.Fatalf("expected 200, got %d", rr.Code)
	}

	var body []model.Sms
	if err := json.Unmarshal(rr.Body.Bytes(), &body); err != nil {
		t.Fatalf("invalid JSON response: %v", err)
	}

	if len(body) == 0 || body[0].Message != "hello" {
		t.Fatalf("unexpected response body")
	}
}

func TestSmsHandler_RepositoryError(t *testing.T) {
	findByUser = func(phoneNumber string) ([]model.Sms, error) {
		return nil, errors.New("db down")
	}
	defer func() { findByUser = repository.FindByUser }()

	req := httptest.NewRequest(http.MethodGet, "/v1/user/123/messages", nil)
	rr := httptest.NewRecorder()

	GetMessages(rr, req)

	if rr.Code != http.StatusInternalServerError {
		t.Fatalf("expected 500, got %d", rr.Code)
	}
}
