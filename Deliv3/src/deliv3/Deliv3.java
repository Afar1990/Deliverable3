/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package deliv3;

/**
 *
 * @author athif
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Card {
    private String rank;
    private String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class CardDeck {
    private List<Card> cards;

    public CardDeck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Random rand = new Random();
        for (int i = 0; i < cards.size(); i++) {
            int j = rand.nextInt(cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, temp);
        }
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        }
        return null;
    }
}

class Player {
    private String name;
    private List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void drawCard(Card card) {
        hand.add(card);
    }

    public Card playCard() {
        if (!hand.isEmpty()) {
            return hand.remove(0);
        }
        return null;
    }

    public boolean hasCards() {
        return !hand.isEmpty();
    }

    public String getName() {
        return name;
    }
}

class Game {
    private List<Player> players;
    private CardDeck deck;

    public Game(List<Player> players) {
        this.players = players;
        this.deck = new CardDeck();
    }

    public void startGame() {
        deck.shuffle();
        for (Player player : players) {
            player.drawCard(deck.drawCard());
        }
    }

    public void playRound() {
        List<Card> cardsOnTable = new ArrayList<>();
        for (Player player : players) {
            Card playedCard = player.playCard();
            if (playedCard != null) {
                cardsOnTable.add(playedCard);
            }
        }

        Card highestCard = cardsOnTable.get(0);
        List<Player> roundWinners = new ArrayList<>();

        for (int i = 1; i < cardsOnTable.size(); i++) {
            Card currentCard = cardsOnTable.get(i);
            int comparison = currentCard.getRank().compareTo(highestCard.getRank());

            if (comparison > 0) {
                highestCard = currentCard;
                roundWinners.clear();
                roundWinners.add(players.get(i));
            } else if (comparison == 0) {
                roundWinners.add(players.get(i));
            }
        }

        if (!roundWinners.isEmpty()) {
            System.out.print("Round winners: ");
            for (Player winner : roundWinners) {
                System.out.print(winner.getName() + " ");
                winner.drawCard(highestCard);
            }
            System.out.println("win(s) the round.");
            for (Player player : players) {
                player.drawCard(cardsOnTable.remove(0));
            }
        } else {
            System.out.println("It's a tie! War!");

            // Handle "war" scenario
            List<Card> warCards = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (Player player : players) {
                    Card warCard = player.playCard();
                    if (warCard != null) {
                        warCards.add(warCard);
                    }
                }
            }

            playWarRound(warCards);
        }
    }

    public void playWarRound(List<Card> warCards) {
        Card highestWarCard = warCards.get(0);
        List<Player> warRoundWinners = new ArrayList<>();

        for (int i = 1; i < warCards.size(); i++) {
            Card currentCard = warCards.get(i);
            int comparison = currentCard.getRank().compareTo(highestWarCard.getRank());

            if (comparison > 0) {
                highestWarCard = currentCard;
                warRoundWinners.clear();
                warRoundWinners.add(players.get(i % 2));
            } else if (comparison == 0) {
                warRoundWinners.add(players.get(i % 2));
            }
        }

        if (!warRoundWinners.isEmpty()) {
            System.out.print("War round winners: ");
            for (Player winner : warRoundWinners) {
                System.out.print(winner.getName() + " ");
                winner.drawCard(highestWarCard);
            }
            System.out.println("win(s) the war round.");
            for (Player player : players) {
                player.drawCard(warCards.remove(0));
            }
        } else {
            System.out.println("War round is a tie! War continues.");
            playWarRound(warCards);
        }
    }

    public boolean isGameOver() {
        for (Player player : players) {
            if (!player.hasCards()) {
                return true;
            }
        }
        return false;
    }

    public Player getGameWinner() {
        for (Player player : players) {
            if (player.hasCards()) {
                return player;
            }
        }
        return null;
    }
}

class Event {
    private String name;
    private String description;

    public Event(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

class PlayerEvent extends Event {
    private Player player;

    public PlayerEvent(String name, String description, Player player) {
        super(name, description);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

class WarEvent extends Event {
    private List<Player> players;
    private List<Card> cards;

    public WarEvent(String name, String description) {
        super(name, description);
        players = new ArrayList<>();
        cards = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCards() {
        return cards;
    }
}

class WarGameManager {
    private Game game;

    public WarGameManager(Game game) {
        this.game = game;
    }

    public void startWar(Player player1, Player player2, List<Card> cards) {
        WarEvent warEvent = new WarEvent("War Event", "A war has started!");
        warEvent.addPlayer(player1);
        warEvent.addPlayer(player2);
        warEvent.addCards(cards);
        System.out.println(warEvent.getName() + ": " + warEvent.getDescription());
        game.playWarRound(cards);
    }
}

class PlayerInput {
    private String playerName;

    public PlayerInput(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}

class HumanPlayerInput extends PlayerInput {
    private Scanner scanner;

    public HumanPlayerInput(String playerName) {
        super(playerName);
        scanner = new Scanner(System.in);
    }

    public Card playCard() {
        System.out.print(getPlayerName() + ", enter your card (e.g. '2 of Hearts'): ");
        String cardInput = scanner.nextLine();
        String[] parts = cardInput.split(" of ");
        if (parts.length != 2) {
            System.out.println("Invalid input format. Please try again.");
            return playCard();
        }
        return new Card(parts[0], parts[1]);
    }
}

public class WarGameWithInput {
    public static void main(String[] args) {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        Game game = new Game(players);
        game.startGame();

        WarGameManager warGameManager = new WarGameManager(game);

        int round = 1;
        while (!game.isGameOver()) {
            System.out.println("Round " + round);

            for (Player player : players) {
                System.out.println(player.getName() + ", it's your turn.");
                HumanPlayerInput input = new HumanPlayerInput(player.getName());
                Card playedCard = input.playCard();
                player.drawCard(playedCard);
            }

            game.playRound();

            if (!game.isGameOver()) {
                PlayerEvent roundWinnerEvent = new PlayerEvent("Round Winner Event", "A round winner has been determined.", game.getGameWinner());
                System.out.println(roundWinnerEvent.getName() + ": " + roundWinnerEvent.getDescription());
            } else {
                Player gameWinner = game.getGameWinner();
                System.out.println("Game winner: " + gameWinner.getName());

                Event gameWinnerEvent = new Event("Game Winner Event", "The game winner has been determined.");
                System.out.println(gameWinnerEvent.getName() + ": " + gameWinnerEvent.getDescription());
            }

            round++;
        }
    }
}
