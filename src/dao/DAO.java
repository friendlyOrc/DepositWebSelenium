package dao;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;

public class DAO {
	public static Connection con;
	
	public DAO(){
            if(con == null){
                String dbUrl = "jdbc:mysql://localhost/bank?autoReconnect=true&useSSL=false";
                String dbClass = "com.mysql.jdbc.Driver";

                try {
                    Class.forName(dbClass);
                    con = DriverManager.getConnection (dbUrl, "root", "G34r1#c42&");
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
	}
	
	public boolean rollingInTheBack() {
		String sql1 = "delete from `transaction`";
		String sql2 = "delete from `saving`";
		String sql3 = "delete from `account`";
		String sql4 = "insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(1, 'Phạm Trung Kiên', '1999-07-20', 'ABC', '0123456789', 'kienpt', '123', \"kienpt@gmail.com\", 0)";
		String sql5 = "insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(2, 'LamVM', '1999-07-20', 'ABC', '0123456789', 'lamvm', '123', \"lamvm@gmail.com\", 1)";
		String sql6 = "insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(3, 'LamNT', '1999-07-20', 'ABC', '0123456789', 'lamnt', '123', \"lamnt@gmail.com\", 0)";
		String sql7 = "insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(4, 'Phạm Văn Kiên', '1999-07-20', 'ABC', '0123456789', 'kienpv', '123', \"kienpv@gmail.com\", 0)";
		String sql8 = "insert into `account`(id, `name`, `dob`, `address`, `idcard`, email, `sex`) values(5, 'Khách hàng', '1999-07-20', 'ABC', '0123456789',  \"kh1@gmail.com\", 1)";
		String sql9 = "insert into `saving`(id, `type`, balance, `status`, interest, `time`, createTime, accountid, staffid) \r\n"
				+ "values(1, 1, 1000000, 1, 4.0, 6, '2020-02-02', 5, 2),\r\n"
				+ "(2, 1, 1000000, 1, 4.0, 6, '2020-02-02', 2, 1),\r\n"
				+ "(3, 1, 1000000, 0, 4.0, 6, '2020-02-02', 2, 1)";
		int rs = 0;
        try{
            PreparedStatement ps = con.prepareStatement(sql1);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql2);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql3);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql4);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql5);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql6);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql7);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql8);
            rs += ps.executeUpdate();
            
            ps = con.prepareStatement(sql9);
            rs += ps.executeUpdate();
            
            
            if(rs == 9) return true;
            else return false;
        }catch(Exception e) {
        	e.printStackTrace();
        }
		return false;
	}
	
	public static void Backupdbtosql() {
	    try {

	        /*NOTE: Getting path to the Jar file being executed*/
	        /*NOTE: YourImplementingClass-> replace with the class executing the code*/
	        CodeSource codeSource = DAO.class.getProtectionDomain().getCodeSource();
	        File jarFile = new File(codeSource.getLocation().toURI().getPath());
	        String jarDir = jarFile.getParentFile().getPath();


	        /*NOTE: Creating Database Constraints*/
	        String dbName = "bank";
	        String dbUser = "root";
	        String dbPass = "G34r1#c42&";

	        /*NOTE: Creating Path Constraints for folder saving*/
	        /*NOTE: Here the backup folder is created for saving inside it*/
	        String folderPath = jarDir + "\\backup";
	        /*NOTE: Creating Folder if it does not exist*/
	        File f1 = new File(folderPath);
	        f1.mkdir();

	        /*NOTE: Creating Path Constraints for backup saving*/
	        /*NOTE: Here the backup is saved in a folder called backup with the name backup.sql*/
	         String savePath = "\"" + jarDir + "\\backup\\" + "backup.sql\"";

	        /*NOTE: Used to create a cmd command*/
	        String executeCmd = "C:\\Program Files\\MySQL\\MySQL Workbench 8.0\\mysqldump.exe -u" + dbUser + " -p" + dbPass + " " + dbName + " -r " + savePath;

	        /*NOTE: Executing the command here*/
	        Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
	        int processComplete = runtimeProcess.waitFor();

	        /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
	        if (processComplete == 0) {
	            System.out.println("Backup Complete");
	        } else {
	            System.out.println("Backup Failure");
	        }

	    } catch (URISyntaxException | IOException | InterruptedException ex) {
	        JOptionPane.showMessageDialog(null, "Error at Backuprestore" + ex.getMessage());
	    }
	}
	
	public static void Restoredbfromsql(String s) {
        try {
            /*NOTE: String s is the mysql file name including the .sql in its name*/
            /*NOTE: Getting path to the Jar file being executed*/
            /*NOTE: YourImplementingClass-> replace with the class executing the code*/
            CodeSource codeSource = DAO.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();

            /*NOTE: Creating Database Constraints*/
	        String dbName = "bank";
	        String dbUser = "root";
	        String dbPass = "G34r1#c42&";

            /*NOTE: Creating Path Constraints for restoring*/
            String restorePath = jarDir + "\\backup" + "\\" + s;

            /*NOTE: Used to create a cmd command*/
            /*NOTE: Do not create a single large string, this will cause buffer locking, use string array*/
            String[] executeCmd = new String[]{"C:\\Program Files\\MySQL\\MySQL Workbench 8.0\\mysql", dbName, "-u" + dbUser, "-p" + dbPass, "-e", " source " + restorePath};

            /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            /*NOTE: processComplete=0 if correctly executed, will contain other values if not*/
            if (processComplete == 0) {
                JOptionPane.showMessageDialog(null, "Successfully restored from SQL : " + s);
            } else {
                JOptionPane.showMessageDialog(null, "Error at restoring");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error at Restoredbfromsql" + ex.getMessage());
        }

    }
}
