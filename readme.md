# Citrus aws S3 plugin

## 1. Introduction

This is an introductory article on support library to Citrus framework, thanks to which we can create end-
to-end tests for S3 service.
First, we’ll show how to create an endpoint for our S3 service and then we’ll focus on creating tests for
each of request type.

## 2. Overview

Citrus is the java framework for creating end-to-end tests for integration. For it to run we need TestNG and
Citrus core dependencies. All required dependencies are included in Release11 library.

Maven
```xml
<groupId>com.release11</groupId>
<artifactId>citrus-s3</artifactId>
<version>1.0.0</version>
```

There is no such item in mvnrepository.com, so first we need to clone the content of repository
https://phabricator.release11.com/diffusion/CITRUSSTHREE/ and install it into local maven repo using

command:
```
mvn install
```

Once it’s done we will be able to add dependency to our pom.xml

## 3. Citrus syntax

Before we start working with S3 plugin we need to understand basics of Citrus syntax. In order to create a
test we need to create a class that extends TestNGCitrusTestRunner preferably with testng @Test
annotation.

```java
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import org.testng.annotations.Test;

@Test
public class CitrusSyntaxExample extends TestNGCitrusTestRunner {
}
```

Once that’s out of the way we can start writing or own test cases. Each test performed with Citrus requires @CitrusTest annotation.

```java
@CitrusTest
public void testCitrusS3Example(){
}
```

Great! Now we have foundation for testing S3 service!

## 4. S3Endpoint

In order to communicate with our S3 service we will be required to feed application with our endpoint data.
S3Endpoint object requires region, access key and secret key in order to operate. If we are using localstack
we may also want to precise the url of service, otherwise the aws S3 service will be queried.

```java
public S3Endpoint s3Endpoint() {
	return S3Endpoint.builder()
		.endpointUri("http://localhost:4566")
		.region("EU_NORTH_1")
		.accessKey("MyAccessKey")
		.secretKey("MyS3cr3tK3y")
		.build();
}
```

To construct the S3Endpoint we use S3EndpointBuilder instance. Preferably we would use credentials or
properties for this type of data, but for sake of explanation the data in examples is given directly.

## 5. S3Message

Having S3Endpont initialized we can send requests to S3 service and receive response. In order to do so
we must construct S3Message object. We can do that by initializng S3MessageBuilder with type of request
we want to perfom, destination bucket and the key – which is basically file path inside the bucket.

```java
S3Message helloMessage = S3Message.builder()
	.method(S3RequestType.PUT)
	.bucket("MyBucket")
	.key("FirstCitrusTest/HelloWorld.txt")
	.build();
```

The role of the object is to contain data about the destination. Content is held in payload field initialized by
CitrusTest.

## 6. Send request

In order to place file into a bucket we need to specify what file or message we want to send and optionaly
verify if the operation was a success, by checking the response.

```java
@Test
public class CitrusSyntaxExample extends TestNGCitrusTestRunner {
	@Autowired
	S3Endpoint s3Endpoint;
	
	@CitrusTest
	public void testSendMessageOperation(){
		S3Message helloMessage = S3Message.builder()
			.method(S3RequestType.PUT)
			.bucket("MyBucket")
			.key("FirstCitrusTest/HelloCitrus.txt")
			.build();
		send(builder -> builder.endpoint(s3Endpoint)
			.message(helloMessage)
			.payload("Hello Citrus!")
			.build()
		);
		receive(builder -> builder.endpoint(s3Endpoint)
			.payload(S3EndpointResponse.PUT_OBJECT_SUCCESS)
		);
	}
}
```

In the example we placed file HelloCitrus.txt into catalog FirstCitrusTest on bucket MyBucket using send()
operation. Next in receive() operation we are expecting that our request met proper response.

## 7. Get file request

We may also want to download specific file and verify its content via Citrus. In order to do so, we have to
specify method with S3RequestType.GET. To do so we will have to create another S3Message.

```java
@Test
public class CitrusSyntaxExample extends TestNGCitrusTestRunner {
	@Autowired
	S3Endpoint s3Endpoint;
	
	@CitrusTest
	public void testGetMessageOperation(){
		//Get file
		S3Message helloS3Message = S3Message.builder()
			.method(S3RequestType.GET)
			.bucket("MyBucket")
			.key("FirstCitrusTest/HelloCitrus.txt")
			.build();
		send(builder -> builder.endpoint(s3Endpoint)
			.message(helloS3Message)
			.build()
		);
		receive(builder -> builder.endpoint(s3Endpoint)
			.payload("Hello Citrus!")
		);
	}
}
```

In this example we downloaded the file from the service and verified its content in receive() operation.

## 8. Delete file request

There might be an instance where we want to delete file in S3 bucket. In order to do so we have to specify
method with S3RequestType.DELETE.

```java
@Test
public class CitrusSyntaxExample extends TestNGCitrusTestRunner {
	@Autowired
	S3Endpoint s3Endpoint;
	
	@CitrusTest
	public void testGetMessageOperation(){
		//Get file
		S3Message helloS3Message = S3Message.builder()
			.method(S3RequestType.DELETE)
			.bucket("MyBucket")
			.key("FirstCitrusTest/HelloCitrus.txt")
			.build();
		send(builder -> builder.endpoint(s3Endpoint)
			.message(helloS3Message)
			.build()
		);
		receive(builder -> builder.endpoint(s3Endpoint)
			.payload(S3EndpointResponse.DELETE_OBJECT_SUCCESS)
		);
	}
}
```

After that all we have to do is to validate response same like in previous example.
