package com.mockrunner.test.gen;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.mockrunner.util.ArrayUtil;
import com.mockrunner.util.ClassUtil;
import com.mockrunner.util.StringUtil;

public class JavaClassGenerator
{
    private String packageInfo;
    private List imports;
    private String className;
    private String superClass;
    private List interfaces;
    private List memberTypes;
    private List memberNames;
    private String[] classCommentLines;
    private boolean createJavaDocComments;
    private List methods;
    private List constructors;
    
    public JavaClassGenerator()
    {
        reset();
    }
    
    public void reset()
    {
        imports = new ArrayList();
        interfaces = new ArrayList();
        memberTypes = new ArrayList();
        memberNames = new ArrayList();
        createJavaDocComments = true;
        methods = new ArrayList();
        constructors = new ArrayList();
    }

    public void setCreateJavaDocComments(boolean createJavaDocComments)
    {
        this.createJavaDocComments = createJavaDocComments;
    }
    
    public void setPackage(Package packageObj)
    {
        this.packageInfo = packageObj.getName();
    }
    
    public void setClassName(String className)
    {
        this.className = className;
    }
    
    public void setSuperClass(Class superClass)
    {
        this.superClass = ClassUtil.getClassName(superClass);
        addImportIfNecessary(superClass);
    }
    
    public void addImport(Class importClass)
    {
        imports.add(importClass.getName());
    }
    
    public void addInterfaceImplementation(Class interfaceClass)
    {
        interfaces.add(ClassUtil.getClassName(interfaceClass));
        addImportIfNecessary(interfaceClass);
    }
    
    public void setClassComment(String[] commentLines)
    {
        classCommentLines = (String[])ArrayUtil.copyArray(commentLines);
    }
    
    public void addMemberDeclaration(Class memberType, String name)
    {
        memberTypes.add(ClassUtil.getClassName(memberType));
        memberNames.add(name);
        addImportIfNecessary(memberType);
    }
    
    public void addConstructorDeclaration()
    {
        constructors.add(new ConstructorDeclaration());
    }
    
    public void addConstructorDeclaration(ConstructorDeclaration constructor)
    {
        constructors.add(constructor);
        addImportsForArguments(constructor);
    }
    
    public void addMethodDeclaration(MethodDeclaration method)
    {
        methods.add(method);
        addImportsForArguments(method);
        addImportForReturnType(method);
    }

    public String generate()
    {
        JavaLineAssembler assembler = new JavaLineAssembler();
        assembler.appendPackageInfo(packageInfo);
        assembler.appendImports(imports);
        assembler.appendNewLine();
        appendCommentBlock(assembler, classCommentLines);
        assembler.appendClassDefintion(className, superClass, (String[])interfaces.toArray(new String[interfaces.size()]));
        assembler.appendLeftBrace();
        assembler.appendNewLine();
        assembler.setIndentLevel(1);
        appendMembers(assembler);
        appendConstructors(assembler);
        appendMethods(assembler);
        assembler.setIndentLevel(0);
        assembler.appendRightBrace();
        return assembler.getResult();
    }
    
    private void appendCommentBlock(JavaLineAssembler assembler, String[] commentLines)
    {
        if(createJavaDocComments)
        {
            assembler.appendJavaDocComment(commentLines);
        }
        else
        {
            assembler.appendBlockComment(commentLines);
        }
    }

    private void appendMembers(JavaLineAssembler assembler)
    {
        for(int ii = 0; ii < memberTypes.size(); ii++)
        {
            assembler.appendMemberDeclaration((String)memberTypes.get(ii), (String)memberNames.get(ii));
        }
    }
    
    private void appendMethods(JavaLineAssembler assembler)
    {
        for(int ii = 0; ii < methods.size(); ii++)
        {
            MethodDeclaration declaration = (MethodDeclaration)methods.get(ii);
            assembler.appendNewLine();
            assembler.appendBlockComment(declaration.getCommentLines());
            String[] modifiers = prepareModifiers(declaration.getModifier());
            String[] argumentTypes = prepareArgumentTypes(declaration.getArguments());
            String returnType = ClassUtil.getClassName(declaration.getReturnType());
            assembler.appendMethodDeclaration(modifiers, returnType, declaration.getName(), argumentTypes, declaration.getArgumentNames());
            assembler.appendIndent();
            assembler.appendLeftBrace();
            assembler.appendNewLine();
            appendCodeLines(assembler, declaration.getCodeLines());
            assembler.appendIndent();
            assembler.appendRightBrace();
            assembler.appendNewLine();
        }
    }
    
    private void appendConstructors(JavaLineAssembler assembler)
    {
        for(int ii = 0; ii < constructors.size(); ii++)
        {
            ConstructorDeclaration declaration = (ConstructorDeclaration)constructors.get(ii);
            assembler.appendNewLine();
            assembler.appendBlockComment(declaration.getCommentLines());
            String[] argumentTypes = prepareArgumentTypes(declaration.getArguments());
            assembler.appendConstructorDeclaration(className, argumentTypes, declaration.getArgumentNames());
            assembler.appendIndent();
            assembler.appendLeftBrace();
            assembler.appendNewLine();
            appendCodeLines(assembler, declaration.getCodeLines());
            assembler.appendIndent();
            assembler.appendRightBrace();
            assembler.appendNewLine();
        }
    }
    
    private void appendCodeLines(JavaLineAssembler assembler, String[] codeLines)
    {
        assembler.setIndentLevel(2);
        assembler.appendCodeLines(codeLines);
        assembler.setIndentLevel(1);
    }
    
    private String[] prepareModifiers(int modifier)
    {
        String modifierString = Modifier.toString(modifier);
        if(null == modifierString || modifierString.trim().length() <= 0) return null;
        return StringUtil.split(modifierString, " ", true);
    }
    
    private String[] prepareArgumentTypes(Class[] arguments)
    {
        if(null == arguments || arguments.length <= 0) return null;
        String[] names = new String[arguments.length];
        for(int ii = 0; ii < arguments.length; ii++)
        {
            Class clazz = arguments[ii];
            names[ii] = ClassUtil.getClassName(clazz);
        }
        return names;
    }
    
    private void addImportsForArguments(ConstructorDeclaration declaration)
    {
        Class[] arguments = declaration.getArguments();
        if(null == arguments || arguments.length <= 0) return;
        for(int ii = 0; ii < arguments.length; ii++)
        {
            addImportIfNecessary(arguments[ii]);
        }
    }
    
    private void addImportForReturnType(MethodDeclaration declaration)
    {
        Class returnType = declaration.getReturnType();
        if(null == returnType) return;
        addImportIfNecessary(returnType);
    }

    private void addImportIfNecessary(Class clazz)
    {
        if(null == clazz) return;
        if(imports.contains(clazz.getName())) return;
        if(clazz.getName().startsWith("java.lang")) return;
        if(clazz.isPrimitive()) return;
        addImport(clazz);
    }
    
    public static class ConstructorDeclaration
    {
        private Class[] arguments;
        private String[] argumentNames;
        private String[] codeLines;
        private String[] commentLines;

        public String[] getCodeLines()
        {
            if(null == codeLines) return null;
            return (String[])ArrayUtil.copyArray(codeLines);
        }
        
        public void setCodeLines(String[] codeLines)
        {
            this.codeLines = (String[])ArrayUtil.copyArray(codeLines);
        }
        
        public String[] getCommentLines()
        {
            if(null == commentLines) return null;
            return (String[])ArrayUtil.copyArray(commentLines);
        }
        
        public void setCommentLines(String[] commentLines)
        {
            this.commentLines = (String[])ArrayUtil.copyArray(commentLines);
        }
       
        public String[] getArgumentNames()
        {
            if(null == argumentNames) return null;
            return (String[])ArrayUtil.copyArray(argumentNames);
        }
        
        public void setArgumentNames(String[] argumentNames)
        {
            this.argumentNames = (String[])ArrayUtil.copyArray(argumentNames);
        }
        
        public Class[] getArguments()
        {
            if(null == arguments) return null;
            return (Class[])ArrayUtil.copyArray(arguments);
        }
        
        public void setArguments(Class[] arguments)
        {
            this.arguments = (Class[])ArrayUtil.copyArray(arguments);
        }
    }
    
    public static class MethodDeclaration extends ConstructorDeclaration
    {
        private int modifier;
        private Class returnType;
        private String name;
  
        public MethodDeclaration()
        {
            this("method");
        }
        
        public MethodDeclaration(String name)
        {
            this(Modifier.PUBLIC, name);
        }
        
        public MethodDeclaration(int modifier, String name)
        {
            this(modifier, name, Void.TYPE);
        }
        
        public MethodDeclaration(int modifier, String name, Class returnType)
        {
            setModifier(modifier);
            setReturnType(returnType);
            setName(name);
        }

        public int getModifier()
        {
            return modifier;
        }
        
        public void setModifier(int modifier)
        {
            this.modifier = modifier;
        }
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public Class getReturnType()
        {
            return returnType;
        }
        
        public void setReturnType(Class returnType)
        {
            this.returnType = returnType;
        }
    }
}