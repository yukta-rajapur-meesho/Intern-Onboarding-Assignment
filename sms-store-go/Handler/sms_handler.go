package handler

import (
	"encoding/json" //converts go structs to json
	"net/http"      //http server
	"sms-store/repository"
	"strings"
)

var findByUser = repository.FindByUser

func GetMessages(w http.ResponseWriter, r *http.Request) { //w used to write to response, r contains request data

	// sanity check for only GET requests
	if r.Method != http.MethodGet {
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Write([]byte("Method Not Allowed"))
		return
	}

	parts := strings.Split(r.URL.Path, "/")
	if len(parts) != 5 {
		http.Error(w, "Invalid URL", http.StatusBadRequest)
		return
	}

	phonenumber, api := parts[3], parts[4]

	if api == "messages" {
		messages, err := findByUser(phonenumber)
		if err != nil {
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		json.NewEncoder(w).Encode(messages)
	} else {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("Invalid API"))
		return
	}
}
