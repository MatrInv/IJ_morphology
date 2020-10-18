import ij .*;
import ij. process .*;
import ij.gui .*;
import java .awt .*;
import ij. plugin . filter .*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Squelettisation implements PlugInFilter {
    byte[] pixels;
    int h,w;

    //fonction indiquant si le pt (x,y) se trouve dans l'image
    public boolean inFrame(int x, int y){
	return (x<w && x>=0 && y<h && y>=0);
    }

    //procédure qui ajoute p à array en le triant selon les distances de chanfrein  
    public void sortingAdd(ArrayList<Point> array, Point p, int[][] dist){
	//si array est vide ou que son dernier elt est inférieur à p
	if(array.isEmpty())
	    array.add(p);
	else if(dist[(int)p.getY()][(int)p.getX()]>=dist[(int)array.get(array.size()-1).getY()][(int)array.get(array.size()-1).getX()])
	    array.add(p);
	else
	    for(int i=0;i<array.size();i++){
		if( dist[(int)p.getY()][(int)p.getX()]<dist[(int)array.get(i).getY()][(int)array.get(i).getX()]){
		    array.add(i,p);
		    break;
		}
	    }
    }

    public int nbVoisins(int x, int y, int[][] bin){
	int cpt = 0;
	if(inFrame(x,y+1) && bin[y+1][x]==1)
	    cpt++;
	if(inFrame(x,y-1) && bin[y-1][x]==1)
	    cpt++;
	if(inFrame(x+1,y+1) && bin[y+1][x+1]==1)
	    cpt++;
	if(inFrame(x-1,y+1) && bin[y+1][x-1]==1)
	    cpt++;
	if(inFrame(x+1,y-1) && bin[y-1][x+1]==1)
	    cpt++;
	if(inFrame(x-1,y-1) && bin[y-1][x-1]==1)
	    cpt++;
	if(inFrame(x+1,y) && bin[y][x+1]==1)
	    cpt++;
	if(inFrame(x-1,y) && bin[y][x-1]==1)
	    cpt++;

	return cpt;
    }

    //Fonction qui retourne le nombre de yokoi en 8-connexité du pt (x,y) sur l'image binaire bin
    public int yokoi_8(int x, int y, int[][] bin){
	int x0,x1,x2,x3,x4,x5,x6,x7;
	
	if(inFrame(x,y+1))
	    x0=1-bin[y+1][x];
	else
	    x0=1;
	
	if(inFrame(x-1,y+1))
	    x1=1-bin[y+1][x-1];
	else
	    x1=1;
	
	if(inFrame(x-1,y))
	    x2=1-bin[y][x-1];
	else
	    x2=1;
	
	if(inFrame(x-1,y-1))
	    x3=1-bin[y-1][x-1];
	else
	    x3=1;
	
	if(inFrame(x,y-1))
	    x4=1-bin[y-1][x];
	else
	    x4=1;
	
	if(inFrame(x+1,y-1))
	    x5=1-bin[y-1][x+1];
	else
	    x5=1;
	
	if(inFrame(x+1,y))
	    x6=1-bin[y][x+1];
	else
	    x6=1;
	
	if(inFrame(x+1,y+1))
	    x7=1-bin[y+1][x+1];
	else
	    x7=1;
	
	int yokoiNb = x0*(1-(x1*x2))+x2*(1-(x3*x4))+x4*(1-(x5*x6))+x6*(1-(x7*x0));

	return yokoiNb;
    }
    
//Fonction qui retourne le nombre de yokoi en 4-connexité du pt (x,y) sur l'image binaire bin
    public int yokoi_4(int x, int y, int[][] bin){
	int x0,x1,x2,x3,x4,x5,x6,x7;
	
	if(inFrame(x,y+1))
	    x0=bin[y+1][x];
	else
	    x0=0;
	if(inFrame(x-1,y+1))
	    x1=bin[y+1][x-1];
	else
	    x1=0;
	
	if(inFrame(x-1,y))
	    x2=bin[y][x-1];
	else
	    x2=0;
	
	if(inFrame(x-1,y-1))
	    x3=bin[y-1][x-1];
	else
	    x3=0;
	
	if(inFrame(x,y-1))
	    x4=bin[y-1][x];
	else
	    x4=0;
	
	if(inFrame(x+1,y-1))
	    x5=bin[y-1][x+1];
	else
	    x5=0;
	
	if(inFrame(x+1,y))
	    x6=bin[y][x+1];
	else
	    x6=0;
	
	if(inFrame(x+1,y+1))
	    x7=bin[y+1][x+1];
	else
	    x7=0;
	
	int yokoiNb = x0*(1-(x1*x2))+x2*(1-(x3*x4))+x4*(1-(x5*x6))+x6*(1-(x7*x0));

	return yokoiNb;
    }
    
    //prédicat sur la simplicité du pt (x,y) dans bin
    public boolean isSimple(int x, int y, int[][] bin){

	return yokoi_8(x,y,bin) == 1 && yokoi_4(x,y,bin) == 1 ;
    }
    
    public void run (ImageProcessor ip){
	h=ip.getHeight();
	w=ip.getWidth();	
	pixels=(byte[])ip.getPixelsCopy();
	//Phase 1
	//Phase 2
	//Creation de l'image de la DT
	ImageProcessor ipDT= new ByteProcessor(w,h);
	ImagePlus imageDT= new ImagePlus("Squelette_DT8", ipDT);
	byte[] pixelsDT= (byte[])ipDT.getPixels();

	//Initialisation du DT
	int WHITE = 255, BLACK = 0;

	int[][] bin = new int[h][w];
	ArrayList<Point> R = new ArrayList<Point>();
	
	for(int i=0;i<pixelsDT.length;i++)
	    if(ip.get(i)!=WHITE){
		ipDT.set(i,WHITE);
		bin[i/w][i%w]=1;
		R.add(new Point(i%w,i/w));
	    }
	    else{
		ipDT.set(i,BLACK);
		bin[i/w][i%w]=0;
	    }

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
	int[][] distMap = new int[h][w];
	int obsX, obsY, transX = mw/2, transY = mh/2;
	
	for(int i=0;i<w*h;i++){
	    min = WHITE;

	    for(int mi=0;mi<=(mh*mw)/2;mi++){

		obsX = (i%w)+(mi%mw)-transX;
		obsY = (i/w)+(mi/mw)-transY;

		if(inFrame(obsX,obsY)){//si on depasse pas du cadre avec le masque
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

	    for(int mi=(mh*mw)/2;mi<(mh*mw);mi++){

		obsX = (i%w)+(mi%mw)-transX;
		obsY = (i/w)+(mi/mw)-transY;

		if(inFrame(obsX,obsY)){//si on depasse pas du cadre avec le masque
		    actu = ipDT.get(obsY*w+obsX)+M[mi];
		    if(actu<min){
			min = actu;
		    }
		}

	    }
	    
	    ipDT.set(i,min);

	    distMap[i/w][i%w]=min;
			
	}
	
	//ON TRIE R EN FONCTION DES DISTANCES DE CHANFREIN DANS UNE NOUVELLE LISTE O
	ArrayList<Point> O = new ArrayList<Point>();//LISTE TRIEE AUTOMATIQUEMENT PAR  SORTINGADD
	for(Point p : R)
	    sortingAdd(O,p,distMap);

	boolean fini=false;
	int x, y;
	while(!fini){
	    fini=true;
	    //ON PARCOURT O DANS L'ORDRE CROISSANT DES DISTANCES DE CHANFREIN
	    for(Point p : O){
		x=(int)p.getX();
		y=(int)p.getY();
		//AU PREMIER PT SIMPLE TROUVE, ON SUPPRIME LE PT DE BIN ET DE O
		//(ici, on modifie legerement la condition de sorte à obtenir un squelette plus significatif qu'un pauvre point.
		//Ainsi, on ne supprime pas les points qui ont moins de deux voisins. On voit alors l'apparition de quelques "eventails" indésirables sur le squelette, mais c'est toujours mieux qu'un seul point.) 
		if(isSimple(x,y,bin) && nbVoisins(x,y,bin)>1){
		    
		    bin[y][x]=0;
		    O.remove(p);
		    fini=false;
		    break;
		    
		}
		
	    }

	    //ON RECOMMENCE JUSQU'A CE QU'IL N'Y AI PLUS DE PT SIMPLE DANS R

	}


	//AFFICHAGE
	for(int j=0;j<bin.length;j++)
	    for(int i=0;i<bin[0].length;i++)
		ipDT.set(i,j,bin[j][i]*255);
	    
	// Mettre des valeurs dans pixelsDT		
	imageDT.show();
	imageDT.updateAndDraw();
    }

    public int setup ( String arg , ImagePlus imp){
	return DOES_8G ;
    }

}
