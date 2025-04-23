package icmon;

import utilz.t_Effect;
import utilz.t_Type;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utilz.Constants.ICMONS.*;
import static utilz.Constants.ICMONS.Combat.*;
import static utilz.Constants.ICMONS.Nature.getNatureCoefficients;
import static utilz.Constants.ICMONS.Nature.getNatureName;
import static utilz.Constants.ICMONS.STATS.*;
import static utilz.Constants.ICMONS.TypeChart.getEffectiveness;
import static utilz.Constants.ICMONS.StatVariations.*;
import static utilz.Constants.SCALE;
import static utilz.HelpMethods.generateICMonFromId;
import static utilz.HelpMethods.generateMoveFromId;
import static utilz.LoadSave.GetICMonSprite;

public class ICMon {

    private static final Random rnd = new Random();

    private boolean MouseOver = false;

    private int id; /**< id de l'ICmon. */
    private String name;	/**< nom du ICmon. */
    private boolean gender; /**< genre du ICmon. male = 0, female = 1  */
    private t_Type[] type; /**< type du ICmon. [2]*/
    private int lvl;	 /**< niveau du ICmon. */
    private long exp; /**< experience du ICmon. */
    private int nature; /**< nature du ICmon. */
    private int current_pv; /**< points de vie actuel du ICmon. */
    private int[] baseStats; /**< statistiques de base du ICmon. [6] */
    private int initial_pv; /**< points de vie initial du ICmon. */
    private int[] iv; /**< valeurs iv du ICmon. [6] */
    private Move[] moveList; /**< liste des attaques du ICmon. [4]*/
    private int nb_move = 0; /**< nombre d'attaques du ICmon. */
    private t_Effect main_effect; /**< effet principal du ICmon. */
    // Affichage

    // Définition des constantes pour le positionnement
    private final int TEXT_MARGIN_LEFT = (int) (20 * SCALE);
    private final int TEXT_MARGIN_TOP = (int) (50 * SCALE);
    private final int LINE_HEIGHT = (int) (20 * SCALE);
    private final int SECTION_SPACING = (int) (15 * SCALE);

    private final int SPRITE_X = (int) ( 200* SCALE);
    private final int SPRITE_Y = (int) ( 200* SCALE);
    private final int SPRITE_WIDTH = (int) ( 200* SCALE);
    private final int SPRITE_HEIGHT = (int) ( 200* SCALE);
    private final Rectangle rect = new Rectangle(SPRITE_X,SPRITE_Y,SPRITE_WIDTH,SPRITE_HEIGHT);

    private ICMonIMG img;

    public ICMon(int id){
        ICMon generated = generateICMonFromId(id);
        this.id = id;
        this.name = generated.name;
        this.gender = generated.gender;
        this.type = generated.type;
        this.lvl = generated.lvl;
        this.exp = generated.exp;
        this.nature = generated.nature;
        this.current_pv = generated.current_pv;
        this.baseStats = generated.baseStats;
        this.initial_pv = generated.initial_pv;
        this.iv = generated.iv;
        this.moveList = generated.moveList;
        this.nb_move = rnd.nextInt(5);
        this.main_effect = t_Effect.noEffect;
        // generate random moves
        for (int i = 0; i<nb_move;i++)
            setNewMove(generateMoveFromId(rnd.nextInt(1,NUMBER_OF_MOVES)));
        // generate sprite
        this.img = new ICMonIMG(SPRITE_X,SPRITE_Y,SPRITE_WIDTH,SPRITE_HEIGHT,name);

    }

    public ICMon(int id, String name, t_Type[] type, int[] baseStats){
        this.id = id;
        this.name = name;
        this.type = type;
        this.baseStats = baseStats;
        this.iv = new int[6];
        for (int i = 0; i < iv.length; i++)
            iv[i] = rnd.nextInt(32)+1;
        this.gender = (rnd.nextInt(2) == 0);
        this.lvl = rnd.nextInt(100) + 1;
        this.exp = expCurve();
        this.nature = rnd.nextInt(25);
        this.current_pv = initial_pv = calcStatFrom(PV);
        this.main_effect = t_Effect.noEffect;
        // Remplissage des moves NULL
        moveList = new Move[4];
        for (int i = 0; i < moveList.length; i++) {
            moveList[i] = new Move();
            moveList[i].setPower(-1);
        }

    }
    public void update(){
        img.update();
    }
    public void draw( Graphics g){

        if(MouseOver) {
            // Position Y courante
            int currentY = TEXT_MARGIN_TOP;

            // Configuration de la police
            g.setFont(new Font("Arial", Font.BOLD, 16));

            // En-tête
            g.setColor(Color.WHITE);
            g.drawString("ICMON DETAILS", TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT + 10;

            // Informations de base
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("█ Basic Information", TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(String.format("%-15s: %s", "Name", name), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %s", "Gender", gender ? "Female" : "Male"), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %d (%d)", "Level", lvl, exp), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %d/%d", "Current PV", current_pv, initial_pv), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %s", "Nature", getNatureName(nature)), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %s", "Current Effect", t_Effect.getName(main_effect)), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT + SECTION_SPACING;

            // Types
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("█ Type Information", TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(String.format("%-15s: %s", "Primary Type", type[0].name()), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;
            g.drawString(String.format("%-15s: %s", "Secondary Type",
                    type[1].name().equals("noType") ? "-" : type[1].name()), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT + SECTION_SPACING;

            // Statistiques
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("█ Base Statistics", TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            String[] statNames = {"HP", "Attack", "Defense", "Sp. Attack", "Sp. Defense", "Speed"};
            for ( int i = 0; i < baseStats.length; i++ ) {
                g.drawString(String.format("%-15s: %3d (IV: %2d)", statNames[i], baseStats[i], iv[i]),
                        TEXT_MARGIN_LEFT, currentY);
                currentY += LINE_HEIGHT;
            }
            currentY += SECTION_SPACING;

            // Liste des attaques
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("█ Move Set", TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(String.format("%-15s: %d", "Number of moves", nb_move), TEXT_MARGIN_LEFT, currentY);
            currentY += LINE_HEIGHT;

            for ( int i = 0; i < moveList.length; i++ ) {
                if ( moveList[i] != null && moveList[i].getPower() != -1 ) {
                    g.drawString(String.format("Move %d:", i + 1), TEXT_MARGIN_LEFT, currentY);
                    currentY += LINE_HEIGHT;

                    // Adaptez ici l'affichage en fonction de votre classe Move et de sa méthode InfoDisplay()
                    // Par exemple:
                    Move move = moveList[i];
                    g.drawString(String.format("  %-15s: %s", "Name", move.getName()), TEXT_MARGIN_LEFT, currentY);
                    currentY += LINE_HEIGHT;
                    g.drawString(String.format("  %-15s: %d", "Power", move.getPower()), TEXT_MARGIN_LEFT, currentY);
                    currentY += LINE_HEIGHT;
                    g.drawString(String.format("  %-15s: %d/%d", "PP", move.getCurrent_pp(), move.getMax_pp()),
                            TEXT_MARGIN_LEFT, currentY);
                    currentY += LINE_HEIGHT;
                    g.drawString(String.format("  %-15s: %s", "Type", move.getType()), TEXT_MARGIN_LEFT, currentY);
                    currentY += LINE_HEIGHT;
                }
            }
        }
        img.draw(g);

    }

    public void DisplayInfo() {
        String separator = "=".repeat(50);
        String smallSeparator = "-".repeat(50);
        
        // En-tête
        System.out.println(separator);
        System.out.println("                    ICMON DETAILS                    ");
        System.out.println(separator);
        
        // Informations de base
        System.out.println("█ Basic Information");
        System.out.println(smallSeparator);
        System.out.printf("%-15s: %s%n", "Name", name);
        System.out.printf("%-15s: %s%n", "Gender", gender ? "Female" : "Male");
        System.out.printf("%-15s: %d (%d)%n", "Level", lvl,exp);
        System.out.printf("%-15s: %d/%d%n", "Current PV", current_pv, initial_pv);
        System.out.printf("%-15s: %s%n", "Nature", getNatureName(nature));
        System.out.printf("%-15s: %s%n", "Current Effect", t_Effect.getName(main_effect));
        
        // Types
        System.out.println("\n█ Type Information");
        System.out.println(smallSeparator);
        System.out.printf("%-15s: %s%n", "Primary Type", type[0].name());
        System.out.printf("%-15s: %s%n", "Secondary Type", type[1].name().equals("noType") ? "-" : type[1].name());
        
        // Statistiques
        System.out.println("\n█ Base Statistics");
        System.out.println(smallSeparator);
        String[] statNames = {"HP", "Attack", "Defense", "Sp. Attack", "Sp. Defense", "Speed"};
        for (int i = 0; i < baseStats.length; i++) {
            System.out.printf("%-15s: %3d (IV: %2d)%n", statNames[i], baseStats[i], iv[i]);
        }
        
        // Liste des attaques
        System.out.println("\n█ Move Set");
        System.out.println(separator);
        System.out.printf("%-15s: %d%n","Number of moves", nb_move);
        for (int i = 0; i < moveList.length; i++) {
            if (moveList[i] != null && moveList[i].getPower() != -1) {
                System.out.printf("\nMove %d:%n", i + 1);
                moveList[i].InfoDisplay();
            }
        }
        
        System.out.println(separator);
    }

    public int calcStatFrom(int stat){
        if(stat == PV) return ( (int)( baseStats[PV] + iv[PV])*lvl/100 ) + lvl+10;
        int value = (int) ((int)(((2*baseStats[stat] + iv[stat])*lvl/100) + 5) * getNatureCoefficients(nature)[stat]);
        if (main_effect == t_Effect.burn && stat == ATT || main_effect == t_Effect.paralyze && stat == SPE ) return value/2;
        return value;
    }

    public int calcDamage(ICMon defender, int moveIndex) {
        Move move = this.moveList[moveIndex];

        // Vérification des inefficacités de type
        double typeEffectiveness = getEffectiveness(move.getType().getValue(), defender.getType()[0].getValue()) *
                                  getEffectiveness(move.getType().getValue(), defender.getType()[1].getValue());
        if (typeEffectiveness < 0.1 || move.getPower() == 0) return 0;

        // Statistiques utilisées selon la catégorie
        int targetedStatOff = move.getCateg().getValue();
        int targetedStatDef = (targetedStatOff == ATT) ? DEF : SPD;

        // Statistiques modifiées
        double attackStat = this.calcStatFrom(targetedStatOff) * getStatVariation(6);
        double defenseStat = defender.calcStatFrom(targetedStatDef) * getStatVariation(6);

        // Facteur de niveau
        double levelFactor = this.lvl * LEVEL_MULTIPLIER + 2;

        // Détermination du coup critique
        boolean isCritical = rnd.nextInt(24) == 0;

        // Calcul de base des dégâts
        double baseDamage = ((levelFactor * attackStat * move.getPower() / defenseStat) / DAMAGE_DIVISOR) + 2;

        // Application des modificateurs
        double randomFactor = (rnd.nextInt(16) + RANDOM_MIN) / 100.0;
        double stabBonus = (this.type[0].getValue() == move.getType().getValue() ||
                (this.type[1].getValue() == move.getType().getValue() &&
                        move.getType().getValue() != 0)) ? 1.5 : 1.0;
        double criticalBonus = isCritical ? 1.5 : 1.0;

        // Calcul final
        int damage = (int)(baseDamage * randomFactor * stabBonus * typeEffectiveness * criticalBonus);

        // Mise à jour des flags
        criticalHitFlag = isCritical;
        moveEffectivenessFlag = typeEffectiveness;

        return Math.max(damage, 1);
    }

    public int affectDamage(ICMon defender, int moveIndex){
        Move move;
        if (moveIndex <0)
            move = isStruggling(moveIndex) ? STRUGGLE_MOVE : CONFUSED_MOVE;
        else
            move = this.moveList[moveIndex];

        if(!this.accuracyCheck(move.getAccuracy())){

            if(!(moveIndex<0)) move.setCurrent_pp(move.getCurrent_pp()-1);
            return 0;
        }
        int damage = calcDamage(defender, moveIndex);

        defender.setCurrent_pv(defender.getCurrent_pv()>damage ? defender.getCurrent_pv()-damage : 0);
        if (!(moveIndex<0)) move.setCurrent_pp(move.getCurrent_pp()-1);
        return 1;
    }

    private boolean accuracyCheck( int accuracy ) {return Math.random()%100 < accuracy;}

    private boolean isStruggling( int moveIndex ) {return moveIndex == STRUGGLE_MOVE_INDEX;}

    public boolean isExisting(){return current_pv != IS_ABSCENT;}

    public boolean isAlive(){return isExisting() && current_pv > 0;}

    public boolean hasMoveLeft(){
        for (Move m : moveList)
            if (m.getCurrent_pp()>0) return true;
        return false;
    }

    public long expCurve(){
        return (long) Math.pow(lvl, 3);
    }

    public int[] getBaseStats() {
        return baseStats;
    }

    public void setBaseStats( int[] baseStats ) {
        this.baseStats = baseStats;
    }

    public t_Type[] getType() {
        return type;
    }

    public void setType( t_Type[] type ) {
        this.type = type;
    }

    public int getNb_move() {
        return nb_move;
    }

    public int countNb_move(){
        int counter = 0;
        for (Move m : moveList)
            if (m.getPower() != -1)
                counter++;
        return counter;
    }

    public int getNature() {
        return nature;
    }

    public void setNature( int nature ) {
        this.nature = nature;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Move[] getMoveList() {
        return moveList;
    }

    public void setMoveList( Move[] moveList ) {
        this.moveList = moveList;
    }

    public void setNewMove(Move move){
        for (int i = 0; i < moveList.length; i++) {
            if (moveList[i].getPower() == -1) {
                moveList[i] = move;
                break;
            }
        }
    }

    public t_Effect getMain_effect() {
        return main_effect;
    }

    public void setMain_effect( t_Effect main_effect ) {
        this.main_effect = main_effect;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl( int lvl ) {
        this.lvl = lvl;
    }

    public int[] getIv() {
        return iv;
    }

    public void setIv( int[] iv ) {
        this.iv = iv;
    }

    public int getInitial_pv() {
        return initial_pv;
    }

    public void setInitial_pv( int initial_pv ) {
        this.initial_pv = initial_pv;
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender( boolean gender ) {
        this.gender = gender;
    }

    public long getExp() {
        return exp;
    }

    public void setExp( long exp ) {
        this.exp = exp;
    }

    public int getCurrent_pv() {
        return current_pv;
    }

    public void setCurrent_pv( int current_pv ) {
        this.current_pv = current_pv;
    }

    public boolean isMouseOver() {
        return MouseOver;
    }

    public void setMouseOver( boolean mouseOver ) {
        MouseOver = mouseOver;
    }

    public Rectangle getRect() {
        return rect;
    }
}