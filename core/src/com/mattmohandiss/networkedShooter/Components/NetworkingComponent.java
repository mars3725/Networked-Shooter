package com.mattmohandiss.networkedShooter.Components;

import com.badlogic.ashley.core.Component;
import com.mattmohandiss.networkedShooter.Screens.GameScreen;

import java.util.Timer;

/**
 * Created by Matthew on 10/17/16.
 */
public class NetworkingComponent implements Component {
	public GameScreen game;
	public Timer timer = new Timer();
}
