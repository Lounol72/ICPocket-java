package ui.settings;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static utilz.HelpMethods.GetPhrase;

/**
 * Représente un paramètre configurable avec getter/setter fonctionnels.
 * Encapsule la logique d'accès aux valeurs et synchronise avec le slider UI.
 */
public class SettingItem {
    
    // === PROPRIÉTÉS ===
    private String nameKey;
    private Supplier<Float> valueGetter;
    private Consumer<Float> valueSetter;
    private float minValue;
    private float maxValue;
    private float step;
    private float defaultValue;
    private SettingSlider slider;
    
    /**
     * Constructeur
     * @param nameKey Clé de traduction pour le nom
     * @param valueGetter Fonction pour lire la valeur
     * @param valueSetter Fonction pour écrire la valeur
     * @param minValue Valeur minimale
     * @param maxValue Valeur maximale
     * @param defaultValue Valeur par défaut
     * @param step Incrément pour ajustement
     * @param sliderX Position X du slider
     * @param sliderY Position Y du slider
     * @param sliderWidth Largeur du slider
     * @param sliderHeight Hauteur du slider
     */
    public SettingItem(String nameKey, 
                      Supplier<Float> valueGetter, 
                      Consumer<Float> valueSetter,
                      float minValue, 
                      float maxValue, 
                      float defaultValue, 
                      float step,
                      int sliderX, int sliderY, int sliderWidth, int sliderHeight) {
        this.nameKey = nameKey;
        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.defaultValue = defaultValue;
        this.step = step;
        
        // Créer le slider associé
        this.slider = new SettingSlider(sliderX, sliderY, sliderWidth, sliderHeight,
                                       minValue, maxValue, defaultValue, step);
        
        // Synchroniser le slider avec la valeur actuelle
        updateSlider();
    }
    
    /**
     * Lit la valeur actuelle
     * @return Valeur actuelle
     */
    public float getValue() {
        return valueGetter.get();
    }
    
    /**
     * Écrit une nouvelle valeur
     * @param value Nouvelle valeur
     */
    public void setValue(float value) {
        float clampedValue = Math.max(minValue, Math.min(maxValue, value));
        valueSetter.accept(clampedValue);
        updateSlider();
    }
    
    /**
     * Réinitialise à la valeur par défaut
     */
    public void reset() {
        setValue(defaultValue);
        slider.reset();
    }
    
    /**
     * Vérifie si la valeur est à la valeur par défaut
     * @return true si valeur = défaut
     */
    public boolean isDefault() {
        return Math.abs(getValue() - defaultValue) < 0.001f;
    }
    
    /**
     * Retourne le nom traduit
     * @return Nom traduit
     */
    public String getDisplayName() {
        return GetPhrase(nameKey);
    }
    
    /**
     * Retourne la valeur formatée en string
     * @return Valeur formatée
     */
    public String getValueString() {
        return String.format("%.3f", getValue());
    }
    
    /**
     * Synchronise le slider avec la valeur actuelle
     */
    public void updateSlider() {
        slider.setValue(getValue());
    }
    
    /**
     * Synchronise la valeur depuis le slider
     */
    public void updateFromSlider() {
        setValue(slider.getValue());
    }
    
    /**
     * Retourne le slider associé
     * @return SettingSlider
     */
    public SettingSlider getSlider() {
        return slider;
    }
    
    /**
     * Retourne la clé de traduction
     * @return Clé de traduction
     */
    public String getNameKey() {
        return nameKey;
    }
    
    /**
     * Retourne la valeur par défaut
     * @return Valeur par défaut
     */
    public float getDefaultValue() {
        return defaultValue;
    }
}

