package org.jboss.test.faces.stub.faces;

import java.io.IOException;

import jakarta.faces.application.StateManager;
import jakarta.faces.context.FacesContext;

@SuppressWarnings("deprecation")
public class StubStateManager extends StateManager
{
   /*MZ
   @Override
   public Object saveView(FacesContext ctx)
   {
      return null;
   }*/

   @Override
   public void writeState(FacesContext ctx, Object state) throws IOException
   {
   }

   /*
   @Override
   public SerializedView saveSerializedView(FacesContext ctx)
   {
      return null;
   }

   @Override
   protected Object getTreeStructureToSave(FacesContext ctx)
   {
      return null;
   }

   @Override
   protected Object getComponentStateToSave(FacesContext ctx)
   {
      return null;
   }

   @Override
   public void writeState(FacesContext ctx, SerializedView sv)
      throws IOException
   {
   }

   @Override
   public UIViewRoot restoreView(FacesContext ctx, String x, String y)
   {
      return null;
   }

   @Override
   protected UIViewRoot restoreTreeStructure(FacesContext ctx, String x, String y)
   {
      return null;
   }

   @Override
   protected void restoreComponentState(FacesContext ctx, UIViewRoot viewRoot, String x)
   {
   }
   */
}
