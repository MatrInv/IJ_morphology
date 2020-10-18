import java.util.ArrayList;

public class FreemanCode {
    private int x;
    private int y;
    private ArrayList<Integer> path;
	
    public FreemanCode(){
	path = new ArrayList<Integer>();
    }

    public void setInitial(int X, int Y){
	x=X;
	y=Y;
    }
	
    public void addToPath(int k){
	path.add(k);
    }
	
    public int X(){
	return x;
    }
	
    public int Y(){
	return y;
    }
	
    public int get(int i){
	return (int)path.get(i);
    }
	
    public int pathSize() {
	return path.size();
    }

    public double perimeter(){
	double perim=0.;
	for(int i=0;i<path.size();i++)
	    if(path.get(i)==0 || path.get(i)==2 || path.get(i)==4 || path.get(i)==6)
		perim+=1.;
	    else
		perim+=Math.sqrt(2);
	
	return perim;
    }

    public static FreemanCode extractFreemanCode(int[][] pic, int voidColor)/* throws Exception*/{
		
	int h = pic.length;
	int w = pic[0].length;
		
	int initX = -1;
	int initY = -1;
		
	FreemanCode fc = new FreemanCode();
		
	//searching for the first pixel
	search:{
			
	    for(int y=0;y<h;y++)
		for(int x=0;x<w;x++)
		    if(pic[y][x]!=voidColor){
			initX=x;
			initY=y;
			break search;
		    }
	}
		
	//throw exception if pic is empty
	/*if(initX<0 || initY<0){
	    throw new Exception(){};
	    }*/
	
	//
	int curX = initX;
	int curY = initY;
	int lastDir = 3;
	
	//while there's is no 8-curve
	do{
	    //for every direction in trigonometric sense
	    for(int d=0;d<8;d++){	
		//we check the first pixel of edge
		if(isEdge(curX+moveX(lastDir),curY+moveY(lastDir),pic,voidColor)){
		    fc.addToPath(lastDir);
		    curX += moveX(lastDir);
		    curY += moveY(lastDir);
		    break;
		}
		
		//incrementing direction
		lastDir = (lastDir+1)%8;
				
	    }
				
	    lastDir = resetFrom(lastDir);
		
	}while(curY!=initY || curX!=initX);
		
	fc.setInitial(initX, initY);
		
	return fc;	
    }

    public static boolean isEmpty(int x, int y, int[][] pic, int voidColor){
	return y<0 || x<0 || y>pic.length-1 || x>pic[0].length-1 || pic[y][x]==voidColor ;
    }
	
    public static boolean isEdge(int x, int y, int[][] pic, int voidColor){
	return (isEmpty(x+1,y,pic,voidColor) || isEmpty(x-1,y,pic,voidColor) || isEmpty(x,y+1,pic,voidColor) || isEmpty(x,y-1,pic,voidColor)) && y>=0 && x>=0 && y<=pic.length-1 && x<=pic[0].length-1 && pic[y][x]!=voidColor;
    }
	
    //renvoit la valeur du mouvement en x selon la direction
    public static int moveX(int dir){
	if(dir==0 || dir==1 || dir==7)
	    return 1;
		
	if(dir==3 || dir==4 || dir==5)
	    return -1;
		
	return 0;
    }
	
    //renvoit la valeur du mouvement en y selon la direction
    public static int moveY(int dir){
	if(dir==1 || dir==2 || dir==3)
	    return -1;
		
	if(dir==5 || dir==6 || dir==7)
	    return 1;
		
	return 0;
    }

    //renvoit la direction réinitialisee en fin de boucle à partir de la direction courante
    public static int resetFrom(int dir) {
	return (dir+5)%8;
    }

    /*
    private static int inf = 1000000000;
    private static int maxD = 10000;
    private static int maxN = 100;
    private static int[][] cache = new int [maxN][maxD*2+10];
    	
	
    //return editDistance between two freeman codes
    public static int editDist(int i, int j, FreemanCode fc1, FreemanCode fc2, int[][] cache /*initialisé à -1*/ /*) {
	int sz1 = fc1.pathSize();
	int sz2 = fc2.pathSize();
		
	if(i==fc1.pathSize())
	    return sz2-j;
		
	if(j==fc2.pathSize())
	    return sz2-i;
		
	if(Math.abs(i-j)>maxD)
	    return inf;
		
	int res = cache[i][i-j+maxD];
		
	if(res>0)
	    return res;
		
	res=inf;
	res=Math.min(res, editDist(i+1, j, fc1, fc2)+1);
	res=Math.min(res, editDist(i, j+1, fc1, fc2)+1);
		
	if(fc1.get(i)==fc2.get(i))
	    res=Math.min(res, editDist(i+1, j+1, fc1, fc2));
	else
	    res=Math.min(res, editDist(i+1, j+1, fc1, fc2)+1);
		
	cache[i][i-j+maxD]=res;
		
	return res;
    }*/
	
    public String toString() {
	String str = new String();
		
	str+="\ninitial X : "+x;
	str+="\ninitial Y : "+y;
	str+="\npath : ";
		
	for(int i=0;i<path.size();i++)
	    str+=path.get(i);
	str+="\n";
		
	return str;
    }
}
