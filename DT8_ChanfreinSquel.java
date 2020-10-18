import ij .*;
import ij. process .*;
import ij.gui .*;
import java .awt .*;
import ij. plugin . filter .*;

public class DT8_ChanfreinSquel implements PlugInFilter {
    byte[] pixels;
    int h,w;


    public void run (ImageProcessor ip){
	h=ip.getHeight();
	w=ip.getWidth();	
	pixels=(byte[])ip.getPixelsCopy();
	//Phase 1
	//Phase 2
	//Creation de l'image de la DT
	ImageProcessor ipDT= new ByteProcessor(w,h);
	ImagePlus imageDT= new ImagePlus("DT8 Chanfrein", ipDT);
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

		if(obsX<w && obsX>=0 && obsY<h && obsY>=0){//si on depasse pas du cadre avec le masque
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

		if(obsX<w && obsX>=0 && obsY<h && obsY>=0){//si on depasse pas du cadre avec le masque
		    //IJ.log(" = "+ipDT.get(obsY*w+obsX)+" + "+M[mi]);
		    actu = ipDT.get(obsY*w+obsX)+M[mi];
		    if(actu<min){
			min = actu;
		    }
		}

	    }

	    //on garde l'intensité max pour normaliser l'intensité
	    if(min>maxIntensity)
		maxIntensity=min;
	    
	    ipDT.set(i,min);
			
	}

	//amplifier pour l'affichage + normalisation de l'intensité
	double AMPL = 255./(double)maxIntensity;
	for(int i=0;i<h*w;i++)
	    if(ipDT.get(i)*AMPL>255)
		ipDT.set(i,255);
	    else
		ipDT.set(i,(int)Math.round((double)ipDT.get(i)*AMPL));

	// Mettre des valeurs dans pixelsDT		
	imageDT.show();
	imageDT.updateAndDraw();
    }

    /*public void applyMask(int[][] m, boolean portionHaute, int x, int y){

      for(int j=-(m.length)/2;j<=(m.length)/2;j++){
      for(int i=-(m[0].length)/2;i<=(m[0].length)/2;i++){
      if( (x+i)>=0 && (y+j)>=0 &&(x+i)<w && (y+j)<h )
      pixels[(y+j)*w+(x+i)] = m[]
      }
      }
	 	
      }*/

    public int setup ( String arg , ImagePlus imp){
	return DOES_8G ;
    }

}
