package it.enea.lecop.eca.view;

import it.enea.lecop.eca.data.MenuItemDao;
import it.enea.lecop.eca.login.Login;
import it.enea.lecop.eca.model.MenuItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class MenuItemBean implements Serializable{
	
	@Inject 
	private Instance<Login> loginUser;
	
	@Inject 
	private MenuItemDao midao;
	
	
	private List<MenuItem> items=null;
	
	
	public List<MenuItem> getAllaccess()
	{
		Login login =loginUser.get();
		if(!login.isLoggedIn())
		{
			return new ArrayList<MenuItem>();
		}
		
		if (items==null || items.isEmpty())
		{
			items=midao.findAllaccess(login.getCurrentUser().getUsername(), login.getCurrentDomain().getName());
		    if (items==null)
		    {
		    	System.out.println("La query restituisce null");
		    	System.out.flush();
		    	items=new ArrayList<MenuItem>();
		    }
		}
		
		return items;
		
	}
	
	public MenuItemBean() {
		
	}

}
