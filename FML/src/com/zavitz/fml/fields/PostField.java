package com.zavitz.fml.fields;

import com.zavitz.fml.FMLCommentScreen;
import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLVote;
import com.zavitz.fml.data.FMLPost.Post;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

public class PostField extends HorizontalFieldManager {
	
	private static final Bitmap comments;
	private VerticalFieldManager functions;
	private LabelField ydi, fyl;
	private boolean showComments = false;
	public static int paints = 1;
	public static long timer = 1L;
	
	private Post _post;
	
	static {
		comments = Bitmap.getBitmapResource("comments.png");
	}
	
	public PostField(Post post) {
		this(post, true);
	}
	
	public PostField(Post post, boolean flag) {
		super(FOCUSABLE);
		showComments = flag;
		_post = post;
		
		LabelField text = new LabelField(post.text) {
			public void paint(Graphics g) {
				g.setColor(Config.GRAY);
				super.paint(g);
			}
			public boolean isSelectionCopyable() {
				return false;
			}
		};
		text.setFont(text.getFont().derive(Font.PLAIN, Config.FONT_SIZE));
		
		fyl = new LabelField(_post.agreed ? _post.agree : "FYL", DrawStyle.HCENTER | FOCUSABLE) { 
			boolean b = false;
			public boolean isSelectionCopyable() {
				return false;
			}
			public void drawFocus(Graphics g, boolean on) { 
				b = true;
				paint(g); 
				b = false;
			} 
			public void paint(Graphics g) { 
				g.setBackgroundColor(Config.DEFAULT_BLUE);
				g.clear();
				g.setColor(b ? Config.WHITE : Config.LIGHTER_BLUE); 
				super.paint(g); 
			} 
			public boolean navigationClick(int status, int time) {
				fuckYourLife();
				return true;
			}
			public boolean keyChar(char character, int status, int time) {
				if(character == Characters.ENTER) {
					fuckYourLife();
					return true;
				}
				return super.keyChar(character,status,time);
			}
		};
		ydi = new LabelField(_post.deserved ? _post.deserve : "YDI", DrawStyle.HCENTER | FOCUSABLE) { 
			boolean b = false;
			public void drawFocus(Graphics g, boolean on) { 
				b = true;
				paint(g); 
				b = false;
			} 
			public boolean isSelectionCopyable() {
				return false;
			}
			public void paint(Graphics g) { 
				g.setBackgroundColor(Config.DEFAULT_BLUE);
				g.clear();
				g.setColor(b ? Config.WHITE : Config.LIGHTER_BLUE); 
				super.paint(g); 
			} 
			public boolean navigationClick(int status, int time) {
				youDeservedIt();
				return true;
			}
			public boolean keyChar(char character, int status, int time) {
				if(character == Characters.ENTER) {
					youDeservedIt();
					return true;
				}
				return super.keyChar(character,status,time);
			}
		};
		
		fyl.setFont(fyl.getFont().derive(Font.BOLD, Config.FONT_SIZE));
		ydi.setFont(fyl.getFont());
		
		Comments c = new Comments();
		functions = new Functions();
		
		functions.add(fyl);
		functions.add(ydi);
		if(showComments)
			functions.add(c);
		
		add(text);
		add(functions);
	}
	
	public PostField getPostField() {
		return this;
	}
	
	public boolean canYDI() {
		return ydi.getText().equals("YDI");
	}
	
	public boolean canFYL() {
		return fyl.getText().equals("FYL");
	}
	
	public void youDeservedIt() {
		_post.deserved = true;
		new FMLVote(ydi, _post.id, false, _post.deserve);
	}
	
	public void fuckYourLife() {
		_post.agreed = true;
		new FMLVote(fyl, _post.id, true, _post.agree);
	}
	
	public Post getPost() {
		return _post;
	}
	
	public int getPreferredWidth() {
		return Display.getWidth();
	}
	
	public int getPreferredHeight() {
		int ideal = 20 + getField(0).getContentHeight();
		int unfortunately = 20 + getField(1).getPreferredHeight();
		return ideal > unfortunately ? ideal : unfortunately;
	}
	
	public int boxHeight() {
		return getField(0).getContentHeight();
	}
	
	public void paint(Graphics g) {
		long l = System.currentTimeMillis();
		paints ++;
		g.setColor(Config.WHITE);
		g.fillRoundRect(5, 5, getPreferredWidth() - 10, getContentHeight() - 10, 15, 15);
		g.setColor(Config.DEFAULT_BLUE);
		g.fillRoundRect(getPreferredWidth() - (5 + getField(1).getPreferredWidth()), 5, getField(1).getPreferredWidth(), getContentHeight() - 10, 15, 15);
		g.fillRect(getPreferredWidth() - (5 + getField(1).getPreferredWidth()), 5, 25, getContentHeight() - 10);
		subpaint(g);
		timer += System.currentTimeMillis() - l;
	}

	protected void sublayout(int w, int h) {
		Field text = getField(0);
		layoutChild(text, getPreferredWidth() - 20 - getField(1).getPreferredWidth(), text.getHeight());
		setPositionChild(text, 10, 10);
		
		Field functions = getField(1);
		layoutChild(functions, functions.getPreferredWidth(), functions.getPreferredHeight());
		setPositionChild(functions, getPreferredWidth() - 60, 10);
		
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
	public boolean isFocus() {
		return functions.getFieldWithFocus() != null;
	}
	
	public class Comments extends HorizontalFieldManager {
		
		private BitmapField icon;
		private LabelField count;
		
		public Comments() {
			count = new LabelField(" " + _post.comments, DrawStyle.HCENTER | FOCUSABLE) { 
				boolean b = false;
				public boolean isSelectionCopyable() {
					return false;
				}
				public void drawFocus(Graphics g, boolean on) { 
					b = true;
					paint(g); 
					b = false;
				} 
				public void paint(Graphics g) { 
					g.setBackgroundColor(Config.DEFAULT_BLUE);
					g.clear();
					if(b) {
						//setFont(getFont().derive(Font.UNDERLINED | Font.BOLD)); 
						g.setColor(Config.WHITE); 
					} else {
						//setFont(getFont().derive(Font.BOLD)); 
						g.setColor(Config.LIGHTER_BLUE); 
					}
					super.paint(g); 
				} 
				public boolean navigationClick(int status, int time) {
					UiApplication.getUiApplication().pushScreen(new FMLCommentScreen(getPost()));
					return true;
				}
				public boolean keyChar(char character, int status, int time) {
					if(character == Characters.ENTER) {
					 	UiApplication.getUiApplication().pushScreen(new FMLCommentScreen(getPost()));
						return true;
					}
					return super.keyChar(character,status,time);
				}
			};
			count.setFont(count.getFont().derive(Font.BOLD, Config.FONT_SIZE));

			icon = new BitmapField(comments);
			
			add(icon);
			add(count);
		}
		
		public void sublayout(int w, int h) {
			Field icon = getField(0);
			layoutChild(icon, 17, 14);
			setPositionChild(icon, 0, 0);
			
			Field count = getField(1);
			layoutChild(count, 38, count.getContentHeight());
			setPositionChild(count, 17, 1);
			
			setExtent(getPreferredWidth(), getPreferredHeight());
		}
				
	}

	public class Functions extends VerticalFieldManager {
		
		public int getPreferredWidth() {
			int max = getField(0).getPreferredWidth();
			if(getField(1).getPreferredWidth() > max) {
				max = getField(1).getPreferredWidth();
				System.out.println("WiDTH: " + "ydi");
			}
			if(getField(2).getPreferredWidth() > max) {
				max = getField(2).getPreferredWidth();
				System.out.println("WiDTH: " + "comments");
			}
			return max + 10;
		}
		public int getPreferredHeight() {
			int min = 15 + getField(0).getContentHeight() + getField(1).getContentHeight();
			if(showComments)
				min += getField(2).getPreferredHeight();
			return boxHeight() > min ? boxHeight() : min;
		}
		public void sublayout(int width, int height) {
			Field fyl = getField(0); // top
			layoutChild(fyl, getPreferredWidth(), fyl.getContentHeight());
			setPositionChild(fyl, (getPreferredWidth() - fyl.getPreferredWidth()) / 2, 0);
	
			Field ydi = getField(1); // middle
			layoutChild(ydi, getPreferredWidth(), ydi.getContentHeight());
			setPositionChild(ydi, (getPreferredWidth() - ydi.getPreferredWidth()) / 2, 5 + fyl.getContentHeight());
	
			if(showComments) {
				Field c = getField(2); // bottom
				layoutChild(c, c.getPreferredWidth(), c.getPreferredHeight());
				setPositionChild(c, (getPreferredWidth() - c.getPreferredWidth()) / 2, getPreferredHeight() - c.getPreferredHeight());
			}
			
			setExtent(getPreferredWidth(), getPreferredHeight());
		}
		
		public PostField getPostField() {
			return getPostField();
		}
	}

}
