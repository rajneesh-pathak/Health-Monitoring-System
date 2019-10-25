/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaproject;

import com.teknikindustries.bulksms.SMS;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
/*-------------------------------------------------*/


public class PatientsForm extends javax.swing.JFrame {
    private Object JoptionPane;
    /**
     * Creates new form PatientsForm
     */

    /*-------------------------------------------------*/
    
    
    
    public PatientsForm() {
        initComponents();
        MySqlConnection();
        JOptionPane.showMessageDialog(null,"Mysql DB Connection Successful on Patients.....");
        MySqlConnectionParameters();
        JOptionPane.showMessageDialog(null,"Mysql DB Connection Successful on Parameters.....");
        fillTable();
        currentTime();
    }
    
    public void currentTime(){
        
        Thread clock = new Thread(){
            public void run(){
                ArrayList<PatientBean> al=retriveData();
                String msg;
                int[] flag = new int[100];
                
                for(int i=0;i<al.size();i++){
                            Calendar a = Calendar.getInstance();
                            String sec = Integer.toString(a.get(Calendar.SECOND));
                            String min = Integer.toString(a.get(Calendar.MINUTE));
                            String hour = Integer.toString(a.get(Calendar.HOUR_OF_DAY));
                            String time = hour + ":" + min;
                            jLabel_time.setText(time + ":" + sec);
                           
                            Date d = new Date();
                            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
                            jLabel_date.setText(s.format(d));
                            
                            String str = retriveData().get(i).getReminder();
                            
                            /*String parts[] = str.split("-");
                            String date = parts[0];
                            String month = parts[1];
                            String year = parts[2];*/
                            
                            if(str.equals(s.format(d)) && flag[i]!=1){
                                msg = retriveData().get(i).getName() + " TODAY you need to meet your doctor";
                                JOptionPane.showMessageDialog(null,msg);
                                SMS send = new SMS();
                                send.SendSMS("Abhishek_Karmakar", "Abhishek123#", msg, "8115257095", "http://bulksms.2way.co.za/eapi/submission/send_sms/2/2.0");   
                                System.out.println("Message has been send to the patient " + retriveData().get(i).getName());
                                flag[i]=1;
                            }
                            
                            /*if(time.equals(retriveData().get(i).getReminder())){
                                            msg = retriveData().get(i).getName() + " it's time to take your medicine";
                                            SMS send = new SMS();
                                            send.SendSMS("Abhishek_Karmakar", "Abhishek123#", msg, "8115257095", "http://bulksms.2way.co.za/eapi/submission/send_sms/2/2.0");
                                            System.out.println("Message has been send");
                                            JOptionPane.showMessageDialog(null,msg);                                 
                             }*/
                             
                    try{
                        sleep(1000);
                    }
                    catch(Exception e){
                        JOptionPane.showMessageDialog(null,e);
                    }
                    
                    if(i==(al.size()-1))
                        i=-1;
                }
            }
        };
        clock.start();
    }

    public Connection MySqlConnection(){
        Connection conn=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/project","root","");
            //JOptionPane.showMessageDialog(null,"Mysql DB Connection Successful.....");
            return conn;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"Mysql Connection Failed in Patients form.....");
            return null;         
        }
    }
    
    
    public Connection MySqlConnectionParameters(){
        Connection conn=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/parameters","root","");
            //JOptionPane.showMessageDialog(null,"Mysql DB Connection Successful.....");
            return conn;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null,"Mysql Connection Failed in parameters form.....");
            return null;         
        }
    }
    
    String photopath = "";
    public ImageIcon resetImageSize(String photopath,byte[] photo){
        ImageIcon myPhoto=null;
        if(photopath!=null){
            myPhoto=new ImageIcon(photopath);
        }
        else{
            myPhoto = new ImageIcon(photo);
        }
        Image img=myPhoto.getImage();
        Image img1=img.getScaledInstance(label_photo.getWidth(),label_photo.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon ph=new ImageIcon(img1);
        return ph;
    }
         
    //show or add data to the Jtable
    public ArrayList<PatientBean> retriveData(){
        ArrayList<PatientBean> al=null;
        al=new ArrayList<PatientBean>();
        Connection conn=null;
        try{
            conn= MySqlConnection();
            String q = "select * from patients";
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery(q);
            PatientBean patient;
            while(rs.next()){
                patient = new PatientBean(rs.getInt(1),rs.getString("name"),
                        rs.getString(3),rs.getBytes("photo"),rs.getInt(5),rs.getInt(6),rs.getInt(7),
                        rs.getInt(8),rs.getString("docadv"),rs.getString("reminder"));
                al.add(patient);
                
            }
        }
        catch(Exception e){
            System.out.println("Error in retriveData method"+e);
        }
        return al;
    }
    
    
    public ArrayList<ParametersBean> retriveDataParameters(){
        ArrayList<ParametersBean> al=null;
        al=new ArrayList<ParametersBean>();
        Connection conn=null;
        try{
            conn= MySqlConnectionParameters();
            String q = "select * from param";
            Statement st = conn.createStatement();
            ResultSet rs=st.executeQuery(q);
            ParametersBean patient;
            while(rs.next()){
                patient = new ParametersBean(rs.getInt(1),rs.getInt(2));
                al.add(patient);
            }
        }
        catch(Exception e){
            System.out.println("Error in retriveDataParameters method"+e);
        }
        return al;
    }
    
    //fill the table / show all data to JTable
    public void fillTable(){
        ArrayList<PatientBean> al=retriveData();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        Object[] row=new Object[9];
        for(int i=0;i<al.size();i++){
            row[0]=al.get(i).getId();
            row[1]=al.get(i).getName();
            row[2]=al.get(i).getDob();
            row[3]=al.get(i).getPressure();
            row[4]=al.get(i).getGlucose();
            row[5]=al.get(i).getTemperature();
            row[6]=al.get(i).getSaturation();
            row[7]=al.get(i).getDocadv();  
            row[8]=al.get(i).getReminder();
            model.addRow(row);
        }        
    }
    
    //Show items to he field
    public void showItemToFields(int index){
        
        jTextField_id.setText(Integer.toString(retriveData().get(index).getId()));
        jTextField_name.setText(retriveData().get(index).getName());
        
        try {
            java.util.Date dob =null;
            dob=new SimpleDateFormat("dd-MM-yyyy").parse((String)retriveData().get(index).getDob());
            jDateChooser_dob.setDate(dob);
        }
        catch(Exception e){
            System.out.println("Error at  showItemToFields"+e);
        }
        label_photo.setIcon(resetImageSize(null,retriveData().get(index).getPhoto()));
        jTextField_pressure.setText(Integer.toString(retriveData().get(index).getPressure()));
        jTextField_glucose.setText(Integer.toString(retriveData().get(index).getGlucose()));
        jTextField_temperature.setText(Integer.toString(retriveData().get(index).getTemperature()));
        jTextField_saturation.setText(Integer.toString(retriveData().get(index).getSaturation()));
        jTextField_advice.setText(retriveData().get(index).getDocadv());
        jTextField_reminder.setText(retriveData().get(index).getReminder());
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jTextField_name = new javax.swing.JTextField();
        jDateChooser_dob = new com.toedter.calendar.JDateChooser();
        label_photo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField_pressure = new javax.swing.JTextField();
        jTextField_glucose = new javax.swing.JTextField();
        jTextField_temperature = new javax.swing.JTextField();
        jTextField_saturation = new javax.swing.JTextField();
        jTextField_advice = new javax.swing.JTextField();
        jButton_new = new javax.swing.JButton();
        jButton_save = new javax.swing.JButton();
        jButton_update = new javax.swing.JButton();
        jButton_delete = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jTextField_search = new javax.swing.JTextField();
        jButton_photo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton_Group_checking = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jTextField_reminder = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel_time = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel_date = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 153));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 0, 0));
        jLabel1.setText("Patient Health Monitoring System");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel2.setText("Created by Abhishek Karmakar");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 26)); // NOI18N
        jLabel3.setText("Patient Information");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setText("Blood Temperature :");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel5.setText("Patient Name :");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("DOB :");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setText("PHOTO :");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/patient_1.jpg"))); // NOI18N
        jLabel8.setBorder(javax.swing.BorderFactory.createMatteBorder(5, 5, 5, 5, new java.awt.Color(0, 0, 0)));

        jTextField_id.setForeground(new java.awt.Color(0, 0, 102));
        jTextField_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_idActionPerformed(evt);
            }
        });

        jTextField_name.setForeground(new java.awt.Color(0, 0, 102));

        jDateChooser_dob.setForeground(new java.awt.Color(0, 0, 102));
        jDateChooser_dob.setDateFormatString("dd-MM-yyyy");

        label_photo.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setText("Patient ID :");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel10.setText("Blood Pressure : ");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setText("Blood Glucose (in ml) :");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel12.setText("Doctor Advice :");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Saturation (%) :");

        jButton_new.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_new.setForeground(new java.awt.Color(0, 0, 153));
        jButton_new.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.jpg"))); // NOI18N
        jButton_new.setText("New/Clear");
        jButton_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_newActionPerformed(evt);
            }
        });

        jButton_save.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_save.setForeground(new java.awt.Color(0, 0, 153));
        jButton_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/insert.jpg"))); // NOI18N
        jButton_save.setText("Insert");
        jButton_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_saveActionPerformed(evt);
            }
        });

        jButton_update.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_update.setForeground(new java.awt.Color(0, 0, 153));
        jButton_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/up.jpg"))); // NOI18N
        jButton_update.setText("Update");
        jButton_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_updateActionPerformed(evt);
            }
        });

        jButton_delete.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_delete.setForeground(new java.awt.Color(0, 0, 153));
        jButton_delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.jpg"))); // NOI18N
        jButton_delete.setText("Delete");
        jButton_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_deleteActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel14.setText("Search Patient By Name");

        jTextField_search.setForeground(new java.awt.Color(255, 0, 51));
        jTextField_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_searchActionPerformed(evt);
            }
        });
        jTextField_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField_searchKeyReleased(evt);
            }
        });

        jButton_photo.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_photo.setForeground(new java.awt.Color(0, 0, 153));
        jButton_photo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pic.jpg"))); // NOI18N
        jButton_photo.setText("Select Photo");
        jButton_photo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_photoActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Patient ID", "Patient Name", "DOB", "Pressure", "Glucose", "Temperature", "Saturation", "Doctor Advice", "Reminder"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 25)); // NOI18N
        jButton1.setForeground(new java.awt.Color(204, 0, 0));
        jButton1.setText("ADMIN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton2.setForeground(new java.awt.Color(102, 255, 255));
        jButton2.setText("Check Improvement");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton_Group_checking.setBackground(new java.awt.Color(51, 51, 51));
        jButton_Group_checking.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jButton_Group_checking.setForeground(new java.awt.Color(102, 255, 255));
        jButton_Group_checking.setText("Group Checking");
        jButton_Group_checking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_Group_checkingActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setText("Set date for Doctor's appointment :");

        jPanel6.setBackground(new java.awt.Color(153, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Normal Blood Pressure");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Upper Limit : 120       Lower Limt : 90");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Normal Blood Glucose");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Upper Limit : 5         Lower Limt : 3");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Normal Blood Oxygen Saturation");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Upper Limit : 100         Lower Limt : 75");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Normal Body Temperature");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Upper Limit : 40         Lower Limt : 34");

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("37 degree celcius is the normal temp.");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel17))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel24)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel21)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addGap(45, 45, 45)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addGap(38, 38, 38)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addGap(40, 40, 40)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 373, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Normal Values", jPanel3);

        jPanel4.setBackground(new java.awt.Color(51, 51, 255));

        jPanel7.setBackground(new java.awt.Color(51, 255, 204));
        jPanel7.setForeground(new java.awt.Color(255, 255, 102));

        jLabel25.setBackground(new java.awt.Color(0, 204, 0));
        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("The patients are sorted according to ");

        jLabel26.setBackground(new java.awt.Color(0, 204, 0));
        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("if they suffer from more than one");

        jLabel27.setBackground(new java.awt.Color(0, 204, 0));
        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("their different PID");

        jLabel28.setBackground(new java.awt.Color(0, 204, 0));
        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("There are total 9 groups formed");

        jLabel29.setBackground(new java.awt.Color(0, 204, 0));
        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("And each are displayed more than once");

        jLabel30.setBackground(new java.awt.Color(0, 204, 0));
        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("abnormalities");

        jLabel31.setBackground(new java.awt.Color(0, 204, 0));
        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("based on different parameters");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel25))
                        .addGap(37, 37, 37))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(58, 58, 58))))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(jLabel26)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addGap(47, 47, 47))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Grouping", jPanel4);

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("of the patient  is countered");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("with the normal parameters");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setText("The health condition");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("improved condition");

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("and display the");

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.jpg"))); // NOI18N
        jLabel39.setText("jLabel39");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jLabel33))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(jLabel36)))
                .addContainerGap(52, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(92, 92, 92))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(128, 128, 128))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel35)
                .addGap(37, 37, 37)
                .addComponent(jLabel39)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Check Improvement", jPanel5);

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(204, 0, 51));
        jLabel37.setText("Creator");

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(204, 0, 51));
        jLabel38.setText("Details");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(106, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38))
                .addGap(95, 95, 95))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .addComponent(jLabel37)
                .addGap(31, 31, 31)
                .addComponent(jLabel38)
                .addGap(95, 95, 95))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Admin", jPanel2);

        jLabel_time.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel_time.setForeground(new java.awt.Color(255, 0, 51));
        jLabel_time.setText("Time");

        jLabel41.setBackground(new java.awt.Color(255, 0, 51));
        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 0, 51));
        jLabel41.setText("TIME :");

        jLabel42.setBackground(new java.awt.Color(255, 0, 51));
        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 0, 51));
        jLabel42.setText("Date : ");

        jLabel_date.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel_date.setForeground(new java.awt.Color(255, 0, 51));
        jLabel_date.setText("date");

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaproject/doc.gif"))); // NOI18N

        jLabel43.setIcon(new javax.swing.ImageIcon(getClass().getResource("/javaproject/top.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(114, 114, 114)
                                        .addComponent(jLabel15))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel13)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel10))))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextField_advice, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_saturation, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_temperature, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_glucose, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_pressure, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                                    .addComponent(jTextField_reminder, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(68, 68, 68)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(136, 136, 136)
                                        .addComponent(jLabel3))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel9)
                                            .addComponent(jButton_photo))
                                        .addGap(120, 120, 120)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jTextField_id)
                                                .addComponent(jTextField_name)
                                                .addComponent(jDateChooser_dob, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(label_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(116, 116, 116)
                                                .addComponent(jLabel14))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(175, 175, 175)
                                                .addComponent(jTextField_search, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(184, 184, 184)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(jButton_new, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jButton_save, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jButton_update, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jButton_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(207, 207, 207)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton_Group_checking, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel41)
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel_time)
                                        .addGap(135, 135, 135))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel42)
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel_date)
                                        .addGap(102, 102, 102))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(508, 508, 508)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel43))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(134, 134, 134)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1445, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(23, 23, 23))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(3, 3, 3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(17, 17, 17)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_new, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jButton_save, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jButton_update, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_search, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel_time)
                                .addComponent(jLabel41)))
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel9))
                            .addComponent(jTextField_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField_name, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser_dob, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(label_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField_pressure, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextField_glucose, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_temperature, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(jLabel7)
                                .addGap(35, 35, 35)
                                .addComponent(jButton_photo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(3, 3, 3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_saturation, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel12))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jTextField_advice, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_reminder, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel_date)
                                .addComponent(jLabel42))
                            .addComponent(jButton_Group_checking, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_idActionPerformed

    private void jButton_photoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_photoActionPerformed
        // for selecting the photo
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter fnef=new FileNameExtensionFilter("*.images","jpg","png");
        chooser.addChoosableFileFilter(fnef);
        int ans=chooser.showSaveDialog(null);
        if(ans==JFileChooser.APPROVE_OPTION){
            File selectedPhoto=chooser.getSelectedFile();
            String path=selectedPhoto.getAbsolutePath();
            label_photo.setIcon(resetImageSize(path,null));
            this.photopath=path;
        }
    }//GEN-LAST:event_jButton_photoActionPerformed

    private void jButton_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_saveActionPerformed
        // for inserting or saving the PATIENTS record into the database
        if((jTextField_id.getText()!=null || jTextField_name.getText()!=null || jDateChooser_dob!=null || jTextField_pressure.getText()!=null
                || jTextField_glucose.getText()!=null || jTextField_temperature.getText()!=null || jTextField_saturation.getText()!=null 
                || jTextField_advice.getText()!=null || jTextField_reminder.getText()!=null) && photopath!=null){
            try {
                Connection conn = MySqlConnection();
                //String qry="insert into patients (id,name,dob,photo,pressure,glucose,temperature,saturation,docadv) values "+ "(?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps=conn.prepareStatement("insert into patients"
                        + "(id,name,dob,photo,pressure,glucose,temperature,saturation,docadv,reminder) values (?,?,?,?,?,?,?,?,?,?)");
                ps.setInt(1,Integer.parseInt(jTextField_id.getText()));
                ps.setString(2,jTextField_name.getText());
                
                SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                String dob1=sdf.format(jDateChooser_dob.getDate());
                ps.setString(3, dob1);
                
                InputStream is=new FileInputStream(new File(photopath));
                ps.setBlob(4, is);
                
                ps.setInt(5,Integer.parseInt(jTextField_pressure.getText()));
                ps.setInt(6,Integer.parseInt(jTextField_glucose.getText()));
                ps.setInt(7,Integer.parseInt(jTextField_temperature.getText()));
                ps.setInt(8,Integer.parseInt(jTextField_saturation.getText()));
                ps.setString(9,jTextField_advice.getText());      
                ps.setString(10,jTextField_reminder.getText());
                
                int res=ps.executeUpdate();
                fillTable();
                if(res>=1){
                    JOptionPane.showMessageDialog(null,res + "Number of Patients"
                            + " inserted into database.......");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Patients insertion Failed.........");
                }
              
            }
            catch(Exception e){  
                 JOptionPane.showMessageDialog(null,e);
            }
            
            jTextField_id.setText("");
            jTextField_name.setText("");
            jDateChooser_dob.setCalendar(null);
            jTextField_pressure.setText("");
            jTextField_glucose.setText("");
            jTextField_temperature.setText("");
            jTextField_saturation.setText("");
            jTextField_advice.setText("");
            jTextField_reminder.setText("");
            
            
        }
        else{
            JOptionPane.showMessageDialog(null, "All fields are compulsory......");
        }
    }//GEN-LAST:event_jButton_saveActionPerformed

    private void jButton_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_updateActionPerformed
        // for updating patients information
        if(jTextField_id!=null || jTextField_name!=null || jDateChooser_dob!=null || jTextField_pressure!=null
                || jTextField_glucose!=null || jTextField_temperature!=null || jTextField_saturation!=null 
                || jTextField_advice!=null || jTextField_reminder!=null){
            String qry=null;
            PreparedStatement ps = null;
            Connection conn = MySqlConnection();
            
            if(photopath==null){
                        try {
                            qry="update patients set name=?,dob=?,pressure=?,glucose=?,temperature=?,saturation=?,docadv=?,reminder=? where id=?";
                            ps=conn.prepareStatement(qry);
                            //ps.setInt(1,Integer.parseInt(jTextField_id.getText()));
                            ps.setString(1,jTextField_name.getText());

                            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                            String dob1=sdf.format(jDateChooser_dob.getDate());
                            ps.setString(2, dob1);

                            //InputStream is=new FileInputStream(new File(photopath));
                            //ps.setBlob(3, is);
                            ps.setInt(3,Integer.parseInt(jTextField_pressure.getText()));
                            ps.setInt(4,Integer.parseInt(jTextField_glucose.getText()));
                            ps.setInt(5,Integer.parseInt(jTextField_temperature.getText()));
                            ps.setInt(6,Integer.parseInt(jTextField_saturation.getText()));
                            ps.setString(7,jTextField_advice.getText());
                            ps.setString(8,jTextField_reminder.getText());
                            ps.setInt(9,Integer.parseInt(jTextField_id.getText()));

                            int res=ps.executeUpdate();
                            fillTable();
                            if(res>=1){
                                JOptionPane.showMessageDialog(null, "Patient"
                                        + " updated Successfully.......");
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "Patients updation Failed.........");
                            }

                        }
                        catch(Exception e){  
                             JOptionPane.showMessageDialog(null,e);
                        }
                        
                        jTextField_id.setText("");
                    jTextField_name.setText("");
                    jDateChooser_dob.setCalendar(null);
              
                    jTextField_pressure.setText("");
                    jTextField_glucose.setText("");
                    jTextField_temperature.setText("");
                    jTextField_saturation.setText("");
                    jTextField_advice.setText("");

                        
                }
                else{ //update with phtoto
                    try {
                            InputStream is=new FileInputStream(new File(photopath));
                            
                            qry="update patients set name=?,dob=?,photo=?,pressure=?,glucose=?,temperature=?,saturation=?,docadv=?,reminder=? where id=?";
                            ps=conn.prepareStatement(qry);
                            
                            ps.setString(1,jTextField_name.getText());

                            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
                            String dob1=sdf.format(jDateChooser_dob.getDate());
                            ps.setString(2, dob1);
                            ps.setBlob(3, is);

                            ps.setInt(4,Integer.parseInt(jTextField_pressure.getText()));
                            ps.setInt(5,Integer.parseInt(jTextField_glucose.getText()));
                            ps.setInt(6,Integer.parseInt(jTextField_temperature.getText()));
                            ps.setInt(7,Integer.parseInt(jTextField_saturation.getText()));
                            ps.setString(8,jTextField_advice.getText());   
                            ps.setString(9,jTextField_reminder.getText());
                            ps.setInt(10,Integer.parseInt(jTextField_id.getText()));

                            int res=ps.executeUpdate();
                            fillTable();
                            if(res>=1){
                                JOptionPane.showMessageDialog(null, "Patient"
                                        + " updated Successfully.......");
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "Patients updation Failed.........");
                            }

                        }
                        catch(Exception e){  
                             JOptionPane.showMessageDialog(null,e);
                        }
                        
                    jTextField_id.setText("");
                    jTextField_name.setText("");
                    jDateChooser_dob.setCalendar(null);
                    jTextField_pressure.setText("");
                    jTextField_glucose.setText("");
                    jTextField_temperature.setText("");
                    jTextField_saturation.setText("");
                    jTextField_advice.setText("");
                    jTextField_reminder.setText("");

                }
            
        }else
            JOptionPane.showMessageDialog(null,"All fields are mandatory.....");
        
    }//GEN-LAST:event_jButton_updateActionPerformed

    private void jButton_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_deleteActionPerformed
        // for deleting the patients data
        if(jTextField_id.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Please enter the Patient id");
        }
        else{
            try{
                String qry="delete from patients where id=?";
                Connection conn=MySqlConnection();
                PreparedStatement ps=conn.prepareStatement(qry);
                ps.setInt(1, Integer.parseInt(jTextField_id.getText().toString()));
                int res=ps.executeUpdate();
                fillTable();
                if(res>=1){
                    JOptionPane.showMessageDialog(null,"Patients deleted Successfully....");
                }
                else
                    JOptionPane.showMessageDialog(null,"Patients deletion Failed....");
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,e);
            }
        }
                    jTextField_id.setText("");
                    jTextField_name.setText("");
                    jDateChooser_dob.setCalendar(null);
                    jTextField_pressure.setText("");
                    jTextField_glucose.setText("");
                    jTextField_temperature.setText("");
                    jTextField_saturation.setText("");
                    jTextField_advice.setText("");
                    jTextField_reminder.setText("");
    }//GEN-LAST:event_jButton_deleteActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int ind = jTable1.getSelectedRow();
        showItemToFields(ind);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_searchKeyReleased
        // to search the student by name
        ArrayList<PatientBean> al=null;
        al=new ArrayList<PatientBean>();
        String val=jTextField_search.getText().toString();
        try{
            Connection conn=MySqlConnection();
            String qry ="select * from patients where name like '%"+val+"%'";
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(qry);
            PatientBean patient;
            while(rs.next()){
                patient = new PatientBean(rs.getInt(1),rs.getString("name"),
                        rs.getString(3),rs.getBytes("photo"),rs.getInt(5),rs.getInt(6),rs.getInt(7),
                        rs.getInt(8),rs.getString("docadv"),rs.getString("reminder"));
                al.add(patient);
            }
            DefaultTableModel model=(DefaultTableModel)jTable1.getModel();
            model.setRowCount(0);
            
            Object[] row=new Object[9];
        for(int i=0;i<al.size();i++){
            row[0]=al.get(i).getId();
            row[1]=al.get(i).getName();
            row[2]=al.get(i).getDob();
            row[3]=al.get(i).getPressure();
            row[4]=al.get(i).getGlucose();
            row[5]=al.get(i).getTemperature();
            row[6]=al.get(i).getSaturation();
            row[7]=al.get(i).getDocadv();  
            row[8]=al.get(i).getReminder();
            model.addRow(row);
        }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }//GEN-LAST:event_jTextField_searchKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Intro s = new Intro();
        s.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_newActionPerformed
        // for clearing the jTextFields
                    jTextField_id.setText("");
                    jTextField_name.setText("");
                    jDateChooser_dob.setCalendar(null);
                    jTextField_pressure.setText("");
                    jTextField_glucose.setText("");
                    jTextField_temperature.setText("");
                    jTextField_saturation.setText("");
                    jTextField_advice.setText("");
                    jTextField_reminder.setText("");
    }//GEN-LAST:event_jButton_newActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //Check improvement of the patient
        
            int BloodPressureUp = retriveDataParameters().get(0).getHigh();
            int BloodPressureLow = retriveDataParameters().get(0).getLow();
            int PreHighBloodPressure = 140;

            int BloodGlucoseLow = retriveDataParameters().get(1).getHigh();
            int BloodGlucoseHigh = retriveDataParameters().get(1).getLow();

            int  oxySaturationHigh = retriveDataParameters().get(2).getHigh();
            int oxySaturationLow = retriveDataParameters().get(2).getLow();

            int bodyTempHigh = retriveDataParameters().get(3).getHigh();
            int bodyTempLow = retriveDataParameters().get(3).getLow();
            
        int index = jTable1.getSelectedRow();
        int pressure = retriveData().get(index).getPressure();
        int glucose = retriveData().get(index).getGlucose();
        int temperature = retriveData().get(index).getTemperature();
        int saturation = retriveData().get(index).getSaturation();
        String DoctorAdvice = retriveData().get(index).getDocadv();
        
        int flag=0;
        String qryPre="";
        String qryGlu="";
        String qryTemp="";
        String qrySat="";
        
        if(pressure>=PreHighBloodPressure)
            qryPre="Very High Blood Pressure";
        else if(pressure<PreHighBloodPressure && pressure>BloodPressureUp)
            qryPre="Pre High Blood Pressure";
        else if(pressure<BloodPressureLow)
            qryPre="Low Blood Pressure";
        else
            flag++;
        
        if(glucose>BloodGlucoseHigh)
            qryGlu="Very high Blood Glucose Level";
        else if(glucose<BloodGlucoseLow)
            qryGlu = "Low Blood Glucose Level";
        else
            flag++;
        
        if(temperature>bodyTempHigh)
            qryTemp = "Very High Body Temp";
        else if(temperature <bodyTempLow)
            qryTemp = "Low Body Temperature";
        else 
            flag++;
        
        if(saturation>oxySaturationHigh )
            qrySat="Very High level of oxygen saturation in the body";
        else if(saturation<oxySaturationLow)
            qrySat="Very low level of oxygen saturation in the body \n Need an immediate check-up";
        else
            flag++;
        

        //UIManager.put("JOptionPane.MessageFont", new FontUIResource(new Font("Arial",Font.BOLD,238)));
        //ImageIcon icon = new ImageIcon("src/images/check.jpg");
        if(flag!=3)
            JOptionPane.showMessageDialog(null,"The Patient Suffers from\n" + qryPre +"\n"+ qryGlu+ "\n" + qryTemp+ "\n" + qrySat
            + "\n---------------------------------------------------\n" 
            + "FOLLOW THE DOCTOR's ADVICE STRICTLY : " + DoctorAdvice);
        else
             JOptionPane.showMessageDialog(null,"The Patient is completly normal..... \n The Patient can go back home");
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_searchActionPerformed

    private void jButton_Group_checkingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_Group_checkingActionPerformed

         grouping s = new grouping();
         s.setVisible(true);
    }//GEN-LAST:event_jButton_Group_checkingActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            /* Set the Nimbus look and feel */
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
            
            /*try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(PatientsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(PatientsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(PatientsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(PatientsForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }*/
            //</editor-fold>
            
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new PatientsForm().setVisible(true);
                }
            });
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PatientsForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PatientsForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PatientsForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PatientsForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>
       
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_Group_checking;
    private javax.swing.JButton jButton_delete;
    private javax.swing.JButton jButton_new;
    private javax.swing.JButton jButton_photo;
    private javax.swing.JButton jButton_save;
    private javax.swing.JButton jButton_update;
    private com.toedter.calendar.JDateChooser jDateChooser_dob;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_date;
    private javax.swing.JLabel jLabel_time;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_advice;
    private javax.swing.JTextField jTextField_glucose;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_name;
    private javax.swing.JTextField jTextField_pressure;
    private javax.swing.JTextField jTextField_reminder;
    private javax.swing.JTextField jTextField_saturation;
    private javax.swing.JTextField jTextField_search;
    private javax.swing.JTextField jTextField_temperature;
    private javax.swing.JLabel label_photo;
    // End of variables declaration//GEN-END:variables

    
}
