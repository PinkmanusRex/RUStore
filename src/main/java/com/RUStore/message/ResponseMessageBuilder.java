package com.RUStore.message;

import static com.RUStore.message.ResponseType.*;

public class ResponseMessageBuilder {
	public static byte[] putSuccessResponse() {
		return new byte[] {PUT_SUCCESS};
	}
	public static byte[] putDupeResponse() {
		return new byte[] {PUT_DUPE_KEY};
	}
	public static byte[] getSuccessResponse() {
		return new byte[] {GET_SUCCESS};
	}
	public static byte[] keyNFResponse() {
		return new byte[] {KEY_NF};
	}
	public static byte[] delSuccessResponse() {
		return new byte[] {DEL_SUCCESS};
	}
}
