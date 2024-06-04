package ca.ulaval.glo2004.domaine.utils;

public class PointCartesien 
{
    private float coordX;
    private float coordY;
    private float coordZ;
    
    public PointCartesien(float coordX, float coordY, float coordZ)
    {
        this.coordX = coordX;
        this.coordY = coordY;
        this.coordZ = coordZ;
    }
    
    public float reqCoordX()
    {
        return this.coordX;
    }
    
    public float reqCoordY()
    {
        return this.coordY;
    }
    
    public float reqCoordZ()
    {
        return this.coordZ;
    }
}
