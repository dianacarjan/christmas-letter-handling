#!/bin/bash

echo "Resource initialization started..."

region="us-east-1"

# Create SNS topic
topic_name="christmas-letter-creation"
awslocal sns create-topic --name ${topic_name} --region $region
echo "SNS topic '$topic_name' created successfully"

# Create SQS queues
dlq_name="dlq-christmas-letter"
awslocal sqs create-queue --queue-name $dlq_name --region $region
echo "DLQ '$dlq_name' created successfully"

queue_name="christmas-letter-processing"
awslocal sqs create-queue --queue-name $queue_name --region $region \
  --attributes '{
                  "RedrivePolicy": "{\"deadLetterTargetArn\": \"arn:aws:sqs:'$region':000000000000:'$dlq_name'\",\"maxReceiveCount\":\"2\"}",
                  "VisibilityTimeout": "10"
                }'

echo "SQS queue '$queue_name' created successfully"

# Create DynamoDB table
table_name="ChristmasLetters"
awslocal dynamodb create-table --table-name $table_name \
  --attribute-definitions \
      AttributeName=Email,AttributeType=S \
  --key-schema \
      AttributeName=Email,KeyType=HASH \
  --provisioned-throughput \
      ReadCapacityUnits=10,WriteCapacityUnits=5

# Subscribe SQS to SNS
awslocal sns subscribe --topic-arn "arn:aws:sns:$region:000000000000:$topic_name" --protocol sqs --notification-endpoint "arn:aws:sqs:$region:000000000000:$queue_name"
echo "Subscribed SQS queue '$queue_name' to SNS topic '$topic_name' successfully"

echo "Resource initialization complete!"

