package model;

import java.io.Serializable;
import java.sql.Date;

public class Saving  implements Serializable {
    /**
    *
    */
   private static final long serialVersionUID = 1L;
   private int id;
   private float balance;
   private int status;
   private int type;
   private float interest;
   private int time;
   private Date createTime;


   public Saving() {
   }

   public Saving(int id, float balance, int status, int type, float interest, int time, Date createTime) {
       this.id = id;
       this.balance = balance;
       this.status = status;
       this.type = type;
       this.interest = interest;
       this.time = time;
       this.createTime = createTime;
   }

   public int getId() {
       return this.id;
   }

   public void setId(int id) {
       this.id = id;
   }

   public float getBalance() {
       return this.balance;
   }

   public void setBalance(float balance) {
       this.balance = balance;
   }

   public int getStatus() {
       return this.status;
   }

   public void setStatus(int status) {
       this.status = status;
   }

   public int getType() {
       return this.type;
   }

   public void setType(int type) {
       this.type = type;
   }

   public float getInterest() {
       return this.interest;
   }

   public void setInterest(float interest) {
       this.interest = interest;
   }

   public int getTime() {
       return this.time;
   }

   public void setTime(int time) {
       this.time = time;
   }

   public Date getCreateTime() {
       return this.createTime;
   }

   public void setCreateTime(Date createTime) {
       this.createTime = createTime;
   }

}

