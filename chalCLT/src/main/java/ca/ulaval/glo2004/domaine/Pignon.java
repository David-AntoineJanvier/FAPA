/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.ulaval.glo2004.domaine;

/**
 *
 * @author David-Antoine
 */
public class Pignon {
    private float hauteur;
    private float longueur;
    
    public Pignon(float longMurParr, float longeurMurPerp, float angle, float epp)
    {
        this.hauteur = longMurParr*(float)Math.abs(Math.tan(angle));
        this.longueur = longMurParr - epp/2.0f;
    }
    
    public Pignon(Pignon p)
    {
    this.hauteur = p.reqHauteur();
    this.longueur = p.reqLongueur();
    }
    
    public float reqHauteur()
    {
        return this.hauteur;
    }
    public void asgHauteur(float arghauteur)
    {
        this.hauteur = arghauteur;
    }
    public float reqLongueur()
    {
        return this.longueur;
    }
    public void asgLongueur(float arglongueur)
    {
        this.longueur = arglongueur;
    }
    
}
