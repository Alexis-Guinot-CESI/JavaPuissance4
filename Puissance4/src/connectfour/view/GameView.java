package connectfour.view;

import connectfour.model.ConnectException;

/**
 * Une vue de jeu.
 * Prend un mod�le de jeu en argument
 *  et g�re les interactions avec l'utilisateur.
 */
public interface GameView {
	
	/**
	 * Lance le syst�me de jeu.
	 * @throws ConnectException 
	 */
	void play() throws ConnectException;

}
