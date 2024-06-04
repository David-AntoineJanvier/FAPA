package ca.ulaval.glo2004.domaine.export;

import ca.ulaval.glo2004.domaine.*;
import static ca.ulaval.glo2004.domaine.Mur.OrientationMur.ARRIERE;
import static ca.ulaval.glo2004.domaine.Mur.OrientationMur.DROITE;
import static ca.ulaval.glo2004.domaine.Mur.OrientationMur.FACADE;
import static ca.ulaval.glo2004.domaine.Mur.OrientationMur.GAUCHE;
import ca.ulaval.glo2004.domaine.utils.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Arrays;

public class ExportSTL
{
    /* OUI ALORS LES AXES SONT EN X POUR LA LONGUEUR (VERS LA DROITE POTO), EN Y POUR L'ÉPAISSEUR (VERS L'INTÉRIEUR DE TON ÉCRAN MEC) ET EN Z POUR LA HAUTEUR (VERS MONSIEUR LE CHRIST) */
    private final String INDENT = "    ";
    private Chalet chalet = null;
    
    public ExportSTL(Chalet chalet)
    {
        this.chalet = chalet;
    }
    
    private void ajoutFacet(BufferedWriter fichierSTL, Triangle triangle)
    {
        try
        {
            fichierSTL.write(INDENT + "facet normal" + triangle.reqNormal() + "\n");
            fichierSTL.write(INDENT + INDENT + "outer loop\n");
            fichierSTL.write(INDENT + INDENT + INDENT + "vertex " + triangle.getPointA().reqCoordX() + " " + 
                    triangle.getPointA().reqCoordY() + " " + triangle.getPointA().reqCoordZ() + " " + "\n");
            fichierSTL.write(INDENT + INDENT + INDENT + "vertex " + triangle.getPointB().reqCoordX() + " " + 
                    triangle.getPointB().reqCoordY() + " " + triangle.getPointB().reqCoordZ() + " " + "\n");
            fichierSTL.write(INDENT + INDENT + INDENT + "vertex " + triangle.getPointC().reqCoordX() + " " + 
                    triangle.getPointC().reqCoordY() + " " + triangle.getPointC().reqCoordZ() + " " + "\n");
            fichierSTL.write(INDENT + INDENT + "endloop\n");
            fichierSTL.write(INDENT + "endfacet\n");
        }
        catch(IOException e){};
        
        return;
    }
    
    public void reinitialisationRepertoire(String chemin) 
    {
        File dir = new File(chemin);
        if (dir.exists()) {
            for (String fichier: dir.list()) {
                File fichierCourant = new File(dir.getPath(), fichier);
                fichierCourant.delete();
            }
        }
        else {
            dir.mkdir();
        }
    }
    
    private void creerFichierSTL(String nomFichier, ArrayList<Triangle> triangleDansMur)
    {
        try(BufferedWriter fichier = new BufferedWriter(new FileWriter(nomFichier)))
        {
            fichier.write("solid mur\n");
            
            for (Triangle i: triangleDansMur)
            {
                ajoutFacet(fichier, i);
            }
            
            fichier.write("endsolid mur\n");
        }
        catch (IOException e){}             
        
    }
    
    public void exporterMurBrut(String cheminFichier)
    {
        /* Mur Facade */
        String cheminFormate = cheminFichier + "ChalCLT_Brut_F.stl";
        formatageMurBrut(cheminFormate, this.chalet.reqMurFacade());
        
        /* Mur Arriere */
        cheminFormate = cheminFichier + "ChalCLT_Brut_A.stl";
        formatageMurBrut(cheminFormate, this.chalet.reqMurArriere());
        
        /* Mur Gauche */
        cheminFormate = cheminFichier + "ChalCLT_Brut_G.stl";
        formatageMurBrut(cheminFormate, this.chalet.reqMurGauche());
        
        /* Mur Droit */
        cheminFormate = cheminFichier + "ChalCLT_Brut_D.stl";
        formatageMurBrut(cheminFormate, this.chalet.reqMurDroit());
    }
    
    public void exporterMurFini(String cheminFichier)
    {
        String cheminFormate = cheminFichier + "ChalCLT_Fini_F.stl";
        formatageMurFini(cheminFormate, this.chalet.reqMurFacade());
        
        cheminFormate = cheminFichier + "ChalCLT_Fini_A.stl";
        formatageMurFini(cheminFormate, this.chalet.reqMurArriere());
        
        cheminFormate = cheminFichier + "ChalCLT_Fini_G.stl";
        formatageMurFini(cheminFormate, this.chalet.reqMurGauche());
        
        cheminFormate = cheminFichier + "ChalCLT_Fini_D.stl";
        formatageMurFini(cheminFormate, this.chalet.reqMurDroit());
    }
    
    public void exporterRetraits(String cheminFichier) 
    {
        String cheminFormate = cheminFichier + "ChalCLT_Retrait_F_";
        formatageRetraits(cheminFormate, this.chalet.reqMurFacade());
        
        cheminFormate = cheminFichier + "ChalCLT_Retrait_A_";
        formatageRetraits(cheminFormate, this.chalet.reqMurArriere());
        
        cheminFormate = cheminFichier + "ChalCLT_Retrait_G_";
        formatageRetraits(cheminFormate, this.chalet.reqMurGauche());
        
        cheminFormate = cheminFichier + "ChalCLT_Retrait_D_";
        formatageRetraits(cheminFormate, this.chalet.reqMurDroit());
    }
        
    private void formatageMurFini(String nomDuFichier, Mur murVersExporter)
    {
        String normal = "0 0 0";
        ArrayList<Triangle> trianglesDuMur = new ArrayList<Triangle>();
        float [] dims = murVersExporter.reqDimensionsMur();

        float longueur = dims[0];
        float epaisseur = dims[4];
        float hauteur = dims[1];
        //DÉFINITION DES POINTS AVEC RAINURE
        float rainureSup = dims[3]; 
        
        float offsetX = 0.0f;
        float offsetY = Mur.POSITION_Y_CENTRE - hauteur/2.0f;
        float debutMurInterieur = 0.0f;
        float debutMurExterieur = 0.0f;
        float finMurInterieur = 0.0f;
        float finMurExterieur = 0.0f;
        
        switch(murVersExporter.reqOrientation())
        {
            case FACADE:
            {
                debutMurInterieur = epaisseur/2.0f + rainureSup/2.0f;
                finMurInterieur = longueur - debutMurInterieur;
                finMurExterieur = longueur;
                
                offsetX = Mur.POSITION_X_CENTRE - longueur/2.0f;
                
                PointCartesien pA = new PointCartesien(0.0f, 0.0f, 0.0f);
                PointCartesien pB = new PointCartesien(0.0f, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pD = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,epaisseur, 0.0f);
                PointCartesien pE = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, epaisseur, 0.0f);
                PointCartesien pF = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pG = new PointCartesien(longueur, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pH = new PointCartesien(longueur, 0.0f, 0.0f);
                PointCartesien pI = new PointCartesien(0.0f, 0.0f, hauteur);
                PointCartesien pJ = new PointCartesien(0.0f, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pK = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pL = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,epaisseur, hauteur);
                PointCartesien pM = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, epaisseur, hauteur);
                PointCartesien pN = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pO = new PointCartesien(longueur, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pP = new PointCartesien(longueur, 0.0f, hauteur);
                
                //CRÉATION DES TRIANGLES DE BASE AVEC RAINURE
                trianglesDuMur.add(new Triangle (pD, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pF, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pB, pA, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pA, pH, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pP, pO, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pO, pJ, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pN, pK, "0 0 0"));
                trianglesDuMur.add(new Triangle (pK, pL, pM, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pA, pB, "0 0 0"));
                trianglesDuMur.add(new Triangle (pI, pJ, pA, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pB, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pC, pD, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pN, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pN, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pP, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pG, pH, pP, "0 0 0"));
            }break;
            
            case GAUCHE:
            {
                debutMurInterieur = epaisseur + rainureSup;
                debutMurExterieur = epaisseur/2.0f + rainureSup/2.0f;
                finMurInterieur = longueur - debutMurInterieur;
                finMurExterieur = longueur - debutMurExterieur;
                offsetX = Mur.POSITION_X_CENTRE - longueur/2.0f;
                
                PointCartesien pA = new PointCartesien(0.0f, longueur - (epaisseur/2.0f + rainureSup/2.0f), 0.0f);
                PointCartesien pB = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, longueur - (epaisseur/2.0f + rainureSup/2.0f), 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, longueur - (epaisseur + rainureSup), 0.0f);
                PointCartesien pD = new PointCartesien(epaisseur, longueur - (epaisseur+rainureSup), 0.0f);
                PointCartesien pE = new PointCartesien(epaisseur, epaisseur - rainureSup, 0.0f);
                PointCartesien pF = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur - rainureSup, 0.0f);
                PointCartesien pG = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pH = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pI = new PointCartesien(0.0f, longueur - (epaisseur/2.0f + rainureSup/2.0f), hauteur);
                PointCartesien pJ = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, longueur - (epaisseur/2.0f + rainureSup/2.0f), hauteur);
                PointCartesien pK = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, longueur - (epaisseur+rainureSup), hauteur);
                PointCartesien pL = new PointCartesien(epaisseur, longueur - (epaisseur+rainureSup), hauteur);
                PointCartesien pM = new PointCartesien(epaisseur, epaisseur - rainureSup, hauteur);
                PointCartesien pN = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur - rainureSup, hauteur);
                PointCartesien pO = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pP = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                //CRÉATION DES TRIANGLES DE BASE AVEC RAINURE
                trianglesDuMur.add(new Triangle (pD, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pF, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pB, pA, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pA, pH, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pP, pO, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pO, pJ, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pN, pK, "0 0 0"));
                trianglesDuMur.add(new Triangle (pK, pL, pM, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pA, pB, "0 0 0"));
                trianglesDuMur.add(new Triangle (pI, pJ, pA, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pB, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pC, pD, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pN, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pN, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pP, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pG, pH, pP, "0 0 0"));
            }break;
            
            case ARRIERE:
            {
                debutMurInterieur = epaisseur/2.0f + rainureSup/2.0f;
                finMurInterieur = longueur - debutMurInterieur;
                finMurExterieur = longueur;
                
                offsetX = Mur.POSITION_X_CENTRE - longueur/2.0f;
                
                float longueurGauche = this.chalet.reqMurGauche().reqDimensionsMur()[0];
                
                PointCartesien pA = new PointCartesien(longueur, longueurGauche, 0.0f);
                PointCartesien pB = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, 0.0f);
                PointCartesien pC = new PointCartesien(longueur - (epaisseur/2.0f+rainureSup/2.0f), -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, 0.0f);
                PointCartesien pD = new PointCartesien(longueur - (epaisseur/2.0f+rainureSup/2.0f),-epaisseur + longueurGauche, 0.0f);
                PointCartesien pE = new PointCartesien(epaisseur/2.0f - rainureSup/2.0f, -epaisseur + longueurGauche, 0.0f);
                PointCartesien pF = new PointCartesien(epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, 0.0f);
                PointCartesien pG = new PointCartesien(0.0f, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, 0.0f);
                PointCartesien pH = new PointCartesien(0.0f, longueurGauche, 0.0f);
                PointCartesien pI = new PointCartesien(longueur, longueurGauche, hauteur);
                PointCartesien pJ = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, hauteur);
                PointCartesien pK = new PointCartesien(longueur - (epaisseur/2.0f+rainureSup/2.0f), -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, hauteur);
                PointCartesien pL = new PointCartesien(longueur - (epaisseur/2.0f+rainureSup/2.0f), -epaisseur + longueurGauche, hauteur);
                PointCartesien pM = new PointCartesien(epaisseur/2.0f - rainureSup/2.0f, -epaisseur + longueurGauche, hauteur);
                PointCartesien pN = new PointCartesien(epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, hauteur);
                PointCartesien pO = new PointCartesien(0.0f, -epaisseur/2.0f - rainureSup/2.0f + longueurGauche, hauteur);
                PointCartesien pP = new PointCartesien(0.0f, longueurGauche, hauteur);
                
                //CRÉATION DES TRIANGLES DE BASE AVEC RAINURE
                trianglesDuMur.add(new Triangle (pD, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pF, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pB, pA, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pA, pH, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pP, pO, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pO, pJ, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pN, pK, "0 0 0"));
                trianglesDuMur.add(new Triangle (pK, pL, pM, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pA, pB, "0 0 0"));
                trianglesDuMur.add(new Triangle (pI, pJ, pA, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pB, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pC, pD, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pN, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pN, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pP, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pG, pH, pP, "0 0 0"));
            }break;
            
            case DROITE:
            {
                debutMurInterieur = epaisseur + rainureSup;
                debutMurExterieur = epaisseur/2.0f + rainureSup/2.0f;
                finMurInterieur = longueur - debutMurInterieur;
                finMurExterieur = longueur - debutMurExterieur;
                
                offsetX = Mur.POSITION_X_CENTRE - longueur/2.0f;
                
                float longueurFacade = this.chalet.reqMurFacade().reqDimensionsMur()[0];
                
                PointCartesien pA = new PointCartesien(longueurFacade, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pB = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, epaisseur/2.0f + rainureSup/2.0f, 0.0f);
                PointCartesien pC = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, epaisseur+rainureSup, 0.0f);
                PointCartesien pD = new PointCartesien(-epaisseur + longueurFacade, epaisseur+rainureSup, 0.0f);
                PointCartesien pE = new PointCartesien(-epaisseur + longueurFacade, longueur - epaisseur - rainureSup, 0.0f);
                PointCartesien pF = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, longueur - epaisseur - rainureSup, 0.0f);
                PointCartesien pG = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pH = new PointCartesien(longueurFacade, longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pI = new PointCartesien(longueurFacade, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pJ = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, epaisseur/2.0f + rainureSup/2.0f, hauteur);
                PointCartesien pK = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, epaisseur+rainureSup, hauteur);
                PointCartesien pL = new PointCartesien(-epaisseur + longueurFacade, epaisseur+rainureSup, hauteur);
                PointCartesien pM = new PointCartesien(-epaisseur + longueurFacade, longueur - epaisseur - rainureSup, hauteur);
                PointCartesien pN = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, longueur - epaisseur - rainureSup, hauteur);
                PointCartesien pO = new PointCartesien(-epaisseur/2.0f - rainureSup/2.0f + longueurFacade, longueur - epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pP = new PointCartesien(longueurFacade, longueur - epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                //CRÉATION DES TRIANGLES DE BASE AVEC RAINURE
                trianglesDuMur.add(new Triangle (pD, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pF, pE, pC, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pB, pA, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pG, pA, pH, "0 0 -1"));
                trianglesDuMur.add(new Triangle (pP, pO, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pO, pJ, pI, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pN, pK, "0 0 0"));
                trianglesDuMur.add(new Triangle (pK, pL, pM, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pA, pB, "0 0 0"));
                trianglesDuMur.add(new Triangle (pI, pJ, pA, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pB, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pJ, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pK, pC, "0 0 0"));
                trianglesDuMur.add(new Triangle (pL, pC, pD, "0 0 0"));
                trianglesDuMur.add(new Triangle (pM, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pE, pN, "0 0 0"));
                trianglesDuMur.add(new Triangle (pN, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pF, pN, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pP, pO, pG, "0 0 0"));
                trianglesDuMur.add(new Triangle (pG, pH, pP, "0 0 0"));
            }break;
            
            default:{};
        }
       
        PointCartesien pointA = new PointCartesien(0.0f, 0.0f, 0.0f);
        PointCartesien pointB = new PointCartesien(dims[0], 0.0f, 0.0f);
        PointCartesien pointC = new PointCartesien(dims[0], 0.0f, dims[1]);
        PointCartesien pointD = new PointCartesien(0.0f, 0.0f, dims[1]);
        PointCartesien pointE = new PointCartesien(0.0f, -dims[4], dims[1]);
        PointCartesien pointF = new PointCartesien(0.0f, -dims[4], 0.0f);
        PointCartesien pointG = new PointCartesien(dims[0], -dims[4], 0.0f);
        PointCartesien pointH = new PointCartesien(dims[0], -dims[4], dims[1]);
        
        if (murVersExporter.reqAccPresentFlag()) 
        {
            ArrayList<Accessoire> listeAccessoires = murVersExporter.reqAccessoiresPresents();
            
            //Collections.sort(listeAccessoires);
            
            /*
            //Traitement de la gauche du premier accessoire
            pointB = new PointCartesien((listeAccessoires.get(0).reqPositionX()-listeAccessoires.get(0).reqLongueur()/2)-offsetX, 0.0f, 0.0f);
            pointC = new PointCartesien((listeAccessoires.get(0).reqPositionX()-listeAccessoires.get(0).reqLongueur()/2)-offsetX, 0.0f, dims[1]);
            pointD = new PointCartesien(0.0f, 0.0f, dims[1]);

            pointE = new PointCartesien(0.0f, -dims[4], dims[1]);
            pointF = new PointCartesien(0.0f, -dims[4], 0.0f);
            pointG = new PointCartesien((listeAccessoires.get(0).reqPositionX()-listeAccessoires.get(0).reqLongueur()/2)-offsetX, -dims[4], 0.0f);
            pointH = new PointCartesien((listeAccessoires.get(0).reqPositionX()-listeAccessoires.get(0).reqLongueur()/2)-offsetX, -dims[4], dims[1]);
            
            trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
            trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
            trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
            trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
            */
            
            // Dessine l'intérieur de l'Accessoire
            for (Accessoire acc : listeAccessoires)
            {
                if (acc.reqAccValide())
                {
                    float accGauche = acc.reqPositionX() - (acc.reqLongueur()/2) - offsetX;
                    float accDroit = acc.reqPositionX() + (acc.reqLongueur()/2) - offsetX;
                    float accHaut = (2 * Mur.POSITION_Y_CENTRE - acc.reqPositionY()) + (acc.reqHauteur()/2) - offsetY;
                    float accBas = (2 * Mur.POSITION_Y_CENTRE - acc.reqPositionY()) - (acc.reqHauteur()/2) - offsetY;
                    float accEpaisseur = dims[4];

                    switch(murVersExporter.reqOrientation())
                    {
                        case FACADE:
                        {
                            pointA = new PointCartesien(accGauche, 0.0f , accBas);
                            pointB = new PointCartesien(accDroit, 0.0f, accBas);
                            pointC = new PointCartesien(accDroit, 0.0f, accHaut);
                            pointD = new PointCartesien(accGauche, 0.0f, accHaut);

                            pointE = new PointCartesien(accGauche, accEpaisseur, accHaut);
                            pointF = new PointCartesien(accGauche,  accEpaisseur, accBas);
                            pointG = new PointCartesien(accDroit,  accEpaisseur, accBas);
                            pointH = new PointCartesien(accDroit,  accEpaisseur, accHaut);
                        }break;
                        
                        case GAUCHE:
                        {
                            pointA = new PointCartesien(0.0f, longueur-accGauche , accBas);
                            pointB = new PointCartesien(0.0f, longueur-accDroit, accBas);
                            pointC = new PointCartesien(0.0f, longueur-accDroit, accHaut);
                            pointD = new PointCartesien(0.0f, longueur-accGauche, accHaut);

                            pointE = new PointCartesien(accEpaisseur, longueur-accGauche, accHaut);
                            pointF = new PointCartesien(accEpaisseur,  longueur-accGauche, accBas);
                            pointG = new PointCartesien(accEpaisseur,  longueur-accDroit, accBas);
                            pointH = new PointCartesien(accEpaisseur,  longueur-accDroit, accHaut);
                        }break;
                        
                        case ARRIERE:
                        {
                            float longueurGauche = this.chalet.reqMurGauche().reqDimensionsMur()[0];
        
                            pointA = new PointCartesien(longueur-accGauche, 0.0f+longueurGauche , accBas);
                            pointB = new PointCartesien(longueur-accDroit, 0.0f+longueurGauche, accBas);
                            pointC = new PointCartesien(longueur-accDroit, 0.0f+longueurGauche, accHaut);
                            pointD = new PointCartesien(longueur-accGauche, 0.0f+longueurGauche, accHaut);

                            pointE = new PointCartesien(longueur-accGauche, -accEpaisseur+longueurGauche, accHaut);
                            pointF = new PointCartesien(longueur-accGauche,  -accEpaisseur+longueurGauche, accBas);
                            pointG = new PointCartesien(longueur-accDroit,  -accEpaisseur+longueurGauche, accBas);
                            pointH = new PointCartesien(longueur-accDroit,  -accEpaisseur+longueurGauche, accHaut);
                        }break;

                        
                        case DROITE:
                        {
                            float longueurFacade = this.chalet.reqMurFacade().reqDimensionsMur()[0];
                            
                            pointA = new PointCartesien(0.0f+longueurFacade, accGauche , accBas);
                            pointB = new PointCartesien(0.0f+longueurFacade, accDroit, accBas);
                            pointC = new PointCartesien(0.0f+longueurFacade, accDroit, accHaut);
                            pointD = new PointCartesien(0.0f+longueurFacade, accGauche, accHaut);

                            pointE = new PointCartesien(-accEpaisseur+longueurFacade, accGauche, accHaut);
                            pointF = new PointCartesien(-accEpaisseur+longueurFacade,  accGauche, accBas);
                            pointG = new PointCartesien(-accEpaisseur+longueurFacade,  accDroit, accBas);
                            pointH = new PointCartesien(-accEpaisseur+longueurFacade,  accDroit, accHaut);
                        }break;
                        
                        default:{};
                    }
                    
                    // Sol de l'accessoire
                    normal = "0 0 -1";
                    trianglesDuMur.add(new Triangle(pointA, pointB, pointG, normal));
                    trianglesDuMur.add(new Triangle(pointA, pointF, pointG, normal));

                    // Interieur gauche de l'accessoire
                    normal = "-1 0 0";
                    trianglesDuMur.add(new Triangle(pointA, pointF, pointE, normal));
                    trianglesDuMur.add(new Triangle(pointA, pointD, pointE, normal));

                    // Plafond de l'accessoire
                    normal = "0 0 1";
                    trianglesDuMur.add(new Triangle(pointD, pointE, pointH, normal));
                    trianglesDuMur.add(new Triangle(pointD, pointC, pointH, normal));

                    // Interieur droit de l'accessoire
                    normal = "1 0 0";
                    trianglesDuMur.add(new Triangle(pointC, pointH, pointG, normal));
                    trianglesDuMur.add(new Triangle(pointC, pointB, pointG, normal));
                }
            }
            
            /*
            for (int i=0; i < listeAccessoires.size(); i++) 
            {
                float droiteAcc = (listeAccessoires.get(i).reqPositionX()+listeAccessoires.get(i).reqLongueur()/2)-offsetX;
                float gaucheAcc = (listeAccessoires.get(i).reqPositionX()-listeAccessoires.get(i).reqLongueur()/2)-offsetX;
                float basAcc = ((2*Mur.POSITION_Y_CENTRE-listeAccessoires.get(i).reqPositionY())-listeAccessoires.get(i).reqHauteur()/2)-offsetY;
                float hautAcc = ((2*Mur.POSITION_Y_CENTRE-listeAccessoires.get(i).reqPositionY())+listeAccessoires.get(i).reqHauteur()/2)-offsetY;
                
                if (i+1 < listeAccessoires.size()) 
                {
                    float gaucheNextAcc = (listeAccessoires.get(i+1).reqPositionX()-listeAccessoires.get(i+1).reqLongueur()/2)-offsetX;
                
                    if (droiteAcc<=gaucheNextAcc) 
                    {                        
                        //Traitement en-dessous de l'accessoire
                        pointA = new PointCartesien(gaucheAcc, 0.0f, 0.0f);
                        pointB = new PointCartesien(droiteAcc, 0.0f, 0.0f);
                        pointC = new PointCartesien(droiteAcc,0.0f, basAcc);
                        pointD = new PointCartesien(gaucheAcc, 0.0f, basAcc);
                        pointE = new PointCartesien(gaucheAcc, -dims[4], basAcc);
                        pointF = new PointCartesien(gaucheAcc, -dims[4], 0.0f);
                        pointG = new PointCartesien(droiteAcc, -dims[4], 0.0f);
                        pointH = new PointCartesien(droiteAcc, -dims[4], basAcc);

                        trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));

                        //Traitement au-dessus de l'accessoire
                        pointA = new PointCartesien(gaucheAcc, 0.0f, hautAcc);
                        pointB = new PointCartesien(droiteAcc, 0.0f, hautAcc);
                        pointC = new PointCartesien(droiteAcc,0.0f, dims[1]);
                        pointD = new PointCartesien(gaucheAcc, 0.0f, dims[1]);
                        pointE = new PointCartesien(gaucheAcc, -dims[4], dims[1]);
                        pointF = new PointCartesien(gaucheAcc, -dims[4], hautAcc);
                        pointG = new PointCartesien(droiteAcc, -dims[4], hautAcc);
                        pointH = new PointCartesien(droiteAcc, -dims[4], dims[1]);

                        trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                        
                        //Traitement entre les deux accessoires
                        pointA = new PointCartesien(droiteAcc, 0.0f, 0.0f);
                        pointB = new PointCartesien(gaucheNextAcc, 0.0f, 0.0f);
                        pointC = new PointCartesien(gaucheNextAcc,0.0f, dims[1]);
                        pointD = new PointCartesien(droiteAcc, 0.0f, dims[1]);
                        pointE = new PointCartesien(droiteAcc, -dims[4], dims[1]);
                        pointF = new PointCartesien(droiteAcc, -dims[4], 0.0f);
                        pointG = new PointCartesien(gaucheNextAcc, -dims[4], 0.0f);
                        pointH = new PointCartesien(gaucheNextAcc, -dims[4], dims[1]);

                        trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                    }
                    else {
                        LinkedList<Accessoire> fileDebutX = new LinkedList<>();
                        
                        LinkedList<Accessoire> fileFinX = new LinkedList<>();
                        //fileFinX.add(listeAccessoires.get(i));
                        
                        LinkedList<Accessoire> fileY = new LinkedList<>();
                        //fileY.add (listeAccessoires.get(i));
                        
                        LinkedList<Accessoire> minFileY = new LinkedList<>();
                        LinkedList<Accessoire> minFileX = new LinkedList<>();
                        
                        float maxValue = droiteAcc;
                        float borneGauche = gaucheAcc;
                        boolean superFini = false;
                        boolean premiereIter = false;
                        
                        for (int j=i; j < 2*(listeAccessoires.size()); j++) {
                            float borneInf = 0.0f;
                            float borneSup = dims[1];
                            float borneDroite = dims[0];
                            
                            if (superFini == false) {
                                
                                if (j<listeAccessoires.size()-1 && superFini == false) {
                                    float droiteAccSuper = (listeAccessoires.get(j+1).reqPositionX()+listeAccessoires.get(j+1).reqLongueur()/2)-offsetX;

                                    if (droiteAccSuper>maxValue) {
                                        maxValue = droiteAccSuper;
                                    }

                                    float gaucheNextAccSuper = (listeAccessoires.get(j+1).reqPositionX()-listeAccessoires.get(j+1).reqLongueur()/2)-offsetX;

                                    if (maxValue>gaucheNextAccSuper) {
                                        fileDebutX.add(listeAccessoires.get(j+1));
                                        fileFinX.add(listeAccessoires.get(j));
                                        fileY.add (listeAccessoires.get(j));
                                        if (premiereIter) {
                                            i++;
                                        }
                                        premiereIter = true;
                                    }
                                    else {
                                        fileFinX.add(listeAccessoires.get(j));
                                        fileY.add (listeAccessoires.get(j));
                                        superFini = true;
                                    }
                                }
                            
                                int longueurFileY = fileY.size();
                                int longueurFileX = fileFinX.size();

                                for (int l=0; l<longueurFileY; l++) {
                                    int indexMin = i;
                                    
                                    for (int k=0; k<fileY.size(); k++) {
                                        if ((((2*Mur.POSITION_Y_CENTRE-fileY.get(k).reqPositionY())-fileY.get(k).reqHauteur()/2)-offsetY)<borneSup) {
                                            borneSup = (((2*Mur.POSITION_Y_CENTRE-fileY.get(k).reqPositionY())-fileY.get(k).reqHauteur()/2)-offsetY);
                                            indexMin = k;
                                        }
                                    }
                                    minFileY.add(fileY.get(indexMin));
                                    fileY.remove(fileY.get(indexMin));
                                    borneSup = dims[1];
                                }

                                for (int l=0; l<longueurFileX; l++) {
                                    int indexMin = i;
                                    
                                    for (int k=0; k<fileFinX.size(); k++) {
                                        if (((fileFinX.get(k).reqPositionX()+fileFinX.get(k).reqLongueur()/2)-offsetX)<borneDroite) {
                                            borneDroite = (fileFinX.get(k).reqPositionX()+fileFinX.get(k).reqLongueur()/2)-offsetX;
                                            indexMin = k;
                                        }
                                    }
                                    minFileX.add(fileFinX.get(indexMin));
                                    fileFinX.remove(fileFinX.get(indexMin));
                                    borneDroite = dims[0];
                                }
                            }
                            
                            if (fileDebutX.isEmpty() && minFileX.isEmpty()) {
                                break;
                            }
                            else {
                                if (fileDebutX.isEmpty()) {
                                    borneDroite = ((minFileX.get(0).reqPositionX()+minFileX.get(0).reqLongueur()/2)-offsetX);
                                    minFileX.removeFirst();
                                }
                                else if (minFileX.isEmpty()) {
                                    borneDroite = ((fileDebutX.get(0).reqPositionX()-fileDebutX.get(0).reqLongueur()/2)-offsetX);
                                    fileDebutX.removeFirst();
                                }
                                else {
                                    if (((fileDebutX.get(0).reqPositionX()-fileDebutX.get(0).reqLongueur()/2)-offsetX)<((minFileX.get(0).reqPositionX()+minFileX.get(0).reqLongueur()/2)-offsetX)) {
                                        borneDroite = ((fileDebutX.get(0).reqPositionX()-fileDebutX.get(0).reqLongueur()/2)-offsetX);
                                        fileDebutX.removeFirst();
                                    }
                                    else {
                                        borneDroite = ((minFileX.get(0).reqPositionX()+minFileX.get(0).reqLongueur()/2)-offsetX);
                                        minFileX.removeFirst();
                                    }
                                }

                                for (Accessoire a : minFileY) {
                                    borneSup = (((2*Mur.POSITION_Y_CENTRE-a.reqPositionY())-a.reqHauteur()/2)-offsetY);

                                    //Traitement entre les accessoires paratageant un même X
                                    pointA = new PointCartesien(borneGauche, 0.0f, borneInf);
                                    pointB = new PointCartesien(borneDroite, 0.0f, borneInf);
                                    pointC = new PointCartesien(borneDroite,0.0f, borneSup);
                                    pointD = new PointCartesien(borneGauche, 0.0f, borneSup);
                                    pointE = new PointCartesien(borneGauche, -dims[4], borneSup);
                                    pointF = new PointCartesien(borneGauche, -dims[4], borneInf);
                                    pointG = new PointCartesien(borneDroite, -dims[4], borneInf);
                                    pointH = new PointCartesien(borneDroite, -dims[4], borneSup);

                                    trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                                    trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                                    trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                                    trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));

                                    borneInf = borneSup + a.reqHauteur();
                                }
                                
                                //Traitement au-dessus du plus haut accessoire
                                pointA = new PointCartesien(borneGauche, 0.0f, borneInf);
                                pointB = new PointCartesien(borneDroite, 0.0f, borneInf);
                                pointC = new PointCartesien(borneDroite,0.0f, dims[1]);
                                pointD = new PointCartesien(borneGauche, 0.0f, dims[1]);
                                pointE = new PointCartesien(borneGauche, -dims[4], dims[1]);
                                pointF = new PointCartesien(borneGauche, -dims[4], borneInf);
                                pointG = new PointCartesien(borneDroite, -dims[4], borneInf);
                                pointH = new PointCartesien(borneDroite, -dims[4], dims[1]);

                                trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                                trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                                trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                                trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                                
                                borneGauche = borneDroite;
                                
                                //Enlève les accessoires en Y déjà passés
                                for (Accessoire acc : minFileY) {
                                    if (((acc.reqPositionX()+acc.reqLongueur()/2)-offsetX)==borneGauche) {
                                        minFileY.remove(acc);
                                    }
                                }
                            }
                        }
                        
                        //Traitment à droite du dernier accessoire superposé
                        if (i+1<listeAccessoires.size()) {
                            float gaucheNextAccSuper = (listeAccessoires.get(i+1).reqPositionX()-listeAccessoires.get(i+1).reqLongueur()/2)-offsetX;
                            
                            //Traitement entre les accessoires
                            pointA = new PointCartesien(borneGauche, 0.0f, 0.0f);
                            pointB = new PointCartesien(gaucheNextAccSuper, 0.0f, 0.0f);
                            pointC = new PointCartesien(gaucheNextAccSuper,0.0f, dims[1]);
                            pointD = new PointCartesien(borneGauche, 0.0f, dims[1]);
                            pointE = new PointCartesien(borneGauche, -dims[4], dims[1]);
                            pointF = new PointCartesien(borneGauche, -dims[4], 0.0f);
                            pointG = new PointCartesien(gaucheNextAccSuper, -dims[4], 0.0f);
                            pointH = new PointCartesien(gaucheNextAccSuper, -dims[4], dims[1]);

                            trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                            trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                            trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                            trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                        }
                    }
                }
                else 
                {
                    //Traitement en-dessous du dernier accessoire
                    pointA = new PointCartesien(gaucheAcc, 0.0f, 0.0f);
                    pointB = new PointCartesien(droiteAcc, 0.0f, 0.0f);
                    pointC = new PointCartesien(droiteAcc,0.0f, basAcc);
                    pointD = new PointCartesien(gaucheAcc, 0.0f, basAcc);
                    pointE = new PointCartesien(gaucheAcc, -dims[4], basAcc);
                    pointF = new PointCartesien(gaucheAcc, -dims[4], 0.0f);
                    pointG = new PointCartesien(droiteAcc, -dims[4], 0.0f);
                    pointH = new PointCartesien(droiteAcc, -dims[4], basAcc);

                    trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));

                    //Traitement au-dessus du dernier accessoire
                    pointA = new PointCartesien(gaucheAcc, 0.0f, hautAcc);
                    pointB = new PointCartesien(droiteAcc, 0.0f, hautAcc);
                    pointC = new PointCartesien(droiteAcc,0.0f, dims[1]);
                    pointD = new PointCartesien(gaucheAcc, 0.0f, dims[1]);
                    pointE = new PointCartesien(gaucheAcc, -dims[4], dims[1]);
                    pointF = new PointCartesien(gaucheAcc, -dims[4], hautAcc);
                    pointG = new PointCartesien(droiteAcc, -dims[4], hautAcc);
                    pointH = new PointCartesien(droiteAcc, -dims[4], dims[1]);

                    trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                    
                    //Traitement de la droite du dernier accessoire
                    pointA = new PointCartesien(droiteAcc, 0.0f, 0.0f);
                    pointB = new PointCartesien(dims[0], 0.0f, 0.0f);
                    pointC = new PointCartesien(dims[0],0.0f, dims[1]);
                    pointD = new PointCartesien(droiteAcc, 0.0f, dims[1]);
                    pointE = new PointCartesien(droiteAcc, -dims[4], dims[1]);
                    pointF = new PointCartesien(droiteAcc, -dims[4], 0.0f);
                    pointG = new PointCartesien(dims[0], -dims[4], 0.0f);
                    pointH = new PointCartesien(dims[0], -dims[4], dims[1]);

                    trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                    trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                }
            }
            :) */
            
            ArrayList<Float> listeX = new ArrayList<Float>();
            ArrayList<Float> listeY = new ArrayList<Float>();
            Float[] listeSortedX = null;
            Float[] listeSortedY = null;
            boolean checkValide = true;
            
            listeX.add(debutMurExterieur);
            listeX.add(finMurExterieur);
            
            listeY.add(0.0f);
            listeY.add(dims[1]);
            
            for (Accessoire acc : listeAccessoires) 
            {
                if (acc.reqAccValide())
                {
                    listeX.add(acc.reqPositionX() - (acc.reqLongueur()/2) - offsetX);
                    listeX.add(acc.reqPositionX() + (acc.reqLongueur()/2) - offsetX);

                    listeY.add((2 * Mur.POSITION_Y_CENTRE - acc.reqPositionY()) + (acc.reqHauteur()/2) - offsetY);
                    listeY.add((2 * Mur.POSITION_Y_CENTRE - acc.reqPositionY()) - (acc.reqHauteur()/2) - offsetY);   
                }
            }
            
            listeSortedX = listeX.toArray(new Float[listeX.size()]);
            Arrays.sort(listeSortedX);
            listeSortedY = listeY.toArray(new Float[listeY.size()]);
            Arrays.sort(listeSortedY);
            
            for (int i=0; i<listeX.size()-1; i++) 
            {
                for (int j=0; j<listeY.size()-1; j++) 
                {
                    checkValide = true;
                    
                    for (Accessoire acc : listeAccessoires) 
                    {
                        if (acc.reqAccValide())
                        {
                            float droiteAcc = (acc.reqPositionX()+acc.reqLongueur()/2)-offsetX;
                            float gaucheAcc = (acc.reqPositionX()-acc.reqLongueur()/2)-offsetX;
                            float basAcc = ((2*Mur.POSITION_Y_CENTRE-acc.reqPositionY())-acc.reqHauteur()/2)-offsetY;
                            float hautAcc = ((2*Mur.POSITION_Y_CENTRE-acc.reqPositionY())+acc.reqHauteur()/2)-offsetY;

                            if ((listeSortedX[i+1]>gaucheAcc && listeSortedX[i+1]<=droiteAcc) && (listeSortedY[j+1]>basAcc&&listeSortedY[j+1]<=hautAcc)) 
                            {
                                checkValide = false;
                                break;
                            }   
                        }
                    }
                   
                    if (checkValide) {
                        switch(murVersExporter.reqOrientation())
                        {
                            case FACADE:
                            {
                                if (i == 0) {
                                    pointA = new PointCartesien(debutMurInterieur, dims[4], listeSortedY[j]);
                                    pointD = new PointCartesien(debutMurInterieur, dims[4], listeSortedY[j+1]);
                                }
                                else {
                                    pointA = new PointCartesien(listeSortedX[i], dims[4], listeSortedY[j]);
                                    pointD = new PointCartesien(listeSortedX[i], dims[4], listeSortedY[j+1]);
                                }

                                if (i == listeX.size()-2) {
                                    pointB = new PointCartesien(finMurInterieur, dims[4], listeSortedY[j]);
                                    pointC = new PointCartesien(finMurInterieur, dims[4], listeSortedY[j+1]);
                                }
                                else {
                                    pointB = new PointCartesien(listeSortedX[i+1], dims[4], listeSortedY[j]);
                                    pointC = new PointCartesien(listeSortedX[i+1], dims[4], listeSortedY[j+1]);
                                }

                                pointE = new PointCartesien(listeSortedX[i], 0.0f, listeSortedY[j+1]);
                                pointF = new PointCartesien(listeSortedX[i], 0.0f, listeSortedY[j]);
                                pointG = new PointCartesien(listeSortedX[i+1], 0.0f, listeSortedY[j]);
                                pointH = new PointCartesien(listeSortedX[i+1], 0.0f, listeSortedY[j+1]);
                            }break;
                            
                            case GAUCHE:
                            {
                                if (i == 0) {
                                    pointA = new PointCartesien(dims[4], longueur - debutMurInterieur, listeSortedY[j]);
                                    pointD = new PointCartesien(dims[4], longueur - debutMurInterieur, listeSortedY[j+1]);
                                }
                                else {
                                    pointA = new PointCartesien(dims[4], longueur - listeSortedX[i], listeSortedY[j]);
                                    pointD = new PointCartesien(dims[4], longueur - listeSortedX[i], listeSortedY[j+1]);
                                }

                                if (i == listeX.size()-2) {
                                    pointB = new PointCartesien(dims[4], longueur - finMurInterieur, listeSortedY[j]);
                                    pointC = new PointCartesien(dims[4], longueur - finMurInterieur, listeSortedY[j+1]);
                                }
                                else {
                                    pointB = new PointCartesien(dims[4], longueur - listeSortedX[i+1], listeSortedY[j]);
                                    pointC = new PointCartesien(dims[4], longueur - listeSortedX[i+1], listeSortedY[j+1]);
                                }

                                pointE = new PointCartesien(0.0f, longueur - listeSortedX[i], listeSortedY[j+1]);
                                pointF = new PointCartesien(0.0f, longueur - listeSortedX[i], listeSortedY[j]);
                                pointG = new PointCartesien(0.0f, longueur - listeSortedX[i+1], listeSortedY[j]);
                                pointH = new PointCartesien(0.0f, longueur - listeSortedX[i+1], listeSortedY[j+1]);
                            }break;
                            
                            case ARRIERE:
                            {
                                float longueurGauche = this.chalet.reqMurGauche().reqDimensionsMur()[0];
                                
                                if (i == 0) {
                                    pointA = new PointCartesien(longueur - debutMurInterieur, -dims[4] + longueurGauche, listeSortedY[j]);
                                    pointD = new PointCartesien(longueur - debutMurInterieur, -dims[4] + longueurGauche, listeSortedY[j+1]);
                                }
                                else {
                                    pointA = new PointCartesien(longueur - listeSortedX[i], -dims[4] + longueurGauche, listeSortedY[j]);
                                    pointD = new PointCartesien(longueur - listeSortedX[i], -dims[4] + longueurGauche, listeSortedY[j+1]);
                                }

                                if (i == listeX.size()-2) {
                                    pointB = new PointCartesien(longueur - finMurInterieur, -dims[4] + longueurGauche, listeSortedY[j]);
                                    pointC = new PointCartesien(longueur - finMurInterieur, -dims[4] + longueurGauche, listeSortedY[j+1]);
                                }
                                else {
                                    pointB = new PointCartesien(longueur - listeSortedX[i+1], -dims[4] + longueurGauche, listeSortedY[j]);
                                    pointC = new PointCartesien(longueur - listeSortedX[i+1], -dims[4] + longueurGauche, listeSortedY[j+1]);
                                }

                                pointE = new PointCartesien(longueur - listeSortedX[i], 0.0f + longueurGauche, listeSortedY[j+1]);
                                pointF = new PointCartesien(longueur - listeSortedX[i], 0.0f + longueurGauche, listeSortedY[j]);
                                pointG = new PointCartesien(longueur - listeSortedX[i+1], 0.0f + longueurGauche, listeSortedY[j]);
                                pointH = new PointCartesien(longueur - listeSortedX[i+1], 0.0f + longueurGauche, listeSortedY[j+1]);
                            }break;
                            
                            case DROITE:
                            {
                                float longueurFacade = this.chalet.reqMurFacade().reqDimensionsMur()[0];
                                
                                if (i == 0) {
                                    pointA = new PointCartesien(-dims[4] + longueurFacade, debutMurInterieur, listeSortedY[j]);
                                    pointD = new PointCartesien(-dims[4] + longueurFacade, debutMurInterieur, listeSortedY[j+1]);
                                }
                                else {
                                    pointA = new PointCartesien(-dims[4] + longueurFacade, listeSortedX[i], listeSortedY[j]);
                                    pointD = new PointCartesien(-dims[4] + longueurFacade, listeSortedX[i], listeSortedY[j+1]);
                                }

                                if (i == listeX.size()-2) {
                                    pointB = new PointCartesien(-dims[4] + longueurFacade, finMurInterieur, listeSortedY[j]);
                                    pointC = new PointCartesien(-dims[4] + longueurFacade, finMurInterieur, listeSortedY[j+1]);
                                }
                                else {
                                    pointB = new PointCartesien(-dims[4] + longueurFacade, listeSortedX[i+1], listeSortedY[j]);
                                    pointC = new PointCartesien(-dims[4] + longueurFacade, listeSortedX[i+1], listeSortedY[j+1]);
                                }

                                pointE = new PointCartesien(0.0f + longueurFacade, listeSortedX[i], listeSortedY[j+1]);
                                pointF = new PointCartesien(0.0f + longueurFacade, listeSortedX[i], listeSortedY[j]);
                                pointG = new PointCartesien(0.0f + longueurFacade, listeSortedX[i+1], listeSortedY[j]);
                                pointH = new PointCartesien(0.0f + longueurFacade, listeSortedX[i+1], listeSortedY[j+1]);
                            }break;
                        }
                    
                        trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
                        trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                    }
                }
            }
            
        }
        else 
        {
            switch(murVersExporter.reqOrientation())
            {
                case FACADE:
                {
                    pointA = new PointCartesien(debutMurInterieur, dims[4], 0.0f);
                    pointB = new PointCartesien(finMurInterieur, dims[4], 0.0f);
                    pointC = new PointCartesien(finMurInterieur, dims[4], dims[1]);
                    pointD = new PointCartesien(debutMurInterieur, dims[4], dims[1]);
                    pointE = new PointCartesien(debutMurExterieur, 0.0f, dims[1]);
                    pointF = new PointCartesien(debutMurExterieur, 0.0f, 0.0f);
                    pointG = new PointCartesien(finMurExterieur, 0.0f, 0.0f);
                    pointH = new PointCartesien(finMurExterieur, 0.0f, dims[1]);
                }break;
                
                case GAUCHE:
                {
                    pointA = new PointCartesien(dims[4], longueur - debutMurInterieur, 0.0f);
                    pointB = new PointCartesien(dims[4], longueur - finMurInterieur, 0.0f);
                    pointC = new PointCartesien(dims[4], longueur - finMurInterieur, dims[1]);
                    pointD = new PointCartesien(dims[4], longueur - debutMurInterieur, dims[1]);
                    pointE = new PointCartesien(0.0f, longueur - debutMurExterieur, dims[1]);
                    pointF = new PointCartesien(0.0f, longueur - debutMurExterieur, 0.0f);
                    pointG = new PointCartesien(0.0f, longueur - finMurExterieur, 0.0f);
                    pointH = new PointCartesien(0.0f, longueur - finMurExterieur, dims[1]);
                }break;
                
                case ARRIERE:
                {
                    float longueurGauche = this.chalet.reqMurGauche().reqDimensionsMur()[0];
                    
                    pointA = new PointCartesien(longueur - debutMurInterieur, -dims[4] + longueurGauche, 0.0f);
                    pointB = new PointCartesien(longueur - finMurInterieur, -dims[4] + longueurGauche, 0.0f);
                    pointC = new PointCartesien(longueur - finMurInterieur, -dims[4] + longueurGauche, dims[1]);
                    pointD = new PointCartesien(longueur - debutMurInterieur, -dims[4] + longueurGauche, dims[1]);
                    pointE = new PointCartesien(longueur - debutMurExterieur, 0.0f + longueurGauche, dims[1]);
                    pointF = new PointCartesien(longueur - debutMurExterieur, 0.0f + longueurGauche, 0.0f);
                    pointG = new PointCartesien(longueur - finMurExterieur, 0.0f + longueurGauche, 0.0f);
                    pointH = new PointCartesien(longueur - finMurExterieur, 0.0f + longueurGauche, dims[1]);
                }break;
                
                case DROITE:
                {
                    float longueurFacade = this.chalet.reqMurFacade().reqDimensionsMur()[0];
                    
                    pointA = new PointCartesien(-dims[4] + longueurFacade, debutMurInterieur, 0.0f);
                    pointB = new PointCartesien(-dims[4] + longueurFacade, finMurInterieur, 0.0f);
                    pointC = new PointCartesien(-dims[4] + longueurFacade, finMurInterieur, dims[1]);
                    pointD = new PointCartesien(-dims[4] + longueurFacade, debutMurInterieur, dims[1]);
                    pointE = new PointCartesien(0.0f + longueurFacade, debutMurExterieur, dims[1]);
                    pointF = new PointCartesien(0.0f + longueurFacade, debutMurExterieur, 0.0f);
                    pointG = new PointCartesien(0.0f + longueurFacade, finMurExterieur, 0.0f);
                    pointH = new PointCartesien(0.0f + longueurFacade, finMurExterieur, dims[1]);
                }break;
            }
            normal = "-1 0 0";
            trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
            trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));

            normal = "1 0 0";
            trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
            trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
        }
        
        creerFichierSTL(nomDuFichier, trianglesDuMur);
    }
    
    private void formatageMurBrut(String nomDuFichier, Mur murVersExporter)
    {
        String normal = "0 0 0";
        ArrayList<Triangle> trianglesDuMur = new ArrayList<Triangle>();
        float [] dims = murVersExporter.reqDimensionsMur();
        PointCartesien pointA = new PointCartesien(0.0f, 0.0f, 0.0f);
        PointCartesien pointB = new PointCartesien(dims[0], 0.0f, 0.0f);
        PointCartesien pointC = new PointCartesien(dims[0], 0.0f, dims[1]);
        PointCartesien pointD = new PointCartesien(0.0f, 0.0f, dims[1]);
        PointCartesien pointE = new PointCartesien(0.0f, -dims[4], dims[1]);
        PointCartesien pointF = new PointCartesien(0.0f, -dims[4], 0.0f);
        PointCartesien pointG = new PointCartesien(dims[0], -dims[4], 0.0f);
        PointCartesien pointH = new PointCartesien(dims[0], -dims[4], dims[1]);
        
        normal = "-1 0 0";
        trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
        trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));
        
        normal = "1 0 0";
        trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));
        trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
        
        normal = "0 -1 0";
        trianglesDuMur.add(new Triangle(pointF, pointA, pointD, normal));
        trianglesDuMur.add(new Triangle(pointF, pointE, pointD, normal));
        
        normal = "0 1 0";
        trianglesDuMur.add(new Triangle(pointG, pointB, pointC, normal));
        trianglesDuMur.add(new Triangle(pointG, pointH, pointC, normal));
        
        normal = "0 0 1";
        trianglesDuMur.add(new Triangle(pointE, pointD, pointC, normal));
        trianglesDuMur.add(new Triangle(pointE, pointH, pointC, normal));
        
        normal = "0 0 -1";
        trianglesDuMur.add(new Triangle(pointF, pointG, pointB, normal));
        trianglesDuMur.add(new Triangle(pointF, pointA, pointB, normal));
        
        creerFichierSTL(nomDuFichier, trianglesDuMur);
    }
    
    private void formatageRetraits(String nomDuFichier, Mur murVersExporter) 
    {
        String normal = "0 0 0";
        ArrayList<Triangle> trianglesDuMur = new ArrayList<Triangle>();
        float [] dims = murVersExporter.reqDimensionsMur();

        float longueur = dims[0];
        float epaisseur = dims[4];
        float hauteur = dims[1];
        float rainureSup = dims[3]; 
        
        int i = 1;
        
        switch(murVersExporter.reqOrientation())
        {
            case FACADE:
            {
                trianglesDuMur.clear();
                
                PointCartesien origineBot = new PointCartesien(0.0f,0.0f,0.0f);
                PointCartesien origineTop = new PointCartesien(0.0f,0.0f,hauteur);
                
                PointCartesien pB = new PointCartesien(0, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pD = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,0, 0.0f);
                PointCartesien pJ = new PointCartesien(0, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pK = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pL = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,0, hauteur);
                
                PointCartesien pE = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f, 0.0f);
                PointCartesien pF = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pG = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pM = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f, hauteur);
                PointCartesien pN = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pO = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                // 1er prisme
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(origineBot, pD, pB, normal));
                trianglesDuMur.add(new Triangle(pD, pB, pC, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(origineTop, pL, pJ, normal));
                trianglesDuMur.add(new Triangle(pL, pJ, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(origineTop, pJ, origineBot, normal));
                trianglesDuMur.add(new Triangle(pB, origineBot, pJ, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(origineTop, pL, pD, normal));
                trianglesDuMur.add(new Triangle(origineTop, pD, origineBot, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pK, pL, pD, normal));
                trianglesDuMur.add(new Triangle(pK, pC, pD, normal));
                
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pJ, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pJ, pK, pC, normal));
                
                String newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
                // 2e prisme
                trianglesDuMur.clear();
                PointCartesien origineBotDroit = new PointCartesien(longueur, 0.0f, 0.0f);
                PointCartesien origineTopDroit = new PointCartesien(longueur, 0.0f, hauteur);
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pE, origineBotDroit, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(origineTopDroit, pM, pO, normal));
                trianglesDuMur.add(new Triangle(pM, pO, pN, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pM, pN, pF, normal));
                trianglesDuMur.add(new Triangle(pF, pE, pM, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(origineBotDroit, pE, origineTopDroit, normal));
                trianglesDuMur.add(new Triangle(origineTopDroit, pM, pE, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pG, origineBotDroit, origineTopDroit, normal));
                trianglesDuMur.add(new Triangle(origineTopDroit, pO, pG, normal));
                
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pF, pG, pO, normal));
                trianglesDuMur.add(new Triangle(pO, pN, pF, normal));
                
                newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
            }break;
            
            case GAUCHE:
            {
                trianglesDuMur.clear();
                
                PointCartesien pA = new PointCartesien(0.0f, 0.0f, 0.0f);
                PointCartesien pB = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, 0.0f, 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, 0.0f, hauteur);
                PointCartesien pD = new PointCartesien(0.0f, 0.0f, hauteur);

                PointCartesien pE = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pF = new PointCartesien(epaisseur + rainureSup, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pG = new PointCartesien(epaisseur + rainureSup, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pH = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);

                PointCartesien pI = new PointCartesien(0.0f, epaisseur, 0.0f);
                PointCartesien pJ = new PointCartesien(epaisseur + rainureSup, epaisseur, 0.0f);
                PointCartesien pK = new PointCartesien(epaisseur + rainureSup, epaisseur, hauteur);
                PointCartesien pL = new PointCartesien(0.0f, epaisseur, hauteur);
                
                //Pour le dessus et le dessous.
                PointCartesien pM = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pN = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                // 1er retrait
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pA, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pC, normal));
                
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pH, pG, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(pI, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pI, pL, pK, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pB, pE, pH, normal));
                trianglesDuMur.add(new Triangle(pB, pC, pH, normal));
                
                trianglesDuMur.add(new Triangle(pF, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pF, pG, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pA, pI, pL, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pL, normal));
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pA, pB, pE, normal));
                trianglesDuMur.add(new Triangle(pA, pM, pE, normal));
                
                trianglesDuMur.add(new Triangle(pM, pF, pJ, normal));
                trianglesDuMur.add(new Triangle(pM, pI, pJ, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(pD, pC, pH, normal));
                trianglesDuMur.add(new Triangle(pD, pN, pH, normal));
                
                trianglesDuMur.add(new Triangle(pN, pG, pK, normal));
                trianglesDuMur.add(new Triangle(pN, pL, pK, normal));
                
                String newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
                // 2e retrait
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pA, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pC, normal));
                
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pH, pG, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(pI, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pI, pL, pK, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pB, pE, pH, normal));
                trianglesDuMur.add(new Triangle(pB, pC, pH, normal));
                
                trianglesDuMur.add(new Triangle(pF, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pF, pG, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pA, pI, pL, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pL, normal));
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pA, pB, pE, normal));
                trianglesDuMur.add(new Triangle(pA, pM, pE, normal));
                
                trianglesDuMur.add(new Triangle(pM, pF, pJ, normal));
                trianglesDuMur.add(new Triangle(pM, pI, pJ, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(pD, pC, pH, normal));
                trianglesDuMur.add(new Triangle(pD, pN, pH, normal));
                
                trianglesDuMur.add(new Triangle(pN, pG, pK, normal));
                trianglesDuMur.add(new Triangle(pN, pL, pK, normal));
                
                newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
                
                
            }break;
            
            case ARRIERE:
            {
                trianglesDuMur.clear();
                
                PointCartesien origineBot = new PointCartesien(0.0f,0.0f,0.0f);
                PointCartesien origineTop = new PointCartesien(0.0f,0.0f,hauteur);
                
                PointCartesien pB = new PointCartesien(0, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pD = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,0, 0.0f);
                PointCartesien pJ = new PointCartesien(0, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pK = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pL = new PointCartesien(epaisseur/2.0f+rainureSup/2.0f,0, hauteur);
                
                PointCartesien pE = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f, 0.0f);
                PointCartesien pF = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pG = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pM = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, 0.0f, hauteur);
                PointCartesien pN = new PointCartesien(longueur - epaisseur/2.0f - rainureSup/2.0f, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pO = new PointCartesien(longueur, -epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                // 1er prisme
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(origineBot, pD, pB, normal));
                trianglesDuMur.add(new Triangle(pD, pB, pC, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(origineTop, pL, pJ, normal));
                trianglesDuMur.add(new Triangle(pL, pJ, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(origineTop, pJ, origineBot, normal));
                trianglesDuMur.add(new Triangle(pB, origineBot, pJ, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(origineTop, pL, pD, normal));
                trianglesDuMur.add(new Triangle(origineTop, pD, origineBot, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pK, pL, pD, normal));
                trianglesDuMur.add(new Triangle(pK, pC, pD, normal));
                
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pJ, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pJ, pK, pC, normal));
                
                String newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
                // 2e prisme
                trianglesDuMur.clear();
                PointCartesien origineBotDroit = new PointCartesien(longueur, 0.0f, 0.0f);
                PointCartesien origineTopDroit = new PointCartesien(longueur, 0.0f, hauteur);
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pE, origineBotDroit, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(origineTopDroit, pM, pO, normal));
                trianglesDuMur.add(new Triangle(pM, pO, pN, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pM, pN, pF, normal));
                trianglesDuMur.add(new Triangle(pF, pE, pM, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(origineBotDroit, pE, origineTopDroit, normal));
                trianglesDuMur.add(new Triangle(origineTopDroit, pM, pE, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pG, origineBotDroit, origineTopDroit, normal));
                trianglesDuMur.add(new Triangle(origineTopDroit, pO, pG, normal));
                
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pF, pG, pO, normal));
                trianglesDuMur.add(new Triangle(pO, pN, pF, normal));
                
                newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
            }break;
            
            case DROITE:
            {
                trianglesDuMur.clear();
                
                PointCartesien pA = new PointCartesien(0.0f, 0.0f, 0.0f);
                PointCartesien pB = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, 0.0f, 0.0f);
                PointCartesien pC = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, 0.0f, hauteur);
                PointCartesien pD = new PointCartesien(0.0f, 0.0f, hauteur);

                PointCartesien pE = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pF = new PointCartesien(epaisseur + rainureSup, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pG = new PointCartesien(epaisseur + rainureSup, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                PointCartesien pH = new PointCartesien(epaisseur/2.0f + rainureSup/2.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);

                PointCartesien pI = new PointCartesien(0.0f, epaisseur, 0.0f);
                PointCartesien pJ = new PointCartesien(epaisseur + rainureSup, epaisseur, 0.0f);
                PointCartesien pK = new PointCartesien(epaisseur + rainureSup, epaisseur, hauteur);
                PointCartesien pL = new PointCartesien(0.0f, epaisseur, hauteur);
                
                //Pour le dessus et le dessous.
                PointCartesien pM = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, 0.0f);
                PointCartesien pN = new PointCartesien(0.0f, epaisseur/2.0f - rainureSup/2.0f, hauteur);
                
                // 1er retrait
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pA, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pC, normal));
                
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pH, pG, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(pI, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pI, pL, pK, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pB, pE, pH, normal));
                trianglesDuMur.add(new Triangle(pB, pC, pH, normal));
                
                trianglesDuMur.add(new Triangle(pF, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pF, pG, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pA, pI, pL, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pL, normal));
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pA, pB, pE, normal));
                trianglesDuMur.add(new Triangle(pA, pM, pE, normal));
                
                trianglesDuMur.add(new Triangle(pM, pF, pJ, normal));
                trianglesDuMur.add(new Triangle(pM, pI, pJ, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(pD, pC, pH, normal));
                trianglesDuMur.add(new Triangle(pD, pN, pH, normal));
                
                trianglesDuMur.add(new Triangle(pN, pG, pK, normal));
                trianglesDuMur.add(new Triangle(pN, pL, pK, normal));
                
                String newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
                
                // 2e retrait
                normal = "0 -1 0";
                trianglesDuMur.add(new Triangle(pA, pB, pC, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pC, normal));
                
                trianglesDuMur.add(new Triangle(pE, pF, pG, normal));
                trianglesDuMur.add(new Triangle(pE, pH, pG, normal));
                
                normal = "0 1 0";
                trianglesDuMur.add(new Triangle(pI, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pI, pL, pK, normal));
                
                normal = "1 0 0";
                trianglesDuMur.add(new Triangle(pB, pE, pH, normal));
                trianglesDuMur.add(new Triangle(pB, pC, pH, normal));
                
                trianglesDuMur.add(new Triangle(pF, pJ, pK, normal));
                trianglesDuMur.add(new Triangle(pF, pG, pK, normal));
                
                normal = "-1 0 0";
                trianglesDuMur.add(new Triangle(pA, pI, pL, normal));
                trianglesDuMur.add(new Triangle(pA, pD, pL, normal));
                
                normal = "0 0 -1";
                trianglesDuMur.add(new Triangle(pA, pB, pE, normal));
                trianglesDuMur.add(new Triangle(pA, pM, pE, normal));
                
                trianglesDuMur.add(new Triangle(pM, pF, pJ, normal));
                trianglesDuMur.add(new Triangle(pM, pI, pJ, normal));
                
                normal = "0 0 1";
                trianglesDuMur.add(new Triangle(pD, pC, pH, normal));
                trianglesDuMur.add(new Triangle(pD, pN, pH, normal));
                
                trianglesDuMur.add(new Triangle(pN, pG, pK, normal));
                trianglesDuMur.add(new Triangle(pN, pL, pK, normal));
                
                newFileName = nomDuFichier + (i++) + ".stl";
                creerFichierSTL(newFileName, trianglesDuMur);
            }break;
            
            default:{};
        }
        
        if (murVersExporter.reqAccPresentFlag()) 
        {
            ArrayList<Accessoire> listeAccessoires = murVersExporter.reqAccessoiresPresents();
            
            for (Accessoire acc : listeAccessoires)
            {
                if (acc.reqAccValide())
                {
                   trianglesDuMur.clear();
                   float accGauche = 0.0f;
                   float accDroit = acc.reqLongueur();
                   float accHaut = acc.reqHauteur();
                   float accBas = 0.0f;
                   float accEpaisseur = dims[4];

                   PointCartesien pointA = new PointCartesien(accGauche, 0.0f , accBas);
                   PointCartesien pointB = new PointCartesien(accDroit, 0.0f, accBas);
                   PointCartesien pointC = new PointCartesien(accDroit, 0.0f, accHaut);
                   PointCartesien pointD = new PointCartesien(accGauche, 0.0f, accHaut);

                   PointCartesien pointE = new PointCartesien(accGauche, -accEpaisseur, accHaut);
                   PointCartesien pointF = new PointCartesien(accGauche,  -accEpaisseur, accBas);
                   PointCartesien pointG = new PointCartesien(accDroit,  -accEpaisseur, accBas);
                   PointCartesien pointH = new PointCartesien(accDroit,  -accEpaisseur, accHaut);

                   //Devant de l'accessoire
                   normal = "0 -1 0";
                   trianglesDuMur.add(new Triangle(pointA, pointB, pointC, normal));
                   trianglesDuMur.add(new Triangle(pointA, pointD, pointC, normal));

                   //Arrière de l'accessoire
                   normal = "0 1 0";
                   trianglesDuMur.add(new Triangle(pointE, pointF, pointG, normal));
                   trianglesDuMur.add(new Triangle(pointE, pointH, pointG, normal));

                   // Sol de l'accessoire
                   normal = "0 0 -1";
                   trianglesDuMur.add(new Triangle(pointA, pointB, pointG, normal));
                   trianglesDuMur.add(new Triangle(pointA, pointF, pointG, normal));

                   // Interieur gauche de l'accessoire
                   normal = "-1 0 0";
                   trianglesDuMur.add(new Triangle(pointA, pointF, pointE, normal));
                   trianglesDuMur.add(new Triangle(pointA, pointD, pointE, normal));

                   // Plafond de l'accessoire
                   normal = "0 0 1";
                   trianglesDuMur.add(new Triangle(pointD, pointE, pointH, normal));
                   trianglesDuMur.add(new Triangle(pointD, pointC, pointH, normal));

                   // Interieur droit de l'accessoire
                   normal = "1 0 0";
                   trianglesDuMur.add(new Triangle(pointC, pointH, pointG, normal));
                   trianglesDuMur.add(new Triangle(pointC, pointB, pointG, normal));

                   String nouveauNomFichier = nomDuFichier + i + ".stl";
                   i++;

                   creerFichierSTL(nouveauNomFichier, trianglesDuMur);   
                }
            }
        }
    }
}
