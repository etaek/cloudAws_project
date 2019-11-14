package aws;

import java.util.Scanner;

import org.apache.commons.codec.binary.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;

import com.amazonaws.services.ec2.model.Reservation;

import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;

public class awsTest {

	static AmazonEC2 ec2;



	private static void init() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);

		}
		ec2 = AmazonEC2ClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion("us-east-1") /* check the region at AWS console */
				.build();
	}

	public static void main(String[] args) throws Exception {
		init();
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		boolean exit=false;
		String instanceId;
		while(true)
		{
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance 2. available zones ");
			System.out.println(" 3. start instance 4. available regions ");
			System.out.println(" 5. stop instance 6. create instance ");
			System.out.println(" 7. reboot instance 8. list images ");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");

			number = menu.nextInt();

			switch(number) {
			case 1 :
				listInstances();
				break;

			case 2:
				availableZones();
				break;
			case 3:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				startInstances(instanceId);
				break;
			case 4:
				availableRegions();
				break;
			case 5:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				stopInstances(instanceId);
				break;
			case 6:
				break;
			case 7:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				rebootInstances(instanceId);
				break;
			case 8:
				break;
			case 99:
				exit=true;
				break;
			default :
				break;
			}
			if(exit==true) {
				System.out.println();
				System.out.println("End Program!");
				break;
			}
		}

	}
	public static void listInstances(){
		System.out.println("Listing instances....");
		boolean done = false;
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);
			for(Reservation reservation : response.getReservations()) {
				for(Instance instance : reservation.getInstances()) {
					System.out.printf(
							"[id] %s, " +
									"[AMI] %s, " +
									"[type] %s, " +
									"[state] %10s, " +
									"[monitoring state] %s",
									instance.getInstanceId(),
									instance.getImageId(),
									instance.getInstanceType(),
									instance.getState().getName(),
									instance.getMonitoring().getState());
				}
				System.out.println();
			}
			request.setNextToken(response.getNextToken());

			if(response.getNextToken() == null) {
				done = true;
			}

		}

	}

	public static void availableZones(){
		int count=0;
		DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

		for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
			System.out.printf(
					"[id]  %s,  " +
							"[region]   %s,  " +
							"[zone]   %s  ",
							zone.getZoneId(),
							zone.getRegionName(),
							zone.getZoneName());
			System.out.println();
			count++;
		}
		System.out.println("\nYou have access to "+count+" Availability Zones.");
	}

	public static void startInstances(String instance_id) {


		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

		ec2.startInstances(request);

		System.out.printf("Successfully started instance %s", instance_id);




	}

	public static void availableRegions(){

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for(Region region : regions_response.getRegions()) {
			System.out.printf("[region]"+ getLPad(region.getRegionName(),16,  " ")+
							  ", [endpoint] "+region.getEndpoint());
	
			System.out.println();
		}

	}
	
	public static String getLPad(String str, int size, String strFillText) {  // Fill string blanks
		for(int i = (str.getBytes()).length; i < size; i++) {
			str = strFillText + str;
		}
		return str;
	}

	public static void stopInstances(String instance_id) {



		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);

		ec2.stopInstances(request);

		System.out.printf("Successfully stop instance %s", instance_id);

	}

	public static void rebootInstances(String instance_id) {



		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);

		RebootInstancesResult response = ec2.rebootInstances(request);
		
		System.out.printf("Successfully reboot instance %s", instance_id);


	}
} 