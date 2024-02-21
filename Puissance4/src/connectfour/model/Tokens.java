package connectfour.model;

import connectfour.view.ConsoleGameView;
import jcurses.system.CharColor;

public enum Tokens {
	
	RED('X', ConsoleGameView.ANSI_RED, CharColor.RED), BLUE('O', ConsoleGameView.ANSI_CYAN, CharColor.BLUE);
	
	private final char symbol;
	private final String color;

	private final short jcursesColor;
	
	private Tokens(char symbol, String color, short jcursesColor) {
		this.symbol = symbol;
		this.color = color;
		this.jcursesColor = jcursesColor;
	}

	public char getSymbol() {
		return symbol;
	}

	public short getJcursesColor() {
		return jcursesColor;
	}

	@Override
	public String toString() {
		return color + symbol + ConsoleGameView.ANSI_RESET;
	}
}
