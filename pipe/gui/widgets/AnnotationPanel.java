package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JSplitPane;

import pipe.dataLayer.AnnotationNote;

/*
 * ParameterPanel.java
 *
 * Created on April 15, 2007, 9:25 AM
 */

/**
 * @author  Pere Bonet
 */
public class AnnotationPanel extends javax.swing.JPanel {
   
   private AnnotationNote annotation;
   
   /**
    * Creates new form ParameterPanel
    */
   public AnnotationPanel(AnnotationNote _annotation) {
      annotation = _annotation;
      initComponents();
      textArea.setText(annotation.getText());
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      java.awt.GridBagConstraints gridBagConstraints;

      panel = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      textArea = new javax.swing.JTextArea();
      buttonPanel = new javax.swing.JPanel();
      okButton = new javax.swing.JButton();
      cancelButton = new javax.swing.JButton();

      setLayout(new BorderLayout());

      panel.setLayout(new java.awt.GridLayout(1, 0));

      panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit Annotation"));
      textArea.setColumns(20);
      textArea.setRows(5);
      jScrollPane1.setViewportView(textArea);

      panel.add(jScrollPane1);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
      buttonPanel.setLayout(new java.awt.GridBagLayout());

      okButton.setText("OK");
      okButton.setMaximumSize(new java.awt.Dimension(75, 25));
      okButton.setMinimumSize(new java.awt.Dimension(75, 25));
      okButton.setPreferredSize(new java.awt.Dimension(75, 25));
      okButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            okButtonActionPerformed(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 6;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
      buttonPanel.add(okButton, gridBagConstraints);

      cancelButton.setText("Cancel");
      cancelButton.setMaximumSize(new java.awt.Dimension(75, 25));
      cancelButton.setMinimumSize(new java.awt.Dimension(75, 25));
      cancelButton.setPreferredSize(new java.awt.Dimension(75, 25));
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
         }
      });

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
      buttonPanel.add(cancelButton, gridBagConstraints);
      
      JSplitPaneFix split = new JSplitPaneFix(JSplitPane.VERTICAL_SPLIT, panel, buttonPanel){
    	  @Override
    	  public int getMinimumDividerLocation(){
    		  return this.lastDividerLocation;
    	  }

    	  @Override
    	  public int getMaximumDividerLocation(){
    		  return this.lastDividerLocation;
    	  }
      };
      split.setResizeWeight(1.0);
      split.setDividerLocation(0.88);
      split.setContinuousLayout(true);
      split.setDividerSize(0);
      add(split);
      this.setPreferredSize(new Dimension(400, 300));
   }// </editor-fold>//GEN-END:initComponents

   private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      exit();
   }//GEN-LAST:event_cancelButtonActionPerformed

   
   private void focusGained(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
      textField.moveCaretPosition(textField.getText().length());
   }
   
   private void focusLost(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
   }   
   
   private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
      annotation.setText(textArea.getText());
      annotation.repaint();
      exit();   
   }//GEN-LAST:event_okButtonActionPerformed
   
   
   private void exit() {
      //Provisional!
      this.getParent().getParent().getParent().getParent().setVisible(false);
   }      
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel buttonPanel;
   private javax.swing.JButton cancelButton;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JButton okButton;
   private javax.swing.JPanel panel;
   private javax.swing.JTextArea textArea;
   // End of variables declaration//GEN-END:variables
   
}
