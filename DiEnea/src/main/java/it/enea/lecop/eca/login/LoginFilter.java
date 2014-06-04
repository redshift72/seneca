package it.enea.lecop.eca.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * questo è utile se si punta manualmente su un altra pagina senza aver fatto il login
 * Ma quando ci solo le chiamate alla action che pilotano la navigazione, la view che viene rappresentata sul browser avra' la stessa url
 *e quindi non farà scattare il filtro. In questo caso si parla di forward
 * 
 * @author fab
 *
 */
@WebFilter(urlPatterns={"/*"})
public class LoginFilter implements Filter {

	
	private FilterConfig filterConfig = null;
	
	@Inject 
     private Instance<Login> loginUser;
     
	@Inject
	  private Instance<ResoucesLogin> res;
	
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
	   HttpServletResponse response = (HttpServletResponse) arg1;
	//    HttpSession session = request.getSession(false);
	    Login user = loginUser.get();
	    
	   
	    System.out.println(">>req uri: "+request.getRequestURI());
    	System.out.println(">>req path traslated: "+request.getPathTranslated());
    	System.out.println(">>req url: "+request.getRequestURL());
    	System.out.println(">>req servlet path: "+request.getServletPath());
    	String context=request.getContextPath();
    	System.out.println(">>req context path: "+context);
    	// arg2.doFilter(arg0, arg1);
	   /*
	    StringWriter sw = new StringWriter();
	    PrintWriter writer = new PrintWriter(sw);
	     
	    writer.println("===============");
	    writer.println("Filter intercepted!");
	    writer.println("===============");
	     
	    // Log the resulting string
	    writer.flush();
	    filterConfig.getServletContext().
	    log(sw.getBuffer().toString());
	    */
	    
    	String path=request.getRequestURI();
	    if (user == null || ! user.isLoggedIn()) {
	    	
	    	//String path=request.getServletPath();
	    	
	    	
	    if (path.contains("index.html")|| path.contains("index.xhtml")|| path.contains("index.jsf")|| path.contains("resources")|| path.contains("resource")
	    	   || path.contains("VAADIN")  || path.contains("HEARTBEAT") || path.contains("UIDL") || path.contains("vaadinUI")|| path.contains("vUser") 
	    	   || path.contains("compEdifici") || path.contains("/viewvaadin/back.html")  )
	    	
	    	{
	    		System.out.println(">>>>>>>FILTRO: -->>>PASSOOO, trattasi di prima pagina o contenuti");
	    		arg2.doFilter(arg0, arg1);
	    		return;
	    	}
	    	
	    	
	    /*		
	    	System.out.println(">>>>>>>FILTRO: -->>>BLOCCO e RIDIRIGO");
	    	response.sendRedirect(context+"/index.jsf"); // No logged-in user found, so redirect to login page.
	   */
	    
	    
	    	
    	System.out.println(">>>>>>>FILTRO: -->>>BLOCCO ");
    	response.sendRedirect(context+"/index.jsf"); // No logged-in user found, so redirect to login page.
        return;
	    } else {
	    	System.out.println(">>>>>>>FILTRO: -->>>LOGGATOOOOO!!!! ");
	    	ResoucesLogin Res=res.get();
	    	if(Res==null) 
	    	{    System.out.println(">>>>>>>FILTRO: -->>>BLOCCO e RIDIRIGO!! RISORSE DI SESSIONE MANCANTI!!!");
	    	     System.out.flush();
	    		response.sendRedirect(context+"/index.jsf");
	    		return;
	    	}else
	    	{
	    		if (Res.checkForbiddenResources(path))
                   {
	    			   System.out.println(">>>>>>>FILTRO: -->>>BLOCCO e RIDIRIGO!! IL PATH E' PROIBITO");
		    	       System.out.flush();
	    			   response.sendRedirect(context+"/index.jsf");
                   }
	    		else 
	    			{
	    			
	    			System.out.println(">>>>>>>FILTRO: -->>>PASSO!! IL PATH E' CONSENTITO");
		    	    System.out.flush();
	    			arg2.doFilter(arg0, arg1);
	    			return;
	    			}
	    	}
	    	
	    	 // Logged-in user found, so just continue request.
	    }
         
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
		this.filterConfig = arg0;
	}
	
}

