package com.RUStore.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UtilTools {
	//the ASCII CharSet that is used for String to byte[] conversions
	public static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;
	
	/**
	 * Places a logical entry [len : int, data : byte[]] into the passed in output stream
	 * <br>
	 * the logical entry is physical a stream of bytes where the first 4 bytes is an int  'len' describing the length of the data, the next 'len' bytes are the actual data itself
	 * <br>
	 * the data immediately succeeds the len
	 * 
	 * @param out DataOutputStream where data will be written to
	 * @param data byte[] array that will be written to out
	 * @throws IOException
	 */
	public static final void pack(DataOutputStream out, byte[] data) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}
	
	/**
	 * Convenience method to write to out and then flush to ensure that the data is actually sent
	 * 
	 * @param out
	 * @param packet
	 * @throws IOException
	 */
	public static final void sendPacket(DataOutputStream out, byte[] packet) throws IOException {
		out.write(packet);
		out.flush();
	}
	
	/**
	 * Convenience method to read in len bytes from the in DataInputStream
	 * 
	 * @param in
	 * @param len
	 * @return byte[] containing the bytes read from in
	 * @throws IOException
	 */
	public static final byte[] readPacket(DataInputStream in, int len) throws IOException {
		byte[] packet = new byte[len];
		in.readFully(packet, 0, len);
		return packet;
	}
	
	/**
	 * Convenience method to read the key from the input stream
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static final String getKeyFromStream(DataInputStream in) throws IOException {
		return new String(
					readPacket(in, in.readInt()),
					UTF_8_CHARSET
				);
	}
}
