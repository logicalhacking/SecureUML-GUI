package ch.ethz.infsec.secureumlgui;


// $Id: ActionTestLoadableModule.java,v 1.5 2005/10/10 21:06:32 linus Exp $
// Copyright (c) 2004-2005 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

//package org.argouml.ui.test;

//import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import java.lang.Thread;

import ch.ethz.infsec.secureumlgui.logging.MultiContextLogger;
import ch.ethz.infsec.secureumlgui.securemodel.dialects.parser.DialectMetaModelParser;
import ch.ethz.infsec.secureumlgui.usecasemapper.control.MenuActionListener;
import ch.ethz.infsec.secureumlgui.util.ExtensionFilenameFilter;
import ch.ethz.infsec.secureumlgui.util.SelectMetamodelActionListener;

import ch.ethz.infsec.secureumlgui.main.ClassLoaderProviderImpl;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.argouml.moduleloader.ModuleInterface;
import org.argouml.ui.ProjectBrowser;
import org.argouml.ui.cmd.GenericArgoMenuBar;
import org.argouml.ui.targetmanager.TargetManager;

import org.netbeans.mdr.handlers.BaseObjectHandler;
//import org.netbeans.mdr.handlers.MDRClassLoader;

/**
 *  SecureUmlModule: ArgoUml Module for editing and displaying SecureUML policies.
 *
 *   Adds SecureUmlComponents to PropertyPanels of ModelElements
 *   representing SecureUml Entities with basic Functionalities like
 *   displaying, adding & removing Permissions for Entities,
 *   textually editing Authorization Constraints of Permissions.
 *   All Changes performed through the SecureUmlComponent are immediately
 *   written down to the underlying Uml ModelElements
 *
 */
public final class SecureUmlModule implements
    ModuleInterface
    //, ActionListener, TargetListener
{


    private static boolean enabled=false;
    //    private static SecureUmlComponentManager suManager;
    private boolean isPropPanelsInitialized = false;

    private static TabSecureUml tab;
    private MultiContextLogger logger = new MultiContextLogger(
        MultiContextLogger.STARTUP);

    DialectMetaModelParser dialectMetaModelParser = new DialectMetaModelParser();

    private static Logger aLog = Logger.getLogger(SecureUmlModule.class);
    /**
     * This is creatable from the module loader.
     */
    public SecureUmlModule()
    {
        Properties log4jProps = new Properties();
        try {
            log4jProps.load(new BufferedInputStream(new FileInputStream(new File("ext/log4j.properties"))));
            PropertyConfigurator.configure(log4jProps);
            aLog.info("Loaded log4j configuration from \"ext/log4j.properties\"");
        } catch (IOException e) {
            logger.error("Could not load log4j configuration from \"ext/log4j.properties\" IOException: " + e.getMessage());
        }
        aLog.info("SecureUML Module initialized");
        logger.info("SecureUML Module initialized");
        //unfortunately, this is too late. this should be done, before mdr is initialized...
        BaseObjectHandler.setClassLoaderProvider(new ClassLoaderProviderImpl());
        //   Thread.currentThread().setContextClassLoader(new MDRClassLoader(new ClassLoaderProviderImpl()));
    }

    static public TabSecureUml getTab() {
        return tab;
    }

    static public String findSecureUMLDir ()
    {
        List<String> ext_dirs =
            org.argouml.moduleloader.ModuleLoader2.getInstance().getExtensionLocations();
        for (String dir: ext_dirs) {
            File su_ext = new File(dir+"/argo_secureuml_gui.jar");
            if(su_ext.exists()) return dir;
        }
        throw (new java.lang.RuntimeException("SecureUML GUI Plugin not found"));
    }



    /**
     *
     */
    private void initializeMenu()
    {
        GenericArgoMenuBar menubar = (GenericArgoMenuBar) ProjectBrowser
                                     .getInstance().getJMenuBar();


        JMenu secureUMLMenu = new JMenu("SecureUML");
        secureUMLMenu.setMnemonic(KeyEvent.VK_S);

        menubar.getTools().addSeparator();

        secureUMLMenu.add(new JMenuItem("Selected Dialect Metamodel:"));

        File[] xmiFiles = findDialectMetamodelFiles();

        ButtonGroup menuItemsGroup = new ButtonGroup();
        for (int i = 0; i < xmiFiles.length; i++)
        {
            File xmiFile = xmiFiles[i];

            JRadioButtonMenuItem menuItem =
                new JRadioButtonMenuItem(xmiFile.getName());

            SelectMetamodelActionListener selectActionListener =
                new SelectMetamodelActionListener(
                xmiFile, dialectMetaModelParser);

            menuItem.addActionListener(selectActionListener);
            menuItemsGroup.add(menuItem);

            secureUMLMenu.add(menuItem);
        }

        secureUMLMenu.addSeparator();

        JMenu useCaseMapperMenu = new JMenu("Use Case Mapper");
        JMenuItem mapUseCasesItem = new JMenuItem("Map Use Cases...");
        mapUseCasesItem.addActionListener(new MenuActionListener());
        useCaseMapperMenu.add(mapUseCasesItem);

        secureUMLMenu.add(useCaseMapperMenu);

        secureUMLMenu.addSeparator();

        //      (JD) show when it is finished:
        JMenuItem writeXmiItem = new JMenuItem("Write XMI");
        writeXmiItem.addActionListener(new WriteXmiActionListener());
        writeXmiItem.setEnabled(true);
        secureUMLMenu.add(writeXmiItem);

        menubar.add(secureUMLMenu);

    }

    boolean isEnabled = true;



    /**
     * looks for .xmi files in the ArgoUML ext/ directory.
     *
     * @return an array containing the found files
     */
    public File[] findDialectMetamodelFiles()
    {
        File extDirectory = new File(findSecureUMLDir());

        if(!extDirectory.exists())
        {
            logger.error("'ext' Directory not found - cannot load Dialect metamodels");
            return null;
        }
        else
        {
            FilenameFilter xmiFilenameFilter = new ExtensionFilenameFilter("xmi");

            File[] xmiFiles = extDirectory.listFiles(xmiFilenameFilter);

            for (int i = 0; i < xmiFiles.length; i++)
            {
                File xmiFile = xmiFiles[i];

                //logger.info("XMI File found: " + xmiFile.getName());
            }

            return xmiFiles;
        }

    }


    // Methods from the ModuleLoader interface
    /**
     *
     * @see ModuleInterface#enable()
     */
    public boolean enable()
    {

        if(!enabled) {
            enabled=true;
            logger.info("SecureUML Module enabled");

            initializeMenu();
            isEnabled = true;

            TabSecureUml tabSecureUml = new TabSecureUml();
            tab=tabSecureUml;

            TargetManager.getInstance().addTargetListener(tabSecureUml);

            Object target = TargetManager.getInstance().getTarget();
            TargetManager.getInstance().setTarget(null);
            TargetManager.getInstance().setTarget(target);

            org.argouml.ui.DetailsPane detailsPane = (org.argouml.ui.DetailsPane)ProjectBrowser.getInstance().getDetailsPane();
            //dont just add, as otherwise probably confilcts with the way, the property tab
            //is resolved by using the last non null tab
            //DetailsPane.java 223
            tabSecureUml.setName("SecureUML Properties");
            detailsPane.addTab( tabSecureUml, true);

            //(JD) A hack. ArgoUML tries to translate the title, and fails...
            int i = detailsPane.getTabs().indexOfComponent(tabSecureUml);
            detailsPane.getTabs().setTitleAt(i,"SecureUML Properties");

            tabSecureUml.setEnabled(false);
        }
        return true;

    }

    /**
     * @see ModuleInterface#disable()
     *
     * This removes us from the Tools menu. If we were not registered there we
     * don't care.
     */
    public boolean disable()
    {
        isEnabled = false;
        isPropPanelsInitialized = false;

        Object target = TargetManager.getInstance().getTarget();
        TargetManager.getInstance().setTarget(null);
        TargetManager.getInstance().setTarget(target);

        return true;
    }

    /**
     * @see ModuleInterface#getName()
     */
    public String getName()
    {
        return "SecureUMLGUI Module";
    }

    /**
     * @see ModuleInterface#getInfo(int)
     */
    public String getInfo(int type)
    {
        switch (type)
        {
        case DESCRIPTION:
            return "This is the SecureUmlGui Module " +
                   "offering an alternative GUI to edit " +
                   "SecureUML-permissions ";
//              break;
        case AUTHOR:
            return "Marcel Beer";
//              break;
        case VERSION:
            return "0.42";
//              break;
        default:
            return null;
        }
    }

    /**
     * The version uid.
     */
    private static final long serialVersionUID = -2570516012301142091L;

}
