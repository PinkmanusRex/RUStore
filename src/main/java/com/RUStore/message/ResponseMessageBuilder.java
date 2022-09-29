package com.RUStore.message;

import static com.RUStore.message.ResponseType.*;
import static com.RUStore.message.UtilTools.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseMessageBuilder {
	public static byte[] putSuccessResponse() {
		return new byte[] {PUT_SUCCESS};
	}
	public static byte[] putDupeResponse() {
		return new byte[] {PUT_DUPE_KEY};
	}
	public static byte[] getDataSuccessResponse(byte[] data) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.writeByte(GET_SUCCESS);
			pack(dOut, data);
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getKeysSuccessResponse(String[] keys) throws IOException {
		try (
				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				DataOutputStream dOut = new DataOutputStream(bOut);
			) {
			dOut.write(GET_SUCCESS);
			dOut.writeInt(keys.length);
			for (String key : keys) 
				pack(dOut, key.getBytes(charSet));
			return bOut.toByteArray();
		}
	}
	public static byte[] keyNFResponse() {
		return new byte[] {KEY_NF};
	}
	public static byte[] delSuccessResponse() {
		return new byte[] {DEL_SUCCESS};
	}
}
