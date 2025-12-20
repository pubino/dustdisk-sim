package astro.galaxy;

import java.awt.*;
import java.io.*;
import java.net.*;

import myutil.*;

public class Data3d
{	
	public static double m[];
	public static double x[];
	public static double y[];
	public static double z[];
	public static double vx[];
	public static double vy[];
	public static double vz[];
	public static double rad[];
	public static double J2[];
	public static Color  col[];
	public static String  OrbName[];
	public static int M=0, N=0, K=0;
	public static double pathx[][], pathy[][], pathz[][];
	public static double rad0[];
	public static double startDate;

        static public void init(String name, int n, double G, double massratio,
				double velratio)
        {
	    int i,j,k;
	    double r=0, th=0, v=0;
	    double v_escape=0;
	    double r0=0.1;
	    double mv;
	    int n7;

	    n7 = (n<20)?20:n;
	    if (name.equals("Lattice")) { n7 = (2*n)*(2*n)*(2*n); }
	    if (name.equals("Lattice Positive Masses")) { n7 = (2*n)*(2*n)*(2*n); }
	    if (name.equals("Lattice2D")) { n7 = (2*n)*(2*n); }

	    m = new double[n7];
	    x = new double[n7];
	    y = new double[n7];
	    z = new double[n7];
	    vx = new double[n7];
	    vy = new double[n7];
	    vz = new double[n7];
	    rad = new double[n7];
	    J2 = new double[n7]; for (j=0; j<n7; j++) J2[j] = 0;
	    rad0 = new double[n7];
	    col = new Color[n7];
	    OrbName = new String[n7];
	    startDate = 2000;

	    for (j=0; j<n7; j++) { OrbName[j] = ""; }

	    int jj;
	    pathx = null; pathy = null; pathz = null;
	    if (name.equals("StarPlanetDust")) {
	      int levels = 1;
	      for (j=0; j<n/levels; j++) {
	       for (k=0; k<levels; k++) {
		r = 5.203*(1+(k-10)/20.0);  // jupiter radius in au
		th = (2*Math.PI*j)/(n/levels);
		v = 2.76;// jupiter velocity in au/year
		v *= velratio/Math.sqrt(r/5.203);
		jj = 3 + levels*j + k;
		if (jj>=n) break;
		x[jj] = r*Math.cos(th);
		y[jj] = r*Math.sin(th);
		z[jj] = 0.0001*Math.random();
		vx[jj] = -v*Math.sin(th);
		vy[jj] = v*Math.cos(th);
		vz[jj] = 0;
	       }
	      }
	      for (j=0; j<n/levels; j++) {
	       for (k=0; k<levels; k++) {
		jj = 3 + levels*j + k;
		if (jj>=n) break;
		m[jj] = 0;
		rad[jj] = 0;
		rad0[jj] = 0;
		col[jj] = new Color((float)0.3,(float)0.3,(float)0.3);
		th = 360*j/((double) (n/(levels+0.0)));
		if (th < 60) {
		    col[jj] = new Color((float)1,(float)(th/60),(float)(0));
		} else if (th < 120) {
		    col[jj] = new Color((float)(1-(th-60)/60),(float)1,(float)(0));
		} else if (th < 180) {
		    col[jj] = new Color((float)(0),(float)1,(float)((th-120)/60));
		} else if (th < 240) {
		    col[jj] = new Color((float)(0),(float)(1-(th-180)/60),(float)(1));
		} else if (th < 300) {
		    col[jj] = new Color((float)((th-240)/60),(float)(0),(float)(1));
		} else {
		    col[jj] = new Color((float)(1), (float)(0), (float)(1-(th-300)/60));
		}
	       }
	      }
	      x[0] = 0; y[0] = 0; z[0] = 0; 
	      vx[0] = 0; vy[0] = 0; vz[0] = 0;
	      col[0] = new Color((float)1.0,(float)1.0,(float)0.0);
	      col[1] = new Color((float)0.3,(float)1.0,(float)1.0);
	      m[0] = 333000;
	      rad[0] = 0.0046;
	      rad0[0] = 0.0;

	      // include jupiter
	      col[1] = new Color((float)0.3,(float)1.0,(float)1.0);
	      m[1] = 318*massratio;
	      rad[1] = 0.0004758;
	      rad0[1] = 0.0004758;
		r = 5.203;  // jupiter radius in au
		v = 2.76;   // jupiter velocity in au/year
		th = 1/((double) n);
		x[1] = r*Math.cos(th*2*Math.PI);
		y[1] = r*Math.sin(th*2*Math.PI);
		vx[1] = -v*Math.sin(th*2*Math.PI);
		vy[1] = v*Math.cos(th*2*Math.PI);

	      // include saturn
	      col[2] = new Color((float)0.3,(float)1.0,(float)1.0);
	      m[2] = 95.2; // saturn
	      rad[2] = 0.0004027;
	      rad0[2] = 0.0004027;
	      J2[2] = 0.0; // J2[2] = 1.6297e-2;
		r = 9.539;  // saturn radius in au
		th = 2/((double) n);
		v = 2.05;   // saturn velocity in au/year
		x[2] = r*Math.cos(th*2*Math.PI);
		y[2] = r*Math.sin(th*2*Math.PI);
		vx[2] = -v*Math.sin(th*2*Math.PI);
		vy[2] = v*Math.cos(th*2*Math.PI);

		/*
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vx[j];
	      }
	      vx[0] = -mv/m[0];
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vy[j];
	      }
	      vy[0] = -mv/m[0];
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vz[j];
	      }
	      vz[0] = -mv/m[0];
	      */
	    } 
	    if (name.equals("StarPlanetDust0")) {
	      x[0] = 0; y[0] = 0; z[0] = 0; 
	      vx[0] = 0; vy[0] = 0; vz[0] = 0;
	      for (j=1; j<n; j++) {
		r = 5.203;  // jupiter radius in au
		th = j/((double) n);
		v = 2.76*velratio;   // jupiter velocity in au/year
		x[j] = r*Math.cos(th*2*Math.PI);
		y[j] = r*Math.sin(th*2*Math.PI);
		z[j] = 0.0001*Math.random();
		vx[j] = -v*Math.sin(th*2*Math.PI);
		vy[j] = v*Math.cos(th*2*Math.PI);
		vz[j] = 0;
	      }
	      for (j=0; j<n; j++) {
		m[j] = 0;
		rad[j] = 0;
		col[j] = new Color((float)0.3,(float)0.3,(float)0.3);
		th = 360*j/((double) n);
		if (th < 60) {
		    col[j] = new Color((float)1,(float)(th/60),(float)(0));
		} else if (th < 120) {
		    col[j] = new Color((float)(1-(th-60)/60),(float)1,(float)(0));
		} else if (th < 180) {
		    col[j] = new Color((float)(0),(float)1,(float)((th-120)/60));
		} else if (th < 240) {
		    col[j] = new Color((float)(0),(float)(1-(th-180)/60),(float)(1));
		} else if (th < 300) {
		    col[j] = new Color((float)((th-240)/60),(float)(0),(float)(1));
		} else {
		    col[j] = new Color((float)(1), (float)(0), (float)(1-(th-300)/60));
		}
		/*
		if (th < 120) {
		    col[j] = new Color((float)(1-th/120),(float)(th/120),(float)(0));
		} else if (th < 240) {
		    col[j] = new Color((float)(0),(float)(1-(th-120)/120),(float)((th-120)/120));
		} else {
		    col[j] = new Color((float)((th-240)/120), (float)(0), (float)(1-(th-240)/120));
		}
		*/
	      }
	      col[0] = new Color((float)1.0,(float)1.0,(float)0.0);
	      col[1] = new Color((float)0.3,(float)1.0,(float)1.0);
	      m[0] = 333000;
	      m[1] = 318*massratio;
	      rad[0] = 0.0046;
	      rad[1] = 0.0004758;

	      // include saturn
	      col[2] = new Color((float)0.3,(float)1.0,(float)1.0);
	      m[2] = 95.2; // saturn
	      rad[2] = 0.0004027;
		r = 9.539;  // saturn radius in au
		th = 2/((double) n);
		v = 2.05;   // saturn velocity in au/year
		x[2] = r*Math.cos(th*2*Math.PI);
		y[2] = r*Math.sin(th*2*Math.PI);
		vx[2] = -v*Math.sin(th*2*Math.PI);
		vy[2] = v*Math.cos(th*2*Math.PI);

	      /*
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vx[j];
	      }
	      vx[0] = -mv/m[0];
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vy[j];
	      }
	      vy[0] = -mv/m[0];
	      mv = 0;
	      for (j=1; j<3; j++) {
		    mv += m[j]*vz[j];
	      }
	      vz[0] = -mv/m[0];
	      */
	    } 
        }

        static public void init(String name, int n, double G)
        {
	    int i,j,k;
	    double r=0, th=0, v=0;
	    double v_escape=0;
	    double r0=0.01;
	    int n7;
	    double mv;

	    n7 = (n<20)?20:n;
	    if (name.equals("Lattice")) { n7 = (2*n)*(2*n)*(2*n); }
	    if (name.equals("Lattice Positive Masses")) { n7 = (2*n)*(2*n)*(2*n); }
	    if (name.equals("Lattice2D")) { n7 = (2*n)*(2*n); }

	    m = new double[n7];
	    x = new double[n7];
	    y = new double[n7];
	    z = new double[n7];
	    vx = new double[n7];
	    vy = new double[n7];
	    vz = new double[n7];
	    rad = new double[n7];
	    rad0 = new double[n7];
	    J2 = new double[n7]; for (j=0; j<n7; j++) {J2[j] = 0.0;}
	    col = new Color[n7];
	    OrbName = new String[n7];

	    for (j=0; j<n; j++) { OrbName[j] = ""; }

	    pathx = null; pathy = null; pathz = null;
		if (name.equals("StarPlanetDust")) {
		  x[0] = 0; y[0] = 0; z[0] = 0; 
		  vx[0] = 0; vy[0] = 0; vz[0] = 0;
		  for (j=1; j<n; j++) {
		    r = 0.1;
		    th = j/((double) n);
		    v_escape = Math.sqrt(3*G/r); 
		    v = 0.5*v_escape;
		    x[j] = r*Math.cos(th*2*Math.PI);
		    y[j] = r*Math.sin(th*2*Math.PI);
		    z[j] = 0.0001*Math.random();
		    vx[j] = -v*Math.sin(th*2*Math.PI);
		    vy[j] = v*Math.cos(th*2*Math.PI);
		    vz[j] = 0;
		  }
	          for (j=0; j<n; j++) {
		    // m[j] = 300000;
		    m[j] = 0;
		    rad[j] = r0;
		    rad0[j] = 0;
		    col[j] = new Color((float)0.3,(float)0.3,(float)0.3);
		  }
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.0);
		  col[1] = new Color((float)0.3,(float)1.0,(float)1.0);
		  m[0] = 1;
		  m[1] = 0.01;
		} else
		if (name.equals("LagrangeN")) {
		  for (j=0; j<n; j++) {
		    r = 0.1;
		    th = (24.0*j)/n;
		    v_escape = Math.sqrt(3*G*(n-1)/r0); 
		    v = 0.05*v_escape;
		    x[j] = r*Math.cos(th*2*Math.PI/24);
		    y[j] = r*Math.sin(th*2*Math.PI/24);
		    z[j] = 0.000000001*Math.random();
		    vx[j] = -v*Math.sin(th*2*Math.PI/24);
		    vy[j] = v*Math.cos(th*2*Math.PI/24);
		    vz[j] = 0;
		  }
	          for (j=0; j<n; j++) {
		    // m[j] = 300000;
		    m[j] = 1;
		    rad[j] = r0;
		    rad0[j] = 0;
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
		  }
		} else
		if (name.equals("Dust Disk Face-On")) {

		  // Sun
		  j=0;
		  col[j] = null;
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  // Jupiter
		  j=1;
		  col[j] = null;
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  r = 5;
		  double mu0 = m[0]/(m[0]+m[1]);
		  double mu1 = m[1]/(m[0]+m[1]);
		  double omega = Math.sqrt(G*m[0]/(mu0*r*r*r));

		  x[0] = -mu1*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = -omega*mu1*r;
		  vz[0] = 0;
		  x[1] = mu0*r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = 0;
		  vy[1] = omega*mu0*r;
		  vz[1] = 0;

		  for (j=2; j<n; j++) {
		    m[j] = 0;
		    rad[j] = 0;
		    rad0[j] = 0;
		    th = 2*Math.PI*j/n;
		    double phi = 2*Math.PI*Math.random();
		    double psi = 2*Math.PI*(Math.random()-0.5)*10./360.;
		    col[j] = null;
		    /*
		    x[j] = mu0*r*Math.cos(th)*Math.cos(psi)*Math.cos(phi);
		    y[j] = mu0*r*Math.sin(th)*Math.cos(psi)*Math.sin(phi);
		    z[j] = mu0*r*            *Math.sin(psi);
		    vx[j] = omega*mu0*r*Math.cos(th)*Math.cos(psi)*Math.sin(phi);
		    vy[j] = omega*mu0*r*Math.sin(th)*Math.cos(psi)*Math.cos(phi);
		    vz[j] = omega*mu0*r*            *Math.sin(psi);
		    */
		    double cos_th  = Math.cos(th ), sin_th  = Math.sin(th );
		    double cos_phi = Math.cos(phi), sin_phi = Math.sin(phi);
		    double cos_psi = Math.cos(psi), sin_psi = Math.sin(psi);
		    double xt, yt, zt, xt1, yt1, zt1;
		    double r2 = r*(1+(Math.random()-0.5)/2);
		    double omega2 = omega*Math.sqrt(r*r*r/(r2*r2*r2));
		    /*
		    x[j] = mu0*r*(cos_th * cos_psi * cos_phi*cos_phi  
			        + sin_th * cos_psi * cos_phi*sin_phi);
		    y[j] = mu0*r*(cos_th * cos_psi * sin_phi*cos_phi  
			        + sin_th * cos_psi * sin_phi*sin_phi);
		    z[j] = -mu0*r*(cos_th * sin_psi * cos_phi  
			         + sin_th * sin_psi * sin_phi);
		    vx[j] = omega*mu0*r*(-sin_th * cos_psi * cos_phi*cos_phi  
			               +  cos_th * cos_psi * cos_phi*sin_phi);
		    vy[j] = omega*mu0*r*(-sin_th * cos_psi * sin_phi*cos_phi  
			               +  cos_th * cos_psi * sin_phi*sin_phi);
		    vz[j] = -omega*mu0*r*(-sin_th * sin_psi * cos_phi  
		 	                +  cos_th * sin_psi * sin_phi);
					*/
		    x[j]  =  mu0*r2*cos_th;
		    y[j]  =  mu0*r2*sin_th;
		    z[j]  =  0;
		    xt =  cos_phi*x[j] + sin_phi*y[j];
		    yt = -sin_phi*x[j] + cos_phi*y[j];
		    zt =                               z[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    x[j] =  cos_phi*xt1 - sin_phi*yt1;
		    y[j] =  sin_phi*xt1 + cos_phi*yt1;
		    z[j] =                               zt1;
		    vx[j] = -omega2*mu0*r2*sin_th;
		    vy[j] =  omega2*mu0*r2*cos_th;
		    vz[j] =  0;
		    xt =  cos_phi*vx[j] + sin_phi*vy[j];
		    yt = -sin_phi*vx[j] + cos_phi*vy[j];
		    zt =                               vz[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    vx[j] =  cos_phi*xt1 - sin_phi*yt1;
		    vy[j] =  sin_phi*xt1 + cos_phi*yt1;
		    vz[j] =                               zt1;
		  }
		} else
		if (name.equals("Dusty Solar System")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.3022e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.09041, -10.09606,   254.73,    118.50, 
			  	1.33489932319922, -19.0427712, j);

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.64052, -14.99464,   178.22,     58.39, 
			  	1.68468287895148,   3.1649484, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic(280.94399, -23.54146,   106.65,      5.95, 
			  	1.88809615264125, -12.8809197, j);

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphemEcliptic(112.80648,  21.86089,    -7.49,      1.42, 
			  	8.29690048890332,  19.0503238, j);

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphemEcliptic(338.47273,  -9.82498,     7.99,      3.19, 
			  	21.0470705432051,   2.5567636, j);

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphemEcliptic(318.14819, -16.23793,     5.26,      1.60, 
			  	31.0107494216548,  -8.3027453, j);

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphemEcliptic(263.97486, -15.20032,     2.74,      0.26, 
			  	31.2623582636109, -26.6429753, j);


		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 2.2e+14/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  fromEphemEcliptic(129.85464,   0.12027,    -4.78,      1.90, 
			  	28.5415895526607,  12.6144319, j);

		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 1.67e+22/5.972e+24; // ??
		  rad[j] = 1300/149668992.0; // ??
		  rad0[j] = 1300/149668992.0; // ??

		  fromEphemEcliptic(23.79895,  -5.68257,     0.89,      0.75, 
			  	97.5764534858654,  21.4755946, j);

		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 1e+21/5.972e+24; // ??
		  rad[j] = 1400/149668992.0; // ??
		  rad0[j] = 1400/149668992.0; // ??

		  fromEphemEcliptic(48.85224,   5.80750,     0.50,      0.48, 
			  	89.4067339131441,  27.0225597, j);

		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.347e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(120.12896,  25.74265,  1738.94,   -364.81, 
			  0.002710808329734,  -0.0084885, j);

		  r = 5;
		  double mu0 = m[0]/(m[0]+m[1]);
		  double mu1 = m[1]/(m[0]+m[1]);
		  double omega = Math.sqrt(G*m[0]/(mu0*r*r*r));

		  /*
		  x[0] = -mu1*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = -omega*mu1*r;
		  vz[0] = 0;
		  x[1] = mu0*r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = 0;
		  vy[1] = omega*mu0*r;
		  vz[1] = 0;
		  */

		  for (j=14; j<n; j++) {
		    m[j] = 0;
		    rad[j] = 0;
		    rad0[j] = 0;
		    double xrand, yrand, rrand2;
		    while (true) {
			    xrand = 40*(Math.random()-0.5);
			    yrand = 40*(Math.random()-0.5);
			    rrand2 = xrand*xrand + yrand*yrand;
			    if (rrand2 < 20*20 && rrand2 > 0.2*0.2) break;
		    }
		    th = Math.atan2(yrand,xrand);
		    double r2 = Math.sqrt(rrand2);
		    double phi = 2*Math.PI*Math.random();
		    double psi = 2*Math.PI*(Math.random()-0.5)*10./360.;
		    col[j] = null;
		    double cos_th  = Math.cos(th ), sin_th  = Math.sin(th );
		    double cos_phi = Math.cos(phi), sin_phi = Math.sin(phi);
		    double cos_psi = Math.cos(psi), sin_psi = Math.sin(psi);
		    double xt, yt, zt, xt1, yt1, zt1;
		    double omega2 = omega*Math.sqrt(r*r*r/(r2*r2*r2));
		    x[j]  =  mu0*r2*cos_th;
		    y[j]  =  mu0*r2*sin_th;
		    z[j]  =  0;
		    xt =  cos_phi*x[j] + sin_phi*y[j];
		    yt = -sin_phi*x[j] + cos_phi*y[j];
		    zt =                               z[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    x[j] = x[0] + cos_phi*xt1 - sin_phi*yt1;
		    y[j] = y[0] + sin_phi*xt1 + cos_phi*yt1;
		    z[j] = z[0] +                              zt1;
		    vx[j] = -omega2*mu0*r2*sin_th;
		    vy[j] =  omega2*mu0*r2*cos_th;
		    vz[j] =  0;
		    xt =  cos_phi*vx[j] + sin_phi*vy[j];
		    yt = -sin_phi*vx[j] + cos_phi*vy[j];
		    zt =                               vz[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    vx[j] = vx[0] + cos_phi*xt1 - sin_phi*yt1;
		    vy[j] = vy[0] + sin_phi*xt1 + cos_phi*yt1;
		    vz[j] = vz[0] +                              zt1;
		  }
		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Dusty Inner Solar System")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.64052, -14.99464,   178.22,     58.39, 
			  	1.68468287895148,   3.1649484, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic(280.94399, -23.54146,   106.65,      5.95, 
			  	1.88809615264125, -12.8809197, j);

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);


		  // Earth's Moon	
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.347e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(120.12896,  25.74265,  1738.94,   -364.81, 
			  0.002710808329734,  -0.0084885, j);

		  r = 5;
		  double mu0 = m[0]/(m[0]+m[5]);
		  double mu1 = m[5]/(m[0]+m[5]);
		  double omega = Math.sqrt(G*m[0]/(mu0*r*r*r));

		  /*
		  x[0] = -mu1*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = -omega*mu1*r;
		  vz[0] = 0;
		  x[1] = mu0*r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = 0;
		  vy[1] = omega*mu0*r;
		  vz[1] = 0;
		  */

		  for (j=6; j<n; j++) {
		    m[j] = 0;
		    rad[j] = 0;
		    rad0[j] = 0;
		    double xrand, yrand, rrand2;
		    while (true) {
			    xrand = 4*(Math.random()-0.5);
			    yrand = 4*(Math.random()-0.5);
			    rrand2 = xrand*xrand + yrand*yrand;
			    if (rrand2 < 2*2 && rrand2 > 0.6*0.6) break;
		    }
		    th = Math.atan2(yrand,xrand);
		    double r2 = Math.sqrt(rrand2);
		    double phi = 2*Math.PI*Math.random();
		    double psi = 2*Math.PI*(Math.random()-0.5)*10./360.;
		    col[j] = null;
		    double cos_th  = Math.cos(th ), sin_th  = Math.sin(th );
		    double cos_phi = Math.cos(phi), sin_phi = Math.sin(phi);
		    double cos_psi = Math.cos(psi), sin_psi = Math.sin(psi);
		    double xt, yt, zt, xt1, yt1, zt1;
		    double omega2 = omega*Math.sqrt(r*r*r/(r2*r2*r2));
		    x[j]  =  mu0*r2*cos_th;
		    y[j]  =  mu0*r2*sin_th;
		    z[j]  =  0;
		    xt =  cos_phi*x[j] + sin_phi*y[j];
		    yt = -sin_phi*x[j] + cos_phi*y[j];
		    zt =                               z[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    x[j] = x[0] + cos_phi*xt1 - sin_phi*yt1;
		    y[j] = y[0] + sin_phi*xt1 + cos_phi*yt1;
		    z[j] = z[0] +                              zt1;
		    vx[j] = -omega2*mu0*r2*sin_th;
		    vy[j] =  omega2*mu0*r2*cos_th;
		    vz[j] =  0;
		    xt =  cos_phi*vx[j] + sin_phi*vy[j];
		    yt = -sin_phi*vx[j] + cos_phi*vy[j];
		    zt =                               vz[j];
		    xt1 =  cos_psi*xt          + sin_psi*zt;
		    yt1 =                yt;
		    zt1 = -sin_psi*xt          + cos_psi*zt;
		    vx[j] = vx[0] + cos_phi*xt1 - sin_phi*yt1;
		    vy[j] = vy[0] + sin_phi*xt1 + cos_phi*yt1;
		    vz[j] = vz[0] +                              zt1;
		  }
		  /* */
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  /* */
		} else
		if (name.equals("FigureEight3")) {
		    for (j=0; j<n; j++) {m[j] = 1/G; rad[j] = 5*r0; rad0[j] = 0;}
		    v_escape = Math.sqrt(3*G*(n-1)/r0); 
		    v_escape = 1e+10;
		    x[0]= -0.000000; y[0]=  0.000000;
		    x[1]=  0.965502; y[1]= -0.241961;
		    x[2]= -0.965502; y[2]=  0.241961;
		    z[0] = 0.01*Math.random();
		    z[1] = 0.01*Math.random();
		    z[2] = 0.01*Math.random();
		    vx[0]=  0.933232; vy[0]=  0.865461;
		    vx[1]= -0.466616; vy[1]= -0.432730;
		    vx[2]= -0.466616; vy[2]= -0.432730;
		    vz[0] = 0;
		    vz[1] = 0;
		    vz[2] = 0;
		    M=60; N=n; K=12;
		    for (j=0; j<n; j++) {
		      col[j] = new Color((float)(0.5+Math.random()/2), 
				         (float)(0.5+Math.random()/2), 
			                 (float)(0.5+Math.random()/2)) ;
		    }
		} else
		if (name.equals("Random")) {
		  for (j=0; j<n; j++) {
		    r = -0.10*Math.log(Math.random());
		    th = 2*Math.PI*Math.random();
		    v_escape = Math.sqrt(3*G*(n-1)/r0); 
		    v = 0.05*v_escape;
		    /*
		    x[j] = r*Math.cos(th);
		    y[j] = r*Math.sin(th);
		    z[j] = 0.000000001*Math.random();
		    vx[j] = -v*Math.sin(th*2*Math.PI/24);
		    vy[j] = v*Math.cos(th*2*Math.PI/24);
		    vz[j] = 0;
		    */
		    double phi = Math.PI*Math.random();
		    x[j] = r*Math.cos(th)*Math.sin(phi);
		    y[j] = r*Math.sin(th)*Math.sin(phi);
		    z[j] = r*Math.cos(phi);
		    r = -0.10*Math.log(Math.random());
		    th = 2*Math.PI*Math.random();
		    phi = Math.PI*Math.random();
		    vx[j] = r*Math.cos(th)*Math.sin(phi);
		    vy[j] = r*Math.sin(th)*Math.sin(phi);
		    vz[j] = r*Math.cos(phi);
		  }
	          for (j=0; j<n; j++) {
		    // m[j] = 300000;
		    m[j] = Math.exp(3*(Math.random()-0.5));
		    rad[j] = r0;
		    rad0[j] = 0;
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
		  }
		} else
		if (name.equals("Gaussian")) {
		  for (j=0; j<n; j++) {
		    v_escape = Math.sqrt(3*G*(n-1)/r0); 
		    v = 0.01*v_escape;
		    r = Math.sqrt(-2*Math.log(Math.random()));
		    th = 2*Math.PI*Math.random();
		    x[j] = r*Math.cos(th);
		    y[j] = r*Math.sin(th);
		    r = Math.sqrt(-2*Math.log(Math.random()));
		    th = 2*Math.PI*Math.random();
		    z[j] = r*Math.cos(th);
		    vx[j] = v*Math.sin(th);
		    r = Math.sqrt(-2*Math.log(Math.random()));
		    th = 2*Math.PI*Math.random();
		    vy[j] = v*Math.sin(th);
		    vz[j] = v*Math.cos(th);
		  }
	          for (j=0; j<n; j++) {
		    // m[j] = 300000;
		    m[j] = Math.exp(3*(Math.random()-0.5));
		    rad[j] = r0;
		    rad0[j] = 0;
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
		  }
		} else
		if (name.equals("Lattice")) {
		  double xmin=1000, xmax=-1000;
		  N = 2*n;
		  r = 8.0/n;
		  for (int ix=-n; ix<n; ix++) {
		  for (int iy=-n; iy<n; iy++) {
		  for (int iz=-n; iz<n; iz++) {
	 	    j = (n+iz) + N*(n+iy) + N*N*(n+ix);
		    // System.out.println("j = "+j);
		    x[j] = (ix+0.5)*r;
		    y[j] = (iy+0.5)*r;
		    z[j] = (iz+0.5)*r;
		    if (x[j] > xmax) xmax = x[j];
		    if (x[j] < xmin) xmin = x[j];
		    vx[j] = 0;
		    vy[j] = 0;
		    vz[j] = 0;
		    // m[j] = Math.exp(3*(Math.random()-0.5));
		    m[j] = 6*Math.random() -3;
		    m[j] *= 100;
		    rad[j] = r0;
		    rad0[j] = r0;
		    /*
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
				       */
		    if (m[j] > 0) {col[j] = new Color(1.0f, 0.5f, 0.0f);}
		    else          {col[j] = new Color(0.0f, 0.5f, 1.0f);}
		  }
		  }
		  }
		  N = N*N*N;
		  System.out.println("xmin = "+xmin+", xmax = "+xmax);
		} else
		if (name.equals("Lattice Positive Masses")) {
		  double xmin=1000, xmax=-1000;
		  N = 2*n;
		  r = 8.0/n;
		  for (int ix=-n; ix<n; ix++) {
		  for (int iy=-n; iy<n; iy++) {
		  for (int iz=-n; iz<n; iz++) {
	 	    j = (n+iz) + N*(n+iy) + N*N*(n+ix);
		    // System.out.println("j = "+j);
		    x[j] = (ix+0.5)*r;
		    y[j] = (iy+0.5)*r;
		    z[j] = (iz+0.5)*r;
		    if (x[j] > xmax) xmax = x[j];
		    if (x[j] < xmin) xmin = x[j];

		    // vx[j] = 0.0001*(Math.random() - 0.5);
		    // vy[j] = 0.0001*(Math.random() - 0.5);
		    // vz[j] = 0.0001*(Math.random() - 0.5);
		    // m[j] = Math.exp(3*(Math.random()-0.5));
		    // m[j] = 3*Math.random();
		    // m[j] *= 100;
		    double zzz, phi;
		    phi = 2*Math.PI*Math.random();
		    zzz = 2*Math.random() - 1;
		    vx[j] = 0.0001*Math.sqrt(1-zzz*zzz)*Math.cos(phi);
		    vy[j] = 0.0001*Math.sqrt(1-zzz*zzz)*Math.sin(phi);
		    vz[j] = 0.0001*zzz;
		    m[j] = 30;

		    rad[j] = r0;
		    rad0[j] = r0;
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
		    /*
		    if (m[j] > 0) {col[j] = new Color(1.0f, 0.5f, 0.0f);}
		    else          {col[j] = new Color(0.0f, 0.5f, 1.0f);}
		    */
		  }
		  }
		  }
		  N = N*N*N;
		  System.out.println("xmin = "+xmin+", xmax = "+xmax);
		} else
		if (name.equals("Lattice2D")) {
		  double xmin=1000, xmax=-1000;
		  N = 2*n;
		  r = 8.0/n;
		  for (int ix=-n; ix<n; ix++) {
		  for (int iy=-n; iy<n; iy++) {
	 	    j = (n+iy) + N*(n+ix);
		    // System.out.println("j = "+j);
		    x[j] = (ix+0.5)*r;
		    y[j] = (iy+0.5)*r;
		    z[j] = 0;
		    if (x[j] > xmax) xmax = x[j];
		    if (x[j] < xmin) xmin = x[j];
		    vx[j] = 0;
		    vy[j] = 0;
		    vz[j] = 0;
		    // m[j] = Math.exp(3*(Math.random()-0.5));
		    m[j] = 6*Math.random() -3;
		    m[j] *= 100;
		    rad[j] = r0;
		    rad0[j] = r0;
		    /*
		    col[j] = new Color((float)(0.5+Math.random()/2), 
				       (float)(0.5+Math.random()/2), 
			               (float)(0.5+Math.random()/2)) ;
				       */
		    if (m[j] > 0) {col[j] = new Color(1.0f, 0.5f, 0.0f);}
		    else          {col[j] = new Color(0.0f, 0.5f, 1.0f);}
		  }
		  }
		  N = N*N;
		  System.out.println("xmin = "+xmin+", xmax = "+xmax);
		} else
		if (name.equals("Janus and 5 others")) {
		  double e;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Janus
		  for (j=1; j<=6; j++) {
		      col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		      m[j] = 3.366e-7; // *1e3 is stable, *1e4 is barely unstable, *1e5 is unstable
		      // m[j] = m[0]/6000;
		      // r = 0.00101251 * (1+(Math.random()-0.5)/1500);
		      r = 0.00101251;
		      th = j*Math.PI/3;
		      v = Math.sqrt(G*m[0]/r);
    
		      x[j] = r*Math.cos(th);
		      y[j] = r*Math.sin(th);
		      z[j] = 0;
		      vx[j] = -v*Math.sin(th);
		      vy[j] = v*Math.cos(th);
		      vz[j] = 0;
		      rad[j] = 5.9e-7;
		      rad0[j] = 0;
		  }

	      /*
		  mv = 0;
		  for (j=1; j<7; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<7; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<7; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("Janus and 19 others")) {
		  double e;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Janus
		  for (j=1; j<=20; j++) {
		      col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		      m[j] = 3.366e-7;
		      // m[j] = m[0]/6000;
		      r = 0.00101251 * (1+(Math.random()-0.5)/1500);
		      th = 2*j*Math.PI/20;
		      v = Math.sqrt(G*m[0]/r);
    
		      x[j] = r*Math.cos(th);
		      y[j] = r*Math.sin(th);
		      z[j] = 0;
		      /*
		      vx[j] = -v*Math.sin(th) + v*(Math.random()-0.5)/10000;
		      vy[j] = v*Math.cos(th) + v*(Math.random()-0.5)/10000;
		      */
		      vx[j] = -v*Math.sin(th);
		      vy[j] = v*Math.cos(th);
		      vz[j] = 0;
		      rad[j] = 5.9e-7;
		      rad0[j] = 0;
		      rad[j] = rad[0]/30;
		  }

	      /*
		  mv = 0;
		  for (j=1; j<21; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<21; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<21; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("Janus and 99 others")) {
		  double e;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Janus
		  for (j=1; j<=100; j++) {
		      col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		      m[j] = 3.366e-7;
		      r = 0.00101251 * (1+(Math.random()-0.5)/1500);
		      th = 2*j*Math.PI/100;
		      v = Math.sqrt(G*m[0]/r);
    
		      x[j] = r*Math.cos(th);
		      y[j] = r*Math.sin(th);
		      z[j] = 0;
		      /*
		      vx[j] = -v*Math.sin(th) + v*(Math.random()-0.5)/10000;
		      vy[j] = v*Math.cos(th) + v*(Math.random()-0.5)/10000;
		      */
		      vx[j] = -v*Math.sin(th);
		      vy[j] = v*Math.cos(th);
		      vz[j] = 0;
		      rad[j] = 5.9e-7;
		      rad0[j] = 5.9e-7;
		      // rad[j] = rad[0]/50;
		  }

	      /*
		  mv = 0;
		  for (j=1; j<101; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<101; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<101; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("Saturn's rings")) {
		  double e;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Rings
		  k = 1;
		  for (i=-1; i<=1; i++) {
		    for (j=1; j<=20; j++) {
		      col[k] = new Color((float)1.0,(float)((1.0+i)/2.0),
			                            (float)((1.0-i)/2.0));
		      // m[k] = 3.366e-7*Math.exp(-5*(Math.random()-0.5));
		      m[k] = 3.366e-7 * 10;
		      r = 0.00101251*(1+0.2*i) * (1+(Math.random()-0.5)/1500);
		      th = 2*(k + 0.5*i)*Math.PI/20;
		      v = Math.sqrt(G*m[0]/r);
    
		      x[k] = r*Math.cos(th);
		      y[k] = r*Math.sin(th);
		      z[k] = 0;
		      vx[k] = -v*Math.sin(th);
		      vy[k] = v*Math.cos(th);
		      vz[k] = 0;
		      rad[k] = 5.9e-7;
		      rad0[k] = 5.9e-7;
		      k++;
		    }
		  }

	      /*
		  mv = 0;
		  for (j=1; j<61; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<61; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<61; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("Saturn and two rings")) {
		  double omega = 3.29764565598247e+03;
		  System.out.println("G = " + G);

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Rings
		  k = 1;
		  for (i=0; i<=1; i++) {
		    for (j=1; j<=200; j++) {
			    /*
		           if (i== 0) {m[k] = 1.82863877637012e-02; r = 1.01251000000000e-03;} 
		      else if (i== 1) {m[k] = 4.12771553680462e-03; r = 1.08338570000000e-03;} 

		           if (i== 0) {m[k] = 6.40825767530828e-03; r = 1.01251000000000e-03;} 
		      else if (i== 1) {m[k] = 7.72817461333123e-04; r = 1.05301040000000e-03;} 

		           if (i== 0) {m[k] = 2.30456583524263e-01; r = 1.01251000000000e-03;} 
		      else if (i== 1) {m[k] = 4.94779020382856e-01; r = 1.41751400000000e-03;} 
			     */
		           if (i== 0) {m[k] = 1.53542000396353e-03; r = 1.01251000000000e-03;} 
		      else if (i== 1) {m[k] = 7.95564921181862e-05; r = 1.03276020000000e-03;} 
		      
		      
		      
		      
			    
		      col[k] = new Color((float)1.0,(float)((1.0+i)/2.0),
			                            (float)((1.0-i)/2.0));
		      th = 2*j*Math.PI/200;
		      v = omega*r;
    
		      x[k] = r*Math.cos(th);
		      y[k] = r*Math.sin(th);
		      z[k] = 0;
		      vx[k] = -v*Math.sin(th);
		      vy[k] = v*Math.cos(th);
		      vz[k] = 0;
		      rad[k] = 5.9e-6;
		      rad0[k] = 5.9e-6;
		      J2[k] = 0.0; 
		      k++;
		    }
		  }
		} else
		if (name.equals("Saturn and three rings")) {
		  double omega = 3.29764565598247e+03;
		  System.out.println("G = " + G);

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Rings
		  k = 1;
		  for (i=-1; i<=1; i++) {
		    for (j=1; j<=20; j++) {
			    /**/
	 	      if      (i==-1) {m[k] = 1.12261140095987e+00; r = 1.20026262373385e-03;} 
		      else if (i== 0) {m[k] = 1.05379068267902e+00; r = 1.39456988327252e-03;}
		      else            {m[k] = 1.85226159021501e+00; r = 9.72085224081182e-04;}
		      /**/
			    /*
	 	      if      (i==-1) {m[k] = 9.69131259168187e-03; r = 9.89976746942712e-04;} 
		      else if (i== 0) {m[k] = 7.75436733046494e-04; r = 1.01251000000000e-03;}
		      else            {m[k] = 1.04769367034501e-02; r = 1.03581608645401e-03;}
		      */
			    /*
	 	      if      (i==-1) {
				m[k] = 8.41873525692317e+00; r = 2.27723515995427e-03;
		      } 
		      else if (i== 0) {
				m[k] = 8.45654228709736e-01; r = 1.01251000000000e-03;
		      }
		      else            {
				m[k] = 1.15291616702689e+02; r = 6.21812494741117e-03;
		      }
		      */
		      
			    
		      col[k] = new Color((float)1.0,(float)((1.0+i)/2.0),
			                            (float)((1.0-i)/2.0));
		      th = 2*j*Math.PI/20;
		      v = omega*r;
    
		      x[k] = r*Math.cos(th);
		      y[k] = r*Math.sin(th);
		      z[k] = 0;
		      vx[k] = -v*Math.sin(th);
		      vy[k] = v*Math.cos(th);
		      vz[k] = 0;
		      rad[k] = 5.9e-6;
		      rad0[k] = 5.9e-6;
		      J2[k] = 0.0; 
		      k++;
		    }
		  }
		} else
		if (name.equals("Saturn and 21 rings")) {
		  double omega = 3.29764565598247e+03;
		  System.out.println("G = " + G);

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Rings
		  k = 1;
		  for (i=-9; i<=10; i++) {
		    for (j=1; j<=200; j++) {
			    /*
			     if (i==-10) {m[k] = 1.04946917728698e-03; r = 9.13691336387618e-04;} 
			else if (i== -9) {m[k] = 8.79972545713893e-04; r = 1.13148914112881e-03;} 
			else if (i== -8) {m[k] = 1.74038604793115e-03; r = 9.79332036036741e-04;} 
			else if (i== -7) {m[k] = 3.83336628543437e-03; r = 1.07377785671094e-03;} 
			else if (i== -6) {m[k] = 1.34215557031485e-03; r = 9.47803578580295e-04;} 
			else if (i== -5) {m[k] = 5.51660543239391e-03; r = 1.05008544198126e-03;} 
			else if (i== -4) {m[k] = 1.00448506236467e-03; r = 1.00801205023175e-03;} 
			else if (i== -3) {m[k] = 9.33262179087712e-04; r = 1.12054395926285e-03;} 
			else if (i== -2) {m[k] = 1.32168490641505e-03; r = 1.02343475203987e-03;} 
			else if (i== -1) {m[k] = 1.16303829640046e-03; r = 1.14489143207532e-03;} 
			else if (i==  0) {m[k] = 6.56434588487539e-04; r = 1.03684616484015e-03;} 
			else if (i==  1) {m[k] = 8.02423911641745e-04; r = 1.15713422472678e-03;} 
			else if (i==  2) {m[k] = 8.51177671498368e-04; r = 1.03073362338586e-03;} 
			else if (i==  3) {m[k] = 1.17209966793778e-04; r = 1.12612136062041e-03;} 
			else if (i==  4) {m[k] = 1.13763358063934e-03; r = 1.01550315153385e-03;} 
			else if (i==  5) {m[k] = 6.25643933862232e-03; r = 1.10098882293930e-03;} 
			else if (i==  6) {m[k] = 3.52478769969189e-03; r = 9.63621740160791e-04;} 
			else if (i==  7) {m[k] = 6.34383099784076e-05; r = 1.08579900712461e-03;} 
			else if (i==  8) {m[k] = 4.13843421116543e-03; r = 9.95368943581232e-04;} 
			else if (i==  9) {m[k] = 2.20888088000280e-04; r = 1.13769639072188e-03;} 
			else if (i== 10) {m[k] = 2.85133809326723e-03; r = 9.32144653236708e-04;} 
			     */
			     if (i==-9) {m[k] = 3.99669479433845e-05; r = 9.48721870000000e-04;} 
			else if (i==-8) {m[k] = 3.93770782870610e-04; r = 9.55809440000000e-04;} 
			else if (i==-7) {m[k] = 2.63506308892998e-04; r = 9.62897010000000e-04;} 
			else if (i==-6) {m[k] = 5.57644773432045e-04; r = 9.69984580000000e-04;} 
			else if (i==-5) {m[k] = 3.89320825004667e-04; r = 9.77072150000000e-04;} 
			else if (i==-4) {m[k] = 6.55552712586138e-04; r = 9.84159720000000e-04;} 
			else if (i==-3) {m[k] = 4.65061851219501e-04; r = 9.91247290000000e-04;} 
			else if (i==-2) {m[k] = 7.12626544102052e-04; r = 9.98334860000000e-04;} 
			else if (i==-1) {m[k] = 5.05665951664880e-04; r = 1.00542243000000e-03;} 
			else if (i== 0) {m[k] = 7.38129395401505e-04; r = 1.01251000000000e-03;} 
			else if (i== 1) {m[k] = 5.16850366959192e-04; r = 1.01959757000000e-03;} 
			else if (i== 2) {m[k] = 7.35323997610358e-04; r = 1.02668514000000e-03;} 
			else if (i== 3) {m[k] = 4.99981533642706e-04; r = 1.03377271000000e-03;} 
			else if (i== 4) {m[k] = 7.03918821675596e-04; r = 1.04086028000000e-03;} 
			else if (i== 5) {m[k] = 4.53112017244901e-04; r = 1.04794785000000e-03;} 
			else if (i== 6) {m[k] = 6.40082981600130e-04; r = 1.05503542000000e-03;} 
			else if (i== 7) {m[k] = 3.70004735151893e-04; r = 1.06212299000000e-03;} 
			else if (i== 8) {m[k] = 5.34177835656960e-04; r = 1.06921056000000e-03;} 
			else if (i== 9) {m[k] = 2.35710186406943e-04; r = 1.07629813000000e-03;} 
			else if (i==10) {m[k] = 3.61723413390969e-04; r = 1.08338570000000e-03;} 
			
		      
			    
		      col[k] = new Color((float)1.0,(float)((10.0+i)/20.0),
			                            (float)((10.0-i)/20.0));
		      th = 2*j*Math.PI/200;
		      v = omega*r;
    
		      x[k] = r*Math.cos(th);
		      y[k] = r*Math.sin(th);
		      z[k] = 0;
		      vx[k] = -v*Math.sin(th);
		      vy[k] = v*Math.cos(th);
		      vz[k] = 0;
		      rad[k] = 5.9e-6;
		      rad0[k] = 5.9e-6;
		      J2[k] = 0.0; 
		      k++;
		    }
		  }
		} else
		if (name.equals("Saturn and a stack")) {
		  double omega = 3.29764565598247e+03;
		  System.out.println("G = " + G);

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Stack
		 m[0]  = 9.5200000000000e+01; x[0]  = -5.3150819012309e-10 ;
		 m[1]  = 1.0038313527903e+00; x[1]  =  5.2556738861803e-04 ;
		 m[2]  = 1.0704000587087e+00; x[2]  =  6.0501506317541e-04 ;
		 m[3]  = 1.0809557136027e+00; x[3]  =  6.7540989098243e-04 ;
		 m[4]  = 1.0865692069669e+00; x[4]  =  7.4121638351205e-04 ;
		 m[5]  = 1.0906908634947e+00; x[5]  =  8.0454691858220e-04 ;
		 m[6]  = 1.0888531143206e+00; x[6]  =  8.6644100250801e-04 ;
		 m[7]  = 1.0894341725200e+00; x[7]  =  9.2759475297990e-04 ;
		 m[8]  = 1.1033952857582e+00; x[8]  =  9.8886313054173e-04 ;
		 m[9]  = 1.1141799195585e+00; x[9]  =  1.0508836273685e-03 ;
		 m[10] = 1.1139344694694e+00; x[10] =  1.1137530864565e-03 ;
		 m[11] = 1.0749154833125e+00; x[11] =  1.1768281306369e-03 ;
		 m[12] = 1.0464788931389e+00; x[12] =  1.2398495985959e-03 ;
		 m[13] = 1.0364243945275e+00; x[13] =  1.3037615640748e-03 ;
		 m[14] = 1.0306277360586e+00; x[14] =  1.3695570783788e-03 ;
		 m[15] = 1.0361192433238e+00; x[15] =  1.4382669210136e-03 ;
		 m[16] = 1.0264813357504e+00; x[16] =  1.5107784599744e-03 ;
		 m[17] = 1.0219172076627e+00; x[17] =  1.5882237781148e-03 ;
		 m[18] = 1.0106616545411e+00; x[18] =  1.6728412339565e-03 ;
		 m[19] = 9.9157337977276e-01; x[19] =  1.7686022640193e-03 ;
		 m[20] = 8.7285538306979e-01; x[20] =  1.8828534725485e-03 ;
		  
		  for (k=1; k<=20; k++) {
		      col[k] = new Color((float)1.0,(float)((1.0+k/20.)/2.0),
			                            (float)((1.0-k/20.)/2.0));
		      v = omega*x[k];
    
		      y[k] = 0;
		      z[k] = 0;
		      vx[k] = 0;
		      vy[k] = v;
		      vz[k] = 0;
		      rad[k] = 5.9e-6;
		      rad0[k] = 5.9e-6;
		      J2[k] = 0.0; 
		  }
		} else
		if (name.equals("ideal Epimetheus/Janus")) {
		  double e;  // this is the eccentricity

		  /*
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Epimetheus
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Epimetheus";
		  m[1] = 9.377e-8;
		  r = 0.00101218;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);

		  // e = 0.009;
		  e = 0.000;
		  r /= 1+e;
		  v *= (1+e);

		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  // z[1] = 0.000001*(Math.random()-0.5)*r;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 3.8e-7;
		  rad0[1] = 3.8e-7;
		  rad[1] = rad[0]/20;

		  // Janus
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Janus";
		  m[2] = 3.366e-7;
		  r = 0.00101251;
		  // r = 0.00101351;  // close to break out
		  th = Math.PI;
		  v = Math.sqrt(G*m[0]/r);

		  // e = 0.007;
		  e = 0.000;
		  r /= 1-e;
		  v *= (1-e);

		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  // z[2] = 0.000001*(Math.random()-0.5)*r;
		  vx[2] = -v*Math.sin(th);
		  vy[2] = v*Math.cos(th);
		  vz[2] = 0;
		  rad[2] = 5.9e-7;
		  rad0[2] = 5.9e-7;
		  rad[2] = rad[0]/20;

		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
		  */
		  /*********************************************/

		  // Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;

		  // Janus
		  col[1] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[1] = "Janus";
		  // m[1] = 3.366e-7/1000;
		  m[1] = 3.366e-7;
		  rad[1] = 5.9e-7;
		  rad0[1] = 5.9e-7;
		  r = 0.00101251;
		  v = Math.sqrt(G*m[0]/r);

		  double dt = 1e-5;
		  double omega = v/r;
		  r = Math.pow((1+(m[1]/m[0]))*(1+(m[1]/m[0]))
			                   *(1-Math.cos(omega*dt))
			       /(m[0]*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] = -(m[1]/m[0])*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] =  (m[1]/m[0])*v*Math.sin(omega*dt/2);
		  vy[0] = -(m[1]/m[0])*v*Math.cos(omega*dt/2);
		  vz[0] = 0;
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = -v*Math.sin(omega*dt/2);
		  vy[1] =  v*Math.cos(omega*dt/2);
		  vz[1] = 0;

		  // Epimetheus
		  col[2] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[2] = "Epimetheus";
		  m[2] = 9.377e-8;
		  rad[2] = 3.8e-7;
		  rad0[2] = 3.8e-7;
		  // r = 0.00101251 + (0.00101218 - 0.00101251)/10;
		  r = 0.00101218;
		  v = Math.sqrt(G*m[0]/r);

		  omega = v/r;
		  r = Math.pow((1+(m[2]/m[0]))*(1+(m[2]/m[0]))
			                   *(1-Math.cos(omega*dt))
			       /(m[0]*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] += (m[2]/m[0])*r;
		  y[0] += 0;
		  z[0] += 0;
		  vx[0] += -(m[2]/m[0])*v*Math.sin(omega*dt/2);
		  vy[0] +=  (m[2]/m[0])*v*Math.cos(omega*dt/2);
		  vz[0] += 0;
		  x[1] += (m[2]/m[0])*r;
		  y[1] += 0;
		  z[1] += 0;
		  vx[1] += -(m[2]/m[0])*v*Math.sin(omega*dt/2);
		  vy[1] +=  (m[2]/m[0])*v*Math.cos(omega*dt/2);
		  vz[1] += 0;
		  x[2] = -r;
		  y[2] = 0;
		  z[2] = 0;
		  vx[2] =  v*Math.sin(omega*dt/2);
		  vy[2] = -v*Math.cos(omega*dt/2);
		  vz[2] = 0;

		    double ax[]=new double[n], 
			   ay[]=new double[n], 
			   az[]=new double[n];
		    for (j=0; j<n; j++) {
			ax[j] = 0; ay[j] = 0; az[j] = 0;
			for (i=0; i<n; i++) {
			    if (i != j) {
				r = (x[i]-x[j])*(x[i]-x[j])
				  + (y[i]-y[j])*(y[i]-y[j])
				  + (z[i]-z[j])*(z[i]-z[j]);
				r = Math.sqrt(r);
				double r3 = r*r*r;
				ax[j] -= G * m[i] * (x[j] - x[i])/r3;
				ay[j] -= G * m[i] * (y[j] - y[i])/r3;
				az[j] -= G * m[i] * (z[j] - z[i])/r3;
			    }
			}
		    }
		    for (j=0; j<n; j++) {
			vx[j] -= ax[j] * dt/2;
			vy[j] -= ay[j] * dt/2;
			vz[j] -= az[j] * dt/2;
		    }

		  /*********************************************/


		} else
		if (name.equals("Ruptured Epimetheus/Janus")) {
		  double e;  // this is the eccentricity

		  // Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; 
		  // J2[0] = 1.6297e-2;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Janus
		  col[1] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[1] = "Janus";
		  m[1] = 3.366e-7;
		  rad[1] = 5.9e-7;
		  rad0[1] = 5.9e-7;
		  r = 0.00101251+0.00000160;
		  v = Math.sqrt(G*m[0]/r);

		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  // vx[1] = -m[2]*v*12000; // dv for cleavage
		  // vy[1] = v;
		  vx[1] = 0;
		  vy[1] = Math.sqrt(G*m[0]/r);
		  vz[1] = 0;

		  // Epimetheus
		  col[2] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[2] = "Epimetheus";
		  m[2] = 9.377e-8;
		  rad[2] = 3.8e-7;
		  rad0[2] = 3.8e-7;
		  r = 0.00101218-0.00000000;
		  x[2] = -r;
		  y[2] = 0;
		  z[2] = 0;
		  // vx[2] =  m[1]*v*12000; // dv for cleavage
		  // vy[2] = v;
		  vx[2] = 0;
		  vy[2] = -Math.sqrt(G*m[0]/r);
		  vz[2] = 0;

		} else
		if (name.equals("ephemeris Epimetheus/Janus")) {
		  double vx_ideal, vy_ideal, vz_ideal;
		  /*
		  pathx = new double[3][];
		  pathy = new double[3][];
		  pathz = new double[3][];
		  */

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 5.6846e+26/5.972e+24;
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  // J2[0] = 0.0; 
		  J2[0] = 1.6297e-2;

		  startDate = 2006.4384;
		  // HORIZONS data from 2006-06-10
		  fromEphem(130.4484023, 19.0434458,    14.33,    -3.77, 
			  	9.77069538094012, 21.7158556, 0);

		  // Epimetheus
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Epimetheus";
		  m[1] = 9.377e-8;
		  rad[1] = 3.8e-7;
		  rad0[1] = 3.8e-7;

		  // HORIZONS data from 2006-06-10
		  fromEphem( 130.4477093, 19.0414219,    22.31,    -3.17, 
			  	9.77163810715679, 23.8501509, 1);

		  // Janus
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Janus";
		  m[2] = 3.366e-7;
		  rad[2] = 5.9e-7;
		  rad0[2] = 5.9e-7;

		  // HORIZONS data from 2006-06-10
		  fromEphem(130.4513668, 19.0420465,    21.41,    -1.75, 
			  	9.77155251682565, 15.1764254, 2);


		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<3; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<3; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<3; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<3; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<3; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}
		  if (pathx != null && pathy != null && pathz != null) {
		      mx = 0; for (j=0; j<3; j++) { mx += m[j]*pathx[j][0]; } 
		      my = 0; for (j=0; j<3; j++) { my += m[j]*pathy[j][0]; } 
		      mz = 0; for (j=0; j<3; j++) { mz += m[j]*pathz[j][0]; } 
		      for (j=1; j<3; j++) {
			for (i=0; i<pathx[j].length; i++){
			    pathx[j][i] -= mx/M - pathx[0][0] + pathx[0][i];
			}
			for (i=0; i<pathy[j].length; i++){
			    pathy[j][i] -= my/M - pathy[0][0] + pathy[0][i];
			}
			for (i=0; i<pathz[j].length; i++){
			    pathz[j][i] -= mz/M - pathz[0][0] + pathz[0][i];
			}
		      }
		  }

	      /*
		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<3; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<3; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<3; j++) { vz[j] -= mv/M; }
	      */
		  {
		      /* compute inclinations */
		      /*
		      double x1, y1, z1, norm1, x2, y2, z2, norm2;
		      double nx, ny, nz, nnorm;
		      x1 = pathx[1][0];
		      y1 = pathy[1][0];
		      z1 = pathz[1][0];
		      norm1 = Math.sqrt(x1*x1 + y1*y1 + z1*z1);
		      x1 /= norm1; y1 /= norm1; z1 /= norm1; 
		      x2 = pathx[1][pathx[1].length/4];
		      y2 = pathy[1][pathx[1].length/4];
		      z2 = pathz[1][pathx[1].length/4];
		      norm2 = Math.sqrt(x2*x2 + y2*y2 + z2*z2);
		      x2 /= norm2; y2 /= norm2; z2 /= norm2; 
		      nx = z2*y1-z1*y2;
		      ny = x2*z1-x1*z2;
		      nz = y2*x1-y1*x2;
		      nnorm = Math.sqrt(nx*nx + ny*ny + nz*nz);
		      nx /= nnorm;
		      ny /= nnorm;
		      nz /= nnorm;
		      //System.out.println("Epimetheus angle = "+(x1*x2+y1*y2+z1*z2));
		      System.out.print("Epimetheus normal = ("+nx);
		      System.out.print(","+ny);
		      System.out.print(","+nz);
		      System.out.println(")");

		      x1 = pathx[2][0];
		      y1 = pathy[2][0];
		      z1 = pathz[2][0];
		      norm1 = Math.sqrt(x1*x1 + y1*y1 + z1*z1);
		      x1 /= norm1; y1 /= norm1; z1 /= norm1; 
		      x2 = pathx[2][pathx[1].length/4];
		      y2 = pathy[2][pathx[1].length/4];
		      z2 = pathz[2][pathx[1].length/4];
		      norm2 = Math.sqrt(x2*x2 + y2*y2 + z2*z2);
		      x2 /= norm2; y2 /= norm2; z2 /= norm2; 
		      nx = z2*y1-z1*y2;
		      ny = x2*z1-x1*z2;
		      nz = y2*x1-y1*x2;
		      nnorm = Math.sqrt(nx*nx + ny*ny + nz*nz);
		      nx /= nnorm;
		      ny /= nnorm;
		      nz /= nnorm;
		      //System.out.println("Janus angle = "+(x1*x2+y1*y2+z1*z2));
		      System.out.print("Janus normal = ("+nx);
		      System.out.print(","+ny);
		      System.out.print(","+nz);
		      System.out.println(")");
		      */
		  }

		  /*
	          r = Math.sqrt(x[1]*x[1] + y[1]*y[1] + z[1]*z[1]);
		  th = Math.atan2(y[1],x[1]);
	          v = Math.sqrt(G*m[0]/r);
	          vx_ideal = -v*Math.sin(th);
	          vy_ideal = v*Math.cos(th);
	          vz_ideal = 0;
		  /*
		  System.out.print("Epimetheus v = ("+(vx[1]));
		  System.out.print(", "+(vy[1]));
		  System.out.print(", "+(vz[1]));
		  System.out.println(")");
		  System.out.print("Epimetheus v_ideal = ("+(vx_ideal));
		  System.out.print(", "+(vy_ideal));
		  System.out.print(", "+(vz_ideal));
		  System.out.println(")");
		  System.out.print("Epimetheus dv = ("+(vx[1]-vx_ideal));
		  System.out.print(", "+(vy[1]-vy_ideal));
		  System.out.print(", "+(vz[1]-vz_ideal));
		  System.out.println(")");
		  double dv_rad = (vx[1]-vx_ideal)*x[1] + (vy[1]-vy_ideal)*y[1];
		  dv_rad /= r;
		  // System.out.println("Epimetheus dv_radial = "+ dv_rad);
		  double dv_tan = -(vx[1]-vx_ideal)*y[1] + (vy[1]-vy_ideal)*x[1];
		  dv_tan /= r;
		  // System.out.println("Epimetheus dv_tangential = "+ dv_tan);

		  // x[1] = r;
		  // y[1] = 0;
		  // z[1] = 0;
		  vx[1] = vx_ideal;
		  vy[1] = vy_ideal;
		  // vz[1] = vz_ideal;

	          r = Math.sqrt(x[2]*x[2] + y[2]*y[2] + z[2]*z[2]);
		  th = Math.atan2(y[2],x[2]);
	          v = Math.sqrt(G*m[0]/r);
	          vx_ideal = -v*Math.sin(th);
	          vy_ideal = v*Math.cos(th);
	          vz_ideal = 0;
		  System.out.print("Janus dv = ("+(vx[2]-vx_ideal));
		  System.out.print(", "+(vy[2]-vy_ideal));
		  System.out.print(", "+(vz[2]-vz_ideal));
		  System.out.println(")");
		  dv_rad = (vx[2]-vx_ideal)*x[2] + (vy[2]-vy_ideal)*y[2];
		  dv_rad /= r;
		  // System.out.println("Epimetheus dv_radial = "+ dv_rad);
		  dv_tan = -(vx[2]-vx_ideal)*y[2] + (vy[2]-vy_ideal)*x[2];
		  dv_tan /= r;
		  // System.out.println("Epimetheus dv_tangential = "+ dv_tan);

		  // x[2] = -r;
		  // y[2] = 0;
		  // z[2] = 0;
		  vx[2] = vx_ideal;
		  vy[2] = vy_ideal;
		  // vz[2] = vz_ideal;

		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<3; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<3; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<3; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<3; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Epimetheus/Janus at L4/L5")) {
		  double e, L, p;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  // J2[0] = 0.0; 
		  J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  r = (0.00101218 + 0.00101251)/2;
		  v = Math.sqrt(G*m[0]/r);
		  // System.out.println(v);
		  p = r;
		  L = r*v;
		  e = 0.00;
		  th = Math.PI/6;
		  // r = p/(1 + e*Math.cos(th));  // method 1
		  r = p/(1+e); // method 2

		  // Epimetheus
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Epimetheus";
		  m[1] = 9.377e-8;
		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  /* // method 1
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*(e+Math.cos(th));
		  */
		  // method 2
		  vx[1] = -(1+e)*v*Math.sin(th);
		  vy[1] = (1+e)*v*Math.cos(th);

		  vz[1] = 0;
		  rad[1] = 3.8e-7;
		  rad0[1] = 3.8e-7;  // ten times too big 053005
		  // rad[1] = rad[0]/20;

		  // Janus
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Janus";
		  m[2] = 3.366e-7;
		  th = -th;
		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  /* // method 1
		  vx[2] = -v*Math.sin(th);
		  vy[2] = v*(e+Math.cos(th));
		  */
		  // method 2
		  vx[2] = -(1+e)*v*Math.sin(th);
		  vy[2] = (1+e)*v*Math.cos(th);

		  vz[2] = 0;
		  rad[2] = 5.9e-7;
		  rad0[2] = 5.9e-7;  // ten times too big 053005
		  // rad[2] = rad[0]/20;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("Sun/Saturn/Epimetheus/Janus")) {
		  double e;  // this is the eccentricity

		  // Sun (is body 3)
		  col[3] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[3] = "Sun";
		  m[3] = 333000;
		  r = 0.09539;  // correct number is 9.539
		  th = Math.PI;
		  v = 0;

		  x[3] = r*Math.cos(th);
		  y[3] = r*Math.sin(th);
		  z[3] = 0;
		  //vx[3] = -v*Math.sin(th);
		  //vy[3] = v*Math.cos(th);
		  vx[3] = 0;
		  vy[3] = 0;
		  vz[3] = 0;
		  rad[3] = 0.0046;
		  rad0[3] = 0.0046;

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  v = Math.sqrt(G*m[3]/r);
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Epimetheus
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Epimetheus";
		  m[1] = 9.377e-8;
		  r = 0.00101218;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);

		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = vy[0] + v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 3.8e-7;
		  rad0[1] = 3.8e-7;

		  // Janus
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Janus";
		  m[2] = 3.366e-7;
		  r = 0.00101251;
		  th = Math.PI;
		  v = Math.sqrt(G*m[0]/r);

		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  vx[2] = -v*Math.sin(th);
		  vy[2] = vy[0] + v*Math.cos(th);
		  vz[2] = 0;
		  rad[2] = 5.9e-7;
		  rad0[2] = 5.9e-7;

	      /*
		double M=0; for (j=0; j<n; j++) { M += m[j]; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		      for (j=0; j<n; j++) { vx[j] -= mv/M; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		      for (j=0; j<n; j++) { vy[j] -= mv/M; }
		mv=0; for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		      for (j=0; j<n; j++) { vz[j] -= mv/M; }
	      */

		      /*
		  mv = 0;
		  for (j=1; j<4; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[3] = -mv/m[3];
		  mv = 0;
		  for (j=1; j<4; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[3] = -mv/m[3];
		  mv = 0;
		  for (j=1; j<4; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[3] = -mv/m[3];
		  */

		} else
		if (name.equals("Dione/Helene")) {
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Dione
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Dione";
		  m[1] = 1.758e-4;
		  r = 0.00252273;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 3.7e-6;
		  rad0[1] = 3.7e-6;

		  // Helene
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Helene";
		  m[2] = 3.366e-7/64;  // this is just a guess (Janus' mass/64)
		  r = 0.00252273;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  vx[2] = -v*Math.sin(th);
		  vy[2] = v*Math.cos(th);
		  vz[2] = 0;
		  rad[2] = 1.0e-7;
		  rad0[2] = 1.0e-7;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("Tethys/Telesto/Calypso")) {
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Tethys
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Tethys";
		  m[1] = 1.042e-4;
		  r = 0.00196965;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 3.5e-6;
		  rad0[1] = 3.5e-6;

		  // Telesto
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Telesto";
		  m[2] = 3.366e-7/64;  // this is just a guess (Janus' mass/64)
		  r = 0.00196965;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  vx[2] = -v*Math.sin(th);
		  vy[2] = v*Math.cos(th);
		  vz[2] = 0;
		  rad[2] = 1.0e-7;
		  rad0[2] = 1.0e-7;

		  // Calypso
		  col[3] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[3] = "Calypso";
		  m[3] = 3.366e-7/64;  // this is just a guess (Janus' mass/64)
		  r = 0.00196965;
		  th = -Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[3] = r*Math.cos(th);
		  y[3] = r*Math.sin(th);
		  z[3] = 0;
		  vx[3] = -v*Math.sin(th);
		  vy[3] = v*Math.cos(th);
		  vz[3] = 0;
		  rad[3] = 1.0e-7;
		  rad0[3] = 1.0e-7;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("Saturn's Inner Moons")) {
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Prometheus
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.4);
		  OrbName[j] = "Prometheus";
		  m[j] = 2.70e+17/5.972e+24;
		  // System.out.println("Prometheus    "+(m[j]/m[0]));
		  r = 139000/149668992.0;
		  // r = 150000/149668992.0;  // wrong...accelerates stirring
		  rad[j] = 46/149668992.0;
		  rad0[j] = 46/149668992.0;
		  th = Math.PI/2;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Pandora
		  j=2;
		  col[j] = new Color((float)0.9,(float)0.3,(float)0.5);
		  OrbName[j] = "Pandora";
		  m[j] = 2.20e+17/5.972e+24;
		  // System.out.println("Pandora       "+(m[j]/m[0]));
		  r = 142000/149668992.0;
		  // r = 150100/149668992.0;  // wrong...accelerates stirring
		  rad[j] = 46/149668992.0;
		  rad0[j] = 46/149668992.0;
		  th = Math.PI;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Epimetheus
		  j=3;
		  col[j] = new Color((float)0.8,(float)0.3,(float)0.6);
		  OrbName[j] = "Epimetheus";
		  m[j] = 5.60e+17/5.972e+24;
		  // System.out.println("Epimetheus    "+(m[j]/m[0]));
		  r = 151000/149668992.0;
		  rad[j] = 57/149668992.0;
		  rad0[j] = 57/149668992.0;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Janus
		  j=4;
		  col[j] = new Color((float)0.8,(float)0.3,(float)0.6);
		  OrbName[j] = "Janus";
		  m[j] = 2.01e+17/5.972e+24;
		  // System.out.println("Janus         "+(m[j]/m[0]));
		  r = 151000/149668992.0;
		  rad[j] = 89/149668992.0;
		  rad0[j] = 89/149668992.0;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Mimas
		  j=5;
		  col[j] = new Color((float)0.7,(float)0.3,(float)0.7);
		  OrbName[j] = "Mimas";
		  m[j] = 3.80e+19/5.972e+24;
		  // System.out.println("Mimas         "+(m[j]/m[0]));
		  r = 186000/149668992.0;
		  rad[j] = 196/149668992.0;
		  rad0[j] = 196/149668992.0;
		  th = 3*Math.PI/4;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Enceladus
		  j=6;
		  col[j] = new Color((float)0.6,(float)0.3,(float)0.8);
		  OrbName[j] = "Enceladus";
		  m[j] = 8.40e+19/5.972e+24;
		  // System.out.println("Enceladus     "+(m[j]/m[0]));
		  r = 238000/149668992.0;
		  rad[j] = 260/149668992.0;
		  rad0[j] = 260/149668992.0;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Tethys
		  j=7;
		  col[j] = new Color((float)0.5,(float)0.3,(float)0.9);
		  OrbName[j] = "Tethys";
		  m[j] = 7.55e+20/5.972e+24;
		  // System.out.println("Tethys        "+(m[j]/m[0]));
		  r = 295000/149668992.0;
		  rad[j] = 530/149668992.0;
		  rad0[j] = 530/149668992.0;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Telesto
		  j=8;
		  col[j] = new Color((float)0.5,(float)0.3,(float)0.9);
		  OrbName[j] = "Telesto";
		  m[j] = (2.70e+17/5.972e+24)/10; // just a guess
		  // System.out.println("Telesto       "+(m[j]/m[0]));
		  r = 295000/149668992.0;
		  rad[j] = 15/149668992.0;
		  rad0[j] = 15/149668992.0;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Calypso
		  j=9;
		  col[j] = new Color((float)0.5,(float)0.3,(float)0.9);
		  OrbName[j] = "Calypso";
		  m[j] = (2.70e+17/5.972e+24)/10; // just a guess
		  // System.out.println("Calypso       "+(m[j]/m[0]));
		  r = 295000/149668992.0;
		  rad[j] = 13/149668992.0;
		  rad0[j] = 13/149668992.0;
		  th = -Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Dione
		  j=10;
		  col[j] = new Color((float)0.4,(float)0.3,(float)1.0);
		  OrbName[j] = "Dione";
		  m[j] = 1.05e+21/5.972e+24;
		  // System.out.println("Dione         "+(m[j]/m[0]));
		  r = 377000/149668992.0;
		  rad[j] = 560/149668992.0;
		  rad0[j] = 560/149668992.0;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Helene
		  j=11;
		  col[j] = new Color((float)0.4,(float)0.3,(float)1.0);
		  OrbName[j] = "Helene";
		  m[j] = (2.70e+17/5.972e+24)/10; // just a guess
		  // System.out.println("Helene        "+(m[j]/m[0]));
		  r = 377000/149668992.0;
		  rad[j] = 16/149668992.0;
		  rad0[j] = 16/149668992.0;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

	      /*
		  mv = 0;
		  for (j=1; j<12; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<12; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<12; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("ephemeris Saturn's Moons")) {
		  startDate = 2006.4384;
		  // Horizons data for 2006-06-10 00:00
		  double vx_ideal, vy_ideal, vz_ideal;

		  // include Saturn
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn:  0";
		  m[j] = 5.6846e+26/5.972e+24;
		  rad[j] = 60268/149668992.0;
		  rad0[j] = 60268/149668992.0;
		  J2[j] = 0.0; // J2[0] = 1.6297e-2;

		  fromEphem(130.4484023, 19.0434458,    14.33,    -3.77, 9.77069538094012, 21.7158556, j);

		  // Prometheus
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Prometheus:  1";
		  m[j] = 1.40e+17/5.972e+24;
		  rad[j] = 74/149668992.0;
		  rad0[j] = 74/149668992.0;

		  fromEphem(130.4539600, 19.0444621,    12.23,    -1.34, 9.77050481886019,  6.4479396, j);

		  // Pandora
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Pandora:  2";
		  m[j] = 1.30e+17/5.972e+24;
		  rad[j] = 55/149668992.0;
		  rad0[j] = 55/149668992.0;

		  fromEphem(130.4542132, 19.0442342,    13.37,    -1.17, 9.77062862525237,  6.2785694, j);

		  // Epimetheus
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Epimetheus:  3";
		  m[j] = 5.5e+17/5.972e+24;
		  rad[j] = 69/149668992.0;
		  rad0[j] = 69/149668992.0;

		  fromEphem(130.4477093, 19.0414219,    22.31,    -3.17, 9.77163810715679, 23.8501509, j);

		  // Janus
		  j=4;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Janus:  4";
		  m[j] = 1.98e+18/5.972e+24;
		  rad[j] = 99/149668992.0;
		  rad0[j] = 99/149668992.0;

		  fromEphem(130.4513668, 19.0420465,    21.41,    -1.75, 9.77155251682565, 15.1764254, j);

		  // Mimas
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Mimas:  5";
		  m[j] = 3.79e+19/5.972e+24;
		  rad[j] = 199/149668992.0;
		  rad0[j] = 199/149668992.0;

		  fromEphem(130.4453914, 19.0452343,     7.83,    -5.55, 9.76957615780299, 26.5298457, j);

		  // Enceladus
		  j=6;
		  col[j] = new Color((float)1.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Enceladus:  6";
		  m[j] = 10.805e+19/5.972e+24;
		  rad[j] = 252/149668992.0;
		  rad0[j] = 252/149668992.0;

		  fromEphem(130.4419536, 19.0450727,     9.53,    -5.67, 9.76953256103799, 29.3008185, j);

		  // Tethys
		  j=7;
		  col[j] = new Color((float)0.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Tethys:  7";
		  m[j] = 61.76e+19/5.972e+24;
		  rad[j] = 536/149668992.0;
		  rad0[j] = 536/149668992.0;

		  fromEphem(130.4568256, 19.0413823,    18.47,    -2.06, 9.77207759069366, 14.5681863, j);

		  // Telesto
		  j=8;
		  col[j] = new Color((float)0.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Telesto:  8";
		  m[j] = 0.0007194e+19/5.972e+24; // just a guess
		  rad[j] = 12/149668992.0;
		  rad0[j] = 12/149668992.0;

		  fromEphem(130.4601978, 19.0454645,    12.95,    -1.98, 9.77031508516390, 11.2750558, j);

		  // Calypso
		  j=9;
		  col[j] = new Color((float)0.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Calypso:  9";
		  m[j] = 0.0003597e+19/5.972e+24; // just a guess
		  rad[j] = 9.5/149668992.0;
		  rad0[j] = 9.5/149668992.0;

		  fromEphem(130.4452404, 19.0396837,    19.88,    -3.76, 9.77248696409408, 24.8175479, j);

		  // Dione
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Dione: 10";
		  m[j] = 109.572e+19/5.972e+24;
		  rad[j] = 562.5/149668992.0;
		  rad0[j] = 562.5/149668992.0;

		  fromEphem(130.4357701, 19.0450048,    11.38,    -5.43, 9.76922089663497, 29.1619283, j);

		  // Helene
		  j=11;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Helene: 11";
		  m[j] = 0.002547e+19/5.972e+24; // just a guess
		  rad[j] = 16/149668992.0;
		  rad0[j] = 16/149668992.0;

		  fromEphem(130.4331197, 19.0408614,    15.40,    -5.26, 9.77109645269393, 31.0169354, j);

		  // Cassini Spacecraft
		  j=12;
		  col[j] = new Color((float)1.0,(float)0.6,(float)0.6);
		  OrbName[j] = "Cassini Probe: 12";
		  m[j] = 0;
		  rad[j] = 16/149668992.0;
		  rad0[j] = 16/149668992.0;

		  fromEphem(130.5001977, 18.9996608,    14.87,    -3.66, 9.79579223239982, 21.5998305, j);

		  // Rhea
		  j=13;
		  col[j] = new Color((float)0.5,(float)1.0,(float)1.0);
		  OrbName[j] = "Rhea: 13";
		  m[j] = 230.9e+19/5.972e+24; 
		  rad[j] = 764/149668992.0;
		  rad0[j] = 764/149668992.0;

		  fromEphem(130.4297947, 19.0449410,    12.13,    -5.19, 9.76886978631172, 28.4430765, j);

		  // Titan
		  j=14;
		  col[j] = new Color((float)1.0,(float)0.5,(float)1.0);
		  OrbName[j] = "Titan: 14";
		  m[j] = 13455.31e+19/5.972e+24; 
		  rad[j] = 2575.5/149668992.0;
		  rad0[j] = 2575.5/149668992.0;

		  fromEphem(130.4531890, 19.0588457,    11.47,    -4.00, 9.76321134752995, 21.0706557, j);

		  // Hyperion
		  j=15;
		  col[j] = new Color((float)0.6,(float)0.5,(float)1.0);
		  OrbName[j] = "Hyperion: 15";
		  m[j] = 1.08e+19/5.972e+24; 
		  rad[j] = 133/149668992.0;
		  rad0[j] = 133/149668992.0;

		  fromEphem(130.5002637, 19.0538329,    13.31,    -3.06, 9.76811038817194, 16.7275703, j);

		  // Iapetus
		  j=16;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.5);
		  OrbName[j] = "Iapetus: 16";
		  m[j] = 180.59e+19/5.972e+24; 
		  rad[j] = 734/149668992.0;
		  rad0[j] = 734/149668992.0;

		  fromEphem(130.3598269, 19.0326072,    15.62,    -3.78, 9.78950811148130, 23.7249522, j);

		  // Phoebe
		  j=17;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Phoebe: 17";
		  m[j] = 0.8289e+19/5.972e+24; 
		  rad[j] = 106.6/149668992.0;
		  rad0[j] = 106.6/149668992.0;

		  fromEphem(130.2119754, 19.1196186,    13.55,    -3.62, 9.86080987280932, 21.1243646, j);

		  double xAngMom[] = new double[18];
		  double yAngMom[] = new double[18];
		  double zAngMom[] = new double[18];
		  double magAngMom[] = new double[18];
		  System.out.println("Angular Momenta");
		  for (j=1; j<18; j++) {
		      xAngMom[j] = m[j]*((y[j]-y[0])*(vz[j]-vz[0])
			               - (z[j]-z[0])*(vy[j]-vy[0]));
		      yAngMom[j] = m[j]*((z[j]-z[0])*(vx[j]-vx[0])
			               - (x[j]-x[0])*(vz[j]-vz[0]));
		      zAngMom[j] = m[j]*((x[j]-x[0])*(vy[j]-vy[0])
			               - (y[j]-y[0])*(vx[j]-vx[0]));
		      magAngMom[j] = Math.sqrt(xAngMom[j]*xAngMom[j]
			                      +yAngMom[j]*yAngMom[j]
					      +zAngMom[j]*zAngMom[j]);
		      System.out.print(OrbName[j]);
		      System.out.print(" = (");
		      System.out.print(xAngMom[j]/*/magAngMom[j]*/+",");
		      System.out.print(yAngMom[j]/*/magAngMom[j]*/+",");
		      System.out.println(zAngMom[j]/*/magAngMom[j]*/+")");
		  }
		  double xAM = 0, yAM = 0, zAM = 0, magAM;
		  for (j=1; j<3; j++) {
		      if (j!=3 && j!=4) {
			  xAM += xAngMom[j];
			  yAM += yAngMom[j];
			  zAM += zAngMom[j];
		      }
		  }
		  magAM = Math.sqrt(xAM*xAM+yAM*yAM+zAM*zAM);
		  for (j=1; j<18; j++) {
		  System.out.print(OrbName[j]);
		  System.out.println(": "+(180/Math.PI)*Math.acos((
				        xAngMom[j]*xAM+
			                yAngMom[j]*yAM+
					zAngMom[j]*zAM)/(magAM*magAngMom[j])));
		  }
		  double xCombo=xAngMom[3]+xAngMom[4];
		  double yCombo=yAngMom[3]+yAngMom[4];
		  double zCombo=zAngMom[3]+zAngMom[4];
		  double magCombo = Math.sqrt(xCombo*xCombo
			                     +yCombo*yCombo
			                     +zCombo*zCombo);
		  System.out.print("Combo = ");
		  System.out.println(": "+(180/Math.PI)*Math.acos((xCombo*xAM+ yCombo*yAM+ zCombo*zAM)
			                /(magAM*magCombo)));

	      /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<12; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<12; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<12; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<12; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<12; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<12; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<12; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<12; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<12; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<12; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<12; j++) { vz[j] -= mv/M; }
	      */
		} else
		if (name.equals("ephemeris Jupiter's Moons")) {
		  startDate = 2008.25;
		  // Horizons data for 2008-04-01 00:00
		  double vx_ideal, vy_ideal, vz_ideal;

		  // include Jupiter
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Jupiter:  0";
		  m[j] = 1.89813e+27/5.972e+24;
		  rad[j] = 71492/149668992.0;
		  rad0[j] = 71492/149668992.0;
		  J2[j] = 0.0; // J2[0] = 1.6297e-2;

		  fromEphem(291.64463, -21.91621, 16.62607,  2.212815,
				  5.26741312716559, -27.5638914, j);

		  // Io
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Io:  1";
		  m[j] = 8.933e+22/5.972e+24;
		  rad[j] = 1821/149668992.0;
		  rad0[j] = 1821/149668992.0;

		  fromEphem(291.65171, -21.91593, 32.35785,  5.083985,
				  5.27015723855705, -31.3143248, j);

		  // Europa
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Europa:  2";
		  m[j] = 4.797e+22/5.972e+24;
		  rad[j] = 1565/149668992.0;
		  rad0[j] = 1565/149668992.0;

		  fromEphem(291.67211, -21.91335, 27.41229,  4.252558,
				  5.27127360201173, -34.6071236, j);

		  // Ganymede
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Ganymede:  3";
		  m[j] = 1.482e+23/5.972e+24;
		  rad[j] = 2634/149668992.0;
		  rad0[j] = 2634/149668992.0;

		  fromEphem(291.58174, -21.92792, 23.15418,  3.090187,
				  5.27201366748384, -19.2734099, j);

		  // Callisto
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Callisto:  4";
		  m[j] = 1.076e+23/5.972e+24;
		  rad[j] = 2403/149668992.0;
		  rad0[j] = 2403/149668992.0;

		  fromEphem(291.57387, -21.93068, 23.22808,  3.247825,
				  5.27838811164282, -23.6213591, j);

		} else
		if (name.equals("Big Mimas")) {
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Epimetheus
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Epimetheus";
		  m[j] = 5.60e+17/5.972e+24;
		  // System.out.println("Epimetheus    "+(m[j]/m[0]));
		  r = 0.00101218;
		  rad[j] = 57/149668992.0;
		  rad0[j] = 57/149668992.0;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Janus
		  j=2;
		  col[j] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[j] = "Janus";
		  m[j] = 2.01e+17/5.972e+24;
		  // System.out.println("Janus         "+(m[j]/m[0]));
		  r = 0.00101251;
		  rad[j] = 89/149668992.0;
		  rad0[j] = 89/149668992.0;
		  th = Math.PI/3;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;

		  // Big Mimas
		  j=3;
		  col[j] = new Color((float)0.7,(float)0.3,(float)0.7);
		  OrbName[j] = "Big Mimas";
		  m[j] = 100*3.80e+19/5.972e+24;  // oversized
		  // System.out.println("Mimas         "+(m[j]/m[0]));
		  r = 186000/149668992.0;
		  rad[j] = 3*196/149668992.0;
		  rad0[j] = 3*196/149668992.0;
		  th = 3*Math.PI/4;
		  v = Math.sqrt(G*m[0]/r);
		  x[j] = r*Math.cos(th);
		  y[j] = r*Math.sin(th);
		  z[j] = 0;
		  vx[j] = -v*Math.sin(th);
		  vy[j] = v*Math.cos(th);
		  vz[j] = 0;
		} else
		if (name.equals("ephemeris Sun/Venus/Earth/Mars/2002AA29")) {
		  double vx_ideal, vy_ideal, vz_ideal;

		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Ephemeris Type: OBSERVER
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20;  angle format=DEG
		     */
		  // Earth
		  j=2;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.3665951, -10.6224931,   141.02,     54.21,  
			  	0.9889718384,   0.36893, j);

		  // Venus
		  j=3;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.6405232, -14.9946425,   178.22,     58.39,  
			  	1.6846828790,   3.16495, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic(280.94399, -23.54146,   106.65,      5.95, 
			  	1.88809615264125, -12.8809197, j);


		  // Asteroid 2002AA29
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "2002AA29";
		  m[j] = 2.01e+7/5.972e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(261.2825948,  41.6827265,   105.67,     23.78,  
			  0.1562891119,   3.35560, j);

		  // Asteroid Cruithne
		  j=6;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Cruithne";
		  m[j] = 2.01e+7/5.972e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(309.9406815, -13.8223055,   129.24,     11.73,  
			  	1.7035370294,  15.79940, j);

		  // Earth's Moon	
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.3483e+22/5.972e+24; // ??
		  rad[j] = 1738/149668992.0; // ??
		  rad0[j] = 1738/149668992.0; // ??

		  fromEphemEcliptic(120.1289564,  25.7426520,  1738.94,   -364.81,  
			  0.0027108083,  -0.00849, j);

		  // Asteroid 2010SO16
		  j=7;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.0);
		  OrbName[j] = "2010SO16";
		  m[j] = 2.01e+7/5.972e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic((1+16./60.+30.12/3600.)*(360./24.),
				  (45+35./60.+38.6/3600),   
				  84.59794,  73.53421, 0.41217832681651,
				  -3.9630506, j);

		  // Asteroid 2010TK7
		  j=8;
		  col[j] = new Color((float)1.0,(float)0.0,(float)1.0);
		  OrbName[j] = "2010TK7";
		  m[j] = 2.01e+7/5.972e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(277.46383,   1.14417, 115.6965,  -21.2247, 
				  0.73846108424890,   9.0936911, j);

		  // Jupiter
		  j=9;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<7; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<7; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<7; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<7; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<7; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<7; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<7; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<7; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("ephemeris Sun/Venus/Earth/Mars/Coorbital")) {
		  double vx_ideal, vy_ideal, vz_ideal;

		  startDate = 2011.0000;
		    /* notes on ephemeris
		     * Ephemeris Type: OBSERVER
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2011-01-01 00:00 UT
		     * Output quantities: 1, 3, 20;  angle format=DEG
		     */
		  // Earth
		  j=2;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 332982;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 280.93322, -23.05725, 152.4678,  11.71310, 
				  0.98335582706189,  -0.0184299, j);

		  // Venus
		  j=3;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(231.92787, -15.23681, 139.7883,  -33.7864, 
				  0.61592887973416,  13.1163543, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic(289.88030, -23.18002, 115.0531,  15.17762, 
				  2.37893907446077,   0.2044619, j);


		  // Asteroid 2002AA29
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "2002AA29";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(203.10625,  -1.04749, 168.2423,  -0.34422, 
				  0.43116602531202,  -0.4023626, j);

		  // Asteroid Cruithne    1986 TO
		  j=6;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Cruithne";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(254.18226, -14.62468, 207.8592,  -3.04346, 
				  1.10703843952804,  35.2901410, j);

		  // Earth's Moon	
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.9736e+24; // ??
		  rad[j] = 1738/149668992.0; // ??
		  rad0[j] = 1738/149668992.0; // ??

		  fromEphemEcliptic(236.17970, -22.80568, 2008.527,  -294.763, 
				  0.00253557933983,   0.0328537, j);

		  // Asteroid 2010SO16
		  j=7;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.0);
		  OrbName[j] = "2010SO16";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(346.85451,  17.53133, 68.97113,  106.8157, 
				  0.41111617964342,   1.2924246, j);

		  // Asteroid 2010TK7
		  j=8;
		  col[j] = new Color((float)1.0,(float)0.0,(float)1.0);
		  OrbName[j] = "2010TK7";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(234.34675,   0.19915, 168.5073,  86.85197, 
				  0.31366091315348,  12.5007974, j);

		  // Jupiter
		  j=9;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.13e+24/5.9736e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(357.19455,  -2.59546, 18.22312,  8.505550, 
				  5.08908060873202,  26.7028843, j);

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<7; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<7; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<7; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<7; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<7; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<7; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<7; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<7; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<7; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("ephemeris 2012DA14")) {
		  double vx_ideal, vy_ideal, vz_ideal;


		  startDate = 2013.0+43./365.25;
		    /* notes on ephemeris
		     * Ephemeris Type: OBSERVER
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2013-02-14 00:00 UT
		     * Output quantities: 1, 3, 20;  angle format=DEG
		     */
		  // Earth
		  j=2;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 332982;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		fromEphemEcliptic(327.54648, -13.09583, 142.7841,  50.97758,
				0.98757412095828,   0.3277251, j);

		  // Asteroid 2002AA29
		  j=3;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "2012DA14";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  fromEphemEcliptic(7.16872, -72.48621, 45.94514,  -196.474,
				  0.00662109023688,  -6.1995004, j);

		  // Earth's Moon	
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.9736e+24; // ??
		  rad[j] = 1738/149668992.0; // ??
		  rad0[j] = 1738/149668992.0; // ??

		  fromEphemEcliptic(9.68145,   7.59379, 1826.451,  635.4264,
				  0.00259515413258,   0.0612223, j);

		} else
		if (name.equals("ephemeris Sun/Earth/2010TK7")) {
		  double vx_ideal, vy_ideal, vz_ideal;

		  startDate = 2011.0000;
		    /* notes on ephemeris
		     * Ephemeris Type: OBSERVER
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2011-01-01 00:00 UT
		     * Output quantities: 1, 3, 20;  angle format=DEG
		     */
		  // Earth
		  j=2;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 332982;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  /*
		  fromEphemEcliptic( 281.13398, -23.03676, 152.3190,  12.69441, 
				  0.98333657325734,  -0.0352695, j);
				  */
		  fromEphemEcliptic( 280.93322, -23.05725, 152.4678,  11.71310, 
				  0.98335582706189,  -0.0184299, j);

		  // Earth's Moon	
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.9736e+24; // ??
		  rad[j] = 1738/149668992.0; // ??
		  rad0[j] = 1738/149668992.0; // ??

		  /*
		  fromEphemEcliptic(28.83553,  12.36705, 1941.071,  915.2078, 
				  0.00246094045937,  -0.0309029, j);
				  */
		  fromEphemEcliptic(236.17970, -22.80568, 2008.527,  -294.763, 
				  0.00253557933983,   0.0328537, j);

		  // Asteroid 2010TK7
		  j=3;
		  col[j] = new Color((float)1.0,(float)0.0,(float)1.0);
		  OrbName[j] = "2010TK7";
		  m[j] = 2.01e+7/5.9736e+24; // ??
		  rad[j] = 89/149668992.0; // ??
		  rad0[j] = 89/149668992.0; // ??

		  /*
		  fromEphemEcliptic(240.70584,  -6.51370, 156.4906,  -35.7405, 
				  1.25431231680012,  10.4827808, j);
				  */
		  fromEphemEcliptic(234.34675,   0.19915, 168.5073,  86.85197, 
				  0.31366091315348,  12.5007974, j);
		} else
		if (name.equals("Sun/Earth/Moon/L2/Occulter")) {
		  double vx_ideal, vy_ideal, vz_ideal;

		  startDate = 2005.1397;
		  // Earth
		  j=1;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.3665951, -10.6224931,   141.02,     54.21,  
			  	0.9889718384,   0.36893, j);

		  // Earth's Moon	
		  j=2;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.3483e+22/5.972e+24; // ??
		  // m[j] = 0;
		  rad[j] = 1738/149668992.0; // ??
		  rad0[j] = 1738/149668992.0; // ??

		  fromEphemEcliptic(120.1289564,  25.7426520,  1738.94,   -364.81,  
			  0.0027108083,  -0.00849, j);

		  // L2
		  j=3;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "L2";
		  m[j] = 1e-20; // ??
		  rad[j] = 1/149668992.0; // ??
		  rad0[j] = 1/149668992.0; // ??

		  double dx = x[1]-x[0];
		  double dy = y[1]-y[0];
		  double dz = z[1]-z[0];
		  double R = Math.sqrt(dx*dx + dy*dy + dz*dz);
		  r = R*Math.pow((m[1]+m[2])/(3*m[0]),1.0/3.0);
		  System.out.println("r = "+r);
		  r = (1.010036606167-1.0);  // from EarthSunLagrange.mod
		  System.out.println("r = "+r);
		  x[j] = -x[0]*r/R;
		  y[j] = -y[0]*r/R;
		  z[j] = -z[0]*r/R;
		  vx[j] = -vx[0]*r/R;
		  vy[j] = -vy[0]*r/R;
		  vz[j] = -vz[0]*r/R;

		  // Occulter
		  j=4;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Occulter";
		  m[j] = 1e-20; // ??
		  rad[j] = 1/149668992.0; // ??
		  rad0[j] = 1/149668992.0; // ??

		  dx = x[1]-x[0];
		  dy = y[1]-y[0];
		  dz = z[1]-z[0];
		  R = Math.sqrt(dx*dx + dy*dy + dz*dz);
		  r = 1.1*R*Math.pow((m[1]+m[2])/(3*m[0]),1.0/3.0);
		  r = (1.010036606167-1.0) + 70000.0/150000000.0;
		  x[j] = -x[0]*r/R;
		  y[j] = -y[0]*r/R;
		  z[j] = -z[0]*r/R;
		  vx[j] = -vx[0]*r/R;
		  vy[j] = -vy[0]*r/R;
		  vz[j] = -vz[0]*r/R;

		} else
		if (name.equals("Solar System (equatorial coords)")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphem( 334.3665951, -10.6224931,   141.02,     54.21, 
			         0.988971838437219,   0.3689321, j);

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.302e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphem(340.0904050, -10.0960565,   254.73,    118.50, 
			  	1.33489932319922, -19.0427712, j);


		  // Venus
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphem(325.6405232, -14.9946425,   178.22,     58.39, 
			  	1.68468287895148,   3.1649484, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphem(280.9439871, -23.5414616,   106.65,      5.95, 
			  	1.88809615264125, -12.8809197, j);


		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphem(197.3865635,  -5.7528433,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphem(112.8065220,  21.8608816,    -7.49,      1.42, 
			  	8.29689893407454,  19.0503106, j);

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphem(338.4727324,  -9.8249752,     7.99,      3.19, 
			  	21.0470705432051,   2.5567636, j);

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphem(318.1481876, -16.2379288,     5.26,      1.60, 
			  	31.0107494216548,  -8.3027453, j);

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.305e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphem(263.9748615, -15.2003184,     2.74,      0.26, 
			  	31.2623582636109, -26.6429753, j);

		  // Earth's Moon	
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphem(120.1289564,  25.7426520,  1738.94,   -364.81, 
			  0.002710808329734,  -0.0084885, j);
		  n=11;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Solar System (ecliptic coords)")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     * Table Settings: angle format=DEG, extra precision=YES
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.3,(float)0.3,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic(334.3665951, -10.6224931, 141.0209,  54.21393,
			  0.98897183843722,   0.3719535, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.0);
		  OrbName[j] = "Mercury";
		  m[j] = 3.302e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.0904050, -10.0960565, 254.7256,  118.5012,
			  1.33489932319922, -19.0352471, j);

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.8,(float)0.8,(float)0.8);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.6405232, -14.9946425, 178.2155,  58.39195,
			  1.68468287895148,   3.1712685, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic( 280.9439871, -23.5414616, 106.6466,  5.951025,
			  1.88809615264064, -12.8783882, j);

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.3865685,  -5.7528453, -7.93434,  3.858613,
			  4.72068156911959, -20.1828284, j);

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphemEcliptic(112.8064821,  21.8608933, -7.48646,  1.423825,
			  8.29690048890473,  19.0512944, j);

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphemEcliptic(338.4727324,  -9.8249752, 7.991979,  3.190581,
			  21.0470705467887,   2.5604348, j);

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphemEcliptic(318.1481876, -16.2379288, 5.260578,  1.603465,
			  31.0107494217556,  -8.2994256, j);

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphemEcliptic(263.9748615, -15.2003192, 2.735266,  0.262328,
			  31.2623584768463, -26.6416667, j);


		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  fromEphemEcliptic(129.8546375,   0.1202724, -4.78410,  1.900384,
			  28.5415895526608,  12.6172504, j);

		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(23.7990060,  -5.6826057, 0.889040,  0.752494,
			  97.5743584592652,  21.4768534, j);

		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(48.8523057,   5.8074557, 0.503690,  0.480068,
			  89.4057317268465,  27.0211031, j);

		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(120.1289564,  25.7426520, 1738.935,  -364.812,
			  0.00271080832973,  -0.0085686, j);

		  n=14;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Solar System (Horizons vectors)")) {
		  startDate = 2012.0000;
		    /* notes on ephemeris
		    Ephemeris Type [change] : VECTORS
		    Target Body [change] :	Venus [299] (etc.)
		    Coordinate Origin [change] : Solar System Barycenter (SSB) 
		    Time Span [change] : Start=2012-01-01, Stop=2012-01-02, Step=2d
		    Table Settings [change] : defaults
		    Display/Output [change] : default (formatted HTML)
		     */

		  double vx_ideal, vy_ideal, vz_ideal;

		  /*

		#1) The J2000 RA/DEC angles are "astrometric", meaning they
		include light-time as mentioned in the quantity description at
		the end of the output.

		R.A._(ICRF/J2000.0)_DEC = J2000.0 astrometric right ascension
		and declination of target center.  Corrected for light-time.
		Units: HMS (HH MM SS.ff) and DMS (DD MM SS.f)

		These values are typically used for reducing measurements
		against a star catalog in the ICRF/J2000 coordinate system.

		If you are initializing a dynamic simulation, you probably would
		want to use geometric state vectors available from a vector
		table type instead of the RA/DEC for astrometric image
		reduction. Geometric vectors are instantaneous states without
		correction for any observational effects.

		#2) "Deflecting bodies" refers to gravitational bending of light
		that affects apparent position in the sky, something also of
		interest to an observer on an observer table. Horizons considers
		light ray bending due to Sun and destination body.

		Dynamically, Horizons presents a numerically integrated n-body
		perturbed system.

		#3) We don't use G separately because it is usually known less
		well than the the GM mass parameters solved for when estimating
		the solar system planetary ephemeris solution. Here are the
		full-precision  GM's Horizons uses internally:

		Units: km^3/s^2

		Planetary systems (1=Mercury, etc., 10=Sun)
		    BODY1_GM       = ( 2.2032080486417923D+04 )
		    BODY2_GM       = ( 3.2485859882645978D+05 )
		    BODY3_GM       = ( 4.0350323347908701D+05 )
		    BODY4_GM       = ( 4.2828314258067119D+04 )
		    BODY5_GM       = ( 1.2671276785779600D+08 )
		    BODY6_GM       = ( 3.7940626061137281D+07 )
		    BODY7_GM       = ( 5.7945490070718741D+06 )
		    BODY8_GM       = ( 6.8365340638792608D+06 )
		    BODY9_GM       = ( 9.8160088770700440D+02 )
		    BODY10_GM      = ( 1.3271244001798698D+11 )

		Planets only (199=Mercury, etc., 301=Moon
		    BODY199_GM     = ( 2.2032080486417923D+04 )
		    BODY299_GM     = ( 3.2485859882645978D+05 )
		    BODY301_GM     = ( 0.4902800582147764D+04 )
		    BODY399_GM     = (  .3986004328969392D+06 )
		    BODY499_GM     = ( 4.282837464930098D+04  )
		    BODY599_GM     = ( 1.266865343445600D+08  )
		    BODY699_GM     = ( 3.793120782046435D+07  )
		    BODY799_GM     = ( 5.793950209634619D+06  )
		    BODY899_GM     = ( 6.835107010942784D+06  )
		    BODY999_GM     = ( 8.731741146667376D+02  )

		Asteroids (1=Ceres, 2=Pallas, 3=Vesta)
		    BODY2000001_GM = (  6.32D+1 )   
		    BODY2000002_GM = (  1.43D+1 )   
		    BODY2000004_GM = (  1.78D+1 )   

		System documentation is here:
		 http://ssd.jpl.nasa.gov/?horizons_doc

		Regards,
		-----------------------------------------------------------------
		Jon Giorgini              |  Navigation & Mission Design Section
		Senior Analyst            |  Solar System Dynamics Group
		Jon.Giorgini@jpl.nasa.gov |  Jet Propulsion Laboratory
		-----------------------------------------------------------------
		*/

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.3,(float)0.3,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = -1.704612905998195E-01;
		  y[j] = 9.676758607337962E-01;
		  z[j] = -3.140642423792612E-05;
		  vx[j] = -1.722905169624698E-02*365.25;
		  vy[j] = -3.001024883870811E-03*365.25;
		  vz[j] = 2.627266603191336E-07*365.25;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Sun";
		  m[j] = 1.3271244001798698E+11 / .3986004328969392E+06 ;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  x[j] = -3.241859398499088E-03;
		  y[j] = -1.331449770492458E-03;
		  z[j] = -8.441430972210388E-07;
		  vx[j] = 4.270617154820447E-06*365.25;
		  vy[j] = -4.648506431568692E-06*365.25;
		  vz[j] = -8.469657867642489E-08*365.25;
			  
		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.0);
		  OrbName[j] = "Mercury";
		  m[j] = 2.2032080486417923E+04 / .3986004328969392E+06 ;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  x[j] = -3.824910108111409E-01;
		  y[j] = -1.955727022061594E-01;
		  z[j] = 1.892637411059862E-02;
		  vx[j] = 7.029877499006405E-03*365.25;
		  vy[j] = -2.381780604663742E-02*365.25;
		  vz[j] = -2.590381459216828E-03*365.25;
			  
		  // Venus
		  j=2;
		  col[j] = new Color((float)0.8,(float)0.8,(float)0.8);
		  OrbName[j] = "Venus";
		  m[j] = 3.2485859882645978E+05 / .3986004328969392E+06 ;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  x[j] = 7.211147749926723E-01;
		  y[j] = 3.334025180138600E-02;
		  z[j] =-4.133082682493956E-02;
		  vx[j] =-1.045793358516289E-03*365.25;
		  vy[j] = 2.010665107676625E-02*365.25;
		  vz[j] = 3.360587977875350E-04*365.25;
			  
		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Mars";
		  m[j] = 4.2828314258067119D+04 / .3986004328969392E+06 ;

		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  x[j] = -1.192725241298415E+00;
		  y[j] = 1.148990485621534E+00;
		  z[j] = 5.330857335041436E-02;
		  vx[j] = -9.195012836122981E-03*365.25;
		  vy[j] = -8.871670960023885E-03*365.25;
		  vz[j] = 4.000329706845314E-05*365.25;
			  
		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1.2671276785779600E+08 / .3986004328969392E+06 ;
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  x[j] = 3.752622496696632E+00;
		  y[j] = 3.256207159994215E+00;
		  z[j] = -9.757709767384923E-02;
		  vx[j] = -5.035554237289496E-03*365.25;
		  vy[j] = 6.060385207824578E-03*365.25;
		  vz[j] = 8.751352649528277E-05*365.25;
			  
		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 3.7940626061137281E+07 / .3986004328969392E+06 ;
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  x[j] = -8.943507237200967E+00;
		  y[j] = -3.720738784092123E+00;
		  z[j] = 4.206135705475783E-01;
		  vx[j] = 1.842570887744860E-03*365.25;
		  vy[j] = -5.163340264906132E-03*365.25;
		  vz[j] = 1.648276691787503E-05*365.25; 
			  
		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 5.7945490070718741E+06 / .3986004328969392E+06 ;
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  x[j] = 2.003510615298455E+01;
		  y[j] = 1.205184752774219E+00;
		  z[j] = -2.550883982941838E-01;
		  vx[j] = -2.649722266077889E-04*365.25;
		  vy[j] = 3.742642248006496E-03*365.25;
		  vz[j] = 1.735555169285604E-05*365.25; 
			  
		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 6.8365340638792608E+06 / .3986004328969392E+06 ;
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  x[j] = 2.601428919232999E+01;
		  y[j] = -1.493950125368399E+01;
		  z[j] = -2.918668092864814E-01;
		  vx[j] = 1.542818728068733E-03*365.25;
		  vy[j] = 2.740646317666675E-03*365.25;
		  vz[j] = -9.236392136662484E-05*365.25; 
			  
		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 9.8160088770700440E+02 / .3986004328969392E+06 ;
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  x[j] = 3.948677256557395E+00;
		  y[j] = -3.183131323838405E+01;
		  z[j] = 2.263946936689972E+00;
		  vx[j] = 3.176212360111409E-03*365.25;
		  vy[j] = -2.386409032632784E-04*365.25;
		  vz[j] = -9.052717567085832E-04*365.25; 
			  
		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 1.000e+12/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  x[j] = -2.031219175446611E+01;
		  y[j] = 2.409419973607690E+01;
		  z[j] = -9.557240473047473E+00;
		  vx[j] = -2.295339385176562E-04*365.25;
		  vy[j] = 1.137515487337339E-03*365.25;
		  vz[j] = -2.514046300825681E-04*365.25; 
			  
		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 1.000e+12/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  x[j] = 8.724518827843193E+01;
		  y[j] = 3.490612400063656E+01;
		  z[j] = -2.220965616494556E+01;
		  vx[j] = -3.238181484961516E-04*365.25;
		  vy[j] = 9.212495072283335E-04*365.25;
		  vz[j] = 9.034180064505134E-04*365.25; 
			  
		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 1.000e+12/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  x[j] = 5.179754709713021E+01;
		  y[j] = 6.753749434759575E+01;
		  z[j] = -1.797070297165055E+01;
		  vx[j] = -2.367474706303894E-03*365.25;
		  vy[j] = 7.730437765658292E-04*365.25;
		  vz[j] = 1.583556037221544E-04*365.25; 
			  
		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 0.4902800582147764E+04 / .3986004328969392E+06 ;
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  x[j] = -1.678007490717977E-01;
		  y[j] = 9.679993528788238E-01;
		  z[j] = 1.948577104980048E-04;
		  vx[j] = -1.727918910480512E-02*365.25;
		  vy[j] = -2.442856789591644E-03*365.25;
		  vz[j] = -1.846888114063579E-05*365.25; 
			  
		  n=14;

		} else
		if (name.equals("Moon Eccentricity (ecliptic coords)")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.3022e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.09041, -10.09606,   254.73,    118.50, 
			  	1.33489932319922, -19.0427712, j);

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.64052, -14.99464,   178.22,     58.39, 
			  	1.68468287895148,   3.1649484, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic(280.94399, -23.54146,   106.65,      5.95, 
			  	1.88809615264125, -12.8809197, j);

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphemEcliptic(112.80648,  21.86089,    -7.49,      1.42, 
			  	8.29690048890332,  19.0503238, j);

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphemEcliptic(338.47273,  -9.82498,     7.99,      3.19, 
			  	21.0470705432051,   2.5567636, j);

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphemEcliptic(318.14819, -16.23793,     5.26,      1.60, 
			  	31.0107494216548,  -8.3027453, j);

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphemEcliptic(263.97486, -15.20032,     2.74,      0.26, 
			  	31.2623582636109, -26.6429753, j);


		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 2.2e+14/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  fromEphemEcliptic(129.85464,   0.12027,    -4.78,      1.90, 
			  	28.5415895526607,  12.6144319, j);

		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 1.67e+22/5.972e+24; // ??
		  rad[j] = 1300/149668992.0; // ??
		  rad0[j] = 1300/149668992.0; // ??

		  fromEphemEcliptic(23.79895,  -5.68257,     0.89,      0.75, 
			  	97.5764534858654,  21.4755946, j);

		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 1e+21/5.972e+24; // ??
		  rad[j] = 1400/149668992.0; // ??
		  rad0[j] = 1400/149668992.0; // ??

		  fromEphemEcliptic(48.85224,   5.80750,     0.50,      0.48, 
			  	89.4067339131441,  27.0225597, j);

		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.347e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(120.12896,  25.74265,  1738.94,   -364.81, 
			  0.002710808329734,  -0.0084885, j);

		  n=14;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("3-Body Solar System (ecliptic coords)")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;


		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.3022e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.09041, -10.09606,   254.73,    118.50, 
			  	1.33489932319922, -19.0427712, j);

		  // Jupiter
		  j=2;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  m[2] += 0.817*Math.pow(5.204/0.723,2);
		  m[2] += 1.000*Math.pow(5.204/1.000,2);
		  m[2] += 0.108*Math.pow(5.204/1.524,2);
		  m[2] += 317.9*Math.pow(5.204/5.204,2);
		  m[2] += 95.19*Math.pow(5.204/9.582,2);
		  m[2] += 14.54*Math.pow(5.204/19.23,2);
		  m[2] += 17.15*Math.pow(5.204/30.10,2);
		  m[2] += 0.002*Math.pow(5.204/39.48,2);
		  
		  m[2] *= 100;

		  n=3;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("3-Body ideal Solar System")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;


		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.3022e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.09041, -10.09606,   254.73,    118.50, 
			  	1.33489932319922, -19.0427712, j);

		  // Jupiter
		  j=2;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  m[2] += 0.817*Math.pow(5.204/0.723,2);
		  m[2] += 1.000*Math.pow(5.204/1.000,2);
		  m[2] += 0.108*Math.pow(5.204/1.524,2);
		  m[2] += 317.9*Math.pow(5.204/5.204,2);
		  m[2] += 95.19*Math.pow(5.204/9.582,2);
		  m[2] += 14.54*Math.pow(5.204/19.23,2);
		  m[2] += 17.15*Math.pow(5.204/30.10,2);
		  m[2] += 0.002*Math.pow(5.204/39.48,2);
		  
		  m[2] *= 10;

		  n=3;

		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j+=2) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j+=2) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j+=2) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j+=2) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j+=2) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j+=2) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j+=2) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }

		  r = 5.204267;
		  v = Math.sqrt(G*m[0]/r);
		  System.out.println("r = "+r+", v = "+v);

		  double dt = 1e-5;
		  double omega = v/r;
		  r = Math.pow((1+(m[2]/m[0]))*(1+(m[2]/m[0]))
			                   *(1-Math.cos(omega*dt))
			       /(m[0]*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] = -(m[2]/m[0])*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] =  (m[2]/m[0])*v*Math.sin(omega*dt/2);
		  vy[0] = -(m[2]/m[0])*v*Math.cos(omega*dt/2);
		  vz[0] = 0;
		  x[2] = r;
		  y[2] = 0;
		  z[2] = 0;
		  vx[2] = -v*Math.sin(omega*dt/2);
		  vy[2] =  v*Math.cos(omega*dt/2);
		  vz[2] = 0;
		  System.out.println("r = "+r+", v = "+v);

		    double ax[]=new double[n], 
			   ay[]=new double[n], 
			   az[]=new double[n];
		    for (j=0; j<n; j+=2) {
			ax[j] = 0; ay[j] = 0; az[j] = 0;
			for (i=0; i<n; i+=2) {
			    if (i != j) {
				r = (x[i]-x[j])*(x[i]-x[j])
				  + (y[i]-y[j])*(y[i]-y[j])
				  + (z[i]-z[j])*(z[i]-z[j]);
				r = Math.sqrt(r);
				double r3 = r*r*r;
				ax[j] -= G * m[i] * (x[j] - x[i])/r3;
				ay[j] -= G * m[i] * (y[j] - y[i])/r3;
				az[j] -= G * m[i] * (z[j] - z[i])/r3;
			    }
			}
		    }
		    for (j=0; j<n; j+=2) {
			vx[j] -= ax[j] * dt/2;
			vy[j] -= ay[j] * dt/2;
			vz[j] -= az[j] * dt/2;
		    }

		} else
		if (name.equals("Solar System restart")) {
			// copy and paste from output of a previous run
startDate = 830005.5583918813; x[0] = -20.280460155178474; y[0] = -39.70297252890547; z[0] = 0.03311336469121886; vx[0] = 0.001026685755731321; vy[0] = -0.0018344520666267838; vz[0] = -6.604507081130189E-6; x[1] = -19.830102273181815; y[1] = -39.65800355245801; z[1] = 0.01743231327733948; vx[1] = -1.8726000725598027; vy[1] = 8.2736735900242; vz[1] = -0.6358528203485354; x[2] = -20.55711643380208; y[2] = -40.37567090264283; z[2] = 0.07258344951286332; vx[2] = 6.787169631523446; vy[2] = -2.788378210394976; vz[2] = 4.433569994449268E-4; x[3] = -21.027288629693352; y[3] = -39.014847716223244; z[3] = 0.0657913292425746; vx[3] = -4.168569802302124; vy[3] = -4.561565300197638; vz[3] = 0.29516307109082; x[4] = -19.193187603611886; y[4] = -40.97151627345379; z[4] = -0.08343779057696413; vx[4] = 3.7521162976963764; vy[4] = 2.674227831538371; vz[4] = -0.06624520941615569; x[5] = -15.789885173119101; y[5] = -36.75900030178205; z[5] = -0.08351829259803223; vx[5] = -1.4015017357187294; vy[5] = 2.269666813637597; vz[5] = 0.009755497614733247; x[6] = -27.9842647342325; y[6] = -45.510557950848366; z[6] = 0.4424114686433411; vx[6] = 1.2991096184339468; vy[6] = -1.530295085281977; vz[6] = -0.022958165259026445; x[7] = -4.418386300205002; y[7] = -29.91036154834941; z[7] = -0.20620648438340308; vx[7] = -0.8526733802025235; vy[7] = 1.2038677233698318; vz[7] = 0.030369021582256434; x[8] = -8.12348961665325; y[8] = -11.789831750766366; z[8] = -0.38955969998033263; vx[8] = -1.0342462979685096; vy[8] = 0.4547038063716627; vz[8] = 0.03503557355732272; x[9] = -26.874445131980927; y[9] = 17.36780757115175; z[9] = 12.358629839842267; vx[9] = -0.6719392443074411; vy[9] = -0.03635714947791956; vz[9] = -0.022225621661053453; x[10] = -22.583983638693915; y[10] = -32.10472262111799; z[10] = 0.8836341478413896; vx[10] = 0.2241171198037648; vy[10] = 0.8398719066169503; vz[10] = -0.38332694895471897; x[11] = 57.16906361919453; y[11] = -60.82746965846321; z[11] = -47.40186366037393; vx[11] = 0.3469785578182357; vy[11] = 0.34041964188097346; vz[11] = 0.1441359837019879; x[12] = -40.569167408994986; y[12] = -690.4338159532188; z[12] = 114.17893690898885; vx[12] = 0.1077828491878673; vy[12] = -0.08240328619297937; vz[12] = 6.780414850835381E-4; x[13] = -21.025235219205737; y[13] = -39.01630070570949; z[13] = 0.06590850449049435; vx[13] = -4.045317370389547; vy[13] = -4.378415100452285; vz[13] = 0.29026138179974087;

startDate = 2005.1397; x[0] = 8.7635778286387000E-01; y[0] = -4.5832531038306580E-01; z[0] = 4.9345524778229840E-04; vx[0] = 3.0117540477324063E00; vy[0] = 5.5907975678619530E00; vz[0] = -4.6522156642589960E-03; x[1] = 1.2356787736196986E00; y[1] = -5.0373408905590600E-01; z[1] = -3.6140545464471070E-02; vx[1] = 2.3107409428760524E00; vy[1] = 1.6247549061000170E01; vz[1] = 9.2042465049737740E-01; x[2] = 1.3433731309147838E00; y[2] = -1.0160623794287897E00; z[2] = -3.3500371310835550E-02; vx[2] = 8.6268151080836760E00; vy[2] = 1.0307636391025282E01; vz[2] = -2.6683196441574040E-01; x[3] = 0.0000000000000000E00; y[3] = 0.0000000000000000E00; z[3] = 0.0000000000000000E00; vx[3] = 0.0000000000000000E00; vy[3] = 0.0000000000000000E00; vz[3] = 0.0000000000000000E00; x[4] = 3.2862010290621250E-01; y[4] = -1.8592262288368049E00; z[4] = -1.3919036959263886E-02; vx[4] = 7.9654200940571910E00; vy[4] = 4.1684488800121010E00; vz[4] = -1.5175654250931148E-01; x[5] = -4.4823067687350795E00; y[5] = -1.4757913335395973E00; z[5] = 1.2570784873150037E-01; vx[5] = 3.4933339816746063E00; vy[5] = 3.0093835510893037E00; vz[5] = -2.9350595655834866E-03; x[6] = -2.9847796568228593E00; y[6] = 7.7414236857377930E00; z[6] = 2.7277973617914064E-03; vx[6] = 1.0611267752174895E00; vy[6] = 4.7161074047143070E00; vz[6] = 8.7801317082008000E-02; x[7] = 1.9291738675363880E01; y[7] = -8.4107567870276280E00; z[7] = -2.5916713061253605E-01; vx[7] = 3.5697934405776760E00; vy[7] = 6.8386136848086680E00; vz[7] = -8.0688490491631630E-03; x[8] = 2.2177619316588240E01; y[8] = -2.1675304385358956E01; z[8] = -3.0999083771161118E-02; vx[8] = 3.8118767736732844E00; vy[8] = 6.4060830926590590E00; vz[8] = -4.3739920967907686E-02; x[9] = -3.1666466152527715E00; y[9] = -3.0782103610040465E01; z[9] = 4.4462895494111080E00; vx[9] = 4.1800766671740560E00; vy[9] = 5.2338853325083130E00; vz[9] = -3.0536153163076960E-01; x[10] = -1.8290611326635010E01; y[10] = 2.0117182085571482E01; z[10] = -8.6818694085135380E00; vx[10] = 2.7488271090415686E00; vy[10] = 6.1986176595111660E00; vz[10] = -1.7604719274159075E-01; x[11] = 8.8840499492982080E01; y[11] = 3.2079076720056065E01; z[11] = -2.4483928637076694E01; vx[11] = 2.9171351915136160E00; vy[11] = 5.9342152245470470E00; vz[11] = 3.0536574822038800E-01; x[12] = 5.8527967676069980E01; y[12] = 6.5031226638434670E01; z[12] = -1.8411426631991443E01; vx[12] = 2.1795869330979720E00; vy[12] = 5.8887601869490400E00; vz[12] = 4.7051640055409294E-02; x[13] = -1.2256421501131163E-03; y[13] = 2.4062043378044245E-03; z[13] = 2.3762113784868769E-04; vx[13] = -1.8162348898704145E-01; vy[13] = -9.4424541613104890E-02; vz[13] = -1.0721161646389670E-03;



		  // Earth
		  j=3;
		  col[j] = new Color((float)0.0,(float)0.0,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.5);
		  OrbName[j] = "Mercury";
		  m[j] = 3.302e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.0,(float)1.0,(float)0.0);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.0,(float)0.0);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??


		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  n=14;

		} else
		if (name.equals("Solar System")) {
			// identical to "Solar System (ecliptic coords)"
			// just a different label
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     * Table Settings: angle format=DEG, extra precision=YES
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Earth
		  j=3;
		  col[j] = new Color((float)0.3,(float)0.3,(float)1.0);
		  OrbName[j] = "Earth";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  J2[j] = 0.0; 

		  x[j] = 0;
		  y[j] = 0;
		  z[j] = 0;
		  vx[j] = 0;
		  vy[j] = 0;
		  vz[j] = 0;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic(334.3665951, -10.6224931, 141.0209,  54.21393,
			  0.98897183843722,   0.3719535, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Mercury
		  j=1;
		  col[j] = new Color((float)1.0,(float)0.5,(float)0.0);
		  OrbName[j] = "Mercury";
		  m[j] = 3.302e+23/59.72e+23;
		  rad[j] = 2440/149668992;
		  rad0[j] = 2440/149668992;

		  fromEphemEcliptic(340.0904050, -10.0960565, 254.7256,  118.5012,
			  1.33489932319922, -19.0352471, j);

		  // Venus
		  j=2;
		  col[j] = new Color((float)0.8,(float)0.8,(float)0.8);
		  OrbName[j] = "Venus";
		  m[j] = 0.817;
		  rad[j] = 0.0000413;
		  rad0[j] = 0.0000413;

		  fromEphemEcliptic(325.6405232, -14.9946425, 178.2155,  58.39195,
			  1.68468287895148,   3.1712685, j);


		  // Mars
		  j=4;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Mars";
		  m[j] = 0.108;
		  rad[j] = 0.0000227;
		  rad0[j] = 0.0000227;

		  fromEphemEcliptic( 280.9439871, -23.5414616, 106.6466,  5.951025,
			  1.88809615264064, -12.8783882, j);

		  // Jupiter
		  j=5;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.3865635,  -5.7528433, -7.93364,  3.858334,
			  4.72068178264637, -20.1834631, j);

		  // Saturn
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphemEcliptic(112.8064821,  21.8608933, -7.48646,  1.423825,
			  8.29690048890473,  19.0512944, j);

		  // Uranus
		  j=7;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphemEcliptic(338.4727324,  -9.8249752, 7.991979,  3.190581,
			  21.0470705467887,   2.5604348, j);

		  // Neptune
		  j=8;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphemEcliptic(318.1481876, -16.2379288, 5.260578,  1.603465,
			  31.0107494217556,  -8.2994256, j);

		  // Pluto
		  j=9;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphemEcliptic(263.9748615, -15.2003192, 2.735266,  0.262328,
			  31.2623584768463, -26.6416667, j);


		  // Comet Halley
		  j=10;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  fromEphemEcliptic(129.8546375,   0.1202724, -4.78410,  1.900384,
			  28.5415895526608,  12.6172504, j);

		  // Eris
		  j=11;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(23.7990060,  -5.6826057, 0.889040,  0.752494,
			  97.5743584592652,  21.4768534, j);

		  // Sedna
		  j=12;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(48.8523057,   5.8074557, 0.503690,  0.480068,
			  89.4057317268465,  27.0211031, j);

		  // Earth's Moon	
		  j=13;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Moon";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(120.1289564,  25.7426520, 1738.935,  -364.812,
			  0.00271080832973,  -0.0085686, j);

		  n=14;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Outer Solar System")) {
		  startDate = 2005.1397;
		    /* notes on ephemeris
		     * Observer Location: Geocentric [Code=500]
		     * Date: 2005-02-21 00:00 UT
		     * Output quantities: 1, 3, 20
		     */
		  double vx_ideal, vy_ideal, vz_ideal;

		  // Sun
		  j=0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  fromEphemEcliptic( 334.36660, -10.62249,   141.02,     54.21, 
			  	 0.988971838437219,   0.3689321, j);
		  System.out.println("1 AU = "+Math.sqrt(x[j]*x[j]+y[j]*y[j]+z[j]*z[j]));

		  // Jupiter
		  j=1;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Jupiter";
		  m[j] = 1898.6e+24/5.972e+24; // ??
		  rad[j] = 69911/149668992.0; // ??
		  rad0[j] = 69911/149668992.0; // ??

		  fromEphemEcliptic(197.38656,  -5.75284,    -7.93,      3.86, 
			  	4.72068178258518, -20.1840422, j);

		  // Saturn
		  j=2;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 5.68461e+26/5.972e+24; // ??
		  rad[j] = 60268/149668992.0; // ??
		  rad0[j] = 60268/149668992.0; // ??

		  fromEphemEcliptic(112.80648,  21.86089,    -7.49,      1.42, 
			  	8.29690048890332,  19.0503238, j);

		  // Uranus
		  j=3;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.6);
		  OrbName[j] = "Uranus";
		  m[j] = 86.832e+24/5.972e+24; // ??
		  rad[j] = 25559/149668992.0; // ??
		  rad0[j] = 25559/149668992.0; // ??

		  fromEphemEcliptic(338.47273,  -9.82498,     7.99,      3.19, 
			  	21.0470705432051,   2.5567636, j);

		  // Neptune
		  j=4;
		  col[j] = new Color((float)0.6,(float)1.0,(float)1.0);
		  OrbName[j] = "Neptune";
		  m[j] = 102.43e+24/5.972e+24; // ??
		  rad[j] = 24624/149668992.0; // ??
		  rad0[j] = 24624/149668992.0; // ??

		  fromEphemEcliptic(318.14819, -16.23793,     5.26,      1.60, 
			  	31.0107494216548,  -8.3027453, j);

		  // Pluto
		  j=5;
		  col[j] = new Color((float)0.6,(float)1.0,(float)0.6);
		  OrbName[j] = "Pluto";
		  m[j] = 1.314e+22/5.972e+24; // ??
		  rad[j] = 870/149668992.0; // ??
		  rad0[j] = 870/149668992.0; // ??

		  fromEphemEcliptic(263.97486, -15.20032,     2.74,      0.26, 
			  	31.2623582636109, -26.6429753, j);


		  /*
		  // Comet Halley
		  j=6;
		  col[j] = new Color((float)1.0,(float)1.0,(float)1.0);
		  OrbName[j] = "Comet Halley";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 16/149668992.0; // ??
		  rad0[j] = 16/149668992.0; // ??

		  fromEphemEcliptic(129.85464,   0.12027,    -4.78,      1.90, 
			  	28.5415895526607,  12.6144319, j);
		  */

		  // Eris
		  j=6;
		  col[j] = new Color((float)1.0,(float)0.4,(float)0.4);
		  OrbName[j] = "Eris";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(23.79895,  -5.68257,     0.89,      0.75, 
			  	97.5764534858654,  21.4755946, j);

		  // Sedna
		  j=7;
		  col[j] = new Color((float)0.4,(float)1.0,(float)0.6);
		  OrbName[j] = "Sedna";
		  m[j] = 7.349e+22/5.972e+24; // ??
		  rad[j] = 1737/149668992.0; // ??
		  rad0[j] = 1737/149668992.0; // ??

		  fromEphemEcliptic(48.85224,   5.80750,     0.50,      0.48, 
			  	89.4067339131441,  27.0225597, j);

		  n=8;

		  /*
		  double M;
		  double mx, my, mz;
		  M = 0;
		  for (j=0; j<n; j++) { M  += m[j]; }
		  mx = 0; for (j=0; j<n; j++) { mx += m[j]*x[j]; } 
		  my = 0; for (j=0; j<n; j++) { my += m[j]*y[j]; } 
		  mz = 0; for (j=0; j<n; j++) { mz += m[j]*z[j]; } 
		  for (j=0; j<n; j++) {x[j] -=mx/M; y[j] -=my/M; z[j] -=mz/M;}

		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vx[j]; }
		  for (j=0; j<n; j++) { vx[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vy[j]; }
		  for (j=0; j<n; j++) { vy[j] -= mv/M; }
		  mv = 0;
		  for (j=0; j<n; j++) { mv += m[j]*vz[j]; }
		  for (j=0; j<n; j++) { vz[j] -= mv/M; }
		  */
		} else
		if (name.equals("Saturn/Janus")) {
		  int jj;
		  double v1, th1;

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  // J2[0] = 0.0; 
		  J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Janus
		  col[1] = new Color((float)0.3,(float)1.0,(float)0.3);
		  m[1] = 3.366e-7;
		  r = 0.00101251;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 5.9e-7;
		  rad0[1] = 5.9e-7;

		  for (j=0; j<n/16; j++) {
		   for (k=0; k<16; k++) {
		    r = 0.00101251;
		    th = Math.PI/3;
		    th1 = (2*Math.PI*k)/16;
		    v = Math.sqrt(G*m[0]/r);
		    v1 = 0.001*v*(j+1)*16/n;
		    jj = 2 + 16*j + k;
		    if (jj>=n) break;
		    x[jj] = r*Math.cos(th);
		    y[jj] = r*Math.sin(th);
		    z[jj] = 0.000001*(Math.random()-0.5)*r;
		    vx[jj] = -v*Math.sin(th) - v1*Math.sin(th+th1);
		    vy[jj] = v*Math.cos(th) + v1*Math.cos(th+th1);
		    vz[jj] = 0;
		    m[jj] = 0;
		    rad[jj] = rad[0]/400000;
		    rad0[jj] = 0;
		    v1 = j*16.0/n;
		    double vmin = v1>1-v1?v1:1-v1;
		    col[jj] = new Color((float)((1-v1)/vmin),0,(float)(v1/vmin));
		   }
		  }
	      /*
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("Saturn/Janus/L5")) {
		  int jj;
		  double v1, th1;

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Janus
		  col[1] = new Color((float)0.3,(float)1.0,(float)0.3);
		  m[1] = 3.366e-7;
		  r = 0.00101251;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);
		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 5.9e-7;
		  rad0[1] = 5.9e-7;

		  // remaining points at L5
		  for (j=2; j<n; j++) {
		    r = 0.00101251;
		    th = Math.PI/3;
		    v = Math.sqrt(G*m[0]/r);
		    x[j] = r*Math.cos(th);
		    y[j] = r*Math.sin(th);
		    z[j] = 0.000001*(Math.random()-0.5)*r;
		    vx[j] = -v*Math.sin(th);
		    vy[j] = v*Math.cos(th);
		    vz[j] = 0;
		    m[j] = 0;
		    rad[j] = rad[0]/4000;
		    rad0[j] = 0;
		    col[j] = Color.white;
		  }
	      /*
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<2; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */
		} else
		if (name.equals("Epimetheus/Janus separated")) {
		  double e;  // this is the eccentricity

		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;
		  J2[0] = 0.0; // J2[0] = 1.6297e-2;
		  r = 0; // 9.539;  // saturn radius in au
		  v = 0; // 2.05;   // saturn velocity in au/year
		  x[0] = r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = v;
		  vz[0] = 0;

		  // Epimetheus
		  col[1] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[1] = "Epimetheus";
		  m[1] = 9.377e-8;
		  r = 0.00101120;
		  th = 0;
		  v = Math.sqrt(G*m[0]/r);

		  // e = 0.009;
		  e = 0.000;
		  r /= 1+e;
		  v *= (1+e);

		  x[1] = r*Math.cos(th);
		  y[1] = r*Math.sin(th);
		  z[1] = 0;
		  vx[1] = -v*Math.sin(th);
		  vy[1] = v*Math.cos(th);
		  vz[1] = 0;
		  rad[1] = 3.8e-7;
		  rad0[1] = 3.8e-7;
		  // rad[1] = rad[0]/20;

		  // Janus
		  col[2] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[2] = "Janus";
		  m[2] = 3.366e-7;
		  r = 0.00101251;
		  th = Math.PI;
		  v = Math.sqrt(G*m[0]/r);

		  // e = 0.007;
		  e = 0.000;
		  r /= 1-e;
		  v *= (1-e);

		  x[2] = r*Math.cos(th);
		  y[2] = r*Math.sin(th);
		  z[2] = 0;
		  vx[2] = -v*Math.sin(th);
		  vy[2] = v*Math.cos(th);
		  vz[2] = 0;
		  rad[2] = 5.9e-7;
		  rad0[2] = 5.9e-7;
		  // rad[2] = rad[0]/20;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("perfect Saturn/Janus")) {
		    // SEE perfectCircular.pdf for explanation
		  // include Saturn
		  col[0] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[0] = "Saturn";
		  m[0] = 95.2; // saturn
		  rad[0] = 0.0004027;
		  rad0[0] = 0.0004027;

		  // Janus
		  col[1] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[1] = "Janus";
		  m[1] = 3.366e-7;
		  rad[1] = 5.9e-7;
		  rad0[1] = 5.9e-7;
		  r = 0.00101251;
		  v = Math.sqrt(G*m[0]/r);
		  System.out.println("r = "+r+", v = "+v);

		  double dt = 1e-5;
		  double omega = v/r;
		  r = Math.pow((1+(m[1]/m[0]))*(1+(m[1]/m[0]))
			                   *(1-Math.cos(omega*dt))
			       /(m[0]*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] = -(m[1]/m[0])*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] =  (m[1]/m[0])*v*Math.sin(omega*dt/2);
		  vy[0] = -(m[1]/m[0])*v*Math.cos(omega*dt/2);
		  vz[0] = 0;
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = -v*Math.sin(omega*dt/2);
		  vy[1] =  v*Math.cos(omega*dt/2);
		  vz[1] = 0;
		  System.out.println("r = "+r+", v = "+v);

		    double ax[]=new double[n], 
			   ay[]=new double[n], 
			   az[]=new double[n];
		    for (j=0; j<n; j++) {
			ax[j] = 0; ay[j] = 0; az[j] = 0;
			for (i=0; i<n; i++) {
			    if (i != j) {
				r = (x[i]-x[j])*(x[i]-x[j])
				  + (y[i]-y[j])*(y[i]-y[j])
				  + (z[i]-z[j])*(z[i]-z[j]);
				r = Math.sqrt(r);
				double r3 = r*r*r;
				ax[j] -= G * m[i] * (x[j] - x[i])/r3;
				ay[j] -= G * m[i] * (y[j] - y[i])/r3;
				az[j] -= G * m[i] * (z[j] - z[i])/r3;
			    }
			}
		    }
		    for (j=0; j<n; j++) {
			vx[j] -= ax[j] * dt/2;
			vy[j] -= ay[j] * dt/2;
			vz[j] -= az[j] * dt/2;
		    }

		} else
		if (name.equals("Jupiter/Saturn resonance")) {
		  // Sun
		  j = 0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;

		  // Jupiter
		  j = 1;
		  col[j] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[j] = "Jupiter";
		  m[j] = 318;
		  rad[j] = 0.0004758;
		  rad0[j] = 0.0004758;
		  r = 5.203;  // jupiter radius in au
		  v = 2.76;   // jupiter velocity in au/year

		  double dt = 1e-1;
		  double omega = v/r;
		  r = Math.pow((1+(m[1]/m[0]))*(1+(m[1]/m[0]))
			                   *(1-Math.cos(omega*dt))
			       /(m[0]*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] = -(m[1]/m[0])*r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] =  (m[1]/m[0])*v*Math.sin(omega*dt/2);
		  vy[0] = -(m[1]/m[0])*v*Math.cos(omega*dt/2);
		  vz[0] = 0;
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] = -v*Math.sin(omega*dt/2);
		  vy[1] =  v*Math.cos(omega*dt/2);
		  vz[1] = 0;

		  // Saturn
		  j = 2;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 95.2; // saturn
		  rad[j] = 0.0004027;
		  rad0[j] = 0.0004027;
		  // r = 9.539;  // saturn radius in au
		  // v = 2.05;   // saturn velocity in au/year

		  // omega = v/r;
		  omega = omega/2;
		  r = Math.pow((1+(m[2]/(m[0]+m[1])))*(1+(m[2]/(m[0]+m[1])))
			                   *(1-Math.cos(omega*dt))
			       /((m[0]+m[1])*G*dt*dt/2),
			  -1.0/3.0);
		  v = r*Math.sin(omega*dt/2)/(dt/2);

		  x[0] += (m[2]/(m[0]+m[1]))*r;
		  y[0] += 0;
		  z[0] += 0;
		  vx[0] += -(m[2]/(m[0]+m[1]))*v*Math.sin(omega*dt/2);
		  vy[0] +=  (m[2]/(m[0]+m[1]))*v*Math.cos(omega*dt/2);
		  vz[0] += 0;
		  x[1] += (m[2]/(m[0]+m[1]))*r;
		  y[1] += 0;
		  z[1] += 0;
		  vx[1] += -(m[2]/(m[0]+m[1]))*v*Math.sin(omega*dt/2);
		  vy[1] +=  (m[2]/(m[0]+m[1]))*v*Math.cos(omega*dt/2);
		  vz[1] += 0;
		  x[2] = -r;
		  y[2] = 0;
		  z[2] = 0;
		  vx[2] =  v*Math.sin(omega*dt/2);
		  vy[2] = -v*Math.cos(omega*dt/2);
		  vz[2] = 0;

		    double ax[]=new double[n], 
			   ay[]=new double[n], 
			   az[]=new double[n];
		    for (j=0; j<n; j++) {
			ax[j] = 0; ay[j] = 0; az[j] = 0;
			for (i=0; i<n; i++) {
			    if (i != j) {
				r = (x[i]-x[j])*(x[i]-x[j])
				  + (y[i]-y[j])*(y[i]-y[j])
				  + (z[i]-z[j])*(z[i]-z[j]);
				r = Math.sqrt(r);
				double r3 = r*r*r;
				ax[j] -= G * m[i] * (x[j] - x[i])/r3;
				ay[j] -= G * m[i] * (y[j] - y[i])/r3;
				az[j] -= G * m[i] * (z[j] - z[i])/r3;
			    }
			}
		    }
		    for (j=0; j<n; j++) {
			vx[j] -= ax[j] * dt/2;
			vy[j] -= ay[j] * dt/2;
			vz[j] -= az[j] * dt/2;
		    }

		} else
		if (name.equals("Jupiter/Saturn resonance 2")) {
		  // Sun
		  j = 0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // Jupiter
		  j = 1;
		  col[j] = new Color((float)0.3,(float)1.0,(float)0.3);
		  OrbName[j] = "Jupiter";
		  m[j] = 318;
		  rad[j] = 0.0004758;
		  rad0[j] = 0.0004758;
		  r = 5.45;  // jupiter radius in au
		  v = Math.sqrt(G*m[0]/r);
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] =  0;
		  vy[1] =  v;
		  vz[1] = 0;

		  // Saturn
		  j = 2;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Saturn";
		  m[j] = 95.2; // saturn
		  rad[j] = 0.0004027;
		  rad0[j] = 0.0004027;
		  r = 8.65;  // jupiter radius in au
		  v = Math.sqrt(G*m[0]/r);
		  x[2] = -r;
		  y[2] = 0;
		  z[2] = 0;
		  vx[2] =  0;
		  vy[2] = -v;
		  vz[2] = 0;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("Binary Star with Planet")) {
		  // Sun
		  j = 0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "Sun";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;
		  r = 0.25;  // 0.5 and less is stable.  0.6 is unstable
		  v = Math.sqrt(G*m[0]/(4*r));
		  x[0] = -r;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = -v;
		  vz[0] = 0;

		  // Sun Companion
		  j = 1;
		  col[j] = new Color((float)1.0,(float)0.9,(float)0.4);
		  OrbName[j] = "Sun Companion";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] =  0;
		  vy[1] =  v;
		  vz[1] = 0;

		  // Earthlike Planet
		  j = 2;
		  col[j] = new Color((float)1.0,(float)0.3,(float)0.3);
		  OrbName[j] = "Earthlike Planet";
		  m[j] = 1; 
		  rad[j] = 0.00004301;
		  rad0[j] = 0.00004301;
		  r = 1;  // jupiter radius in au
		  v = Math.sqrt(G*(m[0]+m[1])/(r));
		  x[2] = -r;
		  y[2] = 0;
		  z[2] = 0;
		  vx[2] =  0;
		  vy[2] = -v;
		  vz[2] = 0;

	      /*
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vx[j];
		  }
		  vx[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vy[j];
		  }
		  vy[0] = -mv/m[0];
		  mv = 0;
		  for (j=1; j<3; j++) {
			mv += m[j]*vz[j];
		  }
		  vz[0] = -mv/m[0];
	      */

		} else
		if (name.equals("HD 209458")) {
		  // HD209458
		  j = 0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "HD 209458  ";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // HD209458b
		  j = 1;
		  col[j] = new Color((float)0.3,(float)0.8,(float)0.9);
		  OrbName[j] = "HD 209458 b";
		  m[j] = 318;
		  rad[j] = 0.0004758;
		  rad0[j] = 0.0004758;
		  r = 0.045;
		  v = Math.sqrt(G*(m[0])/(r));
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] =  0;
		  vy[1] =  v;
		  vz[1] = 0;

		  startDate = 2009;
		} else
		if (name.equals("HD 209458 edge on")) {
		  // HD209458
		  j = 0;
		  col[j] = new Color((float)1.0,(float)1.0,(float)0.3);
		  OrbName[j] = "HD 209458  ";
		  m[j] = 333000;
		  rad[j] = 0.0046;
		  rad0[j] = 0.0046;
		  x[0] = 0;
		  y[0] = 0;
		  z[0] = 0;
		  vx[0] = 0;
		  vy[0] = 0;
		  vz[0] = 0;

		  // HD209458b
		  j = 1;
		  col[j] = new Color((float)0.3,(float)0.8,(float)0.9);
		  OrbName[j] = "HD 209458 b";
		  m[j] = 318;
		  rad[j] = 0.0004758;
		  rad0[j] = 0.0004758;
		  r = 0.045;
		  v = Math.sqrt(G*(m[0])/(r));
		  x[1] = r;
		  y[1] = 0;
		  z[1] = 0;
		  vx[1] =  0;
		  vy[1] =  0;
		  vz[1] = v;

		  startDate = 2009;
		} else
		{}
        }

	static public void fromEphem(double theta, double phi, double dtheta_cos_phi, 
		  double dphi, double rho, double drho, int body_j)
	{
	    // convert RA from deg to rad
	    theta = theta*2*Math.PI/360;

	    // convert dec from deg to rad
	    phi = phi*2*Math.PI/360;

	    // convert dRA*cosD from arcseconds/hour to rad/year
	    dtheta_cos_phi = dtheta_cos_phi*(2*Math.PI/(360*3600))*(24*365.25);

	    // convert d(DEC)/dt from arcseconds/hour to rad/year
	    dphi = dphi*(2*Math.PI/(360*3600))*(24*365.25);

	    // convert deldot from km/s to au/year
	    drho = drho*3600*24*365.25/149597870.691;

	    // convert spherical coords to cartesian
	    x[body_j] = rho*Math.cos(phi)*Math.cos(theta);
	    y[body_j] = rho*Math.cos(phi)*Math.sin(theta);
	    z[body_j] = rho*Math.sin(phi);
	    vx[body_j] = drho*Math.cos(phi)*Math.cos(theta)
		  - rho*Math.sin(phi)*Math.cos(theta)*dphi
		  - rho*Math.sin(theta)*dtheta_cos_phi;
	    vy[body_j] = drho*Math.cos(phi)*Math.sin(theta)
		  - rho*Math.sin(phi)*Math.sin(theta)*dphi
		  + rho*Math.cos(theta)*dtheta_cos_phi;
	    vz[body_j] = drho*Math.sin(phi) + rho*Math.cos(phi)*dphi;
	}

	static public void fromEphemEcliptic(double theta, 
		    double phi, double dtheta_cos_phi, 
		    double dphi, double rho, double drho, int body_j)
	{
	    double y0, z0, vy0, vz0;
	    double ang_ecl = 23.5*2*Math.PI/360.0;
	    // convert RA from deg to rad
	    theta = theta*2*Math.PI/360;

	    // convert dec from deg to rad
	    phi = phi*2*Math.PI/360;

	    // convert dRA*cosD from arcseconds/hour to rad/year
	    dtheta_cos_phi = dtheta_cos_phi*(2*Math.PI/(360*3600))*(24*365.25);

	    // convert d(DEC)/dt from arcseconds/hour to rad/year
	    dphi = dphi*(2*Math.PI/(360*3600))*(24*365.25);

	    // convert deldot from km/s to au/year
	    drho = drho*3600*24*365.25/149597870.691;

	    // convert spherical coords to cartesian
	    x[body_j] = rho*Math.cos(phi)*Math.cos(theta);
	    y0        = rho*Math.cos(phi)*Math.sin(theta);
	    z0        = rho*Math.sin(phi);
	    vx[body_j] = drho*Math.cos(phi)*Math.cos(theta)
		  - rho*Math.sin(phi)*Math.cos(theta)*dphi
		  - rho*Math.sin(theta)*dtheta_cos_phi;
	    vy0        = drho*Math.cos(phi)*Math.sin(theta)
		  - rho*Math.sin(phi)*Math.sin(theta)*dphi
		  + rho*Math.cos(theta)*dtheta_cos_phi;
	    vz0        = drho*Math.sin(phi) + rho*Math.cos(phi)*dphi;

	    y[body_j] =  y0*Math.cos(ang_ecl) + z0*Math.sin(ang_ecl);
	    z[body_j] = -y0*Math.sin(ang_ecl) + z0*Math.cos(ang_ecl);

	    vy[body_j] =  vy0*Math.cos(ang_ecl) + vz0*Math.sin(ang_ecl);
	    vz[body_j] = -vy0*Math.sin(ang_ecl) + vz0*Math.cos(ang_ecl);
	}

	static public void loadPath(String fname, int body_j, int m)
	{
	    int k,n;
	    String line;
	    BufferedReader in;
	    URL url = null;
	    /*
	    try { in = new BufferedReader(new FileReader(fname));}
	    catch (FileNotFoundException fe)
	    {System.err.println(fname+" not found"); return;}
	    */
	    {
		//try{ url = new URL("file:///C:/usr/rvdb/public_html/JAVA/astro/galaxy/"+fname);}
		try{ url = new URL("http://www.princeton.edu/~rvdb/JAVA/astro/galaxy/"+fname);}
		catch (MalformedURLException me) {
		    System.out.println("Malformed URL");
		    return;
		}
		try{ in = new BufferedReader(
			  new InputStreamReader (url.openStream())); }
		catch (IOException ffe) {
		    System.err.println("file not found");
		    return;
		}
	    }

	    try { line = in.readLine(); }
	    catch (IOException e) {System.err.println("IO Error"); return;}
	    n = SL.atoi(line);
	    pathx[body_j] = new double[n];
	    pathy[body_j] = new double[n];
	    pathz[body_j] = new double[n];
	    for (k=0; k<n; k++) {
		try { line = in.readLine(); }
		catch (IOException e) {System.err.println("IO Error");}
		if (line == null) break;
		
		double theta = SL.atof(line.substring(0,11));
		double phi = SL.atof(line.substring(12,23));
		double dtheta_cos_phi = SL.atof(line.substring(24,32));
		double dphi = SL.atof(line.substring(33,42));
		double rho = SL.atof(line.substring(43,56));
		double drho = SL.atof(line.substring(57,66));

		/*
		if (k ==0) {
		    System.out.println("theta = "+theta);
		    System.out.println("phi = "+phi);
		    System.out.println("rho = "+rho);
		}
		*/

		// convert RA from deg to rad
		theta = theta*2*Math.PI/360;

		// convert dec from deg to rad
		phi = phi*2*Math.PI/360;

		// convert dRA*cosD from arcseconds/hour to rad/year
		dtheta_cos_phi = dtheta_cos_phi*(2*Math.PI/(360*3600))*(24*365.25);

		// convert d(DEC)/dt from arcseconds/hour to rad/year
		dphi = dphi*(2*Math.PI/(360*3600))*(24*365.25);

		// convert deldot from km/s to au/year
		drho = drho*3600*24*365.25/149597870.691;

		// convert spherical coords to cartesian
		pathx[body_j][k] = rho*Math.cos(phi)*Math.cos(theta);
		pathy[body_j][k] = rho*Math.cos(phi)*Math.sin(theta);
		pathz[body_j][k] = rho*Math.sin(phi);
	    }
	}

}
