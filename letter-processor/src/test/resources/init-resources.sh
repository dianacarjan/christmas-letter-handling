#!/bin/bash

table_name="ChristmasLetters"

awslocal dynamodb create-table --table-name $table_name \
  --attribute-definitions \
      AttributeName=Email,AttributeType=S \
  --key-schema \
      AttributeName=Email,KeyType=HASH \
  --provisioned-throughput \
      ReadCapacityUnits=10,WriteCapacityUnits=5

sleep 2

topic_name="test-topic"
queue_name="test-queue"
dlq_name="test-dlq"

sns_arn_prefix="arn:aws:sns:us-east-1:000000000000"
sqs_arn_prefix="arn:aws:sqs:us-east-1:000000000000"

awslocal sqs create-queue --queue-name $dlq_name \
  --attributes '{"DelaySeconds": "0", "MessageRetentionPeriod": "259200", "VisibilityTimeout": "3"}'
echo "DLQ '$dlq_name' created successfully"

awslocal sqs create-queue --queue-name $queue_name \
  --attributes '{
                  "RedrivePolicy": "{\"deadLetterTargetArn\": \"arn:aws:sqs:us-east-1:000000000000:test-dlq\",\"maxReceiveCount\":\"2\"}",
                    "VisibilityTimeout": "3"
                }'

awslocal sns create-topic --name $topic_name
echo "SNS topic '$topic_name' created successfully"

awslocal sns subscribe --topic-arn "$sns_arn_prefix:$topic_name" --protocol sqs --notification-endpoint "$sqs_arn_prefix:$queue_name"
echo "Subscribed SQS queue '$queue_name' to SNS topic '$topic_name' successfully"

echo "Successfully initialized resources"
