package game;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;


public class GameWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JFrame frame;    

    public GameWindow( GamePanel gamePanel) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setTitle("ICPocket");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
    }
}