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
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Region;

public class awsTest {

	static AmazonEC2 ec2;

	static boolean error=false;

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
		String keypair;
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
			System.out.println(" 1. list instance   2. available zones ");
			System.out.println(" 3. start instance  4. available regions ");
			System.out.println(" 5. stop instance   6. create instance ");
			System.out.println(" 7. reboot instance 8. delete instance ");
			System.out.println(" 9. list images     10. list keypair ");
			System.out.println(" 11. create keypair 12. delete keypair ");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer: ");

			number = menu.nextInt();
			error=false;
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
				System.out.println("Enter keypair name : ");
				keypair=id_string.next();
				createInstances(amiId,keypair);
				break;
			case 7:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				rebootInstances(instanceId);
				break;
			case 8:
				System.out.println("Enter instance id : ");
				instanceId=id_string.next();
				deleteInstances(instanceId);
				break;
			case 9:
				listImages();
				break;
			case 10:
				listKeyPairs();
				break;
			case 11:
				createKeyPairs(); 
				break;
			case 12:
				System.out.println("Enter keypair name : ");
				keypair=id_string.next();
				deleteKeyPairs(keypair);
				break;
			case 99:
				exit=true;
				break;
			default :
				System.out.println("Wrong number!");
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
			DescribeInstancesResult result = ec2.describeInstances(request);
			for(Reservation reservation : result.getReservations()) {
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
			request.setNextToken(result.getNextToken());

			if(result.getNextToken() == null) {
				done = true;
			}

		}

	}

	public static void availableZones(){
		int count=0;
		DescribeAvailabilityZonesResult result = ec2.describeAvailabilityZones();

		for(AvailabilityZone zone : result.getAvailabilityZones()) {
			System.out.println("[id]  " + zone.getZoneId()+
					",  [region]  " + zone.getRegionName()+
					",  [zone]  "+ zone.getZoneName());

			count++;
		}
		System.out.println("\nYou have access to "+count+" Availability Zones.");
	}

	public static void startInstances(String instance_id) {

		System.out.println("Starting Instance.... "+instance_id);

		try {
			StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);
			ec2.startInstances(request);
		}catch(Exception e) {
			System.out.println("ERROR : Nonexistent Instance!");
			error=true;
		}

		if(error!=true)
			System.out.println("Successfully started instance "+ instance_id);




	}

	public static void availableRegions(){

		DescribeRegionsResult result = ec2.describeRegions();

		for(Region region : result.getRegions()) {
			System.out.println("[region]"+ getLPad(region.getRegionName(),16,  " ")+ ", [endpoint] "+region.getEndpoint());
		}

	}

	public static String getLPad(String str, int size, String strFillText) {  // Fill string blanks
		for(int i = (str.getBytes()).length; i < size; i++) {
			str = strFillText + str;
		}
		return str;
	}

	public static void stopInstances(String instance_id) {


		System.out.println("Stopping Instance.... "+instance_id);


		try {
			StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instance_id);
			StopInstancesResult result = ec2.stopInstances(request);
		}catch(Exception e) {
			System.out.println("ERROR : Nonexistent Instance!");
			error=true;
		}

		if(error!=true)
			System.out.printf("Successfully stop instance %s", instance_id);


	}

	public static void createInstances(String ami_id,String keypair) {


		RunInstancesRequest request = new RunInstancesRequest()
				.withImageId(ami_id)
				.withInstanceType(InstanceType.T2Micro)
				.withMaxCount(1)
				.withMinCount(1)
				.withKeyName(keypair);


		RunInstancesResult result  = ec2.runInstances(request);

		String reservation_id = result .getReservation().getInstances().get(0).getInstanceId();

		System.out.println("Successfully started EC2 instance "+ reservation_id +" based on AMI "+ami_id);

	}

	public static void rebootInstances(String instance_id) {

		System.out.println("Rebooting Instance.... "+instance_id);
		try {
			RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instance_id);
			RebootInstancesResult result = ec2.rebootInstances(request);
		}catch(Exception e) {
			System.out.println("ERROR : Instance is not running!");
			error=true;
		}
		if(error!=true)
			System.out.println("Successfully reboot instance " + instance_id);


	}

	public static void deleteInstances(String instance_id) {

		System.out.println("Terminating Instance.... "+instance_id);
		try {
			TerminateInstancesRequest request = new TerminateInstancesRequest()
					.withInstanceIds(instance_id);
			ec2.terminateInstances(request)
			.getTerminatingInstances()
			.get(0)
			.getPreviousState()
			.getName();
		}catch(Exception e) {
			System.out.println("ERROR : Nonexistent Instance!");
			error=true;
		}
		if(error!=true)
			System.out.println("The Instance is terminated with id: "+ instance_id);

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
			System.out.println("[ImageID] " + images.get(i).getImageId()+
					", [Name] "+images.get(i).getName()+
					", [Owner] "+images.get(i).getOwnerId());


		}



	}

	public static void createKeyPairs() {

		Scanner scan = new Scanner(System.in);


		System.out.print("input keyname : ");
		String key= scan.next();

		CreateKeyPairRequest request = new CreateKeyPairRequest();
		request.withKeyName(key);

		CreateKeyPairResult result =ec2.createKeyPair(request);

		System.out.println();
		System.out.println("Successfully create keypairs  "+key);

	}

	public static void listKeyPairs() {

		DescribeKeyPairsResult result = ec2.describeKeyPairs();

		for(KeyPairInfo key_pair : result.getKeyPairs()) {

			System.out.println( "[name] " +  key_pair.getKeyName());

		}

	}

	public static void deleteKeyPairs(String keypair) {
		DescribeKeyPairsResult show_result = ec2.describeKeyPairs();

		for(KeyPairInfo key_pair : show_result.getKeyPairs()) {

			if(key_pair.getKeyName().equals(keypair)) {
				error=false;
				break;
			}
			else {
				error=true;
			}


		}
		if(error!=true) {
			DeleteKeyPairRequest request = new DeleteKeyPairRequest().withKeyName(keypair);
			DeleteKeyPairResult del_result = ec2.deleteKeyPair(request);

			System.out.println("Successfully delete keypairs  "+ keypair);
		}
		else {
			System.out.println("ERROR : Nonexistent keypair!");
		}
	}
} 