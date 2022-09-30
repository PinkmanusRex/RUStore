package com.RUStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static com.RUStore.message.RequestMessageBuilder.*;
import static com.RUStore.message.ResponseType.*;
import static com.RUStore.message.UtilTools.*;

/* any necessary Java packages here */

public class RUStoreClient {

	/* any necessary class members here */
	private final String host;
	private final int port;
	
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;

	/**
	 * RUStoreClient Constructor, initializes default values
	 * for class members
	 *
	 * @param host	host url
	 * @param port	port number
	 * @throws NullPointerException if host or port is null
	 */
	public RUStoreClient(String host, int port) {
		// Implement here
		this.host = Objects.requireNonNull(host);
		this.port = Objects.requireNonNull(port);
	}

	/**
	 * Opens a socket and establish a connection to the object store server
	 * running on a given host and port.
	 *
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect() throws UnknownHostException, IOException {

		// Implement here
		clientSocket = new Socket(host, port);
		try {
			in = new DataInputStream(
					new BufferedInputStream(
							clientSocket.getInputStream()
							)
					);
		} catch (IOException e) {
			clientSocket.close();
			clientSocket = null;
			throw e;
		}
		try {
			out = new DataOutputStream(
					new BufferedOutputStream(
							clientSocket.getOutputStream()
							)
					);
		} catch (IOException e) {
			clientSocket.close();
			in.close();
			clientSocket = null;
			in = null;
			throw e;
		}

	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT be 
	 * overwritten
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param data	byte array representing arbitrary data object
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, byte[] data) throws IOException {

		// Implement here
		sendPacket(out, putDataRequest(key, data));
		
		byte status = in.readByte();

		if (status == PUT_SUCCESS)
			return 0;
		else if (status == PUT_DUPE_KEY)
			return 1;
		else
			throw new IOException(String.format("%d does not match PUT_SUCCESS or PUT_DUPE_KEY response status codes", status));
	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT 
	 * be overwritten.
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param file_path	path of file data to transfer
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, String file_path) throws IOException {

		// Implement here
		sendPacket(out, putDataRequest(key, file_path));
		
		byte status = in.readByte();

		if (status == PUT_SUCCESS)
			return 0;
		else if (status == PUT_DUPE_KEY)
			return 1;
		else
			throw new IOException(String.format("%d does not match PUT_SUCCESS or PUT_DUPE_KEY response status codes", status));
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server.
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		object data as a byte array, null if key doesn't exist.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 */
	public byte[] get(String key) throws IOException {

		// Implement here
		sendPacket(out, getDataRequest(key));
		
		byte status = in.readByte();

		if (status == KEY_NF)
			return null;
		else if (status == GET_SUCCESS)
			return readPacket(in, in.readInt());
		else
			throw new IOException(String.format("%d does not match KEY_NF or GET_SUCCESS response status codes", status));
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server and places it in a file. 
	 * 
	 * @param key	key associated with the object
	 * @param	file_path	output file path
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int get(String key, String file_path) throws IOException {

		// Implement here
		sendPacket(out, getDataRequest(key));
		
		byte status = in.readByte();
		
		if (status == KEY_NF)
			return 1;
		else if (status == GET_SUCCESS) {
			Files.write(
					Paths.get(file_path), 
					readPacket(in, in.readInt())
				);
			return 0;
		} else
			throw new IOException(String.format("%d does not match KEY_NF or GET_SUCCESS response status codes", status));
	}

	/**
	 * Removes data object associated with a given key 
	 * from the object store server. Note: No need to download the data object, 
	 * simply invoke the object store server to remove object on server side
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int remove(String key) throws IOException {

		// Implement here
		sendPacket(out, delDataRequest(key));
		
		byte status = in.readByte();
		
		if (status == KEY_NF)
			return 1;
		else if (status == DEL_SUCCESS)
			return 0;
		else
			throw new IOException(String.format("%d does not match KEY_NF or DEL_SUCCESS response status codes", status));

	}

	/**
	 * Retrieves of list of object keys from the object store server
	 * 
	 * @return		List of keys as string array, null if there are no keys.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 */
	public String[] list() throws IOException {

		// Implement here
		sendPacket(out, getKeysRequest());
		
		byte status = in.readByte();
		
		if (status == KEY_NF)
			return null;
		else if (status == GET_SUCCESS) {
			int noEntries = in.readInt();
			String[] keys = new String[noEntries];
			for (int i = 0; i < noEntries; i += 1) 
				keys[i] = new String(
								readPacket(in, in.readInt()), 
								ASCII_CHARSET
							);
			return keys;
		} else
			throw new IOException(String.format("%d does not match KEY_NF or GET_SUCCESS response status codes", status));
	}

	/**
	 * Signals to server to close connection before closes 
	 * the client socket.
	 * 
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 */
	public void disconnect() throws IOException {

		// Implement here
		try {
			sendPacket(out, disconnectRequest());
		} finally {
			in.close();
			out.close();
			clientSocket.close();
			in = null;
			out = null;
			clientSocket = null;
		}

	}

}
