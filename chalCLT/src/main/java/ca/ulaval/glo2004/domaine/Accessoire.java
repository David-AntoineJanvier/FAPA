package ca.ulaval.glo2004.domaine;
import java.util.ArrayList;
import java.util.Collections;

abstract public class Accessoire
{       
    protected float hauteur;
    protected float longueur;
    protected float positionX;
    protected float positionY;
    protected Mur murActif = null;
    protected int id;
    protected boolean estValide;
    
    protected abstract Accessoire makeCopy(Accessoire A);
    public int reqId()
    {
    return this.id;
    }
    public float reqLongueur()
    {
        return this.longueur;
    }
    
    public float reqHauteur()
    {
        return this.hauteur;
    }
    
    public float reqPositionX()
    {
        return this.positionX;
    }
    
    public float reqPositionY()
    {
        return this.positionY;
    }
    
    public void setHauteur(float nvllHauteur)
    {
        this.hauteur = nvllHauteur;
    }
    
    public void setLongueur(float nvllLongueur)
    {
        this.longueur = nvllLongueur;
    }
    
    public void setPositionX(float nvllPositionX)
    {
        this.positionX = nvllPositionX;
    }
    
    public void asgAccValide(boolean valide)
    {
        this.estValide = valide;
    }
    
    public boolean reqAccValide()
    {
        return this.estValide;
    }
    
    public abstract void setPositionY(float nvllPositionY);
    
    public boolean validerMargeAccessoire(float margeSecurite, int accId)
    {        
        ArrayList<Accessoire> accessoiresPresents = this.murActif.reqAccessoiresPresents();
        boolean invalideX = false, invalideY = false;
        boolean succes = true;
        for (Accessoire i : accessoiresPresents)
        {
            if (i.reqId() != accId)
            {
                invalideX = (((this.longueur/2.0f) + margeSecurite + (i.reqLongueur()/2.0f)) 
                                > (Math.abs(this.positionX - i.reqPositionX())));
                invalideY = (((this.hauteur/2.0f) + margeSecurite + (i.reqHauteur()/2.0f)) 
                                > (Math.abs(this.positionY - i.reqPositionY())));

                if (invalideX && invalideY)
                {
                    // Don't assign this.estValide here, it's done one function call above.
                    // This way we prevent either validerMargeMur or ValiderMargeAcc from overwriting one another.
                    succes = false;
                    
                    break;
                }
            }
        }
        
        return succes;
    }
    
    public static boolean modifierDimensionsAccessoire(float nouveauX, float nouveauY, float nvllLongueur, float nvllHauteur, int accId,boolean flag)
    {
        boolean succes = false;
        
        Accessoire accCible = Chalet.reqMurCourant().identifierAccessoire(accId);
        if (accCible != null)
        {
            if (nvllLongueur == -1 && nvllHauteur == -1)
            {
                nvllLongueur = accCible.longueur;
                nvllHauteur = accCible.hauteur;
            }
            succes = accCible.redimensionnerAccessoire(nouveauX, nouveauY, nvllLongueur, nvllHauteur, accId, flag);   
        }
        
        return succes;
    }
    
    
    public abstract boolean validerMargeMur(float longueurMur, float hauteurMur, float margeSecurite);
    
    protected abstract boolean redimensionnerAccessoire(float nouveauX, float nouveauY, float nvllLongueur, float nvllHauteur, int accId, boolean conv);
    
    /* Surcharge de la fonction nous permettant de sort() 
        une ArrayList d'Accessoire selon leur position en X */
    /*
    @Override
    public int compareTo(Accessoire acc)
    {
        int returnValue = -1;

        if ((this.positionX-this.longueur/2) > (acc.reqPositionX()-acc.reqLongueur()/2))
        {
            returnValue = 1;
        }
        else if ((this.positionX-this.longueur/2) == (acc.reqPositionX()-acc.reqLongueur()/2))
        {
            returnValue = 0;
        }

        return returnValue;
    }*/
}
