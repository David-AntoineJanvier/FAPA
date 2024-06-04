package ca.ulaval.glo2004.domaine.utils;

public class Imperial 
{
    private int pieds;
    private int pouces;
    private int numerateur;
    private int denominateur;
    
    public Imperial(int pieds, int pouces, int numerateur, int denominateur)
    {
        this.pieds = pieds;
        this.pouces = pouces;
        this.numerateur = numerateur;
        this.denominateur = denominateur;
    }
    
    public static float convertirImperialVersFloat(int pieds, int pouces, int numerateur, int denominateur)
    {
        float valeurEnPouces = 0.0f;
        valeurEnPouces = (12.0f * pieds) + (float)pouces + (float)(numerateur/denominateur);
        return valeurEnPouces;
    }
    
    public int[] reqAttributs()
    {
        int[] dimensions = {pieds, pouces, numerateur, denominateur};
        return dimensions;
    }
}
