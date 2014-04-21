package mods.battlegear2.client.utils;

import net.minecraft.util.StatCollector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageSplitDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JLabel imageLabel;

    public BufferedImage imageSection;

    private BufferedImage image;
    private JSlider y2slider;
    private JSlider x2slider;
    private JSlider x1slider;
    private JSlider y1slider;

    /**
     * Create the dialog.
     */
    public ImageSplitDialog(BufferedImage bi) {
        setBounds(100, 100, 453, 470);
        image = bi;
        setModal(true);
        this.setAlwaysOnTop(true);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JLabel lblNewLabel = new JLabel(StatCollector.translateToLocal("gui.splitter.number.x.sections"));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));

        imageLabel = new JLabel("");
        panel.add(imageLabel, BorderLayout.CENTER);


        x2slider = new JSlider();
        x2slider.setValue(1);
        x2slider.setPaintTicks(true);
        x2slider.setSnapToTicks(true);
        x2slider.setPaintLabels(true);
        x2slider.setMajorTickSpacing(1);
        x2slider.setMinorTickSpacing(1);
        x2slider.setMinimum(1);
        x2slider.setMaximum(4);

        x1slider = new JSlider();
        x1slider.setValue(4);
        x1slider.setSnapToTicks(true);
        x1slider.setPaintTicks(true);
        x1slider.setPaintLabels(true);
        x1slider.setMinorTickSpacing(1);
        x1slider.setMinimum(1);
        x1slider.setMaximum(4);
        x1slider.setMajorTickSpacing(1);

        JLabel lblNewLabel_1 = new JLabel(StatCollector.translateToLocal("gui.splitter.x.section"));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);


        y2slider = new JSlider();
        y2slider.setValue(1);
        y2slider.setInverted(true);
        y2slider.setMajorTickSpacing(1);
        y2slider.setMinorTickSpacing(1);
        y2slider.setMinimum(1);
        y2slider.setMaximum(4);
        y2slider.setSnapToTicks(true);
        y2slider.setPaintTicks(true);
        y2slider.setPaintLabels(true);
        y2slider.setOrientation(SwingConstants.VERTICAL);

        JLabel lblYSection = new JLabel(StatCollector.translateToLocal("gui.splitter.y.section"));
        lblYSection.setHorizontalAlignment(SwingConstants.CENTER);

        y1slider = new JSlider();
        y1slider.setValue(4);
        y1slider.setSnapToTicks(true);
        y1slider.setPaintTicks(true);
        y1slider.setPaintLabels(true);
        y1slider.setOrientation(SwingConstants.VERTICAL);
        y1slider.setMinorTickSpacing(1);
        y1slider.setMinimum(1);
        y1slider.setMaximum(4);
        y1slider.setMajorTickSpacing(1);

        String[] split = StatCollector.translateToLocal("gui.splitter.number.y.sections").split("\\*");
        JLabel lblNewLabel_2 = new JLabel();
        JLabel lblYSections = new JLabel();
        if(split.length > 1){
            lblNewLabel_2.setText(split[0]);
            lblYSections.setText(split[1]);
        }else{
            lblYSections.setText(split[0]);
        }

        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        lblYSections.setHorizontalAlignment(SwingConstants.CENTER);
        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(lblNewLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(x1slider, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblNewLabel_1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(x2slider, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(y2slider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblYSection, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(y1slider, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                                        .addComponent(lblYSections, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(44, Short.MAX_VALUE))
        );
        gl_contentPanel.setVerticalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblNewLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(x1slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblNewLabel_1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(x2slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblYSection)
                                                        .addGroup(gl_contentPanel.createSequentialGroup()
                                                                .addComponent(lblNewLabel_2)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(lblYSections)))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(y2slider, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                                                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(y1slider, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        x1slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                x2slider.setMaximum(source.getValue());

                resetImage();
            }
        });

        x2slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                resetImage();
            }
        });

        y1slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                y2slider.setMaximum(source.getValue());
                resetImage();
            }
        });

        y2slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                resetImage();
            }
        });

        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        imageSection =
                                image.getSubimage(
                                        (int)(((float)(getX2slider().getValue()-1) / getX1slider().getValue())*image.getWidth()),
                                        (int)(((float)(getY2slider().getValue()-1) / getY1slider().getValue())*image.getHeight()),
                                        (int)(((float)(image.getWidth() / getX1slider().getValue()))),
                                        (int)(((float)(image.getHeight() / getY1slider().getValue()))));

                        setVisible(false);
                    }
                });
            }
            {
                JButton cancelButton = new JButton("Cancel");
                buttonPane.add(cancelButton);
                cancelButton.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
            }
        }
        resetImage();
    }

    private void resetImage(){

        BufferedImage before = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
        BufferedImage scaled = before;
        int width = 250;
        int height = 250;

        if(before.getWidth() != width || before.getHeight() != height){ //If the hight of the image is not our targert
            scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); //Create a new empty image of the target size
            AffineTransform at = new AffineTransform(); //Create a new Affine Transform
            at.scale((float)width / before.getWidth(), (float)height / before.getHeight()); //Scale the image to the size we want
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC); // use the bi linear transfomation mode
            scaled = scaleOp.filter(before, scaled); //Scale it
        }

        int xBound = 250 / this.getX1slider().getValue();
        int yBound = 250 / this.getY1slider().getValue();

        for(int x = 0; x < width; x++){
            if(x > xBound*(getX2slider().getValue()-1) && x < xBound*(getX2slider().getValue())){
                for(int y = 0; y < height; y++){
                    if(! (y > yBound*(getY2slider().getValue()-1) && y < yBound*(getY2slider().getValue()))){
                        scaled.setRGB(x, y, ((scaled.getRGB(x, y) & 0xfefefe) >> 1) | 0xFF000000);
                    }
                }
            }else{
                for(int y = 0; y < height; y++)


                    scaled.setRGB(x, y, ((scaled.getRGB(x, y) & 0xfefefe) >> 1) | 0xFF000000);
            }
        }

        ImageIcon icon = new ImageIcon(scaled);
        imageLabel.setIcon(icon);
    }

    public JSlider getY2slider() {
        return y2slider;
    }

    public JSlider getX2slider() {
        return x2slider;
    }

    public JSlider getX1slider() {
        return x1slider;
    }

    public JSlider getY1slider() {
        return y1slider;
    }
}
