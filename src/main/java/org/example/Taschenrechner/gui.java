package test;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class gui {

    private static final Color BG = new Color(18, 24, 38);
    private static final Color PANEL = new Color(29, 38, 56);
    private static final Color PANEL_LIGHT = new Color(39, 50, 72);
    private static final Color ACCENT = new Color(76, 145, 255);
    private static final Color TEXT = new Color(238, 243, 250);
    private static final Color MUTED = new Color(159, 174, 196);

    private JFrame fenster;
    private JComboBox<String> modusAuswahl;
    private JPanel cards;
    private CardLayout cardLayout;

    // Grundrechner
    private JTextField display;
    private JLabel rechenAnzeige;
    private double ersterWert = 0;
    private String operator = "";
    private boolean neueEingabe = true;

    // Polynomrechner
    private JComboBox<String> gradAuswahl;
    private JTextField feldA, feldB, feldC, feldD;
    private JLabel labelA, labelB, labelC, labelD, formelAnzeige;
    private JTextArea polyErgebnis;
    private GraphPanel graph;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new gui().start());
    }

    private void start() {
        fenster = new JFrame("Rechner – Grundrechenarten & Polynome");
        fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenster.setMinimumSize(new Dimension(900, 620));
        fenster.setSize(1120, 740);
        fenster.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 22, 22, 22));

        JPanel header = new JPanel(new BorderLayout(15, 8));
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("RECHNER");
        title.setForeground(TEXT);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Grundrechenarten und Polynomfunktionen");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);

        JPanel modeBox = new JPanel(new BorderLayout(8, 5));
        modeBox.setOpaque(false);
        JLabel modeLabel = new JLabel("BERECHNUNGSART");
        modeLabel.setForeground(MUTED);
        modeLabel.setFont(new Font("Arial", Font.BOLD, 10));
        modusAuswahl = new JComboBox<>(new String[]{"Grundrechenarten", "Polynom-Rechner"});
        styleCombo(modusAuswahl);
        modeBox.add(modeLabel, BorderLayout.NORTH);
        modeBox.add(modusAuswahl, BorderLayout.CENTER);
        modeBox.setPreferredSize(new Dimension(230, 55));

        header.add(titleBox, BorderLayout.WEST);
        header.add(modeBox, BorderLayout.EAST);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);
        cards.add(buildBasicCalculator(), "basic");
        cards.add(buildPolynomialCalculator(), "poly");

        modusAuswahl.addActionListener(e -> {
            if (modusAuswahl.getSelectedIndex() == 0) {
                cardLayout.show(cards, "basic");
            } else {
                cardLayout.show(cards, "poly");
            }
        });

        root.add(header, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        fenster.setContentPane(root);
        fenster.setVisible(true);
    }

    // -------------------- GRUNDRECHENARTEN --------------------

    private JPanel buildBasicCalculator() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setOpaque(false);

        JPanel calculator = cardPanel();
        calculator.setLayout(new BorderLayout(12, 14));
        calculator.setPreferredSize(new Dimension(430, 560));

        JPanel displayPanel = new JPanel(new BorderLayout(4, 8));
        displayPanel.setOpaque(false);
        rechenAnzeige = new JLabel(" ");
        rechenAnzeige.setForeground(MUTED);
        rechenAnzeige.setHorizontalAlignment(SwingConstants.RIGHT);
        rechenAnzeige.setFont(new Font("Arial", Font.PLAIN, 14));
        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 38));
        display.setForeground(TEXT);
        display.setBackground(PANEL_LIGHT);
        display.setCaretColor(TEXT);
        display.setBorder(new EmptyBorder(15, 12, 15, 12));
        displayPanel.add(rechenAnzeige, BorderLayout.NORTH);
        displayPanel.add(display, BorderLayout.CENTER);

        JPanel keys = new JPanel(new GridLayout(5, 4, 9, 9));
        keys.setOpaque(false);
        String[][] buttons = {
                {"AC", "⌫", "÷", "×"},
                {"7", "8", "9", "−"},
                {"4", "5", "6", "+"},
                {"1", "2", "3", "="},
                {"±", "0", ",", "="}
        };
        // 4x4 Windows-like layout; equals spans the last two rows.
        keys.setLayout(new GridBagLayout());
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                String caption = buttons[row][col];
                if (row == 4 && col == 3) continue;
                if (row == 3 && col == 3) {
                    JButton b = makeCalcButton("=", true);
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = col; gbc.gridy = row; gbc.gridheight = 2;
                    gbc.weightx = 1; gbc.weighty = 1;
                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.insets = new Insets(4, 4, 4, 4);
                    keys.add(b, gbc);
                    continue;
                }
                JButton b = makeCalcButton(caption, isOperatorButton(caption));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = col; gbc.gridy = row;
                gbc.weightx = 1; gbc.weighty = 1;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.insets = new Insets(4, 4, 4, 4);
                keys.add(b, gbc);
            }
        }

        calculator.add(displayPanel, BorderLayout.NORTH);
        calculator.add(keys, BorderLayout.CENTER);
        JLabel hint = new JLabel("Tasten anklicken oder Zahlen über die Tastatur eingeben");
        hint.setForeground(MUTED);
        hint.setFont(new Font("Arial", Font.PLAIN, 11));
        calculator.add(hint, BorderLayout.SOUTH);
        outer.add(calculator);

        // Keyboard support
        fenster.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals");
        fenster.getRootPane().getActionMap().put("equals", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { calculatorPress("="); }
        });
        return outer;
    }

    private JButton makeCalcButton(String caption, boolean special) {
        JButton button = new JButton(caption);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(TEXT);
        button.setBackground(special ? ACCENT : PANEL_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> calculatorPress(caption));
        return button;
    }

    private boolean isOperatorButton(String s) {
        return s.equals("÷") || s.equals("×") || s.equals("−") || s.equals("+") || s.equals("=");
    }

    private void calculatorPress(String key) {
        String current = display.getText().replace(',', '.');
        if (key.matches("[0-9]")) {
            if (neueEingabe || display.getText().equals("0")) {
                display.setText(key);
                neueEingabe = false;
            } else {
                display.setText(display.getText() + key);
            }
        } else if (key.equals(",")) {
            if (neueEingabe) {
                display.setText("0,");
                neueEingabe = false;
            } else if (!display.getText().contains(",")) {
                display.setText(display.getText() + ",");
            }
        } else if (key.equals("AC")) {
            display.setText("0");
            rechenAnzeige.setText(" ");
            ersterWert = 0;
            operator = "";
            neueEingabe = true;
        } else if (key.equals("⌫")) {
            if (neueEingabe) return;
            String s = display.getText();
            if (s.length() <= 1 || (s.length() == 2 && s.startsWith("-"))) {
                display.setText("0");
                neueEingabe = true;
            } else {
                display.setText(s.substring(0, s.length() - 1));
            }
        } else if (key.equals("±")) {
            if (!display.getText().equals("0")) {
                if (display.getText().startsWith("-")) {
                    display.setText(display.getText().substring(1));
                } else {
                    display.setText("-" + display.getText());
                }
            }
        } else if (key.equals("+") || key.equals("−") || key.equals("×") || key.equals("÷")) {
            double value = parseDisplay();
            if (!operator.isEmpty() && !neueEingabe) {
                value = calculate(ersterWert, value, operator);
                display.setText(format(value));
            }
            ersterWert = value;
            operator = key;
            rechenAnzeige.setText(format(ersterWert) + " " + operator);
            neueEingabe = true;
        } else if (key.equals("=")) {
            if (operator.isEmpty()) return;
            double second = parseDisplay();
            try {
                double result = calculate(ersterWert, second, operator);
                rechenAnzeige.setText(format(ersterWert) + " " + operator + " " + format(second) + " =");
                display.setText(format(result));
                ersterWert = result;
                operator = "";
                neueEingabe = true;
            } catch (ArithmeticException ex) {
                display.setText("Fehler");
                rechenAnzeige.setText("Division durch 0 ist nicht möglich");
                operator = "";
                neueEingabe = true;
            }
        }
    }

    private double parseDisplay() {
        return Double.parseDouble(display.getText().replace(',', '.'));
    }

    private double calculate(double x, double y, String op) {
        switch (op) {
            case "+": return x + y;
            case "−": return x - y;
            case "×": return x * y;
            case "÷":
                if (y == 0) throw new ArithmeticException();
                return x / y;
            default: return y;
        }
    }

    // -------------------- POLYNOMRECHNER --------------------

    private JPanel buildPolynomialCalculator() {
        JPanel container = new JPanel(new BorderLayout(14, 0));
        container.setOpaque(false);

        JPanel left = cardPanel();
        left.setLayout(new BorderLayout(10, 12));
        left.setPreferredSize(new Dimension(330, 500));

        JLabel heading = new JLabel("Polynom eingeben");
        heading.setForeground(TEXT);
        heading.setFont(new Font("Arial", Font.BOLD, 18));

        gradAuswahl = new JComboBox<>(new String[]{"Grad 1", "Grad 2", "Grad 3"});
        styleCombo(gradAuswahl);

        JPanel top = new JPanel(new GridLayout(2, 1, 6, 6));
        top.setOpaque(false);
        top.add(heading);
        top.add(gradAuswahl);

        JPanel fields = new JPanel(new GridLayout(4, 2, 8, 10));
        fields.setOpaque(false);
        labelA = new JLabel("a (x):");
        labelB = new JLabel("b:");
        labelC = new JLabel("c:");
        labelD = new JLabel("d:");
        styleLabel(labelA); styleLabel(labelB); styleLabel(labelC); styleLabel(labelD);
        feldA = makeInput("1");
        feldB = makeInput("-6");
        feldC = makeInput("11");
        feldD = makeInput("-6");
        fields.add(labelA); fields.add(feldA);
        fields.add(labelB); fields.add(feldB);
        fields.add(labelC); fields.add(feldC);
        fields.add(labelD); fields.add(feldD);

        formelAnzeige = new JLabel("f(x) = ax + b");
        formelAnzeige.setForeground(MUTED);
        formelAnzeige.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton drawButton = new JButton("Berechnen und Graph zeichnen");
        drawButton.setBackground(ACCENT);
        drawButton.setForeground(Color.WHITE);
        drawButton.setFont(new Font("Arial", Font.BOLD, 13));
        drawButton.setFocusPainted(false);
        drawButton.addActionListener(e -> calculatePolynomial());

        polyErgebnis = new JTextArea(8, 22);
        polyErgebnis.setEditable(false);
        polyErgebnis.setLineWrap(true);
        polyErgebnis.setWrapStyleWord(true);
        polyErgebnis.setFont(new Font("Arial", Font.PLAIN, 13));
        polyErgebnis.setForeground(TEXT);
        polyErgebnis.setBackground(PANEL_LIGHT);
        polyErgebnis.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel resultBox = new JPanel(new BorderLayout(5, 5));
        resultBox.setOpaque(false);
        JLabel resultTitle = new JLabel("Ergebnis / Nullstellen");
        resultTitle.setForeground(MUTED);
        resultTitle.setFont(new Font("Arial", Font.BOLD, 11));
        resultBox.add(resultTitle, BorderLayout.NORTH);
        resultBox.add(new JScrollPane(polyErgebnis), BorderLayout.CENTER);

        JPanel north = new JPanel(new BorderLayout(5, 5));
        north.setOpaque(false);
        north.add(top, BorderLayout.NORTH);
        north.add(formelAnzeige, BorderLayout.SOUTH);

        JPanel middle = new JPanel(new BorderLayout(5, 10));
        middle.setOpaque(false);
        middle.add(fields, BorderLayout.NORTH);
        middle.add(drawButton, BorderLayout.CENTER);

        left.add(north, BorderLayout.NORTH);
        left.add(middle, BorderLayout.CENTER);
        left.add(resultBox, BorderLayout.SOUTH);

        graph = new GraphPanel();
        graph.setPreferredSize(new Dimension(600, 500));

        JPanel right = cardPanel();
        right.setLayout(new BorderLayout(8, 8));
        JLabel graphTitle = new JLabel("Funktionsgraph");
        graphTitle.setForeground(TEXT);
        graphTitle.setFont(new Font("Arial", Font.BOLD, 18));
        right.add(graphTitle, BorderLayout.NORTH);
        right.add(graph, BorderLayout.CENTER);

        container.add(left, BorderLayout.WEST);
        container.add(right, BorderLayout.CENTER);

        gradAuswahl.addActionListener(e -> updatePolynomialFields());
        updatePolynomialFields();
        return container;
    }

    private void updatePolynomialFields() {
        int degree = gradAuswahl.getSelectedIndex() + 1;
        labelA.setText(degree == 1 ? "a (x):" : degree == 2 ? "a (x²):" : "a (x³):");
        labelB.setText(degree == 1 ? "b (Konstante):" : degree == 2 ? "b (x):" : "b (x²):");
        labelC.setText(degree == 2 ? "c (Konstante):" : "c (x):");
        labelD.setText("d (Konstante):");
        labelC.setVisible(degree >= 2);
        feldC.setVisible(degree >= 2);
        labelD.setVisible(degree == 3);
        feldD.setVisible(degree == 3);
        formelAnzeige.setText(degree == 1 ? "f(x) = ax + b" : degree == 2 ? "f(x) = ax² + bx + c" : "f(x) = ax³ + bx² + cx + d");
        polyErgebnis.setText("");
        graph.clearGraph();
    }

    private void calculatePolynomial() {
        try {
            int degree = gradAuswahl.getSelectedIndex() + 1;
            double a = readNumber(feldA);
            double b = readNumber(feldB);
            double c = degree >= 2 ? readNumber(feldC) : 0;
            double d = degree == 3 ? readNumber(feldD) : 0;
            if (a == 0) {
                polyErgebnis.setText("Der Koeffizient a darf nicht 0 sein.");
                return;
            }

            ArrayList<Double> roots = new ArrayList<>();
            StringBuilder output = new StringBuilder();
            output.append("Funktion:\n").append(polynomialString(degree, a, b, c, d)).append("\n\n");

            if (degree == 1) {
                roots.add(-b / a);
            } else if (degree == 2) {
                double disc = b * b - 4 * a * c;
                double eps = 1e-10 * Math.max(1, Math.abs(b * b) + Math.abs(4 * a * c));
                if (disc > eps) {
                    roots.add((-b - Math.sqrt(disc)) / (2 * a));
                    roots.add((-b + Math.sqrt(disc)) / (2 * a));
                } else if (Math.abs(disc) <= eps) {
                    roots.add(-b / (2 * a));
                }
            } else {
                roots.addAll(solveCubic(a, b, c, d));
            }

            if (roots.isEmpty()) {
                output.append("Keine reellen Nullstellen gefunden.");
            } else {
                Collections.sort(roots);
                output.append("Reelle Nullstellen:\n");
                for (int i = 0; i < roots.size(); i++) {
                    output.append("x").append(i + 1).append(" = ").append(format(roots.get(i))).append("\n");
                }
            }

            polyErgebnis.setText(output.toString());
            graph.setFunction(degree, a, b, c, d, roots);
        } catch (NumberFormatException ex) {
            polyErgebnis.setText("Bitte trage in alle benötigten Felder gültige Zahlen ein.");
        }
    }

    private ArrayList<Double> solveCubic(double a, double b, double c, double d) {
        ArrayList<Double> roots = new ArrayList<>();
        double A = b / a;
        double B = c / a;
        double C = d / a;
        double p = B - A * A / 3.0;
        double q = 2.0 * A * A * A / 27.0 - A * B / 3.0 + C;
        double delta = q * q / 4.0 + p * p * p / 27.0;
        double eps = 1e-12 * Math.max(1, Math.abs(q * q / 4.0) + Math.abs(p * p * p / 27.0));
        double shift = A / 3.0;

        if (delta > eps) {
            double u = Math.cbrt(-q / 2.0 + Math.sqrt(delta));
            double v = Math.cbrt(-q / 2.0 - Math.sqrt(delta));
            roots.add(u + v - shift);
        } else if (delta < -eps) {
            double argument = (3.0 * q / (2.0 * p)) * Math.sqrt(-3.0 / p);
            argument = Math.max(-1.0, Math.min(1.0, argument));
            double phi = Math.acos(argument) / 3.0;
            for (int k = 0; k < 3; k++) {
                roots.add(2.0 * Math.sqrt(-p / 3.0) * Math.cos(phi - 2.0 * Math.PI * k / 3.0) - shift);
            }
        } else {
            if (Math.abs(p) < 1e-12 && Math.abs(q) < 1e-12) {
                roots.add(-shift);
            } else {
                double u = Math.cbrt(-q / 2.0);
                roots.add(2.0 * u - shift);
                roots.add(-u - shift); // double root
            }
        }
        return roots;
    }

    private String polynomialString(int degree, double a, double b, double c, double d) {
        if (degree == 1) return format(a) + "x + (" + format(b) + ")";
        if (degree == 2) return format(a) + "x² + (" + format(b) + ")x + (" + format(c) + ")";
        return format(a) + "x³ + (" + format(b) + ")x² + (" + format(c) + ")x + (" + format(d) + ")";
    }

    private double readNumber(JTextField field) {
        return Double.parseDouble(field.getText().trim().replace(',', '.'));
    }

    // -------------------- GRAPH ZEICHNEN --------------------

    private class GraphPanel extends JPanel {
        private int degree;
        private double a, b, c, d;
        private ArrayList<Double> roots = new ArrayList<>();
        private boolean hasFunction = false;

        GraphPanel() {
            setBackground(new Color(246, 249, 253));
            setBorder(BorderFactory.createLineBorder(new Color(218, 226, 238)));
        }

        void clearGraph() {
            hasFunction = false;
            roots.clear();
            repaint();
        }

        void setFunction(int degree, double a, double b, double c, double d, ArrayList<Double> roots) {
            this.degree = degree;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.roots = new ArrayList<>(roots);
            this.hasFunction = true;
            repaint();
        }

        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int left = 58, right = 20, top = 20, bottom = 42;
            int plotW = w - left - right;
            int plotH = h - top - bottom;

            g.setColor(new Color(246, 249, 253));
            g.fillRect(0, 0, w, h);

            if (!hasFunction || plotW <= 0 || plotH <= 0) {
                g.setColor(new Color(110, 124, 145));
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                drawCentered(g, "Wähle ein Polynom und klicke auf Berechnen", w, h);
                g.dispose();
                return;
            }

            double xMin = -5, xMax = 5;
            if (!roots.isEmpty()) {
                double minRoot = Collections.min(roots);
                double maxRoot = Collections.max(roots);
                double span = Math.max(1, maxRoot - minRoot);
                double margin = Math.max(2, span * 0.30);
                xMin = Math.min(0, minRoot - margin);
                xMax = Math.max(0, maxRoot + margin);
                if (xMax - xMin < 4) { xMin -= 2; xMax += 2; }
            }

            double yMin = 0, yMax = 0;
            for (int i = 0; i <= 500; i++) {
                double x = xMin + (xMax - xMin) * i / 500.0;
                double y = eval(x);
                if (Double.isFinite(y)) {
                    yMin = Math.min(yMin, y);
                    yMax = Math.max(yMax, y);
                }
            }
            if (Math.abs(yMax - yMin) < 1e-9) { yMin -= 2; yMax += 2; }
            double yPadding = (yMax - yMin) * 0.12;
            if (yPadding == 0) yPadding = 1;
            yMin -= yPadding;
            yMax += yPadding;

            double xStep = niceStep((xMax - xMin) / 8.0);
            double yStep = niceStep((yMax - yMin) / 8.0);

            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.setStroke(new BasicStroke(1f));
            for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
                int px = mapX(x, xMin, xMax, left, plotW);
                g.setColor(new Color(225, 231, 239));
                g.drawLine(px, top, px, top + plotH);
                g.setColor(new Color(90, 105, 125));
                String label = formatAxis(x);
                g.drawString(label, px - g.getFontMetrics().stringWidth(label) / 2, top + plotH + 17);
            }
            for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
                int py = mapY(y, yMin, yMax, top, plotH);
                g.setColor(new Color(225, 231, 239));
                g.drawLine(left, py, left + plotW, py);
                g.setColor(new Color(90, 105, 125));
                String label = formatAxis(y);
                g.drawString(label, left - 8 - g.getFontMetrics().stringWidth(label), py + 4);
            }

            // Coordinate axes, if visible in the current window.
            g.setColor(new Color(72, 87, 108));
            g.setStroke(new BasicStroke(1.7f));
            if (xMin <= 0 && xMax >= 0) {
                int x0 = mapX(0, xMin, xMax, left, plotW);
                g.drawLine(x0, top, x0, top + plotH);
                g.drawString("y", x0 + 7, top + 12);
            }
            if (yMin <= 0 && yMax >= 0) {
                int y0 = mapY(0, yMin, yMax, top, plotH);
                g.drawLine(left, y0, left + plotW, y0);
                g.drawString("x", left + plotW - 12, y0 - 7);
            }

            // Function curve.
            Path2D path = new Path2D.Double();
            boolean started = false;
            for (int i = 0; i <= 900; i++) {
                double x = xMin + (xMax - xMin) * i / 900.0;
                double y = eval(x);
                if (!Double.isFinite(y) || y < yMin - (yMax - yMin) || y > yMax + (yMax - yMin)) {
                    started = false;
                    continue;
                }
                int px = mapX(x, xMin, xMax, left, plotW);
                int py = mapY(y, yMin, yMax, top, plotH);
                if (!started) { path.moveTo(px, py); started = true; }
                else path.lineTo(px, py);
            }
            g.setColor(new Color(36, 112, 230));
            g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(path);

            // Mark real roots on the x-axis.
            if (yMin <= 0 && yMax >= 0) {
                int y0 = mapY(0, yMin, yMax, top, plotH);
                g.setColor(new Color(225, 83, 83));
                for (double root : roots) {
                    int px = mapX(root, xMin, xMax, left, plotW);
                    g.fillOval(px - 5, y0 - 5, 10, 10);
                }
            }

            g.setColor(new Color(70, 85, 105));
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("f(x)", left + 5, top + 12);
            g.dispose();
        }

        private double eval(double x) {
            if (degree == 1) return a * x + b;
            if (degree == 2) return (a * x + b) * x + c;
            return ((a * x + b) * x + c) * x + d;
        }

        private int mapX(double x, double min, double max, int left, int width) {
            return left + (int) Math.round((x - min) / (max - min) * width);
        }

        private int mapY(double y, double min, double max, int top, int height) {
            return top + height - (int) Math.round((y - min) / (max - min) * height);
        }

        private double niceStep(double raw) {
            if (raw <= 0 || !Double.isFinite(raw)) return 1;
            double power = Math.pow(10, Math.floor(Math.log10(raw)));
            double fraction = raw / power;
            double nice;
            if (fraction < 1.5) nice = 1;
            else if (fraction < 3.5) nice = 2;
            else if (fraction < 7.5) nice = 5;
            else nice = 10;
            return nice * power;
        }

        private String formatAxis(double value) {
            if (Math.abs(value) < 0.0000001) value = 0;
            DecimalFormat df = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.GERMANY));
            return df.format(value);
        }

        private void drawCentered(Graphics2D g, String text, int w, int h) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2);
        }
    }

    // -------------------- HILFSMETHODEN / DESIGN --------------------

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(49, 63, 86)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        return panel;
    }

    private JTextField makeInput(String value) {
        JTextField field = new JTextField(value);
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setForeground(TEXT);
        field.setBackground(PANEL_LIGHT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(64, 79, 104)),
                new EmptyBorder(7, 9, 7, 9)
        ));
        return field;
    }

    private void styleLabel(JLabel label) {
        label.setForeground(MUTED);
        label.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setForeground(TEXT);
        combo.setBackground(PANEL_LIGHT);
        combo.setFocusable(false);
    }

    private String format(double value) {
        if (Math.abs(value) < 0.0000000001) value = 0;
        if (Math.abs(value) > 1e12 || (value != 0 && Math.abs(value) < 0.0001)) {
            return String.format(Locale.GERMANY, "%.4e", value);
        }
        DecimalFormat df = new DecimalFormat("0.########", DecimalFormatSymbols.getInstance(Locale.GERMANY));
        return df.format(value);
    }
}
