package edu.cnm.deepdive.lordofthesticks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.view.SurfaceHolder;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import edu.cnm.deepdive.lordofthesticks.view.GameView;

public class GamePlay extends AppCompatActivity implements JoystickView.JoystickListener {


  // The view of the game!
  GameView gameView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_game_play);
    JoystickView joystickLeft = findViewById(R.id.joystickLeft);
    JoystickView joystickRight = findViewById(R.id.joystickRight);
    gameView = findViewById(R.id.game_screen);
//    joystickLeft.setZOrderOnTop(true);
//    joystickRight.setZOrderOnTop(true);
    SurfaceHolder surfaceRight = joystickRight.getHolder();
    SurfaceHolder surfaceLeft = joystickLeft.getHolder();
    surfaceRight.setFormat(PixelFormat.TRANSPARENT);
    surfaceLeft.setFormat(PixelFormat.TRANSPARENT);

  }

  @Override
  public void onJoystickMoved(float xPercent, float yPercent, int id) {


    switch (id)
    {
      case R.id.joystickRight:
        Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
        break;
      case R.id.joystickLeft:
        Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
        if (xPercent < 0 && xPercent > -0.5) {              //walking left
          gameView.setMovingLeft(true);
          gameView.setMovingRight(false);
          gameView.setRunningLeft(false);
          gameView.setRunningRight(false);
        } else if (xPercent > 0 && xPercent < 0.5) {        //walking right
          gameView.setMovingRight(true);
          gameView.setMovingLeft(false);
          gameView.setRunningLeft(false);
          gameView.setRunningRight(false);
        }
        else if (xPercent > 0.5 && xPercent < 1) {          //running right
          gameView.setRunningRight(true);
          gameView.setRunningLeft(false);
          gameView.setMovingRight(false);
          gameView.setMovingLeft(false);
        }
        else if (xPercent < -0.5 && xPercent > -1) {        //running left
          gameView.setRunningLeft(true);
          gameView.setRunningRight(false);
          gameView.setMovingRight(false);
          gameView.setMovingLeft(false);
        }
        if (gameView.getLastXPercent() == 0 && xPercent == 0) {     //Stopping movement
          gameView.setRunningRight(false);
          gameView.setRunningLeft(false);
          gameView.setMovingLeft(false);
          gameView.setMovingRight(false);
        }
        gameView.setLastXPercent(xPercent);
        break;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    gameView.resume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    gameView.pause();
  }


  public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
    Drawable drawable = AppCompatResources.getDrawable(context, drawableId);

    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
      Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);

      return bitmap;
    } else {
      throw new IllegalArgumentException("unsupported drawable type");
    }
  }
//  int bitCount(int value) {
//    int count = 0;
//    while (value > 0) {
//      if (value % 2 == 1) {
//        count++;
//        value /= 2;
//      } else {
//        value = value / 2;
//      }
//    }
//    return count;
//  }



}
