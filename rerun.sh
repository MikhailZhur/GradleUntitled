#!/bin/sh
test_file="src/test/resources/FailedTests.txt"
if [ -f "$test_file" ]; then
  if [ -s "$test_file" ]; then
    ./gradlew clean test $(cat $test_file)
  else
      echo "test file is empty"
  fi
fi