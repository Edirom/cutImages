package cutimage;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

public class ImageCutMain {

	public static void main(String[] args) {
		
		CutImageDialog cutImageDialog = new CutImageDialog(new Shell());
		cutImageDialog.open();
		if (cutImageDialog.getReturnCode() == IDialogConstants.CANCEL_ID) {
			return;
		}
		else{
			ImageTiles tiles = new ImageTiles();
			tiles.run(cutImageDialog);
		}
	}
}
	
	