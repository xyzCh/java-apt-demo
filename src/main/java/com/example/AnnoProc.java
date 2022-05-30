package com.example;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.Trees;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.*;
import com.sun.tools.javac.util.*;

@SupportedAnnotationTypes("com.example.annotation.Entity")
public class AnnoProc extends AbstractProcessor {
    Messager messager;
    Trees trees;
    TreeMaker treeMaker;
    Names names;
    Symtab symtab;
    Type type;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        symtab = Symtab.instance(context);
        type = symtab.enterClass(symtab.unnamedModule, names.fromString("com.example.annotation.GetterName")).type;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.iterator().hasNext()) {
            TypeElement te = annotations.iterator().next();

            Set<? extends Element> annotatedElement = roundEnv.getElementsAnnotatedWith(te);
            for (Element el : annotatedElement) {
                trees.getTree(el).accept(new SimpleTreeVisitor<Void, Void>() {

                    @Override
                    public Void visitClass(ClassTree node, Void p) {
                        super.visitClass(node, p);
                        java.util.List<? extends Tree> members = node.getMembers();
                        int index = 0;
                        for (Tree m : members) {
                            if (m.getKind().equals(Kind.VARIABLE)) {
                                // todo: check field's modifier that is exists Getter annotation
                                ListBuffer<JCTree.JCExpression> params = new ListBuffer<JCTree.JCExpression>();
                                ListBuffer<JCTree.JCExpression> params1 = new ListBuffer<JCTree.JCExpression>();
                                ListBuffer<JCTree.JCExpression> elems = new ListBuffer<JCTree.JCExpression>();
                                JCTree.JCModifiers mods = ((JCTree.JCVariableDecl) m).getModifiers();
                                params.append(treeMaker.Assign(treeMaker.Ident(names.fromString("value")),
                                        treeMaker.Literal(((JCTree.JCVariableDecl) m).getName().toString())));
                                JCTree.JCAnnotation GetterName = treeMaker
                                        .Annotation(select("com.example.annotation.GetterName"), params.toList());
                                GetterName.setType(type);
                                elems.append(GetterName);
                                params1.append(
                                        treeMaker.Assign(
                                                treeMaker.Ident(names.fromString("onMethod_")),
                                                treeMaker.NewArray(null, List.nil(), elems.toList())));
                                JCTree.JCAnnotation Getter = treeMaker.Annotation(
                                        select("lombok.Getter"),
                                        params1.toList());
                                mods.annotations = mods.getAnnotations().append(Getter);

                                System.out.println(m.toString());
                            }
                            index++;
                        }

                        return null;
                    }
                }, null);
            }
            return false;
        }
        return true;
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    private JCTree.JCExpression select(String path) {
        JCTree.JCExpression expression = null;
        int i = 0;
        for (String split : path.split("\\.")) {
            if (i == 0)
                expression = treeMaker.Ident(names.fromString(split));
            else {
                expression = treeMaker.Select(expression, names.fromString(split));
            }
            i++;
        }

        return expression;
    }
}