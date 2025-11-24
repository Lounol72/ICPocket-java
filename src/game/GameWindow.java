package game;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;


public class GameWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    public GameWindow(GamePanel gamePanel) {
        // Utiliser 'this' directement puisque la classe hérite déjà de JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(gamePanel);
        setTitle("ICPocket");
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
        setVisible(true);
        addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// La fenêtre a regagné le focus
				// Pour l'instant, aucune action spécifique n'est nécessaire
				// Cette méthode peut être utilisée pour reprendre les animations ou réinitialiser l'état si nécessaire
			}
		});
    }
}