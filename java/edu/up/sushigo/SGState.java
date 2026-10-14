package edu.up.sushigo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


import edu.up.sushigo.GameFramework.infoMessage.GameState;

/**
 * GameState
 * sushi go game state
 *
 *
 * @author Josephine, Maria, Tony, Kenzie
 */
public class SGState extends GameState implements Serializable {

    private static final String TAG = "SGState";
    private static final long serialVersionUID = 7552321013488624386L;

    // ----- Instance Variables -----

    //deck of all cards
    private ArrayList<String> deck = new ArrayList<>();

    private String[] playerNames;

    private String[] lastSelectedCard;


    public static final String[] CARD_NAMES = {
            "UNKNOWN",  //used to hide other player's cards //0
            "EMPTY", //used to indicate an empty card slot in a players hand so it wont be null //1
            "Sashimi", //2
            "Tempura", //3
            "Sashimi", //4
            "Dumpling", //5
            "Maki 1", //6
            "Maki 2", //7
            "Maki 3", //8
            "Salmon Nigiri", //9
            "Egg Nigiri", //10
            "Squid Nigiri", //11
            "Pudding", //12
            "Wasabi", //13
            "Chopsticks", //14
            "Wasabi Egg Nigiri", //15
            "Wasabi Salmon Nigiri", //16
            "Wasabi Squid Nigiri", //17
            "Used Wasabi" //18
    };

    private int numPlayers;

    private int handSize = 0;

    private int currentPlayerId;
    //TODO: we shouldn't be using this because SG isn't turn based

    //array of booleans which says which players have chosen a card
    //useful for seeing if the indexed player has already selected a card
    private boolean[] readyPlayers;

    //keeps track of which playeres have both selected a card and pressed ready, indicating their turn is done
    //and they are ready to swap hands.
    private boolean[] turnCompletedPlayers;

    boolean roundOver;

    boolean gameOver;

    private String[][] playerHands;     // Each player's current hand of cards

    private String[][] playerPlates;    // Cards each player has played

    private int[] playerScores;         // Each player's current score; added to round total scores at end of round

    private int[] roundTotalScores;      //Each player's total score from all previous finished rounds

    private int[] playerPuddings;   //how many puddings each player has; carries over between rounds

    private int[] currRoundPuddings; //how many puddings each player has from *this* round

    private int roundNumber; //which round out of 3 this is

    private int turnCount;

    private boolean finalScores; //whether the final scores/bonuses have been calculated


    /**
     * Helper method to populate deck (doesn't shuffle it yet)
     * <p>
     * There are 108 cards in a sushi go deck
     * each card type is added to the deck the amount of times there are cards of that type in the deck
     */
    public void populateDeck(ArrayList<String> deck) {

        //I definitely should have made a helper method to do this but oh well
        for (int i = 0; i < 14; i++) {
            //there are 14 of each of the following card types in the deck. this loop adds them.
            deck.add(CARD_NAMES[3]); //Tempura
            deck.add(CARD_NAMES[4]); //Sashimi
            deck.add(CARD_NAMES[5]); //Dumpling

            //there are 12 double maki cards in the deck:
            if (i < 12) {
                deck.add(CARD_NAMES[7]); //Maki Roll 2

                //there are 10 of these cards in the deck:
                if (i < 10) {

                    deck.add(CARD_NAMES[9]); //Salmon Nigiri
                    deck.add(CARD_NAMES[12]); //Pudding

                    //there are 8 triple maki cards in the deck:
                    if (i < 8) {
                        deck.add(CARD_NAMES[8]); // Maki Roll 3

                        //there are 6 of these cards in the deck:
                        if (i < 6) {
                            deck.add(CARD_NAMES[13]); //Wasabi
                            deck.add(CARD_NAMES[6]); //Maki Roll 1

                            //there are 5 of these cards in the deck:
                            if (i < 5) {
                                deck.add(CARD_NAMES[11]); //Squid Nigiri
                                deck.add(CARD_NAMES[10]); //Egg Nigiri

                                //there are 4 chopsticks cards in the deck:
                                if (i < 4) {
                                    deck.add(CARD_NAMES[14]); //Chopsticks
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Shuffle Deck
     * <p>
     * Reassigns each card in the given deck to a random location
     */
    public void shuffleDeck(ArrayList<String> deck) {
        Random randy = new Random();
        for (int i = 0; i < 108; i++) { //run thru whole deck
            int randIndex = randy.nextInt(108 - i);
            Collections.swap(deck, randIndex, 107);
        }
    }

    // ----- Constructor -----

    /**
     * Default constructor (takes no args, passes in default args to main constructor)
     */
    public SGState() {
        super();
        // init(numPlayers);
    }


    /**
     * Initializes the game state to the starting configuration.
     */
    
    private void init(int numGamers) {
        numPlayers = numGamers;
        //currentPlayerId = 0;
        roundOver = false;
        gameOver = false;
        roundNumber = 1;

        turnCount = 0;

        //if incorrect num players arg is passed in, run game with default 2 players:
        if (numPlayers > 5 || numPlayers < 2) {
            numPlayers = 2;
        }


        //determine number of cards in each hand based on number of players
        //5 players = 7 cards, 4 = 8 cards, 3 = 9 cards, 2 = 10 cards
        switch (numPlayers) {
            case 2:
                handSize = 10;
                break;

            case 3:
                handSize = 9;
                break;

            case 4:
                handSize = 8;
                break;

            case 5:
                handSize = 7;
                break;

        }


        playerHands = new String[numPlayers][handSize];    // initialize player hands array holding max cards (hand size)
        playerPlates = new String[numPlayers][handSize];  // up to the starting hand size card amount will end up played
        //initialize cards in playerplates as empty (TODO: experiment, possibly undo this)
        SGState state = new SGState();

        for (int i = 0; i < state.getNumPlayers(); i++) {
            for (int j = 0; j < handSize; j++) {
                playerPlates[i][j] = CARD_NAMES[1];
            }
        }
        playerScores = new int[numPlayers];
        roundTotalScores = new int[numPlayers];
        readyPlayers = new boolean[numPlayers];
        turnCompletedPlayers = new boolean[numPlayers];
        lastSelectedCard = new String[numPlayers];
        playerPuddings = new int[numPlayers];
        currRoundPuddings = new int[numPlayers];

        this.playerNames = new String[numPlayers];

        //populate and shuffle deck arraylist
        populateDeck(deck);
        shuffleDeck(deck);

        // Populate hands with cards from the deck
        for (int i = 0; i < numPlayers; i++) {
            for (int j = 0; j < handSize; j++) {
                //playerHands[i][j] = "Card" + (j + 1); example cards
                playerHands[i][j] = deck.remove(deck.size() - 1);
            }
            playerScores[i] = 0;
        }

    }

    public void setNumberPlayers(int length) {

        this.numPlayers = length;

         // Get the number of players after round starts
        init(numPlayers);

    }

    public int getNumPlayers()
    {

        return numPlayers;

    }

    public String getPlayerName(int index)
    {

        return playerNames[index];

    }

    public void setPlayerName(int index, String newName)
    {
        playerNames[index] = newName;
    }


    /**
     * Copy constructor
     * Param: gamestate object
     * makes a deep copy of the given gamestate in the pov of the given player
     */
    public SGState(SGState other) {

        // NETWORK PLAY -> GET IT TO WORK

        this.numPlayers = (other.numPlayers);

        //copy simple instance variables (ints, bools, 1d arrays
        this.currentPlayerId = other.currentPlayerId;

        this.roundOver = other.roundOver;

        this.roundNumber = other.roundNumber;

        this.finalScores = other.finalScores;

        //if(this.readyPlayers != null && other.readyPlayers != null) {
        this.readyPlayers = Arrays.copyOf(other.readyPlayers, other.readyPlayers.length);
        //}
        this.playerScores = Arrays.copyOf(other.playerScores, other.playerScores.length);
        this.roundTotalScores = Arrays.copyOf(other.roundTotalScores, other.roundTotalScores.length);
        this.lastSelectedCard = Arrays.copyOf(other.lastSelectedCard, other.lastSelectedCard.length);
        //if(this.turnCompletedPlayers != null && other.turnCompletedPlayers != null) {
        this.turnCompletedPlayers = Arrays.copyOf(other.turnCompletedPlayers, other.turnCompletedPlayers.length);
        //}
        this.playerHands = new String[other.playerHands.length][other.playerHands[0].length];
        for (int i = 0; i < this.playerHands.length; i++) {
            this.playerHands[i] = Arrays.copyOf(other.playerHands[i], other.playerHands[i].length);
        }

        this.turnCount = other.turnCount;

        this.playerNames = Arrays.copyOf(other.playerNames, other.playerNames.length);

        //make other players hands unknown but keep this player's hand known to themself
        /*for(int i = 0; i < this.playerHands.length; i ++) {
            //if (i != player) { //if opponent
                for(int j = 0; j < this.playerHands[i].length; j ++) {
                    if (!(this.playerHands[i][j].equals(CARD_NAMES[1]))) { //EMPTY
                        //if its not an empty space in opponents hand, change it to unknown card to conceal their hand
                        this.playerHands[i][j] = CARD_NAMES[0]; //UNKNOWN
                    }
                }
            //}
        }*/

        playerPlates = new String[this.numPlayers][];
        //copy all player plates. these are visible to everyone
        for (int i = 0; i < other.numPlayers; i++) {
            this.playerPlates[i] = Arrays.copyOf(other.playerPlates[i], other.playerPlates[i].length);
        }

        //copy player scores. these are visible to everyone? because you could count anyones score by looking at their plate.
        //this.playerScores = Arrays.copyOf(other.playerScores, other.numPlayers);

        //player puddings total from all rounds copied over
        this.playerPuddings = Arrays.copyOf(other.playerPuddings, other.numPlayers);
        this.currRoundPuddings = Arrays.copyOf(other.currRoundPuddings, other.numPlayers);

        //Deck is not copied over and is left empty since player should not be able to see whats in the deck.

    }

    // ----- toString() -----

    /**
     * @return a string representation of the current game state.
     */
    @Override
    public String toString() {
        // Gives "append" function
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sushi Go Game State ===\n");
        sb.append("Number of Players: ").append(numPlayers).append("\n");
        sb.append("Current Player ID: ").append(currentPlayerId).append("\n");
        sb.append("Round Over: ").append(roundOver).append("\n");

        sb.append("\n-- Player Hands --\n");
        for (int i = 0; i < numPlayers; i++) {
            sb.append("Player ").append(i).append(": ");
            for (String card : playerHands[i]) {
                sb.append(card).append(" ");
            }
            sb.append("\n");
        }

        sb.append("\n-- Player Plates --\n");
        for (int i = 0; i < numPlayers; i++) {
            sb.append("Player ").append(i).append(": ");
            for (String card : playerPlates[i]) {
                if (card != null) sb.append(card).append(" ");
            }
            sb.append("\n");
        }

        sb.append("\n-- Scores --\n");
        for (int i = 0; i < numPlayers; i++) {
            sb.append("Player ").append(i).append(": ").append(playerScores[i]).append("\n");
        }

        return sb.toString();
    }

    // ----- Action Methods -----

    /**
     * Player selects a card from their hand.
     *
     * @param playerId  ID of the player selecting the card
     * @param cardIndex index of the card in the player's hand
     * @return true if the move is valid, false otherwise
     */
    public boolean selectCard(int playerId, int cardIndex) {

        //if the player has already completed their turn and pressed ready, they cannot change cards.
        if (turnCompletedPlayers[playerId]) {
            return false;
        }

        if (cardIndex < 0 || cardIndex >= playerHands[playerId].length) {
            return false;
        }

        String selected = playerHands[playerId][cardIndex];
        if (selected == null) return false;
        if (selected.equals("EMPTY")) return false;


        // Move card from hand to plate
        for (int i = 0; i < playerPlates[playerId].length; i++) {
            //find next open spot on plate, either null or empty:
            if (playerPlates[playerId][i] == null || playerPlates[playerId][i].equals(CARD_NAMES[1])) {
                //if the player already selected another card this turn before this,
                //replace the previously selected card with the new one since only one
                //card can be selected per turn:
                if (readyPlayers[playerId]) {
                    playerHands[playerId][cardIndex] = playerPlates[playerId][i - 1];
                    playerPlates[playerId][i - 1] = selected;
                } else {
                    playerPlates[playerId][i] = selected;
                    playerHands[playerId][cardIndex] = CARD_NAMES[1]; //set spot card was taken from to empty
                }

                break;
            }
        }
        readyPlayers[playerId] = true;
        lastSelectedCard[playerId] = selected;

        turnCount++;
        return true;
    }

    /**
     * Player plays one of the three types of nigiri card on a wasabi card.
     * Card types change to indicate scoring bonus; value of the nigiri card is tripled.
     *
     * @param playerId ID of the player using Wasabi
     * @return true if successful, false otherwise
     */
    public boolean useWasabi(int playerId) {
        //does this player have an unused wasabi card?
        int wasabiIndex = getPlayedCardIndex(playerId, "Wasabi");
        if(wasabiIndex == -1) {
            return false;
        }

        //is the last card they selected a type of nigiri?
        if(!(lastSelectedCard[playerId].equals(CARD_NAMES[9])||lastSelectedCard[playerId].equals(CARD_NAMES[10])||lastSelectedCard[playerId].equals(CARD_NAMES[11]))){
            //if not, return false
            return false;
        }

        String nigiriType = lastSelectedCard[playerId];

        //find nigiri card index
        int nigiriIndex = getPlayedCardIndex(playerId, nigiriType);
        if(nigiriIndex == -1) {
            return false;
        }

        //find corresponding seasoned nigiri card to replace it with
        if(nigiriType.equals("Egg Nigiri")) {
            nigiriType = "Wasabi Egg Nigiri";
        }
        else if(nigiriType.equals("Egg Salmon")){
            nigiriType = "Wasabi Salmon Nigiri";
        }else{
            nigiriType = "Wasabi Squid Nigiri";
        }

        //replace wasabi index with used wasabi and nigiri index with seasoned nigiri
        playerPlates[playerId][wasabiIndex] = CARD_NAMES[18];
        playerPlates[playerId][nigiriIndex] = nigiriType;

        //test: replace wasabi card with wasabi egg nigiri when they press the button
        //playerPlates[playerId][wasabiIndex] = CARD_NAMES[15];

        return true;
    }

    /**
     * Player uses Chopsticks card to take an extra card.
     *
     * @param playerId ID of the player using Chopsticks
     * @return true if successful, false otherwise
     */
    public boolean useChopsticks(int playerId) {

        //has this player already finished their turn? return false if so
        if (turnCompletedPlayers[playerId]) {
            return false;
        }

        //does this player have chopsticks on their plate?
        int chopsticksIndex = getPlayedCardIndex(playerId, "Chopsticks");
        if (chopsticksIndex == -1) {
            //if they don't have chopsticks, return false
            return false;
        }

        //have they already picked 1 card? they must pick 1 card before they use chopsticks to get another.
        if (!readyPlayers[playerId]) {
            return false;
        }

        //put chopsticks back in their hand
        for (int i = 0; i < playerHands[playerId].length; i++) {
            if (playerHands[playerId][i].equals(CARD_NAMES[1]) || playerHands[playerId][i] == null) {
                playerHands[playerId][i] = "Chopsticks";
                playerPlates[playerId][chopsticksIndex] = CARD_NAMES[1];
                break;
            }
        }

        //set it so they havent picked a card yet so they can pick another
        readyPlayers[playerId] = false;


        return true;
    }

    /**
     * Sets the number of players (only before the game starts).
     *
     * @param count number of players
     * @return true if valid, false otherwise
     */
    public boolean setNumPlayers(int count) {
        if (count < 2 || count > 5 || !isGameAtStart()) return false;
        numPlayers = count;
        return true;
    }

    /**
     * Player presses "Ready" to end their turn.
     *
     * @param playerId ID of the player ending their turn
     * @return true if valid, false otherwise
     */
    public boolean readyEndTurn(int playerId) {

        //check if they have selected a card. if not, return; they must select a card to play first
        if (readyPlayers[playerId] == false) {
            return false;
        }

        //set player to completed turn
        turnCompletedPlayers[playerId] = true;

        //check if all players have completed their turns
        for (int i = 0; i < turnCompletedPlayers.length; i++) {
            if (turnCompletedPlayers[i] == false) {
                //if a player is not ready yet, don't swap the hands yet and just return
                return true;
            }
        }

        //if all players have completed their turns, pass hands
        passHands();
        return true;
    }

    /**
     * After both players have selected a card, their hand is passed to the next player.
     * The last player passes their hand back to the first player, making a loop.
     * After this method is called, everyone should have the player before them's hand.
     */
    public boolean passHands() {

        //put the last player's hand in a placeholder so we can give it to the first player at the end
        String[] placeholder = new String[playerHands[numPlayers - 1].length];
        placeholder = Arrays.copyOf(playerHands[numPlayers - 1], playerHands[numPlayers - 1].length);

        for (int i = numPlayers - 1; i >= 0; i--) {
            if (i == 0) { // if its the first player, give them the last player's hand we saved earlier.
                playerHands[i] = Arrays.copyOf(placeholder, playerHands[0].length);
            } else { //otherwise, copy the previous person's hand into this 'i'th one
                playerHands[i] = Arrays.copyOf(playerHands[i - 1], playerHands[i - 1].length);
            }
        }

        //set all players' ready and turn completed status back to false as a new turn has begun
        for (int i = 0; i < numPlayers; i++) {
            readyPlayers[i] = false;
            turnCompletedPlayers[i] = false;
        }

        countScores();


        // --- Check if round is over (all hands are empty) ---
        boolean handsEmpty = true;
        for (int i = 0; i < numPlayers; i++) {
            for (String card : playerHands[i]) {
                if (card != null && !card.equals("EMPTY")) {
                    handsEmpty = false;
                    break;
                }
            }
            if (!handsEmpty) break;
        }

        if (handsEmpty) { //if everyone is out of cards, the round is over. initialize next round or end the game
            roundOver = true;
            countScores();
            checkGameOver();
        }

        return true;
    }

    /**
     * checks if the game is over
     * if it is, set gameover to true
     * if not, initializes the next round
     * @return whether the game is over
     * -josie
     */
    private boolean checkGameOver(){
        //have 3 rounds been completed? If so, the game is over. If not, go to the next round.
        if(roundNumber >= 3&&roundOver){
            countScores();
            finalScores = true;
            for(int i = 0; i < numPlayers; i ++){
                playerScores[i] += roundTotalScores[i];
            }
            printFinalReceipt();
            gameOver = true;
            return true;
        }
        initNextRound();
        return false;
    }

    /**
     * should be called when a round ends and its time to start the next round
     * resets all variables as if it were a new game except for:
     * round number
     * player scores
     * player puddings
     * as this information should carry over between rounds.
     * -josie
     */
    private void initNextRound(){
        roundOver = false;
        roundNumber ++; //INCREMENT ROUND NUMBER!!!


        //empty everyones plates and hands
        for (int i = 0; i < this.getNumPlayers(); i++) {
            for (int j = 0; j < handSize; j++) {
                playerPlates[i][j] = CARD_NAMES[1];
                playerHands[i][j] = CARD_NAMES[1];
            }
        }

        //set all these arrays back to their default values
        for(int i = 0; i < numPlayers; i ++) {
            readyPlayers[i] = false;
            turnCompletedPlayers[i] = false;
            lastSelectedCard[i] = null;
            playerScores[i] = 0;
            currRoundPuddings[i] = 0;
        }

        //TODO: empty the deck


        //TODO populate and shuffle deck arraylist
//        populateDeck(deck);
//        shuffleDeck(deck);

        // Populate hands with cards from the deck
        for (int i = 0; i < numPlayers; i++) {
            for (int j = 0; j < handSize; j++) {
                playerHands[i][j] = deck.remove(deck.size() - 1);
            }
        }
    }

    private void printFinalReceipt() {
    }

    // ----- Helper Methods -----

    /**
     * Returns true if the game is still at the start state.
     */
    boolean isGameAtStart() {
        for (int score : playerScores) {
            if (score != 0) return false;
        }
        return true;
    }

    public int getWhoseMove() {
        return currentPlayerId;
    }

    // pass in int into getHand, returns playHand[]
    public String[] getHand(int PlayerHandNum) {
        //String[][] playerHands;
        ;
        return playerHands[PlayerHandNum];

    }

    public String[] getPlayedCards(int playerNum) {

        return playerPlates[playerNum];

    }

    /**
     * getCardIndex
     * <p>
     * calculates the index of the given card in the given players hand
     *
     * @param playerHand the index of the player whose hand we are looking for the card in
     * @param cardName   the name of the card we are looking for in the given players hand
     * @return the int index or -1 if not found
     */
    public int getCardIndex(int playerHand, String cardName) {
        for (int i = 0; i < playerHands[playerHand].length; i++) {
            if (playerHands[playerHand][i].equals(cardName)) {
                return i; //if you find the card in the players hand, return the index its at
            }
        }

        //if it doesnt find a match
        return -1;
    }

    /**
     * getPlayedCardIndex
     * <p>
     * calculates the index of the given card in the given players played cards
     *
     * @param playerPlate the index of the player whose played cards we are looking for the card in
     * @param cardName   the name of the card we are looking for in the given players played cards
     * @return the int index or -1 if not found
     */
    public int getPlayedCardIndex(int playerPlate, String cardName) {
        for (int i = 0; i < playerPlates[playerPlate].length; i++) {
            if (playerPlates[playerPlate][i].equals(cardName)) {
                return i; //if you find the card in the players plate, return the index its at
            }
        }

        //if it doesnt find a match
        return -1;
    }

    public boolean[] getReadyPlayers() {
        return readyPlayers;
    }

    public boolean[] getTurnCompletedPlayers() {
        return turnCompletedPlayers;
    }

    public int[] getPlayerScores() {
        return playerScores;
    }

    public int[] getRoundTotalScores() {
        return roundTotalScores;
    }

    public int getHandSize() { return handSize; }

    public int getRoundNumber() { return roundNumber; }

    public ArrayList<String> getDeck() {
        return deck;
    }

    /**
     * countScores
     * Count Cards and Score
     * counts up how many of each type of card a player has in their played cards
     * used for scoring, particularly combinations of cards that score together
     * and competitive cards where the player with the most gets a bonus at
     * the end of the round.
     * booleans for whether they recieve end of round bonuses
     * calculate player scores based on official scoring rules of sushi go
     * account for combinations of cards that score together
     * TODO set end of round scoring bonuses (make instance vars for these: e.g. makiWinnerPlayerId = 2; if player 2 had most maki
     * if its the end of the game, apply pudding bonus and penalty
     * -josie
     */
    public void countScores() {

        //if the final scores have already been calculated, do not calculate them again to avoid double counting.
        if(finalScores){
            return;
        }

        //for maki where the player with the most at the end of the round gets a bonus:
        int[] makiTotals = new int[numPlayers];

        //go through all player plates
        for(int i = 0; i < numPlayers; i ++) {
            //variables for cards that are part of a scoring combo or bonus
            int numTempura = 0;
            int numSashimi = 0;
            int numDumpling = 0;
            int numMakiTotal = 0; //so maki x3 would += 3 this
            int score = 0; //how much this player's current plate is worth
            currRoundPuddings[i] = 0;

            for (String card : playerPlates[i]) {
                if (card == null) continue;
                switch (card) {
                    case "Tempura":
                        numTempura++;
                        break;
                    case "Sashimi":
                        numSashimi++;
                        break;
                    case "Dumpling":
                        numDumpling++;
                        break;
                    case "Maki 1":
                        numMakiTotal += 1;
                        break;
                    case "Maki 2":
                        numMakiTotal += 2;
                        break;
                    case "Maki 3":
                        numMakiTotal += 3;
                        break;
                    case "Pudding":
                        currRoundPuddings[i]++;
                        break;

                    //the following cards arent part of a combo so just add their value
                    //cards not listed are worth no points such as chopsticks or used wasabi
                    case "Egg Nigiri":
                        score += 1;
                        break;
                    case "Wasabi Egg Nigiri":
                        score += 3;
                        break;
                    case "Salmon Nigiri":
                        score += 2;
                        break;
                    case "Wasabi Salmon Nigiri":
                        score += 6;
                        break;
                    case "Squid Nigiri":
                        score += 3;
                        break;
                    case "Wasabi Squid Nigiri":
                        score += 9;
                        break;
                    default:
                        break;

                }
            }

            //now all the cards in their hand have been tallied up, time to score combos
            score += (5*(numTempura/2)); //each set of 2 tempura is worth 5 points
            score += (10*(numSashimi/3)); //each set of 3 sashimi is worth 10 points
            makiTotals[i] = numMakiTotal;
            switch (numDumpling) { //the points you get from dumplings increases the more dumplings you have
                case 0:
                    break;
                case 1:
                    score += 1;
                    break;
                case 2:
                    score += 3;
                    break;
                case 3:
                    score += 6;
                    break;
                case 4:
                    score += 10;
                    break;
                default: //if they have 5 or more they get 15 points
                    score += 15;
                    break;
            }

            playerScores[i] = score;

            if(roundOver){
                //if the round is over, add totals together
                playerPuddings[i] += currRoundPuddings[i];
                roundTotalScores[i] += playerScores[i];
            }

        }

        //who has the most makis?
        int mostMakis = 0;
        int makiWinner = -1; //playerid of the player with the most makis
        for(int i = 0; i < numPlayers; i ++) {
            if(makiTotals[i] > mostMakis) {
                mostMakis = makiTotals[i];
                makiWinner = i;
            }
        }

        if(roundOver) {
            //TODO award points for makis... do this more elegantly? How to handle a tie??
            if (makiWinner > -1) {
                playerScores[makiWinner] += 6;
            }
        }

    }


    public int[] getScores() {
        return Arrays.copyOf(playerScores, playerScores.length);
    }

    public int[] getPlayerPuddings() {
        return Arrays.copyOf(playerPuddings, playerPuddings.length);
    }

    public int[] getCurrRoundPuddings() {
        return Arrays.copyOf(currRoundPuddings, currRoundPuddings.length);
    }

    public boolean isRoundOver() {
        return roundOver;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOverMessage(String message) {
    }

    public int getTurn()
    {
        return turnCount;
    }

    public int getCurrentRound() {
        return roundNumber;
    }
}





