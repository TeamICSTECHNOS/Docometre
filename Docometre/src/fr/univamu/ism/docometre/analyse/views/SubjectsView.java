package fr.univamu.ism.docometre.analyse.views;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import fr.univamu.ism.docometre.Activator;
import fr.univamu.ism.docometre.AnalysePerspective;
import fr.univamu.ism.docometre.ApplicationActionBarAdvisor;
import fr.univamu.ism.docometre.DocometreMessages;
import fr.univamu.ism.docometre.IImageKeys;
import fr.univamu.ism.docometre.analyse.SelectedExprimentContributionItem;
import fr.univamu.ism.docometre.analyse.datamodel.Channel;
import fr.univamu.ism.docometre.views.ExperimentsLabelDecorator;
import fr.univamu.ism.docometre.views.ExperimentsViewerSorter;

public class SubjectsView extends ViewPart implements IResourceChangeListener, IPerspectiveListener {
	
	public static String ID = "Docometre.SubjectsView";

	public static IUndoContext subjectsViewUndoContext;
	
	private class SubjectsViewUndoContext extends UndoContext {
		@Override
		public String getLabel() {
			return "SubjectsViewUndoContext";
		}
	}
	
	private TreeViewer subjectsTreeViewer;
	private Composite parent;
	private Composite imageMessageContainer;
	

	public SubjectsView() {
		subjectsViewUndoContext = new SubjectsViewUndoContext();
	}
	
	/*
	 * Refresh subject view
	 */
	public static void refresh(IResource parentResource, IResource[] resourcesToSelect) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				IViewPart subjectsView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SubjectsView.ID);
				if (subjectsView instanceof SubjectsView)
					((SubjectsView)subjectsView).refreshInput(parentResource, resourcesToSelect);
			}
		});
	}

	protected void refreshInput(IResource parentResource, IResource[] resourcesToSelect) {
		subjectsTreeViewer.refresh(parentResource, true);
		PlatformUI.getWorkbench().getDecoratorManager().update(ExperimentsLabelDecorator.ID);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		this.parent.setLayout(gl);
		
		// Create tree viewer
		subjectsTreeViewer = new TreeViewer(parent);
		subjectsTreeViewer.setContentProvider(new SubjectsContentProvider());
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		SubjectsLabelProvider subjectsLabelProvider = new SubjectsLabelProvider();
		subjectsTreeViewer.setLabelProvider(new DecoratingLabelProvider(subjectsLabelProvider, decorator));
		subjectsTreeViewer.setComparator(new ExperimentsViewerSorter());
		subjectsTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getSite().setSelectionProvider(subjectsTreeViewer);
		
		subjectsTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object element = ((IStructuredSelection) subjectsTreeViewer.getSelection()).getFirstElement();
				subjectsTreeViewer.setExpandedState(element, !subjectsTreeViewer.getExpandedState(element));
				ApplicationActionBarAdvisor.openEditorAction.run();
			}
		});
		
		LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
		DragSource dragSource = new DragSource(subjectsTreeViewer.getTree(), DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] {transfer});
		
		dragSource.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				event.doit = false;
				if(!subjectsTreeViewer.getSelection().isEmpty()) {
					IStructuredSelection selection = ((IStructuredSelection)subjectsTreeViewer.getSelection());
					Object[] items = selection.toArray();
					event.doit = items.length == 2 && (items[0] instanceof Channel && items[1] instanceof Channel);
				}
			}
			public void dragSetData(DragSourceEvent event) {
				if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
					IStructuredSelection selection = ((IStructuredSelection)subjectsTreeViewer.getSelection());
					transfer.setSelection(selection);
				}
			}
			public void dragFinished(DragSourceEvent event) {
			}
		});
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
		
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), ApplicationActionBarAdvisor.deleteResourcesAction);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), ApplicationActionBarAdvisor.copyResourcesAction);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PASTE.getId(), ApplicationActionBarAdvisor.pasteResourcesAction);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.RENAME.getId(), ApplicationActionBarAdvisor.renameResourceAction);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), ApplicationActionBarAdvisor.refreshResourceAction);
		makePopupMenu();
		
		updateInput(SelectedExprimentContributionItem.selectedExperiment);
	}
	
	private void makePopupMenu() {
		MenuManager popUpMenuManager = new MenuManager("SubjectsViewPopUpMenu");
		subjectsTreeViewer.getTree().setMenu(popUpMenuManager.createContextMenu(subjectsTreeViewer.getTree()));
		getSite().registerContextMenu(popUpMenuManager, subjectsTreeViewer);
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(this);
		super.dispose();
	}
	
	private void createMessageInfo() {
		// Create infos
		if(imageMessageContainer != null) return;
		imageMessageContainer = new Composite(parent, SWT.NORMAL);
		imageMessageContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		imageMessageContainer.setLayout(new GridLayout());
		Label imageSelectLabel = new Label(imageMessageContainer, SWT.CENTER);
		imageSelectLabel.setImage(Activator.getImage(IImageKeys.SELECT_ICON));
		imageSelectLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		Label messageLabel = new Label(imageMessageContainer, SWT.WRAP | SWT.CENTER);
		messageLabel.setText(DocometreMessages.SelectExperimentToProcessDataFilesMessage);
		messageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
	}

	public void updateInput(IResource input) {
		if(input != null) {
			// Remove infos
			if(imageMessageContainer != null && !imageMessageContainer.isDisposed()) imageMessageContainer.dispose();
			// Update part name
			String partName = getPartName();
			partName = partName.replaceAll("\\s\\[.*\\]", "");
			setPartName(partName + " [" + input.getName() + "]");
			getSite().getSelectionProvider().setSelection(new StructuredSelection(input));
		} else {
			// Create infos
			createMessageInfo();
			// Update part name
			String partName = getPartName();
			partName = partName.replaceAll("\\s\\[.*\\]", "");
			setPartName(partName);
		}
		
		subjectsTreeViewer.setInput(input);
		subjectsTreeViewer.refresh();
		// Layout
		parent.layout();
	}

	@Override
	public void setFocus() {
		if(subjectsTreeViewer != null) subjectsTreeViewer.getTree().setFocus();
	}

//	private void refreshInput(IResourceChangeEvent event) {
//		if(SelectedExprimentContributionItem.selectedExperiment != null) 
//			if(event != null && event.getDelta() != null && event.getDelta().findMember(SelectedExprimentContributionItem.selectedExperiment.getFullPath()) == null) return;
//		if(subjectsTreeViewer != null) 
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println("selectedExperiment : " + SelectedExprimentContributionItem.selectedExperiment);
//					if (SelectedExprimentContributionItem.selectedExperiment != null) System.out.println("exist : " + SelectedExprimentContributionItem.selectedExperiment.exists());
//					if(SelectedExprimentContributionItem.selectedExperiment != null && !SelectedExprimentContributionItem.selectedExperiment.exists()) SelectedExprimentContributionItem.selectedExperiment = null;
//					updateInput();
//				}
//			});
//	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getDelta() == null) return;
		IResourceDelta[] resourceDeltas = event.getDelta().getAffectedChildren();
		for (IResourceDelta resourceDelta : resourceDeltas) {
			if (resourceDelta.getKind() == IResourceDelta.ADDED) {
				if (subjectsTreeViewer.getInput() != null) {
					if (((IResource)subjectsTreeViewer.getInput()).getFullPath().equals(resourceDelta.getMovedFromPath())) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								updateInput(resourceDelta.getResource());
							}
						});
					} 
				}
			} 
			if (resourceDelta.getKind() == IResourceDelta.CHANGED) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						subjectsTreeViewer.refresh();
					}
				});
			}
		}
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		if(perspective.getId().equals(AnalysePerspective.ID)) updateInput(SelectedExprimentContributionItem.selectedExperiment);
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
	}

	public static void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IViewPart subjectsView =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SubjectsView.ID);
				if (subjectsView instanceof SubjectsView)
					((SubjectsView)subjectsView).subjectsTreeViewer.refresh();
			}
		});
		
	}

	public ISelection getSelection() {
		return subjectsTreeViewer.getSelection();
	}
	
	public IContainer getInput() {
		if(subjectsTreeViewer != null) return (IContainer) subjectsTreeViewer.getInput();
		return null;
	}
	
}
