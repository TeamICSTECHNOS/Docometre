package fr.univamu.ism.docometre.analyse.functions;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import fr.univamu.ism.docometre.Activator;
import fr.univamu.ism.docometre.DocometreMessages;
import fr.univamu.ism.docometre.analyse.MathEngineFactory;
import fr.univamu.ism.docometre.analyse.SelectedExprimentContributionItem;
import fr.univamu.ism.docometre.analyse.datamodel.Channel;
import fr.univamu.ism.docometre.analyse.editors.ChannelsContentProvider;
import fr.univamu.ism.docometre.dacqsystems.functions.GenericFunction;
import fr.univamu.ism.docometre.dialogs.FunctionalBlockConfigurationDialog;
import fr.univamu.ism.docometre.scripteditor.actions.FunctionFactory;
import fr.univamu.ism.process.Block;
import fr.univamu.ism.process.Script;

public class FindAmplitudeBackward extends GenericFunction {
	
	private static final long serialVersionUID = 1L;
	
	public static final String functionFileName = "FIND_AMPLITUDE_BACKWARD.FUN";
	
	private static final String inputSignalKey = "inputSignal";
	private static final String inputMarker1Key = "inputMarker1";
	private static final String inputMarker2Key = "inputMarker2";
	private static final String amplitudeKey = "amplitude";
	private static final String markersGroupLabelKey = "markersGroupLabel";
	private static final String trialsListKey = "trialsList";
	
	transient private FunctionalBlockConfigurationDialog functionalBlockConfigurationDialog;
	
	@Override
	public String getFunctionFileName() {
		return functionFileName;
	}
	
	@Override
	public Object getGUI(Object functionalBlockConfigurationDialog, Object parent, Object context) {
		if(!(context instanceof Script)) return null;
		
		Composite container  = (Composite) parent;
		Composite paramContainer = new Composite(container, SWT.NORMAL);
		paramContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		paramContainer.setLayout(new GridLayout(2, false));
		
		this.functionalBlockConfigurationDialog = (FunctionalBlockConfigurationDialog) functionalBlockConfigurationDialog;
		
		if(!checkPreBuildGUI(this.functionalBlockConfigurationDialog, paramContainer, 2, context)) {
			return paramContainer;
		}

		// Trials list
		Label trialsListLabel = new Label(paramContainer, SWT.NONE);
		trialsListLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		trialsListLabel.setText(FunctionsMessages.TrialsList);
		Text trialsListText = new Text(paramContainer, SWT.BORDER);
		trialsListText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		String value  = getProperty(trialsListKey, "1:10,15,20:25");
		trialsListText.setText(value);
		trialsListText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				boolean putValue = true;
				String regExp = "^(?!([ \\d]*-){2})\\d+(?: *[-,:;] *\\d+)*$";
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(trialsListText.getText());
				putValue = matcher.matches();
				if(putValue) getTransientProperties().put(trialsListKey, trialsListText.getText());
				else {
					String message = NLS.bind(FunctionsMessages.TrialsListNotValidLabel, trialsListText.getText());
					FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(message);
				}
			}
		});
		
		// Input Signal 1
		Label inputSignal1Label = new Label(paramContainer, SWT.NONE);
		inputSignal1Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		inputSignal1Label.setText(FunctionsMessages.InputSignalLabel);
		ComboViewer inputSignal1ComboViewer = new ComboViewer(paramContainer, SWT.BORDER);
		inputSignal1ComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(inputSignalKey, "");
		inputSignal1ComboViewer.getCombo().setText(value);
		inputSignal1ComboViewer.setContentProvider(new ChannelsContentProvider(true, false, false, false, false, false, false));
		inputSignal1ComboViewer.setLabelProvider(LabelProvider.createTextProvider(new Function<Object, String>() {
			@Override
			public String apply(Object t) {
				if(!(t instanceof Channel)) return null;
				Channel channel = (Channel)t;
				if(channel.isSignal()) return channel.getFullName();
				return null;
			}
		}));
		inputSignal1ComboViewer.setInput(SelectedExprimentContributionItem.selectedExperiment);
		Channel channel = MathEngineFactory.getMathEngine().getChannelFromName(SelectedExprimentContributionItem.selectedExperiment, getProperty(inputSignalKey, ""));
		if(channel != null) inputSignal1ComboViewer.setSelection(new StructuredSelection(channel));
		else {
			String message = NLS.bind(DocometreMessages.ImpossibleToFindChannelTitle, value);
			Activator.logErrorMessage(message);
			this.functionalBlockConfigurationDialog.setErrorMessage(DocometreMessages.FunctionalBlockConfigurationDialogBlockingMessage);
		}
		inputSignal1ComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				getTransientProperties().put(inputSignalKey, inputSignal1ComboViewer.getCombo().getText());
			}
		});
		
		// Input Marker 1 : from
		Label inputMarker1Label = new Label(paramContainer, SWT.NONE);
		inputMarker1Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		inputMarker1Label.setText(FunctionsMessages.FromMarkerLabel);
		ComboViewer inputMarker1ComboViewer = new ComboViewer(paramContainer, SWT.BORDER);
		inputMarker1ComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(inputMarker1Key, "");
		inputMarker1ComboViewer.getCombo().setText(value);
		inputMarker1ComboViewer.setContentProvider(new ChannelsContentProvider(false, false, false, true, false, true, true));
		inputMarker1ComboViewer.setLabelProvider(FunctionsHelper.createTextProvider());
		inputMarker1ComboViewer.setInput(SelectedExprimentContributionItem.selectedExperiment);
		channel = MathEngineFactory.getMathEngine().getChannelFromName(SelectedExprimentContributionItem.selectedExperiment, getProperty(inputMarker1Key, ""));
		if(channel != null) inputMarker1ComboViewer.setSelection(new StructuredSelection(channel));
		else {
			String message = NLS.bind(DocometreMessages.ImpossibleToFindChannelTitle, value);
			Activator.logErrorMessage(message);
			this.functionalBlockConfigurationDialog.setErrorMessage(DocometreMessages.FunctionalBlockConfigurationDialogBlockingMessage);
		}
		inputMarker1ComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				getTransientProperties().put(inputMarker1Key, inputMarker1ComboViewer.getCombo().getText());
			}
		});
		
		// Input Marker 2 : to
		Label inputMarker2Label = new Label(paramContainer, SWT.NONE);
		inputMarker2Label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		inputMarker2Label.setText(FunctionsMessages.ToMarkerLabel);
		ComboViewer inputMarker2ComboViewer = new ComboViewer(paramContainer, SWT.BORDER);
		inputMarker2ComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(inputMarker2Key, "");
		inputMarker2ComboViewer.getCombo().setText(value);
		inputMarker2ComboViewer.setContentProvider(new ChannelsContentProvider(false, false, false, true, false, true, true));
		inputMarker2ComboViewer.setLabelProvider(FunctionsHelper.createTextProvider());
		inputMarker2ComboViewer.setInput(SelectedExprimentContributionItem.selectedExperiment);
		channel = MathEngineFactory.getMathEngine().getChannelFromName(SelectedExprimentContributionItem.selectedExperiment, getProperty(inputMarker2Key, ""));
		if(channel != null) inputMarker2ComboViewer.setSelection(new StructuredSelection(channel));
		else {
			String message = NLS.bind(DocometreMessages.ImpossibleToFindChannelTitle, value);
			Activator.logErrorMessage(message);
			this.functionalBlockConfigurationDialog.setErrorMessage(DocometreMessages.FunctionalBlockConfigurationDialogBlockingMessage);
		}
		inputMarker2ComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				getTransientProperties().put(inputMarker2Key, inputMarker2ComboViewer.getCombo().getText());
			}
		});
		
		// Amplitude
		Label amplitudeLabel = new Label(paramContainer, SWT.NONE);
		amplitudeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		amplitudeLabel.setText(FunctionsMessages.AmplitudeValueLabel);
		Text amplitudeText = new Text(paramContainer, SWT.BORDER);
		amplitudeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(amplitudeKey, "0.0");
		amplitudeText.setText(value);
		amplitudeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				boolean putValue = true;
				String regExp = "^(-)?\\d+(\\.\\d+)?$";
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(amplitudeText.getText());
				putValue = matcher.matches();
				if(putValue) getTransientProperties().put(amplitudeKey, amplitudeText.getText());
				else {
					String message = NLS.bind(FunctionsMessages.EndCutNotValidLabel, amplitudeText.getText());
					FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(message);
				}
			}
		});
		
		// Markers group label 
		Label markersGroupLabelLabel = new Label(paramContainer, SWT.NONE);
		markersGroupLabelLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		markersGroupLabelLabel.setText(FunctionsMessages.MarkersGroupLabel);
		Text markersGroupLabelText = new Text(paramContainer, SWT.BORDER);
		markersGroupLabelText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(markersGroupLabelKey, "Fab");
		markersGroupLabelText.setText(value);
		markersGroupLabelText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				boolean putValue = true;
				String regExp = "^[a-zA-Z][a-zA-Z0-9_]*$";
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(markersGroupLabelText.getText());
				putValue = matcher.matches();
				if(putValue) getTransientProperties().put(markersGroupLabelKey, markersGroupLabelText.getText());
				else {
					String message = NLS.bind(FunctionsMessages.EndCutNotValidLabel, markersGroupLabelText.getText());
					FindAmplitudeBackward.this.functionalBlockConfigurationDialog.setErrorMessage(message);
				}
			}
		});
		
		addCommentField(paramContainer, 1, context);
		
		return paramContainer;
	}
	
	
	@Override
	public String getCode(Object context, Object step) {
		if(!isActivated()) return GenericFunction.getCommentedCode(this, context);
		
		String code = FunctionFactory.getProperty(context, functionFileName, FUNCTION_CODE);
		
		String trialsList = getProperty(trialsListKey, "");
		trialsList = FunctionsHelper.createTrialsListHelper(trialsList);
		String inputSignal1 = getProperty(inputSignalKey, "");
		String inputMarker1 = getProperty(inputMarker1Key, "");
		String inputMarker2 = getProperty(inputMarker2Key, "");
		String markersGroupLabel = getProperty(markersGroupLabelKey, "Fab");
		String amplitude = getProperty(amplitudeKey, "0.0");
		
		code = code.replaceAll(trialsListKey, trialsList).replaceAll(inputSignalKey, inputSignal1).replaceAll(amplitudeKey, amplitude);
		code = code.replaceAll(inputMarker1Key, inputMarker1).replaceAll(inputMarker2Key, inputMarker2).replaceAll(markersGroupLabelKey, markersGroupLabel);
		
		return code + "\n";
	}
	
	@Override
	public Block clone() {
		FindAmplitudeBackward function = new FindAmplitudeBackward();
		super.clone(function);
		return function;
	}

}
