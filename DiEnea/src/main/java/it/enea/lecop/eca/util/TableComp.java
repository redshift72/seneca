package it.enea.lecop.eca.util;

import com.vaadin.ui.Table;

public class TableComp extends Table{

	void addCol(String name, Class<?> cl, Object defaultValue)
	{
		addContainerProperty(name, cl,  defaultValue);
	}
	
}
