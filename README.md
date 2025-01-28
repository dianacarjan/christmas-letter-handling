# christmas-letter-handling
Demo project for AWS SNS/SQS DynamoDB and REST for handling christmas letters
 
Use cases
 
1. Send Christmas letter: public API that receives a Christmas letter (sender’s email, name, wishes, location / coordinates (for present delivery), in JSON format, and publishes it to a SNS topic
 
2. Poll for letters: Santa listening to a SQS queue (subscribed to the SNS topic) for letters and stores them into a DB table (Dynamo)
 
3. Backend API: Santa can retrieve
   - a single letter by email
   - a list of letters – default 10 per page, but should support custom value
 
Challenge  #1
   - Install docker: some options are https://rancherdesktop.io/ or colima - but please don't use commercial / unlicensed software; should you have any doubts - ask
 
   - Use docker-compose to raise a localstack container that will host all the other aws components, for the first challenge you only need one SNS topic. 
       Some resources / hints:
           - https://docs.localstack.cloud/getting-started/installation/#docker-compose
           - https://whattodevnow.medium.com/using-localstack-with-docker-compose-to-mock-aws-services-bb25a5b01d4b
  
   - Implement the first REST endpoint that allows anyone to publish a letter to Santa. The purpose of this endpoint is to simply publish the JSON representation of the letter to the SNS topic.

 
Notes:
   - Write the code as if it were production code (clean code, handle negative scenarios, validations etc.)
   - Work on a feature branch, raise PRs for the main branch and submit them for review
   - Try to aim at having the highest coverage you can achieve with integration testing, and you can also use unit testing where it makes sense
