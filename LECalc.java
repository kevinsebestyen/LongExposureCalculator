import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * 
 */

/**
 * @author Kevin Sebestyen
 *
 */
@SuppressWarnings("serial")
public class LECalc extends JFrame {

    static JPanel finalMainToolArea;
    static JPanel finalHelpPage;
    static JPanel finalResourcesPage;
    static JPanel finalSettingsPage;
    static JPanel finalAboutPage;

    static JLabel stopLabel;
    static JLabel status;
    static JLabel time;

    static int selectedFilterFactorVal = 0;
    static double selectedFilterDensityVal = 0.0;
    static double baseShutterSpeed;
    static double calcShutterSpeed;
    static int stopValue;

    static String result1 = "SHUTTER SPEED WITH ND " + selectedFilterDensityVal
            + "(" + stopValue + "-Stop) Filter:";

    // Holds filter factor values
    static int filterFactorVal[] = { 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024,
            2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288,
            1048576 };

    // Holds filter density values
    static Double filterDensityVal[] = { 0.3, 0.6, 0.9, 1.2, 1.5, 1.8, 2.1,
            2.4, 2.7, 3.0, 3.3, 3.6, 3.9, 4.2, 4.5, 4.8, 5.1, 5.4, 5.7, 6.0 };

    // HashMap that pairs filter value with its stop value
    static HashMap<Double, Integer> densityAndStop = new HashMap<Double, Integer>();
    static HashMap<Integer, Integer> factorAndStop = new HashMap<Integer, Integer>();

    static HashMap<String, Double> shutterSpeed = new HashMap<String, Double>();

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

    public LECalc() {
        super("Long Exposure Calculator");

        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 600, 500);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        createHomePage();
        createHelpPage();
        createAboutPage();
        createSettingsPage();
        createResourcesPage();

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Home", finalMainToolArea);
        tabPane.addTab("About", finalAboutPage);
        tabPane.addTab("Resources", finalResourcesPage);
        tabPane.addTab("Settings", finalSettingsPage);
        tabPane.addTab("Help", finalHelpPage);

        mainPanel.add(tabPane);

        // Border that sits at the bottom
        JPanel bottomBorder = new JPanel();
        bottomBorder.setPreferredSize(new Dimension(600, 45));

    }

    static void populateHashMaps() {

        int stop = 1;

        // Only including 20 filter factor/density values
        for (int i = 0; i < 20; i++) {
            densityAndStop.put(filterDensityVal[i], stop);
            factorAndStop.put(filterFactorVal[i], stop);
            stop++;
        }
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
    static double calculator(double s, double t) {

        for (Entry<Double, Integer> entry : densityAndStop.entrySet()) {
            if (entry.getKey().equals(s)) {
                s = entry.getValue();
                stopValue = entry.getValue();
                break;
            }
        }

        System.out.println("stop value: " + s + " base shutter speed: " + t);

        calcShutterSpeed = t * Math.pow(2, s);

        System.out.println("calc: " + calcShutterSpeed);

        secondsToMinutes(calcShutterSpeed);

        return calcShutterSpeed;
    }

    static String secondsToMinutes(double seconds) {
        double min = seconds / 60;
        int sec = (int) (seconds % 60);
        String s = "";

        if (seconds == 0) {
            s = "0 minutes 0 seconds";
        }
        else if(seconds <= 1) {
            s = seconds + " second";
        }
        else if (seconds < 60.0) {
            s = "" +  seconds + " seconds";
        }
        else if (min == 1) {
            s = "" + (int) min + " minute " + "" + (int) sec + " seconds";
        }
        else {
            s = "" + (int) min + " minutes " + "" + (int) sec + " seconds";
        }
        System.out.println("total time " + s);

        return s;
    }

    // public static String decimalToFraction(Double d)
    // {
    // StringBuffer result = new StringBuffer(" " + ((int) Math.floor(d)));
    // int whole = (int) ((d - Math.floor(d)) * 10000);
    // int gcd = gcd(whole, 10000);
    // result.append(" " + (whole / gcd) + "/" + 10000 / gcd + " ");
    // return result.toString();
    // }
    //
    // public static int gcd(int num, int denom) {
    // if (denom == 0) {
    // return num;
    // }
    // return gcd(denom, num % denom);
    // }

    /**
     * Create main tool area for home page
     * 
     * @return
     */
    static JPanel createHomePage() {

        populateHashMaps();

        // Add main JPanel which holds calculator
        finalMainToolArea = new JPanel();
        // finalMainToolArea.setBackground(Color.LIGHT_GRAY);
        finalMainToolArea.setLayout(null);
        // finalMainToolArea.setBorder(
        // BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black));

        // Create label for filter density box
        JLabel filterDensity = new JLabel("Filter Density",
                SwingConstants.CENTER);
        filterDensity.setBorder(
                BorderFactory.createMatteBorder(3, 3, 3, 3, Color.black));
        filterDensity.setBounds(60, 60, 100, 25);
        finalMainToolArea.add(filterDensity);

        // Create ComboBox for filter value selection
        JComboBox<Double> filterValComboBox = new JComboBox<>(
                filterDensityVal);
        filterValComboBox.setBounds(55, 90, 300, 50);

        status = new JLabel();
        status.setText("ND " + 0.3);
        status.setBounds(400, 100, 60, 25);
        status.setFont(new Font("SansSerif", Font.BOLD, 14));

        stopLabel = new JLabel();
        stopLabel.setText("1-Stop");
        stopLabel.setBounds(470, 100, 100, 25);
        stopLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));

        // Create label for shutter speed box
        JLabel shutterSpeed = new JLabel("Base Shutter Speed",
                SwingConstants.CENTER);
        shutterSpeed.setBorder(
                BorderFactory.createMatteBorder(3, 3, 3, 3, Color.black));
        shutterSpeed.setBounds(60, 140, 140, 25);
        finalMainToolArea.add(shutterSpeed);

        // Create the text field to get base shutter speed
        JTextField userShutter = new JTextField("", 10);
        userShutter.setBounds(60, 180, 140, 25);

        // ActionListeners to get updated values.
        filterComboAction(filterValComboBox);
        // shutterAction(userShutter);
        shutterlistener(userShutter);

        JLabel j1 = new JLabel(result1);
        j1.setBounds(100, 200, 400, 100);
        j1.setFont(new Font("Avenir Next", Font.PLAIN, 16));
        j1.setHorizontalAlignment(SwingConstants.CENTER);
        finalMainToolArea.add(j1);

        time = new JLabel(secondsToMinutes(calcShutterSpeed));
        time.setBounds(100, 240, 400, 100);
        time.setFont(new Font("SansSerif", Font.PLAIN, 16));
        time.setHorizontalAlignment(SwingConstants.CENTER);
        finalMainToolArea.add(time);

        JButton calc = new JButton("Calculate");
        calc.setBounds(200, 182, 80, 20);
        finalMainToolArea.add(calc);
        calc.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("" + filterValComboBox.getSelectedItem()
                        + " " + baseShutterSpeed);

                calculator((double) filterValComboBox.getSelectedItem(),
                        baseShutterSpeed);

                result1 = "SHUTTER SPEED WITH ND "
                        + filterValComboBox.getSelectedItem() + " ("
                        + stopValue + "-Stop) FILTER:";

                j1.setText(result1);
                time.setText(secondsToMinutes(calcShutterSpeed));

            }

        });

        JButton timerButton = new JButton("START TIMER");
        timerButton.setBounds(220, 330, 150, 25);
        timerButton.setAlignmentX(SwingConstants.CENTER);

        finalMainToolArea.add(timerButton);
        finalMainToolArea.add(filterValComboBox);
        finalMainToolArea.add(status);
        finalMainToolArea.add(stopLabel);
        finalMainToolArea.add(userShutter);

        finalMainToolArea.setVisible(true);

        return finalMainToolArea;
    }

    static void shutterlistener(JTextField j) {
        j.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                try {
                    String s = j.getText();

                    if (s.contains("/")) {
                        String[] a = s.split("/");
                        baseShutterSpeed = Double.parseDouble(a[0])
                                / Double.parseDouble(a[1]);
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

    static void filterComboAction(JComboBox<Double> f) {
        f.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedFilterDensityVal = (double) f.getSelectedItem();

                status.setText("ND " + selectedFilterDensityVal);

                // Set stopLabel to the corresponding stop value of filter level
                for (Entry<Double, Integer> entry : densityAndStop
                        .entrySet()) {
                    if (entry.getKey().equals(selectedFilterDensityVal)) {
                        int stopValue = entry.getValue();
                        stopLabel.setText("" + stopValue + "-Stop");
                    }
                }
            }
        });
    }

    static JPanel createHelpPage() {
        finalHelpPage = new JPanel(new BorderLayout());
        // JTextPane textPane = new JTextPane();

        JEditorPane editorpane = new JEditorPane();

        File file = new File("src/helpPageHTML.html");
        try {
            editorpane.setPage(file.toURI().toURL());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        editorpane.setEditable(false);

        JScrollPane scroll = new JScrollPane(editorpane);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        finalHelpPage.add(editorpane);

        return finalHelpPage;
    }

    static JPanel createAboutPage() {

        finalAboutPage = new JPanel(new GridBagLayout());

        String s = "<html><b>Long Exposure Calculator</b><br><center>Version 1.0<br><br>Kevin Sebestyen</center></html>";
        JLabel l = new JLabel(s);
        l.setBorder(BorderFactory.createRaisedSoftBevelBorder());

        finalAboutPage.add(l);

        return finalAboutPage;
    }

    static JPanel createSettingsPage() {
        finalSettingsPage = new JPanel();
        return finalSettingsPage;
    }

    static JPanel createResourcesPage() {
        finalResourcesPage = new JPanel();
        return finalResourcesPage;
    }

}
