package astro.galaxy;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;
import java.util.Random;

/** Runs the original Java code headlessly and emits reference states. */
public final class JavaReference extends DustDiskCanvas {
    @Override public void paint(Graphics graphics) {}

    public static void main(String[] args) throws ReflectiveOperationException {
        Field field = Class.forName("java.lang.Math$RandomNumberGeneratorHolder")
                           .getDeclaredField("randomNumberGenerator");
        field.setAccessible(true);
        Random random = (Random) field.get(null);
        for (String scenario : new String[]{"Dust Disk Face-On", "Dusty Solar System"}) {
            for (int method : new int[]{2, 4}) {
                for (boolean collision : new boolean[]{false, true}) {
                    random.setSeed(524);
                    new JavaReference().record(scenario, method, collision);
                }
            }
        }
    }

    private void record(String scenario, int method, boolean collision) {
        final double gravity = 1.183804e-4;
        int count = scenario.equals("Dust Disk Face-On") ? 9 : 21;
        int center = scenario.equals("Dust Disk Face-On") ? 1 : 5;
        Data3d.init(scenario, count, gravity);
        if (collision) {
            int dust = count - 1;
            Data3d.x[dust] = Data3d.x[0] + 0.004;
            Data3d.y[dust] = Data3d.y[0];
            Data3d.z[dust] = Data3d.z[0];
            Data3d.vx[dust] = Data3d.vx[0];
            Data3d.vy[dust] = Data3d.vy[0] + 0.1;
            Data3d.vz[dust] = Data3d.vz[0];
        }
        double totalMass = 0, momentumX = 0, momentumY = 0, momentumZ = 0;
        for (int index = 0; index < count; index++) {
            totalMass += Data3d.m[index];
            momentumX += Data3d.m[index] * Data3d.vx[index];
            momentumY += Data3d.m[index] * Data3d.vy[index];
            momentumZ += Data3d.m[index] * Data3d.vz[index];
        }
        for (int index = 0; index < count; index++) {
            Data3d.vx[index] -= momentumX / totalMass;
            Data3d.vy[index] -= momentumY / totalMass;
            Data3d.vz[index] -= momentumZ / totalMass;
        }
        double step = collision ? 0.00001 : 0.01;
        setParams(count, Data3d.m, Data3d.x, Data3d.y, Data3d.z, Data3d.vx, Data3d.vy,
                  Data3d.vz, Data3d.rad, Data3d.J2, Data3d.col, Data3d.OrbName,
                  Data3d.startDate, null, null, null, gravity, Data3d.rad0, step, center, 1,
                  16, method, FIXED, false, true, 0, 0);
        int stop = collision ? 12 : 250;
        for (int index = 0; index <= stop; index++) {
            if (index == 0 || index == 1 || index == 12 || index == stop) {
                for (int body = 0; body < count; body++) {
                    Body3d state = p[body];
                    System.out.println(String.join(",", "REFERENCE", scenario, "" + method,
                        "" + collision, "" + index, "" + body, "" + t, "" + state.m,
                        "" + state.rad0, "" + (state.col == Color.black),
                        "" + state.x, "" + state.y, "" + state.z,
                        "" + state.vx, "" + state.vy, "" + state.vz,
                        "" + state.ax, "" + state.ay, "" + state.az,
                        "" + state.jx, "" + state.jy, "" + state.jz));
                }
            }
            if (index < stop) {
                actionPerformed(null);
            }
        }
    }
}
