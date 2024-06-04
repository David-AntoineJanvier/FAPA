package ca.ulaval.glo2004.domaine;

public class Porte extends Accessoire
{
    
    public Porte(float positionX, float positionY, float hauteur, float longueur, int id)
    {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.hauteur   = hauteur;
        this.longueur  = longueur;
        this.estValide = false;
    }
    @Override
    public Accessoire makeCopy(Accessoire A)
    {
       Porte obj = new Porte(A.reqPositionX(),A.reqPositionY(),A.reqHauteur(),A.reqLongueur(),A.reqId());
        obj.estValide = A.estValide;
        obj.murActif = A.murActif;
        return obj;
    }
    @Override
    public void setPositionY(float nvllPositionY)
    {
        float[] dimensionsMurCourant = Chalet.reqMurCourant().reqDimensionsMur();
        this.positionY = Mur.POSITION_Y_CENTRE + dimensionsMurCourant[1]/2 - dimensionsMurCourant[2] - this.hauteur/2;
    }
    @Override
    public boolean validerMargeMur(float longueurMur, float hauteurMur, float margeSecurite)
    {
        boolean succes = true;
        float epaisseur = (this.murActif.reqDimensionsMur())[4];
        float deltaRainure = (this.murActif.reqDimensionsMur())[3];
        Mur.OrientationMur orientation = this.murActif.reqOrientation();
        
        if (positionX > Mur.POSITION_X_CENTRE)
        {
            switch(orientation)
            {
                /* Order is important, since x axis is right oriented, right point of wall
                        is always further from top left than right point of acc. */
                case FACADE:
                {
                    succes &= (((longueurMur/2.0f + Mur.POSITION_X_CENTRE - epaisseur) - (this.positionX + (this.longueur/2.0f))) > margeSecurite);
                }break;
                
                case GAUCHE:
                {
                    succes &= (((longueurMur/2.0f + Mur.POSITION_X_CENTRE - epaisseur/2.0f - deltaRainure) - (this.positionX + (this.longueur/2.0f))) > margeSecurite);
                }break;
                
                case ARRIERE:
                {
                    succes &= (((longueurMur/2.0f + Mur.POSITION_X_CENTRE - epaisseur) - (this.positionX + (this.longueur/2.0f))) > margeSecurite);
                }break;
                
                case DROITE:
                {
                    succes &= (((longueurMur/2.0f + Mur.POSITION_X_CENTRE - epaisseur/2.0f - deltaRainure) - (this.positionX + (this.longueur/2.0f))) > margeSecurite);
                }break;
                
                default:
                {
                    succes &= false;
                }break;
            }
            
        }
        else
        {
            /* Opposite from preivous case. */
            switch(orientation)
            {
                case FACADE:
                {
                    succes &= (((this.positionX - this.longueur/2) - (Mur.POSITION_X_CENTRE - longueurMur/2.0f + epaisseur))> margeSecurite);
                }break;
                
                case GAUCHE:
                {
                    succes &= (((this.positionX - this.longueur/2) - (Mur.POSITION_X_CENTRE - longueurMur/2.0f + epaisseur/2.0f + deltaRainure))> margeSecurite);
                }break;
                
                case ARRIERE:
                {
                    succes &= (((this.positionX - this.longueur/2) - (Mur.POSITION_X_CENTRE - longueurMur/2.0f + epaisseur))> margeSecurite);
                }break;
                
                case DROITE:
                {
                    succes &= (((this.positionX - this.longueur/2) - (Mur.POSITION_X_CENTRE - longueurMur/2.0f + epaisseur/2.0f + deltaRainure))> margeSecurite);
                }break;
                
                default:
                {
                    succes &= false;
                }break;
            }
        }
        
        /* We only check highest point of door */
        succes &= (((this.positionY - this.hauteur/2.0f) - (Mur.POSITION_Y_CENTRE - hauteurMur/2.0f)) > margeSecurite);

        return succes;
    }
    
    public static boolean ajouterPorte(float positionX, float positionY, float hauteur, float longueur, int id)
    {
        boolean succes = true;
        
        float[] dimensionsMurCourant = Chalet.reqMurCourant().reqDimensionsMur();
        float longueurMur = dimensionsMurCourant[0];
        float hauteurMur = dimensionsMurCourant[1];
        float margeSecurite = dimensionsMurCourant[2];
        float[] realPosition = Chalet.reqMurCourant().convertirPoswithRef(positionX, (hauteur/2) + margeSecurite);
        Porte porteAjouter = new Porte(realPosition[0], realPosition[1], hauteur, longueur, id);
        porteAjouter.murActif = Chalet.reqMurCourant();      
        porteAjouter.estValide = succes;
        
        porteAjouter.murActif.ajouterAccessoire(porteAjouter);
        
        return true;
    }
    
    @Override
    protected boolean redimensionnerAccessoire(float nouveauX, float nouveauY, float nvllLongueur, float nvllHauteur, int accId, boolean conv)
    {        
        boolean succes = true;
        float[] dimensionsMur = this.murActif.reqDimensionsMur();
        float[] newPos = {nouveauX,nouveauY};
        
        if (conv)
        {
            newPos = this.murActif.convertirPoswithRef(nouveauX, (nouveauY + dimensionsMur[2]));
        }
        
        this.positionX = newPos[0];
        this.positionY = newPos[1];
        this.longueur = nvllLongueur;
        this.hauteur = nvllHauteur;
        this.estValide = succes;
        
        this.murActif.verifValiditeAccessoires();
        
        return succes; 
    }
    
    
}
