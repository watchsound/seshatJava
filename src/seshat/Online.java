package seshat;

import java.util.Vector;

public class Online {
	public static class PointR {
		  // True if this is the last point of a stroke
		  boolean point_pu;

		 public float x, y;
		  
		 public PointR(float _x, float _y) {
			 this.x = _x;
			 this.y = _y;
			 this.point_pu = false;
		 }
		 public  PointR  operator_assign(  PointR  p) {
		       x=p.x; y=p.y;
		      point_pu=p.point_pu;
		      return  this;
		  }
		public  boolean equals(  PointR   p)   {
		    return p.x==x && p.y==y;
		}
		
		public boolean notEquals(  PointR p)   {
		    return p.x!=x || p.y!=y;
		  }
		
		public  void setpu() {
		    point_pu=true;
		  }
		public  boolean getpu() {
		    return point_pu;
		  }
		};



		//Integer point
	 public static	class Point {
		  // True if this is the last point of a stroke
		private  boolean point_pu;
		 public int x, y;
		  
		 public Point(int _x, int _y) {
			 this.x = _x;
			 this.y = _y;
			 this.point_pu = false;
		 }
		 public  Point   operator_assign(  Point   p) {
		       x=p.x; y=p.y;
		      point_pu=p.point_pu;
		      return  this;
		  }
		public  boolean equals(  Point   p)   {
		    return p.x==x && p.y==y;
		}
		
		public boolean notEquals(  Point  p)   {
		    return p.x!=x || p.y!=y;
		  }
		
		public  void setpu() {
		    point_pu=true;
		  }
		public  boolean getpu() {
		    return point_pu;
		  }
		};


	 public static	class stroke {
		 public  int n_points;
		 public  boolean pen_down;
		 public boolean is_hat;
		 public  Vector<Point> points = new Vector<Point>();
		 
		 public  stroke( ) {
			 this(0,false,false);
			}
		 public  stroke(int n_p ) {
			 this(n_p,false,false);
			}
		 public  stroke( int n_p, boolean pen_d) {
			 this(n_p,pen_d,false);
			}
		public  stroke(int n_p, boolean pen_d , boolean is_ht ) {
			this.n_points = n_p;
			this.pen_down = pen_d;
			this.is_hat = is_ht;
		}
		  
		public  int F_XMIN() {
			  int xmin=Integer.MAX_VALUE;
			  for (int p=0; p<n_points; p++)
			    if (xmin>points.get(p).x) xmin=points.get(p).x;
			  return xmin;
		};
		public  int F_XMAX() {
			  int xmax=Integer.MIN_VALUE;
			  for (int p=0; p<n_points; p++)
			    if (xmax<points.get(p).x) xmax=points.get(p).x;
			  return xmax;
		};
		public  int F_XMED() {
			  int xmed=0;
			  for (int p=0; p<n_points; p++) xmed+=points.get(p).x;
			  return xmed/n_points;
		};
   };


	public static class sentence {
		 public int n_strokes;
		 public  Vector<stroke> strokes = new Vector<stroke>();
		 
		public  sentence(int n_s) {
			this.n_strokes = n_s;
		}
		  
		public  sentence  anula_rep_points() {
			  sentence  sent_norep=new sentence(n_strokes);
			  for (int s=0; s<n_strokes; s++) {
			    stroke stroke_norep = new stroke();
			    Vector<Point> puntos= strokes.get(s).points;
			    int np=strokes.get(s).n_points;
			    for (int p=0; p<np; p++) {
			      if (p<(np-1) && puntos.get(p)==puntos.get(p+1)) continue;
			      Point point = new Point(puntos.get(p).x,puntos.get(p).y);
			      stroke_norep.points.add(point);
			    }
			    stroke_norep.pen_down=strokes.get(s).pen_down;
			    stroke_norep.n_points=stroke_norep.points.size();
			     sent_norep .strokes.add(stroke_norep);
			  }
			  return sent_norep;
		}
		public sentence   suaviza_traza(  ) {
			return suaviza_traza(2);
		}
		public sentence   suaviza_traza(int cont_size ) {
			 int sum_x,sum_y;
			  sentence   sentNorm=new sentence(n_strokes);
			  for (int i=0; i<n_strokes; i++) {
			    stroke strokeNorm = new stroke();
			    Vector<Point> puntos=strokes.get(i).points;
			    int np=strokes.get(i).n_points;
			    for (int p=0; p<np; p++){
			      sum_x=sum_y=0;
			      for (int c=p-cont_size; c<=p+cont_size; c++)
				if (c<0) {
				  sum_x+=puntos.get(0).x;
				  sum_y+=puntos.get(0).y;
				} else if (c>=np) {
				  sum_x+=puntos.get(np-1).x;
				  sum_y+=puntos.get(np-1).y;
				} else {
				  sum_x+=puntos.get(c).x;
				  sum_y+=puntos.get(c).y;
				}
			      Point point = new Point( (sum_x/(cont_size*2+1)), (sum_y/(cont_size*2+1)));
			      strokeNorm.points.add(point);
			    }
			    strokeNorm.pen_down=strokes.get(i).pen_down;
			    strokeNorm.n_points=strokeNorm.points.size();
			     sentNorm .strokes.add(strokeNorm);
			  }
			  return sentNorm;
		}
  } 

}
