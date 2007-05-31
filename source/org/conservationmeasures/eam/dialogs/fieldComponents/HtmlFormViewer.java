/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.fieldComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;
import javax.swing.text.html.StyleSheet;

import org.conservationmeasures.eam.actions.ActionCopy;
import org.conservationmeasures.eam.actions.ActionCut;
import org.conservationmeasures.eam.actions.ActionDelete;
import org.conservationmeasures.eam.actions.ActionPaste;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.EAMResourceImageIcon;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.questions.FontFamiliyQuestion;
import org.conservationmeasures.eam.utils.HtmlFormEventHandler;
import org.martus.swing.HyperlinkHandler;
import org.martus.swing.UiEditorPane;


public class HtmlFormViewer extends UiEditorPane implements HyperlinkListener, MouseListener
{

	public static HtmlFormViewer createHtmlViewerWithPanelFont(String htmlSource, HyperlinkHandler hyperLinkHandler)
	{
		return new HtmlFormViewer(false, htmlSource, hyperLinkHandler);
	}
	
	public static HtmlFormViewer createHtmlViewerWithWizardFont(String htmlSource, HyperlinkHandler hyperLinkHandler)
	{
		return new HtmlFormViewer(htmlSource, hyperLinkHandler);
	}

	protected HtmlFormViewer(String htmlSource, HyperlinkHandler hyperLinkHandler)
	{
		this(true, htmlSource, hyperLinkHandler);
	}

	protected HtmlFormViewer(boolean fontToUse, String htmlSource, HyperlinkHandler hyperLinkHandler)
	{
		fontTest = fontToUse;
		linkHandler = hyperLinkHandler;
		setEditable(false);
		setText(htmlSource);
		addHyperlinkListener(this);
		addMouseListener(this);
	}
	

	public void setFontAsPanelFont()
	{
		fontFamily = getMainWindow().getDataPanelFontFamily();
		fontSize = getMainWindow().getDataPanelFontSize(this);
	}
	
	public void setFontAsWizardFont()
	{
		fontFamily = getMainWindow().getWizardFontFamily();
		fontSize = getMainWindow().getWizardFontSize();
	}
	
	
	public void setText(String text)
	{
		if (fontTest)
			setFontAsWizardFont();
		else 
			setFontAsPanelFont();
		
		HTMLEditorKit htmlKit = new OurHtmlEditorKit(linkHandler);
		StyleSheet style = htmlKit.getStyleSheet();
		customizeStyleSheet(style);
		htmlKit.setStyleSheet(style);
		setEditorKit(htmlKit);

		Document doc = htmlKit.createDefaultDocument();
		setDocument(doc);
		
		super.setText(text);
		setCaretPosition(0);
	}

	public void setFixedWidth( Component component, int width )
	{
		component.setSize( new Dimension( width, Short.MAX_VALUE ) );
		Dimension preferredSize = component.getPreferredSize();
		component.setPreferredSize( new Dimension( width, preferredSize.height ) );
	}
	
	protected void customizeStyleSheet(StyleSheet style)
	{
		style.addRule("body {background: #ffffff;}");
		addRuleFontFamily(style);
		addRuleFontSize(style);
	}

	public void addRuleFontSize(StyleSheet style)
	{
		if (fontSize == 0)
			style.addRule(makeSureRuleHasRightPrefix("body {font-size:"+getFont().getSize()+"pt;}"));			
		else
			style.addRule(makeSureRuleHasRightPrefix("body {font-size:"+fontSize+"pt;}"));		
	}
	
	public void addRuleFontFamily(StyleSheet style)
	{

		style.addRule(makeSureRuleHasRightPrefix("body {font-family:"+new FontFamiliyQuestion("").findChoiceByCode(fontFamily)+";}"));
	}
	
	public String makeSureRuleHasRightPrefix(String rule)
	{
		if (cssDotPrefixWorksCorrectly())
			return rule;

		return replaceDotWithPoundSign(rule);
	}
	
	public boolean cssDotPrefixWorksCorrectly()
	{
		String javaVersion = EAM.getJavaVersion();
		if (javaVersion.startsWith("1.4"))
			return false;
		return true;
	}
	
	private String replaceDotWithPoundSign(String rule)
	{
		if (rule.trim().startsWith("."))
			return rule.trim().replaceFirst(".", "#");

		return rule;
	}

	//FIXME: Richard: should not use static ref here
	public static MainWindow getMainWindow()
	{
		return EAM.mainWindow;
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if(e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
		{
			String clicked = e.getDescription();
			linkHandler.linkClicked(clicked);
		}

	}
	
	
	
	
	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{	
	}

	public void mousePressed(MouseEvent e)
	{
		if(e.isPopupTrigger())
			fireRightClick(e);
	}

	public void mouseReleased(MouseEvent e)
	{
		if(e.isPopupTrigger())
			fireRightClick(e);
	}
	
	void fireRightClick(MouseEvent e)
	{
		//FIXME: Richard: should not reference static mainwindow var here
		getRightClickMenu(EAM.mainWindow.getActions()).show(this, e.getX(), e.getY());
	}
	
	public JPopupMenu getRightClickMenu(Actions actions)
	{
		JPopupMenu menu = new JPopupMenu();
		
		//FIXME: Richard: should not need to create a class here, but pass in the copy acton from JEditorPane
		JMenuItem menuItemCopy = new JMenuItem(new EditorActionCopy());
		menu.add(menuItemCopy);
		
		JMenuItem menuItemCut = new JMenuItem(actions.get(ActionCut.class));
		menuItemCut.setEnabled(false);
		menu.add(menuItemCut);
		
		JMenuItem menuItemPaste = new JMenuItem(actions.get(ActionPaste.class));
		menuItemPaste.setEnabled(false);
		menu.add(menuItemPaste);
		
		JMenuItem menuItemDelete = new JMenuItem(actions.get(ActionDelete.class));
		menuItemDelete.setEnabled(false);
		menu.add(menuItemDelete);
		
		return menu;
	}
	
	String fontFamily;
	int fontSize;
	boolean fontTest;
	
	class EditorActionCopy extends ActionCopy
	{
		public EditorActionCopy()
		{
			super(EAM.mainWindow);
		}
		
		public void doAction(EventObject event) throws CommandFailedException
		{
			copy();
		}
	}
	

	class OurHtmlEditorKit extends HTMLEditorKit
	{
		public OurHtmlEditorKit(HyperlinkHandler handler)
		{
			factory = new OurHtmlViewFactory(handler);
			ourStyleSheet = new StyleSheet();
			ourStyleSheet.addStyleSheet(super.getStyleSheet());
		}
		
		public ViewFactory getViewFactory()
		{
			return factory;
		}

		public StyleSheet getStyleSheet()
		{
			return ourStyleSheet;
		}

		public void setStyleSheet(StyleSheet s)
		{
			ourStyleSheet = s;
		}

		ViewFactory factory;
		StyleSheet ourStyleSheet;
	}
	
	class OurHtmlViewFactory extends HTMLEditorKit.HTMLFactory
	{
		public OurHtmlViewFactory(HyperlinkHandler handlerToUse)
		{
			handler = handlerToUse;
		}
		
		public View create(Element elem)
		{
			if(elem.getName().equals("select"))
			{
				return new OurSelectView(elem, handler);
			}
			if(elem.getName().equals("input"))
			{
				AttributeSet attributes = elem.getAttributes();
				Object typeAttribute = attributes.getAttribute(HTML.Attribute.TYPE);
				if(typeAttribute.equals("submit"))
				{
					return new OurButtonView(elem, handler);
				}
				if(typeAttribute.equals("text"))
				{
					return new OurTextView(elem, handler);
				}
				if(typeAttribute.equals("textarea"))
				{
					return new OurTextView(elem, handler);
				}
				if(typeAttribute.equals("label"))
				{
					return new OurLabelView(elem, handler);
				}
			}
			else if(elem.getName().equals("img"))
			{
				return new OurImageView(elem);
			}
			return super.create(elem);
		}
		
		HyperlinkHandler handler;
	}
	
	class OurButtonView extends FormView
	{
		public OurButtonView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		protected void submitData(String data)
		{
			String buttonName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.buttonPressed(buttonName);
		}
		
		HyperlinkHandler handler;
	}
	
	class OurSelectView extends FormView implements ItemListener
	{
		public OurSelectView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		protected Component createComponent()
		{
			comboBox = (JComboBox)super.createComponent();
			comboBox.addItemListener(this);
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			((HtmlFormEventHandler)handler).setComponent(fieldName, comboBox);
			return comboBox;
		}

		public void itemStateChanged(ItemEvent e)
		{
			String name = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.valueChanged(name, comboBox.getSelectedItem().toString());
		}
		
		HyperlinkHandler handler;
		JComboBox comboBox;
	}
	
	
	class OurTextView extends FormView implements DocumentListener
	{
		public OurTextView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		protected Component createComponent()
		{
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			textField = (JTextComponent)super.createComponent();
			textField.getDocument().addDocumentListener(this);
			((HtmlFormEventHandler)handler).setComponent(fieldName, textField);
			return textField;
		}

		public void changedUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}


		public void insertUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}

		public void removeUpdate(DocumentEvent event) 
		{
			notifyHandler();
		}
		
		private void notifyHandler() 
		{
			String name = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			handler.valueChanged(name, textField.getText());
		}
		
		protected void submitData(String data)
		{
		}
		
		HyperlinkHandler handler;
		JTextComponent textField;

	}

	
	class OurLabelView extends FormView
	{
		public OurLabelView(Element elem, HyperlinkHandler handlerToUse)
		{
			super(elem);
			handler = handlerToUse;
		}

		protected Component createComponent()
		{
			String fieldName = (String)getElement().getAttributes().getAttribute(HTML.Attribute.NAME);
			JLabel label = new JLabel("");
			label.setBorder(new EmptyBorder(new Insets(0,0,10,0)));
			((HtmlFormEventHandler)handler).setComponent(fieldName, label);
			return label;
		}

		HyperlinkHandler handler;
	}
	
	class OurImageView extends ImageView
	{
		public OurImageView(Element elem)
		{
			super(elem);
			name = (String)elem.getAttributes().getAttribute(HTML.Attribute.SRC);
		}

		public Image getImage()
		{
			if(image == null)
			{
				try
				{
					EAMResourceImageIcon icon = new EAMResourceImageIcon(name);
					image = icon.getImage();
				}
				catch(NullPointerException e)
				{
					throw new RuntimeException(name, e);
				}
			}
			return image;
		}
		
		String name;
		Image image;
	}
	
	
	HyperlinkHandler linkHandler;
}

