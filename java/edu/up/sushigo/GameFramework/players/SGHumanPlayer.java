
package edu.up.sushigo.GameFramework.players;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.lang.Integer.min;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.PopupWindow;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

import edu.up.sushigo.GameFramework.GameMainActivity;
import edu.up.sushigo.GameFramework.LocalGame;
import edu.up.sushigo.GameFramework.ProxyGame;
import edu.up.sushigo.GameFramework.infoMessage.GameInfo;
import edu.up.sushigo.GameFramework.infoMessage.IllegalMoveInfo;
import edu.up.sushigo.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.sushigo.R;
import edu.up.sushigo.SGChopsticks;
import edu.up.sushigo.SGPlayCard;
import edu.up.sushigo.SGState;
import edu.up.sushigo.SGWasabi;

public abstract class SGHumanPlayer extends GameHumanPlayer implements View.OnClickListener {

    //instance variables

    //the android activity we are running
    private GameMainActivity myActivity;

    //TODO private SGSurfaceView surfaceView;

    //the ID for the layout to use
    private int layoutId;

    private SGState myCopySGState;

    private int humanHandOffset;
    private int currentHandSize;

    private Button rulesButton;

    private Button readyButton;

    private Button chopsticksButton;

    private Button wasabiButton;

    //make an arraylist of arraylists for all players
    //change opponent hands to image buttons?? or just make arraylist of views...?
    //ArrayList<> stringList = new ArrayList<String>();
    private ArrayList<ArrayList<View>> playerPlayedCardsViews;

    private ArrayList<View> humanPlayerHandViews;

    private TextView humanScoreText;

    private ArrayList<TextView> oppScoreText;

    private int numPlayers;

    private String name;

    private HashMap<String, Integer> cards;

    //typecast to imagebutton or imageview

    private int[] playerScores;
    private String [] playerNames;

    private boolean[] cardUsed = new boolean[5];

    private int playerId;
    /**
     * constructor
     *
     * @param name the name of the player
     *
     * @param layoutId the id of the layout to use
     */
    public SGHumanPlayer(String name, int layoutId) {
        super(name);
        this.layoutId = layoutId;
        humanHandOffset = 0;
        numPlayers = 5;

        // ALL OF THE CARDS, AND THEIR CORRELATED DRAWABLE
        cards = new HashMap<>();

        cards.put("UNKNOWN", R.drawable.sushiflippedcard);
        cards.put("EMPTY", R.drawable.sushiempty);
        cards.put("Sashimi", R.drawable.sashimi);
        cards.put("Tempura", R.drawable.tempura);
        cards.put("Dumpling", R.drawable.dumpling);
        cards.put("Maki 1", R.drawable.maki1);
        cards.put("Maki 2", R.drawable.maki2);
        cards.put("Maki 3", R.drawable.maki3);
        cards.put("Salmon Nigiri", R.drawable.salmonnigiri);
        cards.put("Egg Nigiri", R.drawable.eggnigiri);
        cards.put("Squid Nigiri", R.drawable.squidnigiri);
        cards.put("Pudding", R.drawable.pudding);
        cards.put("Wasabi", R.drawable.wasabi);
        cards.put("Chopsticks", R.drawable.chopsticks);
        cards.put("Wasabi Egg Nigiri", R.drawable.wasabieggnigiri);
        cards.put("Wasabi Salmon Nigiri", R.drawable.wasabisalmonnigiri);
        cards.put("Wasabi Squid Nigiri", R.drawable.wasabisquidnigiri);
        cards.put("Used Wasabi", R.drawable.usedwasabi);

        playerNames = new String[numPlayers];

        playerId=-1;
    }

    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.main_layout);
    }

    @Override
    public void receiveInfo(GameInfo info) {

        if(game instanceof LocalGame) {
            LocalGame bob = (LocalGame) game;
            playerId = bob.getPlayerId(this); //who is playing
        }
        else if(game instanceof ProxyGame) {
            ProxyGame bob = (ProxyGame) game;
            playerId = bob.getPlayerId(this); //who is playing
        }

        //update the view
        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {

            //TODO if the move was out of turn or otherwise illegal, flash the screen
        } else if (!(info instanceof SGState))
            // if we do not have a SGState, ignore
            return;
        else {

            // MAKE A MAIN LOOP THAT UPDATES THE GUI FOR EVERY CARD FOR EVERY HAND

            //this is where to update all of the GUI based on the state
            myCopySGState = (SGState) info;

            playerScores = Arrays.copyOf(myCopySGState.getPlayerScores(), myCopySGState.getPlayerScores().length);

            int[] playerTotalScores = new int[numPlayers];
            for(int i = 0; i < numPlayers; i ++){
                playerTotalScores[i] = (playerScores[i] + myCopySGState.getRoundTotalScores()[i]);
            }
            if(playerTotalScores[0] == playerScores[0]){
                for(int i = 0; i < numPlayers; i ++){
                    playerTotalScores[i] = (playerScores[i]);
                }
            }


            if (playerScores != null) {
                humanScoreText.setText("Round Number: " + myCopySGState.getRoundNumber() + "/3  My Cards: Current Score = " + playerTotalScores[0] + "   Puddings: " + (myCopySGState.getPlayerPuddings()[0] + myCopySGState.getCurrRoundPuddings()[0]));

                for (int i = 0; i < min(oppScoreText.size(), myCopySGState.getNumPlayers() - 1); i++) {
                    oppScoreText.get(i).setText(playerNames[i] + ": Current Score = " + playerTotalScores[i+1] + "   Puddings: " + (myCopySGState.getPlayerPuddings()[i + 1] + myCopySGState.getCurrRoundPuddings()[i + 1]));
                }

            }

            //when the human player can press ready, enable the ready button. otherwise disable it.
            readyButton.setEnabled(false);
            //readyButton.setBackgroundColor(5);
            if (myCopySGState.getReadyPlayers()[playerId]) {
                readyButton.setEnabled(true);
            }
        }

        //-----
        currentHandSize = myCopySGState.getHand(playerId).length;// TODO: IS HUMAN PLAYER 0?

        // Played Cards for only for the user
        String[] hand = myCopySGState.getPlayedCards(playerId);
        for(int j = 0; j < 5; ++j) {
            int handIndex = j + humanHandOffset;

            //check: invalid handIndex
            if (handIndex >= hand.length) break;

            String theCard = hand[handIndex]; //card in my hand to draw on screen

            //draw it on the current imageButton
            ImageButton curr = (ImageButton) playerPlayedCardsViews.get(0).get(j);
            if(curr != null && theCard != null)
            {
                curr.setImageResource(cards.get(theCard));
            }
            else {
                // curr.setVisibility(INVISIBLE);
                curr.setImageResource(cards.get("EMPTY"));
            }
        }

        int otherPlayerNum = 1;
        // All the other players PLATES, goes from 1-Number of players
        for (int i = 0; i < myCopySGState.getNumPlayers(); i++) {
            if(i == playerId)
            {
                continue;
            }
            hand = myCopySGState.getPlayedCards(i);
            for(int j = 0; j < 5; ++j) {
                int handIndex = j + humanHandOffset;

                //check: invalid handIndex
                if (handIndex >= hand.length) break;

                String theCard = hand[handIndex]; //card in my hand to draw on screen

                //draw it on the current imageButton
                ImageView curr = (ImageView) playerPlayedCardsViews.get(otherPlayerNum).get(j);
                if(curr != null && theCard != null)
                {
                    curr.setImageResource(cards.get(theCard));
                }
                else {
                    // curr.setVisibility(INVISIBLE);
                    curr.setImageResource(cards.get("EMPTY"));
                }
            }
            otherPlayerNum++;
        }


        // Every round, the selectable cards are populated to have 5 cards to choose from, instead of decrementing every time

        hand = myCopySGState.getHand(playerId); //human player num?
        for(int j=0; j < humanPlayerHandViews.size(); j++)
        {
            // Updates the selectable cards
            // RANGE

            String theCard = hand[j + humanHandOffset]; //card in my hand to draw on screen
            ImageButton curr = (ImageButton) humanPlayerHandViews.get(j);
            curr.setImageResource(cards.get(theCard));

        }

    }


    public void setName(int playerIndex, String newName) {

        // EVERYONE EXCEPT FOR THE USER
        playerNames[playerIndex] = newName;

    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        myActivity = activity;
        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);
        // save the activity reference so popup code can use it
        this.myActivity = activity;
        rulesButton = activity.findViewById(R.id.buttonRules);
        rulesButton.setOnClickListener(this);

        //find textview that displays scores
        humanScoreText = activity.findViewById(R.id.myCards);
        oppScoreText = new ArrayList<TextView>();

        oppScoreText.add(activity.findViewById(R.id.opponent1));
        oppScoreText.add(activity.findViewById(R.id.opponent2));
        oppScoreText.add(activity.findViewById(R.id.opponent3));
        oppScoreText.add(activity.findViewById(R.id.opponent4));


        //find ready button
        readyButton = activity.findViewById(R.id.buttonReady);

        readyButton.setOnClickListener(this);


        //find chopsticks button
        chopsticksButton = activity.findViewById(R.id.buttonChopsticks);
        chopsticksButton.setOnClickListener(this);

        //find wasabi button
        wasabiButton = activity.findViewById(R.id.buttonWasabi);
        wasabiButton.setOnClickListener(this);

        //find left scroll button
        activity.findViewById(R.id.buttonLeft).setOnClickListener(this);

        //find right scroll button
        activity.findViewById(R.id.buttonRight).setOnClickListener(this);

        // Create a new array list for all players
        playerPlayedCardsViews = new ArrayList<ArrayList<View>>();
        for(int i = 0; i < numPlayers; i++)
        {
            // Adds 5 total views to player hand
            playerPlayedCardsViews.add(new ArrayList<View>());
        }

        // First ArrayList inside of List -> Index = 0
        // playCard is the selection of cards to choose from
        humanPlayerHandViews = new ArrayList<View>();

        ImageButton view11 = activity.findViewById(R.id.handCard1);
        humanPlayerHandViews.add(view11);

        ImageButton view22 = activity.findViewById(R.id.handCard2);
        humanPlayerHandViews.add(view22);

        ImageButton view33 = activity.findViewById(R.id.handCard3);
        humanPlayerHandViews.add(view33);

        ImageButton view44 = activity.findViewById(R.id.handCard4);
        humanPlayerHandViews.add(view44);

        ImageButton view55 = activity.findViewById(R.id.handCard5);
        humanPlayerHandViews.add(view55);

        //add onclicklistener to all imagebuttons in players hand:
        for(int i = 0; i < humanPlayerHandViews.size(); i++) {
            humanPlayerHandViews.get(i).setOnClickListener(this);
        }

        //TODO add onclicklistener to the left and right scroll buttons


        // My Selectable cards

        ImageButton view1 = activity.findViewById(R.id.myCard1);
        playerPlayedCardsViews.get(0).add(view1);

        ImageButton view2 = activity.findViewById(R.id.myCard2);
        playerPlayedCardsViews.get(0).add(view2);

        ImageButton view3 = activity.findViewById(R.id.myCard3);
        playerPlayedCardsViews.get(0).add(view3);

        ImageButton view4 = activity.findViewById(R.id.myCard4);
        playerPlayedCardsViews.get(0).add(view4);

        ImageButton view5 = activity.findViewById(R.id.myCard5);
        playerPlayedCardsViews.get(0).add(view5);

        //TODO add all the player --> All ImageView

        // Player 2 - > Technically called "player 1" in the GUI //TODO the human player is player zero. this IS player 1.

        ImageView player21 = activity.findViewById(R.id.p1C5);
        playerPlayedCardsViews.get(1).add(player21);

        ImageView player22 = activity.findViewById(R.id.p1C4);
        playerPlayedCardsViews.get(1).add(player22);

        ImageView player23 = activity.findViewById(R.id.p1C3);
        playerPlayedCardsViews.get(1).add(player23);

        ImageView player24 = activity.findViewById(R.id.p1C2);
        playerPlayedCardsViews.get(1).add(player24);

        ImageView player25 = activity.findViewById(R.id.p1C1);
        playerPlayedCardsViews.get(1).add(player25);


        // Player 3

        ImageView player31 = activity.findViewById(R.id.p2C5);
        playerPlayedCardsViews.get(2).add(player31);

        ImageView player32 = activity.findViewById(R.id.p2C4);
        playerPlayedCardsViews.get(2).add(player32);

        ImageView player33 = activity.findViewById(R.id.p2C3);
        playerPlayedCardsViews.get(2).add(player33);

        ImageView player34 = activity.findViewById(R.id.p2C2);
        playerPlayedCardsViews.get(2).add(player34);

        ImageView player35 = activity.findViewById(R.id.p2C1);
        playerPlayedCardsViews.get(2).add(player35);

        // Player 4

        ImageView player41 = activity.findViewById(R.id.p3C5);
        playerPlayedCardsViews.get(3).add(player41);

        ImageView player42 = activity.findViewById(R.id.p3C4);
        playerPlayedCardsViews.get(3).add(player42);

        ImageView player43 = activity.findViewById(R.id.p3C3);
        playerPlayedCardsViews.get(3).add(player43);

        ImageView player44 = activity.findViewById(R.id.p3C2);
        playerPlayedCardsViews.get(3).add(player44);

        ImageView player45 = activity.findViewById(R.id.p3C1);
        playerPlayedCardsViews.get(3).add(player45);

        // Player 5

        ImageView player51 = activity.findViewById(R.id.p4C5);
        playerPlayedCardsViews.get(4).add(player51);

        ImageView player52 = activity.findViewById(R.id.p4C4);
        playerPlayedCardsViews.get(4).add(player52);

        ImageView player53 = activity.findViewById(R.id.p4C3);
        playerPlayedCardsViews.get(4).add(player53);

        ImageView player54 = activity.findViewById(R.id.p4C2);
        playerPlayedCardsViews.get(4).add(player54);

        ImageView player55 = activity.findViewById(R.id.p4C1);
        playerPlayedCardsViews.get(4).add(player55);

    }

    @Override
    public void onClick(View v) {
        // handle Rules button first (must match method name exactly)
        if (v != null && v.getId() == R.id.buttonRules) {
            showRulesPopup();
            return;
            }
        //check what view- all buttons share onclick
        int id = v.getId();


        if(myCopySGState == null){
            return; //if copy state hasnt been set yet abort
        }



        //handle hand card clicks
        for (int i = 0; i < humanPlayerHandViews.size(); i++) {
            if(v.equals(humanPlayerHandViews.get(i))) {
                cardUsed[i] = true;
                game.sendAction(new SGPlayCard(this, myCopySGState.getHand(playerId)[i+humanHandOffset]));
                return;
            }
        }

        //chopsticks button:
        if (id == R.id.buttonChopsticks) {
            game.sendAction(new SGChopsticks(this));
        }

        //wasabi button:
        if (id == R.id.buttonWasabi) {
            game.sendAction(new SGWasabi(this));
        }


        // Handle ready button
        if (id == R.id.buttonReady) {
            if (game != null) {
                game.sendAction(new SGReadyAction(this));
            }
            return;
        }
        // Handle scroll buttons
        if (id == R.id.buttonRight) {
            if (humanHandOffset < currentHandSize - 5) {
                humanHandOffset++;
                game.sendAction(new SGScrollHandAction(this, humanHandOffset));
            }
            return;
        }

        if (id == R.id.buttonLeft) {
            if (humanHandOffset > 0) {
                humanHandOffset--;
                game.sendAction(new SGScrollHandAction(this, humanHandOffset));
            }
            return;
        }

        // Handle hand card clicks
        for (int i = 0; i < humanPlayerHandViews.size(); i++) {
            if (v == humanPlayerHandViews.get(i)) {
                int cardIndex = i + humanHandOffset;

                if (myCopySGState != null && cardIndex < myCopySGState.getHand(playerId).length) {
                    String chosenCard = myCopySGState.getHand(playerId)[cardIndex];

                    // Send play card action
                    game.sendAction(new SGPlayCardAction(this, chosenCard, cardIndex));
                }
                return;
            }
        }

//        // Handle special card buttons
//        if (id == R.id.buttonWasabi) {
//            if (myCopySGState != null) {
//                game.sendAction(new SGPlayCardAction(this, "Wasabi", -1));
//            }
//            return;
//        }
    }

    private void showRulesPopup() {
        if (myActivity == null) {
            return; // safety — shouldn't ever happen
        }

        // Inflate rules_popup.xml
        View popupView = myActivity.getLayoutInflater().inflate(R.layout.rules_popup, null);

        // Create the popup window
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Allow dismiss on outside tap
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Close button inside popup
        Button closeBtn = popupView.findViewById(R.id.closeRules);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Show centered on the main layout
        View parent = myActivity.findViewById(R.id.main_layout);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
}


//TODO reciept endgame screen
