package lwAnn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

public class LabelRegister implements java.io.Serializable{
		public String label;
		public String uuid;
		transient BufferedImage canvas;
		public ArrayList<BrushSequence> strokes;
		public BrushSequence curBrushSeq;
		Color c;
		
		LabelRegister(String lab) {
			this.label = lab;
			Random r = new Random();
			c = new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255));
			strokes = new ArrayList<BrushSequence>();
			this.uuid = UUID.randomUUID().toString();
			canvas = new BufferedImage(1024,768,BufferedImage.TYPE_INT_ARGB);
		}
		
		public void dump(String fn) throws IOException {
			System.out.println("TRYING TO DUMP: " + fn);
			fn = fn.replace("lwann_data.dat", "");
			ImageIO.write(canvas, "png", new File(fn+this.uuid+"_"+this.label+"_"+".png"));
	        
		}
		
	    private void writeObject(ObjectOutputStream out) throws IOException {
	        out.defaultWriteObject();
	        ImageIO.write(canvas, "png", out); // png is lossless
	        
	        // but also dump this to files, to be super sure
	        //ImageIO.write(canvas, "png", new File("./"+this.uuid+".png"));
	        
	    }

	    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	        in.defaultReadObject();
	        canvas = ImageIO.read(in);
	    }
		
		public void beginNewSequence(BrushSequence b) {
			curBrushSeq = b;
			strokes.add(curBrushSeq);
			redraw();
		}
		
		public void redraw() {

			canvas = new BufferedImage(1024,768,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D)canvas.getGraphics();
			g2.setColor(c);
			
			for(BrushSequence bc : strokes) {
			
				for(Ellipse2D.Float p : bc.points) {
					g2.fill(p);
				}
			
			}
		}
		
		public void removeBrushseq(BrushSequence b) {
			strokes.remove(b);
			
		}
		
		public void addNewPoint(Point2D.Float pt) {
			curBrushSeq.addNewPoint(pt);
			redraw();
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	

