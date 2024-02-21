package fr.seynax.puissance4.impl.model.grid;

import fr.seynax.puissance4.api.model.Grid;
import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.core.exception.Tokens;

import java.util.ArrayList;
import java.util.List;

public class GridList implements Grid
{
	private final List<List<Tokens>> grid;
	
	private Integer rowOfLastPutToken;
	
	private final int columns;
	private final int rows;
	
	public GridList(int columns, int rows) {
		this.assertDimensions(columns, rows);

		this.columns = columns;
		this.rows = rows;
		grid = new ArrayList <> ();
	}

	@Override
	public boolean isAvailable(final int x)
	{
		assertX(x);

		var row = grid.get(x);
		if(row.size() < rows()) {
			return true;
		}

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

		List<Tokens> rows = grid.get(x);
		if (rows == null) {
			throw new ConnectException("GridList : column not found at " + x + " !");
		}

		if (y >= rows.size()) {
			return null;
		}
		
		return rows.get(y);
	}

	@Override
	public void putToken(Tokens token, int x) throws ConnectException {
		assertX(x);

		List<Tokens> rows = null;
		if (x < grid.size()) {
			rows = grid.get(x);
		}
		
		if (rows == null) {
			rows = new ArrayList <> ();
			grid.add(x, rows);
		}

		assertY(rows.size() + 1);

		rows.add(token);
		rowOfLastPutToken = rows.size() - 1;
	}

	@Override
	public Integer getRowOfLastPutToken() {
		return rowOfLastPutToken;
	}

	@Override
	public void init() {
		rowOfLastPutToken = 0;
		grid.clear();
		for (int column = 0; column < columns(); column ++) {
			grid.add(new ArrayList<>());
		}
	}

	@Override
	public int columns()
	{
		return columns;
	}

	@Override
	public int rows()
	{
		return rows;
	}
}
