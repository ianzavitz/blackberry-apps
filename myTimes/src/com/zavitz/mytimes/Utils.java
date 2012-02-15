package com.zavitz.mytimes;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class Utils {

	
	public static void inform(final String s) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.inform(s);
			}
		});
	}
	
	public static String[] split(String original, String separator) {
        java.util.Vector nodes = new java.util.Vector();
        int index = original.indexOf(separator);
        while(index>=0) {
            nodes.addElement( original.substring(0, index) );
            original = original.substring(index+separator.length());
            index = original.indexOf(separator);
        }
        nodes.addElement( original );
        
        String[] result = new String[ nodes.size() ];
        if( nodes.size()>0 )
            for(int loop=0; loop<nodes.size(); loop++)
                result[loop] = (String)nodes.elementAt(loop);
        
        return result;
    }
	
	public static String replace(String text, String searchString, String replacementString) {
        StringBuffer sBuffer = new StringBuffer();
        int pos = 0;
    
        while ((pos = text.indexOf(searchString)) != -1)
        {
            sBuffer.append(text.substring(0, pos) + replacementString);
            text = text.substring(pos + searchString.length());
        }
    
        sBuffer.append(text);
        return sBuffer.toString();
    }
	
}
