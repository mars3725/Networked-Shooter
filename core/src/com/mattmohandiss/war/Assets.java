package com.mattmohandiss.war;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Matthew on 9/19/16.
 */
public class Assets {
	public static Label.LabelStyle labelStyle;

	public static void load() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("Fonts/visitor.fnt"));
		labelStyle = new Label.LabelStyle(font, Color.LIGHT_GRAY);
	}
}
