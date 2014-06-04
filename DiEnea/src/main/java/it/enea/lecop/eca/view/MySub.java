package it.enea.lecop.eca.view;

import java.util.Collection;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

class MySub extends Window implements Button.ClickListener{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Double val;
	int result=0;
	boolean modal;
	 Button ok = new Button("Ok");
	 Button cancel = new Button("Cancel");
	 Button yes = new Button("Yes");
	 Button no = new Button("No");
	 Button.ClickListener listener;
	 Window dialog;
	 UI parent;
	 ClickEvent event;
	 HorizontalLayout buttonL= new HorizontalLayout();
	 VerticalLayout content = new VerticalLayout();
	 
	 
	public ClickEvent getEvent() {
		return event;
	}
	public void setEvent(ClickEvent event) {
		this.event = event;
	}
	public Button.ClickListener getListener() {
		return listener;
	}
	public MySub setListener(Button.ClickListener listener) {
		this.listener = listener;
		return this;
	}
	public  int getResult() {
		return result;
	}
	
	public MySub(final UI parent,boolean ismodal,String title,Double meritoVal,String msg,int mode)
	{
		this(parent, ismodal, title, meritoVal, msg, mode, null);
	}
	
	public MySub(final UI parent,boolean ismodal,String title,Double meritoVal,String msg,int mode,Button.ClickListener listener) {
        super(title); // Set window caption
        setCaption(title);
        center();
        this.listener=listener;
        modal=ismodal;
         val=meritoVal;
         this.parent=parent;
        // Some basic content for the window
        
        if (val!=null)content.addComponent(new Label(msg +val));
        else content.addComponent(new Label(msg));
        
        
        
        setModal(modal);
        content.setMargin(true);
        content.addComponent(buttonL);
        setContent(content);
        
        // Disable the close button
        setClosable(false);
        dialog=this;
        dialog.setEnabled(false);
	    dialog.setVisible(false);
      
        if(mode==1)
        {	
        	buttonL.addComponent(ok);
        	 // Trivial logic for closing the sub-window
           
            ok.addClickListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    close(); // Close the sub-window
                    result = 1;
                    parent.removeWindow(dialog);
                    if (getListener()!=null)  getListener().buttonClick(getEvent());
                }
            });	
          
        }else if(mode==2)
        {
        	 // Trivial logic for closing the sub-window
            
        	  buttonL.addComponent(ok);
              buttonL.addComponent(cancel);
        	
            ok.addClickListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    close(); // Close the sub-window
                    result = 1;
                    parent.removeWindow(dialog);
                    if (getListener()!=null) getListener().buttonClick(getEvent());
                }
            });	
            
           
            cancel.addClickListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    close(); // Close the sub-window
                    result = 2;
                    parent.removeWindow(dialog);
                }
            });
            
          
        }else if(mode==3)
        {
        	 // Trivial logic for closing the sub-window
            
        	buttonL.addComponent(yes);
            buttonL.addComponent(no);
        	
            yes.addClickListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    close(); // Close the sub-window
                    result = 3;
                    parent.removeWindow(dialog);
                    if (getListener()!=null) getListener().buttonClick(getEvent());
                }
            });	
            
            
            
            no.addClickListener(new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    close(); // Close the sub-window
                    result = 4;
                    parent.removeWindow(dialog);
                }
            });
            
            
        }
    }
	@Override
	public void buttonClick(ClickEvent event) {
		// TODO Auto-generated method stub
		// listener.buttonClick(event);
		    center();
		    setEvent(event);
		    show();
	     
	}
	
	public void show()
	{
		dialog.setEnabled(true);
	    dialog.setVisible(true);
	    dialog.setImmediate(true);
        parent.addWindow(dialog);
	}
	
	
}
