/*******************************************************************************
 * Copyright or © or Copr. Institut des Sciences du Mouvement 
 * (CNRS & Aix Marseille Université)
 * 
 * The DOCoMETER Software must be used with a real time data acquisition 
 * system marketed by ADwin (ADwin Pro and Gold, I and II) or an Arduino 
 * Uno. This software, created within the Institute of Movement Sciences, 
 * has been developed to facilitate their use by a "neophyte" public in the 
 * fields of industrial computing and electronics.  Students, researchers or 
 * engineers can configure this acquisition system in the best possible 
 * conditions so that it best meets their experimental needs. 
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 * 
 * Contributors:
 *  - Frank Buloup - frank.buloup@univ-amu.fr - initial API and implementation [25/03/2020]
 ******************************************************************************/
package demo.gui;

/*
 * Copyright (c) 2013, Joshua Kaplan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 *    disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other materials provided with the distribution.
 *  - Neither the name of matlabcontrol nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The panel that contains the options to select elements of the array.
 * 
 * @author <a href="mailto:nonother@gmail.com">Joshua Kaplan</a>
 */
@SuppressWarnings("serial")
class ArrayPanel extends JPanel
{
    //Input options: String or Double
    private static final int DOUBLE_INDEX = 0, STRING_INDEX = 1;
    
    private static final String[] OPTIONS = new String[2];
    static
    {
        OPTIONS[DOUBLE_INDEX] = "Double";
        OPTIONS[STRING_INDEX] = "String";
    }
    
    /**
     * Number of fields and drop down lists.
     */
    public static final int NUM_ENTRIES = 3;
    
    /**
     * Drop down lists to choose between object types.
     */
    private final JComboBox[] _optionBoxes;
    
    /**
     * Fields for inputting values.
     */
    private final JTextField[] _entryFields;
    
    public ArrayPanel()
    {
        super(new GridLayout(NUM_ENTRIES,2));
        this.setBackground(Color.WHITE);
        
        //Drop down lists and input fields
        _optionBoxes = new JComboBox[NUM_ENTRIES];
        _entryFields = new JTextField[NUM_ENTRIES];
        
        for(int i = 0; i < NUM_ENTRIES; i++)
        {
            _optionBoxes[i] = new JComboBox(OPTIONS);
            _entryFields[i] = new JTextField(8);
            this.add(_optionBoxes[i]);
            this.add(_entryFields[i]);
        }
    }
    
    /**
     * Take the elements of the fields and put them into an array.
     * 
     * @return
     */
    public Object[] getArray()
    {
        ArrayList<Object> entries = new ArrayList<Object>();
        for(int i = 0; i < NUM_ENTRIES; i++)
        {
            if(!_entryFields[i].getText().isEmpty())
            {
                if(_optionBoxes[i].getSelectedIndex() == DOUBLE_INDEX)
                {
                    try
                    {
                        entries.add(Double.parseDouble(_entryFields[i].getText()));
                    }
                    catch(Exception e)
                    {
                        entries.add(0);
                    }
                }
                if(_optionBoxes[i].getSelectedIndex() == STRING_INDEX)
                {
                    entries.add(_entryFields[i].getText());
                }
            }
        }
        
        return entries.toArray();
    }
    
    /**
     * Return the first entry of the fields.
     * 
     * @return
     */
    public Object getFirstEntry()
    {
        if(!_entryFields[0].getText().isEmpty())
        {
            if(_optionBoxes[0].getSelectedIndex() == DOUBLE_INDEX)
            {
                try
                {
                    return Double.parseDouble(_entryFields[0].getText());
                }
                catch(Exception e)
                {
                    return 0;
                }
            }
            if(_optionBoxes[0].getSelectedIndex() == STRING_INDEX)
            {
                return _entryFields[0].getText();
            }
        }
        
        return null;
    }
    
    /**
     * Enable the first {@code n} fields for input. The others are disabled.
     * 
     * @param n
     */
    public void enableInputFields(int n)
    {
        for(int i = 0; i < NUM_ENTRIES; i++)
        {
            _optionBoxes[i].setEnabled(i < n);
            _entryFields[i].setEnabled(i < n);
        }
    }
}
