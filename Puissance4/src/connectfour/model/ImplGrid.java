package connectfour.model;

public class ImplGrid implements Grid {

	private final Tokens[][] grid;
	
	private Integer rowOfLastPutToken;
	
	public ImplGrid(int columns, int rows) {
		if (columns <= 0 || rows <= 0) {
			throw new IllegalArgumentException("Les colonnes ou lignes ne peuvent être inférieur ou égal à 0");
		}
		grid = new Tokens[columns][rows];
	}
	
	@Override
	public Tokens getToken(int x, int y) {
		// TODO Auto-generated method stub
		if (x > grid.length || y > grid[0].length) {
			throw new IllegalArgumentException("Les valeurs x ou y ne peuvent êtres supérieurs au nombre de colonnes / lignes");
		}
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("Les valeurs x ou y ne peuvent êtres inférieurs à 0");
		}
		return grid[x][y];
	}

	@Override
	public void putToken(Tokens token, int x) throws ConnectException {
		// TODO Auto-generated method stub
		if (x >= grid.length) {
			throw new ConnectException("Ne peut être supérieur au nombre de colonnes");	
		}
		if ( x < 0 ) {
			throw new ConnectException("Ne peut être inférieur à 0");
		}
		if (token == null) {
			throw new IllegalArgumentException("Token null");
		}
		int y = 0;
		while (y < grid[0].length && grid[x][y] != null){
			y++;
		}
		if (y >= grid[0].length) {
			throw new ConnectException("La colonne est pleine");
		}
		grid[x][y] = token;
		rowOfLastPutToken = y;
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
		for (int column = 0; column < this.grid.length; column ++) {
			for (int row = 0; row < this.grid[0].length; row ++) {
				this.grid[column][row] = null;
			}
		}
	}
}