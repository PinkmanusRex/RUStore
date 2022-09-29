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
			packer(dOut, data);
			dOut.flush();
			return bOut.toByteArray();
		}
	}
	public static byte[] getKeysSuccessResponse() {
		return new byte[] {GET_SUCCESS};
	}
	public static byte[] keyNFResponse() {
		return new byte[] {KEY_NF};
	}
	public static byte[] delSuccessResponse() {
		return new byte[] {DEL_SUCCESS};
	}
}
