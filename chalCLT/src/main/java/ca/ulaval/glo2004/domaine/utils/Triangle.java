package ca.ulaval.glo2004.domaine.utils;

public class Triangle 
{
    private final PointCartesien pointA;
    private final PointCartesien pointB;
    private final PointCartesien pointC;
    private final String vecteurNormal;
    
    public Triangle(PointCartesien pointA, PointCartesien pointB, PointCartesien pointC, String normal)
    {
        this.pointA = pointA;
        this.pointB = pointB;
        this.pointC = pointC;
        this.vecteurNormal = normal;
    }
    
    public PointCartesien getPointA()
    {
    return this.pointA;
    }
    public PointCartesien getPointB()
    {
    return this.pointB;
    }
    public PointCartesien getPointC()
    {
    return this.pointC;
    }
    
    public String reqNormal()
    {
        return this.vecteurNormal;
    }
}
