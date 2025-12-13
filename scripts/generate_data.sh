#!/bin/bash

echo "Generating test data..."
./gradlew bootRun --args="generate" 2>&1 | grep -E "(Generated|BUILD|ERROR)" | tail -5

