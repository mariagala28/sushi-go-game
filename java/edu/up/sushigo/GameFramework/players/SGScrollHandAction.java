package edu.up.sushigo.GameFramework.players;

import edu.up.sushigo.GameFramework.actionMessage.GameAction;

public class SGScrollHandAction extends GameAction {

    private int humanHandOffset;

    public SGScrollHandAction(SGHumanPlayer player, int humanHandOffset) {
        super(player);  // <-- pass the player to GameAction
        this.humanHandOffset = humanHandOffset;
    }

    public int getHumanHandOffset() {
        return humanHandOffset;
    }
}
