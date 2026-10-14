package edu.up.sushigo.GameFramework.players;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;

public class SGPlayCardAction extends GameAction {

    private String chosenCard;
    private int cardIndex;

    public SGPlayCardAction(SGHumanPlayer player, String chosenCard, int cardIndex) {
        super(player);  // <-- pass the player to GameAction
        this.chosenCard = chosenCard;
        this.cardIndex = cardIndex;
    }

    public String getChosenCard() {
        return chosenCard;
    }

    public int getCardIndex() {
        return cardIndex;
    }
}
