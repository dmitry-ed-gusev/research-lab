#!/usr/bin/env bash

# -- test data for mocking Abstract UI
RIVER_URL="https://private-7843a-enigmariver.apiary-mock.com"
RIVER_API_KEY="y+gfF6LsXQSXjJBAKiMx1XNNqVo+UKeV5L3egLkJijE="

# -- mock data
RIVER_ANSWER_COLLECTION="{
  \"created_at\": \"2015-03-24T18:14:30.964962\",
  \"created_by_user_id\": \"cd6cca2c-a43a-4b31-a331-f352b3f0390f\",
  \"modified_at\": \"2015-03-24T18:14:30.964962\",
  \"id\": \"977b930f-86e3-4a4d-a781-2b254013fe9b\",
  \"name\": \"com.citibikenyc\"
}"

# -- call to Abstract UI (River)
function river() {
  curl -v -H "Content-Type: application/json" -H "Authorization: Bearer $RIVER_API_KEY" -H "Accept: application/json" "$@"
}

# -- creates collection in Abstract
function create_collection() {
  if [ "$#" != 2 ]; then
    echo "Usage: create_collection COLLECTION_ID_VAR COLLECTION_NAME"
    echo "Stores ID of newly created collection into COLLECTION_ID_VAR"
    exit 1
  fi

  local _COLLECTION_ID_VAR="$1"
  local _COLLECTION_NAME="$2"

  mkdir -p tmp

  # -- write name for new collection into tmp file
  cat > tmp/new_collection <<EOF
{
  "name": "$_COLLECTION_NAME"
}
EOF
  # -- call to Abstract and create a collection
  river -X POST -d @tmp/new_collection "$RIVER_URL/v2/collections/" > tmp/collection
  local _COLLECTION_ID="$(jq -r .id < tmp/collection)"
  echo "Created collection: ${_COLLECTION_NAME} -> ${_COLLECTION_ID}"

  # -- export variable with created collection name
  export ${_COLLECTION_ID_VAR}="${_COLLECTION_ID}"
}