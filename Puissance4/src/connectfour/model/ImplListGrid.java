package connectfour.model;

import java.util.ArrayList;
import java.util.List;

public class ImplListGrid implements Grid {
	private final List<List<Tokens>> grid;
	
	private Integer rowOfLastPutToken;
	
	private final int COLUMNS;
	private final int ROWS;
	
	public ImplListGrid(int columns, int rows) {
		if (columns <= 0 || rows <= 0) {
			throw new IllegalArgumentException("Les colonnes ou lignes ne peuvent être inférieur ou égal à 0");
		}
		grid = new ArrayList <> ();
		COLUMNS = columns;
		ROWS = rows;
	}

	@Override
	public Tokens getToken(int x, int y) throws ConnectException {
		// TODO Auto-generated method stub
		if (x > COLUMNS) {
			throw new IllegalArgumentException("La valeur x ne peut être supérieur au nombre de colonnes");
		}
		
		if (x >= grid.size()) {
			return null;
		}
		
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("Les valeurs x ou y ne peuvent êtres inférieurs à 0");
		}
		
		List<Tokens> rows = grid.get(x);
		if (rows == null) {
			throw new ConnectException("La colonne n'existe pas");
		}
		if (y > ROWS) {
			throw new IllegalArgumentException("La valeur y ne peut être supérieur au nombre de lignes");
		}
		
		if (y >= rows.size()) {
			return null;
		}
		
		return rows.get(y);
	}

	@Override
	public void putToken(Tokens token, int x) throws ConnectException {
		// TODO Auto-generated method stub
		if (x > COLUMNS) {
			throw new IllegalArgumentException("La valeur x ne peut être supérieur au nombre de colonnes");
		}
		if (x < 0) {
			throw new IllegalArgumentException("Les valeurs x ou y ne peuvent êtres inférieurs à 0");
		}
		List<Tokens> rows = null;
		if (x < grid.size()) {
			rows = grid.get(x);
		}
		
		if (rows == null) {
			rows = new ArrayList <> ();
			grid.add(x, rows);
		} 
		if (rows.size()+1 > ROWS) {
			throw new ConnectException("Dépassement de ligne");
		}
		rows.add(token);
		rowOfLastPutToken = rows.size()-1;
	}

	@Override
	public Integer getRowOfLastPutToken() {
		// TODO Auto-generated method stub
		return rowOfLastPutToken;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		rowOfLastPutToken = 0;
		grid.clear();
		for (int column = 0; column < COLUMNS; column ++) {
			grid.add(new ArrayList<>());
		}
	}
}
