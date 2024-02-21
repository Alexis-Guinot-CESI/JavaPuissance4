package connectfour.model;

import java.util.List;

/**
 * M�canique du jeu Connect Four.
 * Chaque joueur pose � son tour un jeton dans une colonne de la grille.
 * Le jeu s'arr�te lorsque quatre jetons de m�me couleur
 *  sont align�s en ligne, en colonne ou en diagonale
 *  ou lorsque la grille est remplie (auquel cas il y a match nul).
 * A la cr�ation, le jeu est initialis�.
 */
public interface Game {
	public final static class TokenPosition {
		private int x;
		private int y;
		private Tokens tokens;

		public TokenPosition(int x, int y, Tokens tokens) {
			this.x = x;
			this.y = y;
			this.tokens = tokens;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Tokens getTokens() {
			return tokens;
		}
	}

	/** Nombre de colonnes dans la grille */
	static final int COLUMNS = 7;
	
	/** Nombre de lignes dans la grille */
	static final int ROWS = 6;
	
	/** Nombre de jetons � aligner pour gagner */
	static final int REQUIRED_TOKENS = 4;
	
	/**
	 * R�cup�re le jeton pr�sent en (x,y).
	 * @param x la colonne
	 * @param y la ligne
	 * @return le jeton pr�sent, null si aucun
	 * @throws ConnectException 
	 */
	Tokens getToken(int x, int y) throws ConnectException;
	
	/**
	 * R�cup�re le joueur dont c'est le tour de jouer.
	 * @return le jeton du joueur qui doit jouer
	 */
	Tokens getCurrentPlayer();
	
	/**
	 * Indique si le jeu est termin� ou non.
	 * @return true si l'un des joueurs a gagn� ou si la grille est compl�te
	 */
	boolean isOver();
	
	/**
	 * R�cup�re le vainqueur de la partie.
	 * @return le jeton du joueur qui a remport� la partie
	 *  si elle est termin�e et qu'il n'y a pas eu de match nul.
	 */
	Tokens getWinner();
	
	/**
	 * Ins�re un jeton pour le joueur courant dans la colonne x.
	 * @param column la colonne cible
	 * @throws ConnectException
	 */
	void putToken(int column) throws ConnectException;
	
	/**
	 * Initialise le jeu.
	 * Vide la grille et tire au sort le joueur qui commence.
	 */
	void init();

	List<TokenPosition> getWinPositions();
}
