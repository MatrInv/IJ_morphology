import ij .*;
import ij. process .*;
import ij.gui .*;
import java .awt .*;
import ij. plugin . filter .*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Caracteristiques_Simples implements PlugInFilter {
    byte[] pixels;
    int h,w;

    public boolean inFrame(int x, int y){
	return (x<w && x>=0 && y<h && y>=0);
    }
    
    public void run (ImageProcessor ip) {
	h=ip.getHeight();
	w=ip.getWidth();	
	pixels=(byte[])ip.getPixelsCopy();
	//Phase 1
	//Phase 2
	//Creation de l'image de la DT
	ImageProcessor ipDT= new ByteProcessor(w,h);
	ImagePlus imageDT= new ImagePlus("Caracteristique", ipDT);
	byte[] pixelsDT= (byte[])ipDT.getPixels();

	//Initialisation du DT
	int WHITE = 255, BLACK = 0;

	for(int i=0;i<pixelsDT.length;i++)
	    if(ip.get(i)!=WHITE)
		ipDT.set(i,WHITE);
	    else
		ipDT.set(i,BLACK);

	ArrayList<Point> R = new ArrayList<Point>();
	
	//AIRE
	int A = 0;
	//HAUTEUR & LARGEUR - BOITE ENGLOBANTE
	int Hdeb = h, Hfin = 0, Wdeb = w, Wfin = 0;
	
	for(int j=0;j<h;j++)
	    for(int i=0;i<w;i++)
		if(ipDT.get(i,j)==WHITE){
		    //aire
		    A++;
		    //boite englob.
		    if(Hdeb>j)
			Hdeb=j;
		    if(Hfin<j)
			Hfin=j;
		    if(Wdeb>i)
			Wdeb=i;
		    if(Wfin<i)
			Wfin=i;

		    R.add(new Point(i,j));
		    
		}
	
	//DIAMETRE

	Point pActu;
	int dist, maxDist=0;
	ArrayList<Point> Rbis = new ArrayList<Point>(R);
	while(!Rbis.isEmpty()){
	    pActu = Rbis.remove(0);
	    for(Point p : Rbis){
		dist = (int)( (pActu.getX()-p.getX())*(pActu.getX()-p.getX())+(pActu.getY()-p.getY())*(pActu.getY()-p.getY()) );
		if(dist>maxDist)
		    maxDist = dist;
	    }
	}    
	double diam = Math.sqrt(maxDist);
	
	/*int maxDist=0, dist;
	int Wboite=(Wfin-Wdeb), Hboite=(Hfin-Hdeb);
	int xb, yb;
	//pour chaque point a de la boite englobante
	for(int ya=Hdeb;ya<=Hfin;ya++)
	    for(int xa=Wdeb;xa<=Wfin;xa++)
		//si a est dans R
		if(ipDT.get(xa,ya)==WHITE){
		    //pour tous les points b de la boite englobante qui n'ont pas été déjà parcouru par a
		    for(int b=(ya-Hdeb)*Wboite+(xa-Wdeb);b<Wboite*Hboite;b++){
		    
			xb=Wdeb+(b%Wboite);
			yb=Hdeb+(b/Wboite);

			//si b est dans R
			if(ipDT.get(xb,yb)==WHITE){
			    dist=(xa-xb)*(xa-xb)+(ya-yb)*(ya-yb);//dist euclid
			    //et si la distance est supérieur au max
			    if(dist>maxDist){
				//on stock la valeur de distance carrée
				maxDist=dist;		
			    }
			}
		    }
		}
		double diam = Math.sqrt(maxDist);*/

	//CENTRE DE GRAVITE
	int sumX=0, sumY=0, n=R.size();
	for(Point p : R){
	    sumX+=p.getX();
	    sumY+=p.getY();
	}

	//PERIMETRE
	int[][] img = new int[h][w];
	String str = new String();
	for(int j=0;j<h;j++)
	    for(int i=0;i<w;i++)
		img[j][i]=ipDT.get(i,j);	    
	FreemanCode fc = FreemanCode.extractFreemanCode(img,BLACK);
	IJ.log("=====FreemanCode====="+fc.toString()+"==================\n");
	double perim = fc.perimeter();

	//RAPPORT ISOPERIMETRIQUE
	double rapisoperim = (perim * perim)/(4.*Math.PI*(double)A);

	//ELONGATION
	double elong = 1.-(1./rapisoperim);

	//AFFICHAGE
	IJ.log("Aire = "+A);
	IJ.log("Hauteur = "+Hfin+" - "+Hdeb+" = "+(Hfin-Hdeb));
	IJ.log("Largeur = "+Wfin+" - "+Wdeb+" = "+(Wfin-Wdeb));
	IJ.log("Dimensions boite englobante = "+((Hfin-Hdeb)*(Wfin-Wdeb)));
	IJ.log("Diametre = "+diam);
	IJ.log("Centre de Gravité = ( "+(int)Math.round((double)sumX/n)+" , "+(int)Math.round((double)sumY/n)+" )");
	IJ.log("Perimetre = "+perim);
	IJ.log("Rapport Isoperimetrique = "+rapisoperim);
	IJ.log("Elongation = "+elong);
	
    }

    public int setup ( String arg , ImagePlus imp){
	return DOES_8G ;
    }

}
