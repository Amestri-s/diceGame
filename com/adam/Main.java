package com.adam;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        //Stop program from running if not done so in a terminal
        Console console = System.console();
        if (console == null) {
            System.out.println("No terminal open");
            return;
        }

        //Declare variables that will persist throughout running the game
        int playerOneScore = 0, playerTwoScore = 0, diceOne, diceTwo, diceThree, diceFour, diceTotal;
        String username, password, nickName, line;
        boolean paused = true;

        //Display the leaderboard as it stands
        displayLeaderboard();

        //Log in users
        for (int i = 1; i < 3; i++) {
            //Loop until user authenticated
            while (paused) {
                System.out.println("Player " + i + ". Enter your details.");
                username = console.readLine("Please enter your username:\n");
                password = console.readLine("Please enter your password:\n");
                try {
                    //Open file and check for details
                    BufferedReader reader = new BufferedReader(new FileReader("H:/accounts.txt"));
                    while ((line = reader.readLine()) != null) {
                        if (line.equals(username + " " + password)) {
                            paused = false;
                        }
                    }
                    if (!paused) {
                        System.out.println("Your details were authenticated. Welcome " + username);
                    } else {
                        System.out.println("Your details could not be authenticated. Try again.");
                    }
                    reader.close();
                } catch (FileNotFoundException e) {
                    //Exception handler
                    System.out.println("File not found!");
                    e.printStackTrace();
                } catch (IOException e){
                    //Exception handler
                    System.out.println("Could not read!");
                }
            }
            //Continue if user authenticated
            paused = true;
        }

        //After users are logged in, the game can begin
        for (int i = 1; i < 6; i++) {
            System.out.println("\n\nRound " + i + "\n\n");
            for (int j = 1; j < 3; j++) {
                System.out.println("----------------------------------------");
                System.out.println("Player " + j + ".\nIt is your turn.\n");
                //Roll die
                diceOne = (int)(Math.random()  * (6 - 1 + 1) + 1);
                diceTwo = (int)(Math.random()  * (6 - 1 + 1) + 1);
                diceThree = (int)(Math.random()  * (6 - 1 + 1) + 1);
                diceTotal = diceOne + diceTwo + diceThree;
                //Die results
                System.out.println("Dice one:\t" + diceOne + "\nDice two:\t" + diceTwo + "\nDice three:\t" + diceThree + "\nDice total:\t" + diceTotal + "\n");
                //Check if even
                if (diceTotal % 2 == 0) {
                    diceTotal = diceTotal + 10;
                    System.out.println("The total of all your rolls added up is even meaning you get an extra 10 points!\n");
                } else {
                    diceTotal = diceTotal - 5;
                    System.out.println("The total of all your rolls added up is odd meaning you loose 5 points!\n");
                }
                //Check if double roll
                if (diceOne == diceTwo || diceTwo == diceThree || diceOne == diceThree) {
                    diceFour = (int)(Math.random()  * (6 - 1 + 1) + 1);
                    diceTotal = diceTotal + diceFour;
                    System.out.println("Since you rolled double, you get to roll another dice!\n\nDice four:\t" + diceFour + "\n\nThis was added to your total!\n");
                }
                console.readLine();
                System.out.println("You end this round with " + diceTotal + " extra points.\n");
                //Make sure total doesn't drop below 0
                if (diceTotal < 0) {diceTotal = 0;}
                //Update score for player
                if (j == 1) {playerOneScore = playerOneScore + diceTotal;} else { playerTwoScore = playerTwoScore + diceTotal;}
                System.out.println("Scores\nPlayer One:\t" + playerOneScore + "\nPlayer Two:\t" + playerTwoScore);
                System.out.println("----------------------------------------");
                console.readLine();
            }
        }

        //Check to see if there is a tie
        if (playerOneScore == playerTwoScore) {
            //Handle tie
            System.out.println("There is a tie!\nTime for the tie breaker.\nYou will take turns to each roll a dice that will be added to your total.\nThe first person to get a higher total wins!\n");
            //Continue until there is a winner
            while (paused) {
                for (int i = 1; i < 3; i++) {
                    System.out.println("\nPlayer " + i + ".\nIt is your turn.\n");
                    //Roll dice
                    diceOne = (int)(Math.random()  * (6 - 1 + 1) + 1);
                    System.out.println("Dice:\t" + diceOne);
                    if (i == 1) {playerOneScore = playerOneScore + diceOne;} else {playerTwoScore = playerTwoScore + diceOne;}
                    System.out.println("Scores\nPlayer One:\t" + playerOneScore + "\nPlayer Two:\t" + playerTwoScore);
                }
                if (playerOneScore == playerTwoScore) {
                    paused = true;
                    System.out.println("We still do not have a winner!\nLet's continue.\n\n");
                } else {
                    paused = false;
                }
            }
        }

        //Determine the winner and save score
        if (playerOneScore > playerTwoScore) {
            System.out.println("Well done player one! You won with a total of " + playerOneScore + " points!");
            storeWinner(playerOneScore);
        } else {
            System.out.println("Well done player two! You won with a total of " + playerTwoScore + " points!");
            storeWinner(playerTwoScore);
        }

        //Display the leaderboard
        displayLeaderboard();
    }

    //Store score in file
    public static void storeWinner (int Score) {
        String nick = System.console().readLine("Please enter a nickname to store your score as:\n");
        if (nick.equals("")) {nick = "Anonymous";}
        try {
            //Append file with score details
            PrintWriter writer = new PrintWriter(new FileWriter("H:/scores.txt", true));
            writer.println((nick.replaceAll("\\s+", "_")) + " " + Score);
            writer.close();
            System.out.println("\nYour score has been saved!");
        } catch (IOException e) {
            //Exception handler
            System.out.println("Could not write to file.");
            e.printStackTrace();
        }
    }
    
    //Get the scores from the file and display top five
    public static void displayLeaderboard() {
        //Key, value
        HashMap<Integer, String> unSortedScores= new HashMap<Integer, String>();
        try {
            //Open file
            BufferedReader reader = new BufferedReader(new FileReader("H:/scores.txt"));
            String line, name = "";
            int score = 0;
            //Loop through scores
            while ((line = reader.readLine()) != null) {
                for (int i = 0; i<line.length(); i++) {
                    char chara = line.charAt(i);
                    if (chara == ' ') {
                        //Grab score and name
                        score = Integer.parseInt(line.substring(i+1));
                        name = line.substring(0, i);
                    }
                }
                //Put into hashmap
                unSortedScores.put(score, name);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            //Exception handler
            System.out.println("Could not find file");
            e.printStackTrace();
        } catch (IOException e) {
            //Exception handler
            System.out.println("Could not read file");
            e.printStackTrace();
        }

        //Assign tree map
        TreeMap<Integer, String> sortedScores = new TreeMap<>(unSortedScores);
        //Sort
        Set<Map.Entry<Integer, String>> mappings = sortedScores.entrySet();

        int position = unSortedScores.size();
        System.out.println("\n\tLeaderboard\n");
        for (Map.Entry<Integer, String> mapping : mappings) {
            //Loop through and display top 5
            if (position < 6) {
                System.out.println("Position " + position + "|\t" + mapping.getValue() + " - " + mapping.getKey());
            }
            position--;
        }
        System.out.println("\n");
        System.console().readLine();
    }
}
