#!/usr/bin/env bash
export WP_WORKITEMS_SERVER=localhost:8084
export BASE="http://$WP_WORKITEMS_SERVER/api/v1/workitems"

while true; do
  # Extra list call for more traffic (optional)
  curl -sS "$BASE" >/dev/null

  RESP=$(curl -sS "$BASE")

  # Pick three IDs from the list (first three items)
  GET_ID=$(echo "$RESP"    | jq -r '.content[0].id')
  PUT_ID=$(echo "$RESP"    | jq -r '.content[1].id')
  DELETE_ID=$(echo "$RESP" | jq -r '.content[2].id')

  if [[ "$GET_ID" == "null" || "$PUT_ID" == "null" || "$DELETE_ID" == "null" ]]; then
    echo "Fewer than 3 items available; skipping iteration"
    sleep 1
    continue
  fi


  echo "GET_ID=$GET_ID"
  echo "PUT_ID=$PUT_ID"
  echo "DELETE_ID=$DELETE_ID"

  # Creating a new object
  curl -sS -X POST "$BASE" \
    -H "Content-Type: application/json" \
    -d '{
      "name": "New work item",
      "description": "Created via curl",
      "country": "Country123",
      "city": "CityABC",
      "address": "123 Main Street",
      "assignee": "Assignee0001",
      "points": 8,
      "cost": 0.42,
      "blocked": false
    }' >/dev/null

  curl -sS "$BASE/$GET_ID" >/dev/null

  cat <<EOF | curl -sS -X PUT "$BASE/$PUT_ID" \
    -H "Content-Type: application/json" \
    -d @- >/dev/null
{
  "id": $PUT_ID,
  "name": "Updated work item name",
  "description": "Updated description via PUT",
  "country": "CountryXYZ",
  "city": "CityDEF",
  "address": "456 Updated Ave",
  "assignee": "AssigneeUpdated",
  "points": 13,
  "cost": 0.99,
  "blocked": true
}
EOF

  curl -sS -X DELETE "$BASE/$DELETE_ID" >/dev/null

  # Small pause to keep load realistic and easier to observe
  sleep 1
done
