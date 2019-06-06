package jpen.Acquisition;

import Documentation.AboutDial;
import Dynamique2D.Dynamique2DFrame;
import FilesManipulation.DataChooserFrame;
import FilesManipulation.DataFileChooser;
import commons.writingZ.*;
import static commons.writingZ.Const.PIX;
import static commons.writingZ.Const.RET_CANCEL;
import static commons.writingZ.Const.RET_OK;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.*;
import jpen.PenManager;
import jpen.owner.multiAwt.AwtPenToolkit;

/**
 * SaisieMainFrame
 *
 * @author GILHODES JC <jc.gilhodes@orange.fr>
 */
public class SaisieMainFrame extends javax.swing.JFrame {

    /**
     * @return the dataFileChooser
     */
    public DataFileChooser getDataFileChooser() {
        return dataFileChooser;
    }

    private String version = "V.0000";
    private final StatusReport statusReport;
    private final FicheManip ficheManip;
    SegIdentity id;
    static InfosTextDisplay infosTextDisplay;
    ShowCurrentDataValues SCD;
    private DataChooserFrame dataChooserFrame;
    private DataFileChooser dataFileChooser;
    Dynamique2DFrame dynamique2DFrame;
    Dimension dim;
    JTabbedPane tabbedPane;
    //private PenCanvas2 penCanvas;
    private CanvasFrame canvasFrame;
    PenCanvas2 penCanvas;
    private Color couleurStylet = Color.BLACK;
    private Color couleurLever = Color.GRAY;
    private Color couleurFond = Color.YELLOW;

    private boolean traits;
    private boolean points;
    private boolean traitsPoints;

    boolean execute = false; // ecriture permise ou pas
    final JButton statusReportButton = new JButton("Status Report...");

    static StringBuilder chronique;
    File currentDirAs, currentDirAuto;
    String fileNameAs, fileNameAuto;
    static int nbFilesSaved = 0;
    String units = PIX;
    static float nbLignesMm = 100f; //nb de lignes / mm : 100 intuos 2, 200 intuos 3
    Dimension deskTopDim;
    private boolean ZDimPresente = false;

    /**
     * Creates new form SaisieMainFrame
     */
    public SaisieMainFrame() {
        PenManager penManager = AwtPenToolkit.getPenManager();
        penManager.pen.setFirePenTockOnSwing(true);
        penManager.pen.setFrequencyLater(40);
        penManager.pen.levelEmulator.setPressureTriggerForLeftCursorButton(0.5f);
        statusReport = new StatusReport(AwtPenToolkit.getPenManager());
        ficheManip = new FicheManip(this, true);
        if (ficheManip.getReturnStatus() == Const.RET_CANCEL) {
            Messages.errorMessage("Fichemanip", "Lancement annulé.");
            System.exit(0);

        }

        units = ficheManip.getUnits();
        nbLignesMm = ficheManip.getNbLignesMm();
        id = ficheManip.getIdentity();
        // penCanvas = new PenCanvas2(this, getFicheManip());
        canvasFrame = new CanvasFrame(this, getFicheManip());
        penCanvas = canvasFrame.getPenCanvas();
        couleurStylet = penCanvas.getCouleurStylet();
        couleurFond = penCanvas.getCouleurFond();
        couleurLever = penCanvas.getCouleurLever();

        initComponents();

        setScreenDimension(ficheManip);

        //jScrollPaneGraphie.getViewport().add(penCanvas);
//        canvasesPane.setAlignmentX(Component.CENTER_ALIGNMENT);
//        canvasesPane.add(penCanvas);
        this.jDesktopPane1.add(canvasFrame);
        canvasFrame.setVisible(true);
        try {
            canvasFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        // canvasesPane.setBorder(BorderFactory.createTitledBorder("Pen Enabled Components"));
        currentDirAuto = FicheManip.getCurrentDataDir();//new File(userDir);
        currentDirAs = FicheManip.getCurrentDataDir();

        initHistorique();
    }

    /**
     * Dimension ecran pour adaptation optimale de la fenetre SaisieFrame
     *
     * @param ficheManip
     *
     * @return
     */
    private Dimension setScreenDimension(FicheManip ficheManip) {
        dim = ficheManip.getDimEcran();
        this.setSize(dim);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);

        deskTopDim = new Dimension(dim.width - 10, dim.height - 82);

        jDesktopPane1.setPreferredSize(deskTopDim);
        jDesktopPane1.setMinimumSize(deskTopDim);

        canvasFrame.setPreferredSize(new Dimension(dim.width - 10, dim.height - 102));
        canvasFrame.setMinimumSize(new Dimension(dim.width - 10, dim.height - 102));
        canvasFrame.setSize(dim.width - 10, dim.height - 102);
        return dim;
    }

    private void initHistorique() {
        chronique = new StringBuilder();
        chronique.append("------    Chronique de la session ------\n");
        chronique.append("Tablette connectée : ").append(ficheManip.getTabletteStr()).append("\n");
        String ZDimStr = "Saisie dimension Z ";
        if (this.isZDimPresente()) {
            ZDimStr += "active";
        } else {
            ZDimStr += " absente";
        }
        chronique.append(ZDimStr).append("\n");
        infosTextDisplay = new InfosTextDisplay(chronique.toString(),
                "Chronique saisie", currentDirAuto);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupPression = new javax.swing.ButtonGroup();
        buttonGroupTraceMode = new javax.swing.ButtonGroup();
        jToolBar = new javax.swing.JToolBar();
        jToggleButton_Start = new javax.swing.JToggleButton();
        jLabelGoStop = new javax.swing.JLabel();
        jButtonSaisieFront = new javax.swing.JButton();
        jButtonEfface = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButtonJsonSave = new javax.swing.JButton();
        jButton_Refresh = new javax.swing.JButton();
        jButton_Image = new javax.swing.JButton();
        jButton_ShowData = new javax.swing.JButton();
        jButton_FicheManip = new javax.swing.JButton();
        jButton_Historique = new javax.swing.JButton();
        jButtonStatus = new javax.swing.JButton();
        jToggleButtonShowBoard = new javax.swing.JToggleButton();
        jTF_Status = new javax.swing.JTextField();
        jButton_OpenData = new javax.swing.JButton();
        jButton_DynamFileData = new javax.swing.JButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        jMenuDurete = new javax.swing.JMenu();
        jRadioButtonMenuItemDur = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemMoyen = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemGras = new javax.swing.JRadioButtonMenuItem();
        jMenuTraceMode = new javax.swing.JMenu();
        jRBItemModePoints = new javax.swing.JRadioButtonMenuItem();
        jRBItemModeTraits = new javax.swing.JRadioButtonMenuItem();
        jRBItemModePointsTraits = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        ItemTracePosterriori = new javax.swing.JCheckBoxMenuItem();
        jMenuCouleur = new javax.swing.JMenu();
        jMenuItemCouleur = new javax.swing.JMenuItem();
        jMenuItemColorFeuille = new javax.swing.JMenuItem();
        jMenuItemCouleurLever = new javax.swing.JMenuItem();
        Jmenu_Actions = new javax.swing.JMenu();
        Item_Go = new javax.swing.JMenuItem();
        Item_Stop = new javax.swing.JMenuItem();
        Item_Erase = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        Item_TraceDYnam = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        Item_SeuiPeriode = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar.setRollover(true);
        jToolBar.setMaximumSize(new java.awt.Dimension(42, 42));
        jToolBar.setMinimumSize(new java.awt.Dimension(32, 32));
        jToolBar.setPreferredSize(new java.awt.Dimension(42, 42));

        jToggleButton_Start.setBackground(new java.awt.Color(245, 245, 241));
        jToggleButton_Start.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/start.png"))); // NOI18N
        jToggleButton_Start.setToolTipText("Lancer ou arrêter l'acquisition");
        jToggleButton_Start.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButton_Start.setFocusable(false);
        jToggleButton_Start.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton_Start.setMaximumSize(new java.awt.Dimension(42, 42));
        jToggleButton_Start.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButton_Start.setOpaque(true);
        jToggleButton_Start.setPreferredSize(new java.awt.Dimension(42, 42));
        jToggleButton_Start.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton_Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_StartActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButton_Start);

        jLabelGoStop.setBackground(new java.awt.Color(255, 204, 204));
        jLabelGoStop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelGoStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
        jLabelGoStop.setToolTipText("Etat : en cours d'acquisition ou arret");
        jLabelGoStop.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelGoStop.setOpaque(true);
        jLabelGoStop.setPreferredSize(new java.awt.Dimension(42, 42));
        jToolBar.add(jLabelGoStop);

        jButtonSaisieFront.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/move-front-icon.png"))); // NOI18N
        jButtonSaisieFront.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonSaisieFront.setFocusable(false);
        jButtonSaisieFront.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaisieFront.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaisieFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaisieFrontActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSaisieFront);

        jButtonEfface.setBackground(new java.awt.Color(238, 250, 244));
        jButtonEfface.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButtonEfface.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/Eraser-2-icon.png"))); // NOI18N
        jButtonEfface.setToolTipText("Effacer le tracé affiché");
        jButtonEfface.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonEfface.setFocusable(false);
        jButtonEfface.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonEfface.setMaximumSize(new java.awt.Dimension(42, 42));
        jButtonEfface.setMinimumSize(new java.awt.Dimension(32, 32));
        jButtonEfface.setPreferredSize(new java.awt.Dimension(42, 42));
        jButtonEfface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEffaceActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonEfface);

        jButton1.setFont(new java.awt.Font("Lucida Grande", 3, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/StopSign.gif"))); // NOI18N
        jButton1.setToolTipText("Arret et enregistrement");
        jButton1.setActionCommand("Stop+Save");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar.add(jButton1);

        jButtonJsonSave.setBackground(new java.awt.Color(255, 255, 204));
        jButtonJsonSave.setFont(new java.awt.Font("Lucida Sans", 3, 12)); // NOI18N
        jButtonJsonSave.setForeground(new java.awt.Color(102, 0, 153));
        jButtonJsonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/SaveC_1.gif"))); // NOI18N
        jButtonJsonSave.setText("JSON");
        jButtonJsonSave.setToolTipText("Enregistrement format JSON");
        jButtonJsonSave.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, null, java.awt.Color.darkGray, java.awt.Color.lightGray));
        jButtonJsonSave.setFocusable(false);
        jButtonJsonSave.setMaximumSize(new java.awt.Dimension(42, 42));
        jButtonJsonSave.setMinimumSize(new java.awt.Dimension(42, 42));
        jButtonJsonSave.setOpaque(true);
        jButtonJsonSave.setPreferredSize(new java.awt.Dimension(80, 42));
        jButtonJsonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJsonSaveActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonJsonSave);

        jButton_Refresh.setBackground(new java.awt.Color(245, 245, 241));
        jButton_Refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/video.png"))); // NOI18N
        jButton_Refresh.setToolTipText("Reproduire le tracé sur l'écran");
        jButton_Refresh.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_Refresh.setFocusable(false);
        jButton_Refresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_Refresh.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_Refresh.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_Refresh.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_Refresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_RefreshActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_Refresh);

        jButton_Image.setBackground(new java.awt.Color(245, 245, 241));
        jButton_Image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/xvidcap.png"))); // NOI18N
        jButton_Image.setToolTipText("Capture de  l'image écran du tracé courant");
        jButton_Image.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_Image.setFocusable(false);
        jButton_Image.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_Image.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_Image.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_Image.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_Image.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_Image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ImageActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_Image);

        jButton_ShowData.setBackground(new java.awt.Color(245, 245, 241));
        jButton_ShowData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/kpdf.png"))); // NOI18N
        jButton_ShowData.setToolTipText("Afficher les donnees du tracé");
        jButton_ShowData.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_ShowData.setFocusable(false);
        jButton_ShowData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_ShowData.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_ShowData.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_ShowData.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_ShowData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_ShowData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_ShowDataActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_ShowData);

        jButton_FicheManip.setBackground(new java.awt.Color(245, 245, 241));
        jButton_FicheManip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/preferences-system.png"))); // NOI18N
        jButton_FicheManip.setToolTipText("Afficher, modifier la fiche de manip");
        jButton_FicheManip.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_FicheManip.setFocusable(false);
        jButton_FicheManip.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_FicheManip.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_FicheManip.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_FicheManip.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_FicheManip.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_FicheManip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FicheManipActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_FicheManip);

        jButton_Historique.setBackground(new java.awt.Color(245, 245, 241));
        jButton_Historique.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/historyParchemin.png"))); // NOI18N
        jButton_Historique.setToolTipText("Affiche  l'historique de la session.");
        jButton_Historique.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_Historique.setFocusable(false);
        jButton_Historique.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_Historique.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_Historique.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_Historique.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_Historique.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_Historique.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_HistoriqueActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_Historique);

        jButtonStatus.setBackground(new java.awt.Color(238, 250, 244));
        jButtonStatus.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jButtonStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/okteta.png"))); // NOI18N
        jButtonStatus.setToolTipText("Etat logiciel après chargement");
        jButtonStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonStatus.setFocusable(false);
        jButtonStatus.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStatusActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonStatus);

        jToggleButtonShowBoard.setBackground(new java.awt.Color(238, 250, 244));
        jToggleButtonShowBoard.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        jToggleButtonShowBoard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/new-edit-find-replace.png"))); // NOI18N
        jToggleButtonShowBoard.setToolTipText("Affiche/cache l'affichage saisie courante (x,y, p ...)");
        jToggleButtonShowBoard.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonShowBoard.setFocusable(false);
        jToggleButtonShowBoard.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jToggleButtonShowBoard.setMaximumSize(new java.awt.Dimension(42, 42));
        jToggleButtonShowBoard.setMinimumSize(new java.awt.Dimension(32, 32));
        jToggleButtonShowBoard.setPreferredSize(new java.awt.Dimension(42, 42));
        jToggleButtonShowBoard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonShowBoardActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonShowBoard);

        jTF_Status.setEditable(false);
        jTF_Status.setBackground(new java.awt.Color(204, 255, 255));
        jTF_Status.setFont(new java.awt.Font("Bitstream Vera Sans", 1, 10)); // NOI18N
        jTF_Status.setText("....");
        jTF_Status.setToolTipText("Informations courantes");
        jTF_Status.setPreferredSize(new java.awt.Dimension(800, 30));
        jToolBar.add(jTF_Status);

        jButton_OpenData.setBackground(new java.awt.Color(245, 245, 241));
        jButton_OpenData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/folder_txt.png"))); // NOI18N
        jButton_OpenData.setToolTipText("Ouvrir un fichier et afficher le tracé");
        jButton_OpenData.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_OpenData.setFocusable(false);
        jButton_OpenData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_OpenData.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_OpenData.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_OpenData.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_OpenData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_OpenData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OpenDataActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_OpenData);

        jButton_DynamFileData.setBackground(new java.awt.Color(245, 245, 241));
        jButton_DynamFileData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/black-pages-icon.png"))); // NOI18N
        jButton_DynamFileData.setToolTipText("Trace dynamique des données du fichier courant");
        jButton_DynamFileData.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton_DynamFileData.setFocusable(false);
        jButton_DynamFileData.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_DynamFileData.setMaximumSize(new java.awt.Dimension(42, 42));
        jButton_DynamFileData.setMinimumSize(new java.awt.Dimension(32, 32));
        jButton_DynamFileData.setPreferredSize(new java.awt.Dimension(42, 42));
        jButton_DynamFileData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton_DynamFileData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DynamFileDataActionPerformed(evt);
            }
        });
        jToolBar.add(jButton_DynamFileData);

        getContentPane().add(jToolBar, java.awt.BorderLayout.NORTH);

        jDesktopPane1.setMinimumSize(new java.awt.Dimension(2000, 1500));
        jDesktopPane1.setPreferredSize(new java.awt.Dimension(2000, 1800));
        getContentPane().add(jDesktopPane1, java.awt.BorderLayout.EAST);

        menuBar.setMaximumSize(new java.awt.Dimension(473, 42));
        menuBar.setPreferredSize(new java.awt.Dimension(473, 40));

        fileMenu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/folder_txt.png"))); // NOI18N
        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.setBorderPainted(true);
        fileMenu.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, 0));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenuDurete.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenuDurete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/poedit.png"))); // NOI18N
        jMenuDurete.setText("Stylet");
        jMenuDurete.setToolTipText("Réglage des caracteristiques du stylet");
        jMenuDurete.setBorderPainted(true);
        jMenuDurete.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        buttonGroupPression.add(jRadioButtonMenuItemDur);
        jRadioButtonMenuItemDur.setText("Dur");
        jRadioButtonMenuItemDur.setToolTipText("Sensibilite pression faible");
        jRadioButtonMenuItemDur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemDurActionPerformed(evt);
            }
        });
        jMenuDurete.add(jRadioButtonMenuItemDur);

        buttonGroupPression.add(jRadioButtonMenuItemMoyen);
        jRadioButtonMenuItemMoyen.setSelected(true);
        jRadioButtonMenuItemMoyen.setText("Moyen");
        jRadioButtonMenuItemMoyen.setToolTipText("Sensibilite pression normale");
        jRadioButtonMenuItemMoyen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemMoyenActionPerformed(evt);
            }
        });
        jMenuDurete.add(jRadioButtonMenuItemMoyen);

        buttonGroupPression.add(jRadioButtonMenuItemGras);
        jRadioButtonMenuItemGras.setText("Gras");
        jRadioButtonMenuItemGras.setToolTipText("Sensibilite pression forte");
        jRadioButtonMenuItemGras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemGrasActionPerformed(evt);
            }
        });
        jMenuDurete.add(jRadioButtonMenuItemGras);

        menuBar.add(jMenuDurete);

        jMenuTraceMode.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenuTraceMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/chart_curves.png"))); // NOI18N
        jMenuTraceMode.setText("Trace");
        jMenuTraceMode.setToolTipText("Type de trace : points, traits ....");
        jMenuTraceMode.setBorderPainted(true);
        jMenuTraceMode.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        buttonGroupTraceMode.add(jRBItemModePoints);
        jRBItemModePoints.setText("Points");
        jRBItemModePoints.setToolTipText("");
        jRBItemModePoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBItemModePointsActionPerformed(evt);
            }
        });
        jMenuTraceMode.add(jRBItemModePoints);

        buttonGroupTraceMode.add(jRBItemModeTraits);
        jRBItemModeTraits.setSelected(true);
        jRBItemModeTraits.setText("Traits");
        jRBItemModeTraits.setToolTipText("");
        jRBItemModeTraits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBItemModeTraitsActionPerformed(evt);
            }
        });
        jMenuTraceMode.add(jRBItemModeTraits);

        buttonGroupTraceMode.add(jRBItemModePointsTraits);
        jRBItemModePointsTraits.setText("Points/traits");
        jRBItemModePointsTraits.setToolTipText("");
        jRBItemModePointsTraits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBItemModePointsTraitsActionPerformed(evt);
            }
        });
        jMenuTraceMode.add(jRBItemModePointsTraits);
        jMenuTraceMode.add(jSeparator1);

        ItemTracePosterriori.setText("Tracer après");
        ItemTracePosterriori.setToolTipText("Afficher le tracé à la fin de la saisie");
        jMenuTraceMode.add(ItemTracePosterriori);

        menuBar.add(jMenuTraceMode);

        jMenuCouleur.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jMenuCouleur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/gnome-mime-application-x-theme.png"))); // NOI18N
        jMenuCouleur.setText("Couleurs");
        jMenuCouleur.setToolTipText("Options des couleurs feuille, traces ...");
        jMenuCouleur.setBorderPainted(true);
        jMenuCouleur.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        jMenuItemCouleur.setText("Trace");
        jMenuItemCouleur.setToolTipText("Couleur  du tracé");
        jMenuItemCouleur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCouleurActionPerformed(evt);
            }
        });
        jMenuCouleur.add(jMenuItemCouleur);

        jMenuItemColorFeuille.setText("Fond");
        jMenuItemColorFeuille.setToolTipText("Couleur de fond , efface tout !");
        jMenuItemColorFeuille.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemColorFeuilleActionPerformed(evt);
            }
        });
        jMenuCouleur.add(jMenuItemColorFeuille);

        jMenuItemCouleurLever.setText("Lever");
        jMenuItemCouleurLever.setToolTipText("Couleur de la trace  lever");
        jMenuItemCouleurLever.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCouleurLeverActionPerformed(evt);
            }
        });
        jMenuCouleur.add(jMenuItemCouleurLever);

        menuBar.add(jMenuCouleur);

        Jmenu_Actions.setBackground(new java.awt.Color(241, 241, 239));
        Jmenu_Actions.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Jmenu_Actions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/package_system.png"))); // NOI18N
        Jmenu_Actions.setText("Actions");
        Jmenu_Actions.setToolTipText("Lancer, arreter, effacer...");
        Jmenu_Actions.setBorderPainted(true);
        Jmenu_Actions.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Jmenu_Actions.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        Item_Go.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        Item_Go.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        Item_Go.setText("Démarrer..");
        Item_Go.setToolTipText("Commencer l'acquisition du tracé");
        Item_Go.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_GoActionPerformed(evt);
            }
        });
        Jmenu_Actions.add(Item_Go);

        Item_Stop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        Item_Stop.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        Item_Stop.setText("Arrêter..");
        Item_Stop.setToolTipText("Arrêter l'acquisition du tracé");
        Item_Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_StopActionPerformed(evt);
            }
        });
        Jmenu_Actions.add(Item_Stop);

        Item_Erase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        Item_Erase.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        Item_Erase.setText("Effacer");
        Item_Erase.setToolTipText("Effacer le tracé et les données.");
        Item_Erase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_EraseDataTrace(evt);
            }
        });
        Jmenu_Actions.add(Item_Erase);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem1.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        jMenuItem1.setText("Arreter+Enregister");
        jMenuItem1.setToolTipText("Arreter et enregistrer l'acquisition du tracé");
        Jmenu_Actions.add(jMenuItem1);
        Jmenu_Actions.add(jSeparator8);

        Item_TraceDYnam.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, 0));
        Item_TraceDYnam.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        Item_TraceDYnam.setText("Retracer");
        Item_TraceDYnam.setToolTipText("Reproduire le tracé sur l'écran");
        Item_TraceDYnam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_TraceDYnamActionPerformed(evt);
            }
        });
        Jmenu_Actions.add(Item_TraceDYnam);
        Jmenu_Actions.add(jSeparator9);

        Item_SeuiPeriode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK));
        Item_SeuiPeriode.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        Item_SeuiPeriode.setText("Seuil periode..");
        Item_SeuiPeriode.setToolTipText("Intervalle minimum accepté");
        Item_SeuiPeriode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Item_SeuiPeriodeActionPerformed(evt);
            }
        });
        Jmenu_Actions.add(Item_SeuiPeriode);

        menuBar.add(Jmenu_Actions);

        helpMenu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        helpMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/help-contents.png"))); // NOI18N
        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");
        helpMenu.setBorderPainted(true);
        helpMenu.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void jButtonStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStatusActionPerformed

        JTextArea textArea = new JTextArea();
        final JScrollPane panel = new JScrollPane(textArea);
        panel.setPreferredSize(new Dimension(1200, 800));
        textArea.setText(getStatusReport().toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setTabSize(1);
        textArea.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, panel, "JPen Status Report", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButtonStatusActionPerformed

    private void jToggleButtonShowBoardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonShowBoardActionPerformed
        if (SCD == null) {
            SCD = new ShowCurrentDataValues();
        } 
            SCD.setVisible(!SCD.isVisible());
        
    }//GEN-LAST:event_jToggleButtonShowBoardActionPerformed

    private void jRadioButtonMenuItemMoyenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemMoyenActionPerformed
        dureteCrayon();
    }//GEN-LAST:event_jRadioButtonMenuItemMoyenActionPerformed

    private void jRadioButtonMenuItemGrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemGrasActionPerformed
        dureteCrayon();
    }//GEN-LAST:event_jRadioButtonMenuItemGrasActionPerformed

    private void jRadioButtonMenuItemDurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemDurActionPerformed
        dureteCrayon();
    }//GEN-LAST:event_jRadioButtonMenuItemDurActionPerformed

    private void jMenuItemCouleurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCouleurActionPerformed
        couleurStylet = JColorChooser.showDialog(
                this,
                "Choisir la couleur du crayon",
                couleurStylet);
        getPenCanvas().setCouleurStylet(couleurStylet);
    }//GEN-LAST:event_jMenuItemCouleurActionPerformed

    private void jButtonEffaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEffaceActionPerformed
        getPenCanvas().efface(couleurFond);
        //this.update(this.getGraphics());
        statusEtHistorique(Utils.getDate_TimeNow()[1] + " : données effacées", true);
    }//GEN-LAST:event_jButtonEffaceActionPerformed

    private void jRBItemModePointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBItemModePointsActionPerformed
        changeTraceMode();
    }//GEN-LAST:event_jRBItemModePointsActionPerformed

    private void jRBItemModeTraitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBItemModeTraitsActionPerformed
        changeTraceMode();
    }//GEN-LAST:event_jRBItemModeTraitsActionPerformed

    private void jRBItemModePointsTraitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBItemModePointsTraitsActionPerformed
        changeTraceMode();
    }//GEN-LAST:event_jRBItemModePointsTraitsActionPerformed

    private void jMenuItemColorFeuilleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemColorFeuilleActionPerformed
        couleurFond = JColorChooser.showDialog(
                this,
                "Choisir la couleur du fond",
                couleurFond);
        //System.out.println("Couleur choix fond "+couleurFond.toString());
        getPenCanvas().efface(couleurFond);
        this.update(this.getGraphics());

    }//GEN-LAST:event_jMenuItemColorFeuilleActionPerformed

    private void jMenuItemCouleurLeverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCouleurLeverActionPerformed
        couleurLever = JColorChooser.showDialog(
                this,
                "Choisir la couleur du lever",
                couleurLever);
        getPenCanvas().setCouleurLever(couleurLever);
    }//GEN-LAST:event_jMenuItemCouleurLeverActionPerformed

    private void Item_GoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_GoActionPerformed
        jToggleButton_Start.setSelected(true);
        startStopAction();
    }//GEN-LAST:event_Item_GoActionPerformed

    private void Item_StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_StopActionPerformed
        jToggleButton_Start.setSelected(false);
        startStopAction();
    }//GEN-LAST:event_Item_StopActionPerformed

    private void Item_EraseDataTrace(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_EraseDataTrace
        effacer();
    }//GEN-LAST:event_Item_EraseDataTrace

    private void Item_TraceDYnamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_TraceDYnamActionPerformed
        
        penCanvas.effaceTrace(couleurFond);
        this.penCanvas.retracer();
    }//GEN-LAST:event_Item_TraceDYnamActionPerformed

    private void Item_SeuiPeriodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Item_SeuiPeriodeActionPerformed
//        SeuilPeriodeDial tempDial = new SeuilPeriodeDial(this, true, seuillagePeriode);
//        int retour = tempDial.getReturnStatus();
//        if (retour == RET_OK) {
//            seuillagePeriode = tempDial.getNewSeuilPeriode();
//            statusEtHistorique("Nouvelle p�riode : " + seuillagePeriode, true);
//        }
//        if (retour == RET_CANCEL) {
//            statusEtHistorique("P�riode inchang�e : " + seuillagePeriode, true);
//        }
    }//GEN-LAST:event_Item_SeuiPeriodeActionPerformed

    private void jButton_DynamFileDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DynamFileDataActionPerformed
        if (dataChooserFrame == null) {
            openDataFile();
            return;
        }

        if (this.dynamique2DFrame == null) {
            try {
                dynamique2DFrame = new Dynamique2DFrame(this);
                jDesktopPane1.add(dynamique2DFrame);
               jDesktopPane1.getDesktopManager().maximizeFrame(dynamique2DFrame);
                dynamique2DFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        dataFileChooser = dataChooserFrame.getDataFileChooser();
        if (dataFileChooser != null) {
            dynamique2DFrame.setVisible(true);
            dynamique2DFrame.moveToFront();
            jDesktopPane1.getDesktopManager().maximizeFrame(dynamique2DFrame);
            dynamique2DFrame.setData(dataFileChooser);
            dataChooserFrame.moveToBack();
            canvasFrame.moveToBack();
        }
    }//GEN-LAST:event_jButton_DynamFileDataActionPerformed

    private void jButton_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_RefreshActionPerformed
        if (penCanvas.getSampleList().isEmpty()) {
            Messages.warningMessage("SaisieTrace", " Aucune donnée disponible !");
            return;
        }
        penCanvas.retracer();
    }//GEN-LAST:event_jButton_RefreshActionPerformed

    private void jButton_HistoriqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_HistoriqueActionPerformed
        afficherInfos();
    }//GEN-LAST:event_jButton_HistoriqueActionPerformed

    private void jButton_ShowDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ShowDataActionPerformed
        ShowDataTable showData = new ShowDataTable(this, true, this.getPenCanvas().getSampleList());
    }//GEN-LAST:event_jButton_ShowDataActionPerformed

    private void jButton_FicheManipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FicheManipActionPerformed
        this.ficheManip.setVisible(true);
    }//GEN-LAST:event_jButton_FicheManipActionPerformed

    private void jButton_ImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_ImageActionPerformed

        captureImageTrace();
    }//GEN-LAST:event_jButton_ImageActionPerformed

    private void jButton_OpenDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_OpenDataActionPerformed
        openDataFile();
    }//GEN-LAST:event_jButton_OpenDataActionPerformed

    private void jToggleButton_StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_StartActionPerformed
        startStopAction();
    }//GEN-LAST:event_jToggleButton_StartActionPerformed

    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        save();
    }//GEN-LAST:event_saveMenuItemActionPerformed

    private void jButtonJsonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJsonSaveActionPerformed
       save();
        
    }//GEN-LAST:event_jButtonJsonSaveActionPerformed

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        openDataFile();
    }//GEN-LAST:event_openMenuItemActionPerformed

    
  private void save(){
      if (jToggleButton_Start.isSelected()) {
            this.jTF_Status.setText("Saisie toujours en cours !");
        } else {
            JSonDataEnregistrement jsde = new JSonDataEnregistrement(this);
        }
  }  
    
    
    
    
    
    private void jButtonSaisieFrontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaisieFrontActionPerformed
        System.out.println("Event clic bouton afficher");
        if (!canvasFrame.isSelected()) {
            if(dataChooserFrame!=null){
                dataChooserFrame.moveToBack();
            }
            canvasFrame.moveToFront();
            try {
                canvasFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            if(dataChooserFrame!=null){
                dataChooserFrame.moveToFront();
            }
            canvasFrame.moveToBack();
            try {
                canvasFrame.setSelected(false);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
    }//GEN-LAST:event_jButtonSaisieFrontActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDial aboutDial = new AboutDial(this, true);
        
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jToggleButton_Start.setSelected(false);
        startStopAction();
        save();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void cacherBars() {
        this.jToolBar.setVisible(!jToolBar.isVisible());
        this.menuBar.setVisible(!menuBar.isVisible());
    }

    public void startStopAction() {
        if (jToggleButton_Start.isSelected()) {
            jLabelGoStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledgreen.png"))); // NOI18N
            jLabelGoStop.setBackground(new java.awt.Color(0, 200, 0));
        } else {
            jLabelGoStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
            jLabelGoStop.setBackground(new java.awt.Color(255, 0, 0));
        }
        execute = jToggleButton_Start.isSelected();
        canvasFrame.moveToFront();
        try {
            canvasFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        getPenCanvas().setWriteEnable(jToggleButton_Start.isSelected());

    }

    public void effacer() {
        getPenCanvas().efface(couleurFond);
        this.update(this.getGraphics());

        statusEtHistorique(Utils.getDate_TimeNow()[1] + " : données effacées", true);
    }

    private void dureteCrayon() {
        getPenCanvas().setCoeffPenPression(20f);
        if (jRadioButtonMenuItemMoyen.isSelected()) {
            this.getPenCanvas().setCoeffPenPression(10f);
        } else if (jRadioButtonMenuItemDur.isSelected()) {
            this.getPenCanvas().setCoeffPenPression(5f);
        }
    }

    private void changeTraceMode() {
        setTraits(this.jRBItemModeTraits.isSelected());
        setPoints(this.jRBItemModePoints.isSelected());
        setTraitsPoints(this.jRBItemModePointsTraits.isSelected());
        getPenCanvas().setTraceModePoints(points);
        getPenCanvas().setTraceModeTraits(traits);
        getPenCanvas().setTraceModeTraistPoints(traitsPoints);
    }

    private void getFicheManipItems() {
        getFicheManip().setVisible(true);
        int retour = getFicheManip().getReturnStatus();
        if (retour == RET_OK) {
            currentDirAuto = getFicheManip().getCurrentDataDir();
            fileNameAuto = getFicheManip().getRacineName();
            nbFilesSaved = getFicheManip().getInitNum();
            units = getFicheManip().getUnits();
            nbLignesMm = getFicheManip().getNbLignesMm();
            statusEtHistorique("Fiche manip : Enregistrera dans : " + currentDirAuto.getPath()
                    + File.separator + fileNameAuto, true);
            infosTextDisplay.setCurrentDir(currentDirAuto);
            statusEtHistorique("Modifications fiche manip ", false);
        }
        if (retour == RET_CANCEL) {
            statusEtHistorique("Modifications fiche manip  annulées", false);
        }
    }

    private File openDataFile() {
        System.out.println(" OPen data file ()");
        if (dataChooserFrame == null) {
            System.out.println("Création dataChooseFrame");
            //canvasFrameActive(false);
            dataChooserFrame = new DataChooserFrame(this);
            dataFileChooser = dataChooserFrame.getDataFileChooser();
            jDesktopPane1.add(dataChooserFrame);
            jDesktopPane1.getDesktopManager().maximizeFrame(dataChooserFrame);
             jDesktopPane1.getDesktopManager().minimizeFrame(dataChooserFrame);
            canvasFrame.moveToBack();
            dataChooserFrame.moveToFront();

            try {
                dataChooserFrame.setSelected(true);
               
            } catch (PropertyVetoException ex) {
                Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            dataChooserFrame.setVisible(true);
            dataChooserFrame.moveToFront();
            jDesktopPane1.getDesktopManager().minimizeFrame(dataChooserFrame);
            canvasFrame.moveToBack();
            try {
                dataChooserFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
                Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return dataFileChooser.getCurrentDataFile();
    }

    private void canvasFrameActive(boolean active) {
        System.out.println(" canvasFrame active " + active);
        try {
            canvasFrame.setSelected(active);
            canvasFrame.setVisible(active);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void dataChooserFrameActive(boolean active) {
        System.out.println(" dataChooserFrameActive " + active);
        try {
            dataChooserFrame.setSelected(active);
            dataChooserFrame.setVisible(active);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(SaisieMainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the traits
     */
    public boolean isTraits() {
        return traits;
    }

    /**
     * @param traits the traits to set
     */
    public void setTraits(boolean traits) {
        this.traits = traits;
    }

    /**
     * @return the points
     */
    public boolean isPoints() {
        return points;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(boolean points) {
        this.points = points;
    }

    /**
     * @return the traitsPoints
     */
    public boolean isTraitsPoints() {
        return traitsPoints;
    }

    /**
     * @param traitsPoints the traitsPoints to set
     */
    public void setTraitsPoints(boolean traitsPoints) {
        this.traitsPoints = traitsPoints;
    }

    public String statusEtHistorique(String str, boolean status) {
        if (jTF_Status != null && status) {
            jTF_Status.setText(str);
        }
        str += "\n";
        if (chronique == null) {
            chronique = new StringBuilder();
        }
        chronique.append(str);
        return chronique.toString();
    }

    public void afficherInfos() {
        StringBuilder bilan = new StringBuilder();
        bilan.append(getFicheManip().bilanSession.getBilanSession());
        bilan.append(chronique);
        infosTextDisplay.setText(bilan.toString());
        infosTextDisplay.setVisible(!infosTextDisplay.isVisible());
    }

    private void captureImageTrace() {
        if (penCanvas.getSampleList().isEmpty()) {
            Messages.warningMessage("SaisieTrace", " Aucune donnée disponible !");
            return;
        }

        BufferedImage image;
        try {
            Robot robot = new Robot();
            image = robot.createScreenCapture(getPenCanvas().getVisibleRect());
        } catch (AWTException ex) {
            Messages.errorMessage("SaisieTrace : \n",
                    "Capture Image du tracé" + ex.getMessage());
            statusEtHistorique("Echec capture image", true);
            return;
        }
        String fileName = "HistSaisie_" + commons.writingZ.Utils.getDate_TimeNow()[0] + "_"
                + commons.writingZ.Utils.getDate_TimeNow()[1];
        String filePath = currentDirAuto + File.separator + getFicheManip().getIdSujet()
                + commons.writingZ.Utils.getDate_TimeNow()[0] + "_"
                + commons.writingZ.Utils.getDate_TimeNow()[1];
        filePath += "." + "png";

        PlanarImage planarImage = PlanarImage.wrapRenderedImage(image);
        JAI.create("filestore", planarImage, filePath, "PNG", null);
        statusEtHistorique("Image du tracé enregistrée dans : " + filePath, true);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SaisieMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SaisieMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SaisieMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SaisieMainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SaisieMainFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBoxMenuItem ItemTracePosterriori;
    private javax.swing.JMenuItem Item_Erase;
    private javax.swing.JMenuItem Item_Go;
    private javax.swing.JMenuItem Item_SeuiPeriode;
    private javax.swing.JMenuItem Item_Stop;
    private javax.swing.JMenuItem Item_TraceDYnam;
    private javax.swing.JMenu Jmenu_Actions;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.ButtonGroup buttonGroupPression;
    private javax.swing.ButtonGroup buttonGroupTraceMode;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonEfface;
    private javax.swing.JButton jButtonJsonSave;
    private javax.swing.JButton jButtonSaisieFront;
    private javax.swing.JButton jButtonStatus;
    private javax.swing.JButton jButton_DynamFileData;
    private javax.swing.JButton jButton_FicheManip;
    private javax.swing.JButton jButton_Historique;
    private javax.swing.JButton jButton_Image;
    private javax.swing.JButton jButton_OpenData;
    private javax.swing.JButton jButton_Refresh;
    private javax.swing.JButton jButton_ShowData;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabelGoStop;
    private javax.swing.JMenu jMenuCouleur;
    private javax.swing.JMenu jMenuDurete;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemColorFeuille;
    private javax.swing.JMenuItem jMenuItemCouleur;
    private javax.swing.JMenuItem jMenuItemCouleurLever;
    private javax.swing.JMenu jMenuTraceMode;
    private javax.swing.JRadioButtonMenuItem jRBItemModePoints;
    private javax.swing.JRadioButtonMenuItem jRBItemModePointsTraits;
    private javax.swing.JRadioButtonMenuItem jRBItemModeTraits;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemDur;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemGras;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemMoyen;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    public javax.swing.JTextField jTF_Status;
    private javax.swing.JToggleButton jToggleButtonShowBoard;
    private javax.swing.JToggleButton jToggleButton_Start;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the penCanvas
     */
    public PenCanvas2 getPenCanvas() {
        return penCanvas;
    }

    public CanvasFrame getPenCanvasFrame() {
        return canvasFrame;
    }

    /**
     * @return the ficheManip
     */
    public FicheManip getFicheManip() {
        return ficheManip;
    }

    /**
     * @return the statusReport
     */
    public StatusReport getStatusReport() {
        return statusReport;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<SampleDoubleZ> getSamplesList() {
        ArrayList<SampleDoubleZ> sdList = penCanvas.getSampleList();
        return sdList;
    }

    /**
     * @return the ZDimPresente
     */
    public boolean isZDimPresente() {
        return ZDimPresente;
    }

    /**
     * @param ZDimPresente the ZDimPresente to set
     */
    public void setZDimPresente(boolean ZDimPresente) {
        this.ZDimPresente = ZDimPresente;
    }

    public Dimension getDeskTopPaneDim() {

        return deskTopDim;
    }
}
