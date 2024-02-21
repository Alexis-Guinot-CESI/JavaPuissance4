package fr.seynax.puissance4.impl.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.api.model.Game;
import fr.seynax.puissance4.core.exception.Tokens;
import fr.seynax.puissance4.api.view.GameView;

public class ConsoleGameView implements GameView
{
	// COLORS

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	
	// ATTRIBUTES
	
	private final Game game;
	
	private final BufferedReader reader;
	private String userInput;
	private String errorMessage;
	private boolean exitRequest;
	private boolean restartRequest;
	static final String KEYWORD_EXIT = "exit";
	static final String KEYWORD_RESTART = "restart";
	
	
	// CONSTRUCTOR
	
	public ConsoleGameView(Game game) {
		if (game == null) {
			throw new IllegalArgumentException("game ne peut �tre null");
		}
		this.game = game;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	// METHODS

	@Override
	public void play() {
		clearConsole();
		try {
			displayGrid();
			do {
				errorMessage = null;
				displayGameState();
				userInput = reader.readLine();
				if (userInput.contentEquals(KEYWORD_EXIT)) {
					exitRequest = true;
				}
				else if (userInput.contentEquals(KEYWORD_RESTART)) {
					restartRequest = true;
				}
				else if (!game.isOver()) {
					try {
						int column = Integer.parseInt(userInput);
						game.putToken(column);
						clearConsole();
					} catch (NumberFormatException e) {
						errorMessage = "Je n'ai pas compris cette réponse...";
					}
					catch (ConnectException e) {
						errorMessage = e.getMessage();
					}
				} else {
					errorMessage = "Je n'ai pas compris cette réponse...";
				}
				if (errorMessage != null) {
					System.err.println(errorMessage);
				}
			} while (errorMessage != null);
		} catch (Exception e) {
			e.printStackTrace();
			exitRequest = false;
		}
		if (restartRequest) {
			game.init();
			restartRequest = false;
		}
		if (!exitRequest) {
			play();
		}
		else {
			clearConsole();
			System.out.println(ANSI_GREEN + "EXIT_SUCCESS"+ ANSI_RESET);
		}
	}
	
	// TOOLS
	
	private void displayGameState() {
		if (game.isOver()) {
			Tokens winner = game.getWinner();
			if (winner == null) {
				System.out.println("La partie s'est terminée sur un match nul.");
			} else {
				System.out.println("La partie a été remportée par " + winner + " !");
			}
		} else {
			System.out.print("C'est au tour de [" + game.getCurrentPlayer() + "] ! [0-" + (Game.COLUMNS - 1) + "] : ");
		}
	}
	
	private void displayGrid() throws ConnectException {
		StringBuffer output = new StringBuffer();
		Tokens token;
		for (int x = 0; x < Game.COLUMNS; x++) {
			output.append("   " + x + "  ");
		}
		output.append('\n');
		for (int y = Game.ROWS - 1; y >= 0; y--) {
			for (int x = 0; x < Game.COLUMNS; x++) {
				output.append("|     ");
			}
			output.append("|\n");
			for (int x = 0; x < Game.COLUMNS; x++) {
				output.append("|  ");
				token = game.getToken(x, y);
				if (token == null) {
					output.append(' ');
				} else {
					output.append(token);
				}
				output.append("  ");
			}
			output.append("|\n");
			for (int x = 0; x < Game.COLUMNS; x++) {
				output.append("|_____");
			}
			output.append("|\n");
		}
		output.append("exit  (=sortir du jeu)\nrestart (=redémarre le jeu)\n");
		System.out.println(output.toString());
	}
	
	private void clearConsole() {
		for (int i = 0; i < 50; i++) {
			System.out.println();
		}
	}
}