#!/usr/bin/env bash
set -e

echo "Stopping all services..."
brew services stop kafka redis mongodb-community || true
pkill -f spring-boot || true
pkill -f "go run" || true

echo "Starting infra services..."
brew services start redis
brew services start mongodb-community
brew services start kafka

echo "Waiting for Kafka..."
sleep 10

echo "Starting apps..."

(
  cd ~/Documents/Onboarding/Intern-Assignment/sms-sender-java
  mvn spring-boot:run
)

(
  cd ~/Documents/Onboarding/Intern-Assignment/sms-store-go
  go run main.go > go.log 2>&1 &
)

echo "Done"
