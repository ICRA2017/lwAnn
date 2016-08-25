package lwAnn;

import java.awt.Color;

import java.util.UUID;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BrushSequence implements java.io.Serializable{
	  public float brush_size = DrawingBoard.BRUSH_SIZE;
	  public ArrayList<Ellipse2D.Float> points;
	  public Color color = Color.BLACK;
	  public String uuid;
	  BrushSequence(float size,Color c) {
		  brush_size = size;
		  color = c;
		  uuid = UUID.randomUUID().toString();
		  points = new ArrayList<Ellipse2D.Float>();
	  }
	  
	  public void addNewPoint(Point2D.Float p) {
		  points.add(new Ellipse2D.Float(p.x-brush_size/2,p.y-brush_size/2, brush_size, brush_size));
	  }
}
