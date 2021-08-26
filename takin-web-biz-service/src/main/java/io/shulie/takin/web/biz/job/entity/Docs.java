/**
  * Copyright 2021 bejson.com 
  */
package io.shulie.takin.web.biz.job.entity;
import java.util.List;

/**
 * Auto-generated: 2021-07-14 12:5:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Docs {

    private String id;
    private String g;
    private String a;
    private String v;
    private String p;
    private long timestamp;
    private List<String> ec;
    private List<String> tags;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setG(String g) {
         this.g = g;
     }
     public String getG() {
         return g;
     }

    public void setA(String a) {
         this.a = a;
     }
     public String getA() {
         return a;
     }

    public void setV(String v) {
         this.v = v;
     }
     public String getV() {
         return v;
     }

    public void setP(String p) {
         this.p = p;
     }
     public String getP() {
         return p;
     }

    public void setTimestamp(long timestamp) {
         this.timestamp = timestamp;
     }
     public long getTimestamp() {
         return timestamp;
     }

    public void setEc(List<String> ec) {
         this.ec = ec;
     }
     public List<String> getEc() {
         return ec;
     }

    public void setTags(List<String> tags) {
         this.tags = tags;
     }
     public List<String> getTags() {
         return tags;
     }

}