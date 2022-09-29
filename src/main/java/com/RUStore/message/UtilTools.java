package com.RUStore.message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class UtilTools {
	public static final Charset charSet = Charset.forName("US_ASCII");
	public static final void packer(DataOutputStream out, byte[] data) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}
}
