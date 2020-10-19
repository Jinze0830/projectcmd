package frontend;

import backend.main.com.projectcmd.csvprocessor.CSVReader;
import backend.main.com.projectcmd.printerConnector.FlowLineSvc;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class PrinterControllerUI extends JFrame {
    private JPanel mainPanel;
    private JLabel lotNumber;
    private JTextField lotNumberTextField;
    private JButton startButton;
    private JButton stopButton;
    private JButton resumeButton;
    private JTextField fileNameText;
    private JButton uploadCVSButton;
    private JTextField curBarcodeTextField;
    private FlowLineSvc flowLineSvc;
    private String fileName;
    Queue<String> barcodes = null;

    public PrinterControllerUI(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        CSVReader reader = new CSVReader();
        try {
            //flowLineSvc = new FlowLineSvc("192.168.0.100", 9004);
            flowLineSvc = new FlowLineSvc("192.168.0.100", 9004);
        } catch (IOException exception) {
            JOptionPane.showConfirmDialog(mainPanel, "Network Connect issue",
                    "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
        }


        uploadCVSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser j = new JFileChooser();
                j.setDialogTitle("Select input csv file");
                j.showOpenDialog(null);
                String csvPath = j.getSelectedFile().toPath().toString();
                fileNameText.setText(csvPath);
                String[] arr = csvPath.split("/");
                fileName = arr[arr.length - 1];
                try {
                    barcodes = reader.getAllBarcodes(csvPath);
                } catch (IOException exception) {
                        JOptionPane.showConfirmDialog(mainPanel, "Cannot read file",
                                "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
            }
        });



        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                curBarcodeTextField.setText(flowLineSvc.getCurrentBarcode());
            }
        }, 1000, 1000);


        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lotNumberTextField.getText() == null || lotNumberTextField.getText().equals("")) {
                    JOptionPane.showConfirmDialog(mainPanel, "Please enter lot number",
                            "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
                } else {
                    try {
                        flowLineSvc.updateInFlow(barcodes,
                                "5", lotNumberTextField.getText(),true, fileName);
                    } catch (IOException exception) {
                        JOptionPane.showConfirmDialog(mainPanel, "TCP package send and receive issue",
                                "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flowLineSvc.setStopProgram(true);
            }
        });

        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lotNumberTextField.getText() == null || lotNumberTextField.getText().equals("")) {
                    JOptionPane.showConfirmDialog(mainPanel, "Please enter lot number",
                            "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
                } else {
                    try {
                        flowLineSvc.updateInFlow(barcodes, "5",
                                flowLineSvc.getLotNumber(),true, fileName);
                    } catch (IOException exception) {
                        JOptionPane.showConfirmDialog(mainPanel, "TCP package send and receive issue",
                                "Warning", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new PrinterControllerUI("printer Connector");
        frame.setVisible(true);
    }
}
