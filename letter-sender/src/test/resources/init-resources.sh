#!/bin/bash

topic_name="test-topic"

awslocal sns create-topic --name $topic_name
echo "SNS topic '$topic_name' created successfully"

echo "Successfully initialized resources"
