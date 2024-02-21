package connectfour.app;

import connectfour.model.ConnectException;
import connectfour.model.ImplGame;
import connectfour.view.GameView;
import connectfour.view.JCursesGameViewIA;

import java.io.*;

public class ConnectFour {
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
		} catch (ConnectException e) {
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
			try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("logs.txt"), true))) {
				bufferedWriter.write(String.format("Thread %s caught exception %s%n", t, e) + "\n" + getStackTrace(e));
			} catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
	}
}