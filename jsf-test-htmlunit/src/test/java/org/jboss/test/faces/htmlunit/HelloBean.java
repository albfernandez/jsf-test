package org.jboss.test.faces.htmlunit;

import java.io.Serializable;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("HelloBean")
@SessionScoped
public class HelloBean implements Serializable {

   private static final long serialVersionUID = -6109769270535586954L;
	
   private String name = "testname";
    
   public HelloBean() {}
   
   public String getName() { return name;}
   
   public void setName(String name) { this.name = name; }
   
}
