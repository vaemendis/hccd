package net.vaemendis.hccd;

import com.jdotsoft.jarloader.JarClassLoader;

public class Launcher {

    public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("net.vaemendis.hccd.Hccd", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }     
}