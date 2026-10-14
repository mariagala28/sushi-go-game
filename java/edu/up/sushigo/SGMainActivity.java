package edu.up.sushigo;

import java.util.ArrayList;

import edu.up.sushigo.GameFramework.GameMainActivity;
import edu.up.sushigo.GameFramework.LocalGame;
import edu.up.sushigo.GameFramework.gameConfiguration.GameConfig;
import edu.up.sushigo.GameFramework.gameConfiguration.GamePlayerType;
import edu.up.sushigo.GameFramework.infoMessage.GameState;
import edu.up.sushigo.GameFramework.players.GamePlayer;
import edu.up.sushigo.GameFramework.players.SGHumanPlayer;

public class SGMainActivity extends GameMainActivity {

    //Tag for logging
    private static final String TAG = "TTTMainActivity";
    public static final int PORT_NUMBER = 5213;

    /**
     * a tic-tac-toe game is for two players. The default is human vs. computer
     */
    @Override
    public GameConfig createDefaultConfig() {

        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new SGHumanPlayer(name, R.layout.activity_main) {
                    @Override
                    public String getPlayerName() {
                        return "";
                    }
                };
            }
        });

        playerTypes.add(new GamePlayerType("Local Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new SGComputerPlayer(name);
            }
        });

        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 5, "SushiGo", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer", 1);
        defaultConfig.addPlayer("Local Human Player", 0);
        defaultConfig.addPlayer("Dumb Computer Player", 1);

        // SMART PLAYER
        defaultConfig.addPlayer("Smart Computer Player", 2);

        return defaultConfig;
    }

    /**
     * createLocalGame
     * <p>
     * Creates a new game that runs on the server tablet,
     *
     * @param gameState the gameState for this game or null for a new game
     * @return a new, game-specific instance of a sub-class of the LocalGame
     * class.
     */
    @Override
    public LocalGame createLocalGame(GameState gameState) {
        if (gameState == null)
            return new SGLocalGame();
        return new SGLocalGame((SGState) gameState);
    }
}