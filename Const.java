/*

 */

package commons.writingZ;


/**
 * Const.java
 *
 * Created on 31 decembre 2006, 12:09
 *
 * 9 oct 2010 ajout des constantes de designation des variables
 * 16 Nov 2010 ajout Intuos4 avec passage des dimensions h x l en double
 * de la classe "sample"
 * 
 * 17 nov 2017 Ajout de la dimension Z 
 * @author gilhodes
 */
public interface Const {
    
    /** Caracteres speciaux */
    public static  final String LF ="\n"; 
    public static  final String TAB ="\t"; 
    
    /** Etats de la position 'tip' du stylet par rapport au plan d'ecriture */
    public static final int HORS_CHAMP = -1;// pas mesurable
    public static final int EN_CONTACT = 1;// en contact avec le plan d'écriture
    public static final int LEVER = 0;// sans contact mais mesurable Z et autres variables
    public static final int TRAIT = 1;// en contact avec le plan d'écriture
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_DEFAULT =2;
    /** Type de quadrillage de la feuille (ecran): sans quadrillage*/

    public static final int UNIFORME=0;
    /** Type de quadrillage de la feuille (ecran): maternelle*/
    public static final int MATERNEL=1;
    /** Type de quadrillage de la feuille (ecran): ecole*/
    public static final int GROS_CARREAUX = 2;
    /** Type de quadrillage de la feuille (ecran): petits carreaux*/
    public static final int PETITS_CARREAUX = 3;
    /** Type de fond de la feuille (ecran): mouchete*/
    public static final int MOUCHETE = 4;

    public static final String [] quadName={"uni", "maternel", "gros carreaux"
            ,"petits carreaux", "mouchete"};

    public static final String [] bandName={"Red", "Green", "Blue"};
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    // Wacom tablettes Intuos 3
    public final static int I3_A6 = 0;//Intuos 3 A6
    public final static int I3_A5 = 1;
    public final static int I3_A5W = 2;// A5 Wide
    public final static int I3_A4 = 3;
    public final static int I3_A4OS = 4;//A4 OverSize
    public final static int I3_A3W = 5;// A3 Wide
     // Wacom tablettes Intuos 2
    public final static int I2_A5 = 6;
    public final static int I2_A4R = 7;
    public final static int I2_A4OS = 8;//A4 OverSize
    public final static int I2_A3W = 9;// A3 Wide
    // Wacom tablettes Intuos 4
    public final static int I4_SMALL = 10;
    public final static int I4_MEDIUM = 11;
    public final static int I4_LARGE= 12;
    public final static int I4_XL = 13;
    public final static int I4_WIRELESS = 14;
     // Wacom tablettes Intuos Pro
    public final static int PRO_M = 15;
    public final static int PRO_L = 16;
     // Wacom tablettes Cintiq 22HD
    public final static int CINTIQ_22HD = 17;
 


    // largeurs Intuos  en  mm
    public final static double[] largeurs =
            new double[]{ 157.5, 203.2, 271, 304.8, 304.8, 487.7,// intuos 3
             203,305,305,457, // intuos 2
            157.5, 223.2, 325.1, 462, 203.2, // intuos 4
            224, 325, // pro M et L
            471}; // cintiq 22HD
    
    // Hauteurs Intuos  en  mm
    public final static double[] hauteurs =
            new double[]{ 98.4,152.4,158.8,228.6,304.8,304.8,// intuos 3
            162,241,317,317, // intuos 2
            98.4,139.7, 203.2, 304.8, 127, // intuos 4
            148, 203,  // pro M et L    
            271 // cintiq 22HD         
    };

    /**
     * Niveau de pression
     */
    
    public final static double[] pressions =
            new double[]{ 1024,1024,1024,1024,1024,1024,// intuos 3
            1024,1024,1024,1024, // intuos 2
            2048,2048,2048,2048,2048, // intuos 4
            8192, 8192,  // pro M et L    
            2048 // cintiq 22HD         
    };
    
    // Denomination Intuos 3 2 4

    /**
     *
     */
    public final static String[] tabletNames =
            new String[]{"Intuos 3 A6", "Intuos 3 A5","Intuos 3 A5 Wide",
    "Intuos 3 A4", "Intuos 3 A4 OverSize","Intuos 3 A3 Wide",
    "Intuos 2 A5", "Intuos 2 A4 Regular", "Intuos 2 A4 OverSize", "Intuos 2 A3",
     "Intuos 4 SMALL","Intuos 4 MEDIUM","Intuos 4 LARGE","Intuos 4 XL",
    "Intuos 4 WireLess", "Intuos Pro M", "Intuos Pro L", "Cintiq 22HD"};
    
    public final static String MM = "mm";
    public final static String CMM = "mm/100";
    public final static String MSEC = "ms";
    public final static String SEC = "s";
    public final static String MM_SEC = "mm/sec";
    public final static String PIX = "pixels";
    public final static String RADIAN = "radian";
    public final static String DEGRES = "degres";

    public final static int TXT_FILTER = 0;
    public final static int SEGTXT_FILTER = 1;
    public final static int LISTTXT_FILTER = 2;
    public final static int MEANTXT_FILTER = 2;
    public final static int JSON_FILTER = 4;
    public final static int ARFF_FILTER = 5;

    public final static int DOTS = 0;
    public final static int LINES = 1;

    public final static int DUREE_MAX = 0;
    public final static int DUREE_MOY = 1;
    public final static int DUREE_MIN = 2;
    public final static int NB_FIXE = 3;


    /** Constante de designation des variables de la classe sample :
            int xReel, int yReel, int xScreen, int yScreen,
            long interv, long t, int p, int az, int al 
            * ajout de Z (17 11 2017)
            */

    public final static int NO_SAMPLE = -1;
    public final static int XREEL = 0;
    public final static int YREEL = 1;
    public final static int XSCREEN = 2;
    public final static int YSCREEN = 3;
    public final static int INTERV = 4;
    public final static int TEMPS = 5;
    public final static int PRESSION = 6;
    public final static int AZIMUTH = 7;
    public final static int AL = 8;
    
    public final static int ZREEL = 9;
    public final static int ZSCREEN = 10;

    public final static String[]  varSampleStr = {
        "X(t)","Y(t)","Xsc(t)","Ysc(t)",
        "Interval t ", "T", "Pression",
        "Az.", "Al.","Z(t)","Zsc(t)"
    };
     /** Designation int des types de Format images utilises*/
    static final int TIFF = 0;
    static final int PNG = 1;
    static final int JPEG = 2;
    static final int BMP = 3;
    static final int PICT = 4;

    static final int UNKNOWN = 5;
    /** Labels des ROISShape dans l'ordre  des int (ci dessus) */
    static final String[] formatImageStr = {"tif", "png", "jpg",
        "bmp", "pct", "??"
    };


    /** Options de Segmentation */
    public final static int SEG_TIP = 0;//Seuil?
    public final static int SEG_P = 1;//Seuil pression
    public final static int SEG_V = 2;//Seuil Vitesse
    
    
    
    /** Options d'interpolation de org.apache.commons.math.analysis.interpolation */
    public final static int SPLINE_INTERP = 0;
    public final static int LINEAR_INTERP = 1;
    public final static int LOESS_INTERP = 2;
    public final static int NEVILLE_INTERP = 3;
    public final static int DIV_DIFF_INTERP = 4;
    
    /** Liste des options d'interpolation (garder dans l'ordre des options ci dessus)*/
    public final  String[] typeInterpolation =  {"Spline", "Linear", "Loess","Neville", "Div_Diff" };
    
}

