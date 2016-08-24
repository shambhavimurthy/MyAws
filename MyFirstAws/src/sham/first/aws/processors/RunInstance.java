package sham.first.aws.processors;

/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;

public class RunInstance {

	static AmazonEC2 ec2;

	private static void init() throws Exception {

		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location , and is in valid format.", e);
		}
		ec2 = new AmazonEC2Client(credentials);
		ec2.setEndpoint("https://ec2.ap-south-1.amazonaws.com");
	}

	public static void main(String[] args) throws Exception {

		System.out.println("===========================================");
		System.out.println("Welcome to the AWS Java SDK!");
		System.out.println("===========================================");

		init();

		try {

			DescribeAvailabilityZonesResult availabilityZonesResult = ec2.describeAvailabilityZones();
			System.out.println("You have access to " + availabilityZonesResult.getAvailabilityZones().size()
					+ " Availability Zones.");
			
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Please Enter the Endpoint : ");
			String endpoint = sc.next();
			ec2.setEndpoint(endpoint);
			
			RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
			System.out.println("Please Enter the ImageId : ");
			String imageId = sc.next();
			runInstancesRequest.withImageId(imageId);
			System.out.println("Please Enter the ImageType : ");
			String imageType = sc.next();
			runInstancesRequest.withInstanceType(imageType);
			runInstancesRequest.withMinCount(1);
			runInstancesRequest.withMaxCount(1);
			System.out.println("Please Enter the KeyName : ");
			String keyName = sc.next();
			runInstancesRequest.withKeyName(keyName);
			System.out.println("Please Enter the SecurityGroupId : ");
			String securityGroupId = sc.next();
			runInstancesRequest.withSecurityGroupIds(securityGroupId);
			ec2.runInstances(runInstancesRequest);
			
			sc.close();

			DescribeInstancesResult describeInstancesRequest = ec2.describeInstances();
			List<Reservation> reservations = describeInstancesRequest.getReservations();
			Set<Instance> instances = new HashSet<Instance>();

			for (Reservation reservation : reservations) {
				instances.addAll(reservation.getInstances());
			}

			for (Instance instance : instances) {
				System.out.println("You have the instance with instance id : " + instance.getInstanceId() + " running");
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Caught Exception: " + ase.getMessage());
			System.out.println("Reponse Status Code: " + ase.getStatusCode());
			System.out.println("Error Code: " + ase.getErrorCode());
			System.out.println("Request ID: " + ase.getRequestId());
		}

	}
}
