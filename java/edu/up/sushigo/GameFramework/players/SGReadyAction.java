package edu.up.sushigo.GameFramework.players;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;

public class SGReadyAction extends GameAction {

    public SGReadyAction(GamePlayer player) {
        super(player);  // <-- pass the player to GameAction
    }
}
