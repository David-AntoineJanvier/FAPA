/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.ulaval.glo2004.domaine;

/**
 *
 * @author David-Antoine
 */
public class Rallonge {
    //relative au dessus du mur
    private float hauteur;
    private float longueur;
    
    public Rallonge(float longMurParr, float longeurMurPerp, float angle)
    {
        this.hauteur = longMurParr*(float)Math.abs(Math.tan(angle));
        this.longueur = longeurMurPerp;
    }
    
    public Rallonge(Rallonge r)
    {
    this.hauteur = reqHauteur();
    this.longueur = reqLongueur();
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
