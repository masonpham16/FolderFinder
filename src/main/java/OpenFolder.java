import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class OpenFolder {

    private static String defaultDirectory;

    public static void main(String[] args) {
        // Create an instance of OpenFolder to access getClass() for loading images
        OpenFolder openFolderInstance = new OpenFolder();

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

        // Load the copy icon image
        ImageIcon copyIcon = new ImageIcon(openFolderInstance.getClass().getResource("/META-INF/copyButton.png"));
        Image scaledCopyImage = copyIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledCopyIcon = new ImageIcon(scaledCopyImage);

        // Create the copy button with the scaled icon
        JButton copyButton = new JButton(scaledCopyIcon);
        copyButton.setPreferredSize(new Dimension(20, 20));
        copyButton.setToolTipText("Copy selected folder name to clipboard");

        // Add action listener for the copy button
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedFolderName(folderList, frame);
            }
        });

        // Load the open folder icon image
        ImageIcon openFolderIcon = new ImageIcon(openFolderInstance.getClass().getResource("/META-INF/openButton.png"));
        Image scaledOpenFolderImage = openFolderIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledOpenFolderIcon = new ImageIcon(scaledOpenFolderImage);

        // Create the "Open Folder" button with the scaled icon
        JButton openFolderButton = new JButton(scaledOpenFolderIcon);
        openFolderButton.setPreferredSize(new Dimension(20, 20));
        openFolderButton.setToolTipText("Open selected folder");

        // Add action listener for the open folder button
        openFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSelectedFolder(folderList, frame);
            }
        });

        // Add action listener for the search button
        ActionListener searchAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch(textField.getText().toLowerCase(), folderListModel, frame);
            }
        };

        searchButton.addActionListener(searchAction);

        // Add key listener to the text field to listen for Enter key press
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAction.actionPerformed(null);
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
        folderList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    openSelectedFolder(folderList, frame);
                }
            }
        });

        // Create a menu bar with option to set default directory
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        JMenuItem setDirectoryItem = new JMenuItem("Set Default Directory");
        setDirectoryItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultDirectory(frame);
            }
        });
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
            //JOptionPane.showMessageDialog(frame, "Folder name copied to clipboard.");
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
                    folderListModel.addElement(folder.getName());
                }
            }
        }

        if (folderListModel.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No folders found matching the search criteria.");
        }
    }
}
