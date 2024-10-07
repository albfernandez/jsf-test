package org.jboss.test.faces.mock.factory;

import java.io.Serializable;


public class SerializableNoOp implements NoOp, Serializable {

  private static final long serialVersionUID = 7434976328690189159L;
  public static final SerializableNoOp SERIALIZABLE_INSTANCE = new SerializableNoOp();

}
