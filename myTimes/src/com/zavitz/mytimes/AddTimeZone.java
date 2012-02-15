package com.zavitz.mytimes;

import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class AddTimeZone extends MainScreen implements FieldChangeListener {
	
	private VerticalFieldManager _container;
	public SpecialEdit searchTerm;
	public TimeListField listField;
	private HomeScreen screen;

	public AddTimeZone(HomeScreen pScreen) {
		screen = pScreen;
		VerticalFieldManager internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR) {
            public void paintBackground(Graphics g) {
                g.clear();
                int color = g.getColor();
                g.setColor(0x0);
                g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
                g.setColor(color);
            }

            protected void sublayout(int width, int height) {
                super.sublayout( Display.getWidth(), Display.getHeight());
                setExtent( Display.getWidth(), Display.getHeight());
            }
        };        
        TitleField title = new TitleField();
        internalManager.add(title);
        searchTerm = new SpecialEdit();
        searchTerm.setChangeListener(this);
        internalManager.add(searchTerm);
        
        _container = new VerticalFieldManager( Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR );
        internalManager.add( _container );
        super.add( internalManager );
        
        listField = new TimeListField(this);
        
        add(listField);
        
        if(PersistentUtils.firstRun) {
        	PersistentUtils.firstRun = false;
        	UiApplication.getUiApplication().invokeLater(new Runnable() {
        		public void run() {
		        	Dialog dialog = new Dialog(Dialog.D_OK, "Start typing your search terms to narrow your results", Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.INFORMATION), 0);
		        	UiApplication.getUiApplication().pushScreen(dialog);
        		}
        	});
        }
	}
	
	private MenuItem addTimeZone = new MenuItem("Add Zone",110,1) {
		public void run() {
			zonePressed();
		}
	};
	
	public void makeMenu(Menu menu, int context) {
		if(listField.getSize() > 0 && listField.getSelectedIndex() > -1)
			menu.add(addTimeZone);
		super.makeMenu(menu, context);
	}
	
	public boolean onClose() {
		setDirty(false);
		return super.onClose();
	}
	
	public void zonePressed() {
		screen.addItem(Utils.replace((String)listField.get(listField,listField.getSelectedIndex())," ","_"));
		close();
	}
	
	public void add(Field field) {
		_container.add(field);
	}

	public void fieldChanged(Field field, int context) {
		if(field instanceof AutoTextEditField) {
			String text = searchTerm.getText();
			listField.setTerm(text);
		}
	}
	
	public class SpecialEdit extends AutoTextEditField {
		
		public SpecialEdit() {
			super("Location: ","");
		}
		
    	public void paint(Graphics g) {
    		g.setColor(0x00FFFFFF);
    		super.paint(g);
    	}
    	
    	public boolean keyChar(char key, int status, int time) {
    		if(key == Characters.ENTER) {
    			listField.keyChar(key, status, time);
    			return true;
    		}
    		return super.keyChar(key, status, time);
    	}
    	
    }
	
}
