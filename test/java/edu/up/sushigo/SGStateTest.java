// Maria Galatescu Tests
package edu.up.sushigo;

import org.junit.Test;
import static org.junit.Assert.*;

public class SGStateTest {

    /** Test that the game initializes correctly */
    @Test
    public void testInitialization() { //Check when game starts
        SGState state = new SGState();
        assertEquals(5, state.getHand(0).length); // hand size should be 5 (for alpha release)
        assertEquals(0, state.getPlayerScores()[0]); // initial score is 0
        assertFalse(state.getTurnCompletedPlayers()[0]); // no turns completed yet
    } //Verifies that the initial game state is set correctly, which is fundamental for all future gameplay

    /** Test that selecting a card moves it from hand to plate */
    @Test
    public void testSelectCard() { //Simulates a player selecting a card from their hand
        SGState state = new SGState();
        String card = state.getHand(0)[0]; // first card in player 0 hand
        boolean success = state.selectCard(0, 0);
        assertTrue(success);
        assertEquals("EMPTY", state.getHand(0)[0]); // card removed from hand
        assertEquals(card, state.getPlayedCards(0)[0]); // card added to plate
        assertTrue(state.getReadyPlayers()[0]); // player marked ready
        // Player presses "Ready" to end their turn
        boolean readySuccess = state.readyEndTurn(0);
        assertTrue(readySuccess);
        assertTrue(state.getTurnCompletedPlayers()[0]); // turn is now completed
    } //Ensures the core action of the game (playing a card) works as expected

    /** Test passing hands between players */
    @Test
    public void testPassHands() { //Saves the first player’s hand before passing
        SGState state = new SGState();
        // Manually empty one card in first player's hand to test empty preservation
        state.getHand(0)[0] = "EMPTY";
        String[] firstPlayerHandBefore = state.getHand(0).clone();
        state.passHands();
        String[] firstPlayerHandAfter = state.getHand(0); // First player's new hand should match second player's old hand
        assertArrayEquals(state.getHand(1), firstPlayerHandBefore); // Ensure empty slots are preserved
        assertEquals("EMPTY", state.getHand(1)[0]); // Ready status reset after passing
        // assertFalse(state.getReadyPlayers()[0]);
    } //Checks that turn rotation works correctly and preserves empty card slots

    //Maria Galatescu Tests ^

    //Josie's tests -->
    /**
     * make sure correct number of cards are dealt at beginning of game
     * should be 108 minus however many were dealt (num players x hand size)
     * populate deck is called automatically when an instance of SGState is initialized
     */
    @Test
    public void testPopulateDeck() {
        SGState state = new SGState();
        int numDealt = ((state.getNumPlayers())*(state.getHandSize())); //find how many cards should be dealt out of hand
        assertEquals(108 - numDealt, state.getDeck().size());
    }

    /**
     * test that the isgameatstart method works correctly
     * makes sure a newly initialized gamestate is considered gameatstart
     * makes sure after the first time scores are given it is no longer considered to be the start of the game
     */
    @Test
    public void testIsGameAtStart() {
        SGState state = new SGState();
        //make sure game is considered "at start" when it is first initialized
        assertTrue(state.isGameAtStart());
        //make sure game is no longer considered "at start" after a turn is completed and scored
        for(int i = 0; i < state.getNumPlayers(); i ++){
            state.selectCard(i,0);
            state.readyEndTurn(i);
        }
        assertFalse(state.isGameAtStart());
    }

    /**
     * test if the round ends properly when players run out of cards
     * does it set game over to true when there are no cards left in anyones hands?
     * is game over false before this point?
     * (in the future this can just check if the round is over)
     */
    @Test
    public void testGameOver() {
        SGState state = new SGState();
        //make sure game is not over when first initialized
        assertFalse(state.roundOver);
        //run through test game so that all cards are played
        for(int i = 0; i < state.getHandSize(); i ++) {
            for(int j = 0; j < state.getNumPlayers(); j ++) {
                state.selectCard(j,i);
                state.readyEndTurn(j);
            }
        }
        //check if game is considered over once all cards have been played
        assertTrue(state.roundOver);
    }

    //end of josie's tests


    // TONY'S TEST

    @Test
    public void copyConstructor()
    {
        SGState state = new SGState();
        state.setNumberPlayers(5);

        state.setPlayerName(0, "Original Player");

        SGState copy = new SGState(state);

        copy.setPlayerName(0, "Copy Player");

        // Test -> Should be different
        assertEquals("Original Player", state.getPlayerName(0));
        assertEquals("Copy Player", copy.getPlayerName(0));
    }

    @Test
    public void setPlayerName()
    {
        
        SGState state = new SGState();
        state.setNumberPlayers(5);

        state.setPlayerName(2, "Bob");
        assertEquals("Bob", state.getPlayerName(2));

    }

    @Test
    public void testCalculateScores()
    {

        SGState state = new SGState();
        state.setNumberPlayers(5);

        state.getPlayedCards(0)[0] = "Sashimi";
        state.getPlayedCards(0)[1] = "Tempura";

        state.countScores();

        // 3 + 2 = 5
        assertEquals(5, state.getPlayerScores()[0]);

    }


    // END OF TONY'S TEST




}



