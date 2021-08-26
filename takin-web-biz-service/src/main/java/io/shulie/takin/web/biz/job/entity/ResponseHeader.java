/**
  * Copyright 2021 bejson.com 
  */
package io.shulie.takin.web.biz.job.entity;

/**
 * Auto-generated: 2021-07-14 12:5:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ResponseHeader {

    private int status;
    private int QTime;
    private Params params;
    public void setStatus(int status) {
         this.status = status;
     }
     public int getStatus() {
         return status;
     }

    public void setQTime(int QTime) {
         this.QTime = QTime;
     }
     public int getQTime() {
         return QTime;
     }

    public void setParams(Params params) {
         this.params = params;
     }
     public Params getParams() {
         return params;
     }

}