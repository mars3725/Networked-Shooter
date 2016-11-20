package com.mattmohandiss.networkedShooter.networking;

import com.mattmohandiss.networkedShooter.Enums.MessageType;

import java.io.Serializable;

/**
 * Created by Matthew on 11/15/16.
 */
public class Message implements Serializable {
	public MessageType type;
	public int id;
	public int[] contents;

	public Message(MessageType type, int id) {
		this.type = type;
		this.id = id;
	}

	public Message(MessageType type, int id, int[] contents) {
		this.type = type;
		this.id = id;
		this.contents = contents;
	}
}
