package seshat;

import java.io.OutputStream;
import java.util.Vector;

import seshat.Online.Point;
import seshat.Online.PointR;

public class sentenceF {
	public static class frame {
		  public double x,y,dx,dy,ax,ay,k;

		 public   void print(OutputStream fd) {
			 
		 }
		 public   int get_fr_dim() {
			 return 7;
		 };

		 public  double getFea(int i){
		      switch (i){
		      case 0: return x;
		      case 1: return y;
		      case 2: return dx;
		      case 3: return dy;
		      case 4: return ax;
		      case 5: return ay;
		      case 6: return k;
		      default:
			    System.err.println( "Error: getFea" + i + " )\n" );
			    throw new RuntimeException("Error: getFea" + i + " )\n");
		      }
		    }
		} 
	
	 public  String transcrip;
	 public	    int n_frames;
	 public	    frame[]  frames;
		    
	 public	    sentenceF() {
		 this.n_frames = 0;
		 this.frames = null;
	 };
	 public void destructor() {
		 
	 }

	public boolean data_plot(OutputStream fd) {
		return false;
	}
	public boolean print(OutputStream fd) {
		return false;
	}

	public void calculate_features(Online.sentence  S) {
		  Vector<Point> points = new Vector<Point>();

		    for (int s=0; s<S.n_strokes; s++) {
		      if (S.strokes.get(s).pen_down) {
				  for (int p=0;p<S.strokes.get(s).n_points;p++) {
					  Point pt=S.strokes.get(s).points.get(p);
					  // Set the last point of each stroke -. point_pu=1
					  if (p == (S.strokes.get(s).n_points-1)) pt.setpu();
					  points.add(pt);
				 }
		      } 
		    }
		    // Aspect normalization
		    Vector<PointR> pointsN=normalizaAspect(points);
		    points.clear();

		    n_frames=pointsN.size();

		    frames = new frame[n_frames];

		    // Normalizaed "x" and "y" as first features
		    for (int i=0; i<n_frames; i++) {
		      if( frames[i] == null )
		    	  frames[i] = new frame();
		      frames[i].x=pointsN.get(i).x;
		      frames[i].y=pointsN.get(i).y;
		    }

		    // Derivatives
		    calculate_derivatives(pointsN);
		    
		    // kurvature
		    calculate_kurvature();
		    
		    pointsN.clear();
	};

   private Vector<Online.PointR> normalizaAspect(Vector<Online.Point>   puntos){
	   double ymax=-100000, xmax=-100000, ymin=100000, xmin=100000;
	   
	   // Calculate x,y max and min
	   for (int i=0; i<puntos.size(); i++) {
	     if (puntos.get(i).y<ymin) ymin=puntos.get(i).y;
	     if (puntos.get(i).y>ymax) ymax=puntos.get(i).y;
	     if (puntos.get(i).x<xmin) xmin=puntos.get(i).x;
	     if (puntos.get(i).x>xmax) xmax=puntos.get(i).x;
	   }
	   // Prevent the ymin=ymax case (e.g. for "-" and ".")
	   if (ymin < (ymax+.5) && ymin > (ymax-.5)) ymax=ymin+1;

	   Vector<PointR> trazoNorm = new Vector<PointR>();
	   for (int i = 0; i < puntos.size(); i++) {
	       float TAM=100; 
	     
	     PointR p = new PointR( (float)(TAM * ((puntos.get(i).x - xmin)/(ymax - ymin)))
	    		 ,(float)(TAM * (puntos.get(i).y - ymin)/(ymax - ymin)));
	     
	     // Set in 'p' the attribute of last point of the stroke
	     if (puntos.get(i).getpu()) p.setpu();
	     trazoNorm.add(p);
	   }
	   return trazoNorm;
   }
   private void calculate_derivatives(Vector<Online.PointR>   points ) {
	   calculate_derivatives(points, true);
   }
   private void calculate_derivatives(Vector<Online.PointR>   points, boolean norm) {
	     int sigma=0;
	     int tamW=2;
	   // Denominator calculation
	   for (int i=1; i<=tamW; i++) sigma+=i*i;
	   sigma=2*sigma;

	   // First derivative
	   for (int i=0; i<points.size(); i++) {
		   if( frames[i] == null )
		    	  frames[i] = new frame();
	     frames[i].dx=0;
	     frames[i].dy=0;
	     for (int c=1; c<=tamW; c++) {
	       double context_ant_x,context_ant_y,context_post_x,context_post_y;
	       if (i-c<0) {
	 	context_ant_x=points.get(0).x;
	 	context_ant_y=points.get(0).y;
	       } else {
	 	context_ant_x=points.get(i-c).x;
	 	context_ant_y=points.get(i-c).y;
	       }  
	       if (i+c>=points.size()) {
	 	context_post_x=points.get(points.size()-1).x;
	 	context_post_y=points.get(points.size()-1).y;
	       } else {
	 	context_post_x=points.get(i+c).x;
	 	context_post_y=points.get(i+c).y;
	       }
	       frames[i].dx+=c*(context_post_x-context_ant_x)/sigma;
	       frames[i].dy+=c*(context_post_y-context_ant_y)/sigma;

	       // ---------------------------------------------------
	       if (norm) {
	 	double module=Math.sqrt(frames[i].dx*frames[i].dx+frames[i].dy*frames[i].dy);
	 	if (module>0) {
	 	  frames[i].dx /= module;
	 	  frames[i].dy /= module;
	 	}
	       }
	       // ---------------------------------------------------
	     }
	     if (Math.abs(frames[i].dx)<Float.MIN_VALUE) frames[i].dx=0.0;
	     if (Math.abs(frames[i].dy)<Float.MIN_VALUE) frames[i].dy=0.0;
	   }
	   
	   // Second derivative
	   for (int i=0; i<points.size(); i++) {
	     double context_ant_dx,context_ant_dy,context_post_dx,context_post_dy;
	     if( frames[i] == null )
	    	  frames[i] = new frame();
	     frames[i].ax=0;
	     frames[i].ay=0;
	     for (int c=1; c<=tamW; c++) {
	       if (i-c<0){
	 	context_ant_dx=frames[0].dx;
	 	context_ant_dy=frames[0].dy;
	       } else {
	 	context_ant_dx=frames[i-c].dx;
	 	context_ant_dy=frames[i-c].dy;
	       }
	       if (i+c>=points.size()) {
	 	context_post_dx=frames[points.size()-1].dx;
	 	context_post_dy=frames[points.size()-1].dy;
	       } else {
	 	context_post_dx=frames[i+c].dx;
	 	context_post_dy=frames[i+c].dy;
	       }
	       frames[i].ax+=c*(context_post_dx-context_ant_dx)/sigma;
	       frames[i].ay+=c*(context_post_dy-context_ant_dy)/sigma;
	     }
	     if (Math.abs(frames[i].ax)<Float.MIN_VALUE) frames[i].ax=0.0;
	     if (Math.abs(frames[i].ay)<Float.MIN_VALUE) frames[i].ay=0.0;
	   }
   }
   private  void calculate_kurvature() {
	   for (int i=0; i<n_frames; i++) {
		   if( frames[i] == null )
		    	  frames[i] = new frame();
		    double norma=Math.sqrt(frames[i].dx*frames[i].dx+frames[i].dy*frames[i].dy);
		    if (norma==0) norma=1;
		    frames[i].k=(frames[i].dx*frames[i].ay-frames[i].ax*frames[i].dy)/(norma*norma*norma);
		  }  
   }
	
}