package net.vaemendis.hccd;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Tools {

    public static java.util.List<Image> getApplicationIcons() throws IOException {
        Object o = new Object();
        java.util.List<Image> iconList = new ArrayList<>();
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-256.png")));
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-128.png")));
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-64.png")));
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-48.png")));
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-32.png")));
        iconList.add(ImageIO.read(o.getClass().getResource("/icon-16.png")));
        return iconList;
    }
}
