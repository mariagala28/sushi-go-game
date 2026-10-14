package edu.up.sushigo;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;
import edu.up.sushigo.GameFramework.players.GamePlayer;

public class SGDrawCard extends GameAction {

    // Action - Draw Card

    private static final String TAG = "SGDrawCard";
    private static final long serialVersionUID = -2242980258970485343L;

    // String of cards to choose from -> ArrayList
    private String cardToPlay;
    public SGDrawCard(GamePlayer player, String card)
    {
        super(player);

        this.cardToPlay = card;
    }

    public String getCardToPlay()
    {
        return cardToPlay;
    }

}
