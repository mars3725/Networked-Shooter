package com.mattmohandiss.networkedShooter.Enums;

import java.io.Serializable;

/**
 * Created by Matthew on 11/15/16.
 */
public enum MessageType implements Serializable {
	clientJoin, addPlayer, position, removePlayer, velocity, fireBullet, changeState
}
