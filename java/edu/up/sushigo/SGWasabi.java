package edu.up.sushigo;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;
import edu.up.sushigo.GameFramework.players.GamePlayer;

public class SGWasabi extends GameAction {

    private static final String TAG = "SGChopsticks";
    //TODO: what is this?? -> private static final long serialVersionUID = -2242980258970485343L;

    public SGWasabi(GamePlayer player) {
        // invoke superclass constructor to set the player
        super(player);
    }

}
