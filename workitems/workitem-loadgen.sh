#!/usr/bin/env bash

# Configuration
export WP_WORKITEMS_SERVER=localhost:8084
export BASE="http://$WP_WORKITEMS_SERVER/api/v1/workitems"

# Optional auth: set TOKEN in your environment to enable Authorization header
AUTH_HEADER=()
if [[ -n "${TOKEN:-}" ]]; then
  AUTH_HEADER=(-H "Authorization: Bearer $TOKEN")
fi

# Dependency check
if ! command -v jq >/dev/null 2>&1; then
  echo "jq is required but not installed. Please install jq and re-run." >&2
  exit 1
fi

# Curl wrapper: logs status/time to stderr, returns body on stdout
curl_json() {
  local method="$1"
  local url="$2"
  local data="$3"
  local cid="$4"
  local outfile
  outfile="$(mktemp)"

  if [[ -n "$data" ]]; then
    curl -sS -X "$method" "$url" \
      -H "User-Agent: workitems-loadgen/1.0" \
      -H "X-Request-Id: $cid" \
      "${AUTH_HEADER[@]}" \
      -H "Content-Type: application/json" \
      --connect-timeout 5 -m 10 \
      -d "$data" \
      -o "$outfile" \
      -w "method=$method url=$url status=%{http_code} time=%{time_total} bytes=%{size_download} req_id=$cid\n" 1>&2
  else
    curl -sS -X "$method" "$url" \
      -H "User-Agent: workitems-loadgen/1.0" \
      -H "X-Request-Id: $cid" \
      "${AUTH_HEADER[@]}" \
      --connect-timeout 5 -m 10 \
      -o "$outfile" \
      -w "method=$method url=$url status=%{http_code} time=%{time_total} bytes=%{size_download} req_id=$cid\n" 1>&2
  fi

  cat "$outfile"
  rm -f "$outfile"
}

ITER=0
while true; do
  ((ITER++))

  # Extra list call (traffic + health check)
  CID_LIST1="$(date +%s%N)"
  curl_json GET "$BASE" "" "$CID_LIST1" >/dev/null

  # Actual list (we need the body)
  CID_LIST2="$(date +%s%N)"
  RESP="$(curl_json GET "$BASE" "" "$CID_LIST2")"

  # Guard for short lists
  COUNT="$(echo "$RESP" | jq '.content | length')"
  if [[ -z "$COUNT" || "$COUNT" -lt 3 ]]; then
    echo "iter=$ITER insufficient items ($COUNT); will POST and retry next iteration" >&2
    # Create one random item to help grow the list
    PTS=$((RANDOM % 100))
    COST="$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("%.3f", rand()) }')"
    if ((RANDOM % 2)); then BLOCKED=true; else BLOCKED=false; fi

    POST_DATA="$(cat <<JSON
{
  "name": "New work item $(date +%s%N)",
  "description": "Created via curl - iteration $ITER",
  "country": "Country$RANDOM",
  "city": "City$RANDOM",
  "address": "Street $RANDOM",
  "assignee": "Assignee$RANDOM",
  "points": $PTS,
  "cost": $COST,
  "blocked": $BLOCKED
}
JSON
)"
    CID_POST_BOOTSTRAP="$(date +%s%N)"
    curl_json POST "$BASE" "$POST_DATA" "$CID_POST_BOOTSTRAP" >/dev/null

    sleep 0.5
    continue
  fi

  # Random, distinct indices for GET, PUT, DELETE
  if command -v shuf >/dev/null 2>&1; then
    read -r IDX_GET IDX_PUT IDX_DELETE <<< "$(shuf -i 0-$((COUNT-1)) -n 3 | tr '\n' ' ')"
  else
    IDX_GET=$((RANDOM % COUNT))
    while :; do
      IDX_PUT=$((RANDOM % COUNT))
      [[ $IDX_PUT -ne $IDX_GET ]] && break
    done
    while :; do
      IDX_DELETE=$((RANDOM % COUNT))
      [[ $IDX_DELETE -ne $IDX_GET && $IDX_DELETE -ne $IDX_PUT ]] && break
    done
  fi

  GET_ID="$(echo "$RESP"    | jq -r ".content[$IDX_GET].id")"
  PUT_ID="$(echo "$RESP"    | jq -r ".content[$IDX_PUT].id")"
  DELETE_ID="$(echo "$RESP" | jq -r ".content[$IDX_DELETE].id")"

  # Validate IDs
  if [[ -z "$GET_ID" || "$GET_ID" == "null" || -z "$PUT_ID" || "$PUT_ID" == "null" || -z "$DELETE_ID" || "$DELETE_ID" == "null" ]]; then
    echo "iter=$ITER could not extract valid IDs; skipping iteration" >&2
    sleep 0.5
    continue
  fi

  echo "iter=$ITER idx_get=$IDX_GET idx_put=$IDX_PUT idx_delete=$IDX_DELETE GET_ID=$GET_ID PUT_ID=$PUT_ID DELETE_ID=$DELETE_ID" >&2

  # Randomize POST body
  PTS=$((RANDOM % 100))
  COST="$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("%.3f", rand()) }')"
  if ((RANDOM % 2)); then BLOCKED=true; else BLOCKED=false; fi

  POST_DATA="$(cat <<JSON
{
  "name": "New work item $(date +%s%N)",
  "description": "Created via curl - iteration $ITER",
  "country": "Country$RANDOM",
  "city": "City$RANDOM",
  "address": "Street $RANDOM",
  "assignee": "Assignee$RANDOM",
  "points": $PTS,
  "cost": $COST,
  "blocked": $BLOCKED
}
JSON
)"
  CID_POST="$(date +%s%N)"
  curl_json POST "$BASE" "$POST_DATA" "$CID_POST" >/dev/null

  # GET one work item by ID
  CID_GET="$(date +%s%N)"
  curl_json GET "$BASE/$GET_ID" "" "$CID_GET" >/dev/null
  # GET a non-existing item
  curl_json GET "$BASE/0" "" "$CID_GET" >/dev/null

  # Randomize PUT body
  PTS2=$((RANDOM % 100))
  COST2="$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("%.3f", rand()) }')"
  if [[ "$BLOCKED" == "true" ]]; then BLOCKED2=false; else BLOCKED2=true; fi

  PUT_DATA="$(cat <<JSON
{
  "id": $PUT_ID,
  "name": "Updated work item name $ITER",
  "description": "Updated via PUT at $(date -Is)",
  "country": "Country$RANDOM",
  "city": "City$RANDOM",
  "address": "Addr $RANDOM",
  "assignee": "AssigneeUpdated$RANDOM",
  "points": $PTS2,
  "cost": $COST2,
  "blocked": $BLOCKED2
}
JSON
)"
  CID_PUT="$(date +%s%N)"
  curl_json PUT "$BASE/$PUT_ID" "$PUT_DATA" "$CID_PUT" >/dev/null

  # PUT a non-existing item
  curl_json PUT "$BASE/0" "$PUT_DATA" "$CID_PUT" >/dev/null

  # DELETE one work item by ID
  CID_DELETE="$(date +%s%N)"
  curl_json DELETE "$BASE/$DELETE_ID" "" "$CID_DELETE" >/dev/null

  # DELETE a non-existing item
  curl_json DELETE "$BASE/0" "" "$CID_DELETE" >/dev/null

  # Small jittered pause (0.5s to 1.5s)
  SLEEP_SEC="$(awk -v seed="$RANDOM" 'BEGIN { srand(seed); printf("%.2f", 0.5 + rand()) }')"
  sleep "$SLEEP_SEC"
done
