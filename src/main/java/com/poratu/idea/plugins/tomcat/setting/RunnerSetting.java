package com.poratu.idea.plugins.tomcat.setting;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.RawCommandLineEditor;
import com.poratu.idea.plugins.tomcat.utils.PluginUtils;
import org.jdesktop.swingx.JXButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * Author : zengkid
 * Date   : 2017-02-23
 * Time   : 00:13
 */
public class RunnerSetting {
    private final Project project;
    private JPanel mainPanel;
    private ComboboxWithBrowseButton tomcatField;
    private TextFieldWithBrowseButton docBaseField;
    private JTextField contextPathField;
    private JFormattedTextField portField;
    private JXButton configrationButton;
    private RawCommandLineEditor vmOptons;
    private EnvironmentVariablesComponent envOptions;
    private JFormattedTextField adminPort;
    private Module module;

    public RunnerSetting(Project project) {
        this.project = project;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ComboboxWithBrowseButton getTomcatField() {
        return tomcatField;
    }

    public TextFieldWithBrowseButton getDocBaseField() {
        return docBaseField;
    }

    public JTextField getContextPathField() {
        return contextPathField;
    }

    public JFormattedTextField getPortField() {
        return portField;
    }


    public JFormattedTextField getAdminPort() {
        return adminPort;
    }

    public JXButton getConfigrationButton() {
        return configrationButton;
    }

    public RawCommandLineEditor getVmOptons() {
        return vmOptons;
    }

    public EnvironmentVariablesComponent getEnvOptions() {
        return envOptions;
    }

    private void createUIComponents() {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();


        tomcatField = new ComboboxWithBrowseButton();
        JComboBox<TomcatInfo> comboBox = tomcatField.getComboBox();

        List<TomcatInfo> tomcatInfos = TomcatSettingsState.getInstance().getTomcatInfos();
        CollectionComboBoxModel<TomcatInfo> aModel = new CollectionComboBoxModel<>(tomcatInfos);
        comboBox.setModel(aModel);

        tomcatField.addBrowseFolderListener("Tomcat Server", "Please choose tomcat server path", project, fileChooserDescriptor, new TextComponentAccessor<JComboBox>() {
            public String getText(JComboBox comboBox) {
                Object item = comboBox.getEditor().getItem();
                return item.toString();
            }

            public void setText(JComboBox comboBox, @NotNull String text) {
                TomcatInfo tomcatInfo = TomcatSettingsState.createTomcatInfo(text);

                CollectionComboBoxModel<TomcatInfo> model = (CollectionComboBoxModel) comboBox.getModel();

                if (model.contains(tomcatInfo)) {
                    int maxVersion = TomcatSettingsState.getInstance().getMaxVersion(tomcatInfo);
                    tomcatInfo.setNumber(maxVersion + 1);
                }

                model.add(model.getSize(), tomcatInfo);
                model.setSelectedItem(tomcatInfo);
            }
        });
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
}
