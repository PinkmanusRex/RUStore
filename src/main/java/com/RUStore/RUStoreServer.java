package com.RUStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.RUStore.message.RequestType.*;
import static com.RUStore.message.ResponseMessageBuilder.*;
import static com.RUStore.message.UtilTools.*;

/* any necessary Java packages here */

public class RUStoreServer {

	/* any necessary class members here */
	
	private static Map<String, byte[]> objStore = new HashMap<>();

	/* any necessary helper methods here */

	private static void sendAllKeys(DataOutputStream out) throws IOException {
		Object[] keysAsObj =  objStore.keySet().toArray();
		String[] keys = new String[keysAsObj.length];
		for (int i = 0; i < keys.length; i += 1) {
			keys[i] = (String) keysAsObj[i];
		}
		sendPacket(out, getKeysSuccessResponse(keys));
	}
	private static void deleteEntry(DataOutputStream out, DataInputStream in) throws IOException {
		String key = getKeyFromStream(in);
		byte[] data = objStore.remove(key);
		if (data == null)
			sendPacket(out, keyNFResponse());
		else
			sendPacket(out, delSuccessResponse());
	}
	private static void getDataFulfiller(DataOutputStream out, DataInputStream in) throws IOException {
		String key = getKeyFromStream(in);
		byte[] data = objStore.get(key);
		if (data == null)
			sendPacket(out, keyNFResponse());
		else
			sendPacket(out, getDataSuccessResponse(data));
	}
	private static void putData(DataOutputStream out, DataInputStream in) throws IOException {
		String key = getKeyFromStream(in);
		byte[] data = readPacket(in, in.readInt());
		if (objStore.containsKey(key)) {
			sendPacket(out, putDupeResponse());
		} else {
			objStore.put(key, data);
			sendPacket(out, putSuccessResponse());
		}
	}
	
	private static void runServer(int port) throws IOException {
		try (ServerSocket server = new ServerSocket(port);) {
			while (true) {
				try {
					runClient(server);
				} catch (EOFException e) { //the client crashed so their inputstream will cause EOF. our runClient will close the socket and streams so we just need to catch the error and continue on trudging
				}
			}
		}
	}
	
	private static void runClient(ServerSocket server) throws IOException {
		try (
				Socket clientSocket = server.accept();
				DataInputStream in = new DataInputStream(
						new BufferedInputStream(
								clientSocket.getInputStream()
								)
						);
				DataOutputStream out = new DataOutputStream(
						new BufferedOutputStream(
								clientSocket.getOutputStream()
								)
						);
			) {
			boolean run = true;
			while (run) {
				byte requestType = in.readByte();
				switch (requestType) {
					case DISCONNECT_REQUEST :
						run = false;
						break;
					case GET_KEYS_REQUEST :
						sendAllKeys(out);
						break;
					case DEL_DATA_REQUEST :
						deleteEntry(out, in);
						break;
					case GET_DATA_REQUEST :
						getDataFulfiller(out, in);
						break;
					case PUT_DATA_REQUEST :
						putData(out, in);
						break;
				}
			}
		}
	}

	/**
	 * RUObjectServer Main(). Note: Accepts one argument -> port number
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException{

		// Check if at least one argument that is potentially a port number
		if(args.length != 1) {
			System.out.println("Invalid number of arguments. You must provide a port number.");
			return;
		}

		// Try and parse port # from argument
		int port = Integer.parseInt(args[0]);

		// Implement here //
		runServer(port);
	}

}
