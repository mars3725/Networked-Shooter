package com.mattmohandiss.networkedShooter.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector2;
import com.mattmohandiss.networkedShooter.Box2dLocation;
import com.mattmohandiss.networkedShooter.SteerableEntity;

/**
 * Created by Matthew on 11/21/16.
 */
public class RubberbandingComponent implements Component {
	public SteerableEntity steerable;
	public Box2dLocation idealPosition;
	public Arrive<Vector2> arriveBehavior;
}
