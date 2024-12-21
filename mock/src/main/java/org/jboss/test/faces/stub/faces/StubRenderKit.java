package org.jboss.test.faces.stub.faces;

import java.io.OutputStream;
import java.io.Writer;

import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.Renderer;
import jakarta.faces.render.ResponseStateManager;

public class StubRenderKit extends RenderKit
{
   public static final StubRenderKit INSTANCE = new StubRenderKit();

   @Override
   public void addRenderer(String x, String y, Renderer renderer)
   {
      // Do nothing
   }

   @Override
   public Renderer getRenderer(String x, String y)
   {
      return null;
   }

   @Override
   public ResponseStateManager getResponseStateManager()
   {
      return new StubResponseStateManager();
   }

   @Override
   public ResponseWriter createResponseWriter(Writer writer, String x, String y)
   {
      return new StubResponseWriter();
   }

   @Override
   public ResponseStream createResponseStream(OutputStream stream)
   {
      throw new UnsupportedOperationException();
   }
}
