package fr.seynax.puissance4.impl.view;

import fr.seynax.puissance4.api.model.IGridGameplay;
import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.core.Tokens;
import fr.seynax.puissance4.api.model.IGame;
import jcurses.system.CharColor;
import jcurses.system.Toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JCursesGameView implements IGame
{
	// ATTRIBUTES

	private final IGridGameplay game;

	private String userInput;
	private String errorMessage;
	private boolean exitRequest;
	private boolean restartRequest;
	static final String KEYWORD_EXIT = "exit";
	static final String KEYWORD_RESTART = "restart";

	private final CharColor charColor;
	private int y;
	private int x;
	private int tokenPosition;
	private int lastTokenPosition;
	private boolean redraw;
	private final List<String> messages;

	// CONSTRUCTOR

	public JCursesGameView(IGridGameplay game) {
		if (game == null) {
			throw new IllegalArgumentException("game ne peut �tre null");
		}
		this.game = game;
		Toolkit.init();
		this.y = 0;
		this.x = 0;
		tokenPosition = (int) (IGridGameplay.COLUMNS / 2);
		this.messages = new ArrayList<>();
		this.charColor = new CharColor(CharColor.BLACK, CharColor.WHITE);
		redraw = true;
	}

	// METHODS

	public void redraw() throws ConnectException {
		clearConsole();
		displayGrid();
		displayGameState();
		displayMessages();
	}

	@Override
	public void play() {
		try {
			if(redraw) {
				redraw();
				redraw = false;
			}

			var inputChar = Toolkit.readCharacter();
			errorMessage = null;
			switch(inputChar.getCode()) {
				case 10, 258:
					putToken();
					break;
                case 260:
					tokenPosition = Math.max(0, tokenPosition-1);
					this.displayToken(tokenPosition, 2);
					break;
				case 261:
					tokenPosition = (tokenPosition+1) % IGridGameplay.COLUMNS;
					this.displayToken(tokenPosition, 2);
					break;
				case 27:
					exitRequest = true;
					break;
				case 114:
					restartRequest = true;
					break;
				default:
					userInput = "" + inputChar.getCharacter();
					if (userInput.matches("[0-9]+")) {
						if (!game.isOver()) {
							try {
								this.tokenPosition = Integer.parseInt(userInput);
								putToken();
							} catch (NumberFormatException e) {
								errorMessage = "Je n'ai pas compris cette réponse... " + e.getMessage();
							} catch (ConnectException e) {
								errorMessage = e.getMessage();
							}
						}
					} else {
						print("" + inputChar.getCode());
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ex) {
							throw new RuntimeException(ex);
						}
					}
			}
			if (errorMessage != null) {
				messages.addAll(Arrays.asList(errorMessage.split("\n")));
				redraw = true;
			}
		} catch (Exception e) {
			messages.addAll(Arrays.asList(e.getMessage().split("\n")));
			redraw = true;
		}

		if (restartRequest) {
			game.init();
			restartRequest = false;
			redraw = true;
			tokenPosition = (int) (IGridGameplay.COLUMNS / 2);
		}

		if (!exitRequest) {
			play();
		}
		else {
			clearConsole();
			printLn("EXIT_SUCCESS", CharColor.GREEN);
		}
	}
	
	// TOOLS


	private void putToken() throws ConnectException {
		if(game.isOver()) {
			return;
		}
		short lastForegroundColor = charColor.getForeground();
		var token = game.getCurrentPlayer();
		this.charColor.setForeground(token.getJcursesColor());
		for(int i = 0; i < IGridGameplay.ROWS; i ++) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

			if(game.getToken(tokenPosition, (IGridGameplay.ROWS-1) - i) != null) {
				break;
			}

			clearPosition(this.tokenPosition * 6 + 3, Math.max((i - 1) * 3 + 4, 2));
			Toolkit.printString("" + token.getSymbol(), this.tokenPosition * 6 + 3, i * 3 + 4, this.charColor);
		}
		this.charColor.setForeground(lastForegroundColor);
		game.putToken(this.tokenPosition);
		redraw = true;
		tokenPosition = (int) (IGridGameplay.COLUMNS / 2);
	}

	private void displayGameState() {
		if (game.isOver()) {
			Tokens winner = game.getWinner();
			if (winner == null) {
				printLn("La partie s'est terminee sur un match nul.", CharColor.GREEN);
			} else {
				print("La partie a ete remportee par ", CharColor.GREEN);
				print("" + winner.getSymbol(), winner.getJcursesColor());
				printLn(" !", CharColor.GREEN);
			}
		} else {
			print("C'est au tour de [");
			print("" + game.getCurrentPlayer().getSymbol(), game.getCurrentPlayer().getJcursesColor());
			printLn("] ! [0-" + (IGridGameplay.COLUMNS - 1) + "] : ");
		}
	}

	private void displayToken(int tokenPosition, int y) {
		var lastY = this.y;
		if(y >= 0) {
			this.y = y;
		}
		var token = game.getCurrentPlayer();
		clearPosition(lastTokenPosition * 6 + 3, this.y);
		printLn(" ".repeat(tokenPosition * 6 + 3) + token.getSymbol(), token.getJcursesColor());
		this.lastTokenPosition = tokenPosition;
		if(y >= 0) {
			this.y = lastY;
		}
	}

	private void displayGrid() throws ConnectException {
		printLn("");
		Tokens token;
		for (int x = 0; x < IGridGameplay.COLUMNS; x++) {
			print("   " + x + "  ");
		}
		printLn("");
		displayToken(tokenPosition, -1);

		for (int y = IGridGameplay.ROWS - 1; y >= 0; y--) {
			for (int x = 0; x < IGridGameplay.COLUMNS; x++) {
				print("|     ");
			}
			printLn("|");
			for (int x = 0; x < IGridGameplay.COLUMNS; x++) {
				print("|  ");
				token = game.getToken(x, y);
				if (token == null) {
					print(" ");
				} else {
					var color = token.getJcursesColor();
					for(var winPosition : game.getWinPositions()) {
						if(winPosition.getX() == x && winPosition.getY() == y) {
							color = CharColor.YELLOW;
						}
					}

					print("" + token.getSymbol(), color);
				}
				print("  ");
			}
			printLn("|");
			for (int x = 0; x < IGridGameplay.COLUMNS; x++) {
				print("|_____");
			}
			printLn("|");
		}
	}

	private void print(String message) {
		if(this.y > Toolkit.getScreenHeight()) {
			return;
		}
		print(message, this.charColor.getForeground());
	}

	private void print(String message, short textColor) {
		var lastForeground = this.charColor.getForeground();
		this.charColor.setForeground(textColor);
		Toolkit.printString(message, this.x, this.y, this.charColor);
		this.x+=message.length();
		this.charColor.setForeground(lastForeground);
	}

	private void printLn(String message) {
		printLn(message, this.charColor.getForeground());
	}

	private void printLn(String message, short textColor) {
		for (var line : message.split("\n")) {
			if(this.y > Toolkit.getScreenHeight()) {
				return;
			}
			var lastForeground = this.charColor.getForeground();
			this.charColor.setForeground(textColor);
			Toolkit.printString(line, this.x, this.y++, this.charColor);
			this.charColor.setForeground(lastForeground);
			this.x = 0;
		}
	}

	private void clearConsole() {
		Toolkit.clearScreen(this.charColor);
		this.y = 0;
	}

	public void displayMessages() {
		for(var message : messages) {
			printLn(">" + message, CharColor.RED);
		}
		if(y >= Toolkit.getScreenHeight()) {
			messages.clear();
		}
		for(int i = y; i < Toolkit.getScreenHeight(); i ++) {
			printLn(">", CharColor.RED);
		}
	}

	public void clearPosition(int x, int y) {
		Toolkit.printString(" ", x, y, this.charColor);
	}
}
