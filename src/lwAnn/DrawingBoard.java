package lwAnn;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DrawingBoard extends JFrame implements KeyListener {
	
	 public SceneDataModel curSceneData;
	 public LabelTools labelTools;
	 public PaintSurface paintSurface;
	 public static float BRUSH_SIZE = 24;
	 List<File> files;
	 int cur_file_idx = 0;
	 
	  public DrawingBoard() {
		    this.setSize(1220, 816);
		    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    paintSurface = new PaintSurface(this);
		    this.add(paintSurface, BorderLayout.CENTER);
		    labelTools = new LabelTools(this);
		    this.add(labelTools,BorderLayout.EAST);
		    this.setVisible(true);
		    this.addKeyListener(this);
		    this.setFocusable(true);
		    this.setFocusTraversalKeysEnabled(false);
		   // curSceneData = new SceneDataModel();
		    
		    JMenuBar menuBar = new JMenuBar();
		    JMenu fileMenu = new JMenu("File");
		    JMenuItem loadButton = new JMenuItem("Load Directory");
		    
		    loadButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.out.println("LOAD PRESSED");
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					 int returnVal = chooser.showOpenDialog(getParent());
					    if(returnVal == JFileChooser.APPROVE_OPTION) {
					       System.out.println("Opening: " +
					            chooser.getSelectedFile().getAbsolutePath());
					       files = scanDirectory(chooser.getSelectedFile().getAbsolutePath());
					       cur_file_idx = 0;
					       paintSurface.attachImage(files.get(cur_file_idx));
					       
					       curSceneData = loadSavedData(files.get(cur_file_idx));

				    	   labelTools.loadSceneData(curSceneData);
				    	   saveDataModel();
					    }
				}
			});
		    
		    fileMenu.add(loadButton);
		    menuBar.add(fileMenu);
		    
		    this.setJMenuBar(menuBar);
	
			}
	  
	  public void saveDataModel() {
	 
			try {
			 FileOutputStream fileOut;
			 fileOut = new FileOutputStream(new File(curSceneData.filename));
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(curSceneData);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in "+curSceneData.filename+"/lwann_data.dat");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	  }
	  
	  public SceneDataModel loadSavedData(File f) {
		  SceneDataModel datamodel = null;
	       boolean data_exists = new File(f.getParent()+"/lwann_data.dat").exists();
	       if(data_exists){
	    	   try {
	    	   System.out.println("DATA FOUND ");
	           FileInputStream fileIn = new FileInputStream(f.getParent()+"/lwann_data.dat");
	           ObjectInputStream in = new ObjectInputStream(fileIn);
	           datamodel = (SceneDataModel)in.readObject();
	           in.close();
	           fileIn.close();
	           return datamodel;
	    	   } catch (Exception e) {
	    		   System.out.println(e);
	    	   }
	       } else {
	    	   System.out.println("DATA NOT FOUND, CREATING");
	    	   datamodel = new SceneDataModel(f.getParent()+"/lwann_data.dat");
			   // now serialise it immmediately
	    	   
	       }
	       return datamodel;
	  }
	  
	  public List<File> scanDirectory(String target){
	       File directory = new File(target);

	        List<File> resultList = new ArrayList<File>();

	        // get all the files from a directory
	        File[] fList = directory.listFiles();
	        //resultList.addAll(Arrays.asList(fList));
	        
	        for (File file : fList) {
	            if (file.isFile()) {
	                if(file.getName().contains("jpg")) {

		               // System.out.println(file.getAbsolutePath());
	                	resultList.add(file);
	                }
	            
	            } else if (file.isDirectory()) {
	                resultList.addAll(scanDirectory(file.getAbsolutePath()));
	            }
	        }
	        //System.out.println(fList);
	        return resultList;
	  	}
	  
	  private class LabelTools extends JPanel {
		  JList list;
		  DefaultListModel<LabelRegister> listModel;
		  DrawingBoard board;
		  
		  private LabelTools(final DrawingBoard db) {
			  String[] data = { ""};
			  listModel = new DefaultListModel<LabelRegister>();
			  JScrollPane scrollPane = new JScrollPane();
			  this.addKeyListener(db);
			  board = db;
			   this.setFocusable(true);
			  list = new JList(listModel);
			  MouseListener mouseListener = new MouseAdapter() {
				    public void mouseClicked(MouseEvent e) {
				        if (e.getClickCount() == 1) {


				           String selectedItem = (String) list.getSelectedValue().toString();
				           System.out.println("CLICKED: " + selectedItem);
				           db.paintSurface.attachLabel((LabelRegister)list.getSelectedValue());
				         }
				    }
				};
				list.addMouseListener(mouseListener);
				

			list.setFocusable(false);
				
				
			  scrollPane.setViewportView(list);
			  JLabel title = new JLabel("Labels");
			  JButton add = new JButton("Add Label");
			  add.setSize(64,64);
			  JButton del = new JButton("Delete Label");
			  del.setSize(64,64);
			  list.setFixedCellWidth(196);
			
			  add.setFocusable(false);
			  del.setFocusable(false);
			  
			  del.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.out.println("DELETE PRESSED ON " + list.getSelectedValue());
					if(list.getSelectedValue() != null) {
						db.curSceneData.deleteLabel(list.getSelectedIndex());
						listModel.remove(list.getSelectedIndex());
						board.paintSurface.repaint();
						board.paintSurface.redo.clear();
						board.paintSurface.strokes.clear();
						saveDataModel();
					}
				}
			});
			  
			  add.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						System.out.println("ADD PRESSED");
						if(curSceneData == null) {
				        	  return;
				          }
						String response = JOptionPane.showInputDialog(null,
								 "Enter a new label",
								 "",
								 JOptionPane.QUESTION_MESSAGE);
						if(response != null && !response.isEmpty()) {
		
							System.out.println("ENTERED LABEL: " + response);
							curSceneData.addLabel(response);
							loadSceneData(curSceneData);
							saveDataModel();
						} else{
							System.out.println("empty");
						}
					}
				});
			  
			  
			  
			  
			  
			  
			  this.setLayout(new GridLayout(4,1));
			  this.add(scrollPane);
			  this.add(add);
			  this.add(del);
			  
			  
			  
			  
		  }
		  
		  public void loadSceneData(SceneDataModel sc) {
			  listModel.clear();
			  board.paintSurface.currentLabel = null;
			  for(LabelRegister tp : sc.labels) {
				  listModel.addElement(tp);
			  }
		  }
		  
	  }
	  
	  private class PaintSurface extends JPanel {
		    Stack<BrushSequence> strokes = new Stack<BrushSequence>();
		    Stack<BrushSequence> redo = new Stack<BrushSequence>();
		   public boolean hide_labels = false;
		   public boolean fade_labels = false;
		

		    Point startDrag, endDrag;
		    float brush_size = DrawingBoard.BRUSH_SIZE;
		    Color brush_color = Color.BLACK;
		    
		    BufferedImage currentImage;
		    BufferedImage canvas;
		    DrawingBoard board;
		    LabelRegister currentLabel;
		    Point2D.Float mouse_pos;
		    boolean eraser_on = false;

		    public PaintSurface(final DrawingBoard board) {
		    	this.setBackground(Color.white);
		    	canvas = new BufferedImage(1024,768,BufferedImage.TYPE_INT_ARGB);
		    	this.board = board;
		    	   this.addKeyListener(board);
		    	   this.setFocusable(true);
		    	   mouse_pos = new Point2D.Float(0, 0);
		      this.addMouseListener(new MouseAdapter() {
		        public void mousePressed(MouseEvent e) {
		        	if(e.getButton() == 3) {
		        		eraser_on = true;
		        	} else {
		        	if(curSceneData == null || currentLabel == null) {
			        	  return;
			          }
		        	eraser_on = false;
		        System.out.println("Mouse pressed");
		        
		          //startDrag = new Point(e.getX(), e.getY());
		          endDrag = startDrag;
		          strokes.push(new BrushSequence(brush_size, brush_color));
				  BrushSequence newseq = new BrushSequence(DrawingBoard.BRUSH_SIZE, currentLabel.c);		  
		          Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
				  newseq.addNewPoint(p);
				  board.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				  
	    		  strokes.push(newseq);
	    		  currentLabel.beginNewSequence(newseq);
		          repaint();
		        	}
		        }

		        public void mouseReleased(MouseEvent e) {
		          System.out.println("Mouse released");
		          if(curSceneData == null || currentLabel == null) {
		        	  return;
		          }
		          board.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		          saveDataModel();
		          repaint();
		        }
		        
		      });
		      
		      this.addMouseMotionListener(new MouseAdapter() {
		    	  
		    	  public void mouseMoved(MouseEvent e) {
		    		  mouse_pos.x = e.getX();
		    		  mouse_pos.y = e.getY();
		    	  }
		    	  
		    	  public void mouseDragged(MouseEvent e) {
		    		 // System.out.println("Dragging");
		    		  if(curSceneData == null || currentLabel == null) {
			        	  return;
			          }
		    		  
		    		  if(eraser_on == false) {
			    		  Point2D.Float p = new Point2D.Float(e.getX(), e.getY());
			    		  strokes.peek().addNewPoint(p);
			    		  currentLabel.addNewPoint(p);
		    		  } else {
		    		  Ellipse2D.Float mp = new Ellipse2D.Float(e.getX()-12,e.getY()-12, 24, 24); 
		    		  for(LabelRegister r : curSceneData.labels) {
		    			  
		    			  for(BrushSequence b : r.strokes){
		    				  ArrayList<Ellipse2D.Float> rem = new ArrayList<Ellipse2D.Float>();
		    				  for(Ellipse2D.Float el : b.points) {
		    					  
	    						   Area areaA = new Area(el);
	    						   areaA.intersect(new Area(mp));
	    						   if(!areaA.isEmpty()) {
	    							   System.out.println("collision");
	    							   rem.add(el);
	    						   }
		    				  }
		    				  b.points.removeAll(rem);
		    				  r.redraw();
		    			  }
		    		  }
		    		  }
		    		  repaint();
		    		  
		    	  }
		    	  
			});
		      
		      
		      ActionListener taskPerformer = new ActionListener() {
		    	  public void actionPerformed(ActionEvent evt) {
		    	    board.paintSurface.repaint();
		    	  }
		    	};

		    	new Timer(33, taskPerformer).start();
	
		      
		     // this.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ),"enter" );


		    }
		    
		    public void enter() {
		    	System.out.println("ENTER");
		    }
		    
		    public void redo() {
		    	System.out.println("Redoing");
		    	if(redo.empty()){
		    		return;
		    	}
		    	strokes.push(redo.peek());
		    	redo.pop();
		    	repaint();
		    }
		    
		    public void writeImage() {
		    	try {
		    	
		    	BufferedImage wbg = new BufferedImage(1024,768,BufferedImage.TYPE_INT_ARGB);

		    	Graphics2D cg = wbg.createGraphics();
		    	cg.setPaint(new Color(255,255,255));
		    	cg.fillRect(0, 0, 1024, 768);
		    	cg.drawImage(canvas,0,0,null);
		    	
		    	System.out.println("WRITING FILE");
		    	File outputfile = new File("image.png");
		    	ImageIO.write(wbg, "png", outputfile);
		    	System.out.println("DONE");
		    	} catch (Exception e) {
		    		 System.out.println(e);
		    	 }
		    	
		    	}
		    
		    public void attachLabel(LabelRegister l) {
		    	System.out.println("NEW LABEL ATTACHED");
		    	currentLabel = l;
		    }
		    
		    public void attachImage(File f) {
		    	try {
		    		currentImage = ImageIO.read(f);
		    		BufferedImage resizedImg = new BufferedImage(1024, 768, BufferedImage.TRANSLUCENT);
	    		    Graphics2D g2 = resizedImg.createGraphics();
	    		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    		    g2.drawImage(currentImage, 0, 0, 1024, 768, null);
	    		    g2.dispose();
	    		    currentImage = resizedImg;
	    		    
		    		repaint();
		    	} catch (Exception e) {
		    		System.out.println("COULDN'T LOAD IMAGE FILE " + f.getAbsolutePath() + " " + f.getName());
		    		System.out.println(e);
		    	}
		    	}
		    
		    public void undo() {
		    	System.out.println("Undoing");
		    	if(strokes.empty()){
		    		System.out.println("NOTHING TO UNDO");
		    		return;
		    	}
		    	redo.push(strokes.peek());
		    	BrushSequence top = (BrushSequence)strokes.pop();
		    	LabelRegister target = null;
		    	System.out.println("TAR: " + top.uuid);
		    	for(LabelRegister lk : board.curSceneData.labels) {
		    		for(BrushSequence bs : lk.strokes) {
		    			System.out.println(bs.uuid);
		    			if(bs.uuid.equals(top.uuid)) {
		    				System.out.println("FOUND IT: ");
		    				target = lk;
		    				top = bs;
		    			}
		    		}
		    	}
		    	if(target != null) {
		    		boolean s = target.strokes.remove(top);
		    		target.redraw();
		    		if(s) {
		    			System.out.println("REMOVED");
		    		} else {
		    			System.out.println("FAILED TO REMOVE");
		    		}
		    	} else {
		    		System.out.println("COULDN'T FIND TARGET");
		    	}
		    	strokes.pop();
		    	saveDataModel();
		    	repaint();
		    }
		    
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        if(board.curSceneData == null) {
		        	return;
		        }
		        Graphics2D g2 = (Graphics2D)g;
		        g2.clearRect(0,0, 1024, 768);
		        if(currentImage != null) {
		        	g2.drawImage(currentImage,null,0,0);
		        }
		        
		        
		       // g2.setColor(Color.black);
		        
		        if(hide_labels == false) {
		        	if(fade_labels == true) {
		        		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		        	}
		        for(LabelRegister lr : board.curSceneData.labels){
		   
		        	g2.drawImage(lr.canvas, null, 0, 0);
		        }
		        }
		        
			    g2.dispose();
		        
		    }
		  
		    
		    
		    
	  }

	 boolean key_down = false;
	@Override
	public void keyPressed(KeyEvent evt) {
    	  System.out.println("Key pressed");
          if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Z) {
        	  paintSurface.undo();
        	  key_down = true;
        	  return;
          }
          if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_Y) {
        	  paintSurface.redo();
        	  key_down = true;
        	  return;
          }
          if (evt.getKeyCode() == KeyEvent.VK_H) {
        	  System.out.println("H PRESSED");
        	  paintSurface.hide_labels = !paintSurface.hide_labels;
        	  paintSurface.fade_labels = false;
        	  key_down = true;
        	  repaint();
        	  return;
          }

          if (evt.getKeyCode() == KeyEvent.VK_J) {
        	  paintSurface.hide_labels = false;
        	  paintSurface.fade_labels = !paintSurface.fade_labels ;
        	  key_down = true;
        	  repaint();
        	  return;
          }
          if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
        	  cur_file_idx--;
        	  if(cur_file_idx < 0) {
        		  cur_file_idx = 0;
        	  }
        	  curSceneData = loadSavedData(files.get(cur_file_idx));

        	  labelTools.loadSceneData(curSceneData);
		    	   
			  paintSurface.attachImage(files.get(cur_file_idx));
        	  repaint();
        	  saveDataModel();
        	  return;
          }   

          if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
        	  cur_file_idx++;
        	  if(cur_file_idx > files.size()-1) {
        		  cur_file_idx = files.size()-1;
        	  }
         	  curSceneData = loadSavedData(files.get(cur_file_idx));

        	  labelTools.loadSceneData(curSceneData);
			  paintSurface.attachImage(files.get(cur_file_idx));
        	  repaint();
        	  saveDataModel();
        	  return;
          }   
          
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}
