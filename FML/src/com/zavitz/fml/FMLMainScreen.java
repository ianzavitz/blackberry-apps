package com.zavitz.fml;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.zavitz.fml.data.CategoryReceiver;
import com.zavitz.fml.data.Config;
import com.zavitz.fml.data.FMLPost;
import com.zavitz.fml.data.PStore;
import com.zavitz.fml.data.PostReceiver;
import com.zavitz.fml.data.FMLPost.Post;
import com.zavitz.fml.fields.Message;
import com.zavitz.fml.fields.PostField;
import com.zavitz.fml.fields.SearchToolbar;
import com.zavitz.fml.fields.TitleField;
import com.zavitz.fml.fields.Toolbar;
import com.zavitz.fml.fields.ToolbarListener;

public class FMLMainScreen extends MainScreen implements PostReceiver,
		ToolbarListener, CategoryReceiver {

	private VerticalFieldManager internalManager, manager;
	private LoadingField loadingField;
	private MoreButton moreButton;
	private LabelField categoryLabel;
	private DiagnoseButton diagnoseButton;
	private int currentPage = 0;
	public static String category = "last";
	private static Bitmap iconBtmp;
	private Timer loadingTimer;

	static {
		iconBtmp = Bitmap.getBitmapResource("FMLIcon_32.png");
	}

	public FMLMainScreen() {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);

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
		moreButton = new MoreButton();
		diagnoseButton = new DiagnoseButton();
		categoryLabel = new LabelField("Latest Stories", LabelField.HCENTER
				| LabelField.USE_ALL_WIDTH) {
			public void paint(Graphics g) {
				g.setColor(Config.GRAY);
				super.paint(g);
			}
		};
		categoryLabel
				.setFont(getFont().derive(Font.BOLD, Config.FONT_SIZE + 2));
		categoryLabel.setPadding(5, 0, 0, 0);

		new FMLPost(this, category, currentPage);
	}

	public void add(Field field) {
		manager.add(field);
	}

	public void makeMenu(Menu menu, int instance) {
		if (manager.getFieldWithFocus() instanceof PostField) {
			menu.addSeparator();

			final PostField field = (PostField) manager.getFieldWithFocus();
			final Post post = field.getPost();

			menu.add(new MenuItem("View Comments", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLCommentScreen(post));
				}
			});

			if (!PStore.TOKEN.equals("")) {
				if (category.equals("favorites")) {
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
		}

		menu.addSeparator();

		if (!PStore.TOKEN.equals(""))
			menu.add(new MenuItem("Submit Story", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLSubmitScreen());
				}
			});

		MenuItem cc = new MenuItem("Change Category", 110, 1) {
			public void run() {
				getUi().pushScreen(new FMLCategoryScreen(getInstance()));
			}
		};
		menu.add(cc);
		menu.setDefault(cc);
		if (PStore.TOKEN.equals("")) {
			menu.add(new MenuItem("Login", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLLoginScreen(true));
				}
			});
			menu.add(new MenuItem("Create Account", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLSignupScreen());
				}
			});
		} else {
			menu.add(new MenuItem("Moderate Stories", 110, 1) {
				public void run() {
					getUi().pushScreen(new FMLModerateScreen());
				}
			});
			menu.add(new MenuItem("Logout", 110, 1) {
				public void run() {
					PStore.TOKEN = "";
					PStore.store();
					getUi().pushScreen(new FMLLoginScreen(false));
				}
			});
		}
		menu.addSeparator();
		menu.add(new MenuItem("Reload", 110, 1) {
			public void run() {
				currentPage = 0;
				new FMLPost(getInstance(), category, currentPage);
			}
		});
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

	public void postsLoading() {
		synchronized (getUi().getEventLock()) {
			manager.deleteAll();
			add(loadingField);
			if (loadingTimer == null) {
				loadingTimer = new Timer();
				TimerTask animate = new TimerTask() {
					public void run() {
						loadingField.animate();
					}
				};
				loadingTimer.scheduleAtFixedRate(animate, 1000 / 16, 1000 / 16);
			}
		}
	}

	public void close() {
		setDirty(false);
		super.close();
	}

	public FMLMainScreen getInstance() {
		return this;
	}

	public void postsReceived(Vector posts) {
		synchronized (getUi().getEventLock()) {
			loadingTimer.cancel();
			loadingTimer = null;
			internalManager.delete(manager);
			manager.deleteAll();
			if (!category.startsWith("search"))
				manager.add(categoryLabel);
			if (!posts.isEmpty()) {
				while (!posts.isEmpty()) {
					Post post = (Post) posts.elementAt(0);
					manager.add(new PostField(post));
					posts.removeElementAt(0);
				}
				if (!category.startsWith("search")
						&& !category.equals("favorites"))
					add(moreButton);
			} else if (posts.isEmpty() || manager.getFieldCount() == 0) {
				add(new Message("Sorry, we couldn't find any stories."));
			}
			internalManager.add(manager);
		}
	}

	private int countdowns = 0;

	public void postsFailed(final long timeout) {
		if (timeout > -1) {
			countdowns = (int) (timeout / 1000) + 1;
			new Timer().schedule(new TimerTask() {
				public void run() {
					new FMLPost(getInstance(), category, currentPage);
				}
			}, timeout + 1000);

			final Timer countdown = new Timer();
			countdown.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					countdowns--;
					if (countdowns == 0) {
						countdown.cancel();
						loadingField.resetText();
					} else {
						loadingField.setText("Failed to load, retrying in "
								+ countdowns + " second" + (countdowns > 1 ? "s" : "") + ".");
						if (countdowns > 0) {
							boolean add = true;
							for (int i = 0; i < manager.getFieldCount(); i++)
								if (manager.getField(i) == diagnoseButton)
									add = false;
							if (add)
								synchronized (getUi().getEventLock()) {
									manager.add(diagnoseButton);
								}
						}
					}
				}
			}, 1000, 1000);

		} else
			loadingField.setText("Failed to load stories.");
	}

	private UiApplication getUi() {
		return UiApplication.getUiApplication();
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
			g.drawText(category.startsWith("random") ? "Next" : "More...", 0,
					getPreferredHeight() / 2, DrawStyle.HCENTER
							| DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			currentPage++;
			new FMLPost(getInstance(), category, currentPage);
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				currentPage++;
				new FMLPost(getInstance(), category, currentPage);
			}
			return true;
		}

	}

	class DiagnoseButton extends Field {

		public DiagnoseButton() {
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
			g.drawText("Diagnose Connection", 0, getPreferredHeight() / 2,
					DrawStyle.HCENTER | DrawStyle.VCENTER, getPreferredWidth());
		}

		public boolean navigationClick(int status, int time) {
			getUi().pushScreen(new FMLDiagnoseScreen());
			return true;
		}

		public boolean keyChar(char character, int status, int time) {
			if (character == Characters.ENTER) {
				getUi().pushScreen(new FMLDiagnoseScreen());
			}
			return true;
		}

	}

	public class LoadingField extends Field {

		private String text = "Retrieving stories...";
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
			setText("Retrieving stories...");
		}

	}

	public void toolbarPressed(String s) {
		if (s.equals("All Time")) {
			if (category.startsWith("top"))
				category = "top";
			else
				category = "flop";
		} else if (s.equals("Day")) {
			if (category.startsWith("top"))
				category = "top_day";
			else
				category = "flop_day";
		} else if (s.equals("Week")) {
			if (category.startsWith("top"))
				category = "top_week";
			else
				category = "flop_week";
		} else if (s.equals("Month")) {
			if (category.startsWith("top"))
				category = "top_month";
			else
				category = "flop_month";
		} else {
			category = "search=" + s;
		}
		currentPage = 0;
		new FMLPost(getInstance(), category, currentPage);
	}

	public void categoryReceived(String name) {
		if (name.equals("Close"))
			return;
		else
			for (int i = 0; i < internalManager.getFieldCount(); i++)
				if (internalManager.getField(i) instanceof Toolbar
						|| internalManager.getField(i) instanceof SearchToolbar)
					internalManager.delete(internalManager.getField(i));
		if (name.equals("Newest")) {
			category = "last";
			categoryLabel.setText("Latest Stories");
		} else if (name.equals("Top FML")) {
			category = "top";
			Toolbar toolbar = new Toolbar(this);
			toolbar.addOption("All Time");
			toolbar.addOption("Day");
			toolbar.addOption("Week");
			toolbar.addOption("Month");
			categoryLabel.setText("Top FML Stories");
			internalManager.insert(toolbar, 1);
		} else if (name.equals("Flop FML")) {
			category = "flop";
			Toolbar toolbar = new Toolbar(this);
			toolbar.addOption("All Time");
			toolbar.addOption("Day");
			toolbar.addOption("Week");
			toolbar.addOption("Month");
			categoryLabel.setText("Flop FML Stories");
			internalManager.insert(toolbar, 1);
		} else if (name.equals("Search")) {
			category = "search";
			SearchToolbar toolbar = new SearchToolbar(this);
			manager.deleteAll();
			manager.add(new Message("Please enter your search term above."));
			internalManager.insert(toolbar, 1);
			return;
		} else if (name.equals("Moderate")) {
			getUi().pushScreen(new FMLModerateScreen());
			return;
		} else if (name.equals("Favorites")) {
			categoryLabel.setText("Favorite Stories");
			category = "favorites";
		} else if (name.equals("Random")) {
			category = "random/nocomment";
			categoryLabel.setText("Random Story");
		} else if (name.equals("Love")) {
			categoryLabel.setText("Love Stories");
			category = "love";
		} else if (name.equals("Money")) {
			categoryLabel.setText("Money Stories");
			category = "money";
		} else if (name.equals("Kids")) {
			categoryLabel.setText("Kid Stories");
			category = "kids";
		} else if (name.equals("Work")) {
			categoryLabel.setText("Work Stories");
			category = "work";
		} else if (name.equals("Health")) {
			categoryLabel.setText("Health Stories");
			category = "health";
		} else if (name.equals("Sex")) {
			categoryLabel.setText("Sex Stories");
			category = "sex";
		} else if (name.equals("Miscellaneous")) {
			categoryLabel.setText("Miscellaneous Stories");
			category = "miscellaneous";
		}
		currentPage = 0;
		new FMLPost(getInstance(), category, currentPage);
	}

}