package com.db.ebridge.alias.replacer;

import com.company.Equal;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.search.ProjectAndLibrariesScope;
import com.intellij.psi.search.searches.MethodReferencesSearch;

import java.util.Collection;

public class ReplacePropertiesAction extends AnAction {

    private final ConfigurationSource configurationSource;

    public ReplacePropertiesAction() {
        this.configurationSource = new ConfigurationSource();
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String configFileNames = Messages.showInputDialog(project,
                "Enter config file path",
                "Path to aliases",
                Messages.getQuestionIcon());
        System.out.println(configFileNames);

        ProjectAndLibrariesScope searchScope = new ProjectAndLibrariesScope(project);
        PsiMethod[] methods = JavaPsiFacade.getInstance(project).findClass(Equal.class.getName(), searchScope)
                .findMethodsByName("sample", true);
        PsiMethod method = methods[0];
        Collection<PsiReference> methodRefs = MethodReferencesSearch.search(method).findAll();

        for (PsiReference psiReference : methodRefs) {

            PsiElement element = psiReference.getElement();
            PsiExpression firstArgument = ((PsiMethodCallExpressionImpl) element.getParent()).getArgumentList().getExpressions()[0];

            configurationSource.mapAlias(firstArgument.getText().replaceAll("\"", "")).ifPresent(newName ->
                    ApplicationManager.getApplication().runReadAction(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
                        PsiExpression newExpr = JavaPsiFacade.getInstance(project)
                                .getElementFactory()
                                .createExpressionFromText("\"" + newName + "\"", element.getParent().getContext());
                        firstArgument.replace(newExpr);
                    }))
            );


            System.out.println(psiReference.getCanonicalText());
        }

    }
}
