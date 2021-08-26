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
public class Response {

    private int numFound;
    private int start;
    private List<Docs> docs;
    public void setNumFound(int numFound) {
         this.numFound = numFound;
     }
     public int getNumFound() {
         return numFound;
     }

    public void setStart(int start) {
         this.start = start;
     }
     public int getStart() {
         return start;
     }

    public void setDocs(List<Docs> docs) {
         this.docs = docs;
     }
     public List<Docs> getDocs() {
         return docs;
     }

}