import java.awt.*;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;

public class SortingVisualizer extends JFrame {
    private static final int ARRAY_SIZE = 20;
    private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 600;
    
    private int[] array;
    private boolean[] selected; // Track selected elements
    private boolean[] sorted; // Track sorted elements
    private boolean[] swapping; // Track elements being swapped
    private SortingPanel sortingPanel;
    private JButton[] algorithmButtons;
    private JButton shuffleButton;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private boolean isSorting = false;
    
    public SortingVisualizer() {
        initializeArray();
        setupUI();
        setTitle("Sorting Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    private void initializeArray() {
        array = new int[ARRAY_SIZE];
        selected = new boolean[ARRAY_SIZE];
        sorted = new boolean[ARRAY_SIZE];
        swapping = new boolean[ARRAY_SIZE];
        shuffleArray();
    }
    
    private void shuffleArray() {
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = rand.nextInt(PANEL_HEIGHT - 100) + 10;
            selected[i] = false;
            sorted[i] = false;
            swapping[i] = false;
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 20, 30));
        
        // Create sorting panel
        sortingPanel = new SortingPanel();
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Create status panel
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(sortingPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(new Color(30, 30, 40));
        
        // Algorithm buttons
        String[] algorithms = {"Bubble Sort", "Selection Sort", "Merge Sort", "Quick Sort", "Heap Sort"};
        algorithmButtons = new JButton[algorithms.length];
        
        for (int i = 0; i < algorithms.length; i++) {
            algorithmButtons[i] = createStyledButton(algorithms[i]);
            final int index = i;
            algorithmButtons[i].addActionListener(e -> startSorting(index));
            controlPanel.add(algorithmButtons[i]);
        }
        
        // Shuffle button
        shuffleButton = createStyledButton("Shuffle");
        shuffleButton.addActionListener(e -> {
            shuffleArray();
            sortingPanel.repaint();
            updateStatus("Array shuffled");
        });
        controlPanel.add(shuffleButton);
        
        // Speed slider
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setForeground(Color.WHITE);
        speedSlider = new JSlider(1, 100, 10);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(100, 30));
        
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        
        return controlPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(30, 30, 40));
        
        statusLabel = new JLabel("Ready to sort");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        
        // Enhanced legend
        JLabel legend = new JLabel("   |   Blue = Normal   |   Red = Comparing   |   Orange = Swapping   |   Green = Sorted");
        legend.setForeground(Color.LIGHT_GRAY);
        legend.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(legend);
        
        return statusPanel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(110, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        return button;
    }
    
    private void startSorting(int algorithmIndex) {
        if (isSorting) return;
        
        isSorting = true;
        setButtonsEnabled(false);
        clearSelection();
        clearSorted();
        clearSwapping();
        
        String[] algorithmNames = {"Bubble Sort", "Selection Sort", "Merge Sort", "Quick Sort", "Heap Sort"};
        updateStatus("Running " + algorithmNames[algorithmIndex] + "...");
        
        CompletableFuture.runAsync(() -> {
            try {
                switch (algorithmIndex) {
                    case 0: bubbleSort(); break;
                    case 1: selectionSort(); break;
                    case 2: mergeSort(0, array.length - 1); break;
                    case 3: quickSort(0, array.length - 1); break;
                    case 4: heapSort(); break;
                }
                
                SwingUtilities.invokeLater(() -> {
                    clearSelection();
                    clearSwapping();
                    markAllSorted();
                    sortingPanel.repaint();
                    updateStatus(algorithmNames[algorithmIndex] + " completed!");
                    setButtonsEnabled(true);
                    isSorting = false;
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Error occurred");
                    setButtonsEnabled(true);
                    isSorting = false;
                });
            }
        });
    }
    
    private void clearSelection() {
        for (int i = 0; i < array.length; i++) {
            selected[i] = false;
        }
    }
    
    private void clearSorted() {
        for (int i = 0; i < array.length; i++) {
            sorted[i] = false;
        }
    }
    
    private void clearSwapping() {
        for (int i = 0; i < array.length; i++) {
            swapping[i] = false;
        }
    }
    
    private void markAllSorted() {
        for (int i = 0; i < array.length; i++) {
            sorted[i] = true;
        }
    }
    
    private void markSorted(int index) {
        if (index >= 0 && index < array.length) {
            sorted[index] = true;
        }
    }
    
    private void setButtonsEnabled(boolean enabled) {
        for (JButton button : algorithmButtons) {
            button.setEnabled(enabled);
        }
        shuffleButton.setEnabled(enabled);
    }
    
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
    
    private void sleep() {
        try {
            Thread.sleep(Math.max(10, 300 - speedSlider.getValue() * 2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void highlight(int... indices) {
        clearSelection();
        for (int i : indices) {
            if (i >= 0 && i < array.length) {
                selected[i] = true;
            }
        }
        SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        sleep();
    }
    
    private void swap(int i, int j) {
        // Highlight the elements being swapped
        clearSelection();
        clearSwapping();
        swapping[i] = true;
        swapping[j] = true;
        SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        sleep();
        
        // Perform the swap
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        
        // Keep highlighting briefly after swap
        SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        sleep();
        
        // Clear swap highlighting
        swapping[i] = false;
        swapping[j] = false;
    }
    
    // Bubble Sort
    private void bubbleSort() {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - 1 - i; j++) {
                highlight(j, j + 1);
                
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                }
            }
            // Mark the last i elements as sorted
            markSorted(array.length - 1 - i);
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        }
        // Mark the first element as sorted
        markSorted(0);
    }
    
    // Selection Sort
    private void selectionSort() {
        for (int i = 0; i < array.length - 1; i++) {
            int minIdx = i;
            
            for (int j = i + 1; j < array.length; j++) {
                highlight(minIdx, j);
                
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            
            if (minIdx != i) {
                swap(i, minIdx);
            }
            
            // Mark the current position as sorted
            markSorted(i);
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        }
        // Mark the last element as sorted
        markSorted(array.length - 1);
    }
    
    // Merge Sort
    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
            
            // Mark the merged section as sorted (temporarily)
            for (int i = left; i <= right; i++) {
                markSorted(i);
            }
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        }
    }
    
    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        
        while (i <= mid && j <= right) {
            highlight(i, j);
            
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }
        
        while (i <= mid) {
            highlight(i);
            temp[k++] = array[i++];
        }
        
        while (j <= right) {
            highlight(j);
            temp[k++] = array[j++];
        }
        
        for (int idx = 0; idx < temp.length; idx++) {
            array[left + idx] = temp[idx];
            highlight(left + idx);
        }
    }
    
    // Quick Sort
    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            markSorted(pi); // Mark pivot as sorted
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        } else if (low == high) {
            markSorted(low); // Single element is sorted
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
        }
    }
    
    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            highlight(j, high);
            
            if (array[j] < pivot) {
                i++;
                if (i != j) {
                    swap(i, j);
                }
            }
        }
        
        highlight(i + 1, high);
        swap(i + 1, high);
        
        return i + 1;
    }
    
    // Heap Sort
    private void heapSort() {
        buildMaxHeap();
        
        for (int i = array.length - 1; i > 0; i--) {
            swap(0, i);
            markSorted(i); // Mark the extracted element as sorted
            SwingUtilities.invokeLater(() -> sortingPanel.repaint());
            maxHeapify(0, i);
        }
        markSorted(0); // Mark the last element as sorted
    }
    
    private void buildMaxHeap() {
        for (int i = array.length / 2 - 1; i >= 0; i--) {
            maxHeapify(i, array.length);
        }
    }
    
    private void maxHeapify(int i, int heapSize) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;
        
        if (left < heapSize) {
            highlight(i, left);
            if (array[left] > array[largest]) {
                largest = left;
            }
        }
        
        if (right < heapSize) {
            highlight(largest, right);
            if (array[right] > array[largest]) {
                largest = right;
            }
        }
        
        if (largest != i) {
            swap(i, largest);
            maxHeapify(largest, heapSize);
        }
    }
    
    // Custom panel for drawing
    private class SortingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background
            g2d.setColor(new Color(20, 20, 30));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Calculate bar dimensions
            int barWidth = Math.max(4, (getWidth() - 40) / array.length);
            int maxHeight = getHeight() - 40;
            
            // Draw bars
            for (int i = 0; i < array.length; i++) {
                int barHeight = (array[i] * maxHeight) / 600;
                int x = 20 + i * barWidth;
                int y = getHeight() - barHeight - 20;
                
                // Color based on state priority: swapping > selected > sorted > normal
                if (swapping[i]) {
                    g2d.setColor(new Color(255, 165, 0)); // Orange for swapping
                } else if (selected[i]) {
                    g2d.setColor(new Color(255, 50, 50)); // Red for comparing
                } else if (sorted[i]) {
                    g2d.setColor(new Color(50, 255, 50)); // Green for sorted
                } else {
                    g2d.setColor(new Color(100, 150, 255)); // Blue for normal
                }
                
                g2d.fillRect(x, y, barWidth - 1, barHeight);
                
                // Border
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.drawRect(x, y, barWidth - 1, barHeight);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SortingVisualizer();
        });
    }
}



