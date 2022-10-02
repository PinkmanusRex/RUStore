package com.RUStore;

import java.io.IOException;
import java.util.Arrays;
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

	public static void main(String[] args) throws IOException {

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
		testStringPayload(client);
		client.disconnect();
	}
	
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

}
