package ca.ulaval.glo2004.domaine;

import ca.ulaval.glo2004.domaine.afficheur.Afficheur;
import ca.ulaval.glo2004.domaine.ChaletDTO;
import java.util.ArrayList;
import java.util.UUID;

public class Chalet 
{
    private Mur murFacade = null;
    private Mur murArriere = null;
    private Mur murGauche = null;
    private Mur murDroit = null;
    private static Mur murActif = null;
    private UUID uuid;
    private float rainureSupp = 0.0f;
    private Toit toit = null;

    public Chalet() 
    {
        this.uuid = UUID.randomUUID();
        this.murFacade = new Mur(120.0f, 96.0f, 3.0f, 6.0f, 3, Mur.OrientationMur.FACADE, this.rainureSupp);
        this.murDroit = new Mur(120.0f, 96.0f, 3.0f, 6.0f, 3, Mur.OrientationMur.DROITE, this.rainureSupp);
        this.murGauche = new Mur(120.0f, 96.0f, 3.0f, 6.0f, 3, Mur.OrientationMur.GAUCHE, this.rainureSupp);
        this.murArriere = new Mur(120.0f, 96.0f, 3.0f, 6.0f, 3, Mur.OrientationMur.ARRIERE, this.rainureSupp);
        this.toit = new Toit(120.0f, 120.0f, 6.0f);
    }
    
    public float reqRainureSupp(){
        return this.rainureSupp;
    }
    public void ChaletSetToDTO(ChaletDTO c)
    {
        this.uuid = c.Uuid;
        this.murFacade = new Mur (c.MurFacade);
        this.murArriere = new Mur(c.MurArriere);
        this.murDroit = new Mur(c.MurDroit);
        this.murGauche = new Mur(c.MurGauche);
        murActif = c.MurActif;
        
    }

    
    
    
    
    //////////////////////////     ASG  et REQ   //////////////
    
    
    
    
    public void asgRainureSup(float nouvVal)
    {
        this.murFacade.asgRainureSupplementaire(nouvVal);
        this.murDroit.asgRainureSupplementaire(nouvVal);
        this.murGauche.asgRainureSupplementaire(nouvVal);
        this.murArriere.asgRainureSupplementaire(nouvVal);
    }

    public UUID reqUUID() 
    {
        return uuid;
    }

    public void setMurCourant(Mur murAfficheEcran) 
    {
        murActif = murAfficheEcran;
    }

    public static Mur reqMurCourant() 
    {
        return murActif;
    }

    public Mur reqMurFacade() 
    {
        return murFacade;
    }

    public Mur reqMurArriere() 
    {
        return murArriere;
    }

    public Mur reqMurGauche() 
    {
        return murGauche;
    }

    public Mur reqMurDroit() 
    {
        return murDroit;
    }
    
    
    public Toit reqToit()
    {
    return this.toit;
    }
    
    public void asgOrientationToit(int nouvOrientation)
    {
        this.toit.asgOrientation(nouvOrientation);
        float murPerp = 0;
        float murParr = 0;
        float epp = murDroit.reqDimensionsMur()[4];
        if (nouvOrientation == 0 || nouvOrientation == 2)
        {
        murPerp = murFacade.reqDimensionsMur()[0];
        murParr = murDroit.reqDimensionsMur()[0];
        }
        else
        {
         murParr = murFacade.reqDimensionsMur()[0];
         murPerp = murDroit.reqDimensionsMur()[0];
        }
     toit.majDimsRallonge(murParr, murPerp);
     toit.majDimsPignon(murParr, murPerp, epp);
    }
    
    
    public void asgAngle (float angle)
    {
     toit.asgAngle(angle);
     float murPerp = 0.0f;
     float murParr = 0.0f;
     if (this.toit.reqOrientation() == 0 || this.toit.reqOrientation() == 2)
        {
         murPerp = murFacade.reqDimensionsMur()[0];
         murParr = murDroit.reqDimensionsMur()[0];
        }
        else
        {
         murParr = murFacade.reqDimensionsMur()[0];
         murPerp = murDroit.reqDimensionsMur()[0];
        }
     toit.majDimsRallonge(murParr, murPerp);
     
    }
    
    
    
    
    ////////////////////////    DIMENSIONS   ////////////////////////
    
    public void modifierDimensionAvantArriere(float longueur){
        float current_longeur = murFacade.reqDimensionsMur()[0];
        float epaiseur = murFacade.reqDimensionsMur()[4];
        float marge_securite = murFacade.reqDimensionsMur()[2];
        murFacade.asgLongueur(longueur);
        murArriere.asgLongueur(longueur);
    
        
        ArrayList<Accessoire> accArr = murArriere.reqAccessoiresPresents();
        ArrayList<Accessoire> accAv = murFacade.reqAccessoiresPresents();
        
        ArrayList<Accessoire> accArrCopy = new ArrayList<>();
        ArrayList<Accessoire> accAvCopy = new ArrayList<>();
        
        for (Accessoire A : accArr)
        {
            accArrCopy.add(A.makeCopy(A));
        }
        for (Accessoire A : accAv)
        {
            accAvCopy.add(A.makeCopy(A));
        }
        
        
        boolean succes = true;
        for (Accessoire A : accArrCopy)
        {
            if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE+ A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))- A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            else
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE - A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))+ A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            
        }
        for (Accessoire A : accAvCopy)
        {
            if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE+ A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))- A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            else
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE - A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))+ A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
        }
        if (succes)
        {
            for (Accessoire A : accArr)
            {   
                if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE+A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))- A.reqLongueur()/2.0f);
                }
                else
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE-A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))+ A.reqLongueur()/2.0f);
                }
                
            }
            for (Accessoire A : accAv)
            {
                if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE+A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))- A.reqLongueur()/2.0f);
                }
                else
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE-A.reqLongueur()/2.0f)*(longueur-2*(epaiseur+marge_securite))/(current_longeur-2*(epaiseur+marge_securite)))+ A.reqLongueur()/2.0f);
                }
            }
        }
        else
        {
         murFacade.asgLongueur(current_longeur);
         murArriere.asgLongueur(current_longeur);
        }
        float murPerp = 0.0f;
        float murParr = 0.0f;
        float epp = 0.0f;
        if (this.toit.reqOrientation() == 0 || this.toit.reqOrientation() == 2)
        {
         murPerp = murFacade.reqDimensionsMur()[0];
         murParr = murDroit.reqDimensionsMur()[0];
         epp = murDroit.reqDimensionsMur()[4];
        }
        else
        {
         murParr = murFacade.reqDimensionsMur()[0];
         murPerp = murDroit.reqDimensionsMur()[0];
         epp = murDroit.reqDimensionsMur()[4];
        }
        toit.majDimsPignon(murParr, murPerp, epp);
        toit.majDimsRallonge(murParr, murPerp);
    }
    public void modifierDimensionGaucheDroite(float longueur){
        float current_longeur = murGauche.reqDimensionsMur()[0];
        float marge_securite = murGauche.reqDimensionsMur()[2];
        float epaiseur = murFacade.reqDimensionsMur()[4];
        murGauche.asgLongueur(longueur);
        murDroit.asgLongueur(longueur);
    
        
        ArrayList<Accessoire> accG = murGauche.reqAccessoiresPresents();
        ArrayList<Accessoire> accD = murDroit.reqAccessoiresPresents();
        
        ArrayList<Accessoire> accGCopy = new ArrayList<>();
        ArrayList<Accessoire> accDCopy = new ArrayList<>();
        
        for (Accessoire A : accG)
        {
            accGCopy.add(A.makeCopy(A));
        }
        for (Accessoire A : accD)
        {
            accDCopy.add(A.makeCopy(A));
        }
        
        
        boolean succes = true;
        for (Accessoire A : accGCopy)
        {
            if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE+ A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))- A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            else
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE - A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))+ A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            
        }
        for (Accessoire A : accDCopy)
        {
            if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE+ A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))- A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
            else
            {
                succes &= A.redimensionnerAccessoire(Mur.POSITION_X_CENTRE + ((A.reqPositionX()-Mur.POSITION_X_CENTRE - A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))+ A.reqLongueur()/2.0f,
                        A.reqPositionY(), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
            }
        }
        if (succes)
        {
            for (Accessoire A : accG)
            {   
                if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE+A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))- A.reqLongueur()/2.0f);
                }
                else
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE-A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))+ A.reqLongueur()/2.0f);
                }
            }
            for (Accessoire A : accD)
            {
                if (A.reqPositionX() > Mur.POSITION_X_CENTRE)
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE+A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))- A.reqLongueur()/2.0f);
                }
                else
                {
                    A.setPositionX(Mur.POSITION_X_CENTRE+((A.reqPositionX()-Mur.POSITION_X_CENTRE-A.reqLongueur()/2.0f)*(longueur-2*(epaiseur/2.0f+marge_securite))/(current_longeur-2*(epaiseur/2.0f+marge_securite)))+ A.reqLongueur()/2.0f);
                }
            }
      
        }
        else
        {
         murGauche.asgLongueur(current_longeur);
         murDroit.asgLongueur(current_longeur);
        }
        float murPerp = 0.0f;
        float murParr = 0.0f;
        float epp = murDroit.reqDimensionsMur()[4];
        if (this.toit.reqOrientation() == 0 || this.toit.reqOrientation() == 2)
        {
         murPerp = murFacade.reqDimensionsMur()[0];
         murParr = murDroit.reqDimensionsMur()[0];
        }
        else
        {
         murParr = murFacade.reqDimensionsMur()[0];
         murPerp = murDroit.reqDimensionsMur()[0];
        }
        toit.majDimsPignon(murParr, murPerp, epp);
        toit.majDimsRallonge(murParr, murPerp);
    } 
    public void modifierHauteur(float hauteur){
        float current_hauteur = murFacade.reqDimensionsMur()[1];
        ArrayList<Accessoire> accArr = murArriere.reqAccessoiresPresents();
        ArrayList<Accessoire> accAv = murFacade.reqAccessoiresPresents();
        ArrayList<Accessoire> accG = murGauche.reqAccessoiresPresents();
        ArrayList<Accessoire> accD = murDroit.reqAccessoiresPresents();
        float margeSecurite = murFacade.reqDimensionsMur()[2];
        
        murFacade.asgHauteur(hauteur);
        murArriere.asgHauteur(hauteur);
        murGauche.asgHauteur(hauteur);
        murDroit.asgHauteur(hauteur);
        
        
        ArrayList<Accessoire> accArrCopy = new ArrayList<>();
        ArrayList<Accessoire> accAvCopy = new ArrayList<>();
        ArrayList<Accessoire> accGCopy = new ArrayList<>();
        ArrayList<Accessoire> accDCopy = new ArrayList<>();
        
        boolean succes = true;
        
        for (Accessoire A : accArr)
        {
            accArrCopy.add(A.makeCopy(A));
        }
        for (Accessoire A : accAv)
        {
            accAvCopy.add(A.makeCopy(A));
        }
        
        for (Accessoire A : accG)
        {
            accGCopy.add(A.makeCopy(A));
        }
        for (Accessoire A : accD)
        {
            accDCopy.add(A.makeCopy(A));
        }
        
        for (Accessoire A : accArrCopy)
        {
           succes &= A.redimensionnerAccessoire(A.reqPositionX(), Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
        }
        for (Accessoire A : accAvCopy)
        {
           succes &= A.redimensionnerAccessoire(A.reqPositionX(), Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
        }
        for (Accessoire A : accGCopy)
        {
           succes &= A.redimensionnerAccessoire(A.reqPositionX(), Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
        }
        for (Accessoire A : accDCopy)
        {
           succes &= A.redimensionnerAccessoire(A.reqPositionX(), Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite), A.reqLongueur(), A.reqHauteur(), A.reqId(), false);
        }
        if (succes){
        
            for (Accessoire A : accArr)
            {   
                A.setPositionY(Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite));
            }
            for (Accessoire A : accAv)
            {
                 A.setPositionY(Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite));
            }
            for (Accessoire A : accG)
            {
                 A.setPositionY(Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite));
            }
            for (Accessoire A : accD)
            {
                 A.setPositionY(Mur.POSITION_Y_CENTRE+(A.reqPositionY()-Mur.POSITION_Y_CENTRE)*(hauteur - margeSecurite)/(current_hauteur-margeSecurite));      
            }
        }
        else
        {
        murFacade.asgHauteur(current_hauteur);
        murArriere.asgHauteur(current_hauteur);
        murGauche.asgHauteur(current_hauteur);
        murDroit.asgHauteur(current_hauteur);
        }
            
    }
    public void modifierEpaisseur(float epais){
        murFacade.asgEpaisseur(epais);
        murArriere.asgEpaisseur(epais);
        murGauche.asgEpaisseur(epais);
        murDroit.asgEpaisseur(epais);
        float[] dimsDroit = murDroit.reqDimensionsMur();
        float[] dimsFace = murFacade.reqDimensionsMur();
        float[] dimsParr = null;
        float[] dimsPerp = null;
        if (toit.reqOrientation()==0 || toit.reqOrientation() == 2)
        {
            dimsParr = dimsDroit;
            dimsPerp = dimsFace;
        }
        else
        {
            dimsParr = dimsFace;
            dimsPerp = dimsDroit;
        }
        toit.majDimsPignon(dimsParr[0],dimsPerp[0], epais);
    }
    
    public boolean curseurDansIntervalle(float refX, float refY, float posX, float posY, float longueur, float hauteur) 
    {
        boolean succes = false;
        boolean curseurDansX = (((refX - (longueur / 2)) <= posX) && (posX <= (refX + (longueur / 2))));
        boolean curseurDansY = (((refY - (hauteur / 2)) <= posY) && (posY <= (refY + (hauteur / 2))));

        if (curseurDansX && curseurDansY) 
        {
            succes = true;
        }

        return succes;
    }
    public int IdAccXY(float posX,float posY)
    {
       ArrayList<Accessoire> acc = Chalet.reqMurCourant().reqAccessoiresPresents();
        boolean succes = false;
        for (Accessoire S : acc)
        {
            if (this.curseurDansIntervalle(S.reqPositionX()*Afficheur.PIXELS_PAR_POUCES,S.reqPositionY()*Afficheur.PIXELS_PAR_POUCES,posX,posY,S.reqLongueur()*Afficheur.PIXELS_PAR_POUCES,S.reqHauteur()*Afficheur.PIXELS_PAR_POUCES))
            {
                return S.reqId();
            }
        } 
        return -1;
    }
    public boolean mouseOnAcc(float posX,float posY)
    {
        ArrayList<Accessoire> acc = Chalet.reqMurCourant().reqAccessoiresPresents();
        boolean succes = false;
        for (Accessoire S : acc)
        {
            succes ^= this.curseurDansIntervalle(S.reqPositionX()*Afficheur.PIXELS_PAR_POUCES,S.reqPositionY()*Afficheur.PIXELS_PAR_POUCES,posX,posY,S.reqLongueur()*Afficheur.PIXELS_PAR_POUCES,S.reqHauteur()*Afficheur.PIXELS_PAR_POUCES);
        }
        return succes;
    }

    public void convertirPoint(float coordX, float coordY) 
    {
        float[] dimensionsMurCourant = murActif.reqDimensionsMur();
        float longueurMur = dimensionsMurCourant[0];
        float hauteurMur = dimensionsMurCourant[1];

        if (!murActif.reqAccPresentFlag()) 
        {
            if (curseurDansIntervalle(Mur.POSITION_X_CENTRE, Mur.POSITION_Y_CENTRE,
                    coordX, coordY,
                    longueurMur, hauteurMur)) 
            {
                //controleur.afficheMenu(murActif)
            }
        } 
        else 
        {
            ArrayList<Accessoire> accessoiresDansMur = murActif.reqAccessoiresPresents();
            boolean objetTrouve = false;
            for (Accessoire i : accessoiresDansMur) 
            {
                if (curseurDansIntervalle(i.reqPositionX(), i.reqPositionY(),
                                            coordX, coordY,
                                            i.reqLongueur(), i.reqHauteur())) 
                {
                    //controleur.afficheMenu(i);
                    objetTrouve = true;
                }
            }

            if ((!objetTrouve) && (curseurDansIntervalle(Mur.POSITION_X_CENTRE, Mur.POSITION_Y_CENTRE,
                                    coordX, coordY,
                                    longueurMur, hauteurMur))) 
            {
                //controleur.afficheMenu(murActif)
            }

        }
    }
}
