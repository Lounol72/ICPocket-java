package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import static game.Game.*;

/**
 * The GamePanel class is responsible for rendering the game graphics and handling user inputs.
 * It extends the JPanel class and integrates mouse and keyboard input handling.
 */
public class GamePanel extends JPanel {

    private MouseInputs mouseInputs; // Handles mouse inputs for the game panel
    private Game game; // Reference to the main Game instance

    /**
     * Constructs a GamePanel instance.
     *
     * @param game The main Game instance to be associated with this panel.
     */
    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this);
        this.game = game;

        setPanelSize(); // Sets the size of the panel
        addKeyListener(new KeyboardInputs(this)); // Adds a keyboard input listener
        addMouseListener(mouseInputs); // Adds a mouse input listener
        addMouseMotionListener(mouseInputs); // Adds a mouse motion listener
    }

    /**
     * Sets the preferred size of the panel based on the game dimensions.
     */
    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("Size: " + GAME_WIDTH + " x " + GAME_HEIGHT);
    }

    /**
     * Updates the game state. This method is intended to be called periodically
     * to update the game logic.
     */
    public void updateGame() {
        // Game update logic will be implemented here
    }

    /**
     * Paints the game components on the panel.
     *
     * @param g The Graphics object used for drawing.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Delegates additional rendering to the Game instance
        game.render(g);
    }

    /**
     * Gets the associated Game instance.
     *
     * @return The Game instance.
     */
    public Game getGame() {
        return game;
    }
}