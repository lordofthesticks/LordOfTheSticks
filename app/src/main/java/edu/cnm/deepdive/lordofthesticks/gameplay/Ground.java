package edu.cnm.deepdive.lordofthesticks.gameplay;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Class for {@link MapParser} to look through a map for object layers called 'Ground' to give density for collision detection.
 */
public class Ground {
  private static final float DENSITY = 1.0f;
  public Ground(World world, Shape shape) {
    Body body;
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.StaticBody;
    body = world.createBody(bodyDef);
    body.createFixture(shape, DENSITY).setUserData(this);
    shape.dispose();
  }
}