/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca.ulaval.glo2004.domaine;
import java.util.UUID;
import java.util.ArrayList;


public class ChaletDTO 
{
    public static final int POSITION_X_CENTRE = 50; // FIXME: Add correct value
    public static final int POSITION_Y_CENTRE = 50; // FIXME: Add correct value
    public  Mur MurFacade = null;
    public  Mur MurArriere = null;
    public  Mur MurGauche = null;
    public  Mur MurDroit = null;
    public  Mur MurActif = null; 
    public  UUID Uuid;
    public Toit toit=null;
   
    
    public ChaletDTO (Chalet chalet){
        MurFacade   = new Mur(chalet.reqMurFacade());
        MurArriere  = new Mur(chalet.reqMurArriere());
        MurGauche   = new Mur(chalet.reqMurGauche());
        MurDroit    = new Mur(chalet.reqMurDroit());
        toit = new Toit(chalet.reqToit());
        if (Chalet.reqMurCourant() != null)     /* FIXME: This is a workaround to prevent bug where 
                                                   reqMurCourant is null when we draw top view. 
                                                   The constructor doesn't like a null object. */
        {
            MurActif = new Mur(chalet.reqMurCourant());   
        }
        Uuid = chalet.reqUUID();
    }
    
}
