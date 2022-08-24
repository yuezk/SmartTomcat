package com.poratu.idea.plugins.tomcat.setting;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.ui.CommonActionsPanel;
import com.intellij.util.IconUtil;
import com.poratu.idea.plugins.tomcat.utils.PluginUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Author : zengkid
 * Date   : 2017-02-23
 * Time   : 00:14
 */
public class TomcatSettingsConfigurable extends MasterDetailsComponent {

    @Override
    public String getDisplayName() {
        return "Tomcat Server";
    }

    @Override
    public String getHelpTopic() {
        return "Smart Tomcat Help";
    }

    public TomcatSettingsConfigurable() {
        initTree();
    }

    @Override
    protected @Nullable List<AnAction> createActions(boolean fromPopup) {
        List<AnAction> actions = new ArrayList<>();
        actions.add(new AddTomcatAction());
        actions.add(new MyDeleteAction((Predicate<Object[]>) null));
        return actions;
    }

    @Override
    public boolean isModified() {
        boolean modified = super.isModified();
        if (modified) {
            return true;
        }

        int size = TomcatSettingsState.getInstance().getTomcatInfos().size();
        return myRoot.getChildCount() != size;
    }

    @Override
    public void reset() {
        myRoot.removeAllChildren();

        TomcatSettingsState state = TomcatSettingsState.getInstance();
        for (TomcatInfo info : state.getTomcatInfos()) {
            addNode(info, false);
        }
        super.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        super.apply();

        List<TomcatInfo> tomcatInfos = TomcatSettingsState.getInstance().getTomcatInfos();
        tomcatInfos.clear();

        for (int i = 0; i < myRoot.getChildCount(); i++) {
            TomcatInfoConfigurable configurable = (TomcatInfoConfigurable) ((MyNode) myRoot.getChildAt(i)).getConfigurable();
            tomcatInfos.add(configurable.getEditableObject());
        }
    }

    @Override
    protected boolean wasObjectStored(Object editableObject) {
        return TomcatSettingsState.getInstance().getTomcatInfos().contains(editableObject);
    }

    private void addNode(TomcatInfo tomcatInfo, boolean selectInTree) {
        TomcatInfoConfigurable configurable = new TomcatInfoConfigurable(tomcatInfo, TREE_UPDATER, this::validateName);
        MyNode node = new MyNode(configurable);
        addNode(node, myRoot);

        if (selectInTree) {
            selectNodeInTree(node);
        }
    }

    private void validateName(String name) throws ConfigurationException {
        for (int i = 0; i < myRoot.getChildCount(); i++) {
            TomcatInfoConfigurable configurable = (TomcatInfoConfigurable) ((MyNode) myRoot.getChildAt(i)).getConfigurable();
            if (configurable.getEditableObject().getName().equals(name)) {
                throw new ConfigurationException("Duplicate name: \"" + name + "\"");
            }
        }
    }

    private class AddTomcatAction extends DumbAwareAction {
        public AddTomcatAction() {
            super("Add", "Add a Tomcat server", IconUtil.getAddIcon());
            registerCustomShortcutSet(CommonActionsPanel.getCommonShortcut(CommonActionsPanel.Buttons.ADD), myTree);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null, file -> {
                TomcatInfo tomcatInfo = TomcatSettingsState.createTomcatInfo(file.getPath(), this::createUniqueName);
                tomcatInfo.setName(tomcatInfo.getName());
                addNode(tomcatInfo, true);
            });
        }

        private String createUniqueName(String preferredName) {
            List<String> existingNames = new ArrayList<>();

            for (int i = 0; i < myRoot.getChildCount(); i++) {
                String displayName = ((MyNode) myRoot.getChildAt(i)).getDisplayName();
                existingNames.add(displayName);
            }

            return PluginUtils.generateSequentName(existingNames, preferredName);
        }
    }

}
