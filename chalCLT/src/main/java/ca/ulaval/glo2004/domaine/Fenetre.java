package ca.ulaval.glo2004.domaine;

public class Fenetre extends Accessoire
{
    public Fenetre(float positionX, float positionY, float hauteur, float longueur, int cle)
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.hauteur   = hauteur;
        this.longueur  = longueur;
        this.id = cle;
        this.estValide = false;
    }
    @Override
    public void setPositionY(float nvllPositionY)
    {
     this.positionY = nvllPositionY;   
    }
    @Override
    public Accessoire makeCopy(Accessoire A)
    {
        Fenetre obj = new Fenetre(A.reqPositionX(),A.reqPositionY(),A.reqHauteur(),A.reqLongueur(),A.reqId());
        obj.estValide = A.estValide;
        obj.murActif = A.murActif;
        return obj;
    }
    @Override
    public boolean validerMargeMur(float longueurMur, float hauteurMur, float margeSecurite)
    {
        boolean succes = true;
        float epaisseur = (this.murActif.reqDimensionsMur())[4];
        float deltaRainure = (this.murActif.reqDimensionsMur())[3];
        Mur.OrientationMur orientation = this.murActif.reqOrientation();
        
        
        if (this.positionY < Mur.POSITION_Y_CENTRE)
        {
            /* Order is important, since y axis is down oriented, highest point of acc
                is always further from top left than highest point of wall. */
            succes &= (((this.positionY - (this.hauteur/2.0f)) - (Mur.POSITION_Y_CENTRE - hauteurMur/2.0f)) > margeSecurite);
        }
        else
        {
            /* Opposite from previous case. */
            succes &= (((Mur.POSITION_Y_CENTRE + hauteurMur/2.0f) - (this.positionY + this.hauteur/2.0f)) > margeSecurite);
        }
        
        if (this.positionX > Mur.POSITION_X_CENTRE)
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
        
        return succes;
    }
    
    public static boolean ajouterFenetre(float positionX, float positionY, float hauteur, float longueur, int id)
    {
        boolean succes = true;
        
        float[] realPosition = Chalet.reqMurCourant().convertirPoswithRef(positionX, positionY);
        Fenetre fenetreAjouter = new Fenetre(realPosition[0], realPosition[1], hauteur, longueur, id);
        fenetreAjouter.murActif = Chalet.reqMurCourant();        
        fenetreAjouter.estValide = succes;
        fenetreAjouter.murActif.ajouterAccessoire(fenetreAjouter);
        
        fenetreAjouter.murActif.verifValiditeAccessoires();
        
        return true;
    }
    
    @Override
    protected boolean redimensionnerAccessoire(float nouveauX, float nouveauY, float nvllLongueur, float nvllHauteur, int accId, boolean convertir)
    {   
        boolean succes = true;
        float[] dimensionsMur = this.murActif.reqDimensionsMur();
        
        float[] newPos = {nouveauX,nouveauY};
        
        if (convertir)
        {
            newPos = this.murActif.convertirPoswithRef(nouveauX, (nouveauY + dimensionsMur[2]));
        }
        
        this.positionX = newPos[0];
        this.positionY = newPos[1];
        this.longueur = nvllLongueur;
        this.hauteur = nvllHauteur;
        
        this.murActif.verifValiditeAccessoires();

        return succes; 
    }
    
}
