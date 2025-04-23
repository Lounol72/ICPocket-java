package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ScrollingText {
    private int x, y, width, height;
    private String fullText;
    private StringBuilder currentText;
    private boolean isComplete;
    private long lastCharTime;
    private int charDelay;
    private Font font;
    private Color textColor;

    // Optimisation du word wrapping
    private List<TextLine> textLines;
    private int visibleCharCount;

    // Personnalisation avancée
    private int padding;
    private Color backgroundColor;
    private boolean showBackground;

    public ScrollingText(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentText = new StringBuilder();
        this.textLines = new ArrayList<>();
        this.isComplete = false;
        this.charDelay = 50;
        this.font = new Font("Arial", Font.PLAIN, 16);
        this.textColor = Color.BLACK;
        this.lastCharTime = System.currentTimeMillis();
        this.padding = 10;
        this.backgroundColor = new Color(0, 0, 0, 128);
        this.showBackground = false;
    }

    public void update() {
        if (isComplete || fullText == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCharTime >= charDelay) {
            if (visibleCharCount < fullText.length()) {
                visibleCharCount++;
                currentText.setLength(0);
                currentText.append(fullText, 0, visibleCharCount);
                updateTextLines(); // Recalcule uniquement si nécessaire
                lastCharTime = currentTime;
            } else {
                isComplete = true;
            }
        }
    }

    private void updateTextLines() {
        // Mise à jour optimisée des lignes de texte
        textLines.clear();

        if (currentText.length() == 0) return;

        // Création d'un graphique temporaire pour mesurer le texte
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Graphics2D g2d = ge.createGraphics(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        String[] words = currentText.toString().split("\\s+");
        int lineWidth = 0;
        StringBuilder currentLine = new StringBuilder();
        int effectiveWidth = width - (padding * 2);

        for (String word : words) {
            int wordWidth = fm.stringWidth(word + " ");

            if (lineWidth + wordWidth > effectiveWidth && lineWidth > 0) {
                // Nouvelle ligne
                textLines.add(new TextLine(currentLine.toString(), x + padding, textLines.size() * fm.getHeight()));
                currentLine.setLength(0);
                lineWidth = 0;
            }

            currentLine.append(word).append(" ");
            lineWidth += wordWidth;
        }

        // Dernière ligne
        if (currentLine.length() > 0) {
            textLines.add(new TextLine(currentLine.toString(), x + padding, textLines.size() * fm.getHeight()));
        }

        g2d.dispose();
    }

    public void draw(Graphics g) {
        if (currentText.length() == 0) return;

        Graphics2D g2d = (Graphics2D) g;

        // Dessiner l'arrière-plan si activé
        if (showBackground) {
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(x, y, width, height, 10, 10);
        }

        g2d.setFont(font);
        g2d.setColor(textColor);

        // Activer l'antialiasing
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        // Dessiner chaque ligne précalculée
        FontMetrics fm = g2d.getFontMetrics();
        for (TextLine line : textLines) {
            g2d.drawString(line.text, line.x, y + padding + line.yOffset + fm.getAscent());
        }
    }

    public void reset(String msg) {
        this.fullText = msg;
        this.currentText.setLength(0);
        this.visibleCharCount = 0;
        this.textLines.clear();
        this.isComplete = false;
        this.lastCharTime = System.currentTimeMillis();
    }

    public void skip() {
        if (fullText != null) {
            visibleCharCount = fullText.length();
            currentText.setLength(0);
            currentText.append(fullText);
            updateTextLines();
            isComplete = true;
        }
    }

    // Classe interne pour stocker les lignes précalculées
    private static class TextLine {
        String text;
        int x;
        int yOffset;

        TextLine(String text, int x, int yOffset) {
            this.text = text;
            this.x = x;
            this.yOffset = yOffset;
        }
    }

    // Setters améliorés
    public void setCharDelay(int delay) {
        this.charDelay = delay;
    }

    public void setFont(Font font) {
        this.font = font;
        if (fullText != null) {
            updateTextLines(); // Recalculer les lignes si la police change
        }
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void setBackgroundVisible(boolean visible) {
        this.showBackground = visible;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        if (fullText != null) {
            updateTextLines();
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (fullText != null) {
            updateTextLines();
        }
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        if (fullText != null) {
            updateTextLines();
        }
    }
}