package br.edu.ifsuldeminas.mch.sd.rmi.client;

import br.edu.ifsuldeminas.mch.sd.rmi.remote.Operations;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;

public class CalculatorGUI extends JFrame {

    // ─── Paleta dark ─────────────────────────────────────────────────────────
    private static final Color BG        = new Color(18, 18, 28);
    private static final Color PANEL     = new Color(28, 28, 42);
    private static final Color CARD      = new Color(38, 38, 58);
    private static final Color ACCENT    = new Color(99, 102, 241);
    private static final Color ACCENT2   = new Color(139, 92, 246);
    private static final Color SUCCESS   = new Color(52, 211, 153);
    private static final Color ERROR     = new Color(248, 113, 113);
    private static final Color WARNING   = new Color(251, 191, 36);
    private static final Color TEXT      = new Color(226, 232, 240);
    private static final Color TEXT_DIM  = new Color(100, 116, 139);
    private static final Color BORDER    = new Color(55, 55, 80);

    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 13);
    private static final Font FONT_RESULT = new Font("Segoe UI", Font.BOLD, 20);

    // ─── Estado ──────────────────────────────────────────────────────────────
    private Operations calc;
    private JLabel lblStatus;
    private JLabel lblResult;
    private JLabel lblConnStatus;
    private JTextField fHost;
    private JTextField fPort;
    private DefaultTableModel historyModel;

    // ─── Constructor ─────────────────────────────────────────────────────────
    public CalculatorGUI() {
        super("Calculadora RMI — IFSULDEMINAS");
        applyDarkLAF();
        buildUI();
        // não conecta automaticamente — usuário configura o IP e clica
    }

    // ─── Look & Feel global ──────────────────────────────────────────────────
    private void applyDarkLAF() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("Panel.background", PANEL);
        UIManager.put("TabbedPane.background", PANEL);
        UIManager.put("TabbedPane.selected", CARD);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.contentAreaColor", CARD);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("TextField.background", CARD);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("Table.background", CARD);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.background", PANEL);
        UIManager.put("TableHeader.foreground", TEXT_DIM);
        UIManager.put("ScrollPane.background", CARD);
        UIManager.put("Viewport.background", CARD);
    }

    // ─── UI Principal ────────────────────────────────────────────────────────
    private void buildUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 660);
        setMinimumSize(new Dimension(720, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ─── Header com painel de conexão ────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(PANEL);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(10, 14, 10, 14)
        ));

        // Título
        JLabel title = new JLabel("⊞  Calculadora Remota RMI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.WEST);

        // Painel central: IP + Porta + botão conectar + indicador
        JPanel connPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        connPanel.setOpaque(false);

        JLabel lblHost = new JLabel("Host:");
        lblHost.setFont(FONT_BODY);
        lblHost.setForeground(TEXT_DIM);

        fHost = new JTextField("localhost", 12);
        fHost.setFont(FONT_MONO);
        fHost.setBackground(new Color(48, 48, 70));
        fHost.setForeground(TEXT);
        fHost.setCaretColor(TEXT);
        fHost.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fHost.setPreferredSize(new Dimension(150, 32));

        JLabel lblPort = new JLabel("Porta:");
        lblPort.setFont(FONT_BODY);
        lblPort.setForeground(TEXT_DIM);

        fPort = new JTextField("1099", 5);
        fPort.setFont(FONT_MONO);
        fPort.setBackground(new Color(48, 48, 70));
        fPort.setForeground(TEXT);
        fPort.setCaretColor(TEXT);
        fPort.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(4, 8, 4, 8)
        ));
        fPort.setPreferredSize(new Dimension(70, 32));

        // Indicador de status de conexão (bolinha colorida)
        lblConnStatus = new JLabel("● Desconectado");
        lblConnStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConnStatus.setForeground(ERROR);

        JButton btnConn = btn("Conectar", SUCCESS, this::connect);
        btnConn.setPreferredSize(new Dimension(110, 32));

        // Enter no campo host ou porta também conecta
        fHost.addActionListener(e -> connect());
        fPort.addActionListener(e -> connect());

        connPanel.add(lblHost);
        connPanel.add(fHost);
        connPanel.add(lblPort);
        connPanel.add(fPort);
        connPanel.add(btnConn);
        connPanel.add(lblConnStatus);
        header.add(connPanel, BorderLayout.CENTER);

        // Resultado em destaque
        lblResult = new JLabel("—");
        lblResult.setFont(FONT_RESULT);
        lblResult.setForeground(ACCENT);
        lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
        header.add(lblResult, BorderLayout.EAST);

        return header;
    }

    // ─── Centro (tabs + histórico) ───────────────────────────────────────────
    private JSplitPane buildCenter() {
        JTabbedPane tabs = buildTabs();
        JPanel historyPanel = buildHistoryPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabs, historyPanel);
        split.setDividerLocation(520);
        split.setDividerSize(3);
        split.setBackground(BG);
        split.setBorder(null);
        return split;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(PANEL);
        tabs.setForeground(TEXT);
        tabs.setFont(FONT_BODY);
        tabs.setBorder(new EmptyBorder(8, 8, 8, 4));

        tabs.addTab("  ± Básico  ", buildBasicTab());
        tabs.addTab("  √ Avançado  ", buildAdvancedTab());
        tabs.addTab("  % Outros  ", buildMiscTab());
        tabs.addTab("  # Conversão  ", buildConversionTab());

        return tabs;
    }

    // ─── Aba Básico ──────────────────────────────────────────────────────────
    private JPanel buildBasicTab() {
        JPanel p = card();
        p.setLayout(new GridBagLayout());

        JTextField fA = field("10");
        JTextField fB = field("5");

        GridBagConstraints g = gbc();
        g.gridy = 0; addRow(p, g, "Operando A:", fA);
        g.gridy = 1; addRow(p, g, "Operando B:", fB);

        JPanel btns = new JPanel(new GridLayout(1, 4, 8, 0));
        btns.setOpaque(false);
        btns.add(btn("+", ACCENT,  () -> show(call(() -> calc.sum(num(fA), num(fB))))));
        btns.add(btn("−", ACCENT,  () -> show(call(() -> calc.sub(num(fA), num(fB))))));
        btns.add(btn("×", ACCENT,  () -> show(call(() -> calc.mul(num(fA), num(fB))))));
        btns.add(btn("÷", ACCENT,  () -> show(call(() -> calc.div(num(fA), num(fB))))));

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(14, 0, 0, 0);
        p.add(btns, g);

        return wrap(p);
    }

    // ─── Aba Avançado ────────────────────────────────────────────────────────
    private JPanel buildAdvancedTab() {
        JPanel p = card();
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JTextField fRBase = field("27");
        JTextField fRGrau = field("3");
        JTextField fPBase = field("2");
        JTextField fPExp  = field("10");
        JTextField fFat   = field("10");

        g.gridy = 0; addSection(p, g, "Raiz");
        g.gridy = 1; addRow(p, g, "Base:", fRBase);
        g.gridy = 2; addRow(p, g, "Grau (2=√, 3=∛...):", fRGrau);
        g.gridy = 3; addCenteredBtn(p, g, btn("Calcular Raiz", ACCENT2,
            () -> show(call(() -> calc.sqrt(num(fRBase), num(fRGrau))))));

        g.gridy = 4; addSection(p, g, "Potência");
        g.gridy = 5; addRow(p, g, "Base:", fPBase);
        g.gridy = 6; addRow(p, g, "Expoente:", fPExp);
        g.gridy = 7; addCenteredBtn(p, g, btn("Calcular Potência", ACCENT2,
            () -> show(call(() -> calc.power(num(fPBase), num(fPExp))))));

        g.gridy = 8; addSection(p, g, "Fatorial");
        g.gridy = 9; addRow(p, g, "n:", fFat);
        g.gridy = 10; addCenteredBtn(p, g, btn("Calcular n!", ACCENT2,
            () -> showStr(call(() -> calc.factorial(num(fFat))))));

        return wrap(p);
    }

    // ─── Aba Outros ──────────────────────────────────────────────────────────
    private JPanel buildMiscTab() {
        JPanel p = card();
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JTextField fPA = field("15");
        JTextField fPB = field("200");
        JTextField fMA = field("17");
        JTextField fMB = field("5");

        g.gridy = 0; addSection(p, g, "Porcentagem  (A% de B)");
        g.gridy = 1; addRow(p, g, "A (%):", fPA);
        g.gridy = 2; addRow(p, g, "B (total):", fPB);
        g.gridy = 3; addCenteredBtn(p, g, btn("Calcular %", ACCENT,
            () -> show(call(() -> calc.percentage(num(fPA), num(fPB))))));

        g.gridy = 4; addSection(p, g, "Módulo  (A mod B)");
        g.gridy = 5; addRow(p, g, "A:", fMA);
        g.gridy = 6; addRow(p, g, "B:", fMB);
        g.gridy = 7; addCenteredBtn(p, g, btn("Calcular Módulo", ACCENT,
            () -> show(call(() -> calc.mod(num(fMA), num(fMB))))));

        return wrap(p);
    }

    // ─── Aba Conversão ───────────────────────────────────────────────────────
    private JPanel buildConversionTab() {
        JPanel p = card();
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        JTextField fDec = field("255");
        JTextField fBin = field("11111111");
        JTextField fHex = field("FF");

        g.gridy = 0; addSection(p, g, "Decimal → Binário / Hex");
        g.gridy = 1; addRow(p, g, "Decimal:", fDec);

        JPanel btns1 = new JPanel(new GridLayout(1, 2, 8, 0));
        btns1.setOpaque(false);
        btns1.add(btn("→ Binário", ACCENT,
            () -> showStr(call(() -> calc.decimalToBinary(num(fDec))))));
        btns1.add(btn("→ Hex", ACCENT,
            () -> showStr(call(() -> calc.decimalToHex(num(fDec))))));

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(10, 0, 0, 0);
        p.add(btns1, g);

        g.gridy = 3; addSection(p, g, "Binário → Decimal");
        g.gridy = 4; addRow(p, g, "Binário:", fBin);
        g.gridy = 5; addCenteredBtn(p, g, btn("Converter", ACCENT2,
            () -> show(call(() -> (Number)(double) calc.binaryToDecimal(fBin.getText().trim())))));

        g.gridy = 6; addSection(p, g, "Hex → Decimal");
        g.gridy = 7; addRow(p, g, "Hex:", fHex);
        g.gridy = 8; addCenteredBtn(p, g, btn("Converter", ACCENT2,
            () -> show(call(() -> (Number)(double) calc.hexToDecimal(fHex.getText().trim())))));

        return wrap(p);
    }

    // ─── Painel Histórico ────────────────────────────────────────────────────
    private JPanel buildHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(PANEL);
        p.setBorder(new EmptyBorder(12, 4, 12, 12));

        JLabel title = new JLabel("Histórico");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_DIM);
        p.add(title, BorderLayout.NORTH);

        historyModel = new DefaultTableModel(new Object[]{"Operação"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(historyModel);
        table.setFont(FONT_MONO);
        table.setRowHeight(24);
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.getTableHeader().setBackground(PANEL);
        table.getTableHeader().setForeground(TEXT_DIM);
        table.setShowVerticalLines(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(CARD);
        p.add(scroll, BorderLayout.CENTER);

        JButton btnRefresh = btn("↻ Atualizar", ACCENT2, this::refreshHistory);
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(btnRefresh, BorderLayout.SOUTH);

        return p;
    }

    // ─── Status bar ──────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(15, 15, 24));
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(4, 12, 4, 12)
        ));
        lblStatus = new JLabel("Configure o host e clique em Conectar.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_DIM);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    // ─── Conexão RMI ─────────────────────────────────────────────────────────
    private void connect() {
        String host = fHost.getText().trim();
        String portStr = fPort.getText().trim();

        if (host.isEmpty()) {
            setStatus("Informe o host.", ERROR);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            setStatus("Porta inválida.", ERROR);
            return;
        }

        lblConnStatus.setText("● Conectando...");
        lblConnStatus.setForeground(WARNING);
        setStatus("Conectando a " + host + ":" + port + "...", WARNING);
        calc = null;

        final String url = "rmi://" + host + ":" + port + "/CalculatorService";

        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                calc = (Operations) Naming.lookup(url);
                return null;
            }
            protected void done() {
                try {
                    get();
                    lblConnStatus.setText("● Conectado");
                    lblConnStatus.setForeground(SUCCESS);
                    setStatus("Conectado a " + host + ":" + port + " ✓", SUCCESS);
                } catch (Exception e) {
                    lblConnStatus.setText("● Desconectado");
                    lblConnStatus.setForeground(ERROR);
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    setStatus("Erro: " + msg, ERROR);
                }
            }
        }.execute();
    }

    // ─── Helpers de UI ───────────────────────────────────────────────────────
    private JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        return p;
    }

    private JPanel wrap(JPanel content) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(PANEL);
        outer.setBorder(new EmptyBorder(8, 8, 8, 8));
        outer.add(content);
        return outer;
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField(placeholder, 10);
        f.setFont(FONT_MONO);
        f.setBackground(new Color(48, 48, 70));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JButton btn(String label, Color color, Runnable action) {
        JButton b = new JButton(label) {
            protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g.setColor(color.brighter());
                } else {
                    g.setColor(color);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g.dispose();
                super.paintComponent(g2);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 36));
        b.addActionListener(e -> action.run());
        return b;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, String label, JTextField field) {
        g.gridwidth = 1;
        g.gridx = 0; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_DIM);
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void addSection(JPanel p, GridBagConstraints g, String title) {
        g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(14, 8, 2, 8);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ACCENT);
        p.add(lbl, g);
        g.insets = new Insets(4, 8, 4, 8);
    }

    private void addCenteredBtn(JPanel p, GridBagConstraints g, JButton btn) {
        g.gridx = 0; g.gridwidth = 2;
        g.insets = new Insets(8, 40, 8, 40);
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(btn, g);
        g.insets = new Insets(4, 8, 4, 8);
    }

    // ─── Helpers de lógica ───────────────────────────────────────────────────
    @FunctionalInterface
    interface RemoteCall<T> { T call() throws Exception; }

    private <T> T call(RemoteCall<T> fn) {
        if (calc == null) {
            setStatus("Não conectado. Configure o host e clique em Conectar.", ERROR);
            return null;
        }
        try {
            return fn.call();
        } catch (Exception e) {
            setStatus("Erro remoto: " + e.getMessage(), ERROR);
            return null;
        }
    }

    private void show(Number result) {
        if (result == null) return;
        String txt = formatNumber(result);
        lblResult.setText(txt);
        setStatus("Resultado: " + txt, SUCCESS);
        refreshHistory();
    }

    private void showStr(String result) {
        if (result == null) return;
        lblResult.setText(result);
        setStatus("Resultado: " + result, SUCCESS);
        refreshHistory();
    }

    private String formatNumber(Number n) {
        double d = n.doubleValue();
        if (Double.isNaN(d)) return "Erro";
        if (d == Math.floor(d) && !Double.isInfinite(d) && Math.abs(d) < 1e15)
            return String.valueOf((long) d);
        return String.format("%.6g", d).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private Number num(JTextField f) {
        String s = f.getText().trim();
        if (s.contains(".")) return Double.parseDouble(s);
        return Long.parseLong(s);
    }

    private void refreshHistory() {
        if (calc == null) return;
        new SwingWorker<List<String>, Void>() {
            protected List<String> doInBackground() throws Exception {
                return calc.lastOperations();
            }
            protected void done() {
                try {
                    List<String> ops = get();
                    historyModel.setRowCount(0);
                    for (int i = ops.size() - 1; i >= 0; i--)
                        historyModel.addRow(new Object[]{ops.get(i)});
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private void setStatus(String msg, Color color) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText(msg);
            lblStatus.setForeground(color);
        });
    }

    // ─── Main ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI().setVisible(true));
    }
}
