/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.afficheur.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Stack;
import ca.ulaval.glo2004.domaine.export.ExportSTL;
import ca.ulaval.glo2004.domaine.ChaletDTO;


public class Controleur 
{
    
    /////////////////    Definitions de base    ////////////////
    private Chalet chalet;
    private Afficheur afficheur;
    private Graphics2D myg;
    private ExportSTL export;
    private Stack<ChaletDTO> undo_stack;
    private Stack<ChaletDTO> redo_stack;
    public Controleur(Graphics g)
    {
        afficheur = new Afficheur(this, 10, 10);
        chalet = new Chalet();
        this.myg = (Graphics2D)g;
        this.export = new ExportSTL(this.chalet);
        this.undo_stack= new Stack<>();
        this.redo_stack = new Stack<>();
    }
    
        /////////////////    acquisition    ////////////////
    public ChaletDTO reqChaletDTO()
    {
        return new ChaletDTO(chalet);
    }
    
    //////////////////   Methodes Chalet    /////////////////////////////
    
    public void convertirPoint(Point click)
    {
        chalet.convertirPoint(click.x, click.y);
    }
    
    public void nouveauChalet()
    {
        undo_stack.clear();
        redo_stack.clear();
        chalet = new Chalet();
    }  
    
    //////////////////   Methodes Affichage    /////////////////////////////
    public void affichemenu()
    {
        //call methode afficheur
    }
    
    public void Zoom(double zoomFactor,int x,int y)
    {
        afficheur.Zoom(zoomFactor,x,y);
        afficheur.dessiner(myg);
    }
        
    public void Zoom_reset()
    {
        afficheur.Zoom_Reset();
        afficheur.dessiner(myg);
    }
    
    public void grille_toggle ()
    {
        afficheur.grille_toggle();
        afficheur.dessiner(myg);
    }
    
    public void grille_setLength(int len)
    {
    if(afficheur.grille_setLength(len))
    {afficheur.dessiner(myg);}
    }
    
    public void dessiner(int muractif){}
    
    //////////////////   Methodes Accessoirces    /////////////////////////////
    public boolean ajouterFenetre(float positionX, float positionY,
                                    float hauteur, float longueur, int accId)
    {
        undo_stack.push(this.reqChaletDTO());
        boolean succes = false;
        succes = Fenetre.ajouterFenetre(positionX, positionY, hauteur, longueur, accId);
        afficheur.dessiner(myg);
        return succes;
    }
    
    public boolean ajouterPorte(float positionX, float hauteur, float longueur, int accId)
    {
        undo_stack.push(this.reqChaletDTO());
        boolean succes = false;
        succes = Porte.ajouterPorte(positionX, 0.0f, hauteur, longueur, accId);
        afficheur.dessiner(myg);
        
        return succes;
    }
    
    public boolean supprimerAccessoire(int accId)
    {
        boolean succes = false;
        undo_stack.push(this.reqChaletDTO());
        succes = Chalet.reqMurCourant().supprimerAccessoire(accId);
        afficheur.dessiner(myg);
        return succes;
    }
    
    public boolean modifierDimensionAcc(float nouvelleHauteur, float nouvelleLongueur, float nouveauX, float nouveauY, int accId, boolean flag, boolean deplacement)
    {
        boolean succes = false;
        if (!deplacement)
        {
        undo_stack.push(this.reqChaletDTO());
        }
        succes = Accessoire.modifierDimensionsAccessoire(nouveauX, nouveauY, nouvelleLongueur, nouvelleHauteur, accId,flag);
        afficheur.dessiner(myg);

        return succes;
    }
    
    //////////////////   Methodes Murs    /////////////////////////////
    public void modifierRainureSupp(float rainure){
        undo_stack.push(this.reqChaletDTO());
        chalet.asgRainureSup(rainure);
        afficheur.dessiner(myg);

    }
    
    public void modifierDimensionMursAvantArriere(float longueur){
        undo_stack.push(this.reqChaletDTO());
        chalet.modifierDimensionAvantArriere(longueur);
        afficheur.dessiner(myg);

    }
    public void modifierDimensionMursGaucheDroite(float longueur){
        undo_stack.push(this.reqChaletDTO());
        chalet.modifierDimensionGaucheDroite(longueur);
        afficheur.dessiner(myg);

    }
    
    public void modifierHauteur(float hauteur){
        undo_stack.push(this.reqChaletDTO());
        chalet.modifierHauteur(hauteur);
        afficheur.dessiner(myg);

    }
    
    public void modifierEpaisseur(float epais){
        undo_stack.push(this.reqChaletDTO());
        chalet.modifierEpaisseur(epais);
        afficheur.dessiner(myg);

    }
    
    public void changerOrientationToit(int nouvOrientation)
    {
        chalet.asgOrientationToit(nouvOrientation);
    }
    
    ///////////////////  METHODES TOIT   /////////////////////////
    
    public void toit_asgangle(float angle)
    {
        chalet.asgAngle(angle);
    }
            
          
    
    
    
    
    
    
    
    
    //////////////////   Methodes Couleur    /////////////////////////////
    public void modifierCouleurDessus(Color couleur){
        undo_stack.push(this.reqChaletDTO());
        
    }
    public void modifierCouleurPignon(Color couleur){
        undo_stack.push(this.reqChaletDTO());
        
    }
    public void modifierCouleurRallonge(Color couleur){
        undo_stack.push(this.reqChaletDTO());
        
    }
    public void modifierCouleurMur(Color couleur){
        undo_stack.push(this.reqChaletDTO());
    }
    
    public boolean changerMurActif(int murActif)
    {
        switch(murActif)
        {
            case 0:
                chalet.setMurCourant(null);
                afficheur.vueSelectionnee = Afficheur.Vue.Dessus;
                afficheur.dessiner(myg);
                break;
            case 1:
                chalet.setMurCourant(chalet.reqMurFacade());
                afficheur.vueSelectionnee = Afficheur.Vue.Avant;
                afficheur.dessiner(myg);
                break;
            case 2:
                chalet.setMurCourant(chalet.reqMurDroit());
                afficheur.vueSelectionnee = Afficheur.Vue.Droite;
                afficheur.dessiner(myg);
                break;
            case 3:
                chalet.setMurCourant(chalet.reqMurGauche());
                afficheur.vueSelectionnee = Afficheur.Vue.Gauche;
                afficheur.dessiner(myg);
                break;
            case 4:
                chalet.setMurCourant(chalet.reqMurArriere());
                afficheur.vueSelectionnee = Afficheur.Vue.Arriere;
                afficheur.dessiner(myg);
                break;
        }
        return true;
    }
    
    //////////////////   Export STL   /////////////////////////////
    public void exporterStlBrut(String cheminFichier)
    {
        this.export.exporterMurBrut(cheminFichier);
    }
    
    public void exporterStlFini(String cheminFichier)
    {
        this.export.exporterMurFini(cheminFichier);
    }
    
    public void exporterStlRetraits(String cheminFichier)
    {
        this.export.exporterRetraits(cheminFichier);
    }
    
    /////////////   Sauvegarder/Charger   /////////////////////
    public void sauvegarderChalet(String cheminFichier)
    {
        // TODO: TODO :)
        /* Something Something reqChaletDTO -> *.ser */
    }
    
    public void chargerChalet(String cheminFichier)
    {
        //TODO: TODO :)
        /* Something Something *.ser -> this.chalet */
    }
    
    //////////////////   ERROR   /////////////////////////////
    public void erreur()
    {
        //gestion future des erreurs
    }
    
    public boolean isCursorOnAcc(float posX,float posY)
    {
        return chalet.mouseOnAcc(posX, posY);
    }
   public int AccIdfromXY(float posX, float posY)
   {
       return chalet.IdAccXY(posX,posY);
   }
   
   public void undo()
   {
       if (!undo_stack.empty())
       {
       redo_stack.push(undo_stack.peek());
       chalet.ChaletSetToDTO(undo_stack.pop());
       afficheur.dessiner(myg);
       }
       
   }
   
   public void redo()
   {
       if (!redo_stack.empty())
       {
           undo_stack.push(this.reqChaletDTO());
           chalet.ChaletSetToDTO(redo_stack.pop());
           afficheur.dessiner(myg);
       }
   }
}
