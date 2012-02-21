/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import de.linogistix.common.gui.object.IconType;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author artur
 */
public class GraphicUtil extends Component {

    private static GraphicUtil instance = null;

    /** Creates a new instance of GraphicUtil */
    private GraphicUtil() {
        // Exists only to defeat instantiation.
    }

    public synchronized static GraphicUtil getInstance() {
        if (instance == null) {
            instance = new GraphicUtil();
        }
        return instance;
    }

    public Image iconToImage(Icon icon) {
        return iconToImage(icon, null);
    }

    public Icon getIcon(IconType icon) {
        switch (icon) {
            case INFORMATION:
                return (Icon) UIManager.get("OptionPane.informationIcon");
            case QUESTION:
                return (Icon) UIManager.get("OptionPane.questionIcon");
            case WARNING:
                return (Icon) UIManager.get("OptionPane.warningIcon");
            case ERROR:
                return (Icon) UIManager.get("OptionPane.errorIcon");
            case HELP:
                Image img = Utilities.loadImage("org/openide/resources/propertysheet/propertySheetHelp.png", true); //NOI18N      

                ImageIcon imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case CREATE:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Create.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case RETRIEVE:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Search.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case UPDATE:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Edit.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case DELETE:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Delete.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case SAVE:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Save.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            case RELOAD:
                img = Utilities.loadImage("de/linogistix/common/res/icon/Reload.png", true); //NOI18N      

                imgIcon = new ImageIcon(img); //NOI18N

                return imgIcon;
            default:
                return null;

        }
    }

    //Transparenz bleibt erhalten :)
    public Image iconToImage(Icon icon, Color backgroundColor) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        }
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
        }
        g.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        icon.paintIcon(this, image.getGraphics(), 0, 0);
        return image;
    }

    public String getImageNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.length() - 4);
    }

    public Dimension getIconPreferredSize(IconType icon) {
        ImageIcon image = new ImageIcon(GraphicUtil.getInstance().iconToImage(GraphicUtil.getInstance().getIcon(icon)));
        return new Dimension(image.getIconWidth(), image.getIconHeight());
    }

    public Image scaleIcon(ImageIcon iconImage, int width, int height) {
        Image image = iconImage.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return image;//testImage.getImage();

    }

    /*    public void saveImageToHarddisk() {
    ByteArrayOutputStream out = new ByteArrayOutputStream(0xfff);
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param;
    param = encoder.getDefaultJPEGEncodeParam(painting);
    param.setQuality(0.75f, false);
    encoder.encode(painting/*, param
    FileOutputStream fos = new FileOutputStream(getBasePath() + "spider/painterly" + frameCounter + ".jpg");
    fos.write(out.toByteArray());
    fos.close();
    out.close();
    }
    public static void makeScreenshot(int x, int y, String file_name) {
    try {
    BufferedImage shot =
    (new Robot()).createScreenCapture(new Rectangle(0, 0, x,
    y));
    OutputStream out =
    new BufferedOutputStream(new FileOutputStream(file_name + ".jpg"));
    JPEGImageEncoder enc = JPEGCodec.createJPEGEncoder(out);
    enc.encode(shot);
    out.close();
    } catch (Exception exc) {
    exc.printStackTrace();
    }
    }*/
    public void saveImage(Icon icon, String dateiName) {

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        icon.paintIcon(this, image.getGraphics(), 0, 0);

        try {
            FileOutputStream fos = new FileOutputStream(new File(dateiName));
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*    public void saveGif(Icon icon, String dateiname) {
    System.out.println("save gif");
    try {
    BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics g = image.getGraphics();
    g.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
    icon.paintIcon(this, image.getGraphics(), 0, 0);
    ImageIO.write(image, "GIF", new File("c:\\test.gif"));
    } catch (IOException ex) {
    Exceptions.printStackTrace(ex);
    }
    }*/
    public static BufferedImage convertType(BufferedImage source, int type) {
        int w = source.getWidth(),  h = source.getHeight();
        BufferedImage target = new BufferedImage(w, h, type);
        Graphics2D g = target.createGraphics();
        g.drawRenderedImage(source, null);
        g.dispose();
        return target;
    }

    public BufferedImage makeColorTransparent(Icon icon, Color color) {
        BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Src);
        icon.paintIcon(this, img.getGraphics(), 0, 0);
        g.dispose();
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                if (img.getRGB(j, i) == color.getRGB()) {
                    img.setRGB(j, i, 0x8F1C1C);
                }
            }
        }
        return img;
    }

    public void saveGif(Icon icon, String dateiname) {
        try {
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
            icon.paintIcon(this, image.getGraphics(), 0, 0);
            OutputStream output = new BufferedOutputStream(
                    new FileOutputStream(dateiname));
            //Remove the given color. Through that you can get better result by
            //making transparenz and choosen the removing color.
            image = makeColorTransparent(icon, Color.GREEN);
            //Reduce the color. Only 8 bit support the GIFEncoder
            image = convertType(image, BufferedImage.TYPE_BYTE_INDEXED);
            GIFEncoderGalileo encode = new GIFEncoderGalileo(image);
            encode.Write(output);
        } catch (AWTException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
