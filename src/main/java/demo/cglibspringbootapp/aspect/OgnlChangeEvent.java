package demo.cglibspringbootapp.aspect;


public class OgnlChangeEvent {

   private final String ognl;
   private final Object oldValue;
   private final Object newValue;


   public OgnlChangeEvent(String theOgnl, Object theOldValue, Object theNewValue) {
      ognl = theOgnl;
      oldValue = theOldValue;
      newValue = theNewValue;
   }

   public String getOgnl() {
      return ognl;
   }

   public Object getOldValue() {
      return oldValue;
   }

   public Object getNewValue() {
      return newValue;
   }
}
