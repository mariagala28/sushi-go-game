package edu.up.sushigo;

import java.util.ArrayList;
import java.util.Random;

import edu.up.sushigo.GameFramework.LocalGame;
import edu.up.sushigo.GameFramework.ProxyGame;
import edu.up.sushigo.GameFramework.infoMessage.GameInfo;
import edu.up.sushigo.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.sushigo.GameFramework.players.GameComputerPlayer;
import edu.up.sushigo.GameFramework.players.SGReadyAction;
import edu.up.sushigo.GameFramework.utilities.Logger;

public class SGComputerPlayer extends GameComputerPlayer {


    Random random = new Random();

    private SGState myCopySGState;

    public SGComputerPlayer(String name) {
        // invoke superclass constructor
        super(name); // invoke superclass constructor
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        // if it was a "not your turn" message, just ignore it
        if (info instanceof NotYourTurnInfo) return;

        if(!(info instanceof SGState)) return;




        myCopySGState = (SGState) info;

        // Pick a move
        ArrayList<String> myHand ;//= state.getHand(game.getPlayerIdx());

        int selectedCard = 0;//random.nextInt(myHand.size());



        // Submit our move to the game object. We haven't even checked it it's
        // our turn, or that that position is unoccupied. If it was not our turn,
        // we'll get a message back that we'll ignore. If it was an illegal move,
        // we'll end up here again (and possibly again, and again). At some point,
        // we'll end up randomly pick a move that is legal.

        //game.sendAction(new SGMoveAction(this, myHand.get(selectedCard)));


        int playerId=-1;

        if(game instanceof LocalGame) {
            LocalGame bob = (LocalGame) game;
            playerId = bob.getPlayerId(this); //who is playing
        }
        else if(game instanceof ProxyGame) {
            ProxyGame bob = (ProxyGame) game;
            playerId = bob.getPlayerId(this); //who is playing
        }

        //has the player before me finished their turn?
        //if(playerId < 1){
          //  return;
        //}
        if(playerId>0 && myCopySGState.getTurnCompletedPlayers()[playerId-1] == false)
        {
            return;
        }
        if(playerId == 0)
        {
            if(myCopySGState.getTurn() % myCopySGState.getNumPlayers() != playerId)
            {
                return;
            }
            // Hand size max = first turn
            /*int count = 0;
            for(int i = 0; i < myCopySGState.getHand(playerId).length; i ++) {
                if(!(myCopySGState.getHand(playerId)[i].equals("EMPTY")))
                {
                    count++;
                }
            }
            if(myCopySGState.getHand(playerId).length != count)
            {
                if(myCopySGState.getTurnCompletedPlayers()[myCopySGState.getNumPlayers()-1] == false)
                {
                    //return;
                }
            }*/

            // check last player completed turn


        }
        Logger.log("SGComputer", "My turn!");
        Logger.log("SGComputer", "Sending move");

        int nextCardIndex = 0;

        //if haven't picked a card yet, pick next card in hand
        //find next card
        for(int i = 0; i < myCopySGState.getHand(playerId).length; i ++){
            if(!(myCopySGState.getHand(playerId)[i].equals("EMPTY"))) {
                nextCardIndex = i;
            }
        }
        if(myCopySGState.getReadyPlayers()[playerId] == false) {
            // delay for a second to make opponent think we're thinking
            //sleep(.5);
            game.sendAction(new SGPlayCard(this, myCopySGState.getHand(playerId)[nextCardIndex]));
        }
        else {
            // delay for a second to make opponent think we're thinking
            //sleep(.5);
           game.sendAction(new SGReadyAction(this));
        }

        //if i have picked a card already, press the ready button

    }


}


