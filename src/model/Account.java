package model;

import java.io.Serializable;
import java.sql.Date;

public class Account implements Serializable {
    /**
    *
    */
   private static final long serialVersionUID = 1L;
   private int id;
   private String name;
   private Date dob;
   private int sex;
   private String address;
   private String idcard;
   private String email;
   private String username;
   private String password;
   
   public Account() {
   }

   public Account(int id, String name, Date dob, int sex, String address, String idcard, String email, String username, String password) {
       this.id = id;
       this.name = name;
       this.dob = dob;
       this.sex = sex;
       this.address = address;
       this.idcard = idcard;
       this.email = email;
       this.username = username;
       this.password = password;
   }

   public int getId() {
       return this.id;
   }

   public void setId(int id) {
       this.id = id;
   }

   public String getName() {
       return this.name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public Date getDob() {
       return this.dob;
   }

   public void setDob(Date dob) {
       this.dob = dob;
   }

   public int getSex() {
       return this.sex;
   }

   public void setSex(int sex) {
       this.sex = sex;
   }

   public String getAddress() {
       return this.address;
   }

   public void setAddress(String address) {
       this.address = address;
   }

   public String getIdcard() {
       return this.idcard;
   }

   public void setIdcard(String idcard) {
       this.idcard = idcard;
   }

   public String getEmail() {
       return this.email;
   }

   public void setEmail(String email) {
       this.email = email;
   }

   public String getUsername() {
       return this.username;
   }

   public void setUsername(String username) {
       this.username = username;
   }

   public String getPassword() {
       return this.password;
   }

   public void setPassword(String password) {
       this.password = password;
   }

}
