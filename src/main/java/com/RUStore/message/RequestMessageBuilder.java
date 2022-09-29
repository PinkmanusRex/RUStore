package com.RUStore.message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.RUStore.message.RequestType.*;
import static com.RUStore.message.UtilTools.*;

public class RequestMessageBuilder {
	public static byte[] putObjRequest(String key, byte[] payload) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeByte(PUT_OBJ_REQUEST);
			pack(dOut, key.getBytes(charSet));
			pack(dOut, payload);
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] putFileRequest(String key, String file_path) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeByte(PUT_FILE_REQUEST);
			pack(dOut, key.getBytes(charSet));
			pack(dOut, Files.readAllBytes(Paths.get(file_path)));
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getDataRequest(String key) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeByte(GET_DATA_REQUEST);
			pack(dOut, key.getBytes(charSet));
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] delDataRequest(String key) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeByte(DEL_DATA_REQUEST);
			pack(dOut, key.getBytes(charSet));
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getKeysRequest() {
		return new byte[] {GET_KEYS_REQUEST};
	}
	public static byte[] disconnectRequest() {
		return new byte[] {DISCONNECT_REQUEST};
	}
}
