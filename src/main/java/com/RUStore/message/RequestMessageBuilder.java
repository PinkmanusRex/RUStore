package com.RUStore.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.RUStore.message.RequestType.*;

public class RequestMessageBuilder {
	private static final Charset charSet = Charset.forName("US_ASCII");
	public static byte[] putObjRequest(String key, byte[] payload) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			byte[] keyByteArr = key.getBytes(charSet);
			dOut.writeShort(PUT_OBJ_REQUEST);
			dOut.writeShort(keyByteArr.length);
			dOut.write(keyByteArr);
			dOut.write(payload);
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] putFileRequest(String key, String file_path) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			byte[] keyByteArr = key.getBytes(charSet);
			dOut.writeShort(PUT_FILE_REQUEST);
			dOut.writeShort(keyByteArr.length);
			dOut.write(keyByteArr);
			dOut.write(Files.readAllBytes(Paths.get(file_path)));
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getDataRequest(String key) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			byte[] keyByteArr = key.getBytes(charSet);
			dOut.writeShort(GET_DATA_REQUEST);
			dOut.write(keyByteArr); //the remaining amount of bytes is just the key, so no need to write the length of the key byte array
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] delDataRequest(String key) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			byte[] keyByteArr = key.getBytes(charSet);
			dOut.writeShort(DEL_DATA_REQUEST);
			dOut.write(keyByteArr); //the remaining amount of bytes is just the key, so no need to write the length of the key byte array
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getKeysRequest() throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeShort(GET_KEYS_REQUEST);
			dOut.flush();
			return bOut.toByteArray();
		}
	}
}
