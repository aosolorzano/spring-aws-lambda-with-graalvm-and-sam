## Hiperium City Data Lambda Function

This project contains source code and supports files for a serverless application that you can deploy with the SAM CLI.
It includes the following files and folders.

- **src/main** - Code for the application's Lambda function.
- **src/test** - Unit/Integration tests for the application code.

The application uses AWS resources like Lambda and DynamoDB.
These resources are defined in the `template.yaml` file in the parent project.

### GraalVM
This Lambda Function uses GraalVM to create a native binary of the Java application.
By compiling Java to a native Linux executable, the performance is increased and cold-start is reduced.

This Lambda Function contains build configurations for Maven build systems (see Makefile in the project's root directory).

A docker image is required to generate the Native Linux Executable for the Lambda execution environment.
```bash
docker pull hiperium/native-image-builder:latest
```

---
## Running Integration tests using Testcontainers.
Tests are defined in the `functions/city-data-function/src/test` folder.
Execute the following command to run the tests from the project's root directory:
```bash
./mvnw test -f functions/city-events-function/pom.xml
```

---
## Deploying Lambda Function using Spring Boot Docker Compose
Start the main class from the IDE to run the Lambda Function. 
The project uses the "local" profile as default,
so this profile configures the Docker Compose deployment for the LocalStack environment:

```properties
spring.docker.compose.enabled=true
spring.docker.compose.start.log-level=debug
spring.docker.compose.lifecycle-management=start_and_stop
spring.docker.compose.file=functions/city-data-function/tools/docker/compose.yaml
```

Now, you can invoke the Lambda Function using the following command:
```bash
curl -H "Content-Type: application/json" "http://localhost:8080/findById" \
  -d @functions/city-data-function/src/test/resources/requests/lambda-valid-id-request.json
```

Recall this is used only for local development and testing purposes.
The other Spring profiles don't have this feature enabled.

---
## Deploying Lambda Function using Docker Compose
The following command will deploy the Lambda Function and LocalStack using Docker Compose:
```bash
docker compose up --build
```

### Invoke Lambda Function Locally using CURL
The following command will invoke the Lambda Function using CURL:
```bash
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" \
  -d @functions/city-data-function/src/test/resources/requests/lambda-valid-id-request.json
```

### Getting records from DynamoDB on LocalStack.
Execute the following command to get the created records in DynamoDB:
```bash
awslocal dynamodb scan --table-name Cities
```

---
## Deploy the Lambda Function with SAM CLI.

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications.
It uses Docker to run your functions in an Amazon Linux environment that matches your Lambda infrastructure in AWS.
It can also emulate your application's build environment and API.

To build the SAM package, you must use the following command:
```bash
sam build --config-env 'dev'                \
  --template-file 'functions/template.yaml' \
  --profile 'city-dev'
```

To deploy your application for the first time, run the following in your shell:
```bash
sam deploy                                    \
  --config-env 'dev'                          \
  --template-file 'functions/template.yaml'   \
  --disable-rollback                          \
  --profile 'city-dev'
```

The previous command will package and deploy your application to AWS.

### Fetch, tail, and filter Lambda function logs

To simplify troubleshooting, SAM CLI has a command called `sam logs`.
This command lets you fetch logs generated by your deployed Lambda function from the command line.
In addition to printing the logs on the terminal, this command has several nifty features to help you quickly find the bug.

`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
sam logs -n "CityDataFunction"            \
  --stack-name "city-data-function-dev"   \
  --tail                                  \
  --profile "city-dev"
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

### Invoke Lambda Function deployed in AWS
To invoke the Lambda Function deployed in AWS, use the following command:
```bash
aws lambda invoke --function-name 'city-data-function' \
  --payload file://functions/city-data-function/src/test/resources/requests/lambda-valid-id-request.json \
  --cli-binary-format raw-in-base64-out \
  --profile 'city-dev' response.json
```

You must receive a response from the Lambda Function in the `response.json` file:
```bash
cat response.json | jq
```

### SAM-CLI and APIs (not currently implemented).

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.
```bash
sam local start-api
curl http://localhost:3000/
```

The SAM CLI reads the application template to determine the API's routes and the functions that they invoke.
The `Events` property on each function's definition includes the route and method for each path.
```yaml
Events:
    CitiesApi:
        Type: Api
        Properties:
            Path: /logs
            Method: get
```

### Add a resource to your application

The application template uses AWS Serverless Application Model (AWS SAM) to define application resources.
AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs.
For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

### Cleanup

To delete the Lambda Function you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:
```bash
sam delete --stack-name 'city-data-function' --profile 'city-dev'
```

### Resources

See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready-to-use Apps and learn how authors developed their applications:
[AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/).
