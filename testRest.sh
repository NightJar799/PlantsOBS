#!/usr/bin/env bash

set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"
VERBOSE="${VERBOSE:-0}"
CURL_OPTS=("-s" "-w" "%{http_code}")
if [[ "$VERBOSE" == "1" ]]; then
    CURL_OPTS+=("-v")
fi

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_ok()   { echo -e "${GREEN}[OK]${NC} $1" >&2; }
print_fail() { echo -e "${RED}[FAIL]${NC} $1" >&2; }
print_info() { echo -e "${BLUE}[INFO]${NC} $1" >&2; }
print_warn() { echo -e "${YELLOW}[WARN]${NC} $1" >&2; }

do_request() {
    local method="$1"
    local url="$2"
    local body="$3"
    local expected="${4:-200}"
    local tmp_body=""
    local curl_args=("-X" "$method" "${CURL_OPTS[@]}")

    if [[ -n "$body" ]]; then
        curl_args+=("-H" "Content-Type: application/json" "-d" "$body")
        tmp_body=" (body: $body)"
    fi

    local response_file
    response_file=$(mktemp)
    http_code=$(curl "${curl_args[@]}" -o "$response_file" "$url" 2>/dev/null)
    body_content=$(cat "$response_file")
    rm -f "$response_file"

    if [[ "$http_code" =~ ^[0-9]{3}$ ]]; then
        if [[ "$http_code" == "$expected" ]]; then
            print_ok "$method $url → $http_code$tmp_body"
            RC=0
            echo "$body_content"
            return 0
        else
            print_fail "$method $url → expected $expected, got $http_code$tmp_body"
            echo "Response: $body_content" >&2
            RC=1
            return 1
        fi
    else
        print_fail "Curl error for $method $url"
        RC=2
        return 2
    fi
}

extract_id() {
    local json="$1"
    local id=""
    id=$(echo "$json" | jq -r '.id // empty')
    if [[ -z "$id" ]]; then
        id=$(echo "$json" | jq -r '._embedded.plantSamples[0].id // empty')
    fi
    if [[ -z "$id" ]]; then
        id=$(echo "$json" | jq -r '._embedded.homePlants[0].id // empty')
    fi
    if [[ -z "$id" ]]; then
        id=$(echo "$json" | jq -r '._embedded.robots[0].id // empty')
    fi
    echo "$id"
}

print_info "=== Health check ==="
do_request "POST" "$BASE_URL/api/admin/test" "" "200" > /dev/null
if [[ $RC -eq 0 ]]; then
    print_ok "Service is reachable"
else
    print_fail "Service not responding, aborting."
    exit 1
fi

print_info "\n=== Admin: Plant Samples ==="

SAMPLE_JSON='{
  "id": 20,
  "type": "Хвойное",
  "fruiting": "Да",
  "flower": "Нет",
  "difficulty": 3,
  "wikiUrl": "https://ru.wikipedia.org/wiki/Сосна"
}'
RESP=$(do_request "POST" "$BASE_URL/api/admin/samples" "$SAMPLE_JSON" "201")
SAMPLE_ID=$(extract_id "$RESP")
if [[ -n "$SAMPLE_ID" && "$SAMPLE_ID" != "null" ]]; then
    print_ok "Created sample with ID $SAMPLE_ID"
else
    print_fail "Failed to create sample, response: $RESP"
    exit 1
fi

do_request "GET" "$BASE_URL/api/admin/samples?page=0&size=20" "" "200" > /dev/null
do_request "GET" "$BASE_URL/api/admin/samples/$SAMPLE_ID" "" "200" > /dev/null

PUT_SAMPLE_JSON='{
  "id": '$SAMPLE_ID',
  "type": "Лиственное",
  "fruiting": "Нет",
  "flower": "Да",
  "difficulty": 5,
  "wikiUrl": "https://ru.wikipedia.org/wiki/Берёза"
}'
do_request "PUT" "$BASE_URL/api/admin/samples/$SAMPLE_ID" "$PUT_SAMPLE_JSON" "200" > /dev/null

PATCH_SAMPLE_JSON='{
  "id": '$SAMPLE_ID',
  "difficulty": 7
}'
do_request "PATCH" "$BASE_URL/api/admin/samples/$SAMPLE_ID" "$PATCH_SAMPLE_JSON" "200" > /dev/null

do_request "DELETE" "$BASE_URL/api/admin/samples/$SAMPLE_ID" "" "200" > /dev/null

RESP=$(do_request "POST" "$BASE_URL/api/admin/samples" "$SAMPLE_JSON" "201")
SAMPLE_ID=$(extract_id "$RESP")
print_ok "Re-created sample with ID $SAMPLE_ID for later use"

print_info "\n=== User: Home Plants ==="

HOME_PLANT_JSON='{
  "id": 1,
  "sampleId": '$SAMPLE_ID',
  "name": "Мой фикус",
  "note": "Поливать раз в неделю",
  "species": "Ficus elastica",
  "age": 2
}'
RESP=$(do_request "POST" "$BASE_URL/api/user/myPlants" "$HOME_PLANT_JSON" "201")
PLANT_ID=$(extract_id "$RESP")
if [[ -n "$PLANT_ID" && "$PLANT_ID" != "null" ]]; then
    print_ok "Created home plant with ID $PLANT_ID"
else
    print_fail "Failed to create home plant, response: $RESP"
    exit 1
fi

do_request "GET" "$BASE_URL/api/user/myPlants?page=0&size=20" "" "200" > /dev/null
do_request "GET" "$BASE_URL/api/user/myPlants/$PLANT_ID" "" "200" > /dev/null

# Новый тест: получение характеристик роста через /plantChar/{plantId}
# do_request "GET" "$BASE_URL/api/user/myPlants/plantChar/$PLANT_ID" "" "200" > /dev/null

PUT_PLANT_JSON='{
  "id": '$PLANT_ID',
  "sampleId": '$SAMPLE_ID',
  "name": "Обновлённый фикус",
  "note": "Полив раз в 3 дня",
  "species": "Ficus elastica robusta",
  "age": 3
}'
do_request "PUT" "$BASE_URL/api/user/myPlants/$PLANT_ID" "$PUT_PLANT_JSON" "200" > /dev/null

PATCH_PLANT_JSON='{
  "id": '$PLANT_ID',
  "name": "Фикус-патч"
}'
do_request "PATCH" "$BASE_URL/api/user/myPlants/$PLANT_ID" "$PATCH_PLANT_JSON" "200" > /dev/null

print_info "\n=== User: Search plant samples ==="
ENCODED_TYPE=$(printf '%s' "Хвойное" | jq -sRr @uri)
do_request "GET" "$BASE_URL/api/user/myPlants/search?type=$ENCODED_TYPE&page=0&size=10" "" "200" > /dev/null

print_info "\n=== User: Robot management ==="

ROBOT_JSON='{
  "id": 1,
  "plantId": '$PLANT_ID',
  "name": "Датчик температуры",
  "sensorType": 1,
  "measuredCharacteristic": "Температура",
  "usedCharacteristic": "Температура воздуха"
}'
RESP=$(do_request "POST" "$BASE_URL/api/user/myPlants/$PLANT_ID/robots" "$ROBOT_JSON" "201")
ROBOT_ID=$(extract_id "$RESP")
if [[ -n "$ROBOT_ID" && "$ROBOT_ID" != "null" ]]; then
    print_ok "Created robot with ID $ROBOT_ID for plant $PLANT_ID"
else
    print_fail "Failed to create robot, response: $RESP"
    exit 1
fi

PUT_ROBOT_JSON='{
  "id": '$ROBOT_ID',
  "plantId": '$PLANT_ID',
  "name": "Датчик влажности",
  "sensorType": 2,
  "measuredCharacteristic": "Влажность",
  "usedCharacteristic": "Влажность почвы"
}'
do_request "PUT" "$BASE_URL/api/user/myPlants/$PLANT_ID/robots/$ROBOT_ID" "$PUT_ROBOT_JSON" "200" > /dev/null

PATCH_ROBOT_JSON='{
  "id": '$ROBOT_ID',
  "name": "Датчик влажности (обновлён)"
}'
do_request "PATCH" "$BASE_URL/api/user/myPlants/$PLANT_ID/robots/$ROBOT_ID" "$PATCH_ROBOT_JSON" "200" > /dev/null

print_info "\n=== Plant Manager ==="

REPORT_JSON='{
  "id": '$PLANT_ID',
  "lx": 5000,
  "water": 80,
  "heat": 22,
  "air": 400,
  "nitrogen": 30,
  "soilPh": 6.5,
  "humidity": "50%"
}'
do_request "POST" "$BASE_URL/api/plants/$PLANT_ID/report" "$REPORT_JSON" "201" > /dev/null
do_request "GET" "$BASE_URL/api/plants/$PLANT_ID/environment" "" "200" > /dev/null
do_request "GET" "$BASE_URL/api/plants/$PLANT_ID/recommendations" "" "202" > /dev/null

print_info "\n=== Robot API ==="
do_request "GET" "$BASE_URL/api/robot/$ROBOT_ID" "" "200" > /dev/null
do_request "POST" "$BASE_URL/api/robot/$PLANT_ID/$ROBOT_ID" "" "202" > /dev/null || print_warn "Robot send data may fail if no growth char linked"

print_info "\n=== Clean up ==="
do_request "DELETE" "$BASE_URL/api/user/myPlants/$PLANT_ID/robots/$ROBOT_ID" "" "204" > /dev/null
do_request "DELETE" "$BASE_URL/api/user/myPlants/$PLANT_ID" "" "204" > /dev/null
do_request "DELETE" "$BASE_URL/api/admin/samples/$SAMPLE_ID" "" "200" > /dev/null

print_info "\nAll tests completed successfully!"