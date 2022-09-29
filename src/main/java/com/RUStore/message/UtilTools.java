package com.RUStore.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class UtilTools {
	public static final Charset charSet = Charset.forName("US_ASCII");
	public static final void pack(DataOutputStream out, byte[] data) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}
	public static final void sendPacket(DataOutputStream out, byte[] packet) throws IOException {
		out.write(packet);
		out.flush();
	}
	public static final byte[] readPacket(DataInputStream in, int len) throws IOException {
		byte[] packet = new byte[len];
		in.readFully(packet, 0, len);
		return packet;
	}
}
