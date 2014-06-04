package it.enea.lecop.eca.login;

import it.enea.lecop.eca.data.MenuItemDao;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named
public class ResoucesLogin  implements Serializable{

	@Inject 
	Instance<Login> loginUser;
	
	@Inject
	MenuItemDao  menudao;
	
	
	List<String> forbiddenLinks;

	/**
	 * contyrolla se path e tra le risorse non permesse
	 * @param path
	 * @return true se il path è vietato
	 */
	public boolean checkForbiddenResources(String path) {
	    Login login;
		
		if ((login=loginUser.get()) != null)
		{
			if (forbiddenLinks==null)
			{
				forbiddenLinks=	menudao.getForbiddenLink(login.getCurrentUser().getUsername(),login.getCurrentDomain().getName());
			}
			
			for(String flink: forbiddenLinks)
			{
				if (path.contains(flink))
				{
					//System.out.println("--->>FORBIDDEN "+path+" contains" + flink);
					//System.out.flush();
					
					return true;
				}
			}
			
			return false;
			
		}else return true;
		
		

	}
	
}
