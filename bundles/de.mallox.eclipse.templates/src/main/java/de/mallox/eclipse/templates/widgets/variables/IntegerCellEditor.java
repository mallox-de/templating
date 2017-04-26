package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This cell editor ensures that only Integer values are supported
 * 
 * Source: org.eclipse.emf.edit.ui.provider.PropertyDescriptor.IntegerCellEditor
 */
public class IntegerCellEditor extends TextCellEditor
{
  public IntegerCellEditor(Composite composite)
  {
    super(composite);
    setValidator
      (new ICellEditorValidator()
       {
         public String isValid(Object object)
         {
           if (object instanceof Integer)
           {
             return null;
           }
           else
           {
             String string = (String)object;
             try
             {
               Integer.parseInt(string);
               return null;
             }
             catch (NumberFormatException exception)
             {
               return "Input must be an integer, but was: " + string;
             }
           }
         }
       });
  }

  @Override
  public Object doGetValue()
  {
    return Integer.parseInt((String)super.doGetValue());
  }

  @Override
  public void doSetValue(Object value)
  {
    super.doSetValue(value.toString());
  }
}
