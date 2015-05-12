package com.video.converter;

import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author Hunk501
 */
public class Converter extends Thread {

    private final String ffmpeg;
    private final MainApp main;
    private final String filenameIn;
    private final String filenameOut;
    private Process process;
    
    public Converter(String str, MainApp m, String file_in, String file_out) {
        this.ffmpeg = str;
        this.main = m;
        this.filenameIn = file_in;
        this.filenameOut = file_out;
    }

    /**
     * Cancel the current process
     */
    public void cancel(){
        try {
            process.destroy();
            //JOptionPane.showMessageDialog(main, "Convertion was canceled.!", "Canceled", JOptionPane.WARNING_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    /**
     * Run background thread
     */
    @Override
    public void run(){
        try {
            System.out.println("-> Converting Video...");
            System.out.println("-> From: "+ filenameIn);
            System.out.println("-> To: "+ filenameOut);
            
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg, "-i", filenameIn, "-y", filenameOut);
            //  Background Processes Handler
            process = processBuilder.start();
            
            // Get the result from the background process
            InputStream inputIs = process.getErrorStream();
            File f = new File(filenameIn);
            InputStream readIs = process.getInputStream();
         
            InputStreamReader isr = new InputStreamReader(inputIs);
            BufferedReader br = new BufferedReader(isr);
         
            // Buffer the result       
            String line;
            int lop = 0;
            main.updateStatus(" Converting please wait...");
            
            int len = (int)f.length();
            int filesize = (int)Math.ceil(len / 100); // get the file size
            BufferedInputStream bis = new BufferedInputStream(readIs);
            byte[] buffer = new byte[100];
            int count = 0, c = 0;
            
            
            int xx = 0, xx1 = 0;
            boolean ss = false;
            StringTokenizer st;
            while ((line = br.readLine()) != null) {
                ++lop;
                if(xx == 49){
                    ss = true;
                }
                ++xx;
                if(ss){
                    //System.out.println(xx1);
                    if((count = bis.read(buffer)) != -1){
                        c = c + count;
                        int p = (c / filesize);
                        main.updateProgressBar(p);
                    }
                    ++xx1;
                }
                System.out.println(line);
                //main.updateStatus(" Converting please wait...");
            }
            
            
            
            // wait until convertion is done.!
            int result = process.waitFor();
            // check the result
            main.removeIcon(); //  remove loader icon
            if(result == 0){
                System.out.println("-> Convertion has been done.");
                System.out.println();
                JOptionPane.showMessageDialog(main, "Convertion has been successful.!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else if(result == 1){
                System.out.println("-> Convertion failed.!");
                JOptionPane.showMessageDialog(main, "Convertion failed.!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println("-> An error was encountered while processing convertion...");
            }
            main.enableGUI();

        } catch (IOException | InterruptedException e) {
            System.out.println("-> " + e.getMessage());
        }
    }
    
}
