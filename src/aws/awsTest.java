package aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.binary.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
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
		String amiId;
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
				System.out.println("Enter ami id : ");
				amiId=id_string.next();
				createInstances(amiId);
				break;
			case 7:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				rebootInstances(instanceId);
				break;
			case 8:
				listImages();
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
			System.out.print("[id]  " + zone.getZoneId()+
					",  [region]  " + zone.getRegionName()+
					",  [zone]  "+ zone.getZoneName());

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
			System.out.print("[region]"+ getLPad(region.getRegionName(),16,  " ")+ ", [endpoint] "+region.getEndpoint());
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

	public static void createInstances(String ami_id) {

		RunInstancesRequest run_request = new RunInstancesRequest()
				.withImageId(ami_id)
				.withInstanceType(InstanceType.T2Micro)
				.withMaxCount(1)
				.withMinCount(1)
				.withKeyName("awskey");


		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		System.out.print("Successfully started EC2 instance "+ reservation_id +" based on AMI "+ami_id);

	}

	public static void rebootInstances(String instance_id) {



		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);

		RebootInstancesResult response = ec2.rebootInstances(request);

		System.out.printf("Successfully reboot instance %s", instance_id);


	}


	public static void listImages() {
		
		System.out.println("Listing Images....");
		DescribeImagesRequest request = new DescribeImagesRequest();
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		filter.setName("is-public");

		List<String> values = new ArrayList<>();
		values.add("false");
		filter.setValues(values);
		filters.add(filter);
		request.setFilters(filters);


		DescribeImagesResult result = ec2.describeImages(request);
		List<Image> images = result.getImages();



		for(int i=0;i<images.size();i++) {
			System.out.print("[ImageID] " + images.get(i).getImageId()+
					", [Name] "+images.get(i).getName()+
					", [Owner] "+images.get(i).getOwnerId());


		}



	}

} 