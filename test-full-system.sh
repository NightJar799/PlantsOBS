#!/bin/bash

echo "=== 1. Регистрация нового пользователя ==="
REGISTER=$(curl -s -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "fulltest_'$(date +%s)'",
    "email": "fulltest_'$(date +%s)'@example.com",
    "password": "password123",
    "phoneNumber": "+1234567890"
  }')

TOKEN=$(echo $REGISTER | jq -r '.access_token')
USER_ID=$(echo $REGISTER | jq -r '.user.id')

echo "User ID: $USER_ID"
echo "Token: ${TOKEN:0:50}..."

echo -e "\n=== 2. Проверка токена через Auth Service ==="
curl -s -X POST http://localhost:8082/api/auth/validate \
  -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n=== 3. Проверка Audit Service (должны быть записи) ==="
curl -s http://localhost:8081/api/audit | jq '.totalEntries'

echo -e "\n=== 4. Проверка PlantREST (список растений) ==="
curl -s -X GET "http://localhost:8080/api/user/myPlants?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n=== 5. Создание растения через PlantREST ==="
curl -s -X POST http://localhost:8080/api/user/myPlants \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 100,
    "sampleId": 1,
    "name": "Тестовый цветок",
    "note": "Поливать раз в неделю",
    "species": "Rosa",
    "age": 2
  }' | jq .

echo -e "\n=== 6. Проверка RabbitMQ (сообщения в очереди) ==="
curl -s -u guest:guest -X POST \
  http://localhost:15672/api/queues/%2F/q.audit.events/get \
  -H "Content-Type: application/json" \
  -d '{"count":5,"ackmode":"ack_requeue_true"}' | jq '.[] | {eventType: .payload.metadata.eventType, description: .payload.payload}'

echo -e "\n=== Тестирование завершено! ==="