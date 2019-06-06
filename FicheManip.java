package jpen.Acquisition;

import commons.writingZ.Const;
import commons.writingZ.SegIdentity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
/**
 * FicheManip .java
 *
 * Created on 31 decembre 2006, 10:06
 * 
 *2017  Reprise dans le contexte Saisie avec JPen
 * 22 Janvier 2018 ajout de coeffConvZ coeff de conversion de Z (à déterminer)
 *
 * @author JC Gilhodes
 */
public class FicheManip extends javax.swing.JDialog implements Const {

    SaisieMainFrame parent;
    
    SegIdentity idSeg, previousId;
    
    BilanSession bilanSession;
    private String idSujet;
    private String ageSujet;
    private String lateralisatSujet ;
    private String  sexeSujet ;
    private  String specificitySujet ;
    private String idOperateur;
    private String date;
    private String heure;
    
    private float largeurTablette;
    private float hauteurTablette;
    private float largeurEcran;
    private float hauteurEcran;
    private Dimension dimEcran;
    private Dimension dimTablette;
    private boolean wacomPresent = false;
    private String tabletteStr ; 
    private float coeffConvLargeur = 1f;
    private float coeffConvHauteur = 1f;
    private float coeffConvZ = 1f;// 
   
    
    String units = Const.PIX;
    private String infosManip ;
    
    private String nameTablet = "???";
    private String tabletteConnected = " ??";
    private String operSystem = "inconnu";
    
    private String physicalId = "..........";
    private String logicielVersion = "???";
    private int indexModele = 0;
    private String idCurseur = " ? "; // identification du stylet
    
    private final DefaultComboBoxModel CBModel;
    private final DefaultListModel listModel;
    private final SpinnerNumberModel InitSpinnerModel;

    private static Preferences prefs;
    private static String currentDataDirPath = "";
    private static final String DATA_DIR_PATH_KEY = "DirPathDataKEY";

    private static File currentDataDir;
    private static String racineName = "fichier";
    private int initNum = 0;
    private  boolean interpolateNeeded ;
    private int returnStatus = RET_CANCEL;
    private String[] itemsLatSujet; 
    private String[] itemsSexeSujet;
    
    DefaultComboBoxModel sexModel, lateralitModel;
    
    NumberFormat nf, nf3, nf5;

    /**
     * FicheManip constructeur
     * @param parent
     * @param modal
     */
    public FicheManip(SaisieMainFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        FicheManip.prefs = Preferences.userRoot();
        currentDataDirPath = prefs.get(DATA_DIR_PATH_KEY, System.getProperty("user.dir"));
        currentDataDir = new File(getCurrentDataDirPath());
      
        logicielVersion = parent.getTitle();
        idSeg = new SegIdentity();
        previousId = new SegIdentity();
        
       itemsSexeSujet = idSeg.getItemsSexeSujet() ;
       itemsLatSujet = idSeg.getItemsLateralisatSujet();
       
       sexModel = new DefaultComboBoxModel (itemsSexeSujet);
        lateralitModel = new DefaultComboBoxModel (itemsLatSujet);
        listModel = new DefaultListModel();
        listModel.copyInto(tabletNames);
        CBModel = new DefaultComboBoxModel();
        for (String tabletName : Const.tabletNames) {
            CBModel.addElement(tabletName);
            listModel.addElement(tabletName);
        }
        InitSpinnerModel = new SpinnerNumberModel(
                0, 0, 1024, 1);

        initComponents();
        
        
        /* Verif tablette connectee */
        wacomVerif();
        

//        this.jComboBoxModele.setMaximumRowCount(Const.tabletNames.length);
        // taille ecran courant   
        dimEcran = new Dimension(this.getScreenSize());
        dimTablette = new Dimension(this.getScreenSize());
//        this.jComboBoxModele.setSelectedIndex(I3_A4);
        setFicheDefault();
        getModeleSelection();
        afficheModeleDim();
        setZDimPresence(operSystem);
        setVisible(true);
    }
    
    private  void wacomVerif(){
          /* Recherche tablettes Wacom */
        GetUSB_Devices devicesUsb = new GetUSB_Devices();
        if(devicesUsb.isWacomPresent()){
            setWacomPresent(true);
            StringBuilder  sb = new StringBuilder(devicesUsb.getVendor());
            sb.append(" ").append(devicesUsb.getProduct());
            sb.append(" ").append(devicesUsb.getNumSerie());
            String txt =sb.toString();
            jTextFieldWacomFound.setText(txt);
            
            tabletteStr = sb.toString();
        }else{
            setWacomPresent(false);
            tabletteStr =  " pas de wacom "; 
        }
        
    }
    
    private void setFicheDefault() {
       
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        idSeg = new SegIdentity();
        this.setInfosManip(idSeg.getManipInfos());
        this.setIdSujet(idSeg.getIdSujet());
        this.setAgeSujet(idSeg.getAgeSujet()); 
        this.setLateralisatSujet(idSeg.getLateralisatSujet());
        this.setSexeSujet(idSeg.getSexeSujet());
        this.setSpecificitySujet(idSeg.getSpecificitySujet());
        this.setIdOperateur(idSeg.getIdExperimentateur());

        this.setPhysicalId("......");
        this.setIdCurseur(" ??? ");
        this.setDate(cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.MONTH) + 1) + " " + cal.get(Calendar.YEAR));
        this.setHeure(cal.get(Calendar.HOUR_OF_DAY) + " H " + cal.get(Calendar.MINUTE));
        this.setInitNum(0);
        operSystem =System.getProperty("os.name");
        operSystem += " " +System.getProperty("os.version");
        
        writeTextFields();
        
        this.validate();
    }
    
    private void setZDimPresence(String operSystem){
        if(operSystem.contains("Mac")){
           jLabelZdimPresent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledgreen.png"))); // NOI18N
           jLabelZdimPresent.setBackground(Color.green);
           jLabelZdimPresent.setText("Dimension Z active");
           this.jToggleButtonZ.setSelected(true);
           this.jToggleButtonZ.setEnabled(true);
           
           parent.setZDimPresente(true);
        }
        else{
            jLabelZdimPresent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
           jLabelZdimPresent.setBackground(Color.red);
            jLabelZdimPresent.setText("Dimension Z absente");
            this.jToggleButtonZ.setSelected(false);
           this.jToggleButtonZ.setEnabled(false);
            parent.setZDimPresente(false);
        }
    }

    private void writeTextFields() {
        jTextField_Manip.setText(infosManip);
        jTextFieldIdOpSystem.setText(operSystem);
        jTextField_Name.setText(getIdSujet());
        
        jTextField_NameExp.setText(getIdOperateur());
        jListModeles.setSelectedValue(getNameTablet(), true);
        jTextFieldWacomFound.setText(this.tabletteConnected);
        jTextFieldPhysicalId.setText(physicalId);
        jTextFieldIdStylet.setText(idCurseur);
        jTextField_Date.setText(date);
        jTextField_Heure.setText(heure);
        jTextField_Repertoire.setText(getCurrentDataDir().getPath());
        jTextField_Racine.setText(racineName);
        jTextField_NbSeg.setText("0");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel_IdDevices = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelIDEtData = new javax.swing.JPanel();
        jLabel_NBSeg = new javax.swing.JLabel();
        jTextField_Name = new javax.swing.JTextField();
        jTextField_Date = new javax.swing.JTextField();
        jLabel_Heure = new javax.swing.JLabel();
        jTextField_Heure = new javax.swing.JTextField();
        jTextField_Repertoire = new javax.swing.JTextField();
        jTextField_NbSeg = new javax.swing.JTextField();
        jTextField_NameExp = new javax.swing.JTextField();
        jLabelIdendity = new javax.swing.JLabel();
        jLabelDate = new javax.swing.JLabel();
        jLabelRepertoire = new javax.swing.JLabel();
        jTextField_Racine = new javax.swing.JTextField();
        jLabel_Init = new javax.swing.JLabel();
        jSpinner_InitNbFiles = new javax.swing.JSpinner();
        jButton_Dossier = new javax.swing.JButton();
        jCheckBoxInterpolation = new javax.swing.JCheckBox();
        jLabelExpe = new javax.swing.JLabel();
        jTextField_Manip = new javax.swing.JTextField();
        jToggleButtonZ = new javax.swing.JToggleButton();
        jComboBoxSexe = new javax.swing.JComboBox<>();
        jComboBoxLatéralite = new javax.swing.JComboBox<>();
        jSpinnerAge = new javax.swing.JSpinner();
        jLabelIdSujet = new javax.swing.JLabel();
        jTextFieldSpecificity = new javax.swing.JTextField();
        jPanelDevices = new javax.swing.JPanel();
        jTextFieldIdStylet = new javax.swing.JTextField();
        jLabelWacom = new javax.swing.JLabel();
        jLabelStylo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListModeles = new javax.swing.JList();
        jTextFieldWacomFound = new javax.swing.JTextField();
        jTextFieldPhysicalId = new javax.swing.JTextField();
        jTextField_Largeurtab = new javax.swing.JTextField();
        jTextField_Hauteurtab = new javax.swing.JTextField();
        jLabelWacom1 = new javax.swing.JLabel();
        jTextField_LargeurEcran = new javax.swing.JTextField();
        jTextField_HauteurEcran = new javax.swing.JTextField();
        jLabelScreenIco = new javax.swing.JLabel();
        jTextField_LargeurCoeff = new javax.swing.JTextField();
        jTextField_HauteurCoeff = new javax.swing.JTextField();
        jTextFieldIdOpSystem = new javax.swing.JTextField();
        jLabelPC = new javax.swing.JLabel();
        jTextField_ZCoeff = new javax.swing.JTextField();
        jLabelZdimPresent = new javax.swing.JLabel();
        jPanel_Buttons = new javax.swing.JPanel();
        jButton_Default = new javax.swing.JButton();
        jButton_Ok = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();

        setTitle("Fiche Manip.");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(880, 700));
        setModal(true);
        setPreferredSize(new java.awt.Dimension(880, 700));
        setSize(new java.awt.Dimension(880, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel_IdDevices.setBackground(new java.awt.Color(235, 235, 230));
        jPanel_IdDevices.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel_IdDevices.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel_IdDevices.setMaximumSize(new java.awt.Dimension(850, 958));
        jPanel_IdDevices.setMinimumSize(new java.awt.Dimension(850, 958));
        jPanel_IdDevices.setPreferredSize(new java.awt.Dimension(850, 958));
        jPanel_IdDevices.setLayout(new javax.swing.BoxLayout(jPanel_IdDevices, javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPane1.setMaximumSize(new java.awt.Dimension(32767, 600));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(650, 506));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(650, 600));

        jPanelIDEtData.setBackground(new java.awt.Color(238, 244, 242));
        jPanelIDEtData.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelIDEtData.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jPanelIDEtData.setMaximumSize(new java.awt.Dimension(650, 600));
        jPanelIDEtData.setMinimumSize(new java.awt.Dimension(850, 400));
        jPanelIDEtData.setPreferredSize(new java.awt.Dimension(850, 600));
        jPanelIDEtData.setLayout(new java.awt.GridBagLayout());

        jLabel_NBSeg.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel_NBSeg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel_NBSeg.setText("Nb Segments : ");
        jLabel_NBSeg.setEnabled(false);
        jLabel_NBSeg.setMaximumSize(new java.awt.Dimension(120, 32));
        jLabel_NBSeg.setMinimumSize(new java.awt.Dimension(100, 32));
        jLabel_NBSeg.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelIDEtData.add(jLabel_NBSeg, gridBagConstraints);

        jTextField_Name.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Name.setText("Anonymous");
        jTextField_Name.setToolTipText("Identification sujet");
        jTextField_Name.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField_Name.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Name.setMinimumSize(new java.awt.Dimension(200, 50));
        jTextField_Name.setPreferredSize(new java.awt.Dimension(250, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_Name, gridBagConstraints);

        jTextField_Date.setEditable(false);
        jTextField_Date.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Date.setText("...");
        jTextField_Date.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_Date.setMinimumSize(new java.awt.Dimension(200, 28));
        jTextField_Date.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_Date, gridBagConstraints);

        jLabel_Heure.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel_Heure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_Heure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/history.png"))); // NOI18N
        jLabel_Heure.setMaximumSize(new java.awt.Dimension(120, 32));
        jLabel_Heure.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanelIDEtData.add(jLabel_Heure, gridBagConstraints);

        jTextField_Heure.setEditable(false);
        jTextField_Heure.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Heure.setText("...");
        jTextField_Heure.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_Heure.setMinimumSize(new java.awt.Dimension(200, 28));
        jTextField_Heure.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_Heure, gridBagConstraints);

        jTextField_Repertoire.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Repertoire.setText(" ...dossier données ....");
        jTextField_Repertoire.setToolTipText("Chemin du dossier des données ");
        jTextField_Repertoire.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_Repertoire.setMinimumSize(new java.awt.Dimension(450, 28));
        jTextField_Repertoire.setPreferredSize(new java.awt.Dimension(400, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_Repertoire, gridBagConstraints);

        jTextField_NbSeg.setEditable(false);
        jTextField_NbSeg.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_NbSeg.setText("0");
        jTextField_NbSeg.setToolTipText("Nombre de segments");
        jTextField_NbSeg.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_NbSeg.setMinimumSize(new java.awt.Dimension(200, 28));
        jTextField_NbSeg.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_NbSeg, gridBagConstraints);

        jTextField_NameExp.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_NameExp.setText("Anonymous");
        jTextField_NameExp.setToolTipText("Identification expérimentateur");
        jTextField_NameExp.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_NameExp.setMinimumSize(new java.awt.Dimension(200, 28));
        jTextField_NameExp.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_NameExp, gridBagConstraints);

        jLabelIdendity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/identity.png"))); // NOI18N
        jLabelIdendity.setText("Opérateur id");
        jLabelIdendity.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelIdendity.setMaximumSize(new java.awt.Dimension(150, 44));
        jLabelIdendity.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabelIdendity.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanelIDEtData.add(jLabelIdendity, gridBagConstraints);

        jLabelDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/date.png"))); // NOI18N
        jLabelDate.setText("Date/Heure");
        jLabelDate.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelDate.setMaximumSize(new java.awt.Dimension(150, 44));
        jLabelDate.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabelDate.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelIDEtData.add(jLabelDate, gridBagConstraints);

        jLabelRepertoire.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/folder_txt.png"))); // NOI18N
        jLabelRepertoire.setText("Nom fichiers");
        jLabelRepertoire.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelRepertoire.setMaximumSize(new java.awt.Dimension(150, 44));
        jLabelRepertoire.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabelRepertoire.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        jPanelIDEtData.add(jLabelRepertoire, gridBagConstraints);

        jTextField_Racine.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Racine.setText("*");
        jTextField_Racine.setToolTipText("Racine du nom pour les fichiers de données");
        jTextField_Racine.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_Racine.setMinimumSize(new java.awt.Dimension(200, 28));
        jTextField_Racine.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        jPanelIDEtData.add(jTextField_Racine, gridBagConstraints);

        jLabel_Init.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel_Init.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel_Init.setText("Numéro initial :");
        jLabel_Init.setMaximumSize(new java.awt.Dimension(160, 32));
        jLabel_Init.setMinimumSize(new java.awt.Dimension(160, 32));
        jLabel_Init.setPreferredSize(new java.awt.Dimension(160, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanelIDEtData.add(jLabel_Init, gridBagConstraints);

        jSpinner_InitNbFiles.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jSpinner_InitNbFiles.setModel(InitSpinnerModel);
        jSpinner_InitNbFiles.setToolTipText("Valeur initiale de numérotation des fichiers");
        jSpinner_InitNbFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jSpinner_InitNbFiles.setMaximumSize(new java.awt.Dimension(800, 32));
        jSpinner_InitNbFiles.setMinimumSize(new java.awt.Dimension(200, 28));
        jSpinner_InitNbFiles.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanelIDEtData.add(jSpinner_InitNbFiles, gridBagConstraints);

        jButton_Dossier.setBackground(new java.awt.Color(230, 230, 230));
        jButton_Dossier.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_Dossier.setForeground(new java.awt.Color(51, 51, 51));
        jButton_Dossier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/folder_blue_open.png"))); // NOI18N
        jButton_Dossier.setText("Dossier data");
        jButton_Dossier.setToolTipText("Cliquer pour définir le dossier données courant");
        jButton_Dossier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        jButton_Dossier.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton_Dossier.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jButton_Dossier.setMaximumSize(new java.awt.Dimension(150, 44));
        jButton_Dossier.setMinimumSize(new java.awt.Dimension(130, 40));
        jButton_Dossier.setOpaque(true);
        jButton_Dossier.setPreferredSize(new java.awt.Dimension(150, 40));
        jButton_Dossier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DossierActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        jPanelIDEtData.add(jButton_Dossier, gridBagConstraints);

        jCheckBoxInterpolation.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jCheckBoxInterpolation.setText("  Interpolation : ");
        jCheckBoxInterpolation.setToolTipText("lE données sont régularisées (interpolation) avant enregistrement");
        jCheckBoxInterpolation.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelIDEtData.add(jCheckBoxInterpolation, gridBagConstraints);

        jLabelExpe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/edu-science-icon.png"))); // NOI18N
        jLabelExpe.setText("Expé. infos");
        jLabelExpe.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelExpe.setMaximumSize(new java.awt.Dimension(150, 44));
        jLabelExpe.setMinimumSize(new java.awt.Dimension(130, 40));
        jLabelExpe.setPreferredSize(new java.awt.Dimension(150, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanelIDEtData.add(jLabelExpe, gridBagConstraints);

        jTextField_Manip.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField_Manip.setText("... label experience ....");
        jTextField_Manip.setToolTipText("Intitulé ... commentaires .. expérience");
        jTextField_Manip.setMaximumSize(new java.awt.Dimension(800, 32));
        jTextField_Manip.setMinimumSize(new java.awt.Dimension(450, 28));
        jTextField_Manip.setPreferredSize(new java.awt.Dimension(400, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelIDEtData.add(jTextField_Manip, gridBagConstraints);

        jToggleButtonZ.setBackground(new java.awt.Color(204, 255, 204));
        jToggleButtonZ.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jToggleButtonZ.setSelected(true);
        jToggleButtonZ.setText("Z");
        jToggleButtonZ.setToolTipText("Si possible : capter Z ou pas.");
        jToggleButtonZ.setMaximumSize(new java.awt.Dimension(75, 45));
        jToggleButtonZ.setMinimumSize(new java.awt.Dimension(75, 32));
        jToggleButtonZ.setOpaque(true);
        jToggleButtonZ.setPreferredSize(new java.awt.Dimension(75, 32));
        jToggleButtonZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonZActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelIDEtData.add(jToggleButtonZ, gridBagConstraints);

        jComboBoxSexe.setModel(this.sexModel);
        jComboBoxSexe.setToolTipText("Sexe du sujet");
        jComboBoxSexe.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Sexe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 10))); // NOI18N
        jComboBoxSexe.setMaximumSize(new java.awt.Dimension(800, 80));
        jComboBoxSexe.setMinimumSize(new java.awt.Dimension(200, 60));
        jComboBoxSexe.setPreferredSize(new java.awt.Dimension(250, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanelIDEtData.add(jComboBoxSexe, gridBagConstraints);

        jComboBoxLatéralite.setModel(this.lateralitModel);
        jComboBoxLatéralite.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Latéralité", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 10))); // NOI18N
        jComboBoxLatéralite.setMaximumSize(new java.awt.Dimension(800, 80));
        jComboBoxLatéralite.setMinimumSize(new java.awt.Dimension(200, 60));
        jComboBoxLatéralite.setPreferredSize(new java.awt.Dimension(250, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelIDEtData.add(jComboBoxLatéralite, gridBagConstraints);

        jSpinnerAge.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jSpinnerAge.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        jSpinnerAge.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Age (ans)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 10))); // NOI18N
        jSpinnerAge.setMaximumSize(new java.awt.Dimension(800, 80));
        jSpinnerAge.setMinimumSize(new java.awt.Dimension(200, 60));
        jSpinnerAge.setPreferredSize(new java.awt.Dimension(250, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelIDEtData.add(jSpinnerAge, gridBagConstraints);

        jLabelIdSujet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelIdSujet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/identity.png"))); // NOI18N
        jLabelIdSujet.setText("  Sujet  id");
        jLabelIdSujet.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelIdSujet.setMaximumSize(new java.awt.Dimension(800, 50));
        jLabelIdSujet.setMinimumSize(new java.awt.Dimension(200, 50));
        jLabelIdSujet.setPreferredSize(new java.awt.Dimension(250, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanelIDEtData.add(jLabelIdSujet, gridBagConstraints);

        jTextFieldSpecificity.setText("sans");
        jTextFieldSpecificity.setToolTipText("Spécificité(s) , particlarité(s) du dujet....");
        jTextFieldSpecificity.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Spécificité", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 10))); // NOI18N
        jTextFieldSpecificity.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldSpecificity.setMinimumSize(new java.awt.Dimension(200, 50));
        jTextFieldSpecificity.setPreferredSize(new java.awt.Dimension(250, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelIDEtData.add(jTextFieldSpecificity, gridBagConstraints);

        jTabbedPane1.addTab("Informations Manip.", jPanelIDEtData);

        jPanelDevices.setBackground(new java.awt.Color(230, 230, 230));
        jPanelDevices.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelDevices.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        jPanelDevices.setMaximumSize(new java.awt.Dimension(650, 450));
        jPanelDevices.setMinimumSize(new java.awt.Dimension(850, 460));
        jPanelDevices.setName(""); // NOI18N
        jPanelDevices.setPreferredSize(new java.awt.Dimension(850, 480));
        jPanelDevices.setLayout(new java.awt.GridBagLayout());

        jTextFieldIdStylet.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldIdStylet.setText(" ??? ");
        jTextFieldIdStylet.setToolTipText("Identification du stylet, à renseigner facultatif)");
        jTextFieldIdStylet.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Id Curseur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        jTextFieldIdStylet.setMaximumSize(new java.awt.Dimension(800, 40));
        jTextFieldIdStylet.setMinimumSize(new java.awt.Dimension(200, 40));
        jTextFieldIdStylet.setPreferredSize(new java.awt.Dimension(300, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelDevices.add(jTextFieldIdStylet, gridBagConstraints);

        jLabelWacom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWacom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tablet32.png"))); // NOI18N
        jLabelWacom.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelWacom.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelWacom, gridBagConstraints);

        jLabelStylo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStylo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/signature.png"))); // NOI18N
        jLabelStylo.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelStylo.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelStylo, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(258, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(258, 300));

        jListModeles.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Type tablette", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 1, 14))); // NOI18N
        jListModeles.setModel(listModel);
        jListModeles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListModeles.setToolTipText("Type de la tablette utilisée");
        jListModeles.setMaximumSize(new java.awt.Dimension(300, 320));
        jListModeles.setPreferredSize(new java.awt.Dimension(300, 320));
        jListModeles.setSelectedIndex(I3_A4);
        jListModeles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListModelesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListModeles);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanelDevices.add(jScrollPane1, gridBagConstraints);

        jTextFieldWacomFound.setEditable(false);
        jTextFieldWacomFound.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldWacomFound.setForeground(new java.awt.Color(204, 0, 51));
        jTextFieldWacomFound.setText("pas de tablette wacom connectée ");
        jTextFieldWacomFound.setToolTipText("Tablette wacom connectée");
        jTextFieldWacomFound.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Connectée", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldWacomFound.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldWacomFound.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldWacomFound.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldWacomFound.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextFieldWacomFound, gridBagConstraints);

        jTextFieldPhysicalId.setEditable(false);
        jTextFieldPhysicalId.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldPhysicalId.setText("...........");
        jTextFieldPhysicalId.setToolTipText("Numero de série de la tablette de saisie (facultatif)");
        jTextFieldPhysicalId.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Num. série", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldPhysicalId.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldPhysicalId.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldPhysicalId.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldPhysicalId.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldPhysicalId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPhysicalIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextFieldPhysicalId, gridBagConstraints);

        jTextField_Largeurtab.setEditable(false);
        jTextField_Largeurtab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Largeurtab.setText("0");
        jTextField_Largeurtab.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Largeur  Tab.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Largeurtab.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Largeurtab.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Largeurtab.setOpaque(true);
        jTextField_Largeurtab.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_Largeurtab, gridBagConstraints);

        jTextField_Hauteurtab.setEditable(false);
        jTextField_Hauteurtab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Hauteurtab.setText("0");
        jTextField_Hauteurtab.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hauteur Tab", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Hauteurtab.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Hauteurtab.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Hauteurtab.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_Hauteurtab, gridBagConstraints);

        jLabelWacom1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWacom1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tablet32.png"))); // NOI18N
        jLabelWacom1.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelWacom1.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelWacom1, gridBagConstraints);

        jTextField_LargeurEcran.setEditable(false);
        jTextField_LargeurEcran.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_LargeurEcran.setText("0");
        jTextField_LargeurEcran.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Largeur Ecran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_LargeurEcran.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_LargeurEcran.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_LargeurEcran.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_LargeurEcran, gridBagConstraints);

        jTextField_HauteurEcran.setEditable(false);
        jTextField_HauteurEcran.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_HauteurEcran.setText("0");
        jTextField_HauteurEcran.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hauteur Ecran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_HauteurEcran.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_HauteurEcran.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_HauteurEcran.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_HauteurEcran, gridBagConstraints);

        jLabelScreenIco.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelScreenIco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/screen.png"))); // NOI18N
        jLabelScreenIco.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelScreenIco.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelScreenIco, gridBagConstraints);

        jTextField_LargeurCoeff.setEditable(false);
        jTextField_LargeurCoeff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_LargeurCoeff.setText("0");
        jTextField_LargeurCoeff.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff largeur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_LargeurCoeff.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_LargeurCoeff.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_LargeurCoeff.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_LargeurCoeff, gridBagConstraints);

        jTextField_HauteurCoeff.setEditable(false);
        jTextField_HauteurCoeff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_HauteurCoeff.setText("0");
        jTextField_HauteurCoeff.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Hauteur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_HauteurCoeff.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_HauteurCoeff.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_HauteurCoeff.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_HauteurCoeff, gridBagConstraints);

        jTextFieldIdOpSystem.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldIdOpSystem.setText("OS");
        jTextFieldIdOpSystem.setToolTipText("Systeme d'exploitation");
        jTextFieldIdOpSystem.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Système ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldIdOpSystem.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldIdOpSystem.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldIdOpSystem.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldIdOpSystem.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextFieldIdOpSystem, gridBagConstraints);

        jLabelPC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/server-icon.png"))); // NOI18N
        jLabelPC.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelPC.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelPC, gridBagConstraints);

        jTextField_ZCoeff.setEditable(false);
        jTextField_ZCoeff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_ZCoeff.setText("0");
        jTextField_ZCoeff.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Z", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_ZCoeff.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_ZCoeff.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_ZCoeff.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_ZCoeff, gridBagConstraints);

        jLabelZdimPresent.setBackground(new java.awt.Color(255, 51, 51));
        jLabelZdimPresent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelZdimPresent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
        jLabelZdimPresent.setText(" Z Dimension Off");
        jLabelZdimPresent.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelZdimPresent.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelZdimPresent.setOpaque(true);
        jLabelZdimPresent.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelZdimPresent, gridBagConstraints);

        jTabbedPane1.addTab("Matériel", jPanelDevices);

        jPanel_IdDevices.add(jTabbedPane1);

        getContentPane().add(jPanel_IdDevices, java.awt.BorderLayout.CENTER);

        jPanel_Buttons.setBackground(new java.awt.Color(169, 170, 164));
        jPanel_Buttons.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel_Buttons.setMaximumSize(new java.awt.Dimension(750, 32767));
        jPanel_Buttons.setMinimumSize(new java.awt.Dimension(750, 55));
        jPanel_Buttons.setPreferredSize(new java.awt.Dimension(750, 52));

        jButton_Default.setBackground(new java.awt.Color(242, 241, 239));
        jButton_Default.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton_Default.setText("Défaut");
        jButton_Default.setToolTipText("Anonymat, par défaut");
        jButton_Default.setMaximumSize(new java.awt.Dimension(77, 41));
        jButton_Default.setPreferredSize(new java.awt.Dimension(79, 41));
        jButton_Default.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_DefaultActionPerformed(evt);
            }
        });
        jPanel_Buttons.add(jButton_Default);

        jButton_Ok.setBackground(new java.awt.Color(242, 241, 239));
        jButton_Ok.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton_Ok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/button_accept.png"))); // NOI18N
        jButton_Ok.setToolTipText("Enregistrer la nouvelle identité");
        jButton_Ok.setMaximumSize(new java.awt.Dimension(41, 41));
        jButton_Ok.setMinimumSize(new java.awt.Dimension(41, 41));
        jButton_Ok.setPreferredSize(new java.awt.Dimension(41, 41));
        jButton_Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_OkActionPerformed(evt);
            }
        });
        jPanel_Buttons.add(jButton_Ok);

        jButton_Cancel.setBackground(new java.awt.Color(242, 241, 239));
        jButton_Cancel.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton_Cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/button_cancel.png"))); // NOI18N
        jButton_Cancel.setToolTipText("Annuler...");
        jButton_Cancel.setMaximumSize(new java.awt.Dimension(41, 41));
        jButton_Cancel.setMinimumSize(new java.awt.Dimension(41, 41));
        jButton_Cancel.setPreferredSize(new java.awt.Dimension(41, 41));
        jButton_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_CancelActionPerformed(evt);
            }
        });
        jPanel_Buttons.add(jButton_Cancel);

        getContentPane().add(jPanel_Buttons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_CancelActionPerformed
        idSeg = previousId;
        doClose(RET_CANCEL);
    }//GEN-LAST:event_jButton_CancelActionPerformed

    private void jButton_DefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DefaultActionPerformed
        idSeg.setDefaultSegIdentity();
    }//GEN-LAST:event_jButton_DefaultActionPerformed

    private void jButton_OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_OkActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_jButton_OkActionPerformed

    private void jButton_DossierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DossierActionPerformed
        setDataDirectory();
    }//GEN-LAST:event_jButton_DossierActionPerformed

    private void jListModelesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListModelesMouseClicked
        getModeleSelection();
        units = MM;
        afficheModeleDim();
    }//GEN-LAST:event_jListModelesMouseClicked
    /**
     * Voir si des items ont �t� modifi�s
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        doClose(RET_CANCEL);
    }//GEN-LAST:event_formWindowClosing

    private void jTextFieldPhysicalIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhysicalIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhysicalIdActionPerformed

    private void jToggleButtonZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonZActionPerformed
        if(!jToggleButtonZ.isSelected())jToggleButtonZ.setBackground(Color.red);
        else jToggleButtonZ.setBackground(Color.green);
        
        parent.setZDimPresente(jToggleButtonZ.isSelected());
    }//GEN-LAST:event_jToggleButtonZActionPerformed

    /**
     * Fournit le status du dialogue
     *
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    private int doClose(int retStatus) {
        returnStatus = retStatus;
        this.setVisible(false);
        if (bilanSession == null) {
            bilanSession = new BilanSession(this);
        } else {// Si modification notifier au bilan les items modifi�s

            if (returnStatus == RET_OK) {
                bilanSession.modifications();
            }
        }
        return retStatus;
    }

    /**
     * affiche les items de l'identity� cas d'un fichier de donnees relu par
     * saisieTrace
     */
    public void setId(SegIdentity id) {
//       System.out.println("idSeg : "+idSeg.getString());

        this.setIdSujet(id.getIdSujet());
        this.setAgeSujet(id.getAgeSujet());
        this.setLateralisatSujet(id.getLateralisatSujet());
        this.setSexeSujet(id.getSexeSujet());
        this.setIdOperateur(id.getIdExperimentateur());
        this.setNameTablet(id.getTabletteModele());
        this.setTabletteConnected(tabletteConnected);
        this.idCurseur = id.getIdCurseur();
        this.date = id.getDate();
        this.heure = id.getHeure();
        
        jTextField_Name.setText(getIdSujet());
        jTextField_NameExp.setText(getIdOperateur());
        jListModeles.setSelectedValue(getNameTablet(), true);
        jTextFieldWacomFound.setText(tabletteConnected);
        jTextFieldPhysicalId.setText(physicalId);
        jTextFieldIdStylet.setText(idCurseur);
        jTextField_Date.setText(date);
        jTextField_Heure.setText(heure);
        jTextField_Repertoire.setText(id.getRepertoire());
        jTextField_NbSeg.setText(id.getNbSegments());
    }

    /**
     * recupere les items de l'identite
     */
    public SegIdentity getIdentity() {
        SegIdentity idNew = new SegIdentity();
        
        idNew.setIdSujet(jTextField_Name.getText());
        idNew.setAgeSujet(this.getAgeSujet());
        idNew.setSexeSujet(this.getSexeSujet());
        idNew.setLateralisatSujet(this.getLateralisatSujet());
        idNew.setSpecificitySujet(this.getSpecificitySujet());
        idNew.setIdExperimentateur(jTextField_NameExp.getText());
        idNew.setDate(jTextField_Date.getText());
        idNew.setHeure(jTextField_Heure.getText());
        idNew.setRepertoire(jTextField_Repertoire.getText());
        idNew.setNbSegments(jTextField_NbSeg.getText());
        idNew.setTabletteModele((String) this.jListModeles.getSelectedValue());
        idNew.setTabletteConnected((String)this.jTextFieldPhysicalId.getText());
        idNew.setManipInfos((String) this.jTextField_Manip.getText());
        idNew.setOpSystem(this.getOperSystem());
        idNew.setIdCurseur(this.getIdCurseur());
        return idNew;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Default;
    private javax.swing.JButton jButton_Dossier;
    private javax.swing.JButton jButton_Ok;
    private javax.swing.JCheckBox jCheckBoxInterpolation;
    private javax.swing.JComboBox<String> jComboBoxLatéralite;
    private javax.swing.JComboBox<String> jComboBoxSexe;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelExpe;
    private javax.swing.JLabel jLabelIdSujet;
    private javax.swing.JLabel jLabelIdendity;
    private javax.swing.JLabel jLabelPC;
    private javax.swing.JLabel jLabelRepertoire;
    private javax.swing.JLabel jLabelScreenIco;
    private javax.swing.JLabel jLabelStylo;
    private javax.swing.JLabel jLabelWacom;
    private javax.swing.JLabel jLabelWacom1;
    private javax.swing.JLabel jLabelZdimPresent;
    private javax.swing.JLabel jLabel_Heure;
    private javax.swing.JLabel jLabel_Init;
    private javax.swing.JLabel jLabel_NBSeg;
    private javax.swing.JList jListModeles;
    private javax.swing.JPanel jPanelDevices;
    private javax.swing.JPanel jPanelIDEtData;
    private javax.swing.JPanel jPanel_Buttons;
    private javax.swing.JPanel jPanel_IdDevices;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerAge;
    private javax.swing.JSpinner jSpinner_InitNbFiles;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldIdOpSystem;
    private javax.swing.JTextField jTextFieldIdStylet;
    private javax.swing.JTextField jTextFieldPhysicalId;
    private javax.swing.JTextField jTextFieldSpecificity;
    private javax.swing.JTextField jTextFieldWacomFound;
    private javax.swing.JTextField jTextField_Date;
    private javax.swing.JTextField jTextField_HauteurCoeff;
    private javax.swing.JTextField jTextField_HauteurEcran;
    private javax.swing.JTextField jTextField_Hauteurtab;
    private javax.swing.JTextField jTextField_Heure;
    private javax.swing.JTextField jTextField_LargeurCoeff;
    private javax.swing.JTextField jTextField_LargeurEcran;
    private javax.swing.JTextField jTextField_Largeurtab;
    private javax.swing.JTextField jTextField_Manip;
    private javax.swing.JTextField jTextField_Name;
    private javax.swing.JTextField jTextField_NameExp;
    private javax.swing.JTextField jTextField_NbSeg;
    private static javax.swing.JTextField jTextField_Racine;
    private javax.swing.JTextField jTextField_Repertoire;
    private javax.swing.JTextField jTextField_ZCoeff;
    private javax.swing.JToggleButton jToggleButtonZ;
    // End of variables declaration//GEN-END:variables

    private File setDataDirectory() {
        File dataDir;

        // Select new or existing file
        // Define the destination file name.
        JFileChooser dirChooser;
        dirChooser = new javax.swing.JFileChooser();
        dirChooser.setApproveButtonText("Select");
        dirChooser.setCurrentDirectory(getCurrentDataDir());
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            txtFileFilter filter = new txtFileFilter();
//            dataFileChooser.setFileFilter(filter);
        int returnVal = dirChooser.showOpenDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        dataDir = dirChooser.getSelectedFile();

        if (dataDir != null) {
            setCurrentDataDirPath(dataDir.getPath());
            setCurrentDataDir(dataDir);
            this.jTextField_Repertoire.setText(getCurrentDataDir().toString());
        } else {

        }

        return dataDir;
    }
    
    
    
    /**
     * @return the infosManip
     */
    public String getInfosManip() {
        return infosManip;
    }

    /**
     * @param infosManip the infosManip to set
     */
    public void setInfosManip(String infosManip) {
        this.infosManip = infosManip;
        this.jTextField_Manip.setText(infosManip);
    }

    /**
     * @return the operSystem
     */
    public String getOperSystem() {
        return operSystem;
    }

    /**
     * @param operSystem the operSystem to set
     */
    public void setOperSystem(String operSystem) {
        if(this.operSystem.contentEquals("inconnu")){
           operSystem =System.getProperty("os.name");
        }
        else this.operSystem = operSystem;
        this.jTextFieldIdOpSystem.setText(operSystem);
    }

    /**
     * @return the currentDataDir
     */
    public static File getCurrentDataDir() {
        return currentDataDir;
    }

    /**{
     * @param aDataDirectory the currentDataDir to set
     */
    public static void setCurrentDataDir(File aDataDirectory) {
        currentDataDir = aDataDirectory;
    }

    /**
     * @return the racineName
     */
    public static String getRacineName() {
        racineName = jTextField_Racine.getText();
        return racineName;
    }

    /**
     * @param aRacineName the racineName to set
     */
    public static void setRacineName(String aRacineName) {
        racineName = aRacineName;
    }

    private void getModeleSelection() {

        indexModele = this.jListModeles.getSelectedIndex();
        setNameTablet(tabletNames[indexModele]);

        /* les dimensions sont arrondies */
        largeurTablette = (float) Math.round(Const.largeurs[indexModele]);
        hauteurTablette = (float) Math.round(Const.hauteurs[indexModele]);
        //System.out.println("getModel " + tabletNames[indexModele] + "  " + largeurTablette + " " + hauteurTablette);
        dimTablette = new Dimension();
        dimTablette.setSize(getLargeurTablette(), getHauteurTablette());
        units = MM;

    }

    private void afficheModeleDim() {
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        nf3 = NumberFormat.getInstance();
        nf3.setMaximumFractionDigits(3);
        
        nf5 = NumberFormat.getInstance();
        nf5.setMaximumFractionDigits(5);
        /* Dimension ecran */
        getScreenSize();
        String hScreen = nf.format(this.getDimEcran().height) + " pixels";
        this.jTextField_HauteurEcran.setText(hScreen);
        String lScreen = nf.format(getDimEcran().width) + " pixels";
        this.jTextField_LargeurEcran.setText(lScreen);

        /* Dimensions tablette */
        getModeleSelection();

        String h = nf3.format(hauteurTablette) + " " + units;
        jTextField_Hauteurtab.setText(h);
        String l = nf3.format(largeurTablette) + " " + units;
        jTextField_Largeurtab.setText(l);
//        System.out.println("getModel " + tabletNames[indexModele] + "  " + largeurTablette + " l " + l);
//        System.out.println("getModel " + tabletNames[indexModele] + "  " + hauteurTablette + " h " + h);
        /* Coefficients de conversion */

        String coeffH = nf3.format(this.getCoeffConvHauteur());
        this.jTextField_HauteurCoeff.setText(coeffH);
        String coeffL = nf3.format(this.getCoeffConvLargeur());
        jTextField_LargeurCoeff.setText(coeffL);
        
        String coeffZ =  nf5.format(this.getCoeffConvZ());
        this.jTextField_ZCoeff.setText(coeffZ);
    }

    public final Dimension getScreenSize() {
        // taille ecran courant 
        Toolkit tk = Toolkit.getDefaultToolkit();
        hauteurEcran = tk.getScreenSize().height;
        largeurEcran = tk.getScreenSize().width;
        dimEcran = new Dimension((int) largeurEcran, (int) hauteurEcran);
        return dimEcran;
    }

    /**
     * @return the currentDataDirPath
     */
    public static String getCurrentDataDirPath() {
        return currentDataDirPath;
    }

    /**
     * @param aCurrentDataDirPath the currentDataDirPath to set
     */
    public static void setCurrentDataDirPath(String newDirPath) {
        prefs.put(DATA_DIR_PATH_KEY, newDirPath);
        currentDataDirPath = newDirPath;
        currentDataDir = new File(currentDataDirPath);
    }

    public String getUnits() {
        return units;
    }

    public Dimension getTabletSize() {
        return getDimTablette();
    }

    /**
     * @return the nameTablet
     */
    public String getNameTablet() {
        indexModele = this.jListModeles.getSelectedIndex();
        setNameTablet(tabletNames[indexModele]);
        return nameTablet;
    }

    public float getNbLignesMm() {
        if (getNameTablet().contains("Intuos 2")) {
            return 100f;
        } else {
            return 200f; // intuos 3  et 4 serie
        }
    }

    /**
     * @return the idCurseur
     */
    public String getIdCurseur() {
        idCurseur = this.jTextFieldIdStylet.getText();
        return idCurseur;
    }

    /**
     * @param idCurseur the idCurseur to set
     */
    public void setIdCurseur(String idCurseur) {
        this.idCurseur = idCurseur;
        jTextFieldIdStylet.setText(idCurseur);
    }

    /**
     * @return the initNum
     */
    public int getInitNum() {
        initNum = (Integer) this.jSpinner_InitNbFiles.getValue();
        return initNum;
    }

    /**
     * @param initNum the initNum to set
     */
    public void setInitNum(int initNum) {
        this.initNum = initNum;
        this.jSpinner_InitNbFiles.setValue(initNum);
    }

    /**
     * @return the idSujet
     */
    public String getIdSujet() {
        idSujet = jTextField_Name.getText();
        return idSujet;
    }

    /**
     * @param idSujet the idSujet to set
     */
    public void setIdSujet(String idSujet) {
        this.idSujet = idSujet;
        this.jTextField_Name.setText(idSujet);
    }

    /**
     * @return the idOperateur
     */
    public String getIdOperateur() {
        idOperateur = this.jTextField_NameExp.getText();
        return idOperateur;
    }

    /**
     * @param idOperateur the idOperateur to set
     */
    public void setIdOperateur(String idOperateur) {
        this.idOperateur = idOperateur;
        this.jTextField_NameExp.setText(idOperateur);
    }

    /**
     * @return the date
     */
    public String getDate() {
        date = this.jTextField_Date.getText();
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
        this.jTextField_Date.setText(date);
    }

    /**
     * @return the heure
     */
    public String getHeure() {
        heure = this.jTextField_Heure.getText();
        return heure;
    }

    /**
     * @param heure the heure to set
     */
    public void setHeure(String heure) {
        this.heure = heure;
        this.jTextField_Heure.setText(heure);
    }

    /**
     * @return the physicalId
     */
    public String getPhysicalId() {
        return physicalId;
    }

    /**
     * @param physicalId the physicalId to set
     */
    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
        this.jTextFieldPhysicalId.setText(physicalId);
    }

    /**
     * @param nameTablet the nameTablet to set
     */
    public void setNameTablet(String nameTablet) {
        this.nameTablet = nameTablet;
        jListModeles.setSelectedValue(nameTablet, true);
    }

    /**
     * @return the largeurTablette
     */
    public float getLargeurTablette() {
        return largeurTablette;
    }

    /**
     * @return the hauteurTablette
     */
    public float getHauteurTablette() {
        return hauteurTablette;
    }

    /**
     * @return the dimEcran
     */
    public Dimension getDimEcran() {
        return this.getScreenSize();
    }

    /**
     * @return the dimTablette
     */
    public Dimension getDimTablette() {
        return dimTablette;

    }

    /**
     * @return the coeffConvLargeur
     */
    public float getCoeffConvLargeur() {
        coeffConvLargeur = largeurEcran / largeurTablette;
        return coeffConvLargeur;
    }

    /**
     * @return the coeffConvHauteur
     */
    public float getCoeffConvHauteur() {
        coeffConvHauteur = hauteurEcran / hauteurTablette;
        return coeffConvHauteur;
    }

    /**
     * @return the logicielVersion
     */
    public String getLogicielVersion() {
        return logicielVersion;
    }

    /**
     * @return the interpoltNeeded
     */
    public boolean isInterpolatNeeded() {
        interpolateNeeded = this.jCheckBoxInterpolation.isSelected();
        return interpolateNeeded;
    }

    /**
     * @param interpolateNeeded the interpoltNeeded to set
     */
    public void setInterpolatNeeded(boolean interpolateNeeded) {
        this.interpolateNeeded = interpolateNeeded;
    }

    /**
     * @return the wacomPresent
     */
    public boolean isWacomPresent() {
        return wacomPresent;
    }

    /**
     * @param wacomPresent the wacomPresent to set
     */
    public void setWacomPresent(boolean wacomPresent) {
        this.wacomPresent = wacomPresent;
    }

    /**
     * @return the tabletteStr
     */
    public String getTabletteStr() {
        return tabletteStr;
    }

    /**
     * @param tabletteStr the tabletteStr to set
     */
    public void setTabletteStr(String tabletteStr) {
        this.tabletteStr = tabletteStr;
    }

    /**
     * @return the tabletteConnected
     */
    public String getTabletteConnected() {
        return tabletteConnected;
    }

    /**
     * @param tabletteConnected the tabletteConnected to set
     */
    public void setTabletteConnected(String tabletteConnected) {
        this.tabletteConnected = tabletteConnected;
    }

    /**
     * 
     * Provisoire : 20 mm pour 1024
     * @return the coeffConvZ
     */
    public float getCoeffConvZ() {
        coeffConvZ  = 20f/1024f;
        return coeffConvZ;
    }

    /**
     * @param coeffConvZ the coeffConvZ to set
     */
    public void setCoeffConvZ(float coeffConvZ) {
        this.coeffConvZ = coeffConvZ;
    }

    /**
     * @return the ageSujet
     */
    public String getAgeSujet() {
        ageSujet = Integer.toString((Integer)this.jSpinnerAge.getValue());
        return ageSujet;
    }

    /**
     * @param ageSujet the ageSujet to set
     */
    public void setAgeSujet(String ageSujet) {
        System.out.println("ageSujet source "+ageSujet);
        this.ageSujet = ageSujet;
        if(ageSujet.contains("inconnu"))this.ageSujet="0";
        System.out.println("age dest "+this.ageSujet+" Integer "+Integer.decode(this.ageSujet));
        
        this.jSpinnerAge.setValue(Integer.decode(this.ageSujet));
    }

    /**
     * @return the lateralisatSujet
     */
    public String getLateralisatSujet() {
        lateralisatSujet = (String)this.jComboBoxLatéralite.getSelectedItem();
        return lateralisatSujet;
    }

    /**
     * @param lateralisatSujet the lateralisatSujet to set
     */
    public void setLateralisatSujet(String lateralisatSujet) {
        this.lateralisatSujet = lateralisatSujet;
        this.jComboBoxLatéralite.setSelectedItem(lateralisatSujet);
    }

    /**
     * @return the sexeSujet
     */
    public String getSexeSujet() {
        sexeSujet = (String) this.jComboBoxSexe.getSelectedItem();
        return sexeSujet;
    }

    /**
     * @param sexeSujet the sexeSujet to set
     */
    public void setSexeSujet(String sexeSujet) {
        this.sexeSujet = sexeSujet;
        this.jComboBoxSexe.setSelectedItem(sexeSujet);
    }

    /**
     * @return the specificitySujet
     */
    public String getSpecificitySujet() {
       specificitySujet = this.jTextFieldSpecificity.getText();
        return specificitySujet;
    }

    /**
     * @param specificitySujet the specificitySujet to set
     */
    public void setSpecificitySujet(String specificitySujet) {
        this.specificitySujet = specificitySujet;
        this.jTextFieldSpecificity.setText(specificitySujet);
    }
}
