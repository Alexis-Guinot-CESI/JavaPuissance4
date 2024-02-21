package fr.seynax.puissance4.impl.model.grid;

import fr.seynax.puissance4.api.model.Grid;
import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.core.exception.Tokens;

public class GridArray implements Grid
{
	private final Tokens[][] grid;
	private Integer rowOfLastPutToken;
	
	public GridArray(int columns, int rows) {
		this.assertDimensions(columns, rows);

		grid = new Tokens[columns][rows];
	}

	@Override
	public boolean isAvailable(final int x)
	{
		assertX(x);

		var row = grid[x];
		for(var token : row) {
			if(token == null) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Tokens getToken(int x, int y) throws ConnectException
	{
		assertX(x);
		assertY(y);

		return grid[x][y];
	}

	@Override
	public void putToken(Tokens token, int x) throws ConnectException
	{
		assertX(x);
		assertToken(token);

		int y = 0;
		while (y < rows()&& grid[x][y++] != null);
		assertY(y);

		grid[x][y] = token;
		rowOfLastPutToken = y;
	}

	@Override
	public Integer getRowOfLastPutToken() {
		return rowOfLastPutToken;
	}

	@Override
	public void init() {
		rowOfLastPutToken = 0;
		for (int column = 0; column < columns(); column ++) {
			for (int row = 0; row < rows(); row ++) {
				this.grid[column][row] = null;
			}
		}
	}

	@Override
	public int columns() {
		return grid.length;
	}

	@Override
	public int rows() {
		return grid[0].length;
	}
}