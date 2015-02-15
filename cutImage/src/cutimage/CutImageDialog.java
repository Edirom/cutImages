package cutimage;


import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;



public class CutImageDialog extends Dialog{

	private final static int TILE_SIZE = 256;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

	private String[] rightLabels = new String[] { "Image path", "Target directory", 
			"Images prefix", "Tile size", "Source directory" };	
	private int leftLabelWidt;

	private HashMap<Composite, GC> gcCache = new HashMap<Composite, GC>();
		
	private String targetPath = null;
	private String sourcePath = null;
	private String sourceDirectory = null;
	private int size = TILE_SIZE;
	private int errorCode = 0;
	private String prefix="";


	/**
	 * @param parentShell
	 */
	public CutImageDialog(Shell parentShell) {		
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setSize(450, 480);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite topParent) {
		this.getShell().setText("Cut Image");

		leftLabelWidt = getMaximalLabelWidth(rightLabels, topParent);

		Composite parent = createComposite(topParent, 1);
		createSourceSection(parent);
		createTargetSection(parent);
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {		
		super.okPressed();	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent1) {		
		super.createButtonsForButtonBar(parent1);
		getButton(IDialogConstants.OK_ID).setText("Cut");
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/**
	 * Method for create controls in source section.
	 * 
	 * @param parent
	 */
	private void createSourceSection(Composite parent) {
	
		Section sourceSection = createDetailSection(parent, SWT.NONE, "Source", 1);
		Composite sourceComposite = createDetailComposite(sourceSection, 2, false);
		sourceSection.setClient(sourceComposite);		
		Composite sourceDetailsComposite = createComposite(sourceComposite, 4);

		// Info composite
		Composite infoComposite = new Composite(sourceDetailsComposite, SWT.NONE);
		infoComposite.setLayout(new GridLayout(3, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 4;
		infoComposite.setLayoutData(data);
		Label infoLabel = new Label(infoComposite, SWT.NONE);
		infoLabel.setImage(infoComposite.getShell().getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		Text infoText = new Text(infoComposite, SWT.READ_ONLY | SWT.MULTI);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		infoText.setLayoutData(data);
		infoText.setText("Select an image or a derectory for cut more images");
		infoText.setBackground(infoComposite.getBackground());

		// Selection sources area
		// Image selection
		createLabel(sourceDetailsComposite, "Image", leftLabelWidt);
		Composite mandatoryImage= createDecorator(sourceDetailsComposite);
		//addDrawListener(mandatoryImage);		
		Composite imageSelectionComposite = createComposite(sourceDetailsComposite, 1);
		Text sourcePathControl = createText(imageSelectionComposite, "");
		sourcePathControl.setEditable(false);

		final Button sourceDialogButton = new Button(sourceDetailsComposite, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		sourceDialogButton.setLayoutData(data);
		sourceDialogButton.setText("...");
		
		// Directory selection
		createLabel(sourceDetailsComposite, "Source directory", leftLabelWidt);
		Composite mandatorySourceDecorator= createDecorator(sourceDetailsComposite);
		//addDrawListener(mandatorySourceDecorator);		
		Composite directorySelectionComposite = createComposite(sourceDetailsComposite, 1);		
		Text sourceDirectoryControl = createText(directorySelectionComposite, "");		
		sourceDirectoryControl.setEditable(false);

		final Button sourceDirectoryButton = new Button(sourceDetailsComposite, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		sourceDirectoryButton.setLayoutData(data);
		sourceDirectoryButton.setText("...");
		sourceDirectoryButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog fileDialog = new DirectoryDialog(getShell());
				sourceDirectory = fileDialog.open();
				if (sourceDirectory != null) {
					sourceDirectoryControl.setText(sourceDirectory);
					if (sourcePath != null) {
						sourcePath = null;
						sourcePathControl.setText("");
					}	
				}
				handleOkButton();
			}
		});			
		sourceDialogButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				sourcePath = fileDialog.open();
				if (sourcePath != null) {
					sourcePathControl.setText(sourcePath);
					if (sourceDirectory != null) {
						sourceDirectory = null;
						sourceDirectoryControl.setText("");
					}				
				}
				handleOkButton();
			}
		});
	}

	/**
	 * Method to create controls in target section
	 * 
	 * @param parent
	 */
	private void createTargetSection(Composite parent) {
		
		Section targetSection = createDetailSection(parent, SWT.NONE, "Target", 1);
		Composite targetComposite = createDetailComposite(targetSection, 2, false);
		targetSection.setClient(targetComposite);
		Composite tergetDetailsComposite = createComposite(targetComposite, 4);
		
		// Info composite
		Composite infoComposite = new Composite(tergetDetailsComposite, SWT.NONE);
		infoComposite.setLayout(new GridLayout(2, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 4;
		infoComposite.setLayoutData(data);
		Label infoLabel = new Label(infoComposite, SWT.NONE);
		infoLabel.setImage(infoComposite.getShell().getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		Text infoText = new Text(infoComposite, SWT.READ_ONLY | SWT.MULTI);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		infoText.setLayoutData(data);
		infoText.setText("If a directory for source is selected, "
				+ "for each original image \na directory "
				+ "in target directory with tiles will be created");
		infoText.setBackground(infoComposite.getBackground());

		// Target directory
		createLabel(tergetDetailsComposite, "Target directory", leftLabelWidt);
		Composite mandatoryTargetDecorator= createDecorator(tergetDetailsComposite);
		
		//addDrawListener(mandatoryTargetDecorator);
		Composite targetPathComposite = createComposite(tergetDetailsComposite, 1);
		Text targetPathControl = createText(targetPathComposite, "");
		targetPathControl.setEditable(false);

		final Button targetDialogButton = new Button(tergetDetailsComposite, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		targetDialogButton.setLayoutData(data);
		targetDialogButton.setText("...");
		targetDialogButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog fileDialog = new DirectoryDialog(getShell());
				targetPath = fileDialog.open();
				if (targetPath != null) {
					targetPathControl.setText(targetPath);
				}
				handleOkButton();
			}
		});

		// Image prefix
		createLabel(tergetDetailsComposite, "Images prefix", leftLabelWidt);
		createDecorator(tergetDetailsComposite);
		Composite imsgePrefixComposite = createComposite(tergetDetailsComposite, 1);
		((GridData) imsgePrefixComposite.getLayoutData()).horizontalSpan = 2;
		Text prefixControl = createText(imsgePrefixComposite, "");
		prefixControl.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				prefix = prefixControl.getText();	
			}
		});

		// Tile size
		createLabel(tergetDetailsComposite, "Tile size", leftLabelWidt);
		createDecorator(tergetDetailsComposite);		
		Composite tileSizeComposite = createComposite(tergetDetailsComposite, 1);
		((GridData) tileSizeComposite.getLayoutData()).horizontalSpan = 2;
		Text sizeControl = createText(tileSizeComposite, "256");
		((GridData) sizeControl.getLayoutData()).horizontalIndent = 2;
		sizeControl.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if(sizeControl.getText() != null && !sizeControl.getText().isEmpty()){
					String newValue = sizeControl.getText();
					try {
						size =Integer.parseInt(newValue);
						if(size <= 0 || newValue.startsWith("0")){
							errorCode = 1;
						}
						else{
							errorCode = 0;
						}
					} catch (NumberFormatException e) {
						errorCode = 1;
					}
				}
				else {
					size = TILE_SIZE;
					errorCode = 0;
				}	
				handleOkButton();
			}
		});
	}

	/**
	 * Validate input for controls and enable cut-button
	 * 
	 * @return result
	 */
	private boolean handleOkButton() {
		boolean result = true;
		//sourceDirectory
		if((sourcePath == null || sourcePath.isEmpty()) && (sourceDirectory == null || sourceDirectory.isEmpty())
				|| (targetPath == null || targetPath.isEmpty()) || errorCode == 1){
			result = false;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(result);
		return result;
	}


	//****** GETTERS ************
	/**
	 * @return sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @return prefix
	 */
	public String getPrefix() {
		if(prefix == null || prefix.isEmpty()){
			prefix = "";
		}
		return prefix;
	}

	/**
	 * @return size
	 */
	public int getSize() {
		if(size <= 0){
			size = TILE_SIZE;
		}
		return size;
	}

	/**
	 * @return targetPath
	 */
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @return sourceDirectory
	 */
	public String getSourceDirectory() {
		return sourceDirectory;
	}


	//*********HELP METHODS******************

	/**
	 * compute max space from label to control field
	 * 
	 * @param labels
	 * @param parent
	 * @return width
	 */
	private int getMaximalLabelWidth(String[] labels, Composite parent) {
		int width = 0;
		GC gc = gcCache.get(parent);
		if (gc == null) {
			gc = new GC(parent);
			gcCache.put(parent, gc);
		}
		for (String label : labels) {
			int labelWidth = gc.stringExtent(label).x;
			if (width == 0 || width < labelWidth) {
				width = labelWidth;
			}
		}
		gcCache.clear();
		return width;
	}

	/**
	 * @param parent
	 * @param numColumns
	 * @return composite
	 */
	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(numColumns, false));
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(data);
		return composite;
	}

	/**
	 * @param parent
	 * @param text
	 * @param width
	 */
	private Label createLabel(Composite parent, String text, Object... width) {
		Label label = new Label(parent, SWT.RIGHT);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		if (width != null && width.length > 0 && width[0] instanceof Integer) {
			gridData.widthHint = (Integer) width[0];
		}
		label.setLayoutData(gridData);
		label.setText(text);
		return label;
	}

	/**
	 * @param parent
	 * @param text
	 */
	private Text createText(Composite parent, String Name) {
		Text text = new Text(parent, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		text.setLayoutData(data);
		text.setText(Name);
		return text;
	}

	/**
	 * @param parent
	 * @param style
	 * @param title
	 * @param numColumn
	 * @return
	 */
	private Section createDetailSection(Composite parent, int style, String title, int numColumn) {
		Section section = formToolkit.createSection(parent, style);
		GridLayout layout = new GridLayout(numColumn, true);
		section.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		section.setLayoutData(data);
		section.setText(title);
		formToolkit.createCompositeSeparator(section);
		section.setBackground(parent.getBackground());
		return section;
	}

	/**
	 * @param parent
	 * @param numColumns
	 * @param equalWidth
	 * @return
	 */
	private Composite createDetailComposite(Composite parent, int numColumns, boolean equalWidth) {
		Composite details = formToolkit.createComposite(parent);
		GridLayout layout = new GridLayout(numColumns, equalWidth);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		details.setLayoutData(data);
		details.setLayout(layout);
		details.setBackground(parent.getBackground());
		return details;
	}

	/**
	 * @param parent
	 * @return
	 */
	private Composite createDecorator(Composite parent){
		Composite imageComposite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.TRAIL, SWT.CENTER, false, false);
		gridData.widthHint = 8;//image.getBounds().width;
		gridData.heightHint = 8;//image.getBounds().height;
		imageComposite.setLayoutData(gridData);
		return imageComposite;
	}

	// TODO: define an image as resource for run an jar-file
//	/**
//	 * @param imageComposite
//	 */
//	private void addDrawListener(Composite imageComposite){
//		imageComposite.addPaintListener(new PaintListener() {
//			public void paintControl(PaintEvent e) {
//				e.gc.drawImage(image, 0, 0);
//			}
//		});
//	}

}
