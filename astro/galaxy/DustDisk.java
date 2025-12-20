package astro.galaxy;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
// import java.awt.image.*;
// import java.util.*;
// import java.io.*;
// import java.net.*;
// import java.text.*;
import javax.swing.*;

import myutil.*;

public class DustDisk extends JApplet implements ActionListener, WindowListener,
						ItemListener
{	
	JTextField DT;
	JTextField J0;
	JTextField WARP;
	JTextField N;
	JTextField ORIENT;

	JButton START;
	JButton PAUSEPLAY;
	JButton TRAIL;
	JButton ZOOMIN;
	JButton ZOOMOUT;
	JButton SHOWORIG;
	JButton ACCRETE;
	JComboBox SELECTION;
	JComboBox INTEGRATOR;
	JComboBox STEPLENGTH;

	JPanel cp1, cp2, cp3;
	JPanel tp;
	JPanel bp;
	DustDiskCanvas cc;

	double dt, dt_var=0.01, dt_fix;
	int j0;
	int warp, n;
	int integrator;
	double orient;
	int steplength;
	double zoom = 16;
	boolean showorig = false;
	boolean accrete = false;
	int randwalk;
	final int ANIMATE=0;
	final int TRAPNEWT=1;
	final int MIDPOINT=2;
	final int RUNGEKUTTA=3;
	final int HERMITE=4;
	final int EULER=5;
	final int GASMODEL=6;
	final int FIXED=0;
	final int VARIABLE=1;
	final int FACEON=0;
	final int EDGEON=1;

        public static void main(String[] args) {
                new AppletFrame(new DustDisk(), 1000, 1000);
        }

	public void init()
	{	
	    int j;

	    setFont(new Font("Courier", Font.BOLD, 12));
	    setBackground(Color.white);

	    DT = new JTextField(6);
	    J0 = new JTextField(6);
	    WARP = new JTextField(6);
	    N = new JTextField(6);
	    ORIENT = new JTextField(6);
	    DT.setText("0.01");
	    J0.setText("5");
	    WARP.setText("50");
	    N.setText("2000");
	    ORIENT.setText("0");

	    START   = new JButton("Start");
	    PAUSEPLAY = new JButton("Pause");
	    TRAIL = new JButton("Show Trails");
	    ZOOMIN = new JButton("+");
	    ZOOMOUT = new JButton("-");
	    SHOWORIG = new JButton("Orig");
	    ACCRETE = new JButton("Accrete");
	    SELECTION = new JComboBox();
	    //SELECTION.addItem("--Select One--");
	    /*
	    SELECTION.addItem("LagrangeN");
	    SELECTION.addItem("FigureEight3");
	    SELECTION.addItem("Gaussian");
	    SELECTION.addItem("Random");
	    */
	    SELECTION.addItem("Dusty Solar System");
	    SELECTION.addItem("Dust Disk Face-On");

	    INTEGRATOR = new JComboBox();
	    INTEGRATOR.addItem("Midpoint (Leapfrog)");
	    INTEGRATOR.addItem("Hermite");
	    /*
	    INTEGRATOR.addItem("Hermite");
	    INTEGRATOR.addItem("Gas Model");
	    INTEGRATOR.addItem("Euler");
	    INTEGRATOR.addItem("RungeKutta4");
	    INTEGRATOR.addItem("TrapNewt");
	    INTEGRATOR.addItem("Animate Only");
	    */

	    STEPLENGTH = new JComboBox();
	    STEPLENGTH.addItem("Fixed");
	    STEPLENGTH.addItem("Variable");

	    START.addActionListener( this );
	    PAUSEPLAY.addActionListener( this );
	    TRAIL.addActionListener( this );
	    DT.addActionListener( this );
	    J0.addActionListener( this );
	    WARP.addActionListener( this );
	    N.addActionListener( this );
	    ZOOMIN.addActionListener( this );
	    ZOOMOUT.addActionListener( this );
	    SHOWORIG.addActionListener( this );
	    ACCRETE.addActionListener( this );
	    SELECTION.addItemListener( this );
	    INTEGRATOR.addItemListener( this );
	    STEPLENGTH.addItemListener( this );
	    ORIENT.addActionListener( this );

	    START.setToolTipText("Start the simulation");
	    PAUSEPLAY.setToolTipText("Pause/Play the simulation");
	    TRAIL.setToolTipText("Show/Hide historical trails");
	    ZOOMIN.setToolTipText("Zoom in");
	    ZOOMOUT.setToolTipText("Zoom out");
	    SHOWORIG.setToolTipText("Show original trajectories");
	    ACCRETE.setToolTipText("Accrete small bodies onto large ones");
	    DT.setToolTipText("Integrator's time step.  It's dangerous to make this too large.");
	    WARP.setToolTipText("Number of time steps between screen redraws.  Big numbers speed up simulation without degrading precision.");
	    N.setToolTipText("Number of bodies to simulate.  Bodies are numbered 0..n-1.");
	    J0.setToolTipText("Specifies body to center.  Body 0 is always held to the x-axis.");
	    ORIENT.setToolTipText("Specifies orientation.");

	    cp1 = new JPanel();
	    cp1.setLayout(new BoxLayout(cp1, BoxLayout.X_AXIS));
	    cp1.add(new JLabel("Instance:"));
	    cp1.add(SELECTION);
	    cp2 = new JPanel();
	    cp2.add(new JLabel("Integrator:"));
	    cp2.add(INTEGRATOR);
	    cp2.add(Box.createRigidArea(new Dimension(5,5)));
	    cp2.add(new JLabel("Steplength:"));
	    cp2.add(STEPLENGTH);

	    cp3 = new JPanel();
	    cp3.add(Box.createRigidArea(new Dimension(5,5)));
	    cp3.add(new JLabel("Orientation:"));
	    cp3.add(ORIENT);
	    cp3.add(new JLabel("degrees"));

	    tp = new JPanel();
	    tp.setLayout(new BoxLayout(tp, BoxLayout.X_AXIS));
	    tp.add(Box.createRigidArea(new Dimension(5,5)));
	    tp.add(new JLabel("dt="));
	    tp.add(DT);
	    tp.add(Box.createRigidArea(new Dimension(5,5)));
	    tp.add(new JLabel("warp="));
	    tp.add(WARP);
	    tp.add(Box.createRigidArea(new Dimension(5,5)));
	    tp.add(new JLabel("n="));
	    tp.add(N);
	    tp.add(Box.createRigidArea(new Dimension(5,5)));
	    tp.add(new JLabel("center on="));
	    tp.add(J0);

	    bp = new JPanel();
	    bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
	    bp.add(START);
	    bp.add(PAUSEPLAY);
	    bp.add(TRAIL);
	    bp.add(ZOOMIN);
	    bp.add(ZOOMOUT);
	    bp.add(SHOWORIG);
	    bp.add(ACCRETE);

	    cc = new DustDiskCanvas();
	    // cc.addWindowListener(this);
	    cc.setSize(500,500);
	    cc.setVisible(true);

	    JPanel cr = new JPanel();
	    cr.setLayout(new BoxLayout(cr, BoxLayout.Y_AXIS));
	    cr.add(cp1);
	    cr.add(cp2);
	    cr.add(cp3);
	    cr.add(tp);
	    cr.add(bp);
	    cr.add(Box.createRigidArea(new Dimension(50,400)));
	    Container c = getContentPane();
	    c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));
	    c.add(cc);
	    c.add(cr);

	    /*
	    Container c = getContentPane();
	    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
	    c.add(cc);
	    c.add(cp1);
	    c.add(cp2);
	    //c.add(Box.createRigidArea(new Dimension(50,50)));
	    c.add(tp);
	    c.add(bp);
	    // add(cc);
	    // cc.show();
	    */
	}

	public void itemStateChanged(ItemEvent e) {
	  if (e.getItem().equals("Dust Disk Face-On")) 
	  	{dt=dt_fix=0.01; j0=1; warp=50; n=1000; randwalk=0; N.setEnabled(true);
			orient=70;
	        STEPLENGTH.setSelectedItem("Fixed");
		} else 
	  if (e.getItem().equals("Dusty Solar System")) 
	  	{dt=dt_fix=0.01; j0=5; warp=50; n=2000; randwalk=0; N.setEnabled(true);
			orient=0;
	        STEPLENGTH.setSelectedItem("Fixed");
		} else 
	  {}
	  DT.setText(""+dt);
	  WARP.setText(""+warp); N.setText(""+n); J0.setText(""+j0);
	  ORIENT.setText(""+orient); 
	}

        public void actionPerformed(ActionEvent e)
        {
	    int j;
	    double mv;
	    double r=0, th=0, v=0;
	    // double v_escape=0;
	    double G;

	    // G=1.177e-4;
	    G=1.183804e-4;

	    n = SL.atoi(N.getText());
	    int n7 = (n<7)?7:n;

	    double m[];
	    double x[];
	    double y[];
	    double z[];
	    double vx[];
	    double vy[];
	    double vz[];
	    double rad[];
	    double J2[];
	    Color  col[];
	    String name[];
	    double startDate;
	    double pathx[][] = null;
	    double pathy[][] = null;
	    double pathz[][] = null;
	    double rad0[] = null;
	    double[][] as = null;
	    double[][] ac = null;
	    double[][] bs = null;
	    double[][] bc = null;
	    int i, k, M=0, N=0, K=0;

	    if (INTEGRATOR.getSelectedItem().equals("Euler")) {
		integrator = EULER;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("Midpoint (Leapfrog)")) {
		integrator = MIDPOINT;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("Hermite")) {
		integrator = HERMITE;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("Gas Model")) {
		integrator = GASMODEL;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("RungeKutta")) {
		integrator = RUNGEKUTTA;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("TrapNewt")) {
		integrator = TRAPNEWT;
	    } else
	    if (INTEGRATOR.getSelectedItem().equals("Animate Only")) {
		integrator = ANIMATE;
	    } 

	    /*
	    if (STEPLENGTH.getSelectedItem().equals("Fixed")) {
		steplength = FIXED;
		DT.setText(""+dt_fix); 
	    } else
	    if (STEPLENGTH.getSelectedItem().equals("Variable")) {
		steplength = VARIABLE;
		DT.setText(""+dt_var); 
	    } 
	    */

	    if (e.getSource() == START) {
		if (SELECTION.getSelectedItem().equals(
			    "--Select One--")) {
		  return;
		} else 
		pathx = null; pathy = null; pathz = null;
		Data3d.init(SELECTION.getSelectedItem().toString(), n, G);
		M=Data3d.M;
		N=Data3d.N;
		K=Data3d.K;
		m=Data3d.m;
		rad=Data3d.rad;
		J2=Data3d.J2;
		col=Data3d.col;
		name=Data3d.OrbName;
		startDate=Data3d.startDate;
		x=Data3d.x;
		y=Data3d.y;
		z=Data3d.z;
		vx=Data3d.vx;
		vy=Data3d.vy;
		vz=Data3d.vz;
		pathx = Data3d.pathx;
		pathy = Data3d.pathy;
		pathz = Data3d.pathz;
		rad0=Data3d.rad0;

		dt = SL.atof(DT.getText());
		j0 = SL.atoi(J0.getText());
		warp = SL.atoi(WARP.getText());
		orient = SL.atof(ORIENT.getText());

                if (randwalk == 3) {
		      th = 2*Math.PI*Math.random();
		      double phi = 2*Math.PI*Math.random();
		      double dv = 0.1*Math.sqrt(vx[1]*vx[1]+vy[1]*vy[1]);
		      System.out.print("th = "+th);
		      System.out.print(", phi = "+phi);
		      System.out.println(", dv = "+dv);

		      // vx[1] += dv*Math.cos(th)*Math.cos(phi);
		      // vy[1] += dv*Math.sin(th)*Math.cos(phi);
		      // vz[1] += dv*Math.sin(phi);

		      vx[1] += 0.0126;
		      // vy[1] += 0.00262;
		      // vz[1] += -0.3889;
		      vx[2] += -0.0120;
		      // vy[2] += 0.00041;
		      // vz[2] += -0.1038;
	        }

		M=0; for (j=0; j<n; j++) { M += m[j]; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		      for (j=0; j<n; j++) { vx[j] -= mv/M; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		      for (j=0; j<n; j++) { vy[j] -= mv/M; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		      for (j=0; j<n; j++) { vz[j] -= mv/M; }

		System.out.print("x[1] = "+x[1]);
		System.out.print(", y[1] = "+y[1]);
		System.out.print(", z[1] = "+z[1]);
		System.out.print(", vx[1] = "+vx[1]);
		System.out.print(", vy[1] = "+vy[1]);
		System.out.print(", vz[1] = "+vz[1]);
		System.out.print(", x[2] = "+x[2]);
		System.out.print(", y[2] = "+y[2]);
		System.out.print(", z[2] = "+z[2]);
		System.out.print(", vx[2] = "+vx[2]);
		System.out.print(", vy[2] = "+vy[2]);
		System.out.print(", vz[2] = "+vz[2]);
		System.out.print("\n");
		cc.setParams( n,m,x,y,z,vx,vy,vz,rad,J2,col,name,startDate,
			      pathx,pathy,pathz,
			      G,rad0,dt,j0,warp,zoom,integrator,steplength,
			      showorig, accrete, randwalk, orient);
		cc.timer.start();
		System.out.println("zoom = "+zoom);
	    } else if (e.getSource() == PAUSEPLAY) {
		if (PAUSEPLAY.getText().equals("Pause")) {
		    PAUSEPLAY.setText("Play");
		    PAUSEPLAY.setToolTipText("Restart the simulation");
		    cc.timer.stop();
		} else
		if (PAUSEPLAY.getText().equals("Play")) {
		    PAUSEPLAY.setText("Pause");
		    PAUSEPLAY.setToolTipText("Pause the simulation");
		    // cc.timer.setDelay(dt);
		    cc.timer.start();
		} 
	    } else if (e.getSource() == TRAIL) {
		if (TRAIL.getText().equals("Show Trails")) {
		    TRAIL.setText("Hide Trails");
		    TRAIL.setToolTipText("Show historical trails");
		    cc.setTrails(true);
		} else
		if (TRAIL.getText().equals("Hide Trails")) {
		    TRAIL.setText("Show Trails");
		    TRAIL.setToolTipText("Hide historical trails");
		    cc.setTrails(false);
		} 
	    } else if (e.getSource() == SHOWORIG) {
		if (SHOWORIG.getText().equals("Orig")) {
		    SHOWORIG.setText("Current");
		    SHOWORIG.setToolTipText("Show current position");
		    showorig = true;
		} else
		if (SHOWORIG.getText().equals("Current")) {
		    SHOWORIG.setText("Orig");
		    SHOWORIG.setToolTipText("Show original position");
		    showorig = false;
		} 
		cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			integrator,steplength, orient);
	    } else if (e.getSource() == ACCRETE) {
		if (ACCRETE.getText().equals("Accrete")) {
		    ACCRETE.setText("Don't Accrete");
		    ACCRETE.setToolTipText("Stop accretion");
		    accrete = true;
		} else
		if (ACCRETE.getText().equals("Don't Accrete")) {
		    ACCRETE.setText("Accrete");
		    ACCRETE.setToolTipText("Accrete small bodies onto large ones");
		    accrete = false;
		} 
		cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			integrator,steplength, orient);
	    } else if (e.getSource() == ZOOMIN) {
		zoom /= 2;
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    } else if (e.getSource() == ZOOMOUT) {
		zoom *= 2;
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    } else if (e.getSource() == DT) {
		dt = SL.atof(DT.getText());
		System.out.println("dt = "+dt);
		if (steplength==FIXED) {dt_fix = dt;}
		                  else {dt_var = dt;}
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    } else if (e.getSource() == J0) {
		j0 = SL.atoi(J0.getText());
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    } else if (e.getSource() == WARP) {
		warp = SL.atoi(WARP.getText());
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    } else if (e.getSource() == ORIENT) {
		orient = SL.atof(ORIENT.getText());
		if (cc != null) {
		    cc.setAnimEffects(dt,j0,warp,zoom,showorig,accrete,
			    integrator,steplength, orient);
		    cc.timer.start();
		}
	    }
	    cc.setAnimEffects(dt,j0,warp,zoom,showorig, accrete, integrator,steplength,
			    orient);
        }

	public void stop()
	{
	    if (cc != null) if (cc.timer != null) cc.timer.stop();
	}

	public void windowClosing(WindowEvent e)
	{
	    cc.timer.stop();
	    cc.setVisible(false);
	}
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { cc.timer.stop(); }
	public void windowDeiconified(WindowEvent e) { cc.timer.start(); }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
}
