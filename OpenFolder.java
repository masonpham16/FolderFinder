import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
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

        // Create a panel to hold folder name and copy button
        JPanel folderPanel = new JPanel();
        folderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton copyButton = new JButton("Copy");
        copyButton.setPreferredSize(new Dimension(70, 25)); // Set button size

        // Add action listener for the copy button
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copySelectedFolderName(folderList, frame);
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
        
        // Add components to the frame
        frame.add(label);
        frame.add(textField);
        frame.add(searchButton);
        frame.add(scrollPane);
        frame.add(folderPanel); // Add folder panel with copy button

        // Make the frame visible
        frame.setVisible(true);
    }

    // Method to perform the search
    private static void performSearch(String searchString, DefaultListModel<String> folderListModel, JFrame frame) {
        // Use the default directory
        if (defaultDirectory == null || defaultDirectory.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Default directory not set. Please set it in the settings.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File directory = new File(defaultDirectory);
        if (directory.exists() && directory.isDirectory()) {
            File[] subfolders = directory.listFiles(File::isDirectory);
            ArrayList<String> matchingFolders = new ArrayList<>();

            if (subfolders != null) {
                for (File folder : subfolders) {
                    if (folder.getName().toLowerCase().contains(searchString)) {
                        matchingFolders.add(folder.getName());
                    }
                }
            }

            folderListModel.clear(); // Clear previous results
            if (!matchingFolders.isEmpty()) {
                // Add matching folders to the list
                for (String folderName : matchingFolders) {
                    folderListModel.addElement(folderName);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No folders found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Default directory not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to open the selected folder
    private static void openSelectedFolder(JList<String> folderList, JFrame frame) {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null && defaultDirectory != null) {
            File folder = new File(defaultDirectory, selectedFolder);
            try {
                Desktop.getDesktop().open(folder);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error opening folder.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to copy the selected folder name to clipboard
    private static void copySelectedFolderName(JList<String> folderList, JFrame frame) {
        String selectedFolder = folderList.getSelectedValue();
        if (selectedFolder != null) {
            StringSelection stringSelection = new StringSelection(selectedFolder);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(frame, "Folder name copied to clipboard!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No folder selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to set the default directory
    private static void setDefaultDirectory(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            defaultDirectory = selectedDirectory.getAbsolutePath();
            saveDefaultDirectory();
        }
    }

    // Method to load the default directory from properties file
    private static void loadDefaultDirectory() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            defaultDirectory = properties.getProperty("defaultDirectory", "");
        } catch (IOException e) {
            defaultDirectory = "";
        }
    }

    // Method to save the default directory to properties file
    private static void saveDefaultDirectory() {
        Properties properties = new Properties();
        properties.setProperty("defaultDirectory", defaultDirectory);
        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
