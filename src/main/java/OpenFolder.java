import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class OpenFolder {

    private static String defaultDirectory;

    public static void main(String[] args) {
        // Load default directory from properties file
        loadDefaultDirectory();

        // Create the JFrame (the window)
        JFrame frame = new JFrame("Search and Open Folder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new FlowLayout());

        // Create label and text field for folder name input
        JLabel label = new JLabel("Search for:");
        JTextField textField = new JTextField(25);

        // Create the search button
        JButton searchButton = new JButton("Search");

        // Create a list to display search results
        DefaultListModel<String> folderListModel = new DefaultListModel<>();
        JList<String> folderList = new JList<>(folderListModel);
        folderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(folderList);
        scrollPane.setPreferredSize(new Dimension(450, 80));

        // Create a panel to hold the buttons
        JPanel folderPanel = new JPanel();
        folderPanel.setLayout(new FlowLayout());

        // Load the copy icon image with fallback
        JButton copyButton = createButtonWithIcon("/META-INF/copyButton.png", "Copy selected folder name to clipboard");
        copyButton.addActionListener(e -> copySelectedFolderName(folderList, frame));

        // Load the "Open Folder" button with the scaled icon and fallback
        JButton openFolderButton = createButtonWithIcon("/META-INF/openButton.png", "Open selected folder");
        openFolderButton.addActionListener(e -> openSelectedFolder(folderList, frame));

        // Add action listener for the search button
        searchButton.addActionListener(e -> performSearch(textField.getText().toLowerCase(), folderListModel, frame));

        // Add key listener to the text field to listen for Enter key press
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch(textField.getText().toLowerCase(), folderListModel, frame);
                }
            }
        });

        // Add key listener to the list to listen for Enter key press
        folderList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    openSelectedFolder(folderList, frame);
                }
            }
        });

        // Add mouse listener to open the selected folder on double click
        folderList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openSelectedFolder(folderList, frame);
                }
            }
        });

        // Create a menu bar with an option to set default directory
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        JMenuItem setDirectoryItem = new JMenuItem("Set Default Directory");
        setDirectoryItem.addActionListener(e -> setDefaultDirectory(frame));
        menu.add(setDirectoryItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Add components to the folder panel
        folderPanel.add(new JLabel("Selected Folder: "));
        folderPanel.add(copyButton);
        folderPanel.add(openFolderButton);

        // Add components to the frame
        frame.add(label);
        frame.add(textField);
        frame.add(searchButton);
        frame.add(scrollPane);
        frame.add(folderPanel);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Method to create a button with an icon and fallback if the icon isn't found
    private static JButton createButtonWithIcon(String iconPath, String tooltip) {
        ImageIcon icon;
        try {
            // Load icon from the classpath
            icon = new ImageIcon(OpenFolder.class.getResource(iconPath));
            if (icon.getImage() == null) {
                throw new NullPointerException("Image not found");
            }
        } catch (Exception e) {
            // Fallback to a simple button with text if the image is missing
            System.err.println("Icon not found: " + iconPath + ". Using fallback button.");
            JButton fallbackButton = new JButton("Action");
            fallbackButton.setToolTipText(tooltip);
            return fallbackButton;
        }

        // Create the button with the scaled icon
        int width = 32;
        int height = 32;
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setPreferredSize(new Dimension(width, height));
        button.setToolTipText(tooltip);
        return button;
    }

    // Method to load default directory from a properties file
    private static void loadDefaultDirectory() {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            defaultDirectory = prop.getProperty("defaultDirectory", "C:\\");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Method to set default directory and save it to properties file
    private static void setDefaultDirectory(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            defaultDirectory = selectedDirectory.getAbsolutePath();
            try (OutputStream output = new FileOutputStream("config.properties")) {
                Properties prop = new Properties();
                prop.setProperty("defaultDirectory", defaultDirectory);
                prop.store(output, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to copy the selected folder name to clipboard
    private static void copySelectedFolderName(JList<String> folderList, JFrame frame) {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            StringSelection selection = new StringSelection(selectedFolder);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(frame, "Folder name copied to clipboard.");
        } else {
            JOptionPane.showMessageDialog(frame, "No folder selected to copy.");
        }
    }

    // Method to open the selected folder
    private static void openSelectedFolder(JList<String> folderList, JFrame frame) {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            try {
                Desktop.getDesktop().open(new File(selectedFolder));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Unable to open the folder.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "No folder selected to open.");
        }
    }

    // Method to perform the search for folders
    private static void performSearch(String query, DefaultListModel<String> folderListModel, JFrame frame) {
        File defaultDir = new File(defaultDirectory);
        File[] folders = defaultDir.listFiles(File::isDirectory);

        folderListModel.clear();
        if (folders != null) {
            for (File folder : folders) {
                if (folder.getName().toLowerCase().contains(query)) {
                    folderListModel.addElement(folder.getAbsolutePath());
                }
            }
        }

        if (folderListModel.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No folders found matching the search criteria.");
        }
    }
}
