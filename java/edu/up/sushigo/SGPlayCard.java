package edu.up.sushigo;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;
import edu.up.sushigo.GameFramework.players.GamePlayer;

public class SGPlayCard extends GameAction {

    private static final String TAG = "SGPlayCard";
    private static final long serialVersionUID = -2242980258970485343L;

    private String cardToPlay;
    public SGPlayCard(GamePlayer player, String cardToPlay)
    {
        // invoke superclass constructor to set the player
        super(player);

        this.cardToPlay = cardToPlay;
    }

    public String getCardToPlay() {
        return cardToPlay;
    }


}
