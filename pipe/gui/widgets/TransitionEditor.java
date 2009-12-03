package pipe.gui.widgets;

import java.awt.Color;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretListener;

import pipe.dataLayer.DataLayer;
import pipe.dataLayer.RateParameter;
import pipe.dataLayer.Transition;
import pipe.gui.GuiView;


/**
 *
 * @author  pere
 */
public class TransitionEditor 
        extends javax.swing.JPanel {
   
   Transition transition;
   boolean attributesVisible;
   boolean timed;
   boolean infiniteServer;
   Integer priority = 0;
   Double rate;
   String name;   
   RateParameter rParameter;
   DataLayer pnmlData;
   GuiView view;
   JRootPane rootPane;
   
   
   /**
    * Creates new form PlaceEditor
    */
   public TransitionEditor(JRootPane _rootPane, Transition _transition, 
           DataLayer _pnmlData, GuiView _view) {
      transition = _transition;
      pnmlData = _pnmlData;
      view = _view;
      rParameter = transition.getRateParameter();
      name = transition.getName();
      timed = transition.isTimed();
      infiniteServer = transition.isInfiniteServer();
      rootPane = _rootPane;
      
      initComponents();
      
      this.serverLabel.setVisible(false);
      this.serverPanel.setVisible(false);
      
      rootPane.setDefaultButton(okButton);

      attributesVisible = transition.getAttributesVisible();
      
      rate = transition.getRate();
         
      if (timed){
         timedTransition();
      } else {
         immediateTransition();
         priority = _transition.getPriority();
      }
      
      if (infiniteServer) {
         infiniteServerRadioButton.setSelected(true);
      } else {
         singleServerRadioButton.setSelected(true);
      }
      
      
      if (rParameter != null){
         for (int i = 1; i < rateComboBox.getItemCount(); i++) {
            if (rParameter == (RateParameter)rateComboBox.getItemAt(i)){
               rateComboBox.setSelectedIndex(i);
            }
         }
      }      
   }
   
   
   private void timedTransition(){
      timedRadioButton.setSelected(true);
      
      rateLabel.setText("Rate:");
      rateTextField.setText("" + transition.getRate());
      
      prioritySlider.setEnabled(false);
      priorityTextField.setText("0");
      
      Enumeration buttons = semanticsButtonGroup.getElements();
      while (buttons.hasMoreElements()){
         ((AbstractButton)buttons.nextElement()).setEnabled(true);
      }      
      
      priorityLabel.setEnabled(false);
      priorityPanel.setEnabled(false);

      RateParameter[] rates = pnmlData.getRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
         for (int i = 0; i < rates.length; i++) {
            rateComboBox.addItem(rates[i]);
         }
      } else {
         rateComboBox.setEnabled(false);
      }      
   }
   
   
   private void immediateTransition(){
      immediateRadioButton.setSelected(true); 
      
      rateLabel.setText("Weight:");
      rateTextField.setText("" + transition.getRate());      
      
      prioritySlider.setEnabled(true);
      priorityTextField.setText("" + transition.getPriority());
      
      priorityLabel.setEnabled(true);
      priorityPanel.setEnabled(true);      

      RateParameter[] rates = pnmlData.getRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
         for (int i = 0; i < rates.length; i++) {
            rateComboBox.addItem(rates[i]);
         }
      } else {
         rateComboBox.setEnabled(false);
      }            
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      java.awt.GridBagConstraints gridBagConstraints;

      timingButtonGroup = new javax.swing.ButtonGroup();
      semanticsButtonGroup = new javax.swing.ButtonGroup();
      transitionEditorPanel = new javax.swing.JPanel();
      nameLabel = new javax.swing.JLabel();
      nameTextField = new javax.swing.JTextField();
      rateLabel = new javax.swing.JLabel();
      priorityLabel = new javax.swing.JLabel();
      attributesCheckBox = new javax.swing.JCheckBox();
      rateComboBox = new javax.swing.JComboBox();
      timingPanel = new javax.swing.JPanel();
      timedRadioButton = new javax.swing.JRadioButton();
      immediateRadioButton = new javax.swing.JRadioButton();
      serverPanel = new javax.swing.JPanel();
      singleServerRadioButton = new javax.swing.JRadioButton();
      infiniteServerRadioButton = new javax.swing.JRadioButton();
      rotationLabel = new javax.swing.JLabel();
      rotationComboBox = new javax.swing.JComboBox();
      rateTextField = new javax.swing.JTextField();
      serverLabel = new javax.swing.JLabel();
      timingLabel = new javax.swing.JLabel();
      priorityPanel = new javax.swing.JPanel();
      prioritySlider = new javax.swing.JSlider();
      priorityTextField = new javax.swing.JTextField();
      buttonPanel = new javax.swing.JPanel();
      cancelButton = new javax.swing.JButton();
      okButton = new javax.swing.JButton();

      setLayout(new java.awt.GridBagLayout());

      transitionEditorPanel.setLayout(new java.awt.GridBagLayout());

      transitionEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transition Editor"));
      nameLabel.setText("Name:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(nameLabel, gridBagConstraints);

      nameTextField.setText(transition.getName());
      nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
         @Override
		public void focusGained(java.awt.event.FocusEvent evt) {
            nameTextFieldFocusGained(evt);
         }
         @Override
		public void focusLost(java.awt.event.FocusEvent evt) {
            nameTextFieldFocusLost(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(nameTextField, gridBagConstraints);

      rateLabel.setText("Rate:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(rateLabel, gridBagConstraints);

      priorityLabel.setText("Priority:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(priorityLabel, gridBagConstraints);

      attributesCheckBox.setSelected(transition.getAttributesVisible());
      attributesCheckBox.setText("Show transition attributes");
      attributesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      attributesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(attributesCheckBox, gridBagConstraints);

      rateComboBox.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            rateComboBoxActionPerformed(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(rateComboBox, gridBagConstraints);

      timingPanel.setLayout(new java.awt.GridLayout());

      timingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
      timingButtonGroup.add(timedRadioButton);
      timedRadioButton.setText("Timed");
      timedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      timedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      timedRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
      timedRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
      timedRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
      timedRadioButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            timedRadioButtonActionPerformed(evt);
         }
      });

      timingPanel.add(timedRadioButton);

      timingButtonGroup.add(immediateRadioButton);
      immediateRadioButton.setText("Immediate");
      immediateRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      immediateRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      immediateRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
      immediateRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
      immediateRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
      immediateRadioButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            immediateRadioButtonActionPerformed(evt);
         }
      });

      timingPanel.add(immediateRadioButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(timingPanel, gridBagConstraints);

      serverPanel.setLayout(new java.awt.GridLayout());

      serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
      semanticsButtonGroup.add(singleServerRadioButton);
      singleServerRadioButton.setText("Single");
      singleServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      singleServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      serverPanel.add(singleServerRadioButton);

      semanticsButtonGroup.add(infiniteServerRadioButton);
      infiniteServerRadioButton.setSelected(true);
      infiniteServerRadioButton.setText("Infinite");
      infiniteServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      infiniteServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
      serverPanel.add(infiniteServerRadioButton);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(serverPanel, gridBagConstraints);

      rotationLabel.setText("Rotation:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(rotationLabel, gridBagConstraints);

      rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "+45\u00B0", "+90\u00B0", "-45\u00B0" }));
      rotationComboBox.setMaximumSize(new java.awt.Dimension(70, 20));
      rotationComboBox.setMinimumSize(new java.awt.Dimension(70, 20));
      rotationComboBox.setPreferredSize(new java.awt.Dimension(70, 20));
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(rotationComboBox, gridBagConstraints);

      rateTextField.setMaximumSize(new java.awt.Dimension(40, 19));
      rateTextField.setMinimumSize(new java.awt.Dimension(40, 19));
      rateTextField.setPreferredSize(new java.awt.Dimension(40, 19));
      rateTextField.addCaretListener(new javax.swing.event.CaretListener() {
         public void caretUpdate(javax.swing.event.CaretEvent evt) {
            rateTextFieldCaretUpdate(evt);
         }
      });
      rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
         @Override
		public void focusGained(java.awt.event.FocusEvent evt) {
            rateTextFieldFocusGained(evt);
         }
         @Override
		public void focusLost(java.awt.event.FocusEvent evt) {
            rateTextFieldFocusLost(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(rateTextField, gridBagConstraints);

      serverLabel.setText("Server:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(serverLabel, gridBagConstraints);

      timingLabel.setText("Timing:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      transitionEditorPanel.add(timingLabel, gridBagConstraints);

      prioritySlider.setMajorTickSpacing(50);
      prioritySlider.setMaximum(127);
      prioritySlider.setMinimum(1);
      prioritySlider.setMinorTickSpacing(1);
      prioritySlider.setSnapToTicks(true);
      prioritySlider.setToolTipText("1: lowest priority; 127: highest priority");
      prioritySlider.setValue(transition.getPriority());
      prioritySlider.addChangeListener(new javax.swing.event.ChangeListener() {
         public void stateChanged(javax.swing.event.ChangeEvent evt) {
            prioritySliderStateChanged(evt);
         }
      });

      priorityPanel.add(prioritySlider);

      //prova
      priorityTextField.setEditable(false);
      priorityTextField.setHorizontalAlignment(SwingConstants.RIGHT);
      priorityTextField.setText("1");
      priorityTextField.setMaximumSize(new java.awt.Dimension(36, 19));
      priorityTextField.setMinimumSize(new java.awt.Dimension(36, 19));
      priorityTextField.setPreferredSize(new java.awt.Dimension(36, 19));
      priorityTextField.setText(""+transition.getPriority());
      priorityPanel.add(priorityTextField);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 3;
      transitionEditorPanel.add(priorityPanel, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      add(transitionEditorPanel, gridBagConstraints);

      buttonPanel.setLayout(new java.awt.GridBagLayout());

      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonHandler(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      buttonPanel.add(cancelButton, gridBagConstraints);

      okButton.setText("OK");
      okButton.setMaximumSize(new java.awt.Dimension(75, 25));
      okButton.setMinimumSize(new java.awt.Dimension(75, 25));
      okButton.setPreferredSize(new java.awt.Dimension(75, 25));
      okButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            okButtonHandler(evt);
         }
      });
      okButton.addKeyListener(new java.awt.event.KeyAdapter() {
         @Override
		public void keyPressed(java.awt.event.KeyEvent evt) {
            okButtonKeyPressed(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      buttonPanel.add(okButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 8, 3);
      add(buttonPanel, gridBagConstraints);

   }// </editor-fold>//GEN-END:initComponents

   private void rateTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_rateTextFieldCaretUpdate
      try {
         if ((rateComboBox.getSelectedIndex() > 0) &&
            (((RateParameter)rateComboBox.getSelectedItem()).getValue()
                    != Double.parseDouble(rateTextField.getText()))){
            rateComboBox.setSelectedIndex(0);
         }
      } catch (NumberFormatException nfe){
         if (!nfe.getMessage().equalsIgnoreCase("empty String")) {
            System.out.println("NumberFormatException (not Empty String): \n" +
                    nfe.getMessage());
         }
      } catch (Exception e){
         System.out.println(e.toString());
      }
   }//GEN-LAST:event_rateTextFieldCaretUpdate

   private void rateTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusLost
      focusLost(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusLost

   private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
      focusLost(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusLost

   private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
      focusGained(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusGained

   private void rateTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusGained
      focusGained(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusGained

   
   
   private void focusGained(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
      textField.moveCaretPosition(textField.getText().length());
   }
   
   private void focusLost(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
   }   
   
   
   private void rateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateComboBoxActionPerformed
      int index = rateComboBox.getSelectedIndex();
      if (index > 0){
         rateTextField.setText(pnmlData.getRateParameters()[index-1].getValue().toString());
      }
   }//GEN-LAST:event_rateComboBoxActionPerformed

   
   private void timedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timedRadioButtonActionPerformed
      if (timedRadioButton.isSelected()){
         timedTransition();
      } else {
         immediateTransition();
      }
   }//GEN-LAST:event_timedRadioButtonActionPerformed

   private void immediateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_immediateRadioButtonActionPerformed
      if (immediateRadioButton.isSelected()){
         immediateTransition();
      } else {
         timedTransition();
      }
   }//GEN-LAST:event_immediateRadioButtonActionPerformed

   private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prioritySliderStateChanged
      priorityTextField.setText("" +prioritySlider.getValue());
   }//GEN-LAST:event_prioritySliderStateChanged

   
   private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
         okButtonHandler(new java.awt.event.ActionEvent(this,0,""));
      }
   }//GEN-LAST:event_okButtonKeyPressed


   CaretListener caretListener = new javax.swing.event.CaretListener() {
      public void caretUpdate(javax.swing.event.CaretEvent evt) {
         JTextField textField = (JTextField)evt.getSource();
         textField.setBackground(new Color(255,255,255));
         //textField.removeChangeListener(this);
      }
   };   
   
   
   
   private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler

      view.getUndoManager().newEdit(); // new "transaction""
       
      String newName = nameTextField.getText();
      if (!newName.equals(name)){
         view.getUndoManager().addEdit(transition.setPNObjectName(newName));
      }

      if (timedRadioButton.isSelected() != timed) {
         view.getUndoManager().addEdit(
                 transition.setTimed(!timed));
      }
      

      if (infiniteServerRadioButton.isSelected() != infiniteServer) {
         view.getUndoManager().addEdit(
                 transition.setInfiniteServer(!infiniteServer));
      }      
      
      int newPriority = prioritySlider.getValue();
      if (newPriority != priority && !transition.isTimed()) {
         view.getUndoManager().addEdit(transition.setPriority(newPriority));
      }
      
      if (rateComboBox.getSelectedIndex() > 0) {
         // There's a rate parameter selected
         RateParameter parameter = 
                 (RateParameter)rateComboBox.getSelectedItem() ;
         if (parameter != rParameter){

            if (rParameter != null) {
               // The rate parameter has been changed
               view.getUndoManager().addEdit(transition.changeRateParameter(
                       (RateParameter)rateComboBox.getSelectedItem()));
            } else {
               // The rate parameter has been changed
               view.getUndoManager().addEdit(transition.setRateParameter(
                       (RateParameter)rateComboBox.getSelectedItem()));
            }
         }
      } else {
         // There is no rate parameter selected
         if (rParameter != null) {
            // The rate parameter has been changed
            view.getUndoManager().addEdit(transition.clearRateParameter());
         }
         try{
            Double newRate = Double.parseDouble(rateTextField.getText());
            if (newRate != rate) {
               view.getUndoManager().addEdit(transition.setRate(newRate));
            }
         } catch (NumberFormatException nfe){
            rateTextField.setBackground(new Color(255,0,0));
            rateTextField.addCaretListener(caretListener);
            return;
         } catch (Exception e){
            System.out.println(":" + e);
         }
      } 

      if (attributesVisible != attributesCheckBox.isSelected()){
         transition.toggleAttributesVisible();
      }      
            
      Integer rotationIndex = rotationComboBox.getSelectedIndex();
      if (rotationIndex > 0) {
         int angle = 0;
         switch (rotationIndex) {
            case 1:
               angle = 45;
               break;
            case 2:
               angle = 90;
               break;
            case 3:
               angle = 135; //-45
               break;
            default:
               break;               
         }
         if (angle != 0) {
            view.getUndoManager().addEdit(transition.rotate(angle));
         }
      }
      transition.repaint();
      exit();
   }//GEN-LAST:event_okButtonHandler

   private void exit() {
      rootPane.getParent().setVisible(false);
   }
   
   
   private void cancelButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonHandler
      //Provisional!
      exit();
   }//GEN-LAST:event_cancelButtonHandler
      
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JCheckBox attributesCheckBox;
   private javax.swing.JPanel buttonPanel;
   private javax.swing.JButton cancelButton;
   private javax.swing.JRadioButton immediateRadioButton;
   private javax.swing.JRadioButton infiniteServerRadioButton;
   private javax.swing.JLabel nameLabel;
   private javax.swing.JTextField nameTextField;
   private javax.swing.JButton okButton;
   private javax.swing.JLabel priorityLabel;
   private javax.swing.JPanel priorityPanel;
   private javax.swing.JSlider prioritySlider;
   private javax.swing.JTextField priorityTextField;
   private javax.swing.JComboBox rateComboBox;
   private javax.swing.JLabel rateLabel;
   private javax.swing.JTextField rateTextField;
   private javax.swing.JComboBox rotationComboBox;
   private javax.swing.JLabel rotationLabel;
   private javax.swing.ButtonGroup semanticsButtonGroup;
   private javax.swing.JLabel serverLabel;
   private javax.swing.JPanel serverPanel;
   private javax.swing.JRadioButton singleServerRadioButton;
   private javax.swing.JRadioButton timedRadioButton;
   private javax.swing.ButtonGroup timingButtonGroup;
   private javax.swing.JLabel timingLabel;
   private javax.swing.JPanel timingPanel;
   private javax.swing.JPanel transitionEditorPanel;
   // End of variables declaration//GEN-END:variables
   
}
