package brillo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author estebanfcv
 */
public class Util {

    public static void cerrarProcesos(BufferedInputStream bis, InputStream is, Process process, BufferedReader br) {
        try {
            if (bis != null) {
                bis.close();
            }
            if (is != null) {
                is.close();
            }
            if (process != null) {
                process.destroy();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int obtenerBrilloMaximo() {
        String[] brilloActual = {"sh", "-c", "cat /sys/class/backlight/intel_backlight/max_brightness"};
        Process process = null;
        int brillo = 0;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            process = Runtime.getRuntime().exec(brilloActual);
            is = process.getInputStream();
            bis = new BufferedInputStream(is);
            byte[] contents = new byte[1024];
            int bytesRead;
            if ((bytesRead = bis.read(contents)) != -1) {
                brillo = new Integer(new String(contents, 0, bytesRead).trim());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            cerrarProcesos(bis, is, process, null);
        }
        return brillo;
    }

    public static int obtenerBrilloActual(int BRILLO_MAXIMO) {
        int actual = 0;
        String[] brilloActual = {"sh", "-c", "cat /sys/class/backlight/intel_backlight/brightness"};
        Process process = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            process = Runtime.getRuntime().exec(brilloActual);
            is = process.getInputStream();
            bis = new BufferedInputStream(is);
            byte[] contents = new byte[1024];
            int bytesRead;
            if ((bytesRead = bis.read(contents)) != -1) {
                actual = new Integer(new String(contents, 0, bytesRead).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrarProcesos(bis, is, process, null);
        }
        return Math.round((float) actual * 100 / (float) BRILLO_MAXIMO);
    }

    public static void cambiarBrillo(int BRILLO_MAXIMO, int brilloActual) {
        String[] comando = {"sh", "-c", "echo " + ((BRILLO_MAXIMO * brilloActual) / 100) + " > /sys/class/backlight/intel_backlight/brightness"};
        try {
            Runtime.getRuntime().exec(comando);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void guardarBrilloActual(int brilloActual) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/etc/rc.local"));
            String renglonOriginal = "";
            String renglonModificado = "";
            while ((renglonOriginal = br.readLine()) != null) {
                if (renglonOriginal.contains("echo")) {
                    renglonModificado = renglonOriginal.replace(obtenerNumeroBrillo(renglonOriginal), String.valueOf(brilloActual));
                    break;
                }
            }
            renglonOriginal = renglonOriginal.replace("/", "\\/");
            renglonModificado = renglonModificado.replace("/", "\\/");
            if (!renglonOriginal.isEmpty() && !renglonModificado.isEmpty()) {
                String[] sed = {"/bin/bash", "-c", "echo Zelda090| sudo -S " + "sed  -i  's/" + renglonOriginal + "/" + renglonModificado + "/g' /etc/rc.local"};
                new ProcessBuilder(sed).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cerrarProcesos(null, null, null, br);
        }
    }

    private static String obtenerNumeroBrillo(String renglon) {
        String numero = "";
        for (int i = 0; i < renglon.length(); i++) {
            char x = renglon.charAt(i);
            if (x >= 48 && x <= 57) {
                numero += renglon.charAt(i);
            }
        }
        return numero;
    }
}