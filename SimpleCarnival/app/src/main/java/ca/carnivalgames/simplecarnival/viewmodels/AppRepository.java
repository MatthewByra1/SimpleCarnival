package ca.carnivalgames.simplecarnival.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ca.carnivalgames.simplecarnival.Utils;
import ca.carnivalgames.simplecarnival.fragments.settings.ScoreboardStatistic;
import ca.carnivalgames.simplecarnival.persistence.AppInfo;
import ca.carnivalgames.simplecarnival.persistence.GameStatistic;
import ca.carnivalgames.simplecarnival.persistence.User;
import ca.carnivalgames.simplecarnival.persistence.UserState;

import static ca.carnivalgames.simplecarnival.persistence.AppInfo.NOT_LOGGED_IN;

public class AppRepository extends DatabaseRepository {
  private final MutableLiveData<Boolean> userWasSavedObservable = new MutableLiveData<>();
  private final MutableLiveData<List<ScoreboardStatistic>> scoreboardObservable =
      new MutableLiveData<>();

  ArrayList<String> gamesList = new ArrayList<>();

  {
    gamesList.add("Catch a Coin");
    gamesList.add("Petting Zoo");
    gamesList.add("Cup Shuffler");
    gamesList.add("Super Vine Bros");
  }

  public AppRepository(@NonNull Application application) {
    super(application);
  }

  public void setStatusToLoggedOut() {
    Utils.Threads.runOnDBThread(
        new Runnable() {
          @Override
          public void run() {
            db().setInfo(new AppInfo(System.currentTimeMillis(), NOT_LOGGED_IN));
          }
        });
  }

  public LiveData<Boolean> setUpNewUser(final String username) {
    Utils.Threads.runOnDBThread(
        new Runnable() {
          @Override
          public void run() {
            boolean usernameExist = db().getUserViaCurrentThread(username).size() != 0;

            if (usernameExist) userWasSavedObservable.postValue(false);
            else {
              long id = db().addUser(new User(username, System.currentTimeMillis()));
              signInUser((int) id);
            }
          }
        });

    return userWasSavedObservable;
  }

  public void signInUser(final int id) {
    Utils.Threads.runOnDBThread(
        new Runnable() {
          @Override
          public void run() {
            db().setInfo(new AppInfo(System.currentTimeMillis(), id));
          }
        });
  }

  public void saveGame(final GameStatistic statistic) {
    Utils.Threads.runOnDBThread(
        new Runnable() {
          @Override
          public void run() {
            db().setStatistic(statistic);
          }
        });
  }

  public LiveData<List<ScoreboardStatistic>> getScoreBoardInfo() {
    Utils.Threads.runOnDBThread(
        new Runnable() {
          @Override
          public void run() {
            if (UserState.getLoggedIn() != null) {
              //              List<MenuItem> menuItemList = Menu.getItemList();
              List<GameStatistic> scorePerGameForCurrentUser =
                  db().getAllUserStatisticsViaCurrentThread(UserState.getLoggedIn().username);

              List<ScoreboardStatistic> scoreboardStatistics = new ArrayList<>();
              for (String gameName : gamesList) {
                GameStatistic topScore = db().getTopStatisticsForGame(gameName).get(0);
                if (topScore != null) {
                  GameStatistic userGameStatistic =
                      getGameStatisticByGameNameInList(scorePerGameForCurrentUser, gameName);
                  scoreboardStatistics.add(new ScoreboardStatistic(topScore, userGameStatistic));
                }
              }
              scoreboardObservable.postValue(scoreboardStatistics);
            }
          }
        });

    return scoreboardObservable;
  }

  private GameStatistic getGameStatisticByGameNameInList(
      List<GameStatistic> listOfGames, String nameOfGame) {
    for (GameStatistic game : listOfGames) {
      if (game.getGameTitle() == nameOfGame) {
        return game;
      }
    }

    return getEmptyGameStatistic(nameOfGame, UserState.getLoggedIn().username);
  }

  private GameStatistic getEmptyGameStatistic(String nameOfGame, String userName) {
    return new GameStatistic("null", userName, nameOfGame, 0, 0, 0, 0);
  }
}
