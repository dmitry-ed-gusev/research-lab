#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "Missing argument: group"
    exit 1
fi

if [ -z "$2" ]; then
    echo "Missing environment variable: GITLAB_TOKEN"
    exit 1
fi

GROUP_NAME="$1"
GITLAB_TOKEN="$2"
GROUP_ID=$(curl --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "https://git.severstal.severstalgroup.com/api/v4/groups?search=$GROUP_NAME" | jq '.[0].id')
PROJECT_URLS=($(curl -s --header "PRIVATE-TOKEN: $GITLAB_TOKEN" "https://git.severstal.severstalgroup.com/api/v4/groups/$GROUP_ID/projects?per_page=100&sort=asc&order_by=name" | jq '.[] | .ssh_url_to_repo' | sed 's/"//g'))

echo "$PROJECT_URLS"