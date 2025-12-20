package astro.galaxy;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import javax.swing.*;

import myutil.*;
import myutil.Format;

public class DustDiskCanvas extends Canvas implements ActionListener
{
	int n;
	int n0;
        int[] zSort;
	Body3d[] p;
	Body3d[] p0;
        double[] minrad;
        double[] maxrad;
        double[] minapp;
        double[] avgdiff;
	double[] x1, y1;
	double r0[];
	double G, rad0;
	double t, dt, told;
        boolean reset = true;
	int j0;
	int warp;
        int cnt;
	double zoom;
	boolean showorig;
	boolean accrete;
	double theta;
	double dt0 = 0.1;
	double th0;
	double costh0, sinth0;
	double v0;
	Body3d pi, pj;
	double[] cum_mass;
	int integrator;
	double orient;
	int steplength;
	int randwalk = 1;
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
	// static final double startDate = 2005.1397; // 00:00 Feb 21, 2005
	static double startDate = 2006.4384; // 00:00 Jun 10, 2006
	static final String[] Date = {
	    "Jan  1", "Jan  2", "Jan  3", "Jan  4", "Jan  5", "Jan  6", "Jan  7", 
	    "Jan  8", "Jan  9", "Jan 10", "Jan 11", "Jan 12", "Jan 13", "Jan 14", 
	    "Jan 15", "Jan 16", "Jan 17", "Jan 18", "Jan 19", "Jan 20", 
	    "Jan 21", "Jan 22", "Jan 23", "Jan 24", "Jan 25", "Jan 26", 
	    "Jan 27", "Jan 28", "Jan 29", "Jan 30", "Jan 31", "Feb  1", 
	    "Feb  2", "Feb  3", "Feb  4", "Feb  5", "Feb  6", "Feb  7", 
	    "Feb  8", "Feb  9", "Feb 10", "Feb 11", "Feb 12", "Feb 13", 
	    "Feb 14", "Feb 15", "Feb 16", "Feb 17", "Feb 18", "Feb 19", 
	    "Feb 20", "Feb 21", "Feb 22", "Feb 23", "Feb 24", "Feb 25", 
	    "Feb 26", "Feb 27", "Feb 28", "Mar  1", "Mar  2", "Mar  3", 
	    "Mar  4", "Mar  5", "Mar  6", "Mar  7", "Mar  8", "Mar  9", 
	    "Mar 10", "Mar 11", "Mar 12", "Mar 13", "Mar 14", "Mar 15", 
	    "Mar 16", "Mar 17", "Mar 18", "Mar 19", "Mar 20", "Mar 21", 
	    "Mar 22", "Mar 23", "Mar 24", "Mar 25", "Mar 26", "Mar 27", 
	    "Mar 28", "Mar 29", "Mar 30", "Mar 31", "Apr  1", "Apr  2", 
	    "Apr  3", "Apr  4", "Apr  5", "Apr  6", "Apr  7", 
	    "Apr  8", "Apr  9", "Apr 10", "Apr 11", "Apr 12", 
	    "Apr 13", "Apr 14", "Apr 15", "Apr 16", "Apr 17", 
	    "Apr 18", "Apr 19", "Apr 20", "Apr 21", "Apr 22", 
	    "Apr 23", "Apr 24", "Apr 25", "Apr 26", "Apr 27", 
	    "Apr 28", "Apr 29", "Apr 30", "May  1", "May  2", 
	    "May  3", "May  4", "May  5", "May  6", "May  7", 
	    "May  8", "May  9", "May 10", "May 11", "May 12", 
	    "May 13", "May 14", "May 15", "May 16", "May 17", 
	    "May 18", "May 19", "May 20", "May 21", "May 22", 
	    "May 23", "May 24", "May 25", "May 26", "May 27", 
	    "May 28", "May 29", "May 30", "May 31", "Jun  1", 
	    "Jun  2", "Jun  3", "Jun  4", "Jun  5", "Jun  6", 
	    "Jun  7", "Jun  8", "Jun  9", "Jun 10", "Jun 11", 
	    "Jun 12", "Jun 13", "Jun 14", "Jun 15", "Jun 16", 
	    "Jun 17", "Jun 18", "Jun 19", "Jun 20", "Jun 21", 
	    "Jun 22", "Jun 23", "Jun 24", "Jun 25", "Jun 26", 
	    "Jun 27", "Jun 28", "Jun 29", "Jun 30", "Jul  1", 
	    "Jul  2", "Jul  3", "Jul  4", "Jul  5", "Jul  6", 
	    "Jul  7", "Jul  8", "Jul  9", "Jul 10", "Jul 11", 
	    "Jul 12", "Jul 13", "Jul 14", "Jul 15", "Jul 16", 
	    "Jul 17", "Jul 18", "Jul 19", "Jul 20", "Jul 21", 
	    "Jul 22", "Jul 23", "Jul 24", "Jul 25", "Jul 26", 
	    "Jul 27", "Jul 28", "Jul 29", "Jul 30", "Jul 31", 
	    "Aug  1", "Aug  2", "Aug  3", "Aug  4", "Aug  5", 
	    "Aug  6", "Aug  7", "Aug  8", "Aug  9", "Aug 10", 
	    "Aug 11", "Aug 12", "Aug 13", "Aug 14", "Aug 15", 
	    "Aug 16", "Aug 17", "Aug 18", "Aug 19", "Aug 20", 
	    "Aug 21", "Aug 22", "Aug 23", "Aug 24", "Aug 25", 
	    "Aug 26", "Aug 27", "Aug 28", "Aug 29", "Aug 30", 
	    "Aug 31", "Sep  1", "Sep  2", "Sep  3", "Sep  4", 
	    "Sep  5", "Sep  6", "Sep  7", "Sep  8", "Sep  9", 
	    "Sep 10", "Sep 11", "Sep 12", "Sep 13", "Sep 14", 
	    "Sep 15", "Sep 16", "Sep 17", "Sep 18", "Sep 19", 
	    "Sep 20", "Sep 21", "Sep 22", "Sep 23", "Sep 24", 
	    "Sep 25", "Sep 26", "Sep 27", "Sep 28", "Sep 29", 
	    "Sep 30", "Oct  1", "Oct  2", "Oct  3", "Oct  4", 
	    "Oct  5", "Oct  6", "Oct  7", "Oct  8", "Oct  9", 
	    "Oct 10", "Oct 11", "Oct 12", "Oct 13", "Oct 14", 
	    "Oct 15", "Oct 16", "Oct 17", "Oct 18", "Oct 19", 
	    "Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24", 
	    "Oct 25", "Oct 26", "Oct 27", "Oct 28", "Oct 29", 
	    "Oct 30", "Oct 31", "Nov  1", "Nov  2", "Nov  3", 
	    "Nov  4", "Nov  5", "Nov  6", "Nov  7", "Nov  8", 
	    "Nov  9", "Nov 10", "Nov 11", "Nov 12", "Nov 13", 
	    "Nov 14", "Nov 15", "Nov 16", "Nov 17", "Nov 18", 
	    "Nov 19", "Nov 20", "Nov 21", "Nov 22", "Nov 23", 
	    "Nov 24", "Nov 25", "Nov 26", "Nov 27", "Nov 28", 
	    "Nov 29", "Nov 30", "Dec  1", "Dec  2", "Dec  3", 
	    "Dec  4", "Dec  5", "Dec  6", "Dec  7", "Dec  8", 
	    "Dec  9", "Dec 10", "Dec 11", "Dec 12", "Dec 13", 
	    "Dec 14", "Dec 15", "Dec 16", "Dec 17", "Dec 18", 
	    "Dec 19", "Dec 20", "Dec 21", "Dec 22", "Dec 23", 
	    "Dec 24", "Dec 25", "Dec 26", "Dec 27", "Dec 28", 
	    "Dec 29", "Dec 30", "Dec 31"
	    };
	double mindt = 1e+300, maxdt = 0;
	double th3, th3old=0, t3, t3old=0;

	public javax.swing.Timer timer=null;

	double cmax0, cmax;

	double old_x[], old_y[], old_z[], old_vx[], old_vy[], old_vz[],
	       old_ax[], old_ay[], old_az[], old_jx[], old_jy[], old_jz[];

	public void setParams(
		int n,
		double m[],
		double x[],
		double y[],
		double z[],
		double vx[],
		double vy[],
		double vz[],
		double rad[],
		double J2[],
		Color  col[],
		String name[],
		double startDate,
		double pathx[][],
		double pathy[][],
		double pathz[][],
		double G,
		double rad0[],
		double dt0, // simulation time interval in milliseconds
		int j0,
		int warp,
		double zoom,
		int integrator,
		int steplength,
		boolean showorig,
		boolean accrete,
		int randwalk,
		double orient)
	{
	    setBackground(Color.black);
	    if (timer==null) {timer = new javax.swing.Timer (25, this);}

	    int i, j;

	    this.n = n;
	    p  = new Body3d[n]; for (j=0; j<n; j++) p[j]  = new Body3d();
	    p0 = new Body3d[n]; for (j=0; j<n; j++) p0[j] = new Body3d();
            minrad = new double[n]; for (j=0; j<n; j++) minrad[j] = 0;// 1e+30;
            maxrad = new double[n]; for (j=0; j<n; j++) maxrad[j] = 0;//-1e+30;
            minapp = new double[n]; for (j=0; j<n; j++) minapp[j] = 1e+30;
            avgdiff= new double[n]; for (j=0; j<n; j++) avgdiff[j]= 0;
	    r0 = new double[n];

	    for (j=0; j<n; j++) {
		p[j].m = m[j];
		p[j].x = x[j];
		p[j].y = y[j];
		p[j].z = z[j];
		p[j].vx = vx[j];
		p[j].vy = vy[j];
		p[j].vz = vz[j];
		p[j].name = name[j];
		if (pathx != null) p[j].pathx = pathx[j];
		if (pathy != null) p[j].pathy = pathy[j];
		if (pathz != null) p[j].pathz = pathz[j];
		p[j].rad = rad[j];
		p[j].J2 = J2[j];
		p[j].col = col[j];
		p[j].escaped = false;
		p[j].rad0 = rad0[j];
		p0[j].x = x[j];
		p0[j].y = y[j];
		p0[j].z = z[j];
		p0[j].vx = vx[j];
		p0[j].vy = vy[j];
		p0[j].vz = vz[j];
		double dx = p[j].x - p[0].x;
		double dy = p[j].y - p[0].y;
		double dz = p[j].z - p[0].z;
		r0[j] = Math.sqrt(dx*dx + dy*dy + dz*dz);
	    }
	    th0 = Math.atan2(p0[1].y,p0[1].x);
	    costh0=Math.cos(th0);
	    sinth0=Math.sin(th0);

	    v0 = Math.sqrt(p[1].vx*p[1].vx + p[1].vy*p[1].vy);

	    for (j=n; j>0; j--) {
		if (p[j-1].m != 0) break;
	    }
	    n0 = j;

	    cmax = 0.1;
	    this.G = G;
	    this.j0 = j0;
	    this.dt0 = dt0;
	    this.warp = warp;
	    this.zoom = zoom;
	    this.showorig = showorig;
	    this.accrete = accrete;
	    this.randwalk = randwalk;
	    this.integrator = integrator;
	    this.orient = orient;
	    this.steplength = steplength;
	    this.startDate = startDate;
	    cum_mass = new double[n];

            cnt = 1;

	    // if (n<=7) {cmax = 2;} // for the Lagrange case

	    pi = new Body3d(); pj = new Body3d();

	    theta = 0;
	    t=0;
            if (integrator == MIDPOINT) {
		double r03;
		for (j=0; j<n; j++) {
		    p[j].ax = 0; p[j].ay = 0; p[j].az = 0;
		    for (i=0; i<n0; i++) {
			if (i != j) {
			    double r3 = dist3(p[i], p[j]);
			    r03 = p[i].rad0 + p[j].rad0;
			    r03 = r03*r03*r03;
			    if (r3<r03) r3=r03;
			    p[j].ax -= G * p[i].m * (p[j].x - p[i].x)/r3;
			    p[j].ay -= G * p[i].m * (p[j].y - p[i].y)/r3;
			    p[j].az -= G * p[i].m * (p[j].z - p[i].z)/r3;
			}
		    }
		}
		/*
		if (n==2) { // doing this only for "perfect Saturn/Janus"
		    for (j=0; j<n; j++) {
			p[j].vx -= p[j].ax * dt/2;
			p[j].vy -= p[j].ay * dt/2;
			p[j].vz -= p[j].az * dt/2;
		    }
		}
		*/
	    } else
            if (integrator == HERMITE) {
		double r02;
		for (j=0; j<n; j++) {
		    p[j].ax = p[j].ay = p[j].az = 0;
		    p[j].jx = p[j].jy = p[j].jz = 0;
		}
		for (i=0; i<n; i++) {
		    for (j=i+1; j<n; j++) {
			double rjix = p[j].x-p[i].x, 
			       rjiy = p[j].y-p[i].y, 
			       rjiz = p[j].z-p[i].z, 
			       vjix = p[j].vx-p[i].vx, 
			       vjiy = p[j].vy-p[i].vy,
			       vjiz = p[j].vz-p[i].vz;
			r02 = p[i].rad0 + p[j].rad0;
			r02 = r02*r02;
			double r2 = rjix*rjix + rjiy*rjiy + rjiz*rjiz;
			if (r2<r02) r2=r02;
			double r3 = r2*Math.sqrt(r2);
			double rv = (rjix*vjix + rjiy*vjiy + rjiz*vjiz)/r2;
			p[i].ax += G*p[j].m * rjix / r3;
			p[i].ay += G*p[j].m * rjiy / r3;
			p[i].az += G*p[j].m * rjiz / r3;
			p[j].ax -= G*p[i].m * rjix / r3;
			p[j].ay -= G*p[i].m * rjiy / r3;
			p[j].az -= G*p[i].m * rjiz / r3;
			p[i].jx += G*p[j].m * (vjix - 3*rv*rjix)/r3;
			p[i].jy += G*p[j].m * (vjiy - 3*rv*rjiy)/r3;
			p[i].jz += G*p[j].m * (vjiz - 3*rv*rjiz)/r3;
			p[j].jx -= G*p[i].m * (vjix - 3*rv*rjix)/r3;
			p[j].jy -= G*p[i].m * (vjiy - 3*rv*rjiy)/r3;
			p[j].jz -= G*p[i].m * (vjiz - 3*rv*rjiz)/r3;
		    }
		}
	    }
		old_x  = new double[n];
		old_y  = new double[n];
		old_z  = new double[n];
		old_vx = new double[n];
		old_vy = new double[n];
		old_vz = new double[n];
		old_ax = new double[n];
		old_ay = new double[n];
		old_az = new double[n];
		old_jx = new double[n];
		old_jy = new double[n];
		old_jz = new double[n];

		zSort = new int[n];
	}

	public void destroy()
	{
	    timer.stop();
	    timer=null;
	    GL.bufferclose();
	}

	public void actionPerformed (ActionEvent event)
	{
	    int i, j, k;
	    double r03;
	    for (k=0; k<warp; k++) {
              cnt++;
              if (randwalk == 5 && cnt%10000 == 1) {
		      double th = 0;
		      double ph = Math.PI/4;
		      // double dv = 0.0000025*3.4e-7/p[j].m;
		      double dv = 0.000025; 
		      p[0].vx += 1e-3*Math.cos(th)*Math.cos(ph);
		      p[0].vy += 1e-3*Math.sin(th)*Math.cos(ph);
		      p[0].vz += 1e-3*Math.sin(ph);
	      }
              if (randwalk == 1 && cnt%10000 == 1) {
                  for (j=1; j<n; j++) {
		      double th = 2*Math.PI*Math.random();
		      double ph = 2*Math.PI*Math.random();
		      // double dv = 0.0000025*3.4e-7/p[j].m;
		      double dv = 0.0000025; // 053005
		      p[j].vx += (v0*dv)*Math.cos(th)*Math.cos(ph);
		      p[j].vy += (v0*dv)*Math.sin(th)*Math.cos(ph);
		      p[j].vz += (v0*dv)*Math.sin(ph);
		  }
	      }
              if (randwalk == 2 && cnt%1000000 == 1) {
		  double th = 2*Math.PI*Math.random();
		  double phi = 2*Math.PI*Math.random();
                  for (j=1; j<n; j++) {
		      double dv = 0.000025*3.4e-7/p[j].m;
		      // double dv = 0.0000025;
		      p[j].vx += 2*(j-1.5)*(v0*dv)*Math.cos(th)*Math.cos(phi);
		      p[j].vy += 2*(j-1.5)*(v0*dv)*Math.sin(th)*Math.cos(phi);
		      p[j].vz += 2*(j-1.5)*(v0*dv)*Math.sin(phi)/10;
		  }
	      }
	      
	      // dynamic step size
  	      if (steplength == VARIABLE)
	      {
		  dt = 1e+300;
		  for (i=0; i<n; i++) {
		      for (j=0; j<i; j++) {
			  double rx = p[j].x - p[i].x;
			  double ry = p[j].y - p[i].y;
			  double rz = p[j].z - p[i].z;
			  double velx = p[j].vx - p[i].vx;
			  double vely = p[j].vy - p[i].vy;
			  double velz = p[j].vz - p[i].vz;
			  /*
			  double accx = p[j].ax - p[i].ax;
			  double accy = p[j].ay - p[i].ay;
			  double accz = p[j].az - p[i].az;
			  double r = Math.sqrt(rx*rx+ry*ry+rz*rz);
			  double v = Math.sqrt(velx*velx+vely*vely+velz*velz);
			  double a = Math.sqrt(accx*accx+accy*accy+accz*accz);
			  */
			  double r = Math.abs(rx)+Math.abs(ry)+Math.abs(rz);
			  double v = Math.abs(velx)+Math.abs(vely)
			                            +Math.abs(velz);
			  double tmp = r/(v+1e-30);
			  // double tmp = v/(a+1e-30);
			  if (tmp < dt) {
			      dt = tmp;
			      // System.out.println("i = "+i+", j = "+j+", v = "+v+", a = "+a);
			  }
		      }
		  }
		  dt *= dt0;
		  if (dt < mindt) {
		      mindt = dt;
		      // System.out.println("mindt = "+mindt);
		  }
		  if (dt > maxdt) {
		      maxdt = dt;
		      // System.out.println("maxdt = "+maxdt);
		  }
	      } else {
		  dt = dt0;
	      }
	      /**/

	      t += dt;
              if (integrator == MIDPOINT) {
	        double th = Math.atan2(p[1].y,p[1].x),
                       th2= Math.atan2(p[1].y+p[1].vy*dt,p[1].x+p[1].vx*dt);
	        double costh=Math.cos(th), sinth=Math.sin(th),
	               costh2=Math.cos(th2), sinth2=Math.sin(th2);
		for (j=0; j<n; j++) {
		    p[j].vx += p[j].ax * dt/2;
		    p[j].vy += p[j].ay * dt/2;
		    p[j].vz += p[j].az * dt/2;
		}
		/*
		for (j=0; j<n; j++) {
		    for (i=0; i<j; i++) {
			double r = dist(p[i], p[j]);
			double r3 = r*r*r;
			r03 = p[i].rad0 + p[j].rad0;
			r03 = r03*r03*r03;
			if (r3<r03) { // collision...bounce
			    double xij = p[j].x - p[i].x;
			    double yij = p[j].y - p[i].y;
			    double zij = p[j].z - p[i].z;
			    double vxij = p[j].vx - p[i].vx;
			    double vyij = p[j].vy - p[i].vy;
			    double vzij = p[j].vz - p[i].vz;
			    double norm = (vxij*xij+vyij*yij+vzij*zij)
				 / Math.sqrt(xij*xij+yij*yij+zij*zij);
			    double vxnorm = xij*norm;
			    double vynorm = yij*norm;
			    double vznorm = zij*norm;
			    p[j].vx += 1.8*vxnorm;
			    p[j].vy += 1.8*vynorm;
			    p[j].vz += 1.8*vznorm;
			    p[i].vx -= 1.8*vxnorm;
			    p[i].vy -= 1.8*vynorm;
			    p[i].vz -= 1.8*vznorm;
			}
		    }
		}
		*/
		for (j=0; j<n; j++) {
	          double xt = costh*p[j].x+sinth*p[j].y;
	          double yt = -sinth*p[j].x+costh*p[j].y;
		  p[j].x += p[j].vx * dt;
		  p[j].y += p[j].vy * dt;
		  p[j].z += p[j].vz * dt;
		  if (p[j].col != Color.green) {
	            double xtnew = costh2*p[j].x+sinth2*p[j].y;
	            double ytnew = -sinth2*p[j].x+costh2*p[j].y;
		    // if (j>1 && xt > 0 && yt*ytnew < 0) {p[j].col = Color.green;}
                    if (j>1 && xt > 0) {
			if (yt > 0 &&  yt < minapp[j]) {minapp[j] = yt;} else
			if (yt < 0 && -yt < minapp[j]) {minapp[j] = -yt;} 
                    } 
		    double r = dist(p[0], p[j]);
		    if (r < minrad[j]) minrad[j] = r;
		    if (r > maxrad[j]) maxrad[j] = r;
		  }
		}
		for (j=0; j<n; j++) {
		    p[j].ax = 0; p[j].ay = 0; p[j].az = 0;
		    for (i=0; i<n0; i++) {
			if (i != j) {
			    double r = dist(p[i], p[j]);
			    double r3 = r*r*r;
			    /*
			    if (j==1 && i==2) {
				System.out.println("r3 = "+r3);
			    }
			    */
			    r03 = p[i].rad0 + p[j].rad0;
			    r03 = r03*r03*r03;
			    if (r3<r03) { // collision...slow down
				r3=r03;
				System.out.println("collision");
				/*
				// 053005 below
				double speed = 
				    Math.sqrt(
				    (p[j].vx-p[i].vx)*(p[j].vx-p[i].vx)
				   +(p[j].vy-p[i].vy)*(p[j].vy-p[i].vy)
				   +(p[j].vz-p[i].vz)*(p[j].vz-p[i].vz)
				   );
				System.err.println("speed = "+
					(speed*93e+6/(365*24))+" mph");
				double friction = 0.0;

				p[j].ax -= friction*(p[j].vx - p[i].vx)/dt;
				p[j].ay -= friction*(p[j].vy - p[i].vy)/dt;
				p[j].az -= friction*(p[j].vz - p[i].vz)/dt;
				*/
				if (accrete) p[j].col = Color.black;
			    }
			    p[j].ax -= G * p[i].m * (p[j].x - p[i].x)/r3;
			    p[j].ay -= G * p[i].m * (p[j].y - p[i].y)/r3;
			    p[j].az -= G * p[i].m * (p[j].z - p[i].z)/r3;
			    if (p[i].J2 != 0) {
				/* Oblateness factor J2.
				 * Based on gravitational potential given by
				 * V = GM(1-j2(R/r)^2(1-3sin^2(phi))/2)/r
				 * where r is radius and phi is latitude
				 * and R is equitorial radius of planet
				 */
				double c1;
				double r5 = r3*r*r;
				c1 = 1.5*G*p[i].m*p[i].J2*p[i].rad*p[i].rad;
				p[j].ax += c1*(p[j].x-p[i].x)/r5;
				p[j].ay += c1*(p[j].y-p[i].y)/r5;
				p[j].az += c1*(p[j].z-p[i].z)/r5;
			    }
			}
		    }
		}
		for (j=0; j<n; j++) {
		    p[j].vx += p[j].ax * dt/2;
		    p[j].vy += p[j].ay * dt/2;
		    p[j].vz += p[j].az * dt/2;
		}
              } else
              if (integrator == HERMITE) {
		double r02;
		double far;

		for (i=0; i<n; i++) {
		  old_x[i]  = p[i].x;  old_y[i]  = p[i].y;  old_z[i]  = p[i].z;
		  old_vx[i] = p[i].vx; old_vy[i] = p[i].vy; old_vz[i] = p[i].vz;
		  old_ax[i] = p[i].ax; old_ay[i] = p[i].ay; old_az[i] = p[i].az;
		  old_jx[i] = p[i].jx; old_jy[i] = p[i].jy; old_jz[i] = p[i].jz;
		  p[i].x += p[i].vx*dt + p[i].ax*dt*dt/2 + p[i].jx*dt*dt*dt/6;
		  p[i].y += p[i].vy*dt + p[i].ay*dt*dt/2 + p[i].jy*dt*dt*dt/6;
		  p[i].z += p[i].vz*dt + p[i].az*dt*dt/2 + p[i].jz*dt*dt*dt/6;
		  p[i].vx += p[i].ax*dt + p[i].jx*dt*dt/2;
		  p[i].vy += p[i].ay*dt + p[i].jy*dt*dt/2;
		  p[i].vz += p[i].az*dt + p[i].jz*dt*dt/2;
		}
		for (i=0; i<n; i++) {
		    p[i].ax = p[i].ay = p[i].az = 0;
		    p[i].jx = p[i].jy = p[i].jz = 0;
		}
		for (i=0; i<n; i++) {
		    for (j=i+1; j<n; j++) {
			double rjix = p[j].x - p[i].x;
			double rjiy = p[j].y - p[i].y;
			double rjiz = p[j].z - p[i].z;
			double vjix = p[j].vx - p[i].vx;
			double vjiy = p[j].vy - p[i].vy;
			double vjiz = p[j].vz - p[i].vz;

			double r2 = rjix*rjix + rjiy*rjiy + rjiz*rjiz;
			r02 = p[i].rad0 + p[j].rad0;
			r02 = r02*r02;
			if (r2<r02) {r2=r02; far=1;} else {far=0;}
			double r3 = r2*Math.sqrt(r2);
			double rv = (rjix*vjix + rjiy*vjiy + rjiz*vjiz)/r2;

			p[i].ax += G*p[j].m * rjix/r3;
			p[i].ay += G*p[j].m * rjiy/r3;
			p[i].az += G*p[j].m * rjiz/r3;
			p[j].ax -= G*p[i].m * rjix/r3;
			p[j].ay -= G*p[i].m * rjiy/r3;
			p[j].az -= G*p[i].m * rjiz/r3;
			p[i].jx += G*p[j].m * (vjix - 3*far*rv*rjix)/r3;
			p[i].jy += G*p[j].m * (vjiy - 3*far*rv*rjiy)/r3;
			p[i].jz += G*p[j].m * (vjiz - 3*far*rv*rjiz)/r3;
			p[j].jx -= G*p[i].m * (vjix - 3*far*rv*rjix)/r3;
			p[j].jy -= G*p[i].m * (vjiy - 3*far*rv*rjiy)/r3;
			p[j].jz -= G*p[i].m * (vjiz - 3*far*rv*rjiz)/r3;
		    }
		}
		for (i=0; i<n; i++) {
		    p[i].vx = old_vx[i] + (old_ax[i] + p[i].ax)*dt/2
			                + (old_jx[i] - p[i].jx)*dt*dt/12;
		    p[i].vy = old_vy[i] + (old_ay[i] + p[i].ay)*dt/2
			                + (old_jy[i] - p[i].jy)*dt*dt/12;
		    p[i].vz = old_vz[i] + (old_az[i] + p[i].az)*dt/2
			                + (old_jz[i] - p[i].jz)*dt*dt/12;
		    p[i].x = old_x[i] + (old_vx[i] + p[i].vx)*dt/2
			              + (old_ax[i] - p[i].ax)*dt*dt/12;
		    p[i].y = old_y[i] + (old_vy[i] + p[i].vy)*dt/2
			              + (old_ay[i] - p[i].ay)*dt*dt/12;
		    p[i].z = old_z[i] + (old_vz[i] + p[i].vz)*dt/2
			              + (old_az[i] - p[i].az)*dt*dt/12;
		}
	      }
	    }
	    /*
	    double kinetic=0, potential=0;
	    for (j=0; j<n; j++) {
		kinetic += p[j].m*
		          (p[j].vx*p[j].vx+p[j].vy*p[j].vy+p[j].vz*p[j].vz);
		for (i=0; i<n; i++) {
		    if (i != j) {
			double r1 = dist(p[i], p[j]);
			potential -= G*p[i].m*p[j].m/r1;
		    }
		}
	    }
	    */
	    if (j0 >= 1 && j0 < n) { 
		    theta = Math.atan2(p[j0].y-p[0].y,p[j0].x-p[0].x); 
		    double dx1 = p[1].x-p[0].x;
		    double dy1 = p[1].y-p[0].y;
		    double dx2 = p[2].x-p[0].x;
		    double dy2 = p[2].y-p[0].y;
		    double r0 = (Math.sqrt(dx1*dx1 + dy1*dy1) 
			       + Math.sqrt(dx2*dx2 + dy2*dy2))/2;
		    double omega = Math.sqrt(G*p[0].m/(r0*r0*r0));
		    // System.out.println(":: G = "+G+" "+p[1].m+" "+p[2].m+" "+omega);
	    } else {theta = 0.0;}

	    paint(getGraphics());
	}

	public void setAnimEffects(double dt0, int j0,
				   int warp, double zoom, boolean showorig,
				   boolean accrete,
				   int integrator, int steplength, double orient)
	{
	    // timer.setDelay(dt); 
	    this.dt0 = dt0;
	    this.j0 = j0;
	    this.warp = warp;
	    this.zoom = zoom;
	    this.showorig = showorig;
	    this.accrete = accrete;
	    this.integrator = integrator;
	    this.orient = orient;
	    this.steplength = steplength;
	}

	public void setTrails(boolean trails)
	{
	    this.trails = trails;
	    if (trails) firstpass = true;
	}

	double dist( Body3d pi, Body3d pj )
	{
	    double r;
	    double xdiff = pi.x - pj.x;
	    double ydiff = pi.y - pj.y;
	    double zdiff = pi.z - pj.z;
	    r = Math.sqrt(xdiff*xdiff + ydiff*ydiff + zdiff*zdiff);
	    return r;
	}

	double dist3( Body3d pi, Body3d pj )
	{
	    double r = dist(pi, pj);
	    return r*r*r;
	}

	void qkBodySort(Body3d[] p, int left, int right) 
	{
	    int i, last;

	    if (left >= right) return;

	    BodySwap(p, left, (left+right)/2);
	    last = left;
	    for (i = left+1; i <= right; i++) 
		if (p[i].cm_dist < p[left].cm_dist)
		    BodySwap(p, ++last, i);
	    BodySwap(p, left, last);
	    qkBodySort(p, left, last-1);
	    qkBodySort(p, last+1, right);
	}

	void BodySwap(Body3d[] p, int i, int j)
	{
	    Body3d temp;

	    temp= p[i];
	    p[i] = p[j];
	    p[j] = temp;
	}

	boolean trails = false;
	boolean firstpass = true;
	public void paint (Graphics g) 
	{
	    int i, j, ii, jj, w=500, h=500;
	    double r;
	    double costh=Math.cos(theta), sinth=Math.sin(theta);
	    double xt;
	    double yt;
	    double xmin, xmax, ymin, ymax;
	    double kin=0, pot=0;
	    DecimalFormat fmt = new DecimalFormat("###,##0.###");
	    DecimalFormat fmt2 = new DecimalFormat("#0.#########");
	    double sin_orient = Math.sin(orient*Math.PI/180);
	    double cos_orient = Math.cos(orient*Math.PI/180);

	    for (j=0; j<n; j++) {zSort[j] = j;}
	    qksort(zSort,0,n-1);

	    if (p!=null && j0 >= 0 && j0 < n && !showorig) { 
		xt = costh*p[j0].x+sinth*p[j0].y;
		yt = -sinth*p[j0].x+costh*p[j0].y;
	    } else {
		xt = 0;
	        yt = 0;
	    }

	    double dx, dy, dz, roverr;
	    if (j0>0 && p != null && !showorig) {
		  dx = p[j0].x - p[0].x;
		  dy = p[j0].y - p[0].y;
		  dz = p[j0].z - p[0].z;
		  roverr = Math.sqrt(dx*dx + dy*dy + dz*dz)/r0[j0];
	    } else {
		    roverr = 1;
	    }
	  xmin = xt-roverr*zoom;
	  xmax = xt+roverr*zoom;
	  ymin = yt-roverr*zoom;
	  ymax = yt+roverr*zoom;
	  if (trails) {
	   if (firstpass) {
	    firstpass = false;
	    w = getSize().width;
	    h = getSize().height;
	    GL.ginit(getGraphics(), getSize().width, getSize().height, this,false);
	    GL.ortho2( xmin, xmax, ymin, ymax);
	    GL.color(Color.black);
	    GL.clear();
	    x1 = new double[n];
	    y1 = new double[n];
	    for (j=0; j<n; j++) {
		x1[j] =  costh*p[j].x+sinth*p[j].y;
		y1[j] = -sinth*p[j].x+costh*p[j].y;
	    }
	   } else {
	    GL.ortho2( xmin, xmax, ymin, ymax);
	    for (j=0; j<n; j++) {
			if (p[j].col == null) {
			  float pjred = (float)(10*p[j].z/Math.sqrt(p[j].x*p[j].x
						               +p[j].y*p[j].y
						               +p[j].z*p[j].z)
					);
			  if (pjred>1) pjred=1f; else if (pjred<-1) pjred=-1f;
			  Color col;
			  if (pjred>=0) {
			    col = new Color(1f,1f-0.5f*pjred,1f-pjred);
			  } else {
			    col = new Color(1f+pjred,1f+0.5f*pjred,1f);
			  }
			  GL.color(col);
			} else {
		          GL.color(p[j].col);
			}
		r = p[j].rad;
		GL.pnt2(costh*p[j].x+sinth*p[j].y,
		       cos_orient*(-sinth*p[j].x+costh*p[j].y) +
		       sin_orient*p[j].z);
		/*
		GL.pnt2(costh*p[j].x+sinth*p[j].y,
	               -sinth*p[j].x+costh*p[j].y);
		GL.move2(x1[j], y1[j]);
		x1[j] =  costh*p[j].x+sinth*p[j].y;
		y1[j] = -sinth*p[j].x+costh*p[j].y;
		GL.draw2(x1[j], y1[j]);
		       */
	    }
	   }
	  } else {
	    GL.ginit(g, getSize().width, getSize().height, 
			    this,true);
	    GL.ortho2( xmin, xmax, ymin, ymax);

	    GL.color(Color.black);
	    GL.clear();

	    for (j=0; j<n; j++) {
		GL.color(p[j].col);
		GL.drawString(p[j].name,
		    		0 + (int)(0.98*(w-0)),  
				0 + (int)(0.02*h+15*j), 
				"RIGHT", "TOP");
	    }

	    GL.color(Color.white);

	    if (showorig) {
		for (j=0; j<n; j++) {
		    if (p[j].col != Color.black) {
		        GL.color(p[j].col);
		        GL.pnt2(costh0*p0[j].x+sinth0*p0[j].y,
			       cos_orient*(-sinth0*p0[j].x+costh0*p0[j].y) +
			       sin_orient*p0[j].z);
		    }
		}
	    } else {
		if (j0 == 0 && p != null) {
		  if (p[0].pathx != null) {
		    for (i=0; i<n; i++) {
			ii = zSort[i];
		        GL.color(p[ii].col);
			GL.move2(costh*p[ii].pathx[0]+sinth*p[ii].pathy[0],
				-sinth*p[ii].pathx[0]+costh*p[ii].pathy[0]);
			for (j=1; j<p[ii].pathx.length; j++) {
			    GL.draw2(costh*p[ii].pathx[j]+sinth*p[ii].pathy[j], 
				    -sinth*p[ii].pathx[j]+costh*p[ii].pathy[j]);
			}
		    }
		  }
		}
		for (j=n-1; j>=0; j--) {
		    jj = zSort[j];
		    if (p[jj].col != Color.black) {
			double rad = p[jj].rad;
			if (rad < 0.005*(xmax-xmin)) rad = 0.005*(xmax-xmin);
			if (p[jj].col == null) {
			  float pjred = (float)(10*p[jj].z/Math.sqrt(p[jj].x*p[jj].x
						               +p[jj].y*p[jj].y
						               +p[jj].z*p[jj].z)
					);
			  if (pjred>1) pjred=1f; else if (pjred<-1) pjred=-1f;
			  Color col;
			  if (pjred>=0) {
			    col = new Color(1f,1f-0.5f*pjred,1f-pjred);
			  } else {
			    col = new Color(1f+pjred,1f+0.5f*pjred,1f);
			  }
			  GL.color(col);
			} else {
		          GL.color(p[jj].col);
			}
			/*
		        GL.pnt2(costh*p[jj].x+sinth*p[jj].y,
			       -sinth*p[jj].x+costh*p[jj].y);
			/**/
		        GL.circf(costh*p[jj].x+sinth*p[jj].y,
			       cos_orient*(-sinth*p[jj].x+costh*p[jj].y) +
			       sin_orient*p[jj].z, 
			       rad);
			       /**/
		    }
		}
	    }

	    GL.color(Color.white);
	    int year = (int) Math.floor(startDate+t/1.0029);
	    int day = (int) Math.floor(365*(startDate+t/1.0029-year));
	    // GL.drawString("Time = "+fmt.format(t), 
	    GL.drawString(Date[day]+", "+year,
		    		0 + (int)(0.02*w),  
				h - (int)(0.02*h), "LEFT", "BOTTOM");
	    GL.drawString("Magnif. = "+1./zoom,
		    		w - (int)(0.02*w),  
				h - (int)(0.02*h), "RIGHT", "BOTTOM");

	    double kinetic=0, potential=0;
	    for (j=0; j<n; j++) {
		kinetic += p[j].m*
		          (p[j].vx*p[j].vx+p[j].vy*p[j].vy+p[j].vz*p[j].vz);
		for (i=0; i<n; i++) {
		    if (i != j) {
			double r1 = dist(p[i], p[j]);
			potential -= G*p[i].m*p[j].m/r1;
		    }
		}
	    }
	    GL.drawString("Energy (10^12) = "+fmt2.format(1e12*(kinetic+potential)), 
		    		0 + (int)(0.02*w),  
				0 + (int)(0.02*h), "LEFT", "TOP");

	    GL.swapbuffers();
	  }
	}

	public void qksort(int[] v, int left, int right)
	{
	    int i, last;

	    if (left >= right) return;  /* do nothing if array contains
                                        fewer than two elements */

	    swap(v, left, (left + right)/2);	/* move partition elem */
	    last = left;				/* to v[left] */
	    for (i = left+1; i <= right; i++) {	/* partition */
	      if (p[v[i]] != null && p[v[left]] != null) {
		if (p[v[i]].z < p[v[left]].z) {
		    swap(v, ++last, i);
		}
	      }
	    }
	    swap(v, left, last);	/* restore partition elem */
	    qksort(v, left, last-1);
	    qksort(v, last+1, right);
	}

	/*  *** swap: interchange v[i] and v[j] */

	void swap(int[] v, int i, int j)
	{
	    int temp;
	    temp = v[i]; v[i] = v[j]; v[j] = temp;
	}
}
