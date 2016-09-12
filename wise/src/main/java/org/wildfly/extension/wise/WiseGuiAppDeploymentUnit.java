/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.extension.wise;

import java.security.AccessController;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.SimpleAttachable;
import org.jboss.dmr.ModelNode;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;

public class WiseGuiAppDeploymentUnit extends SimpleAttachable implements DeploymentUnit {

    public WiseGuiAppDeploymentUnit(ClassLoader loader, String context) {
        this(loader, context, new JBossWebMetaData());
    }

    public WiseGuiAppDeploymentUnit(ClassLoader loader, String context, JBossWebMetaData jbossWebMetaData) {
        if (jbossWebMetaData == null) {
            jbossWebMetaData = new JBossWebMetaData();
        }
        jbossWebMetaData.setContextRoot(context);
        this.putAttachment(WiseGuiAppService.CLASSLOADER_KEY, loader);
        this.putAttachment(WiseGuiAppService.JBOSSWEB_METADATA_KEY, jbossWebMetaData);
    }

    @Override
    public ServiceName getServiceName() {
        return ServiceName.JBOSS.append("wise-gui-app-deployment");
    }

    @Override
    public DeploymentUnit getParent() {
        return null;
    }

    @Override
    public String getName() {
        return "wise-gui-app";
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return currentServiceContainer();
    }

    @Override
    public ModelNode getDeploymentSubsystemModel(String subsystemName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModelNode createDeploymentSubModel(String subsystemName, PathElement address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModelNode createDeploymentSubModel(String subsystemName, PathAddress address) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ModelNode createDeploymentSubModel(String subsystemName, PathAddress address, Resource resource) {
        throw new UnsupportedOperationException();
    }

    private static ServiceContainer currentServiceContainer() {
        if (System.getSecurityManager() == null) {
            return CurrentServiceContainer.getServiceContainer();
        }
        return AccessController.doPrivileged(CurrentServiceContainer.GET_ACTION);
    }
}
