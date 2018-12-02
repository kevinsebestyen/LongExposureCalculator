import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * 
 */

/**
 * 
 * Long Exposure Calculator
 * 
 * @author Kevin Sebestyen
 * @version 1.0
 * @date December 2018
 *
 */
@SuppressWarnings("serial")
public class LECalc extends JFrame {

    // Create JPanels that make up the application
    static JPanel finalMainToolArea;
    static JPanel finalHelpPage;
    static JPanel finalResourcesPage;
    static JPanel finalSettingsPage;
    static JPanel finalAboutPage;
    static JPanel finalNDFilterPage;

    // Create JLabels that need to be accessed in multiple functions
    static JLabel stopLabel;
    static JLabel status;
    static JLabel time;

    // Create JPanel for the Quick Guide
    static JPanel qgPanel;
    static JLabel qg1;
    static JLabel qg2;
    static JLabel qg3;
    static JLabel qg4;

    // Variables needed to hold different values
    static int selectedFilterFactorVal = 0;
    static double selectedFilterDensityVal = 0.0;
    static double baseShutterSpeed;
    static double calcShutterSpeed;
    static int stopValue;

    // String to be used by the status label
    static String result1 = "SHUTTER SPEED WITH ND " + selectedFilterDensityVal
            + " (" + stopValue + "-Stop) Filter:";

    // Holds filter factor values
    static int filterFactorVal[] = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024,
            2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288,
            1048576 };

    // Holds filter density values
    static Double filterDensityVal[] = { 0.3, 0.6, 0.9, 1.2, 1.5, 1.8, 2.1,
            2.4, 2.7, 3.0, 3.3, 3.6, 3.9, 4.2, 4.5, 4.8, 5.1, 5.4, 5.7, 6.0 };

    // Array that holds the stop values
    static Integer[] stopVals = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            14, 15, 16, 17, 18, 19, 20 };

    // ComboBoxModel for filter values
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static DefaultComboBoxModel model = new DefaultComboBoxModel(
            filterDensityVal);

    // Timer object for the countdown timer
    static Timer t = new Timer();

    // Background color used across the application
    static Color color = new Color(230, 234, 237);

    /**
     * Main class. Launches frame.
     * 
     * @param args
     */
    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LECalc frame = new LECalc();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
            }
        });
    }

    /**
     * Constructor.
     */
    public LECalc() {
        super("Long Exposure Calculator");

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 600, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        // Call functions that create different pages
        createHomePage();
        createHelpPage();
        createAboutPage();
        createSettingsPage();
        createResourcesPage();
        createNDFilterPage();

        // Create tabbed pane
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Home", finalMainToolArea);
        tabPane.addTab("About", finalAboutPage);
        tabPane.addTab("Resources", finalResourcesPage);
        tabPane.addTab("ND Filters", finalNDFilterPage);
        tabPane.addTab("Settings", finalSettingsPage);
        tabPane.addTab("Help", finalHelpPage);

        mainPanel.add(tabPane);

        // Border that sits at the bottom
        JPanel bottomBorder = new JPanel();
        bottomBorder.setPreferredSize(new Dimension(600, 45));

    }

    /**
     * Calculates the shutter speed based on:
     * 
     * @param s
     *            - stop value of filter being used
     * @param t
     *            - base shutter speed (time)
     * @return calculated shutter speed
     */
    static double calculator(int s, double t) {

        calcShutterSpeed = t * Math.pow(2, s);

        // Convert the calcuted shutter speed to a HH:MM:SS format
        secondsToMinutes(calcShutterSpeed);

        return calcShutterSpeed;
    }

    /**
     * Converts seconds to minutes. Will display different messages depending on
     * how many seconds are passed in.
     * 
     * @param seconds
     *            - calculated shutter speed
     * @return s - formated string containing the minutes needed
     */
    static String secondsToMinutes(double seconds) {
        int hours = (int) (seconds / 3600);
        double min = (seconds / 60) % 60;
        int sec = (int) (seconds % 60);
        String s = "";

        if (seconds == 0) {
            s = "0 hours 0 minutes 0 seconds";
        }
        else if (seconds < 1) {

            // Display the decimal as a fraction
            s = convertDecimalToFraction(seconds);
        }
        else if (seconds < 60.0) {

            // Display numbers to the ones decimal place
            int scale = (int) Math.pow(10, 1);
            double seco = (double) Math.round(seconds * scale) / scale;
            s = "" + seco + " seconds";
        }
        else if (min == 1) {
            s = "" + (int) min + " minute " + "" + (int) sec + " seconds";
        }
        else {
            s = "" + (int) hours + " hours " + "" + (int) min + " minutes "
                    + "" + (int) sec + " seconds";
        }

        return s;
    }

    /**
     * Displays shutter speeds less than one second as fractions.
     * 
     * Code citation:
     * 
     * @Title Converting decimal numbers to fractions
     * @Author Joni Salonen
     * @Date Unknown
     * @Availability http://jonisalonen.com/2012/converting-decimal-numbers-to-ratios/
     * 
     * @param x
     *            - shutter speed
     * @return string containing the fraction
     */
    static private String convertDecimalToFraction(double x) {

        if (x < 0) {
            return "-" + convertDecimalToFraction(-x);
        }

        double tolerance = 1.0E-6;
        double h1 = 1;
        double h2 = 0;
        double k1 = 0;
        double k2 = 1;
        double b = x;

        do {
            double a = Math.floor(b);
            double aux = h1;
            h1 = a * h1 + h2;
            h2 = aux;
            aux = k1;
            k1 = a * k1 + k2;
            k2 = aux;
            b = 1 / (b - a);
        } while (Math.abs(x - h1 / k1) > x * tolerance);

        return (int) h1 + "/" + (int) k1 + " second";
    }

    /**
     * Create main tool area for home page
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    static JPanel createHomePage() {

        // Add main JPanel which holds calculator
        finalMainToolArea = new JPanel();
        finalMainToolArea.setLayout(null);

        finalMainToolArea.setBackground(color);

        // Create label for filter density box
        JLabel filterDensity = new JLabel("Filter Level",
                SwingConstants.CENTER);
        filterDensity.setBorder(BorderFactory.createRaisedBevelBorder());
        filterDensity.setBounds(60, 60, 100, 25);
        finalMainToolArea.add(filterDensity);

        // ComboBox
        @SuppressWarnings({ "rawtypes" })
        JComboBox filterValComboBox = new JComboBox(model);
        filterValComboBox.setBounds(55, 90, 300, 50);

        // staus and stopLabel shows the user's selection
        status = new JLabel();
        status.setText("ND " + 0.3);
        status.setBounds(370, 100, 100, 25);
        status.setFont(new Font("SansSerif", Font.BOLD, 14));

        stopLabel = new JLabel();
        stopLabel.setText("1-Stop");
        stopLabel.setBounds(470, 100, 100, 25);
        stopLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

        // Create label for shutter speed box
        JLabel shutterSpeed = new JLabel("Base Shutter Speed",
                SwingConstants.CENTER);
        shutterSpeed.setBorder(BorderFactory.createRaisedBevelBorder());
        shutterSpeed.setBounds(60, 140, 140, 25);
        finalMainToolArea.add(shutterSpeed);

        // Create the text field to get base shutter speed
        JTextField userShutter = new JTextField("", 10);
        userShutter.setBounds(60, 180, 140, 25);

        // ActionListeners to get updated values.
        filterComboAction(filterValComboBox);
        shutterlistener(userShutter);

        // Summary of what the user chose
        JLabel j1 = new JLabel(result1);
        j1.setBounds(100, 200, 400, 100);
        j1.setFont(new Font("Avenir Next", Font.PLAIN, 16));
        j1.setHorizontalAlignment(SwingConstants.CENTER);
        finalMainToolArea.add(j1);

        // Displays calculated shutter speed
        time = new JLabel(secondsToMinutes(calcShutterSpeed));
        time.setBounds(100, 240, 400, 100);
        time.setFont(new Font("SansSerif", Font.PLAIN, 16));
        time.setHorizontalAlignment(SwingConstants.CENTER);
        finalMainToolArea.add(time);

        // Creates the button that calculates the shutter speed
        JButton calc = new JButton("Calculate");
        calc.setBounds(200, 182, 100, 20);
        finalMainToolArea.add(calc);
        calc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // Get corresponding stop value of ND filter level
                int s = 0;
                for (int i = 0; i < stopVals.length; i++) {
                    if (filterValComboBox.getSelectedIndex() == i) {
                        s = stopVals[i];
                    }
                }

                // Calculate
                calculator(s, baseShutterSpeed);

                result1 = "SHUTTER SPEED WITH ND "
                        + filterValComboBox.getSelectedItem() + " (" + s
                        + "-Stop) FILTER:";

                j1.setText(result1);
                time.setText(secondsToMinutes(calcShutterSpeed));
            }
        });

        // Timer button
        JButton timerButton = new JButton("START TIMER");
        timerButton.setBounds(220, 330, 150, 40);
        timerButton.setAlignmentX(SwingConstants.CENTER);

        // ActionListener for timerButton
        timer(timerButton);

        // Quick Guide icon
        JButton quickGuide = new JButton();
        ImageIcon icon1 = new ImageIcon(
                LECalc.class.getResource("quickGuideIcon.png"));
        Image img2 = icon1.getImage();
        Image newimg2 = img2.getScaledInstance(20, 20,
                java.awt.Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(newimg2);
        quickGuide.setIcon(newIcon);
        quickGuide.setBounds(530, 0, 50, 50);
        quickGuide.setBorderPainted(false);
        quickGuide.setBorder(null);
        quickGuide.setMargin(new Insets(0, 0, 0, 0));
        quickGuide.setContentAreaFilled(false);

        //
        // Quick Guide labels created here
        //
        qg1 = new JLabel(
                "<html><center>Select filter<br>level</center></html>",
                SwingConstants.CENTER);
        qg1.setBounds(340, 80, 120, 40);
        qg1.setBorder(
                BorderFactory.createDashedBorder(Color.black, 2, 5, 1, true));
        qg1.setOpaque(true);
        qg1.setVisible(false);

        qg2 = new JLabel(
                "<html><center>Input base<br>shutter speed</center></html>",
                SwingConstants.CENTER);
        qg2.setBounds(20, 195, 110, 50);
        qg2.setBorder(
                BorderFactory.createDashedBorder(Color.black, 2, 5, 1, true));
        qg2.setOpaque(true);
        qg2.setVisible(false);

        qg3 = new JLabel(
                "<html><center>Calculated shutter<br>speed</center></html>",
                SwingConstants.CENTER);
        qg3.setBounds(415, 270, 150, 50);
        qg3.setBorder(
                BorderFactory.createDashedBorder(Color.black, 2, 5, 1, true));
        qg3.setOpaque(true);
        qg3.setVisible(false);

        qg4 = new JLabel(
                "<html><center>Click here to start<br>the countdown!</center></html>",
                SwingConstants.CENTER);
        qg4.setBounds(70, 365, 150, 50);
        qg4.setBorder(
                BorderFactory.createDashedBorder(Color.black, 2, 5, 1, true));
        qg4.setOpaque(true);
        qg4.setVisible(false);

        finalMainToolArea.add(qg1);
        finalMainToolArea.add(qg2);
        finalMainToolArea.add(qg3);
        finalMainToolArea.add(qg4);

        // ActionListener for the quickGuide button
        quickGuideAction(quickGuide);

        finalMainToolArea.add(quickGuide);
        finalMainToolArea.add(timerButton);
        finalMainToolArea.add(filterValComboBox);
        finalMainToolArea.add(status);
        finalMainToolArea.add(stopLabel);
        finalMainToolArea.add(userShutter);

        finalMainToolArea.setVisible(true);

        return finalMainToolArea;
    }

    /**
     * Action Listener for the quickGuide button. Shows the user a quick guide
     * on using the application.
     * 
     * @param b
     *            - quickGuide button to be passed in.
     */
    static void quickGuideAction(JButton b) {
        b.addMouseListener(new MouseListener() {

            @Override
            public void mouseEntered(MouseEvent e) {
                qg1.setVisible(true);
                qg2.setVisible(true);
                qg3.setVisible(true);
                qg4.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                qg1.setVisible(false);
                qg2.setVisible(false);
                qg3.setVisible(false);
                qg4.setVisible(false);
            }

            // Don't need the following, but Eclipse cries when I delete them so
            // here they are
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * Action Listener for the shutter speed text field. Reads in the base
     * shutter speed provided by the user and stores it in baseShutterSpeed if
     * input is correct.
     * 
     * @param j
     *            - shutter speed text field.
     */
    static void shutterlistener(JTextField j) {
        j.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                try {
                    String s = j.getText();

                    // If input contains two "/" send message.
                    if (s.indexOf("/") != s.lastIndexOf("/")) {
                        JOptionPane.showMessageDialog(null,
                                "<html><center>- Invalid Entry -</center><br><center>Input must be in a form of a fraction or whole number.</center><br><center>Ex: 1/250 or 30</center</html");
                    }
                    // If they put in a fraction divide the numerator and
                    // denominator
                    else if (s.contains("/")) {
                        String[] a = s.split("/");

                        baseShutterSpeed = Double.parseDouble(a[0])
                                / Double.parseDouble(a[1]);

                        // Check for divide by zero
                        if (Double.parseDouble(a[1]) == 0) {
                            JOptionPane.showMessageDialog(null,
                                    "Cannot divide by zero.");
                            baseShutterSpeed = 0;
                        }
                    }
                    else if (!s.contains("/")) {
                        baseShutterSpeed = Double.parseDouble(s);
                    }
                }
                catch (Exception e1) {
                    JOptionPane.showMessageDialog(null,
                            "<html><center>- Invalid Entry -</center><br><center>Input must be in a form of a fraction or whole number.</center><br><center>Ex: 1/250 or 30</center</html");
                }
            }
        });
    }

    /**
     * ItemListener for the JComboBox.
     * 
     * @param f
     *            - filter value combo box
     */
    static void filterComboAction(JComboBox<Double> f) {
        f.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                // If item is a double, then they are using filter density
                if (f.getSelectedItem() instanceof Double) {
                    selectedFilterDensityVal = (double) f.getSelectedItem();
                    status.setText("ND " + selectedFilterDensityVal);
                }
                // If item is an integer, then they are using filter factor
                else if (f.getSelectedItem() instanceof Integer) {
                    selectedFilterDensityVal = (int) f.getSelectedItem();
                    status.setText("ND " + (int) selectedFilterDensityVal);
                }

                for (int i = 0; i < stopVals.length; i++) {
                    if (f.getSelectedIndex() == i) {
                        stopLabel.setText("" + stopVals[i] + "-Stop");
                    }
                }
            }
        });
    }

    /**
     * Creates the help page for the application. Creates information by loading
     * in an HTML file.
     * 
     * @return completed JPanel.
     */
    static JPanel createHelpPage() {

        finalHelpPage = new JPanel(new BorderLayout());
        finalHelpPage.setBackground(color);

        JEditorPane editorpane = new JEditorPane();
        editorpane.setContentType("text/html");

        String s = "<html><style type=\"text/css\" media=\"screen\">\n"
                + "    body {background-color: rgb(230, 234, 237);}\n"
                + "</style>\n"
                + "<p style=\"text-align: center;\"><span style=\"font-size: 22px; color: #808080;\"> <span style=\"font-family: verdana,geneva,sans-serif;\">taking the shot</span> </span>\n"
                + "</p>\n" + "<hr />\n" + "<p>&nbsp;</p>\n"
                + "<h4 style=\"text-align: center;\"><strong> <span style=\"font-family: symbol; font-size: 12px;\">Meter the scene.</span> </strong></h4>\n"
                + "<p style=\"text-align: left; margin-left: 30px;\"><span style=\"font-family: verdana,geneva,sans-serif;\">Compose your image, focus on your subject, and let the camera meter the scene without the ND filter. Take note of the shutter speed suggested by the camera.&nbsp;</span>\n"
                + "</p>\n" + "<p style=\"text-align: left;\">&nbsp;</p>\n"
                + "<h4 style=\"text-align: center;\"><span style=\"font-family: symbol; font-size: 12px;\"> <strong>Set the ND filter level.</strong> </span></h4>\n"
                + "<p style=\"margin-left: 30px;\"><span style=\"font-family: verdana,geneva,sans-serif;\">Set the filter notation (found in settings) that is used on the ND filter. Choose the filter density/f-stop reduction that you want to use.&nbsp;</span>\n"
                + "</p>\n" + "<p>&nbsp;</p>\n"
                + "<h4 style=\"text-align: center;\"><span style=\"font-family: symbol; font-size: 12px;\"> <strong>Set the base shutter speed.</strong> </span></h4>\n"
                + "<p style=\"margin-left: 30px;\"><span style=\"font-family: symbol;\">Dial in the shutter speed suggested by the camera when it&nbsp;metered&nbsp;the scene. Press&nbsp;<em>calculate</em>&nbsp;to get the adjusted shutter speed. </span>\n"
                + "</p>\n" + "<p>&nbsp;</p>\n"
                + "<h4 style=\"text-align: center;\"><span style=\"font-family: symbol; font-size: 12px;\"> <strong>Start the countdown.</strong> </span></h4>\n"
                + "<p style=\"margin-left: 30px;\"><span style=\"font-family: verdana,geneva,sans-serif;\">Start the timer and close the shutter when the time runs out.&nbsp;</span>\n"
                + "</p>\n"
                + "<p style=\"margin-left: 30px;\"><span style=\"font-family: verdana,geneva,sans-serif;\"><br /></span>\n"
                + "</p></html>";

        editorpane.setText(s);
        editorpane.setEditable(false);

        // Add scroll bar
        JScrollPane scroll = new JScrollPane(editorpane);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setVisible(true);
        scroll.setPreferredSize(new Dimension(10, 300));

        finalHelpPage.add(scroll);

        return finalHelpPage;
    }

    /**
     * Creates the about page for the application.
     * 
     * @return completed JPanel.
     */
    static JPanel createAboutPage() {

        // Not much to say here
        finalAboutPage = new JPanel();
        finalAboutPage.setBackground(color);

        finalAboutPage.setLayout(new BorderLayout());

        String s = "<html><center><b>Long Exposure Calculator</b>"
                + "</center><br><center>Version 1.0<br><br>Kevin Sebestyen</center><br><br><br>"
                + "<center><p><em><span>Long Exposure Calculator</span></em><span> is a tool for long exposure "
                + "photographers to use that will allow them to easily figure out the necessary shutter speed needed to create well "
                + "exposed images when using neutral density filters on lenses.</span></p></center></html>";

        JLabel l = new JLabel((s), SwingConstants.CENTER);
        l.setBackground(color);

        finalAboutPage.add(l, BorderLayout.CENTER);

        return finalAboutPage;
    }

    /**
     * Creates settings page for the application. Allows the user to choose the
     * notation they want to be displayed for the ND filters. Notation depends
     * on what brand ND filter they are using.
     * 
     * @return completed JPanel.
     */
    static JPanel createSettingsPage() {

        finalSettingsPage = new JPanel();
        finalSettingsPage.setLayout(null);
        finalSettingsPage.setBackground(color);

        JLabel settings = new JLabel("ND Filter Notation",
                SwingConstants.CENTER);
        settings.setBounds(83, 70, 130, 25);
        settings.setBorder(BorderFactory.createRaisedBevelBorder());

        // Two buttons for two different notations
        JButton filterFactor = new JButton("Filter Factor (ND1024)");
        filterFactor.setBounds(80, 100, 200, 35);
        JButton filterDensity = new JButton("Filter Density (ND 3.0)");
        filterDensity.setBounds(290, 100, 200, 35);

        // ActionListeners
        filterFactorSettingAction(filterFactor);
        filterDensitySettingAction(filterDensity);

        finalSettingsPage.add(settings);
        finalSettingsPage.add(filterFactor);
        finalSettingsPage.add(filterDensity);

        return finalSettingsPage;
    }

    /**
     * Creates the ND Filter tab. Users will be able to click three ND filters
     * to see in what situations they are useful and will see a sample image of
     * each filter.
     * 
     * @return completed ND Filter panel
     */
    static JPanel createNDFilterPage() {

        finalNDFilterPage = new JPanel();
        finalNDFilterPage.setLayout(new GridLayout(2, 1));
        finalNDFilterPage.setBackground(color);

        //
        // JPanel for sample images
        //
        JPanel j2 = new JPanel();
        j2.setBackground(Color.black);
        JLabel tsl1 = new JLabel();

        // Sample image for 3 stop
        ImageIcon ts1 = new ImageIcon(LECalc.class.getResource("3stop1.jpg"));
        Image img1 = ts1.getImage();
        Image newImage = img1.getScaledInstance(340, 227, Image.SCALE_DEFAULT);
        ImageIcon newIcon = new ImageIcon(newImage);

        // Sample image for 6 stop
        ImageIcon st1 = new ImageIcon(LECalc.class.getResource("6stop1.jpg"));
        Image img2 = st1.getImage();
        Image newImage2 = img2.getScaledInstance(340, 227,
                Image.SCALE_DEFAULT);
        ImageIcon newIcon2 = new ImageIcon(newImage2);

        // Sample image for 10 stop
        ImageIcon ts2 = new ImageIcon(LECalc.class.getResource("10stop1.jpg"));
        Image img3 = ts2.getImage();
        Image newImage3 = img3.getScaledInstance(340, 227,
                Image.SCALE_DEFAULT);
        ImageIcon newIcon3 = new ImageIcon(newImage3);

        j2.add(tsl1);
        j2.setVisible(false);

        //
        // JPanel for ND filter recommendations
        //
        JPanel j1 = new JPanel(new BorderLayout());
        j1.setBackground(color);

        JLabel label1 = new JLabel("\n\nwhich filter to use");
        label1.setFont(new Font("Avenir Next", Font.PLAIN, 18));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setPreferredSize(new Dimension(100, 50));

        // Label for ND filter info
        JLabel label2 = new JLabel(
                "Select a stop-value above to see more information");
        label2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setPreferredSize(new Dimension(300, 100));

        // ND Filter buttons and another panel for added complexity. Love it.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(color);

        JButton threeStop = new JButton("3-Stop");
        threeStop.setPreferredSize(new Dimension(100, 35));
        threeStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String s = "<html><p style=\"text-align: center;\">Good for</p>\n"
                        + "<hr />\n"
                        + "<ul style=\"list-style-type: circle;\">\n"
                        + "<li style=\"text-align: left;\">Photographing waves (won't lose texture of water)</li>\n"
                        + "<li style=\"text-align: left;\">Photographing events on bright days</li>\n"
                        + "<li style=\"text-align: left;\">Non-dramatic images</li>\n"
                        + "</ul></html>";
                label2.setText(s);
                j2.setVisible(true);
                tsl1.setIcon(newIcon);
            }
        });

        JButton sixStop = new JButton("6-Stop");
        sixStop.setPreferredSize(new Dimension(100, 35));
        sixStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String s = "<html><p style=\"text-align: center;\">Good for</p>\n"
                        + "<hr />\n"
                        + "<ul style=\"list-style-type: circle;\">\n"
                        + "<li style=\"text-align: left;\">Landscape photographs</li>\n"
                        + "<li style=\"text-align: left;\">Shooting waterfalls (softens the texture of the water)</li>\n"
                        + "<li style=\"text-align: left;\">Sunsets &amp; sunrises</li>\n"
                        + "</ul></html>";
                label2.setText(s);
                j2.setVisible(true);
                tsl1.setIcon(newIcon2);
            }
        });

        JButton tenStop = new JButton("10-Stop");
        tenStop.setPreferredSize(new Dimension(100, 35));
        tenStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String s = "<html><p style=\"text-align: center;\">Good for</p>\n"
                        + "<hr />\n"
                        + "<ul style=\"list-style-type: circle;\">\n"
                        + "<li style=\"text-align: left;\">Abstract photography</li>\n"
                        + "<li style=\"text-align: left;\">Very bright lighting conditions (mid day photography)</li>\n"
                        + "<li style=\"text-align: left;\">Photographing dramatic images</li>\n"
                        + "</ul></html>";
                label2.setText(s);
                j2.setVisible(true);
                tsl1.setIcon(newIcon3);
            }
        });

        buttonPanel.add(threeStop);
        buttonPanel.add(sixStop);
        buttonPanel.add(tenStop);

        j1.add(label1, BorderLayout.NORTH);
        j1.add(buttonPanel, BorderLayout.CENTER);
        j1.add(label2, BorderLayout.SOUTH);

        finalNDFilterPage.add(j1);
        finalNDFilterPage.add(j2);

        return finalNDFilterPage;
    }

    /**
     * Action Listener for the filterFactor button in settings. If pressed, the
     * JComboBox will be filled with values from the filterFactorVal array. This
     * displays the ND filter levels in the format ND1024.
     * 
     * @param b
     *            - button to be pressed.
     */
    static void filterFactorSettingAction(JButton b) {
        b.addActionListener(new ActionListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeAllElements();
                for (int i = 0; i < filterFactorVal.length; i++) {
                    model.addElement(filterFactorVal[i]);
                }
            }
        });
    }

    /**
     * Action Listener for the filterDensity button in settings. If pressed the
     * JComboBox will be filled with values from the filterDensityVal array.
     * This displays the ND filter levels in the format ND 0.3.
     * 
     * @param b
     *            - button to be pressed.
     */
    static void filterDensitySettingAction(JButton b) {
        b.addActionListener(new ActionListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeAllElements();
                for (int i = 0; i < filterDensityVal.length; i++) {
                    model.addElement(filterDensityVal[i]);
                }
            }
        });
    }

    /**
     * Creates the resources page.
     * 
     * @return completed JPanel.
     */
    static JPanel createResourcesPage() {
        finalResourcesPage = new JPanel(new BorderLayout());

        JEditorPane editorpane = new JEditorPane();
        editorpane.setEditorKit(
                JEditorPane.createEditorKitForContentType("text/html"));
       
        
        String s = "<style type=\"text/css\" media=\"screen\">\n"
                + "    body {background-color: rgb(230, 234, 237);}\n"
                + "</style>\n"
                + "<p style=\"text-align: center;\"><span style=\"color: #888888; font-family: verdana, geneva;\"><span style=\"caret-color: #888888; font-size: 22px;\">getting started</span></span>\n"
                + "</p>\n" + "<ul style=\"list-style-type: circle;\">\n"
                + "    <br>\n"
                + "    <li style=\"text-align: left;\"><span style=\"font-family: verdana, geneva;\">Neutral density (ND) filters help to reduce the amount of light entering into the camera. This creates the opportunity to use longer exposure times.</span>\n"
                + "    </li>\n" + "</ul>\n"
                + "<ul style=\"list-style-type: circle;\">\n"
                + "    <li><span style=\"font-family: verdana, geneva;\">Different ND filters can be used for different effects. The&nbsp;<em>ND Filters</em> tab shows more information on when to use different ND filters.<span></li>\n"
                + "</ul>\n" + "<p style=\"text-align: center;\">&nbsp;</p>\n"
                + "<hr />\n" + "<p style=\"text-align: center;\">&nbsp;</p>\n"
                + "<p style=\"text-align: center;\"><span style=\"caret-color: #888888; font-size: 22px; color: #888888; font-family: verdana, geneva;\">get connected</span>\n"
                + "        </p>\n"
                + "        <p style=\"text-align: center;\"><span style=\"caret-color: #888888; font-size: 12px; color: #333333; font-family: symbol;\">Use these links to access helpful photography websites</span>\n"
                + "        </p>\n"
                + "        <h5 style=\"margin-left: 30px; text-align: center;\"><span style=\"font-family: symbol;\"> <span style=\"caret-color: #888888; font-size: 12px;\"> <br /> </span> </span></h5>\n"
                + "        <h5 style=\"margin-left: 30px; text-align: center;\"><span style=\"font-family: symbol;\"> <span style=\"caret-color: #888888; font-size: 12px;\"> <a href=\"https://www.capturelandscapes.com\">CaptureLandscapes</a>&nbsp; &nbsp;learn skills on capturing landscapes </span> </span></h5>\n"
                + "        <h5 style=\"text-align: center;\"><span style=\"font-family: symbol; font-size: 12px;\"> <a href=\"https://fstoppers.com\">Fstoppers</a>&nbsp; articles and tutorials </span></h5>\n"
                + "        <h5 style=\"text-align: center;\"><span style=\"font-family: symbol; font-size: 12px;\"> <a href=\"https://www.dpreview.com\">Digital Photography Review</a>&nbsp; news, gear recomendations, and an active community </span></h5>\n"
                + "        <p>&nbsp;</p>\n" + "        <hr />\n"
                + "        <p style=\"text-align: center;\">&nbsp;&nbsp;</p>\n"
                + "        <p style=\"text-align: center;\"><span style=\"color: #888888; font-family: verdana, geneva; font-size: 22px;\">perfecting the shot</span>\n"
                + "        </p>\n"
                + "        <p style=\"margin-left: 30px;\"><span style=\"color: #333333; font-family: verdana, geneva;\"> <br /> </span>\n"
                + "        </p>\n"
                + "        <ul style=\"list-style-type: circle;\">\n"
                + "            <li><span style=\"font-family: verdana, geneva;\">Always use a tripod when doing long exposure photography. Any movement of the camera will create noise and distortion.</span>\n"
                + "            </li>\n" + "        </ul>\n"
                + "        <ul style=\"list-style-type: circle;\">\n"
                + "            <li><span style=\"font-family: verdana, geneva;\">Compose your image before using the ND filter.</span>\n"
                + "            </li>\n" + "        </ul>\n"
                + "        <ul style=\"list-style-type: circle;\">\n"
                + "            <li><span style=\"font-family: verdana, geneva;\">Set the lens to Manual Focus after attaching the ND filter and before pressing the shutter release. This will prevent the lens from changing the focus that you set before applying the ND filter.</span>\n"
                + "            </li>\n" + "        </ul>\n"
                + "        <ul style=\"list-style-type: circle;\">\n"
                + "            <li><span style=\"font-family: verdana, geneva;\">Use a remote shutter release when using Bulb mode to prevent shaking of the camera and tripod.</span>  <span style=\"font-family: verdana, geneva;\">&nbsp;</span>\n"
                + "            </li>\n" + "        </ul>\n"
                + "        <div><span style=\"font-family: verdana, geneva;\"><br /></span>\n"
                + "        </div>\n" + "        <div>\n"
                + "            <hr />\n" + "        </div>\n"
                + "        <div>&nbsp;</div>\n" + "        <div>\n"
                + "            <div style=\"text-align: center;\"><span><br /></span>\n"
                + "            </div>\n" + "        </div>";
        
        editorpane.setText(s);
        editorpane.setEditable(false);

        // Enabled the hyperlinks in the HTML code
        editorpane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    System.out.println(e.getURL());
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                        catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        catch (URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        // Add scroll bar
        JScrollPane scroll = new JScrollPane(editorpane);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(10, 300));

        finalResourcesPage.add(scroll, BorderLayout.CENTER);

        return finalResourcesPage;
    }

    /**
     * Starts the timer.
     * 
     * @param b
     *            - timer button
     */
    static void startTimer(JButton b) {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            double timeleft = calcShutterSpeed;

            @Override
            public void run() {
                String s = secondsToMinutes(timeleft);
                time.setText(s);
                timeleft--;
                if (timeleft < 0) {
                    t.cancel();
                    b.setText("START TIMER");
                    b.setForeground(Color.BLACK);
                    time.setText("0 hours 0 minutes 0 seconds");
                }
            }
        }, 0, 1000);
    }

    /**
     * Stops the timer.
     */
    static void stopTimer() {
        t.cancel();
        time.setText(secondsToMinutes(calcShutterSpeed));
    }

    /**
     * Creates the timer if "Start" is pressed.
     * 
     * @param b
     */
    static void timer(JButton b) {
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // If start button is pressed, change text to display stop
                if (b.getText() == "START TIMER") {
                    startTimer(b);
                    b.setText("STOP TIMER");
                    b.setForeground(Color.RED);
                }
                // If stop button is pressed, cancel timer and change text to
                // start
                else if (b.getText() == "STOP TIMER") {
                    stopTimer();
                    b.setText("START TIMER");
                    b.setForeground(Color.BLACK);
                }
            }
        });
    }
}
