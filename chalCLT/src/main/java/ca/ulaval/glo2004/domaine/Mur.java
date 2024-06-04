package ca.ulaval.glo2004.domaine;
import ca.ulaval.glo2004.domaine.afficheur.Afficheur;
import ca.ulaval.glo2004.domaine.utils.PointCartesien;
import java.util.ArrayList;

public class Mur 
{
    public static final float POSITION_X_CENTRE = 117.0f;
    public static final float POSITION_Y_CENTRE = 69.0f;
   
    public static enum OrientationMur
    {
        FACADE,
        ARRIERE,
        GAUCHE,
        DROITE,
        MAX_ORIENTATION_VALUE
    }
    
    private float longueur;
    private float hauteur;
    private float largeur;
    private int nombreDeCouches;
    private float margeSecurite;
    private float rainureSupplementairePouce;
    private final float rainureInitialePourcent;
    private float[] couleur = new float[] {250.0f, 00.0f, 00.0f};
    
    private boolean drapeauAcc = false;
    private ArrayList<Accessoire> accessoiresPresents = null;
    private OrientationMur orientation = OrientationMur.MAX_ORIENTATION_VALUE;
    
    public Mur(float longueur, float hauteur, float margeSecurite, float largeur, 
                int nombreDeCouches, OrientationMur orientation, float rainureSupplementairePouce)
    {
        this.longueur = longueur;
        this.hauteur = hauteur;
        this.margeSecurite = margeSecurite;
        this.largeur = largeur;
        this.nombreDeCouches = nombreDeCouches;
        this.orientation = orientation;
        this.rainureSupplementairePouce = rainureSupplementairePouce;
        
        this.accessoiresPresents = new ArrayList<Accessoire>();
        this.rainureInitialePourcent = 0.50f;
    }
    
    public Mur (Mur mur)
    {
        float[] dimensionsAncien = mur.reqDimensionsMur();
    
        this.longueur = dimensionsAncien[0];
        this.hauteur = dimensionsAncien[1];
        this.margeSecurite = dimensionsAncien[2];
        this.largeur = dimensionsAncien[4];                        
        this.nombreDeCouches = mur.reqNombreCouches();
        this.orientation = mur.reqOrientation();
        this.rainureSupplementairePouce = dimensionsAncien[3];
        this.accessoiresPresents = new ArrayList<>();
        for (Accessoire A : mur.reqAccessoiresPresents())
        {
            this.accessoiresPresents.add(A.makeCopy(A));
        }
        this.rainureInitialePourcent = mur.reqRainurePourcent();
        this.drapeauAcc = mur.reqAccPresentFlag();
    }
    
    public float[] reqDimensionsMur()
    {
        float[] dimensionsMur = {this.longueur, this.hauteur, this.margeSecurite, this.rainureSupplementairePouce, this.largeur};
        return dimensionsMur;
    }
    
    public int reqNombreCouches()
    {
        return this.nombreDeCouches;
    }
    
    public float reqRainurePourcent()
    {
        return this.rainureInitialePourcent;
    }
    
    public ArrayList<Accessoire> reqAccessoiresPresents()
    {
        return this.accessoiresPresents;
    }
    
    public boolean reqAccPresentFlag()
    {
        return this.drapeauAcc;
    }
    
    public OrientationMur reqOrientation()
    {
        return this.orientation;
    }
    
    public void asgLongueur(float nvllLongueur)
    {
        if(nvllLongueur > 2*this.largeur) {
            this.longueur = nvllLongueur;
        }
        
    }
    
    
    public void asgHauteur(float nvllHauteur)
    {
        this.hauteur = nvllHauteur;
    }
        
    public void asgMargeSecurite(float nvllMargeSecurite)
    {
        this.margeSecurite = nvllMargeSecurite;
    }
    
    public void asgCouleur(float[] nouvCouleur){
    this.couleur = nouvCouleur;
    }
    
    public float[] getCouleur() {
        return this.couleur;
    }
    
    public void asgEpaisseur(float epaisseur){
        this.largeur = epaisseur;
    }
    public void asgRainureSupplementaire(float nvllRainureSupplementaire)
    {
        this.rainureSupplementairePouce = nvllRainureSupplementaire;
    }
    
    public Accessoire identifierAccessoire(int accId)
    {
        ArrayList<Accessoire> accessoire = this.accessoiresPresents;
        Accessoire accessoireIdentifier = null;
        
        for (Accessoire i: accessoire)
        {
            if (i.reqId()==accId)
            {
                accessoireIdentifier = i;
            }
        }
        return accessoireIdentifier;
    }
    
    public void ajouterAccessoire(Accessoire accAjouter)
    {
        this.accessoiresPresents.add(accAjouter);
        this.drapeauAcc = true;
        
        this.verifValiditeAccessoires();
    }
    
    public boolean supprimerAccessoire(int accId)
    {
        Accessoire accSupprimer = identifierAccessoire(accId);
        boolean succes = false;
        
        if (accSupprimer != null)
        {
            succes = this.accessoiresPresents.remove(accSupprimer);
            if (succes && this.accessoiresPresents.isEmpty())
            {
                this.drapeauAcc = false;
            }   
        }
        
        this.verifValiditeAccessoires();
        
        return succes;
    }
    
    public float[] convertirPoswithRef(float posX, float posY)
    {   
        float newPosXwithRef = POSITION_X_CENTRE - (float)(this.longueur/2) + posX;
        float newPosYwithRef = POSITION_Y_CENTRE + (float)(this.hauteur/2) - posY;
        
        float[] newPositions = {newPosXwithRef, newPosYwithRef};     
        return newPositions;
    }
    
    public void verifValiditeAccessoires()
    {
        boolean succes = true;
        
        for (Accessoire acc : this.accessoiresPresents)
        {
            succes = true;
            
            succes &= acc.validerMargeAccessoire(this.margeSecurite, acc.reqId());
            succes &= acc.validerMargeMur(this.longueur, this.hauteur, this.margeSecurite);
            
            acc.asgAccValide(succes);
        }
    }
}
