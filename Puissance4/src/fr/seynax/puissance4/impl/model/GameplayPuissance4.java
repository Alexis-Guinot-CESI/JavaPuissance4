package fr.seynax.puissance4.impl.model;

import fr.seynax.puissance4.api.model.IGrid;
import fr.seynax.puissance4.impl.model.grid.GridList;
import fr.seynax.puissance4.api.model.IGridGameplay;
import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.core.Tokens;

import java.util.ArrayList;
import java.util.List;

public class GameplayPuissance4 implements IGridGameplay
{
	private final IGrid grid;
	
	public Tokens currentPlayer;
	
	static private final Tokens[] TOKEN_VALUES = Tokens.values();
	
	private boolean over;

	private List<TokenPosition> winPositions;

	private Tokens winner;
	
	public GameplayPuissance4() {
		grid = new GridList(IGridGameplay.COLUMNS, IGridGameplay.ROWS);
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
		this.over = false;
		this.winner = null;
		this.grid.init();
		this.currentPlayer = TOKEN_VALUES[(int)(Math.random() * TOKEN_VALUES.length)];
	}

	@Override
	public void putToken(int x) throws ConnectException {
		this.grid.putToken(this.getCurrentPlayer(), x);	
		this.over = this.calculateOver(x);
		this.currentPlayer = this.getNextPlayer();
	}

	@Override
	public Tokens getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public boolean isOver() {
		return over;
	}

	private boolean calculateOver(int x) throws ConnectException {
		if (inspectLeftDiagonal(x, grid.getRowOfLastPutToken()) >= IGridGameplay.REQUIRED_TOKENS)  {
			this.winner = getCurrentPlayer();
			return true;
		}

		if(inspectRightDiagonal(x, grid.getRowOfLastPutToken()) >= IGridGameplay.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}

		if (inspectVertical(x, grid.getRowOfLastPutToken()) >= IGridGameplay.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}
		
		if (inspectHorizontalW(x, grid.getRowOfLastPutToken()) >= IGridGameplay.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}

		if(inspectHorizontal(x, grid.getRowOfLastPutToken()) >= IGridGameplay.REQUIRED_TOKENS) {
			this.winner = getCurrentPlayer();
			return true;
		}

		winPositions.clear();
		for (int column = 0; column < IGridGameplay.COLUMNS ; column ++) {
			for (int row = 0; row < IGridGameplay.ROWS ; row ++) {
				if (grid.getToken(column, row) == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	private int inspectLeftDiagonal(int x, int y) throws ConnectException {
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
	
	private int inspectRightDiagonal(int x, int y) throws ConnectException {
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
	
	private int inspectVertical(int x, int y) throws ConnectException {
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
	
	private int inspectHorizontal(int x, int y) throws ConnectException {
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
		for (int i = x; i >= 0; i --) {
			if (getToken(i, y) != currentPlayer) {
				break;
			}
			winPositions.add(new TokenPosition(i, y, this.currentPlayer));
			foundInLine++;
		}
		return foundInLine;
	}
	
	@Override
	public Tokens getWinner() {
		return winner;
	}

	private Tokens getNextPlayer() {
		return TOKEN_VALUES[(currentPlayer.ordinal() + 1) % TOKEN_VALUES.length];
	}

	public List<TokenPosition> getWinPositions() {
		return winPositions;
	}
}