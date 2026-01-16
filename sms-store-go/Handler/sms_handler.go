package Handler

import (
	"encoding/json"
	"net/http"
	"strings"
	"fmt"

	"sms-store/Repository"
)

func GetMessages(w http.ResponseWriter, r *http.Request) {

    if r.Method != http.MethodGet {
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Write([]byte("Method Not Allowed"))
		return
	}

    fmt.Println("ACK")

	parts := strings.Split(r.URL.Path, "/")
	userId := parts[3]

	messages, err := Repository.FindByUser(userId)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	json.NewEncoder(w).Encode(messages)
}
