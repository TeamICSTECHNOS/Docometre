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
import org.eclipse.swt.widgets.Spinner;
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

public class ButterworthHighPass extends GenericFunction {
	
	private static final long serialVersionUID = 1L;
	
	public static final String functionFileName = "BUTTERWORTH_HIGHPASS.FUN";
	
	private static final String orderKey = "order";
	private static final String cutOffFrequencyKey = "cutOffFrequency";
	private static final String inputSignalKey = "inputSignal";
	private static final String outputSignalSuffixKey = "outputSignal";
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
				ButterworthHighPass.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				boolean putValue = true;
				String regExp = "^(?!([ \\d]*-){2})\\d+(?: *[-,:;] *\\d+)*$";
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(trialsListText.getText());
				putValue = matcher.matches();
				if(putValue) getTransientProperties().put(trialsListKey, trialsListText.getText());
				else {
					String message = NLS.bind(FunctionsMessages.TrialsListNotValidLabel, trialsListText.getText());
					ButterworthHighPass.this.functionalBlockConfigurationDialog.setErrorMessage(message);
				}
			}
		});
		
		Label orderLabel = new Label(paramContainer, SWT.NONE);
		orderLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		orderLabel.setText(FunctionsMessages.OrderLabel);
		Spinner orderSpinner = new Spinner(paramContainer, SWT.BORDER);
		orderSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(orderKey, "2");
		orderSpinner.setValues(Integer.valueOf(value), 1, 100, 0, 1, 10);
		orderSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getTransientProperties().put(orderKey, orderSpinner.getText());
			}
		});
		
		Label cutOffLabel = new Label(paramContainer, SWT.NONE);
		cutOffLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		cutOffLabel.setText(FunctionsMessages.CutoffFrequencyLabel);
		Text cutOffFrequencyText = new Text(paramContainer, SWT.BORDER);
		cutOffFrequencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(cutOffFrequencyKey, "5");
		cutOffFrequencyText.setText(value);
		cutOffFrequencyText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ButterworthHighPass.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				boolean putValue = true;
				String regExp = "\\d+\\.?\\d*";
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(cutOffFrequencyText.getText());
				putValue = matcher.matches();
				if(putValue) getTransientProperties().put(cutOffFrequencyKey, cutOffFrequencyText.getText());
				else {
					String message = NLS.bind(FunctionsMessages.CutOffFrequencyNotValidLabel, cutOffFrequencyText.getText());
					ButterworthHighPass.this.functionalBlockConfigurationDialog.setErrorMessage(message);
				}
			}
		});
		
		Label inputSignalLabel = new Label(paramContainer, SWT.NONE);
		inputSignalLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		inputSignalLabel.setText(FunctionsMessages.InputSignalLabel);
		
		ComboViewer inputSignalComboViewer = new ComboViewer(paramContainer, SWT.BORDER);
		inputSignalComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(inputSignalKey, "");
		inputSignalComboViewer.getCombo().setText(value);
		
		inputSignalComboViewer.setContentProvider(new ChannelsContentProvider(true, false, false, false, false, false, false));
		inputSignalComboViewer.setLabelProvider(LabelProvider.createTextProvider(new Function<Object, String>() {
			
			@Override
			public String apply(Object t) {
				if(!(t instanceof Channel)) return null;
				Channel channel = (Channel)t;
				if(channel.isSignal()) return channel.getFullName();
				return null;
			}
		}));
		inputSignalComboViewer.setInput(SelectedExprimentContributionItem.selectedExperiment);
		Channel channel = MathEngineFactory.getMathEngine().getChannelFromName(SelectedExprimentContributionItem.selectedExperiment, getProperty(inputSignalKey, ""));
		if(channel != null) inputSignalComboViewer.setSelection(new StructuredSelection(channel));
		else {
			String message = NLS.bind(DocometreMessages.ImpossibleToFindChannelTitle, value);
			Activator.logErrorMessage(message);
			this.functionalBlockConfigurationDialog.setErrorMessage(DocometreMessages.FunctionalBlockConfigurationDialogBlockingMessage);
		}
		
		inputSignalComboViewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ButterworthHighPass.this.functionalBlockConfigurationDialog.setErrorMessage(null);
				getTransientProperties().put(inputSignalKey, inputSignalComboViewer.getCombo().getText());
			}
		});
		
		Label outputSignalSuffixLabel = new Label(paramContainer, SWT.NONE);
		outputSignalSuffixLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		outputSignalSuffixLabel.setText(FunctionsMessages.OutputSignalSuffixLabel);
		Text outputSignalSuffixText = new Text(paramContainer, SWT.BORDER);
		outputSignalSuffixText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		value  = getProperty(outputSignalSuffixKey, "BHP");
		outputSignalSuffixText.setText(value);
		outputSignalSuffixText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getTransientProperties().put(outputSignalSuffixKey, outputSignalSuffixText.getText());
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
		String order = getProperty(orderKey, "2");
		String cutOffFrequency = getProperty(cutOffFrequencyKey, "5");
		String inputSignal = getProperty(inputSignalKey, "");
		String outputSignal = inputSignal + getProperty(outputSignalSuffixKey, "BHP");
		
		code = code.replaceAll(trialsListKey, trialsList).replaceAll(orderKey, order).replaceAll(cutOffFrequencyKey, cutOffFrequency);
		code = code.replaceAll(inputSignalKey, inputSignal).replaceAll(outputSignalSuffixKey, outputSignal);
		
		return code + "\n";
	}
	
	@Override
	public Block clone() {
		ButterworthHighPass function = new ButterworthHighPass();
		super.clone(function);
		return function;
	}

}
