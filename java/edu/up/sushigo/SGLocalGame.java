package edu.up.sushigo;


import java.util.ArrayList;

import edu.up.sushigo.GameFramework.infoMessage.GameInfo;
import edu.up.sushigo.GameFramework.players.GameComputerPlayer;
import edu.up.sushigo.GameFramework.players.GamePlayer;
import edu.up.sushigo.GameFramework.LocalGame;
import edu.up.sushigo.GameFramework.actionMessage.GameAction;
import edu.up.sushigo.GameFramework.players.ProxyPlayer;
import edu.up.sushigo.GameFramework.players.SGHumanPlayer;
import edu.up.sushigo.GameFramework.players.SGReadyAction;
import edu.up.sushigo.GameFramework.utilities.Logger;


public class SGLocalGame extends LocalGame {
    //Tag for logging

    private static final String TAG = "SGLocalGame";

    // the number of moves that have been played so far, used to
    // determine whether the game is over
    protected int moveCount;


    /**
     * Constructor for the SGLocalGame.
     */
    public SGLocalGame() {

        // perform superclass initialization
        super();

        // create a new, unfilled-in TTTState object
        super.state = new SGState();
    }

    /**
     * Constructor for the SGLocalGame with loaded sgState
     *
     *
     */
    public SGLocalGame(SGState mySGState) {
        super();
        super.state = new SGState(mySGState);
    }

    /**
     * This is where you should initialize anything specific to the
     * number of players.  For example you may need to init your
     * game state or part of it.  Loading data could also happen here.
     *
     * @param players
     */
    @Override
    public void start(GamePlayer[] players) {
        super.start(players);

        SGState sgState = (SGState) state;
        sgState.setNumberPlayers(players.length);

        // Set names in SGState
        for (int i = 0; i < players.length; i++) {
            sgState.setPlayerName(i, players[i].getPlayerName());
        }

        // Optional: update human player's view of other player names
        int humanIndex = -1;
        for (int i = 0; i < players.length; i++) {
            if (players[i] instanceof SGHumanPlayer) {
                humanIndex = i;
                break;
            }
        }
        if (humanIndex != -1) {
            SGHumanPlayer human = (SGHumanPlayer) players[humanIndex];
            int num = 0;
            for (int i = 0; i < players.length; i++) {
                if (i != humanIndex) {
                    human.setName(num++, players[i].getPlayerName());
                }
            }
        }
    }


    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return a message that tells who has won the game, or null if the
     * game is not over
     */

    @Override
    protected String checkIfGameOver() {
        SGState sgState = (SGState) state;

        // Only proceed if the final round (round 3) is over
        if (!sgState.isRoundOver() || sgState.getCurrentRound() < 3) {
            return null; // not the final round yet
        }

        // 1. Calculate cumulative scores (playerScores + roundTotalScores)
        int numPlayers = sgState.getNumPlayers();
        int[] scores = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            scores[i] = sgState.getPlayerScores()[i] + sgState.getRoundTotalScores()[i];
        }

        // 2. Find the highest score
        int maxScore = scores[0];
        for (int score : scores) {
            if (score > maxScore) maxScore = score;
        }

        // 3. Collect winner indices (handle ties)
        ArrayList<Integer> winnerIndices = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            if (scores[i] == maxScore) {
                winnerIndices.add(i);
            }
        }

        // 4. Build the message
        StringBuilder message = new StringBuilder();
        if (winnerIndices.size() == 1) {
            int winner = winnerIndices.get(0);
            String winnerName = sgState.getPlayerName(winner);
            message.append(winnerName)
                    .append(" wins with ")
                    .append(maxScore)
                    .append(" points!");
        } else {
            message.append("It's a tie between: ");
            for (int i = 0; i < winnerIndices.size(); i++) {
                int w = winnerIndices.get(i);
                String name = sgState.getPlayerName(w);
                message.append(name);
                if (i != winnerIndices.size() - 1) message.append(", ");
            }
            message.append(" with ").append(maxScore).append(" points!");
        }

        // Save message in state so human players can see it
        sgState.setGameOverMessage(message.toString());

        Logger.log("checkGameOver", message.toString());
        return message.toString();
    }

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p the player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // make a copy of the state, and send it to the player
        p.sendInfo(new SGState(((SGState) state)));

    }

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx the player's player-number (ID)
     * @return true iff the player is allowed to move
     */
    protected boolean canMove(int playerIdx) {
        return true;
    }

    /**
     * getPlayerId
     *
     * calculates the id of a given GamePlayer (which is just the index into super.players
     * a GamePlayer object
     *
     * @return  the player id or -1 if not found
     */

//    public int getPlayerId(GamePlayer player)
//    {
//        for(int i = 0; i < super.players.length; i ++) {
//            if(super.players[i].equals(player)){
//                //found which player its referencing
//                return i;
//            }
//        }
//
//        //if it doesnt find a match
//        return -1;
//    }



    @Override
    protected boolean makeMove(GameAction action)
    {
        // Initial setup
        //SGMoveAction tm = (SGMoveAction) action;
        SGState state = (SGState) super.state;

        //If the recieved action is a playcard action:
        if(action instanceof SGPlayCard) {
            //typecast and send to gamestate
            SGPlayCard pCard = (SGPlayCard) action;

            //find corresponding sgstate player id int
            int id = getPlayerId(pCard.getPlayer());

            //find index of card in players hand so we can call selectCard on it
            int cardIndex = state.getCardIndex(id, ((SGPlayCard) action).getCardToPlay());

            return state.selectCard(id, cardIndex);

        }

        //if the recieved action is a ready/swaphands action:
        if(action instanceof SGReadyAction) {
            //typecast and send to gamestate
            SGReadyAction rAction = (SGReadyAction) action;

            //find corresponding sgstate player id int
            int id = getPlayerId(rAction.getPlayer());

            //update readyplayers bool array in state
            return state.readyEndTurn(id);

            //check if all other players are ready. if so, swap hands in state
        }

        //if the recieved action is a player using a chopsticks card:
        if(action instanceof SGChopsticks) {

            //typecast and send to gamestate
            SGChopsticks cAction = (SGChopsticks) action;

            //find corresponding sgstate player id int
            int id = getPlayerId(cAction.getPlayer());

            //call chopsticks method in state on that player
            return state.useChopsticks(id);
        }

        //if the recieved action is a player using a wasabi card:
        if(action instanceof SGWasabi) {

            //typecast and send to gamestate
            SGWasabi cAction = (SGWasabi) action;

            //find corresponding sgstate player id int
            int id = getPlayerId(cAction.getPlayer());

            //call wasabi method in state on that player
            return state.useWasabi(id);
        }

        
        // Set up card here -->

        // Get the 0/1 id of our player
        //int playerId = getPlayerIdx(tm.getPlayer());

        // if it is the player's turn, indicate an illegal move
        //if(!canMove(playerId)) return false;

        // Check for illegal move, if card is duplicate/invalid -->

        // get 0/1 id of player whose move it is
        int whoseMove = state.getWhoseMove();

        // Place card here -->


        // Bump the move count
        moveCount++;

        return true;
    }

    // --- TESTING ---

    public int whoWon()
    {
        // Checks who won, prints message accordingly
        return 1;
    }

}
