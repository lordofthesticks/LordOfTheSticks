/*
Copyright 2019 Brian Alexander, John Bailey, Austin DeWitt, Trey Page

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package edu.cnm.deepdive.lordofthesticks.gameplay;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Class hold all of the logic for the game by creating the {@link Player} setting the view through
 * {@link #orthographicCamera} usage. By initially creating the {@link #world} then {@link Player} then rendering and unpdating
 * everything by pulling in the {@link Player} and {@link MapParser} to create the game. 
 */
public class StickTest extends ApplicationAdapter {

  private static final float SCALE = 1.5f;
  public static final float PIXEL_PER_METER = 32f;
  private static final float TIME_STEP = 1 / 60f;
  private static final int VELOCITY_ITERATIONS = 6;
  private static final int POSITION_ITERATIONS = 2;
  private static final float VELOCITY_Y = -9.85f;
  private static final float VELOCITY_X = 0f;
  private static final String MAP_PATH = "map/GameMap.tmx";
  private OrthographicCamera orthographicCamera;
  private Box2DDebugRenderer box2DDebugRenderer;
  private Viewport gamePort;
  private World world;
  private Player player;
  private SpriteBatch batch;
  private Texture texture;
  private OrthogonalTiledMapRenderer tiledMapRenderer;
  private TiledMap tiledMap;
  private BitmapFont font;

  public static final int V_Height = 500;
  public static final int V_WIDTH = 1000;

  @Override
  public void create() {
    orthographicCamera = new OrthographicCamera();
    gamePort = new FitViewport(StickTest.V_WIDTH,StickTest.V_Height,orthographicCamera);
    orthographicCamera.position.set(gamePort.getWorldWidth() / 2,gamePort.getWorldHeight() /2 , 0);
//    orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    world = new World(new Vector2(VELOCITY_X, VELOCITY_Y), false);
    batch = new SpriteBatch();
    texture = new Texture(Player.PLAYER_IMG_PATH);
    box2DDebugRenderer = new Box2DDebugRenderer();
    tiledMap = new TmxMapLoader().load(MAP_PATH);
    tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    MapParser.parseMapLayers(world, tiledMap);
    player = new Player(world);
    world.setContactListener(new WorldContactListener());

  }

  @Override
  public void render() {
    String str = "Hello World";
    font = new BitmapFont();

    update();
    Gdx.gl.glClearColor(0.0f, 0.0f, 0f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    tiledMapRenderer.render();
    batch.setProjectionMatrix(orthographicCamera.combined);
    batch.begin();
    font.draw(batch, str, 10, 10);
    batch.end();
    batch.begin();
    // All drawing goes in here! Will have to add in the crap here. The texture comes first, then x and y coordinates, then width then height.
//    font.draw(batch, str, 10, 10);
    batch.draw(texture, player.getBody().getPosition().x * PIXEL_PER_METER - (texture.getWidth() / 4),
        player.getBody().getPosition().y * PIXEL_PER_METER - (texture.getHeight() / 4), 60f, 150f);
    batch.end();
  }

  private void update() {
    world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

    inputUpdate();

    cameraUpdate();
    tiledMapRenderer.setView(orthographicCamera);

  }

  private void cameraUpdate() {
    Vector3 position = orthographicCamera.position;
    position.x = player.getBody().getPosition().x * PIXEL_PER_METER;
    position.y = player.getBody().getPosition().y * PIXEL_PER_METER;
    orthographicCamera.position.set(position);
    orthographicCamera.update();
  }

  @Override
  public void resize(int width, int height) {
    orthographicCamera.setToOrtho(false, width / SCALE, height / SCALE);
  }

  @Override
  public void dispose() {
    texture.dispose();
    batch.dispose();
    box2DDebugRenderer.dispose();
    world.dispose();
    tiledMapRenderer.dispose();
    tiledMap.dispose();
  }

  private void inputUpdate() {
    int horizontalForce = 0;
    boolean isJumping = false;
    if (Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
      touchPos = orthographicCamera.unproject(touchPos);
      if (touchPos.x / PIXEL_PER_METER > player.getBody().getPosition().x)
        horizontalForce += 1;
      if (touchPos.x / PIXEL_PER_METER < player.getBody().getPosition().x)
        horizontalForce -= 1;
      if (touchPos.y / PIXEL_PER_METER > player.getBody().getPosition().y && !player.isJumping())
        isJumping = true;
    }
    playerUpdate(horizontalForce, isJumping);
  }


  private void playerUpdate(int horizontalForce, boolean isJumping) {
    if (player.isDead()) {
      world.destroyBody(player.getBody());
      player = new Player(world);
    }
    if(isJumping)
      player.getBody().applyForceToCenter(0, Player.JUMP_FORCE, false);
    player.getBody().setLinearVelocity(horizontalForce * Player.RUN_FORCE, player.getBody().getLinearVelocity().y);
  }

}