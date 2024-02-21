package fr.seynax.puissance4.api.view;

import fr.seynax.puissance4.core.exception.ConnectException;

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
	void play() throws InterruptedException;
}