package edu.up.sushigo;

import edu.up.sushigo.GameFramework.LocalGame;
import edu.up.sushigo.GameFramework.ProxyGame;
import edu.up.sushigo.GameFramework.infoMessage.GameInfo;
import edu.up.sushigo.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.sushigo.GameFramework.players.GameComputerPlayer;
import edu.up.sushigo.GameFramework.players.SGReadyAction;
import edu.up.sushigo.GameFramework.utilities.Logger;

public class SGSmartComputerPlayer extends GameComputerPlayer {

    private SGState myCopySGState;

    public SGSmartComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

        if (info instanceof NotYourTurnInfo) return;
        if(!(info instanceof SGState)) return;

        myCopySGState = (SGState) info;

        int playerId=-1;

        if(game instanceof LocalGame) {
            LocalGame smartComputer = (LocalGame) game;
            playerId = smartComputer.getPlayerId(this); //who is playing
        }
        else if(game instanceof ProxyGame) {
            ProxyGame smartComputer = (ProxyGame) game;
            playerId = smartComputer.getPlayerId(this); //who is playing
        }


        if(playerId > 0 && myCopySGState.getTurnCompletedPlayers()[playerId-1] == false)
        {
            return;
        }

        if(playerId == 0) {
            if (myCopySGState.getTurn() % myCopySGState.getNumPlayers() != playerId) {
                return;
            }
        }

        Logger.log("SGComputer", "My turn!");
        Logger.log("SGComputer", "Sending move");

        String[] hand = myCopySGState.getHand(playerId);

        int bestIndex = findBestCard(hand);

        String cardToPlay = hand[bestIndex];

        if(myCopySGState.getReadyPlayers()[playerId] == false) {
            game.sendAction(new SGPlayCard(this, cardToPlay));
        }
        else {
            // Already made a move, so press Ready
            game.sendAction(new SGReadyAction(this));
        }

    }

    private int findBestCard(String[] hand) {
        int bestIndex = 0;
        int bestScore = -999;

        for(int i=0; i < hand.length; i++) {
            if(!hand[i].equals("EMPTY")) {
                int value = evaluateCard(hand[i]);
                if(value > bestScore) {
                    bestScore = value;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;

    }

    private int evaluateCard(String card) {
        switch(card) {
            case "Squid Nigiri": return 3;
            case "Salmon Nigiri": return 2;
            case "Egg Nigiri": return 1;

            case "Tempura": return 2;
            case "Sashimi": return 3;
            case "Dumpling": return 1;

            case "Maki 1": return 1;
            case "Maki 2": return 2;
            case "Maki 3": return 3;

            case "Wasabi": return 0;
            case "Chopsticks": return 0;
            case "Pudding": return 0;

            case "EMPTY": return -999;
            case "UNKNOWN": return -999;

            default: return 0;
        }
    }


}
