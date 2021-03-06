package fr.univamu.ism.docometre.analyse;

import fr.univamu.ism.docometre.Activator;
import fr.univamu.ism.docometre.preferences.MathEnginePreferencesConstants;

public final class MathEngineFactory {
	
	private static MathEngine mathEngine;
	
	public static MathEngine getMathEngine() {
		String mathEnginePref = Activator.getDefault().getPreferenceStore().getString(MathEnginePreferencesConstants.MATH_ENGINE);
		if(mathEngine != null) {
			if(MathEnginePreferencesConstants.MATH_ENGINE_MATLAB.equals(mathEnginePref) && mathEngine instanceof MatlabEngine) return mathEngine;
			if(MathEnginePreferencesConstants.MATH_ENGINE_PYTHON.equals(mathEnginePref) && mathEngine instanceof PythonEngine) return mathEngine;
		}
		clear();
		if(MathEnginePreferencesConstants.MATH_ENGINE_MATLAB.equals(mathEnginePref)) {
			mathEngine = new MatlabEngine();
		}
		if(MathEnginePreferencesConstants.MATH_ENGINE_PYTHON.equals(mathEnginePref)) {
			mathEngine = new PythonEngine();
		}
		return mathEngine;
	}

	public static void clear() {
		mathEngine = null;
	}
	
	public static boolean isMatlab() {
		String mathEnginePref = Activator.getDefault().getPreferenceStore().getString(MathEnginePreferencesConstants.MATH_ENGINE);
		return MathEnginePreferencesConstants.MATH_ENGINE_MATLAB.equals(mathEnginePref);
	}
	
	public static boolean isPython() {
		String mathEnginePref = Activator.getDefault().getPreferenceStore().getString(MathEnginePreferencesConstants.MATH_ENGINE);
		return MathEnginePreferencesConstants.MATH_ENGINE_PYTHON.equals(mathEnginePref);
	}
	
}
