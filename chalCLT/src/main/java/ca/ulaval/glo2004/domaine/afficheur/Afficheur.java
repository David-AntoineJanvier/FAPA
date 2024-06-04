package ca.ulaval.glo2004.domaine.afficheur;

import ca.ulaval.glo2004.domaine.*;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Afficheur 
{
    private static final int POSITION_X_CENTRE = 700;
    private static final int POSITION_Y_CENTRE = 412;
    public static final float PIXELS_PAR_POUCES = 6.0f;
    private boolean grille = false;
    private int grille_len = 12;
    
    public enum Vue
    {
        Dessus,
        Avant,
        Droite,
        Gauche,
        Arriere
    }
  
    private final Controleur controleur;
    public Vue vueSelectionnee;
    private float hauteurInitiale;
    private float longueurInitiale; 
    private double zoomScale;
    private double zoom_posx;
    private double zoom_posy;
    private boolean zoom_flag;
    private boolean reset_zoom_flag;
    private int zoom_factor_tracker;
    private AffineTransform identity;
    
    
    public Afficheur (Controleur controleur, float hauteurInitiale, float longeurInitiale){
        this.controleur = controleur;
        this.hauteurInitiale = hauteurInitiale;
        this.longueurInitiale = longeurInitiale;
        this.vueSelectionnee = Vue.Dessus;
        this.zoomScale= 1.0;
        this.zoom_posx = 0.0;
        this.zoom_posy = 0.0;
        this.zoom_flag = false;
        this.reset_zoom_flag =false;
        this.zoom_factor_tracker = 0;
        this.identity = new AffineTransform();
        
        
    }
    public void Zoom(double zoomFactor, double x , double y)
    {
        this.zoom_factor_tracker += (zoomFactor > 1 ? -1 : 1);
        this.zoomScale = zoomFactor;
        this.zoom_posx = x;
        this.zoom_posy = y;
        this.zoom_flag = true;
    }
    public void Zoom_Reset()
    {
       this.reset_zoom_flag = true;
    }

    private void dessiner_grille(Graphics2D g)
    {
    g.setColor(Color.LIGHT_GRAY);
    float length = PIXELS_PAR_POUCES*grille_len;
    int ligne = 2 + Math.abs(zoom_factor_tracker/3);
        
        for (float i = -5000.0f; i <5000.0; i = i+length){                        //float in case pixels par pouces would change to an uneven number : less information would be lost
            g.fillRect(-5000, (int)i, 10000, ligne);
        }
        for (float i = -5000.0f; i <5000.0f; i = i+length){
            g.fillRect((int)i, -5000, ligne, 10000);
        }
    }
    
    
    public void grille_toggle(){
        this.grille = (!grille);
    }
    
    public boolean grille_setLength(int len)                                    //dit au drawer de ne pas redessiner si false, contraire si true
    {
        if(len == grille_len)
            return false; 
        grille_len = len;
        return true;
    }
    
    public void dessiner(Graphics2D g)
    {
        g.setColor(Color.gray);
        g.fillRect(-5000, -5000, 10000, 10000);                   //large numbers, setting foreground as a big grey rectangle
        g.setColor(Color.LIGHT_GRAY);
        
        
        
        if (this.zoom_flag)
        {
            g.translate(this.zoom_posx, this.zoom_posy);
            g.scale(this.zoomScale, this.zoomScale);
            g.translate(-this.zoom_posx, -this.zoom_posy);
            this.zoom_flag = false;
        }
        if (this.reset_zoom_flag)
        {
            g.setTransform(this.identity);
            this.reset_zoom_flag = false;
            this.zoom_factor_tracker = 0;
        }
        
        ///////////////////////   TEMPORAIRE : A REMPLACER PAR ORIENTATION DU TOIT => //////////////////////////
        int arriere_longueur = (int)(this.controleur.reqChaletDTO().MurArriere.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int gauche_longueur  = (int)(this.controleur.reqChaletDTO().MurGauche.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        ///////////////////////  <= TEMPORAIRE : A REMPLACER PAR ORIENTATION DU TOIT //////////////////////////
        
        
        switch (vueSelectionnee)
        {
            case Avant:
                dessinerMur(g);
                break;
            case Arriere:
                dessinerMur(g);
                break;
            case Gauche:
                //dessinerCoteArriere(g, false);
                dessinerCoters(g);
                dessinerMur(g);
               // dessinerCoteAvant(g, false);
                break;
            case Droite:
                //dessinerCoteAvant(g, true);
                dessinerCoters(g);
                dessinerMur(g);
                //dessinerCoteArriere(g, true);
                break;
            case Dessus:
                dessinerDessusToit(g);
                break;
        }
        
        if(grille){dessiner_grille(g);}
        
    }
    
    private void dessinerCoters(Graphics g)
    {
        dessinerCoteArriere(g,false);
        dessinerCoteAvant(g,false);
    }
    private void dessinerMur(Graphics g)
    {
        ///////////////////    VARIABLES   /////////////////////////////
        Mur muractif      =   this.controleur.reqChaletDTO().MurActif;              //Définition des variables de bases pour augmenter la lisibilité
        int longueur      =  (int)(muractif.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int hauteur       =  (int)(muractif.reqDimensionsMur()[1]*PIXELS_PAR_POUCES);
        int margeSecurite =  (int)(muractif.reqDimensionsMur()[2]*PIXELS_PAR_POUCES);
        int marge_supp    =  (int)(muractif.reqDimensionsMur()[3]*PIXELS_PAR_POUCES);
        int epaisseur     =  (int)(muractif.reqDimensionsMur()[4]);
        int position_x = POSITION_X_CENTRE - longueur/2;
        int position_y = POSITION_Y_CENTRE - hauteur/2;
        
        ////////////////////////   MUR  /////////////////////////////////
        if (this.controleur.reqChaletDTO().MurActif.reqOrientation() == Mur.OrientationMur.GAUCHE ||
                this.controleur.reqChaletDTO().MurActif.reqOrientation() == Mur.OrientationMur.DROITE)
        {
            g.setColor(Color.BLUE.darker());
        }
        else
        {
            g.setColor(Color.green.darker());
        }
        g.fillRect(position_x + marge_supp, position_y,longueur - 2*marge_supp,hauteur);
        
        /*
        //////////////////////    MARGES    ////////////////////////////            //inutile pour l'instant
        g.setColor(Color.red.darker());                                           
        g.fillRect(position_x, position_y,margeSecurite,hauteur);                   //MARGE GAUCHE
        g.fillRect(position_x + longueur, position_y,margeSecurite,hauteur);        //MARGE Droite
        */
        
        ///////////////////////  ACCESSOIRES  /////////////////////////
        if (this.controleur.reqChaletDTO().MurActif.reqAccPresentFlag())
        {           
            ArrayList<Accessoire> acc = this.controleur.reqChaletDTO().MurActif.reqAccessoiresPresents();
            
            for (Accessoire A : acc)
            {
                if (A.reqAccValide())
                {
                    g.setColor(Color.gray);
                }
                else
                {
                    g.setColor(Color.red.darker());
                }
                int posx     = (int)((A.reqPositionX()-A.reqLongueur()/2)*PIXELS_PAR_POUCES);
                int posy     = (int)((A.reqPositionY()-A.reqHauteur()/2)*PIXELS_PAR_POUCES);
                int long_acc = (int)((A.reqLongueur())*PIXELS_PAR_POUCES);
                int h_acc    = (int)((A.reqHauteur())*PIXELS_PAR_POUCES);
                g.fillRect(posx,posy,long_acc ,h_acc);
   
            }
        }
                
    }
    
    
    private void dessinerCoteGauche(Graphics g, boolean inversion)
    {
        g.setColor(Color.blue.darker());                                                  //A changer pour livrable 5
        
        /////////////////    VARIABLES OBjETS  //////////////////
        Mur murGauche     =   this.controleur.reqChaletDTO().MurGauche;              //Définition des variables de bases
        Mur murFace       =   this.controleur.reqChaletDTO().MurFacade;
        int longueur      =  (int)(murFace.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int hauteur       =  (int)(murFace.reqDimensionsMur()[1]*PIXELS_PAR_POUCES);
        int margeSecurite =  (int)(murGauche.reqDimensionsMur()[2]*PIXELS_PAR_POUCES);
        int epaisseur     =  (int)(murGauche.reqDimensionsMur()[4]*PIXELS_PAR_POUCES);
        ///////////////////  VARIABLES DESSIN  ///////////
        int position_x = POSITION_X_CENTRE - longueur/2 - epaisseur/2;
        int position_y = POSITION_Y_CENTRE - hauteur/2;
        if (inversion)
            {position_x = POSITION_X_CENTRE + longueur/2;}
        ///////////////////   DESSIN  ////////////////////
        g.drawRect(position_x, position_y, epaisseur/2 , hauteur);
        g.fillRect(position_x, position_y, epaisseur/2 , hauteur);
        
    }
    
    private void dessinerCoteDroite(Graphics g, boolean inversion)
    {
        g.setColor(Color.blue.darker());
         /////////////////    VARIABLES OBJETS  //////////////////
        Mur murDroit      =   this.controleur.reqChaletDTO().MurDroit;
        Mur murFace       =   this.controleur.reqChaletDTO().MurFacade;
        int longueur      =  (int)(murFace.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int hauteur       =  (int)(murFace.reqDimensionsMur()[1]*PIXELS_PAR_POUCES);
        int margeSecurite =  (int)(murDroit.reqDimensionsMur()[2]*PIXELS_PAR_POUCES);
        int epaisseur     =  (int)(murDroit.reqDimensionsMur()[4]*PIXELS_PAR_POUCES);
        ///////////////////  VARIABLES DESSIN  ///////////
        int position_x = POSITION_X_CENTRE + longueur/2;
        int position_y = POSITION_Y_CENTRE - hauteur/2;
        if (inversion)
        {position_x = POSITION_X_CENTRE - longueur/2 - epaisseur/2;}
        ///////////////////   DESSIN  ////////////////////
        g.drawRect(position_x, position_y, epaisseur/2 , hauteur);
        g.fillRect(position_x, position_y, epaisseur/2 , hauteur);
        
    }
        private void dessinerCoteAvant(Graphics g, boolean inversion)
    {
        g.setColor(Color.green.darker());
         /////////////////    VARIABLES OBJETS  //////////////////
        Mur murAvant       =   this.controleur.reqChaletDTO().MurFacade;             
        Mur murCote        =   this.controleur.reqChaletDTO().MurDroit;
        int longueur       =  (int)(murCote.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int hauteur        =  (int)(murCote.reqDimensionsMur()[1]*PIXELS_PAR_POUCES);
        int margeSecurite  =  (int)(murAvant.reqDimensionsMur()[2]*PIXELS_PAR_POUCES);
        int epaisseur      =  (int)(murAvant.reqDimensionsMur()[4]*PIXELS_PAR_POUCES);
        ///////////////////  VARIABLES DESSIN  ///////////
        int position_x = POSITION_X_CENTRE + longueur/2;
        int position_y = POSITION_Y_CENTRE - hauteur/2;
        if (inversion)
        {position_x = POSITION_X_CENTRE - longueur/2 - epaisseur/2;}
        
        ///////////////////   DESSIN  ////////////////////
        g.drawRect(position_x, position_y, epaisseur/2 , hauteur);
        g.fillRect(position_x, position_y, epaisseur/2 , hauteur);
        
    }
        private void dessinerCoteArriere(Graphics g, boolean inversion)
    {
        g.setColor(Color.green.darker());
         /////////////////    VARIABLES OBJETS  //////////////////
        Mur murArriere     =   this.controleur.reqChaletDTO().MurArriere;
        Mur murCote        =   this.controleur.reqChaletDTO().MurDroit;
        int longueur       =  (int)(murCote.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int hauteur        =  (int)(murCote.reqDimensionsMur()[1]*PIXELS_PAR_POUCES);
        int margeSecurite  =  (int)(murArriere.reqDimensionsMur()[2]*PIXELS_PAR_POUCES);
        int epaisseur      =  (int)(murArriere.reqDimensionsMur()[4]*PIXELS_PAR_POUCES);
        ///////////////////  VARIABLES DESSIN  ///////////
        int position_x = POSITION_X_CENTRE - longueur/2 - epaisseur/2;
        int position_y = POSITION_Y_CENTRE - hauteur/2;
        if (inversion)
        {position_x = POSITION_X_CENTRE + longueur/2;}
        ///////////////////   DESSIN  ////////////////////
        g.drawRect(position_x, position_y, epaisseur/2 , hauteur);
        g.fillRect(position_x, position_y, epaisseur/2 , hauteur);
        
    }
    
    
    
    private void dessinerDessusToit(Graphics g){
        ////////////////  VARIABLES LISIBILITÉ  ///////////
        int arriere_longueur = (int)(this.controleur.reqChaletDTO().MurArriere.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int gauche_longueur  = (int)(this.controleur.reqChaletDTO().MurGauche.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int facade_longueur  = (int)(this.controleur.reqChaletDTO().MurFacade.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int droit_longueur   = (int)(this.controleur.reqChaletDTO().MurDroit.reqDimensionsMur()[0]*PIXELS_PAR_POUCES);
        int epaisseur        = (int)(this.controleur.reqChaletDTO().MurDroit.reqDimensionsMur()[4]*PIXELS_PAR_POUCES);
        int rainure          = (int)(this.controleur.reqChaletDTO().MurArriere.reqDimensionsMur()[3]*PIXELS_PAR_POUCES);
        int alt    = 0;
        int altgd  = 0;

        //////////////   Gestion du cas alternatif : superposition rainures differentres.
        if(false){
            alt = 1;
            altgd = 0;
        }
        else{
            alt = 0;
            altgd = 1;
        }
        
        g.setColor(Color.green.darker());
        //MurArriere
        int position_x = POSITION_X_CENTRE - arriere_longueur/2 + epaisseur*alt/2;
        int position_y = POSITION_Y_CENTRE - gauche_longueur/2;
        g.fillRect(position_x, position_y, arriere_longueur - epaisseur*alt, epaisseur/2);
        g.fillRect(position_x + epaisseur/2, position_y + epaisseur/2, arriere_longueur - epaisseur - epaisseur*alt, epaisseur/2);
        int x_topLeft = position_x;
        int y_topLeft = position_y;
        
        //Facade
        g.setColor(Color.green.darker());
        position_x = POSITION_X_CENTRE - facade_longueur/2 + epaisseur*alt/2;
        position_y = POSITION_Y_CENTRE + gauche_longueur/2 - epaisseur/2;
        g.fillRect(position_x, position_y, facade_longueur - epaisseur*alt , epaisseur/2);
        g.fillRect(position_x + epaisseur/2, position_y - epaisseur/2, facade_longueur - epaisseur - epaisseur*alt, epaisseur/2);
        int x_botLeft = position_x;
        int y_botLeft = position_y;
        
        g.setColor(Color.BLUE.darker());
        //MurGauche
        position_x = POSITION_X_CENTRE - arriere_longueur/2;
        position_y = POSITION_Y_CENTRE - gauche_longueur/2 + epaisseur*altgd/2;
        g.fillRect(position_x, position_y, epaisseur/2 , gauche_longueur - epaisseur*altgd);
        g.fillRect(position_x + epaisseur/2, position_y + epaisseur/2, epaisseur/2, gauche_longueur - epaisseur - epaisseur*altgd);
        
        //MurDroite
        position_x = POSITION_X_CENTRE + arriere_longueur/2 - epaisseur/2;
        position_y = POSITION_Y_CENTRE - droit_longueur/2 + epaisseur*altgd/2;
        g.fillRect(position_x, position_y, epaisseur/2 , droit_longueur - epaisseur*altgd);
        g.fillRect(position_x - epaisseur/2, position_y + epaisseur/2, epaisseur/2, droit_longueur - epaisseur - epaisseur*altgd);
        int x_topRight = position_x;
        int y_topRight = position_y;
        //Rainure_supp
        //Top_left
        g.setColor(Color.gray);
        g.fillRect(x_topLeft, y_topLeft + epaisseur/2 - rainure/2, epaisseur/2 +rainure/2, rainure);
        g.fillRect(x_topLeft+ epaisseur/2 -rainure/2, y_topLeft + epaisseur/2, rainure, rainure + epaisseur/2);
        g.fillRect(x_topLeft+ epaisseur/2, y_topLeft + epaisseur, epaisseur/2, rainure);
        //topRight
        g.fillRect(x_topRight-rainure/2, y_topRight - rainure/2, epaisseur/2 +rainure/2, rainure);
        g.fillRect(x_topRight-rainure/2, y_topRight, rainure, epaisseur/2 + rainure/2);
        g.fillRect(x_topRight-epaisseur/2, y_topRight + epaisseur/2, epaisseur/2 +rainure/2, rainure);
        //botLeft
        g.fillRect(x_botLeft, y_botLeft-rainure/2, rainure/2 +epaisseur/2, rainure);
        g.fillRect(x_botLeft+epaisseur/2-rainure/2, y_botLeft-rainure-epaisseur/2, rainure, rainure +epaisseur/2);
        g.fillRect(x_botLeft+epaisseur/2-rainure/2, y_botLeft-rainure-epaisseur/2, rainure/2+epaisseur/2, rainure );
        //botRight
        int x_botRight = x_botLeft +facade_longueur -epaisseur;                 
        g.fillRect(x_botRight, y_botLeft-rainure-epaisseur/2, rainure/2 +epaisseur/2, rainure);
        g.fillRect(x_botRight+epaisseur/2-rainure/2, y_botLeft-rainure-epaisseur/2, rainure, rainure+epaisseur/2);
        g.fillRect(x_botRight+epaisseur/2-rainure/2, y_botLeft-rainure/2, rainure/2+epaisseur/2, rainure);
    }
    
    private void dessinerPignon(Graphics g){}
    
    private void dessinerRallonge(Graphics g){}
    
}
