#!/usr/bin/env bash

BUILD_STATUS=()
TEST_STATUS=()

for PROJECT_URL in "${PROJECT_URLS[@]}"; do

 REPO_PATH="$GROUP_NAME/$(echo "$PROJECT_URL" | awk -F'/' '{print $NF}' | awk -F'.' '{print $1}')"

 if [ ! -d "$REPO_PATH" ]; then
   echo "git clone $REPO_PATH"
   git clone "$PROJECT_URL" "$REPO_PATH"
 else
   echo "git pull $REPO_PATH"
   (cd "$REPO_PATH" && git pull)
 fi

 START=$(date +%s%N)
 (cd "$REPO_PATH" && gradle clean build)
 CODE="$?"
 END=$(date +%s%N)
 DURATION="$((($END - $START)/1000000))"
 DURATION="$((($DURATION)/60000))m $((($DURATION)%60000/1000))s $((($DURATION)%1000))ms"

 if [ $CODE -eq 0 ]; then
   BUILD_STATUS+=("BUILD SUCCESS ($DURATION)")
 else
   BUILD_STATUS+=("BUILD FAILED ($DURATION)")
 fi

 START=$(date +%s%N)
 (cd "$REPO_PATH" && gradle test)
 CODE="$?"
 END=$(date +%s%N)
 DURATION="$((($END - $START)/1000000))"
 DURATION="$((($DURATION)/60000))m $((($DURATION)%60000/1000))s $((($DURATION)%1000))ms"

  if [ $CODE -eq 0 ]; then
    TEST_STATUS+=("TEST SUCCESS ($DURATION)")
  else
    TEST_STATUS+=("TEST FAILED ($DURATION)")
  fi
done

for i in "${!PROJECT_URLS[@]}"; do
 SERVICE_NAME="$(echo ${PROJECT_URLS[$i]} | awk -F'/' '{print $NF}' | awk -F'.' '{print $1}')"
 printf '%-40s%-30s%-30s\n' "$GROUP_NAME/$SERVICE_NAME" "${BUILD_STATUS[$i]}" "${TEST_STATUS[$i]}"
done