package jpen.Acquisition;

import commons.writingZ.Const;
import commons.writingZ.SegIdentity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
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
 * 15 mai 2019 ajout de la pression et du coeff de pression
 * @author JC Gilhodes
 */
public class FicheManip extends javax.swing.JDialog implements Const {
    
    

    SaisieMainFrame parent;
    
    SegIdentity idSeg, previousId;
    
    TypeTablette typeTablette;
    
    BilanSession bilanSession;
    private String idSujet;
    private String ageSujet;
    private String lateralisatSujet ;
    private String  sexeSujet ;
    private String specificitySujet ;
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
    private float coeffConvP =1f;
    private float pression;
    private String allNameTablet;
    private String newNameTablet;
   
    
    String units = Const.PIX;
    private String infosManip ;
    
    private String nameTablet = "???";
    private String tabletteConnected = " ??";
    private String operSystem = "inconnu";
    
    private String physicalId = "..........";
    private String logicielVersion = "???";
    private int indexModele = 0;
    private String idCurseur = " ? "; // identification du stylet
   
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
    PenCanvas2 penCanvas;
    private double PMIN;
    private ArrayList ListPression;
    
    DefaultComboBoxModel sexModel, lateralitModel;
    
    NumberFormat nf, nf3, nf5;
    
    
   // public static void main (String[] args) throws Exception{
    //    Preferences prefs = Preferences.userNodeForPackage(FicheManip.class);
     //   String newNameTablet = prefs.get(NAME_KEY,NAME_DEFAULT_VALUE);
    //    JTextField jTextFieldNomNewTablet = new JTextField(newNameTablet)
    //    Object[] message = {
    //        "NOM", jTextFieldNomNewTablet
     //   };
     //   String newNom = jTextFieldNomNewTablet.getText().trim();
      //  if(newNom.equals(newNameTablet)==false){
            //            prefs.put(NAME_KEY);
      //  }
     //   prefs.flush();
   // }
   
   

    /**
     * FicheManip constructeur
     * @param parent
     * @param modal
     */
    public FicheManip(SaisieMainFrame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        FicheManip.prefs = Preferences.userRoot();
        prefs = Preferences.userNodeForPackage(this.getClass());
        
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
        //CBModel = new DefaultComboBoxModel();
        for (String tabletName : Const.tabletNames) {
          //  CBModel.addElement(tabletName);
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
          /* Recherche tablette si la tablette est présente */
        GetUSB_Devices devicesUsb = new GetUSB_Devices(); //recupere les periphérique USB
        if(devicesUsb.isWacomPresent()){ //si la tablette est presente
            setWacomPresent(true);
            StringBuilder  sb = new StringBuilder(devicesUsb.getVendor());
            sb.append(" ").append(devicesUsb.getProduct());
            sb.append(" ").append(devicesUsb.getNumSerie());
            String txt =sb.toString();
            jTextFieldWacomFound.setText(txt + " détectée!"); // nom de la tablette detectée
            jTextFieldWacomFound1.setText(txt + " détectée!");
            tabletteStr = sb.toString();
        }else{ // si la tablette est absente
            setWacomPresent(false);
            tabletteStr =  " pas de wacom ";
            jTextFieldWacomFound.setText(" Aucune tablette détectée!");
            jTextFieldWacomFound1.setText(" Aucune tablette détectée!");
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
           
           jLabelZdimPresent1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledgreen.png"))); // NOI18N
           jLabelZdimPresent1.setBackground(Color.green);
           jLabelZdimPresent1.setText("Dimension Z active");
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
            
            jLabelZdimPresent1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
            jLabelZdimPresent1.setBackground(Color.red);
            jLabelZdimPresent1.setText("Dimension Z absente");
            this.jToggleButtonZ.setSelected(false);
            this.jToggleButtonZ.setEnabled(false);
            
            parent.setZDimPresente(false);
        }
    }

    private void writeTextFields() {
        jTextField_Manip.setText(infosManip);
        jTextFieldIdOpSystem.setText(operSystem);
        jTextFieldIdOpSystem1.setText(operSystem);
        jTextField_Name.setText(getIdSujet());
        
        jTextField_NameExp.setText(getIdOperateur());
        jListModeles.setSelectedValue(getNameTablet(), true);
        jTextFieldWacomFound.setText(this.tabletteConnected);
        jTextFieldPhysicalId.setText(physicalId);
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
        jTextField_CoeffPression = new javax.swing.JTextField();
        jLabelZdimPresent = new javax.swing.JLabel();
        jComboBoxCurseur = new javax.swing.JComboBox<>();
        jTextField_ZCoeff = new javax.swing.JTextField();
        jTextField_Pression = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jTextFieldStatut = new javax.swing.JTextField();
        jComboBoxCurseur1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ajouterNewTablet = new javax.swing.JButton();
        jLabelPC1 = new javax.swing.JLabel();
        jLabelWacom2 = new javax.swing.JLabel();
        jTextFieldWacomFound1 = new javax.swing.JTextField();
        jTextFieldPhysicalId1 = new javax.swing.JTextField();
        jTextFieldHauteurNewTablet = new javax.swing.JTextField();
        jTextFieldLargeurNewTablet = new javax.swing.JTextField();
        jLabelZdimPresent1 = new javax.swing.JLabel();
        jLabelScreenIco1 = new javax.swing.JLabel();
        jTextField_LargeurEcran1 = new javax.swing.JTextField();
        jTextField_HauteurEcran1 = new javax.swing.JTextField();
        jTextField_LargeurCoeff1 = new javax.swing.JTextField();
        jTextField_HauteurCoeff1 = new javax.swing.JTextField();
        jTextField_ZCoeff1 = new javax.swing.JTextField();
        jTextField_CoeffPression1 = new javax.swing.JTextField();
        jTextFieldPressionNewTablet = new javax.swing.JTextField();
        jTextFieldIdOpSystem1 = new javax.swing.JTextField();
        jTextField_Pression1 = new javax.swing.JTextField();
        jTextField_Hauteurtab1 = new javax.swing.JTextField();
        jTextField_Largeurtab1 = new javax.swing.JTextField();
        jTextFieldNomNewTablet = new javax.swing.JTextField();
        jLabelStylo1 = new javax.swing.JLabel();
        jPanel_Buttons = new javax.swing.JPanel();
        jButton_Default = new javax.swing.JButton();
        jButton_Ok = new javax.swing.JButton();
        jButton_Cancel = new javax.swing.JButton();

        setTitle("Fiche Manip.");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocation(new java.awt.Point(50, 50));
        setMinimumSize(new java.awt.Dimension(700, 500));
        setModal(true);
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
        jPanel_IdDevices.setPreferredSize(new java.awt.Dimension(850, 600));
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
        jTextField_Racine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_RacineActionPerformed(evt);
            }
        });
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
        jTextField_Manip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_ManipActionPerformed(evt);
            }
        });
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
        jComboBoxSexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSexeActionPerformed(evt);
            }
        });
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
        jListModeles.setMaximumSize(new java.awt.Dimension(500, 500));
        jListModeles.setPreferredSize(new java.awt.Dimension(300, 400));
        jListModeles.setSelectedIndex(18);
        jListModeles.setVisibleRowCount(15);
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
        jTextFieldWacomFound.setForeground(java.awt.Color.red);
        jTextFieldWacomFound.setText("pas de tablette wacom connectée ");
        jTextFieldWacomFound.setToolTipText("Tablette wacom connectée");
        jTextFieldWacomFound.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Connectée", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldWacomFound.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldWacomFound.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldWacomFound.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldWacomFound.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldWacomFound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWacomFoundActionPerformed(evt);
            }
        });
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
        jTextField_Largeurtab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LargeurtabActionPerformed(evt);
            }
        });
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
        jTextField_Hauteurtab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_HauteurtabActionPerformed(evt);
            }
        });
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
        jTextField_LargeurEcran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LargeurEcranActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
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
        jTextField_HauteurEcran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_HauteurEcranActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelScreenIco, gridBagConstraints);

        jTextField_LargeurCoeff.setEditable(false);
        jTextField_LargeurCoeff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_LargeurCoeff.setText("0");
        jTextField_LargeurCoeff.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff largeur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_LargeurCoeff.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_LargeurCoeff.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_LargeurCoeff.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_LargeurCoeff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LargeurCoeffActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
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
        gridBagConstraints.gridy = 12;
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
        jTextFieldIdOpSystem.setPreferredSize(new java.awt.Dimension(320, 40));
        jTextFieldIdOpSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdOpSystemActionPerformed(evt);
            }
        });
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

        jTextField_CoeffPression.setEditable(false);
        jTextField_CoeffPression.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_CoeffPression.setText("0");
        jTextField_CoeffPression.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Pression", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_CoeffPression.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_CoeffPression.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_CoeffPression.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_CoeffPression, gridBagConstraints);

        jLabelZdimPresent.setBackground(java.awt.Color.red);
        jLabelZdimPresent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelZdimPresent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
        jLabelZdimPresent.setText(" Z Dimension Off");
        jLabelZdimPresent.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelZdimPresent.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelZdimPresent.setOpaque(true);
        jLabelZdimPresent.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jLabelZdimPresent, gridBagConstraints);

        jComboBoxCurseur.setBackground(new java.awt.Color(255, 255, 255));
        jComboBoxCurseur.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jComboBoxCurseur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Grip Pen", "Touch", "Souris" }));
        jComboBoxCurseur.setToolTipText("Type de curseur");
        jComboBoxCurseur.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Curseur"));
        jComboBoxCurseur.setMinimumSize(new java.awt.Dimension(300, 70));
        jComboBoxCurseur.setPreferredSize(new java.awt.Dimension(300, 70));
        jComboBoxCurseur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCurseurActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanelDevices.add(jComboBoxCurseur, gridBagConstraints);

        jTextField_ZCoeff.setEditable(false);
        jTextField_ZCoeff.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_ZCoeff.setText("0");
        jTextField_ZCoeff.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Z", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_ZCoeff.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_ZCoeff.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_ZCoeff.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_ZCoeff, gridBagConstraints);

        jTextField_Pression.setEditable(false);
        jTextField_Pression.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Pression.setText("0");
        jTextField_Pression.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pression", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Pression.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Pression.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Pression.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_Pression.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_PressionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanelDevices.add(jTextField_Pression, gridBagConstraints);

        jTabbedPane1.addTab("Matériel", jPanelDevices);

        jPanel1.setBackground(new java.awt.Color(238, 244, 242));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jTextFieldStatut.setEditable(false);
        jTextFieldStatut.setBackground(new java.awt.Color(255, 0, 0));
        jTextFieldStatut.setFont(new java.awt.Font("Lucida Grande", 0, 11)); // NOI18N
        jTextFieldStatut.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldStatut.setText("Aucune tablette ajoutée");
        jTextFieldStatut.setToolTipText("Informations sur l'ajout d'une tablette");
        jTextFieldStatut.setMinimumSize(new java.awt.Dimension(300, 40));
        jTextFieldStatut.setPreferredSize(new java.awt.Dimension(700, 30));
        jTextFieldStatut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldStatutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jTextFieldStatut, gridBagConstraints);

        jComboBoxCurseur1.setBackground(new java.awt.Color(255, 255, 255));
        jComboBoxCurseur1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jComboBoxCurseur1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Grip Pen", "Touch", "Souris" }));
        jComboBoxCurseur1.setToolTipText("Type de curseur");
        jComboBoxCurseur1.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Curseur"));
        jComboBoxCurseur1.setMinimumSize(new java.awt.Dimension(300, 70));
        jComboBoxCurseur1.setPreferredSize(new java.awt.Dimension(300, 70));
        jComboBoxCurseur1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCurseur1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jComboBoxCurseur1, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tablet32.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/1288541387_full_screen.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/preferences-system.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        jPanel1.add(jLabel2, gridBagConstraints);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/poedit.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        jPanel1.add(jLabel4, gridBagConstraints);

        ajouterNewTablet.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        ajouterNewTablet.setForeground(new java.awt.Color(51, 153, 0));
        ajouterNewTablet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/button_accept.png"))); // NOI18N
        ajouterNewTablet.setText("Ajouter");
        ajouterNewTablet.setPreferredSize(new java.awt.Dimension(122, 39));
        ajouterNewTablet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ajouterNewTabletMouseClicked(evt);
            }
        });
        ajouterNewTablet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ajouterNewTabletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        jPanel1.add(ajouterNewTablet, gridBagConstraints);

        jLabelPC1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPC1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/server-icon.png"))); // NOI18N
        jLabelPC1.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelPC1.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelPC1, gridBagConstraints);

        jLabelWacom2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelWacom2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/tablet32.png"))); // NOI18N
        jLabelWacom2.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelWacom2.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelWacom2, gridBagConstraints);

        jTextFieldWacomFound1.setEditable(false);
        jTextFieldWacomFound1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldWacomFound1.setForeground(java.awt.Color.red);
        jTextFieldWacomFound1.setText("pas de tablette wacom connectée ");
        jTextFieldWacomFound1.setToolTipText("Tablette wacom connectée");
        jTextFieldWacomFound1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Connectée", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldWacomFound1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldWacomFound1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldWacomFound1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldWacomFound1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldWacomFound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWacomFound1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldWacomFound1, gridBagConstraints);

        jTextFieldPhysicalId1.setEditable(false);
        jTextFieldPhysicalId1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldPhysicalId1.setText("...........");
        jTextFieldPhysicalId1.setToolTipText("Numero de série de la tablette de saisie (facultatif)");
        jTextFieldPhysicalId1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Num. série", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldPhysicalId1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldPhysicalId1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldPhysicalId1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldPhysicalId1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldPhysicalId1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPhysicalId1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldPhysicalId1, gridBagConstraints);

        jTextFieldHauteurNewTablet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextFieldHauteurNewTablet.setText("0");
        jTextFieldHauteurNewTablet.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hauteur Tab", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextFieldHauteurNewTablet.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldHauteurNewTablet.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldHauteurNewTablet.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldHauteurNewTablet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldHauteurNewTabletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldHauteurNewTablet, gridBagConstraints);

        jTextFieldLargeurNewTablet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextFieldLargeurNewTablet.setText("0");
        jTextFieldLargeurNewTablet.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Largeur  Tab.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextFieldLargeurNewTablet.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldLargeurNewTablet.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldLargeurNewTablet.setOpaque(true);
        jTextFieldLargeurNewTablet.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldLargeurNewTablet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldLargeurNewTabletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldLargeurNewTablet, gridBagConstraints);

        jLabelZdimPresent1.setBackground(java.awt.Color.red);
        jLabelZdimPresent1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelZdimPresent1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ledred.png"))); // NOI18N
        jLabelZdimPresent1.setText(" Z Dimension Off");
        jLabelZdimPresent1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabelZdimPresent1.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelZdimPresent1.setOpaque(true);
        jLabelZdimPresent1.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelZdimPresent1, gridBagConstraints);

        jLabelScreenIco1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelScreenIco1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/screen.png"))); // NOI18N
        jLabelScreenIco1.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelScreenIco1.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelScreenIco1, gridBagConstraints);

        jTextField_LargeurEcran1.setEditable(false);
        jTextField_LargeurEcran1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_LargeurEcran1.setText("0");
        jTextField_LargeurEcran1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Largeur Ecran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_LargeurEcran1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_LargeurEcran1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_LargeurEcran1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_LargeurEcran1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LargeurEcran1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_LargeurEcran1, gridBagConstraints);

        jTextField_HauteurEcran1.setEditable(false);
        jTextField_HauteurEcran1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_HauteurEcran1.setText("0");
        jTextField_HauteurEcran1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hauteur Ecran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_HauteurEcran1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_HauteurEcran1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_HauteurEcran1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_HauteurEcran1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_HauteurEcran1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_HauteurEcran1, gridBagConstraints);

        jTextField_LargeurCoeff1.setEditable(false);
        jTextField_LargeurCoeff1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_LargeurCoeff1.setText("0");
        jTextField_LargeurCoeff1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff largeur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_LargeurCoeff1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_LargeurCoeff1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_LargeurCoeff1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_LargeurCoeff1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_LargeurCoeff1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_LargeurCoeff1, gridBagConstraints);

        jTextField_HauteurCoeff1.setEditable(false);
        jTextField_HauteurCoeff1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_HauteurCoeff1.setText("0");
        jTextField_HauteurCoeff1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Hauteur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_HauteurCoeff1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_HauteurCoeff1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_HauteurCoeff1.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_HauteurCoeff1, gridBagConstraints);

        jTextField_ZCoeff1.setEditable(false);
        jTextField_ZCoeff1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_ZCoeff1.setText("0");
        jTextField_ZCoeff1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Z", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_ZCoeff1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_ZCoeff1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_ZCoeff1.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_ZCoeff1, gridBagConstraints);

        jTextField_CoeffPression1.setEditable(false);
        jTextField_CoeffPression1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_CoeffPression1.setText("0");
        jTextField_CoeffPression1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Coeff Pression", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_CoeffPression1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_CoeffPression1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_CoeffPression1.setPreferredSize(new java.awt.Dimension(380, 50));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_CoeffPression1, gridBagConstraints);

        jTextFieldPressionNewTablet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextFieldPressionNewTablet.setText("0");
        jTextFieldPressionNewTablet.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pression", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextFieldPressionNewTablet.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldPressionNewTablet.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldPressionNewTablet.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldPressionNewTablet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPressionNewTabletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldPressionNewTablet, gridBagConstraints);

        jTextFieldIdOpSystem1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldIdOpSystem1.setText("OS");
        jTextFieldIdOpSystem1.setToolTipText("Systeme d'exploitation");
        jTextFieldIdOpSystem1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Système ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jTextFieldIdOpSystem1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTextFieldIdOpSystem1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldIdOpSystem1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldIdOpSystem1.setPreferredSize(new java.awt.Dimension(320, 40));
        jTextFieldIdOpSystem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIdOpSystem1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldIdOpSystem1, gridBagConstraints);

        jTextField_Pression1.setEditable(false);
        jTextField_Pression1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Pression1.setText("0");
        jTextField_Pression1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pression", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Pression1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Pression1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Pression1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_Pression1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Pression1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_Pression1, gridBagConstraints);

        jTextField_Hauteurtab1.setEditable(false);
        jTextField_Hauteurtab1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Hauteurtab1.setText("0");
        jTextField_Hauteurtab1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hauteur Tab", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Hauteurtab1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Hauteurtab1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Hauteurtab1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_Hauteurtab1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Hauteurtab1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_Hauteurtab1, gridBagConstraints);

        jTextField_Largeurtab1.setEditable(false);
        jTextField_Largeurtab1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextField_Largeurtab1.setText("0");
        jTextField_Largeurtab1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Largeur  Tab.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextField_Largeurtab1.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextField_Largeurtab1.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextField_Largeurtab1.setOpaque(true);
        jTextField_Largeurtab1.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextField_Largeurtab1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_Largeurtab1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextField_Largeurtab1, gridBagConstraints);

        jTextFieldNomNewTablet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTextFieldNomNewTablet.setText("...");
        jTextFieldNomNewTablet.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Nom Tab.", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jTextFieldNomNewTablet.setMaximumSize(new java.awt.Dimension(800, 50));
        jTextFieldNomNewTablet.setMinimumSize(new java.awt.Dimension(320, 40));
        jTextFieldNomNewTablet.setOpaque(true);
        jTextFieldNomNewTablet.setPreferredSize(new java.awt.Dimension(380, 50));
        jTextFieldNomNewTablet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNomNewTabletActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        jPanel1.add(jTextFieldNomNewTablet, gridBagConstraints);

        jLabelStylo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStylo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/signature.png"))); // NOI18N
        jLabelStylo1.setMaximumSize(new java.awt.Dimension(42, 42));
        jLabelStylo1.setPreferredSize(new java.awt.Dimension(42, 42));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jLabelStylo1, gridBagConstraints);

        jTabbedPane1.addTab("Ajouter une tablette", jPanel1);

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
        setLocationRelativeTo(null);
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
    /**
     * Voir si des items ont �t� modifi�s
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        doClose(RET_CANCEL);
    }//GEN-LAST:event_formWindowClosing

    private void jTextField_HauteurEcranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_HauteurEcranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_HauteurEcranActionPerformed

    private void jTextField_LargeurEcranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LargeurEcranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LargeurEcranActionPerformed

    private void jTextFieldPhysicalIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhysicalIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhysicalIdActionPerformed

    private void jTextFieldWacomFoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldWacomFoundActionPerformed
        
    }//GEN-LAST:event_jTextFieldWacomFoundActionPerformed

    private void jListModelesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListModelesMouseClicked

        getModeleSelection();
        units = MM;
        afficheModeleDim();
        wacomVerif();
        
       
    }//GEN-LAST:event_jListModelesMouseClicked

    private void jToggleButtonZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonZActionPerformed
        if(!jToggleButtonZ.isSelected())jToggleButtonZ.setBackground(Color.red);
        else jToggleButtonZ.setBackground(Color.green);

        parent.setZDimPresente(jToggleButtonZ.isSelected());
    }//GEN-LAST:event_jToggleButtonZActionPerformed

    private void jTextField_ManipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_ManipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_ManipActionPerformed

    private void jButton_DossierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_DossierActionPerformed
        setDataDirectory();
    }//GEN-LAST:event_jButton_DossierActionPerformed

    private void jTextField_RacineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_RacineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_RacineActionPerformed

    private void ajouterNewTabletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ajouterNewTabletActionPerformed
           
       
    }//GEN-LAST:event_ajouterNewTabletActionPerformed

    private void jTextField_LargeurtabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LargeurtabActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LargeurtabActionPerformed

    private void jComboBoxSexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSexeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxSexeActionPerformed

    private void ajouterNewTabletMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ajouterNewTabletMouseClicked
        
        newNameTablet = jTextFieldNomNewTablet.getText(); 
        tabletNames[18] = newNameTablet;
        getModeleSelection();
        afficheModeleDim();
        
       
    }//GEN-LAST:event_ajouterNewTabletMouseClicked

    private void jTextField_HauteurtabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_HauteurtabActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_HauteurtabActionPerformed

    private void jComboBoxCurseurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCurseurActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCurseurActionPerformed

    private void jTextField_LargeurCoeffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LargeurCoeffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LargeurCoeffActionPerformed

    private void jTextFieldStatutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldStatutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldStatutActionPerformed

    private void jTextField_LargeurEcran1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LargeurEcran1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LargeurEcran1ActionPerformed

    private void jTextField_HauteurEcran1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_HauteurEcran1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_HauteurEcran1ActionPerformed

    private void jTextField_LargeurCoeff1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_LargeurCoeff1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_LargeurCoeff1ActionPerformed

    private void jTextFieldHauteurNewTabletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHauteurNewTabletActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldHauteurNewTabletActionPerformed

    private void jTextFieldPhysicalId1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhysicalId1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhysicalId1ActionPerformed

    private void jComboBoxCurseur1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCurseur1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxCurseur1ActionPerformed

    private void jTextFieldLargeurNewTabletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLargeurNewTabletActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLargeurNewTabletActionPerformed

    private void jTextFieldIdOpSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdOpSystemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIdOpSystemActionPerformed

    private void jTextField_PressionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_PressionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_PressionActionPerformed

    private void jTextFieldPressionNewTabletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPressionNewTabletActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPressionNewTabletActionPerformed

    private void jTextFieldIdOpSystem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIdOpSystem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIdOpSystem1ActionPerformed

    private void jTextField_Pression1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Pression1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Pression1ActionPerformed

    private void jTextField_Hauteurtab1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Hauteurtab1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Hauteurtab1ActionPerformed

    private void jTextField_Largeurtab1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_Largeurtab1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_Largeurtab1ActionPerformed

    private void jTextFieldNomNewTabletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNomNewTabletActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldNomNewTabletActionPerformed

    private void jTextFieldWacomFound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldWacomFound1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldWacomFound1ActionPerformed

    
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
        jTextFieldWacomFound1.setText(tabletteConnected);
        jTextFieldPhysicalId.setText(physicalId);
        //jTextFieldIdStylet.setText(idCurseur);
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
    private javax.swing.JButton ajouterNewTablet;
    private javax.swing.JButton jButton_Cancel;
    private javax.swing.JButton jButton_Default;
    private javax.swing.JButton jButton_Dossier;
    private javax.swing.JButton jButton_Ok;
    private javax.swing.JCheckBox jCheckBoxInterpolation;
    private javax.swing.JComboBox<String> jComboBoxCurseur;
    private javax.swing.JComboBox<String> jComboBoxCurseur1;
    private javax.swing.JComboBox<String> jComboBoxLatéralite;
    private javax.swing.JComboBox<String> jComboBoxSexe;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelDate;
    private javax.swing.JLabel jLabelExpe;
    private javax.swing.JLabel jLabelIdSujet;
    private javax.swing.JLabel jLabelIdendity;
    private javax.swing.JLabel jLabelPC;
    private javax.swing.JLabel jLabelPC1;
    private javax.swing.JLabel jLabelRepertoire;
    private javax.swing.JLabel jLabelScreenIco;
    private javax.swing.JLabel jLabelScreenIco1;
    private javax.swing.JLabel jLabelStylo;
    private javax.swing.JLabel jLabelStylo1;
    private javax.swing.JLabel jLabelWacom;
    private javax.swing.JLabel jLabelWacom1;
    private javax.swing.JLabel jLabelWacom2;
    private javax.swing.JLabel jLabelZdimPresent;
    private javax.swing.JLabel jLabelZdimPresent1;
    private javax.swing.JLabel jLabel_Heure;
    private javax.swing.JLabel jLabel_Init;
    private javax.swing.JLabel jLabel_NBSeg;
    private javax.swing.JList jListModeles;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelDevices;
    private javax.swing.JPanel jPanelIDEtData;
    private javax.swing.JPanel jPanel_Buttons;
    private javax.swing.JPanel jPanel_IdDevices;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinnerAge;
    private javax.swing.JSpinner jSpinner_InitNbFiles;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldHauteurNewTablet;
    private javax.swing.JTextField jTextFieldIdOpSystem;
    private javax.swing.JTextField jTextFieldIdOpSystem1;
    private javax.swing.JTextField jTextFieldLargeurNewTablet;
    private javax.swing.JTextField jTextFieldNomNewTablet;
    private javax.swing.JTextField jTextFieldPhysicalId;
    private javax.swing.JTextField jTextFieldPhysicalId1;
    private javax.swing.JTextField jTextFieldPressionNewTablet;
    private javax.swing.JTextField jTextFieldSpecificity;
    private javax.swing.JTextField jTextFieldStatut;
    private javax.swing.JTextField jTextFieldWacomFound;
    private javax.swing.JTextField jTextFieldWacomFound1;
    private javax.swing.JTextField jTextField_CoeffPression;
    private javax.swing.JTextField jTextField_CoeffPression1;
    private javax.swing.JTextField jTextField_Date;
    private javax.swing.JTextField jTextField_HauteurCoeff;
    private javax.swing.JTextField jTextField_HauteurCoeff1;
    private javax.swing.JTextField jTextField_HauteurEcran;
    private javax.swing.JTextField jTextField_HauteurEcran1;
    private javax.swing.JTextField jTextField_Hauteurtab;
    private javax.swing.JTextField jTextField_Hauteurtab1;
    private javax.swing.JTextField jTextField_Heure;
    private javax.swing.JTextField jTextField_LargeurCoeff;
    private javax.swing.JTextField jTextField_LargeurCoeff1;
    private javax.swing.JTextField jTextField_LargeurEcran;
    private javax.swing.JTextField jTextField_LargeurEcran1;
    private javax.swing.JTextField jTextField_Largeurtab;
    private javax.swing.JTextField jTextField_Largeurtab1;
    private javax.swing.JTextField jTextField_Manip;
    private javax.swing.JTextField jTextField_Name;
    private javax.swing.JTextField jTextField_NameExp;
    private javax.swing.JTextField jTextField_NbSeg;
    private javax.swing.JTextField jTextField_Pression;
    private javax.swing.JTextField jTextField_Pression1;
    private static javax.swing.JTextField jTextField_Racine;
    private javax.swing.JTextField jTextField_Repertoire;
    private javax.swing.JTextField jTextField_ZCoeff;
    private javax.swing.JTextField jTextField_ZCoeff1;
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
        this.jTextFieldIdOpSystem1.setText(operSystem);
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
       
        //si la tablette fait partie de la liste
        if (newNameTablet == null){      //alors la variable newNameTablet est null 
        indexModele = this.jListModeles.getSelectedIndex(); //on reccupere les caractéristiques de tablettes préenregistrées
        setNameTablet(tabletNames[indexModele]);

        /* les dimensions sont arrondies */
        largeurTablette = (float) Math.round(Const.largeurs[indexModele]);
        hauteurTablette = (float) Math.round(Const.hauteurs[indexModele]);
        
        dimTablette = new Dimension();
        dimTablette.setSize(getLargeurTablette(), getHauteurTablette());
        units = MM;
        System.out.println();
        /* Pression */
        
        pression = (float) Math.round(Const.pressions[indexModele]);
        
        } else {
            //si on ajoute une nouvelle tablette, on prend les paramétres des jtextfield de l'onglet "ajouter une tablette"
        largeurTablette = (float) Math.round(Float.parseFloat(jTextFieldLargeurNewTablet.getText())); //on récupere les paramétres entrés dans les textfields
        String l = nf3.format(largeurTablette) + " " + units;
        jTextField_Largeurtab1.setText(l);
        
        hauteurTablette = (float) Math.round(Float.parseFloat(jTextFieldHauteurNewTablet.getText()));
        String h = nf3.format(hauteurTablette) + " " + units;
        jTextField_Hauteurtab1.setText(h);
        
        pression = (float) Math.round(Float.parseFloat(jTextFieldPressionNewTablet.getText()));
        String p = nf3.format(pression) + " " + units;
        jTextField_Pression1.setText(p);
        
        dimTablette = new Dimension();
        dimTablette.setSize(getLargeurTablette(), getHauteurTablette());
        units = MM;
        
        this.jTextFieldStatut.setText("Tablette ajoutée: " + newNameTablet + ", dimensions: "+ largeurTablette+ " "+ hauteurTablette );
        jTextFieldStatut.setBackground(Color.green); //le textfield devient vert pour confirmer l'ajout de la tablette
        }
   }


    private void afficheModeleDim() {
        if (newNameTablet == null){
          
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
        this.jTextField_HauteurEcran1.setText(hScreen);
        String lScreen = nf.format(getDimEcran().width) + " pixels";
        this.jTextField_LargeurEcran.setText(lScreen);
        this.jTextField_LargeurEcran1.setText(lScreen);

        /* Dimensions tablette */
        getModeleSelection();

        String h = nf3.format(hauteurTablette) + " " + units;
        jTextField_Hauteurtab.setText(h);
        
        String l = nf3.format(largeurTablette) + " " + units;
        jTextField_Largeurtab.setText(l);
        
        String p = nf3.format(pression) + " " + "UA";
        jTextField_Pression.setText(p);
        
        System.out.println(tabletNames[indexModele] +" " + largeurTablette + " " + hauteurTablette + " "+ pression);
       
//        System.out.println("getModel " + tabletNames[indexModele] + "  " + largeurTablette + " l " + l);
//        System.out.println("getModel " + tabletNames[indexModele] + "  " + hauteurTablette + " h " + h);
        
        /* Coefficients de conversion */

        String coeffH = nf3.format(this.getCoeffConvHauteur());
        this.jTextField_HauteurCoeff.setText(coeffH);
        String coeffL = nf3.format(this.getCoeffConvLargeur());
        jTextField_LargeurCoeff.setText(coeffL);
        
        
        String coeffZ =  nf5.format(this.getCoeffConvZ());
        this.jTextField_ZCoeff.setText(coeffZ);
       
        
        String coeffP = nf5.format(this.getCoeffConvP()); // ajout d'un coeff de pression
        this.jTextField_CoeffPression.setText(coeffP);
        
        } else {
        
        /* Dimensions tablette */
        getModeleSelection();
        
        String coeffH = nf3.format(this.getCoeffConvHauteur());
        this.jTextField_HauteurCoeff1.setText(coeffH);
        
        String coeffL = nf3.format(this.getCoeffConvLargeur());
        jTextField_LargeurCoeff1.setText(coeffL);
        
        
        String coeffZ =  nf5.format(this.getCoeffConvZ());
        this.jTextField_ZCoeff1.setText(coeffZ);
        
        String coeffP = nf5.format(this.getCoeffConvP());
        this.jTextField_CoeffPression1.setText(coeffP); //coeff de pression d'une tablette nouvellement ajoutée
        
        idCurseur =(String) this.jComboBoxCurseur1.getSelectedItem(); //comboBox permettant de selectionner un nouveau stylet
        
        System.out.println(newNameTablet +" " + largeurTablette + " " + hauteurTablette + " "+ pression);
        
        dimTablette = new Dimension();
        dimTablette.setSize(getLargeurTablette(), getHauteurTablette());
        units = MM;
        
        this.jTextFieldStatut.setText("Tablette ajoutée: " + newNameTablet + ", dimensions: "+ largeurTablette+ " "+ hauteurTablette );
        jTextFieldStatut.setBackground(Color.green); //le textfield est vert cela indique que la tablette est bien ajoutée
        
                }
        
        
       
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
        if (newNameTablet == null){ //si la tablette est dans la liste
            indexModele = this.jListModeles.getSelectedIndex();
            setNameTablet(tabletNames[indexModele]);   
        }else{ //sinon si il s'agit d'une nouvelle tablette
            nameTablet = newNameTablet;
        }
       
        return nameTablet;
    }
    
    /**
     * @return the allNameTablet
     */
    public String getAllNameTablet() {
        allNameTablet = nameTablet + newNameTablet;
        return allNameTablet;
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
        if (newNameTablet== null){
        idCurseur =(String) this.jComboBoxCurseur.getSelectedItem();   
        } else{
        idCurseur =(String) this.jComboBoxCurseur1.getSelectedItem();    
        }
        return idCurseur;
    }

    /**
     * @param idCurseur the idCurseur to set
     */
    public void setIdCurseur(String idCurseur) {
        this.idCurseur = idCurseur;
        this.jComboBoxCurseur.setSelectedItem(idCurseur);
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
     * @return the pression
     */
    public float getPression() { //ajout du parametre de pression
        pression = (float) Const.pressions[indexModele];
        return pression;
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
     * @return the coeffConvP
     */
    public float getCoeffConvP() {
        return coeffConvP;
        
        
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
    public boolean isWacomPresent() { //verifie si la tablette est presente
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
    public String getTabletteConnected() { //statut connecté
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
