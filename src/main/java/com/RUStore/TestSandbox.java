package com.RUStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.RUStore.message.UtilTools.*;

/**
 * This TestSandbox is meant for you to implement and extend to 
 * test your object store as you slowly implement both the client and server.
 * 
 * If you need more information on how an RUStorageClient is used
 * take a look at the RUStoreClient.java source as well as 
 * TestSample.java which includes sample usages of the client.
 */
public class TestSandbox{

	public static void main(String[] args) throws Exception {

		// Create a new RUStoreClient
		RUStoreClient client = new RUStoreClient("localhost", 12345);

		// Open a connection to a remote service
		System.out.println("Connecting to object server...");
		try {
			client.connect();
			System.out.println("Established connection to server.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to connect to server.");
		}
		testRemove(client);
		client.disconnect();
	}
	
	//test removing a key-value entry
	public static void testRemove(RUStoreClient client) throws IOException {
		System.out.print("TEST_REMOVE : ");
		client.put("removed", "Shouldn't exist".getBytes(UTF_8_CHARSET));
		client.remove("removed");
		System.out.println(client.get("removed"));
	}
	
	//test duplicate key when doing PUT request
	public static void testDupeKeyPut(RUStoreClient client) throws IOException {
		System.out.print("TEST_DUPE_KEY_PUT : ");
		client.put("dupe_key", "Correct entry".getBytes(UTF_8_CHARSET));
		client.put("dupe_key", "Incorrect entry".getBytes(UTF_8_CHARSET));
		System.out.println(new String(client.get("dupe_key"), UTF_8_CHARSET));
	}
	
	//test PUT for 22MB file
	public static void testBigFile(RUStoreClient client) throws IOException {
		System.out.print("TEST_BIG_FILE : ");
		String fileKey = "book";
		String inputPath = "./inputfiles/book.pdf";
		String outputPath = "./outputfiles/book.pdf";
		client.put(fileKey, inputPath);
		client.get(fileKey, outputPath);
		File fileIn = new File(inputPath);
		File fileOut = new File(outputPath);
		byte[] fileInBytes = Files.readAllBytes(fileIn.toPath());
		byte[] fileOutBytes = Files.readAllBytes(fileOut.toPath());
		if (Arrays.equals(fileInBytes, fileOutBytes))
			System.out.println("File contents equal");
		else
			System.out.println("File contents not equals");
		Files.delete(fileOut.toPath());
	}
	
	
	//test sending a bunch of strings and getting those same strings back
	public static void testStringPayload(RUStoreClient client) throws IOException {
		System.out.print("TEST_STRING_PAYLOAD : ");
		String[][] keyVals = new String[][] {
			{"joe", "Joseph Smorgin"},
			{"atlas", "Titanic Atlas"},
			{"java-tool", "Eclipse"},
			{"h2o", "Water"}
		};
		for (String[] pair : keyVals)
			client.put(pair[0], pair[1].getBytes(UTF_8_CHARSET));
		String testRes = Arrays.stream(keyVals)
			.map(it -> it[0])
			.map(key -> {
				try {
					return String.format("{%s, %s}", key, new String(client.get(key), UTF_8_CHARSET));
				} catch (IOException e) {
					return String.format("{%s, error}", key);
				}
			})
			.collect(Collectors.joining(","));
		System.out.println(testRes);
	}
	
	//test being able to retrieve all keys
	public static void testGetAllKeys(RUStoreClient client) throws IOException {
		System.out.print("TEST_GET_ALL_KEYS : ");
		String[][] keyVals = new String[][] {
			{"joe", "Joseph Smorgin"},
			{"atlas", "Titanic Atlas"},
			{"java-tool", "Eclipse"},
			{"h2o", "Water"}
		};
		for (String[] pair : keyVals)
			client.put(pair[0], pair[1].getBytes(UTF_8_CHARSET));
		String keys = Arrays.stream(client.list()).collect(Collectors.joining(", "));
		System.out.println(keys);
	}
	
	//test sending a serializable and getting that serializable back
	public static void testSerializablePayload(RUStoreClient client) throws IOException, ClassNotFoundException {
		System.out.print("TEST_SERIALIZABLE_PAYLOAD : ");
		HashMap<String, String> payload = new HashMap<String, String>(
					Map.ofEntries(
							Map.entry("Potato", "Mashed Potato"),
							Map.entry("Apple", "MacBook Pro")
						)
				);
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				ObjectOutputStream oOut = new ObjectOutputStream(bOut);
			) {
			oOut.writeObject(payload);
			oOut.flush();
			byte[] bytePayload = bOut.toByteArray();
			client.put("payload", bytePayload);
			byte[] responseByteArr = client.get("payload");
			try (
					ByteArrayInputStream bIn = new ByteArrayInputStream(responseByteArr);
					ObjectInputStream oIn = new ObjectInputStream(bIn);
				) {
				Object o = oIn.readObject();
				HashMap<String, String> o2 = (HashMap<String, String>) o;
				String st = o2.entrySet()
								.stream()
								.map(e -> String.format("{%s, %s}", e.getKey(), e.getValue()))
								.collect(Collectors.joining(","));
				System.out.println(st);
			}
		}
	}

}
