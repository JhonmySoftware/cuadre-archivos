package com.banco.cuadre;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class CuadreApp extends JFrame {
    
    private static final Color DAVI_ROJO = new Color(210, 20, 30);
    private static final Color DAVI_ROJO_OSCURO = new Color(160, 15, 25);
    private static final Color DAVI_ROJO_CLARO = new Color(220, 40, 50);
    private static final Color DAVI_GRIS = new Color(80, 80, 90);
    private static final Color DAVI_AZUL = new Color(30, 50, 80);
    
    private static final Color BG_PRINCIPAL = new Color(240, 242, 245);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BG_INPUT = new Color(250, 251, 253);
    private static final Color TEXT_PRINCIPAL = new Color(40, 40, 50);
    private static final Color TEXT_SECUNDARIO = new Color(100, 110, 120);
    private static final Color BORDER_LIGHT = new Color(220, 225, 230);
    
    private LectorExcel.InfoArchivo infoArchivo1;
    private LectorExcel.InfoArchivo infoArchivo2;
    
    private JComboBox<String> cmbArchivo1;
    private JComboBox<String> cmbHoja1;
    private JComboBox<String> cmbArchivo2;
    private JComboBox<String> cmbHoja2;
    
    private JPanel panelRelaciones;
    private List<RelacionItem> relaciones = new ArrayList<>();
    
    private JTextArea areaLog;
    private JLabel lblEstado;
    private JProgressBar barraProgreso;
    
    private JRadioButton rbMatchAll;
    private JRadioButton rbMatchAny;
    private ButtonGroup bgMatchMode;
    
    private static class RelacionItem {
        JComboBox<String> cmbA;
        JComboBox<String> cmbB;
        JButton btnEliminar;
        JLabel lblNumero;
        JLabel lblTipo;
        JTextField txtBuscaA;
        JTextField txtBuscaB;
        DefaultComboBoxModel<String> modeloA;
        DefaultComboBoxModel<String> modeloB;
        List<String> todasColumnasA = new ArrayList<>();
        List<String> todasColumnasB = new ArrayList<>();
        JCheckBox chkObligatorio;
        boolean esPrimaryKey = false;
    }
    
    private void filtrarColumnasA(RelacionItem item) {
        String filtro = item.txtBuscaA.getText().toLowerCase();
        SwingUtilities.invokeLater(() -> {
            item.modeloA.removeAllElements();
            for (String col : item.todasColumnasA) {
                if (col.toLowerCase().contains(filtro)) {
                    item.modeloA.addElement(col);
                }
            }
        });
    }
    
    private void filtrarColumnasB(RelacionItem item) {
        String filtro = item.txtBuscaB.getText().toLowerCase();
        SwingUtilities.invokeLater(() -> {
            item.modeloB.removeAllElements();
            for (String col : item.todasColumnasB) {
                if (col.toLowerCase().contains(filtro)) {
                    item.modeloB.addElement(col);
                }
            }
        });
    }
    
    public CuadreApp() {
        setTitle("Cuadre de Archivos - Banco Davivienda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        crearInterfaz();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    private void crearInterfaz() {
        setBackground(BG_PRINCIPAL);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(BG_PRINCIPAL);
        
        panelPrincipal.add(crearEncabezado(), BorderLayout.NORTH);
        panelPrincipal.add(crearContenido(), BorderLayout.CENTER);
        panelPrincipal.add(crearPanelInferior(), BorderLayout.SOUTH);
        
        setContentPane(panelPrincipal);
    }
    
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DAVI_AZUL);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JPanel barraColores = new JPanel(new GridLayout(1, 4, 0, 0));
        barraColores.setPreferredSize(new Dimension(0, 8));
        barraColores.add(crearBanda(DAVI_ROJO));
        barraColores.add(crearBanda(DAVI_ROJO_OSCURO));
        barraColores.add(crearBanda(DAVI_ROJO_CLARO));
        barraColores.add(crearBanda(DAVI_ROJO));
        
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(BG_PRINCIPAL);
        contenido.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        
        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        
        JLabel lblDavivienda = new JLabel("BANCO DAVIVIENDA");
        lblDavivienda.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDavivienda.setForeground(DAVI_ROJO);
        lblDavivienda.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel("Cuadre de Archivos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(DAVI_AZUL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Validacion y cruce de informacion");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(TEXT_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        texto.add(lblDavivienda);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblTitulo);
        texto.add(Box.createVerticalStrut(4));
        texto.add(lblSubtitulo);
        izq.add(texto);
        
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        der.setOpaque(false);
        
        lblEstado = new JLabel("Listo para iniciar");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(TEXT_SECUNDARIO);
        
        JPanel estadoPanel = new JPanel();
        estadoPanel.setLayout(new BoxLayout(estadoPanel, BoxLayout.X_AXIS));
        estadoPanel.setOpaque(false);
        estadoPanel.add(lblEstado);
        
        der.add(estadoPanel);
        
        contenido.add(izq, BorderLayout.WEST);
        contenido.add(der, BorderLayout.EAST);
        
        panel.add(barraColores, BorderLayout.NORTH);
        panel.add(contenido, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearBanda(Color color) {
        JPanel p = new JPanel();
        p.setBackground(color);
        return p;
    }
    
    private JPanel crearContenido() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 30, 15, 30));
        
        panel.add(crearPanelSeleccion(), BorderLayout.NORTH);
        panel.add(crearPanelRelaciones(), BorderLayout.CENTER);
        panel.add(crearPanelEjecutar(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelSeleccion() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);
        
        panel.add(crearPanelArchivo("Archivo A", "ORIGEN", true, DAVI_ROJO));
        panel.add(crearPanelArchivo("Archivo B", "DESTINO", false, DAVI_ROJO));
        
        return panel;
    }
    
    private JPanel crearPanelArchivo(String titulo, String subtitulo, boolean esArchivo1, Color colorAccent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(colorAccent);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(TEXT_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JSeparator sep = new JSeparator();
        sep.setForeground(colorAccent);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createVerticalStrut(3));
        headerPanel.add(lblSubtitulo);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(sep);
        headerPanel.add(Box.createVerticalStrut(15));
        
        JButton btnSeleccionar = crearBotonPrimario("Seleccionar archivo Excel", colorAccent);
        btnSeleccionar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JComboBox<String> cmbArchivo = new JComboBox<>();
        cmbArchivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cmbArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbArchivo.setBackground(BG_INPUT);
        cmbArchivo.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JComboBox<String> cmbHoja = new JComboBox<>();
        cmbHoja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        cmbHoja.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbHoja.setBackground(BG_INPUT);
        cmbHoja.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        if (esArchivo1) {
            cmbArchivo1 = cmbArchivo;
            cmbHoja1 = cmbHoja;
        } else {
            cmbArchivo2 = cmbArchivo;
            cmbHoja2 = cmbHoja;
        }
        
        final JComboBox<String> cmbArchivoFinal = cmbArchivo;
        final JComboBox<String> cmbHojaFinal = cmbHoja;
        
        btnSeleccionar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
            
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File archivo = chooser.getSelectedFile();
                
                try {
                    LectorExcel.InfoArchivo info = new LectorExcel().leerArchivo(archivo.getAbsolutePath());
                    
                    if (esArchivo1) {
                        infoArchivo1 = info;
                        cmbArchivo1.removeAllItems();
                        cmbArchivo1.addItem(archivo.getName());
                        cmbHoja1.removeAllItems();
                        for (LectorExcel.InfoHoja hoja : info.hojas) {
                            cmbHoja1.addItem(hoja.nombre);
                        }
                        cmbHoja1.setSelectedIndex(0);
                    } else {
                        infoArchivo2 = info;
                        cmbArchivo2.removeAllItems();
                        cmbArchivo2.addItem(archivo.getName());
                        cmbHoja2.removeAllItems();
                        for (LectorExcel.InfoHoja hoja : info.hojas) {
                            cmbHoja2.addItem(hoja.nombre);
                        }
                        cmbHoja2.setSelectedIndex(0);
                    }
                    
                    actualizarRelaciones();
                    agregarLog("Archivo cargado: " + archivo.getName());
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cmbHoja.addActionListener(e -> actualizarRelaciones());
        
        panel.add(headerPanel);
        panel.add(btnSeleccionar);
        panel.add(Box.createVerticalStrut(12));
        
        JPanel archivoPanel = crearLabeledField("Archivo:", cmbArchivoFinal);
        panel.add(archivoPanel);
        panel.add(Box.createVerticalStrut(10));
        
        JPanel hojaPanel = crearLabeledField("Hoja:", cmbHojaFinal);
        panel.add(hojaPanel);
        
        return panel;
    }
    
    private JPanel crearLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECUNDARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        
        return panel;
    }
    
    private JButton crearBotonPrimario(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JButton crearBotonSecundario(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JPanel crearPanelRelaciones() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setOpaque(false);
        
        JPanel tituloPanel = new JPanel();
        tituloPanel.setLayout(new BoxLayout(tituloPanel, BoxLayout.Y_AXIS));
        tituloPanel.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Relaciones de Columnas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(DAVI_AZUL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblSubtitulo = new JLabel("Defina como se relacionan las columnas entre archivos");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(TEXT_SECUNDARIO);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        tituloPanel.add(lblTitulo);
        tituloPanel.add(lblSubtitulo);
        
        JPanel panelModo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelModo.setOpaque(false);
        
        JPanel modoGroup = new JPanel();
        modoGroup.setLayout(new BoxLayout(modoGroup, BoxLayout.X_AXIS));
        modoGroup.setOpaque(false);
        modoGroup.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JLabel lblModo = new JLabel("Modo:");
        lblModo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblModo.setForeground(DAVI_AZUL);
        
        rbMatchAll = new JRadioButton("TODAS (Y)");
        rbMatchAll.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rbMatchAll.setForeground(DAVI_AZUL);
        rbMatchAll.setSelected(true);
        rbMatchAll.setOpaque(false);
        rbMatchAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbMatchAll.addActionListener(e -> agregarLog("Modo: TODAS las columnas"));
        
        rbMatchAny = new JRadioButton("CUALQUIERA (O)");
        rbMatchAny.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rbMatchAny.setForeground(DAVI_AZUL);
        rbMatchAny.setOpaque(false);
        rbMatchAny.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbMatchAny.addActionListener(e -> agregarLog("Modo: AL MENOS UNA columna"));
        
        bgMatchMode = new ButtonGroup();
        bgMatchMode.add(rbMatchAll);
        bgMatchMode.add(rbMatchAny);
        
        modoGroup.add(lblModo);
        modoGroup.add(Box.createHorizontalStrut(10));
        modoGroup.add(rbMatchAll);
        modoGroup.add(Box.createHorizontalStrut(8));
        modoGroup.add(rbMatchAny);
        
        panelModo.add(modoGroup);
        
        panelEncabezado.add(tituloPanel, BorderLayout.WEST);
        panelEncabezado.add(panelModo, BorderLayout.EAST);
        
        panel.add(panelEncabezado, BorderLayout.NORTH);
        
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(BG_CARD);
        cardPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel headerRel = new JPanel(new GridLayout(1, 1));
        headerRel.setBackground(new Color(245, 247, 250));
        headerRel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblInfo = new JLabel("PK = Primary Key (identificador unico) | KEY = Campo adicional");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(TEXT_SECUNDARIO);
        headerRel.add(lblInfo);
        
        cardPanel.add(headerRel);
        
        panelRelaciones = new JPanel();
        panelRelaciones.setLayout(new BoxLayout(panelRelaciones, BoxLayout.Y_AXIS));
        panelRelaciones.setBackground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(panelRelaciones);
        scroll.setPreferredSize(new Dimension(0, 180));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(BORDER_LIGHT));
        
        cardPanel.add(scroll);
        
        panel.add(cardPanel, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBotones.setOpaque(false);
        
        JButton btnAgregarPK = crearBotonSecundario("+ Primary Key", DAVI_ROJO);
        btnAgregarPK.addActionListener(e -> agregarRelacionBase(true));
        
        JButton btnAgregar = crearBotonSecundario("+ Agregar Key", DAVI_AZUL);
        btnAgregar.addActionListener(e -> agregarRelacionBase(false));
        
        JButton btnAgregarDefault = crearBotonSecundario("Auto-detectar", DAVI_GRIS);
        btnAgregarDefault.addActionListener(e -> autoDetectarRelaciones());
        
        panelBotones.add(btnAgregarPK);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnAgregarDefault);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void agregarRelacionBase(boolean esPK) {
        if (infoArchivo1 == null || infoArchivo2 == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambos archivos primero", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        RelacionItem item = new RelacionItem();
        item.esPrimaryKey = esPK;
        int numero = relaciones.size() + 1;
        
        JPanel panelItem = new JPanel();
        panelItem.setLayout(new BoxLayout(panelItem, BoxLayout.Y_AXIS));
        panelItem.setBackground(Color.WHITE);
        panelItem.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(esPK ? DAVI_ROJO : BORDER_LIGHT, esPK ? 2 : 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelSuperior.setBackground(Color.WHITE);
        
        JPanel badgePanel = new JPanel();
        badgePanel.setLayout(new BoxLayout(badgePanel, BoxLayout.X_AXIS));
        badgePanel.setOpaque(false);
        
        item.lblNumero = new JLabel(esPK ? "PK" : "K" + numero);
        item.lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        item.lblNumero.setForeground(esPK ? DAVI_ROJO : DAVI_AZUL);
        
        item.lblTipo = new JLabel(esPK ? "PRIMARY KEY" : "KEY");
        item.lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 9));
        item.lblTipo.setForeground(Color.WHITE);
        item.lblTipo.setBackground(esPK ? DAVI_ROJO : DAVI_AZUL);
        item.lblTipo.setOpaque(true);
        item.lblTipo.setBorder(new EmptyBorder(2, 6, 2, 6));
        
        badgePanel.add(item.lblNumero);
        badgePanel.add(Box.createHorizontalStrut(5));
        badgePanel.add(item.lblTipo);
        
        JPanel buscaPanelA = new JPanel();
        buscaPanelA.setLayout(new BoxLayout(buscaPanelA, BoxLayout.Y_AXIS));
        buscaPanelA.setOpaque(false);
        
        JLabel lblBuscaA = new JLabel("A");
        lblBuscaA.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBuscaA.setForeground(TEXT_SECUNDARIO);
        lblBuscaA.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        item.txtBuscaA = new JTextField();
        item.txtBuscaA.setPreferredSize(new Dimension(100, 26));
        item.txtBuscaA.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.txtBuscaA.setToolTipText("Buscar...");
        
        buscaPanelA.add(lblBuscaA);
        buscaPanelA.add(item.txtBuscaA);
        
        JLabel lblFlecha = new JLabel("→");
        lblFlecha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFlecha.setForeground(DAVI_AZUL);
        
        JPanel buscaPanelB = new JPanel();
        buscaPanelB.setLayout(new BoxLayout(buscaPanelB, BoxLayout.Y_AXIS));
        buscaPanelB.setOpaque(false);
        
        JLabel lblBuscaB = new JLabel("B");
        lblBuscaB.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblBuscaB.setForeground(TEXT_SECUNDARIO);
        lblBuscaB.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        item.txtBuscaB = new JTextField();
        item.txtBuscaB.setPreferredSize(new Dimension(100, 26));
        item.txtBuscaB.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.txtBuscaB.setToolTipText("Buscar...");
        
        buscaPanelB.add(lblBuscaB);
        buscaPanelB.add(item.txtBuscaB);
        
        item.btnEliminar = new JButton("X");
        item.btnEliminar.setBackground(new Color(240, 240, 245));
        item.btnEliminar.setForeground(TEXT_SECUNDARIO);
        item.btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        item.btnEliminar.setPreferredSize(new Dimension(30, 30));
        item.btnEliminar.setBorderPainted(false);
        item.btnEliminar.setFocusPainted(false);
        item.btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        final JPanel panelItemFinal = panelItem;
        item.btnEliminar.addActionListener(e -> {
            panelRelaciones.remove(panelItemFinal);
            relaciones.remove(item);
            reordenarRelaciones();
            panelRelaciones.revalidate();
            panelRelaciones.repaint();
        });
        
        panelSuperior.add(badgePanel);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(buscaPanelA);
        panelSuperior.add(Box.createHorizontalStrut(5));
        panelSuperior.add(lblFlecha);
        panelSuperior.add(Box.createHorizontalStrut(5));
        panelSuperior.add(buscaPanelB);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(item.btnEliminar);
        
        JPanel panelCombos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelCombos.setBackground(Color.WHITE);
        
        item.modeloA = new DefaultComboBoxModel<>();
        item.modeloB = new DefaultComboBoxModel<>();
        item.cmbA = new JComboBox<>(item.modeloA);
        item.cmbA.setPreferredSize(new Dimension(300, 28));
        item.cmbA.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        item.cmbB = new JComboBox<>(item.modeloB);
        item.cmbB.setPreferredSize(new Dimension(300, 28));
        item.cmbB.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        item.chkObligatorio = new JCheckBox("Obligatorio");
        item.chkObligatorio.setSelected(!esPK);
        item.chkObligatorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.chkObligatorio.setForeground(esPK ? DAVI_ROJO : DAVI_AZUL);
        item.chkObligatorio.setOpaque(false);
        item.chkObligatorio.setEnabled(esPK);
        
        panelCombos.add(Box.createHorizontalStrut(50));
        panelCombos.add(item.cmbA);
        panelCombos.add(Box.createHorizontalStrut(15));
        panelCombos.add(item.cmbB);
        panelCombos.add(Box.createHorizontalStrut(15));
        panelCombos.add(item.chkObligatorio);
        
        LectorExcel.InfoHoja hoja1 = infoArchivo1.hojas.get(cmbHoja1.getSelectedIndex());
        LectorExcel.InfoHoja hoja2 = infoArchivo2.hojas.get(cmbHoja2.getSelectedIndex());
        
        for (String col : hoja1.encabezados) {
            item.modeloA.addElement(col);
            item.todasColumnasA.add(col);
        }
        for (String col : hoja2.encabezados) {
            item.modeloB.addElement(col);
            item.todasColumnasB.add(col);
        }
        
        item.txtBuscaA.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
        });
        
        item.txtBuscaB.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
        });
        
        panelItem.add(panelSuperior);
        panelItem.add(Box.createVerticalStrut(8));
        panelItem.add(panelCombos);
        
        relaciones.add(item);
        panelRelaciones.add(panelItem);
        panelRelaciones.revalidate();
        panelRelaciones.repaint();
        
        agregarLog((esPK ? "PK" : "Key") + " agregada");
    }
    
    private void reordenarRelaciones() {
        int pkCount = 0;
        int keyCount = 0;
        for (RelacionItem item : relaciones) {
            if (item.esPrimaryKey) {
                pkCount++;
                item.lblNumero.setText("PK");
                item.lblNumero.setForeground(DAVI_ROJO);
                item.lblTipo.setText("PRIMARY KEY");
                item.lblTipo.setBackground(DAVI_ROJO);
            } else {
                keyCount++;
                item.lblNumero.setText("K" + keyCount);
                item.lblNumero.setForeground(DAVI_AZUL);
                item.lblTipo.setText("KEY");
                item.lblTipo.setBackground(DAVI_AZUL);
            }
        }
    }
    
    private void actualizarRelaciones() {
        if (infoArchivo1 == null || infoArchivo2 == null) return;
        
        LectorExcel.InfoHoja hoja1 = infoArchivo1.hojas.get(cmbHoja1.getSelectedIndex());
        LectorExcel.InfoHoja hoja2 = infoArchivo2.hojas.get(cmbHoja2.getSelectedIndex());
        
        for (RelacionItem item : relaciones) {
            String selA = (String) item.cmbA.getSelectedItem();
            String selB = (String) item.cmbB.getSelectedItem();
            
            item.todasColumnasA.clear();
            item.todasColumnasB.clear();
            item.modeloA.removeAllElements();
            item.modeloB.removeAllElements();
            
            for (String col : hoja1.encabezados) {
                item.modeloA.addElement(col);
                item.todasColumnasA.add(col);
            }
            for (String col : hoja2.encabezados) {
                item.modeloB.addElement(col);
                item.todasColumnasB.add(col);
            }
            
            if (selA != null) item.cmbA.setSelectedItem(selA);
            if (selB != null) item.cmbB.setSelectedItem(selB);
        }
    }
    
    private void autoDetectarRelaciones() {
        if (infoArchivo1 == null || infoArchivo2 == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambos archivos primero", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        panelRelaciones.removeAll();
        relaciones.clear();
        
        LectorExcel.InfoHoja hoja1 = infoArchivo1.hojas.get(cmbHoja1.getSelectedIndex());
        LectorExcel.InfoHoja hoja2 = infoArchivo2.hojas.get(cmbHoja2.getSelectedIndex());
        
        List<String> colsA = hoja1.encabezados;
        List<String> colsB = hoja2.encabezados;
        
        int max = Math.min(colsA.size(), colsB.size());
        int agregadas = 0;
        
        for (int i = 0; i < max; i++) {
            String colA = colsA.get(i).toLowerCase().replace("-", "").replace("_", "").replace(" ", "");
            String colB = colsB.get(i).toLowerCase().replace("-", "").replace("_", "").replace(" ", "");
            
            if (colA.equals(colB)) {
                RelacionesAutoDetectada(colsA.get(i), colsB.get(i), agregadas == 0);
                agregadas++;
            }
        }
        
        if (agregadas == 0) {
            agregarRelacionBase(false);
        }
        
        panelRelaciones.revalidate();
        panelRelaciones.repaint();
        agregarLog("Auto-detectadas " + agregadas + " relaciones");
    }
    
    private void RelacionesAutoDetectada(String colA, String colB, boolean esPK) {
        RelacionItem item = new RelacionItem();
        item.esPrimaryKey = esPK;
        int numero = relaciones.size() + 1;
        
        JPanel panelItem = new JPanel();
        panelItem.setLayout(new BoxLayout(panelItem, BoxLayout.Y_AXIS));
        panelItem.setBackground(Color.WHITE);
        panelItem.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(esPK ? DAVI_ROJO : BORDER_LIGHT, esPK ? 2 : 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelSuperior.setBackground(Color.WHITE);
        
        JPanel badgePanel = new JPanel();
        badgePanel.setLayout(new BoxLayout(badgePanel, BoxLayout.X_AXIS));
        badgePanel.setOpaque(false);
        
        item.lblNumero = new JLabel(esPK ? "PK" : "K" + numero);
        item.lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 12));
        item.lblNumero.setForeground(esPK ? DAVI_ROJO : DAVI_AZUL);
        
        item.lblTipo = new JLabel(esPK ? "PRIMARY KEY" : "KEY");
        item.lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 9));
        item.lblTipo.setForeground(Color.WHITE);
        item.lblTipo.setBackground(esPK ? DAVI_ROJO : DAVI_AZUL);
        item.lblTipo.setOpaque(true);
        item.lblTipo.setBorder(new EmptyBorder(2, 6, 2, 6));
        
        badgePanel.add(item.lblNumero);
        badgePanel.add(Box.createHorizontalStrut(5));
        badgePanel.add(item.lblTipo);
        
        item.txtBuscaA = new JTextField();
        item.txtBuscaA.setPreferredSize(new Dimension(100, 26));
        item.txtBuscaA.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        item.txtBuscaB = new JTextField();
        item.txtBuscaB.setPreferredSize(new Dimension(100, 26));
        item.txtBuscaB.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        item.chkObligatorio = new JCheckBox("Obligatorio");
        item.chkObligatorio.setSelected(!esPK);
        item.chkObligatorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.chkObligatorio.setForeground(esPK ? DAVI_ROJO : DAVI_AZUL);
        item.chkObligatorio.setOpaque(false);
        item.chkObligatorio.setEnabled(esPK);
        
        item.btnEliminar = new JButton("X");
        item.btnEliminar.setBackground(new Color(240, 240, 245));
        item.btnEliminar.setForeground(TEXT_SECUNDARIO);
        item.btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        item.btnEliminar.setPreferredSize(new Dimension(30, 30));
        item.btnEliminar.setBorderPainted(false);
        item.btnEliminar.setFocusPainted(false);
        final JPanel pItem = panelItem;
        item.btnEliminar.addActionListener(e -> {
            panelRelaciones.remove(pItem);
            relaciones.remove(item);
            reordenarRelaciones();
            panelRelaciones.revalidate();
            panelRelaciones.repaint();
        });
        
        panelSuperior.add(badgePanel);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(new JLabel("A:"));
        panelSuperior.add(item.txtBuscaA);
        panelSuperior.add(new JLabel("→"));
        panelSuperior.add(new JLabel("B:"));
        panelSuperior.add(item.txtBuscaB);
        panelSuperior.add(Box.createHorizontalStrut(15));
        panelSuperior.add(item.chkObligatorio);
        panelSuperior.add(item.btnEliminar);
        
        JPanel panelCombos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelCombos.setBackground(Color.WHITE);
        
        item.modeloA = new DefaultComboBoxModel<>();
        item.modeloB = new DefaultComboBoxModel<>();
        item.cmbA = new JComboBox<>(item.modeloA);
        item.cmbA.setPreferredSize(new Dimension(300, 28));
        item.cmbA.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.cmbB = new JComboBox<>(item.modeloB);
        item.cmbB.setPreferredSize(new Dimension(300, 28));
        item.cmbB.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        panelCombos.add(Box.createHorizontalStrut(50));
        panelCombos.add(item.cmbA);
        panelCombos.add(Box.createHorizontalStrut(15));
        panelCombos.add(item.cmbB);
        
        LectorExcel.InfoHoja hoja1 = infoArchivo1.hojas.get(cmbHoja1.getSelectedIndex());
        LectorExcel.InfoHoja hoja2 = infoArchivo2.hojas.get(cmbHoja2.getSelectedIndex());
        
        for (String col : hoja1.encabezados) {
            item.modeloA.addElement(col);
            item.todasColumnasA.add(col);
        }
        for (String col : hoja2.encabezados) {
            item.modeloB.addElement(col);
            item.todasColumnasB.add(col);
        }
        
        item.cmbA.setSelectedItem(colA);
        item.cmbB.setSelectedItem(colB);
        
        item.txtBuscaA.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasA(item); }
        });
        
        item.txtBuscaB.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarColumnasB(item); }
        });
        
        panelItem.add(panelSuperior);
        panelItem.add(Box.createVerticalStrut(8));
        panelItem.add(panelCombos);
        
        relaciones.add(item);
        panelRelaciones.add(panelItem);
    }
    
    private JPanel crearPanelEjecutar() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JTextArea instrucciones = new JTextArea(
            "PK = Primary Key (identificador unico obligatorio)\n" +
            "KEY = Campo adicional de comparacion\n" +
            "TODAS (Y): Todas las relaciones deben coincidir\n" +
            "CUALQUIERA (O): Al menos una relacion debe coincidir"
        );
        instrucciones.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        instrucciones.setEditable(false);
        instrucciones.setOpaque(false);
        instrucciones.setForeground(TEXT_SECUNDARIO);
        instrucciones.setBorder(null);
        infoPanel.add(instrucciones);
        
        JButton btnEjecutar = new JButton("Ejecutar Cruce");
        btnEjecutar.setPreferredSize(new Dimension(180, 50));
        btnEjecutar.setBackground(DAVI_ROJO);
        btnEjecutar.setForeground(Color.WHITE);
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEjecutar.setFocusPainted(false);
        btnEjecutar.setBorderPainted(false);
        btnEjecutar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEjecutar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnEjecutar.setBackground(new Color(180, 15, 25)); }
            public void mouseExited(MouseEvent e) { btnEjecutar.setBackground(DAVI_ROJO); }
        });
        btnEjecutar.addActionListener(e -> ejecutarCruce());
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(btnEjecutar, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 30, 15, 30));
        
        barraProgreso = new JProgressBar(0, 100);
        barraProgreso.setStringPainted(true);
        barraProgreso.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barraProgreso.setBackground(BG_CARD);
        barraProgreso.setForeground(DAVI_ROJO);
        barraProgreso.setBorder(new LineBorder(BORDER_LIGHT));
        
        areaLog = new JTextArea(4, 50);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaLog.setBackground(DAVI_AZUL);
        areaLog.setForeground(new Color(200, 210, 220));
        areaLog.setCaretColor(DAVI_ROJO);
        
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(new LineBorder(DAVI_AZUL, 2));
        
        panel.add(barraProgreso, BorderLayout.NORTH);
        panel.add(scrollLog, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void ejecutarCruce() {
        if (infoArchivo1 == null || infoArchivo2 == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambos archivos primero", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (relaciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos una relacion de columnas", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<String> colsA = new ArrayList<>();
        List<String> colsB = new ArrayList<>();
        List<Boolean> esObligatorio = new ArrayList<>();
        boolean usarOR = rbMatchAny.isSelected();
        
        for (RelacionItem item : relaciones) {
            colsA.add((String) item.cmbA.getSelectedItem());
            colsB.add((String) item.cmbB.getSelectedItem());
            esObligatorio.add(item.chkObligatorio.isSelected() || item.esPrimaryKey);
        }
        
        final boolean matchAny = usarOR;
        
        Thread worker = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("Procesando...");
                    lblEstado.setForeground(DAVI_ROJO);
                    barraProgreso.setValue(0);
                    barraProgreso.setString("0% - Iniciando...");
                    areaLog.setText("");
                    agregarLog("=== INICIANDO CRUCE ===");
                    agregarLog("Modo: " + (matchAny ? "CUALQUIERA (O)" : "TODAS (Y)"));
                });
                
                LectorExcel.InfoHoja hoja1 = infoArchivo1.hojas.get(cmbHoja1.getSelectedIndex());
                LectorExcel.InfoHoja hoja2 = infoArchivo2.hojas.get(cmbHoja2.getSelectedIndex());
                
                agregarLog("Archivo A: " + hoja1.nombre + " (" + hoja1.datos.size() + " registros)");
                agregarLog("Archivo B: " + hoja2.nombre + " (" + hoja2.datos.size() + " registros)");
                
                int pkCount = 0;
                int keyCount = 0;
                for (RelacionItem item : relaciones) {
                    if (item.esPrimaryKey) {
                        pkCount++;
                        agregarLog("PK: " + item.cmbA.getSelectedItem() + " -> " + item.cmbB.getSelectedItem());
                    } else {
                        keyCount++;
                        agregarLog("K" + keyCount + ": " + item.cmbA.getSelectedItem() + " -> " + item.cmbB.getSelectedItem());
                    }
                }
                
                SwingUtilities.invokeLater(() -> barraProgreso.setString("30% - Indexando..."));
                
                ValidadorCruce validador = new ValidadorCruce();
                
                SwingUtilities.invokeLater(() -> barraProgreso.setString("50% - Cruce de datos..."));
                agregarLog("Realizando cruce...");
                
                Map<String, Object> resultadoCompleto = validador.generarResultadoConModo(
                    hoja1.datos,
                    colsA,
                    hoja2.datos,
                    colsB,
                    esObligatorio,
                    matchAny
                );
                
                SwingUtilities.invokeLater(() -> barraProgreso.setString("80% - Preparando archivo..."));
                agregarLog("Cruce completado.");
                
                Map<String, String> estadisticas = (Map<String, String>) resultadoCompleto.get("estadisticas");
                
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Guardar resultado");
                chooser.setSelectedFile(new File("Cuadre_Resultado.xlsx"));
                
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String tempRuta = chooser.getSelectedFile().getAbsolutePath();
                    if (!tempRuta.endsWith(".xlsx")) {
                        tempRuta += ".xlsx";
                    }
                    final String ruta = tempRuta;
                    
                    SwingUtilities.invokeLater(() -> barraProgreso.setString("85% - Guardando archivo..."));
                    agregarLog("Guardando archivo...");
                    
                    EscritorExcel escritor = new EscritorExcel();
                    escritor.guardarResultadoNuevo(resultadoCompleto, ruta, (percent, msg) -> {
                        final int p = percent;
                        final String m = msg;
                        SwingUtilities.invokeLater(() -> {
                            barraProgreso.setValue(p);
                            barraProgreso.setString(m);
                        });
                    });
                    
                    agregarLog("Archivo guardado: " + ruta);
                    
                    StringBuilder statsFinal = new StringBuilder();
                    for (Map.Entry<String, String> entry : estadisticas.entrySet()) {
                        statsFinal.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                    
                    SwingUtilities.invokeLater(() -> {
                        barraProgreso.setValue(100);
                        barraProgreso.setString("100% - Completado");
                        lblEstado.setText("Listo para iniciar");
                        lblEstado.setForeground(TEXT_SECUNDARIO);
                        JOptionPane.showMessageDialog(this,
                            "Cruce completado!\n\n" + statsFinal.toString() + "\nArchivo guardado en:\n" + ruta,
                            "Resultado", JOptionPane.INFORMATION_MESSAGE);
                    });
                    
                    agregarLog("=== CRUCE COMPLETADO ===");
                }
                
            } catch (Exception ex) {
                agregarLog("ERROR: " + ex.getMessage());
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    lblEstado.setText("Error");
                    lblEstado.setForeground(Color.WHITE);
                    barraProgreso.setString("Error");
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        
        worker.start();
    }
    
    private void agregarLog(String mensaje) {
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            areaLog.append("[" + timestamp + "] " + mensaje + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            CuadreApp app = new CuadreApp();
            app.setVisible(true);
        });
    }
}
