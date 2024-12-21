//$Id$
package org.jboss.test.faces.stub.faces;

import jakarta.faces.FacesException;
import jakarta.faces.FactoryFinder;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseListener;
import jakarta.faces.lifecycle.Lifecycle;

public class StubLifecycle extends Lifecycle
{
   public static final Lifecycle INSTANCE = new StubLifecycle();

   public StubLifecycle()
   {
      StubLifecycleFactory.setLifecycle(this);
      FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, StubLifecycleFactory.class.getName());
   }

   @Override
   public void addPhaseListener(PhaseListener pl)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void execute(FacesContext ctx) throws FacesException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public PhaseListener[] getPhaseListeners()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void removePhaseListener(PhaseListener pl)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void render(FacesContext ctx) throws FacesException
   {
      throw new UnsupportedOperationException();
   }
}
