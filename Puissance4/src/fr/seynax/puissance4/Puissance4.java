package fr.seynax.puissance4;

import fr.seynax.puissance4.core.exception.ConnectException;
import fr.seynax.puissance4.impl.model.ImplGame;
import fr.seynax.puissance4.api.view.GameView;
import fr.seynax.puissance4.impl.view.JCursesGameViewIA;

import java.io.*;

public class Puissance4
{
	public static void main(String[] args) throws Exception {
		ExceptionCatcher.start();
		// Cr�ation du jeu
		// On instancie une vue, � laquelle on fournit un mod�le (MVC)
		System.out.println("INIT !");
		GameView gameView = new JCursesGameViewIA(new ImplGame());
		
		// Lancement du syst�me de jeu
		try {
			System.out.println("PLAY !");
			gameView.play();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("STOP !");
		// Fin de l'application
		System.exit(1);
	}

	public final static class ExceptionCatcher implements Thread.UncaughtExceptionHandler {
		public static void start() {
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionCatcher());
		}

		public static String getStackTrace(final Throwable throwable) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw, true);
			throwable.printStackTrace(pw);
			return sw.getBuffer().toString();
		}

		public void uncaughtException(Thread t, Throwable e)
		{
			var logsFilePath = new File("logs.txt").getAbsolutePath();
			try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logsFilePath, false))) {
				System.err.println("WRITE EXCEPTION IN : " + logsFilePath);
				bufferedWriter.write(String.format("Thread %s caught exception %s%n", t, e) + "\n" + getStackTrace(e));
			} catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
	}
}