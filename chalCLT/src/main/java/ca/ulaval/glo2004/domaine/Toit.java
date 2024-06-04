/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.ulaval.glo2004.domaine;
import java.awt.Color;
import ca.ulaval.glo2004.domaine.Pignon;
import ca.ulaval.glo2004.domaine.Rallonge;
import java.util.ArrayList;


/**
 *
 * @author David-Antoine
 */
public class Toit {
    private float angle;
    //(vus de face)
    private int Orientation;
    private Pignon RSPigon;
    private Pignon LSPigon;
    private Rallonge rallongeToit;
    private Color couleur;
    
    public Toit(float longMurDroite, float longMurFace, float epp)
    {
        this.angle = (float)Math.PI/4.0f;
        this.LSPigon = new Pignon(longMurDroite, longMurFace, angle, 6.0f);
        this.RSPigon = new Pignon(longMurDroite, longMurFace, angle, 6.0f);
        this.Orientation = 0;
        this.couleur = new Color(200, 0, 200);
        this.rallongeToit = new Rallonge(longMurDroite, longMurFace, angle);
        
    }
    
    public Toit(Toit t)
    {
    this.angle = t.reqAngle();
    this.LSPigon = new Pignon(t.reqLSPignons());
    this.RSPigon = new Pignon(t.reqRSPignons());
    this.rallongeToit = new Rallonge(t.reqRallonge());
    this.Orientation = t.reqOrientation();
    this.couleur = t.reqCouleur();
    }
    
    public Pignon reqRSPignons()
    {
        
        return this.RSPigon;
    }
    
    public Pignon reqLSPignons()
    {
        
        return this.LSPigon;
    }
    
    public Rallonge reqRallonge()
    {
    return this.rallongeToit;
    }
    
    public Color reqCouleur()
    {
        return this.couleur;
    }
    public void asgCouleur(float[] couleur)
    {
        this.couleur = new Color(couleur[0], couleur[1], couleur[2]);
    }
    
    public float reqAngle()
    {
        return this.angle;
    }
    public void asgAngle(float angleVar)
    {
        this.angle  = angleVar;
    }
    public void asgOrientation(int nouvOrientation)
    {
        this.Orientation = nouvOrientation;
    }
    
    public int reqOrientation()
    {
        return this.Orientation;
    }
    
    public void majDimsPignon(float longueurMurParr, float longueurMurPerp, float epp)
    {
        float argLongueur = longueurMurParr - epp/2.0f;
        RSPigon.asgLongueur(argLongueur);
        LSPigon.asgLongueur(argLongueur);
        float argHauteur = (longueurMurParr - epp/2.0f)*(float)Math.abs(Math.tan(angle));
        RSPigon.asgHauteur(argHauteur);
        LSPigon.asgHauteur(argHauteur);
    }
    
    public void majDimsRallonge(float longueurMurParr, float longueurMurPerp)
    {
        rallongeToit.asgLongueur(longueurMurPerp);
        rallongeToit.asgHauteur(longueurMurParr*(float)Math.tan(angle));
    }
    
}
