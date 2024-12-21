package org.jboss.test.faces.stub.faces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.el.CompositeELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIOutput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.BigDecimalConverter;
import jakarta.faces.convert.BigIntegerConverter;
import jakarta.faces.convert.BooleanConverter;
import jakarta.faces.convert.ByteConverter;
import jakarta.faces.convert.CharacterConverter;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.DoubleConverter;
import jakarta.faces.convert.FloatConverter;
import jakarta.faces.convert.IntegerConverter;
import jakarta.faces.convert.LongConverter;
import jakarta.faces.convert.ShortConverter;
import jakarta.faces.event.ActionListener;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.validator.Validator;

@SuppressWarnings("deprecation")
public class StubApplication extends Application
{
   
   private jakarta.el.CompositeELResolver elResolver;
   private jakarta.el.CompositeELResolver additionalResolvers;
   private Collection locales;
   
   public StubApplication()
   {
     elResolver = new CompositeELResolver();
     additionalResolvers = new CompositeELResolver();
     elResolver.add(additionalResolvers);
     //elResolver.add(EL.EL_RESOLVER);
   }
   
   @Override
   public Object evaluateExpressionGet(FacesContext context, String expression, Class type) throws jakarta.el.ELException 
   {
      return getExpressionFactory().createValueExpression(context.getELContext(), expression, type).getValue(context.getELContext());
   }
   
   @Override
   public void addELContextListener(jakarta.el.ELContextListener elcl) 
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public void addELResolver(jakarta.el.ELResolver r) 
   {
      additionalResolvers.add(r);
   }
   
   @Override
   public UIComponent createComponent(jakarta.el.ValueExpression ve, FacesContext context, String id) throws FacesException 
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public jakarta.el.ELContextListener[] getELContextListeners() 
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public jakarta.el.ELResolver getELResolver() 
   {
      return elResolver;
   }
   
   @Override
   public java.util.ResourceBundle getResourceBundle(FacesContext context, String string) 
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public void removeELContextListener(jakarta.el.ELContextListener elcl) 
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public ActionListener getActionListener()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setActionListener(ActionListener listener)
   {
      throw new UnsupportedOperationException();
   }

   private Locale defaultLocale = Locale.ENGLISH;

   @Override
   public Locale getDefaultLocale()
   {
      return defaultLocale;
   }

   @Override
   public void setDefaultLocale(Locale locale)
   {
      defaultLocale = locale;
   }

   @Override
   public String getDefaultRenderKitId()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setDefaultRenderKitId(String rk)
   {
      throw new UnsupportedOperationException();
   }

   private String msgBundleName;

   @Override
   public String getMessageBundle()
   {
      return msgBundleName;
   }

   @Override
   public void setMessageBundle(String bundleName)
   {
      this.msgBundleName = bundleName;
   }

   private NavigationHandler navigationHandler = null; //new SeamNavigationHandler( new StubNavigationHandler() );

   @Override
   public NavigationHandler getNavigationHandler()
   {
      return navigationHandler;
   }

   @Override
   public void setNavigationHandler(NavigationHandler navigationHandler)
   {
      this.navigationHandler = navigationHandler;
   }

   /*MZ
   @Override
   public PropertyResolver getPropertyResolver()
   {
      throw new UnsupportedOperationException();
   }
   */
   
   /*MZ
   @Override
   public void setPropertyResolver(PropertyResolver pr)
   {
      throw new UnsupportedOperationException();
   }

   private VariableResolver variableResolver = new VariableResolver() { 
      @Override
      public Object resolveVariable(FacesContext ctx, String name) throws EvaluationException
      {
         return null;
      }
   };

   @Override
   public VariableResolver getVariableResolver()
   {
      return variableResolver;
   }

   @Override
   public void setVariableResolver(VariableResolver variableResolver)
   {
      this.variableResolver = variableResolver;
   }
   */
   
   private ViewHandler viewHandler = null; //new SeamViewHandler( new StubViewHandler() );

   @Override
   public ViewHandler getViewHandler()
   {
      return viewHandler;
   }

   @Override
   public void setViewHandler(ViewHandler viewHandler)
   {
      this.viewHandler = viewHandler;
   }

   private StateManager stateManager = null; //new SeamStateManager( new StubStateManager() );

   @Override
   public StateManager getStateManager()
   {
      return stateManager;
   }

   @Override
   public void setStateManager(StateManager stateManager)
   {
      this.stateManager = stateManager;
   }

   @Override
   public void addComponent(String name, String x)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIComponent createComponent(String name) throws FacesException
   {
      // Best guess component creation with a dummy component if it can't be found
      if (name.startsWith("org.jboss.seam.mail.ui") || name.startsWith("org.jboss.seam.excel.ui"))
      {
        try
        {
           return (UIComponent) Class.forName(name).newInstance();
        } 
        catch (Exception e)
        {
           throw new UnsupportedOperationException("Unable to create component " + name);
        }
      }
      else
      {
         // Oh well, can't simply create the component so put a dummy one in its place
         return new UIOutput();
      }
   }

   /*MZ
   @Override
   public UIComponent createComponent(ValueBinding vb, FacesContext fc, String x)
            throws FacesException
   {
      throw new UnsupportedOperationException();
   }
   */

   @Override
   public Iterator getComponentTypes()
   {
      throw new UnsupportedOperationException();
   }

   private final Map<Class, Converter> converters = new HashMap<Class, Converter>();
   {
      converters.put(Integer.class, new IntegerConverter());
      converters.put(Long.class, new LongConverter());
      converters.put(Float.class, new FloatConverter());
      converters.put(Double.class, new DoubleConverter());
      converters.put(Boolean.class, new BooleanConverter());
      converters.put(Short.class, new ShortConverter());
      converters.put(Byte.class, new ByteConverter());
      converters.put(Character.class, new CharacterConverter());
      converters.put(BigDecimal.class, new BigDecimalConverter());
      converters.put(BigInteger.class, new BigIntegerConverter());
   }

   private final Map<String, Converter> convertersById = new HashMap<String, Converter>();
   {
      convertersById.put(IntegerConverter.CONVERTER_ID, new IntegerConverter());
      convertersById.put(LongConverter.CONVERTER_ID, new LongConverter());
      convertersById.put(FloatConverter.CONVERTER_ID, new FloatConverter());
      convertersById.put(DoubleConverter.CONVERTER_ID, new DoubleConverter());
      convertersById.put(BooleanConverter.CONVERTER_ID, new BooleanConverter());
      convertersById.put(ShortConverter.CONVERTER_ID, new ShortConverter());
      convertersById.put(ByteConverter.CONVERTER_ID, new ByteConverter());
      convertersById.put(CharacterConverter.CONVERTER_ID, new CharacterConverter());
      convertersById.put(BigDecimalConverter.CONVERTER_ID, new BigDecimalConverter());
      convertersById.put(BigIntegerConverter.CONVERTER_ID, new BigIntegerConverter());
   }

   @Override
   public void addConverter(String id, String converterClass)
   {
      convertersById.put(id, instantiateConverter(converterClass));
   }

   @Override
   public void addConverter(Class type, String converterClass)
   {
      converters.put(type, instantiateConverter(converterClass));
   }

   private Converter instantiateConverter(String converterClass)
   {
      try
      {
         return (Converter) Class.forName(converterClass).newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Converter createConverter(String id)
   {
      return convertersById.get(id);
   }

   @Override
   public Converter createConverter(Class clazz)
   {
      return converters.get(clazz);
   }

   @Override
   public Iterator getConverterIds()
   {
      return convertersById.keySet().iterator();
   }

   @Override
   public Iterator getConverterTypes()
   {
      return converters.keySet().iterator();
   }

   /*MZ
   @Override
   public MethodBinding createMethodBinding(String expression, Class[] params)
         throws ReferenceSyntaxException
   {
      return null; //new UnifiedELMethodBinding(expression, params);

   }
   */
   
   /*MZ
   @Override
   public ValueBinding createValueBinding(String expression)
         throws ReferenceSyntaxException
   {
      return null; //new UnifiedELValueBinding(expression);
   }*/

   @Override
   public Iterator getSupportedLocales()
   {
      if (locales == null)
      {
         return  Collections.singleton(defaultLocale).iterator();
      }
      else
      {
         return locales.iterator();
      }
   }

   @Override
   public void setSupportedLocales(Collection locales)
   {
      this.locales = locales;
   }

   private final Map<String, Validator> validatorsById = new HashMap<String, Validator>();
   
   @Override
   public void addValidator(String id, String validatorClass)
   {
      validatorsById.put(id, instantiateValidator(validatorClass));
   }

   private Validator instantiateValidator(String validatorClass)
   {
      try
      {
         return (Validator) Class.forName(validatorClass).newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public Validator createValidator(String id) throws FacesException
   {
      return validatorsById.get(id);
   }

   @Override
   public Iterator getValidatorIds()
   {
      return validatorsById.keySet().iterator();
   }

   @Override
   public ExpressionFactory getExpressionFactory()
   {
      return null; //SeamExpressionFactory.INSTANCE;
   }

	@Override
   public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass, Class<?> sourceBaseType, Object source)
   {
   }

   @Override
   public void publishEvent(FacesContext context, Class<? extends SystemEvent> systemEventClass, Object source)
   {
   }
   
}
