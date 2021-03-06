package fr.univamu.ism.docometre.analyse.datamodel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;

import fr.univamu.ism.docometre.Activator;
import fr.univamu.ism.docometre.DocometreMessages;
import fr.univamu.ism.docometre.ResourceType;
import fr.univamu.ism.docometre.analyse.editors.BatchDataProcessingEditor;

public class MoveUpHandler extends SelectionAdapter {
	
	private final class MoveUpOperation extends AbstractOperation {
		
		private BatchDataProcessingItem[] items;
		
		public MoveUpOperation(String label, BatchDataProcessingItem[] items) {
			super(label);
			this.items = items;
			addContext(dataProcessBatchEditor.getUndoContext());
		}
		
		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			if(resourceType == ResourceType.DATA_PROCESSING) {
				for (BatchDataProcessingItem item : items) dataProcessBatchEditor.getBatchDataProcessing().moveProcessUp(item);
				dataProcessBatchEditor.refreshProcesses();
			} else {
				for (BatchDataProcessingItem item : items) dataProcessBatchEditor.getBatchDataProcessing().moveSubjectUp(item);
				dataProcessBatchEditor.refreshSubjects();
			}
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			execute(monitor, info);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			BatchDataProcessingItem[] newItems = new BatchDataProcessingItem[items.length];
			System.arraycopy(items, 0, newItems, 0, items.length);
			List<Object> list = Arrays.asList((Object[])newItems);
			Collections.reverse(list);
			BatchDataProcessingItem[] batchDataProcessingItems = list.toArray(new BatchDataProcessingItem[items.length]);
			if(resourceType == ResourceType.DATA_PROCESSING) {
				for (BatchDataProcessingItem item : batchDataProcessingItems) dataProcessBatchEditor.getBatchDataProcessing().moveProcessDown(item);
				dataProcessBatchEditor.refreshProcesses();
			} else {
				for (BatchDataProcessingItem item : batchDataProcessingItems) dataProcessBatchEditor.getBatchDataProcessing().moveSubjectDown(item);
				dataProcessBatchEditor.refreshSubjects();
			}
			return Status.OK_STATUS;
		}
		
	}
	
	private BatchDataProcessingEditor dataProcessBatchEditor;
	private IOperationHistory operationHistory;
	private ResourceType resourceType;
	
	public MoveUpHandler(BatchDataProcessingEditor dataProcessBatchEditor, ResourceType resourceType) {
		this.dataProcessBatchEditor = dataProcessBatchEditor;
		operationHistory = PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
		this.resourceType = resourceType;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		BatchDataProcessingItem[] selection = dataProcessBatchEditor.getSelectedProcesses();
		if(resourceType == ResourceType.SUBJECT) selection = dataProcessBatchEditor.getSelectedSubjects();
		if(selection.length > 0) {
			try {
				if(resourceType == ResourceType.DATA_PROCESSING) {
					if(dataProcessBatchEditor.getBatchDataProcessing().canMoveProcessesUp(selection)) 
						operationHistory.execute(new MoveUpOperation(DocometreMessages.MoveUpProcessModifyOperationLabel, selection), null, null);
				} else {
					if(dataProcessBatchEditor.getBatchDataProcessing().canMoveSubjectsUp(selection)) 
						operationHistory.execute(new MoveUpOperation(DocometreMessages.MoveUpSubjectModifyOperationLabel, selection), null, null);
				}
			} catch (ExecutionException e1) {
				Activator.logErrorMessageWithCause(e1);
				e1.printStackTrace();
			}
		}
	}

}
