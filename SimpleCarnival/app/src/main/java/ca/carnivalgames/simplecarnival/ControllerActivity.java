package ca.carnivalgames.simplecarnival;

import android.os.Bundle;
import android.view.View;

import ca.carnivalgames.simplecarnival.fragments.menu.MenuFragment;

// Todo: Documentation
public class ControllerActivity extends SetUpActivity implements ViewController {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_controller);
    backButton = findViewById(R.id.controller_back_button);
    super.onCreate(savedInstanceState);
  }

  @Override
  void onShowHome() {
    hideControls();
    loadFragment(new MenuFragment());
  }

  private void hideControls() {
    hideGameControls();
    hideNavControls();
  }

  @Override
  public void hideGameControls() {}

  @Override
  public void showGameControls() {}

  @Override
  public void hideNavControls() {
    backButton.setVisibility(View.INVISIBLE);
  }

  @Override
  public void showNavControls() {
    backToMenu = true;
    backButton.setVisibility(View.VISIBLE);
  }
}
