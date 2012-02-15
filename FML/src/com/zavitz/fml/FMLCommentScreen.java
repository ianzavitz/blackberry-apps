package com.zavitz.fml;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.zavitz.fml.data.CommentReceiver;
import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLComments;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.FMLComments.Comment;
import com.zavitz.fml.data.FMLPost.Post;
import com.zavitz.fml.fields.CommentField;
import com.zavitz.fml.fields.PostField;
import com.zavitz.fml.fields.TitleField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class FMLCommentScreen extends MainScreen implements CommentReceiver {

	private Post _post;
	private VerticalFieldManager internalManager, manager;
	private LoadingField loadingField;
	private static Bitmap iconBtmp;
	private Timer loadingTimer;
	private Vector _comments;
	private int commentStart = 0;

	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLCommentScreen(Post post) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		_post = post;

		internalManager = new VerticalFieldManager(Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR) {
			public void paintBackground(Graphics g) {
				g.clear();
				int color = g.getColor();
				g.setColor(Config.BACKGROUND_BLUE);
				g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
				g.setColor(color);
			}

			protected void sublayout(int width, int height) {
				super.sublayout(Display.getWidth(), Display.getHeight());
				setExtent(Display.getWidth(), Display.getHeight());
			}
		};
		super.add(internalManager);
		internalManager.add(new TitleField());

		manager = new VerticalFieldManager(Manager.VERTICAL_SCROLL
				| Manager.VERTICAL_SCROLLBAR);
		internalManager.add(manager);

		loadingField = new LoadingField();

		add(new PostField(_post, false));
		if (!PStore.TOKEN.equals(""))
			add(new PostButton());
		new FMLComments(this, _post);
	}

	public void add(Field field) {
		manager.add(field);
	}

	public void makeMenu(Menu menu, int instance) {
		if (manager.getFieldWithFocus() instanceof PostField) {
			final PostField field = (PostField) manager.getFieldWithFocus();
			final Post post = field.getPost();

			if (!PStore.TOKEN.equals("")) {
				if (FMLMainScreen.category.equals("favorites")) {
					menu.add(new MenuItem("Remove Favorite", 110, 1) {
						public void run() {
							getUi().pushScreen(
									new FMLFavoritesScreen(false, post.id));
						}
					});
				} else {
					menu.add(new MenuItem("Make Favorite", 110, 1) {
						public void run() {
							getUi().pushScreen(
									new FMLFavoritesScreen(true, post.id));
						}
					});
				}
			}

			menu.add(new MenuItem("Share Story", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLShareScreen(post.text));
				}
			});

			menu.add(new MenuItem("Copy Story", 110, 1) {
				public void run() {
					Clipboard.getClipboard().put(post.text);
				}
			});

			if (field.canFYL())
				menu.add(new MenuItem("F*** Your Life", 110, 1) {
					public void run() {
						field.fuckYourLife();
					}
				});

			if (field.canYDI())
				menu.add(new MenuItem("You Deserved It", 110, 1) {
					public void run() {
						field.youDeservedIt();
					}
				});
			menu.addSeparator();
		}

		if (PStore.TOKEN.equals(""))
			menu.add(new MenuItem("Login", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLLoginScreen(true));
				}
			});
		else
			menu.add(new MenuItem("Logout", 110, 1) {
				public void run() {
					PStore.TOKEN = "";
					PStore.store();
					getUi().pushScreen(new FMLLoginScreen(false));
				}
			});

		menu.addSeparator();
		menu.add(new MenuItem("Options", 110, 1) {
			public void run() {
				getUi().pushScreen(new FMLOptionsScreen());
			}
		});
		menu.add(new MenuItem("Close", 110, 1) {
			public void run() {
				setDirty(false);
				close();
			}
		});
	}

	public void commentsLoading() {
		synchronized (getUi().getEventLock()) {
			add(loadingField);
			loadingTimer = new Timer();
			TimerTask animate = new TimerTask() {
				public void run() {
					loadingField.animate();
				}
			};
			loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
		}
	}

	public void commentsReceived(Vector comments) {
		_comments = comments;
		loadingTimer.cancel();
		loadComments(commentStart, 20);
	}

	public void loadComments(final int start, final int count) {
		synchronized (getUi().getEventLock()) {
			manager.deleteRange(1, manager.getFieldCount() - 1);
			if (!PStore.TOKEN.equals(""))
				add(new PostButton());
			for (int i = start; i < start + count && i < _comments.size(); i++) {
				Comment comment = (Comment) _comments.elementAt(i);
				add(new CommentField(comment));
			}
			if (start + count < _comments.size())
				add(new MoreButton());
			System.gc();
		}
	}

	public FMLCommentScreen getInstance() {
		return this;
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
	}

	public class LoadingField extends Field {

		private String text = "Retrieving comments...";
		private int alpha = 0;
		private int mod = 20;

		public LoadingField() {
			super();
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		}

		public int getPreferredHeight() {
			return 20 + Config.FONT_SIZE + 36;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			g.drawRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);

			g.drawBitmap((getPreferredWidth() - iconBtmp.getWidth()) / 2, 10,
					iconBtmp.getWidth(), iconBtmp.getHeight(), iconBtmp, 0, 0);

			g.setGlobalAlpha(alpha);
			g.setColor(Config.WHITE);
			g.fillRect((getPreferredWidth() - iconBtmp.getWidth()) / 2, 10,
					iconBtmp.getWidth(), iconBtmp.getHeight());
			g.setGlobalAlpha(255);

			g.setColor(Config.DEFAULT_BLUE);
			if (!getFont().equals(g.getFont()))
				g.setFont(getFont());
			g.drawText(text, 0, (10 + iconBtmp.getHeight()) + 4,
					DrawStyle.HCENTER | DrawStyle.TOP, getPreferredWidth());

		}

		public void animate() {
			int i = alpha + mod;
			if (i <= 0) {
				mod *= -1;
				i = 0;
			}
			if (i >= 255) {
				mod *= -1;
				i = 255;
			}
			alpha = i;
			invalidate();
		}

		public void setText(String s) {
			text = s;
			invalidate();
		}

		public void resetText() {
			setText("Retrieving comments...");
		}

	}

	class PostButton extends Field {

		public PostButton() {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		}

		public int getPreferredHeight() {
			return 20 + 10 + Config.FONT_SIZE;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		private boolean focus = false;

		public void drawFocus(Graphics g, boolean on) {
			focus = true;
			paint(g);
			focus = false;
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			if (focus)
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			else
				g.setColor(Config.GRAY);
			g.setFont(getFont());
			g.drawText("Post Comment", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			getUi().pushScreen(new FMLPostCommentScreen(_post));
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				getUi().pushScreen(new FMLPostCommentScreen(_post));
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

	class MoreButton extends Field {

		public MoreButton() {
			super(FOCUSABLE);
			setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE));
		}

		public int getPreferredHeight() {
			return 20 + 10 + Config.FONT_SIZE;
		}

		public int getPreferredWidth() {
			return Display.getWidth();
		}

		protected void layout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		private boolean focus = false;

		public void drawFocus(Graphics g, boolean on) {
			focus = true;
			paint(g);
			focus = false;
		}

		protected void paint(Graphics g) {
			g.setColor(Config.WHITE);
			g.fillRoundRect(5, 5, getPreferredWidth() - 10,
					getContentHeight() - 10, 15, 15);
			g.setColor(Config.DEFAULT_BLUE);
			if (focus)
				g.drawRoundRect(5, 5, getPreferredWidth() - 10,
						getContentHeight() - 10, 15, 15);
			else
				g.setColor(Config.GRAY);
			g.setFont(getFont());
			g.drawText("More...", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			commentStart += 20;
			loadComments(commentStart, 20);
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				commentStart += 20;
				loadComments(commentStart, 20);
				return true;
			}
			return super.keyChar(character, status, time);
		}

	}

}