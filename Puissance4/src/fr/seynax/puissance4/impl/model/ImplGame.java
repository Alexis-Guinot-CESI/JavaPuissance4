package fr.seynax.puissance4.impl.model;

import fr.seynax.puissance4.impl.model.grid.GridList;
import fr.seynax.puissance4.api.model.Game;
import fr.seynax.puissance4.api.model.Grid;
import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.core.exception.Tokens;

import java.util.ArrayList;
import java.util.List;

public class ImplGame implements Game
{
	private final Grid grid;
	
	public Tokens currentPlayer;
	
	static private final Tokens[] TOKEN_VALUES = Tokens.values();
	
	private boolean Over;

	private List<TokenPosition> winPositions;

	private Tokens winner;
	
	public ImplGame() {
		grid = new GridList(Game.COLUMNS,Game.ROWS);
		winPositions = new ArrayList<>();
		init();
	}

	@Override
	public boolean isAvailable(final int x)
	{
		return grid.isAvailable(x);
	}

	@Override
	public Tokens getToken(int x, int y) throws ConnectException
	{
		return this.grid.getToken(x,y);
	}
	
	@Override
	public void init() {
		winPositions.clear();
		this.Over = false;
		this.winner = null;
		this.grid.init();
		this.currentPlayer = TOKEN_VALUES[(int)(Math.random() * TOKEN_VALUES.length)];
	}

	@Override
	public void putToken(int x) throws ConnectException {
		this.grid.putToken(this.getCurrentPlayer(), x);	
		this.Over = this.calculateOver(x);
		this.currentPlayer = this.getNextPlayer();
	}

	@Override
	public Tokens getCurrentPlayer() {
		// TODO Auto-generated method stub
		return currentPlayer;
	}

	@Override
	public boolean isOver() {
		return Over;
	}

	private boolean calculateOver(int x) throws ConnectException {
		if (inspectDiagonaleNWSE(x, grid.getRowOfLastPutToken()) >= Game.REQUIRED_TOKENS || inspectDiagonaleNESW(x, grid.getRowOfLastPutToken()) >= Game.REQUIRED_TOKENS )  {
			this.winner = getCurrentPlayer();
			return true;
		}
		
		if (inspectVerticalS(x, grid.getRowOfLastPutToken()) >= Game.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}
		
		if (inspectHorizontalW(x, grid.getRowOfLastPutToken()) >= Game.REQUIRED_TOKENS || inspectHorizontalE(x, grid.getRowOfLastPutToken())>= Game.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}

		winPositions.clear();
		for (int column = 0 ; column < Game.COLUMNS ; column ++) {
				for (int row = 0 ; row < Game.ROWS ; row ++) {
					if (grid.getToken(column, row) == null) {
						return false;
					}
				}
		}
		return true;
	}
	
	private int inspectDiagonaleNWSE(int x,int y) throws ConnectException {
		winPositions.clear();
		winPositions.add(new TokenPosition(x, y, this.currentPlayer));
		int foundInLine = 0;
		 for (int i = 1; x - i >= 0 && y + i < ROWS && getToken(x - i, y + i) == currentPlayer; i++) {
			 winPositions.add(new TokenPosition(x - i, y + i, this.currentPlayer));
			 foundInLine++;
		 }
		 for (int i = 1; x + i < COLUMNS && y - i >= 0 && getToken(x + i, y - i) == currentPlayer; i++) {
			 winPositions.add(new TokenPosition(x + i, y - i, this.currentPlayer));
			 foundInLine++;
		 }
		 return foundInLine + 1;
	}
	
	private int inspectDiagonaleNESW(int x,int y) throws ConnectException {
		winPositions.clear();
		winPositions.add(new TokenPosition(x, y, this.currentPlayer));
		int foundInLine = 0;
		 for (int i = 1; x - i >= 0 && y - i >= 0 && getToken(x - i, y - i) == currentPlayer; i++) {
			 winPositions.add(new TokenPosition(x - i, y - i, this.currentPlayer));
			 foundInLine++;
		 }
		 for (int i = 1; x + i <= COLUMNS && y + i < ROWS && getToken(x + i, y + i) == currentPlayer; i++) {
			 winPositions.add(new TokenPosition(x + i, y + i, this.currentPlayer));
			 foundInLine++;
		 }
		 return foundInLine + 1;
	}
	
	private int inspectVerticalS(int x, int y) throws ConnectException {
		winPositions.clear();
		int foundInLine = 0;
		for (int i = y; i >= 0 ; i--) {
			if (getToken(x, i) != currentPlayer) {
				break;
			}
			winPositions.add(new TokenPosition(x, i, this.currentPlayer));
			foundInLine++;
		}
		return foundInLine;
	}
	
	private int inspectHorizontalE(int x, int y) throws ConnectException {
		winPositions.clear();
		int foundInLine = 0;
		for (int i = x; i < COLUMNS; i++) {
			if (getToken(i, y) != currentPlayer) {
				break;
			}
			winPositions.add(new TokenPosition(i, y, this.currentPlayer));
			foundInLine++;
		}
		return foundInLine;
	}
	
	private int inspectHorizontalW(int x, int y) throws ConnectException {
		winPositions.clear();
		int foundInLine = 0;
		for (int i = x; i >= 0 ; i-- ) {
			if (getToken(i, y) != currentPlayer) {
				break;
			}
			winPositions.add(new TokenPosition(x, i, this.currentPlayer));
			foundInLine++;
		}
		return foundInLine;
	}
	
	@Override
	public Tokens getWinner() {
		// TODO Auto-generated method stub
		return winner;
	}
	
	
	private Tokens getNextPlayer() {
		int indice = (currentPlayer.ordinal()+ 1)%TOKEN_VALUES.length;
		return TOKEN_VALUES[indice];
	}

	public List<TokenPosition> getWinPositions() {
		return winPositions;
	}
}