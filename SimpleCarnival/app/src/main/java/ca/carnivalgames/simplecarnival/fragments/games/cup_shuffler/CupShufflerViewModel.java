package ca.carnivalgames.simplecarnival.fragments.games.cup_shuffler;

import android.animation.ObjectAnimator;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.animation.AnimatorSet.Builder;
import android.animation.AnimatorSet;
import android.animation.Animator;
import java.util.ArrayList;



import androidx.lifecycle.ViewModel;

import java.util.Random;

public class CupShufflerViewModel extends ViewModel {
    // Game objects
    private ImageView leftCup;
    private ImageView middleCup;
    private ImageView rightCup;
    private ImageView ball;
    private ImageView hasBall;

    private int screenWidth;
    private int screenHeight;
    private int level = 1;
    private int highest = 1;
    private int coinsCollected = 0;

    void initCup(ImageView cup1, ImageView cup2, ImageView cup3, ImageView ball, int firstSlot, int secondSlot, int thirdSlot){
        leftCup = cup1;
        leftCup.setLeft(firstSlot);
        leftCup.setY(-200);

        middleCup = cup2;
        middleCup.setLeft(secondSlot);
        middleCup.setY(-200);

        rightCup = cup3;
        rightCup.setLeft(thirdSlot);
        rightCup.setY(-200);

        this.ball = ball;
        this.ball.setY(180);

        hasBall = middleCup;
    }

    // Switches the two cups' positions
    private AnimatorSet moveCup(ImageView cup1, ImageView cup2, ImageView ball, int rand){

        float cup1X = cup1.getX();
        float cup2X = cup2.getX();
        Animator cupAnimation1 = ObjectAnimator.ofFloat(cup1, "x", cup2X);
        Animator cupAnimation2 = ObjectAnimator.ofFloat(cup2, "x", cup1X);
        cupAnimation1.setDuration(1000/level);
        cupAnimation2.setDuration(1000/level);
        AnimatorSet sets = new AnimatorSet();

    if (rand == 1 || rand == 2) {
        Animator ballAnimation = ObjectAnimator.ofFloat(ball, "x", cup1X);
        ballAnimation.setDuration(1000/level);
        sets.playTogether(cupAnimation1, cupAnimation2, ballAnimation);
        ball.setX(cup1X);
    }
    else{
        sets.playTogether(cupAnimation1, cupAnimation2);
    }
    
        cup1.setX(cup2X);
        cup2.setX(cup1X);

        return sets;
    }


    void shuffle(ImageView cup1, ImageView cup2, ImageView cup3, ImageView ball){

        hasBall = cup2;

        AnimatorSet setAnimator = new AnimatorSet();
        ArrayList<Animator> animationSet = new ArrayList<Animator>();

        Random random = new Random();
        for (int i = 0; i < level*5; i++){
            int randomInteger = random.nextInt(3);
            if (randomInteger == 1){
                animationSet.add(moveCup(cup1,cup2, ball, randomInteger));
            }
            else if (randomInteger == 2){
                animationSet.add(moveCup(cup3,cup2, ball, randomInteger));
            } else {
                animationSet.add(moveCup(cup1,cup3, ball, randomInteger));
            }

        }

        setAnimator.playSequentially(animationSet);
        setAnimator.setDuration(2000/level);
        setAnimator.start();


    }

    private void ballToCup(ImageView ball, ImageView cup){
        ObjectAnimator ballAnimation = ObjectAnimator.ofFloat(ball, "x", cup.getX());
        ballAnimation.setDuration(1000/level);
        ballAnimation.start();
        ball.setX(cup.getX());
    }

    void hideBall(ImageView ball){
        Random rand = new Random();
        int random = rand.nextInt(3);

        if (random == 0) {
            hasBall = leftCup;
            ball.setLeft(leftCup.getLeft());
        }
        else if (random == 1) {
            hasBall = middleCup;
            ball.setLeft(middleCup.getLeft());
        }
        else {
            hasBall = rightCup;
            ball.setLeft(rightCup.getLeft());
        }
    }

    int streakLevel(){
        return level;
    }

    int highestLevel() { return highest; }

    int getCoinsCollected() { return coinsCollected; }

    boolean guess(ImageView cup){
        if (cup.equals(hasBall)){
            level++;
            coinsCollected += level;
            if (highest < level){
                highest = level;
            }
            return true;
        }
        else{
            level = 1;
            return false;
        }
    }

  void setScreenSize(int width, int height){
      screenWidth = width;
      screenHeight = height;
  }

  void bringCupsDown(ImageView cup1, ImageView cup2, ImageView cup3){
      ObjectAnimator cup1Animation = ObjectAnimator.ofFloat(cup1, "y", 400);
      ObjectAnimator cup2Animation = ObjectAnimator.ofFloat(cup2, "y", 400);
      ObjectAnimator cup3Animation = ObjectAnimator.ofFloat(cup3, "y", 400);

      cup1Animation.setDuration(1000);
      cup2Animation.setDuration(1000);
      cup3Animation.setDuration(1000);

      cup1Animation.start();
      cup2Animation.start();
      cup3Animation.start();
  }

  void bringCupsUp(ImageView cup1, ImageView cup2, ImageView cup3){
        ObjectAnimator cup1Animation = ObjectAnimator.ofFloat(cup1, "y", -50);
        ObjectAnimator cup2Animation = ObjectAnimator.ofFloat(cup2, "y", -50);
        ObjectAnimator cup3Animation = ObjectAnimator.ofFloat(cup3, "y", -50);

        cup1Animation.setDuration(1000);
        cup2Animation.setDuration(1000);
        cup3Animation.setDuration(1000);

        cup1Animation.start();
        cup2Animation.start();
        cup3Animation.start();
    }


  ImageView ballLocation(){return hasBall;}
}
