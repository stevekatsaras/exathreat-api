exathreat-api
-------------
An API that ingests syslog events from authenticated client "gateways" and stores them into an ElasticSearch container. The API performs several functions.

1. It requires a "gateway" to authenticate itself prior to forwarding syslog events to it. An example of an incoming Auth Request would be:

		{
			"apiKey": "baac41f2-6383-4ccd-a699-c8ef75dcac79"
		}

A faild authentication response would be:

		{
			"authenticated": false,
			"errorMsg": "A sample error message",
			"errorCause": [
				"A sample error outlining the cause of the error"
			],
			"errorCode": "error_code"
		}

A successful authentication response would be:

		{
			"authenticated": true,
			"orgCode": "abc123def456",
			"orgName": "My dummy Org"
		}

2. If authentication is successful, the API will now listen for incoming events and insert every event into ElasticSearch (against that client). An example of an incoming Event Request would be:

		{
			"apiKey": "baac41f2-6383-4ccd-a699-c8ef75dcac79",
			"orgCode": "abc123def456",
			"orgName": "My dummy Org",
			"events": [
				{
					"event": "my network or log message #1"
				},
				{
					"event": "my network or log message #2"
				},
				{
					"event": "my network or log message #n"
				}
			]
		}

To build your application:
*. ./gradlew clean build

To build your docker image: 
*. docker build -t exathreat-api .

To list your docker images:
*. docker image list | grep exathreat-api

To run your docker image (creates a new container):
*. docker run --name exathreat-api -e PROFILE=aws -e DB_ADDRESS=192.168.1.82 -e DB_PORT=3306 -e DB_NAME=exathreat -e DB_USERNAME=api -e DB_PASSWORD=39ZxjVui2xmd -e ES_DOMAIN=192.168.1.82 -e ES_PORT=9200 -e ES_SCHEME=http -e CACHE_TIMEOUT=60000 -p 8080:8080 exathreat-api

To get an existing container:
*. docker ps

To start an existing container (preserve data):
*. docker start <CONTAINER_NAME>

To stop a container:
*. docker stop <CONTAINER_NAME>

To remove your dangling images:
*. docker rmi -f $(docker images --filter dangling=true)

To authenticate docker to your AWS ECR registry:
*. aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com

---

To tag your docker image to your ECR repository in AWS:
*. docker tag exathreat-api 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-api

To push your tagged docker image to your ECR repository in AWS:
*. docker push 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-api

To pull your docker image from your ECR repository in AWS:
*. docker pull 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-api
