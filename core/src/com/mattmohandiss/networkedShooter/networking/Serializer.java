package com.mattmohandiss.networkedShooter.networking;

import java.io.*;

/**
 * Created by Matthew on 11/15/16.
 */
public class Serializer {
	public static byte[] serialize(Message obj) throws IOException {
		try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
			try (ObjectOutputStream o = new ObjectOutputStream(b)) {
				o.writeObject(obj);
			}
			return b.toByteArray();
		}
	}

	public static Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
			try (ObjectInputStream o = new ObjectInputStream(b)) {
				return (Message) o.readObject();
			}
		}
	}
}

