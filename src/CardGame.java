import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CardGame {
    private static int noPlayers;
    public static void main(String[] args) {
        // Get number of players
        Scanner userInp = new Scanner(System.in); // Scanner to read inputs
        System.out.println("Please enter the number of players:");
        boolean validNoPlayers = false;
        while (!validNoPlayers) {
            try {
                noPlayers = Integer.parseUnsignedInt(userInp.nextLine());
                validNoPlayers = true;
            } catch (Exception e) {
                System.out.println("Invalid number of players, please enter a valid number:");         
            }
        }
        
        System.out.println("Please enter location of pack to load:");
        String packFile = userInp.nextLine();

        ArrayList<Card> pack = new ArrayList<>();
        boolean validPack = false;
        while (!validPack) {
            try {
                Scanner fileScanner = new Scanner(new File(packFile));
                while (fileScanner.hasNextLine()) {
                    //Parses unsigned int so that if a negative number is in the pack file it throws an exception
                    int cardVal = Integer.parseUnsignedInt(fileScanner.nextLine());
                    pack.add(new Card(cardVal));
                }
                if (pack.size() == 8*noPlayers) {
                    validPack = true;
                    fileScanner.close();
                    userInp.close();
                } else {
                    System.out.println("Invalid pack size, please enter location of a valid pack to load:");
                    packFile = userInp.nextLine();                    
                }
            } catch (Exception e) {
                System.out.println("Invalid pack name, please enter location of a valid pack to load:");
                packFile = userInp.nextLine();
            }
        }

        // Instantiating card decks
        ArrayList<CardDeck> decks = new ArrayList<>(); // List of decks, equal to no. of players
        for (int i=0; i<noPlayers; i++) {
            decks.add(new CardDeck());
        }

        // Instantiating players
        ArrayList<PlayerThread> players = new ArrayList<>(); // List of players
        for (int i=0; i<noPlayers; i++) {
            CardDeck left = decks.get(i); // Deck to the left and right of each player
            CardDeck right = decks.get((i+1) % noPlayers);
            players.add(new PlayerThread(left, right, players));
        }
        /*for (PlayerThread player : players) {
            player.setPlayers(players);
        }*/

        // Dealing the cards to players
        for (int i=0; i<4; i++) {
            for (int j=0; j<noPlayers; j++) {
                players.get(j).addCard(pack.get(i*noPlayers + j));
            }
        }

        // Dealing cards to decks
        for (int i=0; i<4; i++) {
            for (int j=0; j<noPlayers; j++) {
                decks.get(j).addCard(pack.get(i*noPlayers + j + 4*noPlayers));
            }
        }

        for (PlayerThread playerThread : players) {
            Thread thread = new Thread(playerThread);
            thread.start();
        }
    }
}