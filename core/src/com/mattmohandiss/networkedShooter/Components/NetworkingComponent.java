package com.mattmohandiss.networkedShooter.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Timer;
import com.mattmohandiss.networkedShooter.GameScreen;

/**
 * Created by Matthew on 10/17/16.
 */
public class NetworkingComponent implements Component {
	public GameScreen game;
	public Timer timer = new Timer();
}
