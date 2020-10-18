import ij .*;
import ij. process .*;
import ij.gui .*;
import java .awt .*;
import ij. plugin . filter .*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class AxeMedian_DT8 implements PlugInFilter {
    byte[] pixels;
    int h,w;

    public boolean inFrame(int x, int y){
	return (x<w && x>=0 && y<h && y>=0);
    }
    
    public void run (ImageProcessor ip){
	h=ip.getHeight();
	w=ip.getWidth();	
	pixels=(byte[])ip.getPixelsCopy();
	//Phase 1
	//Phase 2
	//Creation de l'image de la DT
	ImageProcessor ipDT= new ByteProcessor(w,h);
	ImagePlus imageDT= new ImagePlus("AxeMedian_DT8", ipDT);
	byte[] pixelsDT= (byte[])ipDT.getPixels();

	//Initialisation du DT
	int WHITE = 255, BLACK = 0;

	for(int i=0;i<pixelsDT.length;i++)
	    if(ip.get(i)!=WHITE)
		ipDT.set(i,WHITE);
	    else
		ipDT.set(i,BLACK);

	//Initialisation du mask
	int mh = 3 , mw = 3;
	int[] M = new int[mw*mh];
	for(int i=0;i<mw*mh;i++)
	    M[i] = 1;
	M[(mh*mw)/2]=0;

	//premier balayage de haut en bas
	int maxIntensity=BLACK;
	int min;
	int actu;
	int obsX, obsY, transX = mw/2, transY = mh/2;
	
	for(int i=0;i<w*h;i++){
	    min = WHITE;
	    //IJ.log("======( "+(i%w)+" , "+(i/w)+" ) : "+ipDT.get(i)+" =====");
	    for(int mi=0;mi<=(mh*mw)/2;mi++){

		obsX = (i%w)+(mi%mw)-transX;
		obsY = (i/w)+(mi/mw)-transY;
		//IJ.log("( "+obsX + " , "+obsY+") : "+(obsY*w+obsX));

		if(inFrame(obsX,obsY)){//si on depasse pas du cadre avec le masque
		    //IJ.log(" = "+ipDT.get(obsY*w+obsX)+" + "+M[mi]);
		    actu = ipDT.get(obsY*w+obsX)+M[mi];
		    if(actu<min){
			min = actu;
		    }
		}

	    }
	    
	    ipDT.set(i,min);
			
	}


	//Deuxieme balayage de bas en haut
	for(int i=w*h-1;i>=0;i--){
	    min = WHITE;
	    //IJ.log("======( "+(i%w)+" , "+(i/w)+" ) : "+ipDT.get(i)+" =====");
	    for(int mi=(mh*mw)/2;mi<(mh*mw);mi++){

		obsX = (i%w)+(mi%mw)-transX;
		obsY = (i/w)+(mi/mw)-transY;
		//IJ.log("( "+obsX + " , "+obsY+") : "+(obsY*w+obsX));

		if(inFrame(obsX,obsY)){//si on depasse pas du cadre avec le masque
		    //IJ.log(" = "+ipDT.get(obsY*w+obsX)+" + "+M[mi]);
		    actu = ipDT.get(obsY*w+obsX)+M[mi];
		    if(actu<min){
			min = actu;
		    }
		}

	    }
	    
	    ipDT.set(i,min);
			
	}

	//Recherche des points de l'axe median avec les distances de chanfrein
	ArrayList<Point> AM = new ArrayList<Point>();
	int rb;
	for(int yb=0;yb<h;yb++)
	    for(int xb=0;xb<w;xb++)
		if(ipDT.get(xb,yb)!=BLACK) //&& ipDT.get(xb,yb)!=BLACK+1)
		{
		    rb = ipDT.get(xb,yb);
		    SEARCH:{
			for(int j=0;j<3;j++)
			    for(int i=0;i<3;i++){
				if(inFrame(i-1+xb,j-1+yb) && rb < ipDT.get(i-1+xb,j-1+yb) )
				    break SEARCH;
			    }
			AM.add(new Point(xb,yb));
		    }
		
		}

	//Mise à jour de l'image
	for(int y=0;y<h;y++)
	    for(int x=0;x<w;x++)
		ipDT.set(x,y,BLACK);
	for(Point P : AM)
	    ipDT.set((int)P.getX(),(int)P.getY(),WHITE);
		
	// Mettre des valeurs dans pixelsDT		
	imageDT.show();
	imageDT.updateAndDraw();
    }

    public int setup ( String arg , ImagePlus imp){
	return DOES_8G ;
    }

}
